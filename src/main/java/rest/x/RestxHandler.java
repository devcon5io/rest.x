package rest.x;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * Routing handler to delegate requests to Resteasy for processing.
 */
public interface RestxHandler extends Handler<RoutingContext> {


    static RestxHandler create(Vertx vertx, ResteasyDeployment deployment){
        return new RestxHandlerImpl(vertx, deployment);
    }

}
