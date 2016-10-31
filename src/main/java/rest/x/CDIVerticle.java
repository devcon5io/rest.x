package rest.x;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

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
        return CDIUtils.getBeanInstance(beanManager, type, annotations);
    }

    protected <T> void addBeanInstance(T bean){
        CDIUtils.addBeanInstance(beanManager, bean);
    }
}
