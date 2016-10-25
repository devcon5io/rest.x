package rest.x;

import static io.tourniquet.junit.net.NetworkMatchers.isReachable;
import static io.tourniquet.junit.net.NetworkMatchers.port;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.tourniquet.junit.net.NetworkUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 *
 */
public class RestxTest {

    private ExecutorService pool;

    private int port;

    @Before
    public void setUp() throws Exception {

        this.pool = Executors.newFixedThreadPool(1);
        Restx.main(new String[0]);
        this.port = Restx.container().instance().select(RouteConfig.class).get().getPort();

    }

    @Test
    public void main() throws Exception {

        ensurePortReachable(this.port, 100, 10);

        Client client = ClientBuilder.newClient();
        String name = client.target("http://localhost:"+this.port).path("/test/1").request().get(String.class);
        assertEquals("success", name);

    }

    private void ensurePortReachable(final int port, int retries, final int checkInterval) throws InterruptedException {

        int counter = retries;
        while(NetworkUtils.isPortAvailable(port) && counter-- > 0) {
            Thread.sleep(checkInterval);
        }
        assumeThat(port(port), isReachable());
        System.out.println("Server reachable after " + (retries - counter) * checkInterval + " ms");
    }

    @After
    public void tearDown() throws Exception {

        this.pool.shutdown();
    }

    // ---------------------- TEST RESOURCES AND BEANS --------------------------------------

    @ApplicationScoped
    public static class RouteConfig {

        @Inject
        private Vertx vertx;

        @Inject
        private VertxResteasyDeployment deployment;

        private int port;

        @PostConstruct
        public void initPort(){
            this.port = NetworkUtils.findAvailablePort();
        }

        public int getPort() {
            return port;
        }

        @Produces
        @ApplicationScoped
        public Router getRouter(){

            Router router = Router.router(vertx);

            router.route("/test/*").handler(RestxHandler.create(vertx, deployment));

            return router;
        }

        @Produces
        @ApplicationScoped
        public DeploymentOptions options(){
            DeploymentOptions options = new DeploymentOptions()
                    .setConfig(new JsonObject().put("http.port", port)
                    );
            return options;
        }
    }

    @Path("test")
    public static class TestResource {

        @GET
        @Path("1")
        public String test(){
            return "success";
        }
    }

}
