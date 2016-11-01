package rest.x.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

import io.vertx.core.AbstractVerticle;

/**
 * Base Verticle for all CDI enabled verticles.
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

    /**
     * Adds an unmanaged bean to the CDI context by registering it at the current bean manager for this verticle.
     * @param bean
     *  the bean to be added to the context
     * @param <T>
     *      the type of the the bean
     */
    protected <T> void addBeanInstance(T bean){
        CDIUtils.addBeanInstance(beanManager, bean);
    }

    /**
     * The qualifier for the deployment options for this verticle. If the verticle requires specific deployment
     * options, they have to be qualified with this annotation. Alternative to overriding this method,
     * extending Verticles can be qualified with the annotation too.
     * @return
     *  the qualifier annotation type
     */
    public Annotation[] getQualifier(){
        return CDIUtils.getQualifiers(this);
    }
}
