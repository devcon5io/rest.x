package rest.x.cdi;

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
import io.vertx.core.VertxOptions;

/**
 * Launcher for Vert.x.
 */
@ApplicationScoped
public class CDIVertxLauncher {
    /*
        * CDI Vert.x launcher
        */
    private Vertx vertx;

    @Inject
    private Instance<VertxOptions> vertxOptions;

    @Inject
    @Any
    private Instance<DeploymentOptions> options;

    @Inject
    @Any
    private Instance<Verticle> allDiscoveredVerticles;

    /**
     * Initializes Vertx and deploys all discovered verticles. Verticle DeploymentOptions have to be provided
     * via CDI and can be assigned to specific Verticles using qualifier annotations on producers and Verticles.
     * @param obj
     *  unused
     */
    public void initVertx(@Observes
                          @Initialized(ApplicationScoped.class) Object obj) {

        if(vertxOptions.isUnsatisfied()){
            this.vertx = Vertx.vertx();
        } else {
            this.vertx = Vertx.vertx(vertxOptions.get());
        }

        allDiscoveredVerticles.forEach(v -> {

            if(options.isUnsatisfied()){
                vertx.deployVerticle(v);
            } else {
                DeploymentOptions opts = options.select(CDIUtils.getQualifiers(v)).get();
                vertx.deployVerticle(v, opts);
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
