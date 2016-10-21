package rest.x;

import static java.util.stream.Collectors.toList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.HashSet;
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

    /**
     * CDI Extension to support resource auto-discovery
     */
    public static class ResourceAutoDiscovery implements Extension {

        private final Set<Class> resources = new HashSet<>();

        public void pathFound(
                @Observes
                @WithAnnotations(Path.class)
                final ProcessAnnotatedType pat) {

            resources.add(pat.getAnnotatedType().getJavaClass());
        }

        public Collection<Class> getResourceClasses(final Class<?>... annotations) {

            return this.resources.stream().filter(resource -> isAnnotated(resource, annotations)).collect(toList());
        }

        private boolean isAnnotated(final Class resource, final Class<?>... annotations) {

            for (Class<?> annotation : annotations) {
                if (resource.getAnnotation(annotation) == null) {
                    return false;
                }
            }
            return true;
        }

    }
}
