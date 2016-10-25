package rest.x;

import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

/**
 *
 */
@ApplicationScoped
public class Restx {

    private static AtomicReference<WeldContainer> CURRENT = new AtomicReference<>();

    public static WeldContainer container(){
        return CURRENT.get();
    }

    private static void setContainer(WeldContainer container){
        if(!CURRENT.compareAndSet(null, container)){
            throw new IllegalStateException("Container already initialized");
        }
    }

    public static void main(String[] args) {
        setContainer(new Weld().initialize());
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
