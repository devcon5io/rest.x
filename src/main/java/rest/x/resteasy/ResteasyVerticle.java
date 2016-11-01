package rest.x.resteasy;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import rest.x.cdi.CDIVerticle;

/**
 *
 */
@ApplicationScoped
@Resteasy
public class ResteasyVerticle extends CDIVerticle {

    private HttpServer httpServer;

    @Inject
    private Vertx vertx;

    @Inject
    private Router router;

    @Override
    public void start() throws Exception {

        int port = 8080;
        if(config().getInteger("http.port") != null){
            port = config().getInteger("http.port");
        }

        // Start the front end server using the Jax-RS controller
        this.httpServer = vertx.createHttpServer()
                               .requestHandler(router::accept)
                               .listen(port, result -> {
                                   if(result.result() != null) {
                                       System.out.println("Server started on port " + result.result().actualPort());
                                   } else {
                                       throw new RuntimeException("Port already bound");
                                   }
                               });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        this.httpServer.close();
    }

}
