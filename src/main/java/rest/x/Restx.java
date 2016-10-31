package rest.x;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Launcher and Vertx Initializer.
 */
@ApplicationScoped
public class Restx {

    private static AtomicReference<Args> ARGLINE = new AtomicReference<>();
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
        BeanManager beanManager = CURRENT.get().getBeanManager();
        CDIUtils.addBeanInstance(beanManager, new Args(args));
    }


    private Vertx vertx;

    @Inject
    private Instance<VertxOptions> vertxOptions;

    @Inject
    private Instance<DeploymentOptions> options;

    @Inject
    @Any
    private Instance<Verticle> allDiscoveredVerticles;

    public void initVertx(@Observes @Initialized(ApplicationScoped.class) Object obj) {

        if(vertxOptions.isUnsatisfied()){
            this.vertx = Vertx.vertx();
        } else {
            this.vertx = Vertx.vertx(vertxOptions.get());
        }

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
