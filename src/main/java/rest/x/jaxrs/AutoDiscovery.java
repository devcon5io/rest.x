package rest.x.jaxrs;

import static java.util.stream.Collectors.toSet;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * CDI Extension to support resource and provider auto-discovery
 */
public class AutoDiscovery implements Extension {

    private final Map<Class, Set<Class>> discoveredTypes = new HashMap<>();

    /**
     * Subscription to all {@link javax.ws.rs.Path} annotated resources found during startup.
     *
     * @param pat
     *         the annotated type found
     */
    public void pathFound(
            @Observes
            @WithAnnotations(Path.class)
            final ProcessAnnotatedType pat) {

        register(Path.class, pat.getAnnotatedType().getJavaClass());
    }

    private void register(final Class resourceType, final Class javaClass) {

        discoveredTypes.putIfAbsent(resourceType, new HashSet<>());
        discoveredTypes.get(resourceType).add(javaClass);
    }

    /**
     * Subscription to all {@link javax.ws.rs.ext.Provider} annotated resources found during startup.
     *
     * @param pat
     *         the annotated type found
     */
    public void providerFound(
            @Observes
            @WithAnnotations(Provider.class)
            final ProcessAnnotatedType pat) {

        register(Provider.class, pat.getAnnotatedType().getJavaClass());
    }

    /**
     * Returns all classes of resources found during startup.
     *
     * @param type
     *         the type of the discovered resource to retrieve
     * @param annotations
     *         annotations to filter the result set. If none are specified, all are returned.
     *
     * @return a collection of the resource types.
     */
    public Collection<Class> getClasses(Class type, final Class<?>... annotations) {

        if (this.discoveredTypes.containsKey(type)) {

            return this.discoveredTypes.get(type)
                                       .stream()
                                       .filter(resource -> isAnnotated(resource, annotations))
                                       .collect(toSet());
        } else {
            return Collections.emptySet();
        }
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
