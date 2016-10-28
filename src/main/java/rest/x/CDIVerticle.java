package rest.x;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Set;

import io.vertx.core.AbstractVerticle;

/**
 * Base Verticle for all CDI enabled verticles.
 * @author John Ament
 * @author Gerald M&uuml;cke
 */
public abstract class CDIVerticle extends AbstractVerticle {


    @Inject
    private BeanManager beanManager;

    /**
     * Retrieves an instance of a specific type and optionally with specific annotations from the CDI context
     * @param type
     *  the type class of the bean that should be retrieved.
     * @param <T>
     *     The type parameter for the bean
     * @param annotations
     *  optional set of annotations used for looking up the instance. Could be qualifiers or other annotations.
     * @return an instance of the bean that matched the type
     */
    protected <T> T getBeanInstance(final Class<T> type, Annotation... annotations){
        final Set<Bean<?>> beans = beanManager.getBeans(type, annotations);
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
        final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, type, creationalContext);
    }

    protected <T> void addBeanInstance(T bean){

        final AnnotatedType type = beanManager.createAnnotatedType(bean.getClass());
        final InjectionTarget target = beanManager.createInjectionTarget(type);
        final CreationalContext cct = beanManager.createCreationalContext(null);
        target.inject(bean, cct);
    }
}
