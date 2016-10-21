package rest.x;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.jboss.weld.environment.se.Weld;

/**
 *
 */
@ApplicationScoped
public class Restx {

    public static void main(String[] args) {

        Weld weld = new Weld();
        weld.initialize();
    }


    private Vertx vertx;

    @Inject
    private Instance<DeploymentOptions> options;

    @Inject
    @Any
    private Instance<Verticle> allDiscoveredVerticles;

    public void initVertx(@Observes @Initialized(ApplicationScoped.class) Object obj) {
        this.vertx = Vertx.vertx();

        allDiscoveredVerticles.forEach(v -> {

            if(options.isUnsatisfied()){
                vertx.deployVerticle(v);
            } else {
                vertx.deployVerticle(v, options.get());
            }

        });
    }

    @Produces
    @ApplicationScoped
    public Vertx getVertx() {
        return vertx;
    }

    @PreDestroy
    public void shutdown() {
        this.vertx.close();
    }
}
