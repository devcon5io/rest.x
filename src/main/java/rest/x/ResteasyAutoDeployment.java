package rest.x;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Set;

import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;

/**
 * Creates the VertxResteasyDeployment with auto-discovered resourced
 */
@ApplicationScoped
public class ResteasyAutoDeployment {

    @Inject
    private BeanManager beanManager;

    @Inject
    private ResourceAutoDiscovery registry;

    @Produces
    @ApplicationScoped
    public VertxResteasyDeployment createDeployment() {

        VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        deployment.start();

        registry.getResourceClasses(Path.class)
                .forEach(resource -> deployment.getRegistry().addSingletonResource(getBeanInstance(resource)));
        return deployment;
    }

    protected <T> T getBeanInstance(final Class<T> type) {

        final Set<Bean<?>> beans = beanManager.getBeans(type);
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
        final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, type, creationalContext);
    }

}
