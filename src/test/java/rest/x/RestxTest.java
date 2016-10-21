package rest.x;

import static io.tourniquet.junit.net.NetworkMatchers.isReachable;
import static io.tourniquet.junit.net.NetworkMatchers.port;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import io.tourniquet.junit.net.NetworkUtils;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class RestxTest {

    private static AtomicInteger PORT = new AtomicInteger();
    private ExecutorService pool;

    @Before
    public void setUp() throws Exception {

        PORT.set(NetworkUtils.findAvailablePort());
        this.pool = Executors.newFixedThreadPool(1);
        pool.submit(() -> Restx.main(new String[0]));

    }

    @Test
    public void main() throws Exception {

        ensurePortReachable(PORT.get(), 100, 10);

        Client client = ClientBuilder.newClient();
        String name = client.target("http://localhost:"+PORT.get()).path("/test/1").request().get(String.class);
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
                    .setConfig(new JsonObject().put("http.port", PORT.get())
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
