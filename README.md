# rest.x
Jax-RS & CDI Services with Vert.x

Rest.x is not a framework on it's own. It is built on top of Vert.x, Resteasy and Weld.
It provided a convenient way for developing Jax-RS based webservices, use
CDI and the non-blocking & highly scalable infrastructure of Vert.x.

The glue code it provides, solves the biggest problem of Vert.x and resteasy - security -
in a convenient way.

Vert.x provides it's own non standardized way for authentication and authorizion, which
is non-blocking callback based.
Jax-RS on the other hand includes only synchronous access to Security features.

Rest.x now allows to access the request context, including it's authentication and
authorization aspects inside a Jax-RS resource.

## Setup

Add rest.x to your pom and a vert.x auth implementation of your choice.
The rest.x dependency includes vertx-core, vertx-web, resteasy-vertx and weld-se

    <dependency>
        <groupId>io.devcon5</groupId>
        <artifactId>rest.x</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-auth-shiro</artifactId>
        <version>${vertx.version}</version>
    </dependency>

Add an empty `META-INF/beans.xml` to your resources so your jar is recognized
as bean deployment archive and scanned for CDI beans. 
Rest.x will automatically detect your Jax-RS resources and deploys them.

Next you need a `Router` producer which creates a Vert.x Router and
defines, which Resources are protected and which auth provider is used.

    public class RoutingConfiguration {
    
        @Inject
        private Vertx vertx;
    
        @Inject
        private AuthProvider authProvider;
    
        @Inject
        private VertxResteasyDeployment deployment;
    
        @Produces
        @ApplicationScoped
        public Router getRouter(){
    
            Router router = Router.router(vertx);
            router.route().handler(CookieHandler.create());
            router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
            router.route().handler(UserSessionHandler.create(authProvider));
            router.route().handler(BodyHandler.create());
    
            router.route("/protected/*").handler(RedirectAuthHandler.create(authProvider, "/static/loginpage.html"));
            //this handler is needed to
            router.route("/protected/*").handler(RestxHandler.create(vertx, deployment));
    
            router.route("/static/*").handler(StaticHandler.create());
            router.route("/loginhandler").handler(FormLoginHandler.create(authProvider));
            router.route("/logout").handler(context -> {
                context.clearUser();
                // Redirect back to the index page
                context.response().putHeader("location", "/").setStatusCode(302).end();
            });
            return router;
        }
    }

The `RestxHandler` will inject the security context and dispatches calls to the Jax-RS resources. 

The `AuthProvider` can either be injected or created directly. If being injected, 
you need a producer, for example the ShiroAuthProvider

    public class AuthProviderProducer {
    
        @Inject
        private Vertx vertx;
    
        @Produces
        public AuthProvider getAuthProvider(){
            return ShiroAuth.create(vertx, ShiroAuthRealmType.PROPERTIES, new JsonObject());
        }
    }

Finally, you need a Jax-RS resource, for example:

    @Path("private")
    public class PrivateResource {
    
        @GET
        public String getResource(){
            return "this is private";
        }
    }

Start the Service with `rest.x.Restx.main()`

## Authorization in Jax-RS

Though, this resource is only protected by authentication, not by authorization.
Unfortunately the current resteasy-vertx implementation does not support annotations like
`@RolesAllowed`. But you can do the check manually using the Jax-RS `SecurityContext`.
But the `SecurityContext` provides only synchronous call to `isUserInRole()` which doesn't
play well with the asynchronous way of authorization of Vert.x. 
So Rest.x provides a convenient way of accessing the `User` of the current
request which plays nicely with the Jax-RS way of asynchronous response processing.

    @GET
    @Path("/hello")
    public void doGet(@Context SecurityContext sc, @Suspended AsyncResponse response) {

        //get the RestxPrincipal. 
        //this need to be done before asynchronous processing
        //otherwise the SecurityContext isn't accessible anymore
        RestxPrincipal principal = (RestxPrincipal) sc.getUserPrincipal();
        
        //acces the Vert.x user and call the is authorized method
        principal.getUser().isAuthorised("role:developer", res -> {

            if(res != null){
                if(res.result()){
                    //resume the Jax-RS asynchronous call once the user is authorized
                    response.resume(Response.status(200).entity("Hello " + principal.getName()).build());
                } else {
                    response.resume(Response.status(403).entity("Not authorized").build());
                }
            } else {
                response.resume(Response.status(500).entity("Could not authorize").build());
            }

        });
    }




## Developing CDI Verticles

To develop your own CDI verticles, you only have to inherit from `rest.x.CDIVerticle`

Credit goes to John Ament's example [https://github.com/johnament/vertx-cdi] 
