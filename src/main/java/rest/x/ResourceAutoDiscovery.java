package rest.x;

import static java.util.stream.Collectors.toList;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * CDI Extension to support resource auto-discovery
 */
public class ResourceAutoDiscovery implements Extension {

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
