package rest.x;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;

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
     *
     * @param type
     * @param <T>
     * @return
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
