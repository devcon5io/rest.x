package rest.x;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 *
 */
@ApplicationScoped
public class ResteasyVerticle extends CDIVerticle {

    private HttpServer httpServer;

    @Inject
    private Vertx vertx;

    @Inject
    private Router router;

    @Override
    protected void onVertxStart() {


        // Start the front end server using the Jax-RS controller
        this.httpServer = vertx.createHttpServer()
                               .requestHandler(router::accept)
                               .listen(18080, result -> {
                                   //TODO add error if result is null (port already bound)
                                   if(result != null) {
                                       System.out.println("Server started on port " + result.result().actualPort());
                                   } else {
                                       throw new RuntimeException("Port already bound");
                                   }
                               });
    }

    @Override
    protected void onVertxStop() {
        super.onVertxStop();
        this.httpServer.close();
    }

}
