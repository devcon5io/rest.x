package rest.x;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

/**
 * Base Verticle for all CDI enabled verticles.
 * @author John Ament
 * @author Gerald M&uuml;cke
 */
public abstract class CDIVerticle extends AbstractVerticle {

    @Inject
    private Vertx vertx;

    @Inject
    private BeanManager beanManager;

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    protected <T> T getBeanInstance(final Class<T> type){
        final Set<Bean<?>> beans = beanManager.getBeans(type);
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
        final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, type, creationalContext);
    }

    protected <T> void addBeanInstance(T bean, Class scope){

        AnnotatedType type = beanManager.createAnnotatedType(bean.getClass());
        InjectionTarget target = beanManager.createInjectionTarget(type);
        CreationalContext cct = beanManager.createCreationalContext(null);
        target.inject(bean, cct);
    }
}
