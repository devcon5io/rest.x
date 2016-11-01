package rest.x.resteasy;

import static java.util.stream.Collectors.toList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import rest.x.cdi.CDIUtils;
import rest.x.jaxrs.AutoDiscovery;

/**
 * Creates the VertxResteasyDeployment with auto-discovered resourced
 */
@ApplicationScoped
public class ResteasyAutoDeployment {

    @Inject
    private BeanManager beanManager;

    @Inject
    private AutoDiscovery registry;

    @Produces
    @ApplicationScoped
    public VertxResteasyDeployment createDeployment() {

        VertxResteasyDeployment deployment = new VertxResteasyDeployment();
        //TODO add support for various scopes
        deployment.setProviderClasses(registry.getClasses(Provider.class)
                                              .stream()
                                              .map(cls -> cls.getName())
                                              .collect(toList()));
        deployment.start();
        registry.getClasses(Path.class)
                .forEach(resource -> deployment.getRegistry()
                                               .addSingletonResource(CDIUtils.getBeanInstance(beanManager, resource)));

        return deployment;
    }

}
