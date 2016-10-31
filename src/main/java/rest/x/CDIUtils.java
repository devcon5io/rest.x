package rest.x;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Util class for interacting with the CDI container and bean manager
 */
public final class CDIUtils {
    private CDIUtils(){}

    /**
     * Retrieves an instance of a specific type and optionally with specific annotations from the CDI context
     * @param beanManager
     *  the bean manager from which to retrieve the bean instance
     * @param type
     *  the type class of the bean that should be retrieved.
     * @param <T>
     *     The type parameter for the bean
     * @param annotations
     *  optional set of annotations used for looking up the instance. Could be qualifiers or other annotations.
     * @return an instance of the bean that matched the type
     */
    public static <T> T getBeanInstance(BeanManager beanManager, final Class<T> type, Annotation... annotations){
        final Set<Bean<?>> beans = beanManager.getBeans(type, annotations);
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
        final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, type, creationalContext);
    }

    /**
     * Adds an unmanaged instance as bean to the bean manager to be available inside the CDI context.
     * @param beanManager
     *  the beanmanager to add the instance to
     * @param bean
     *  the bean to be added
     * @param <T>
     *  the type of the bean
     */
    public static <T> void addBeanInstance(BeanManager beanManager, T bean){

        final AnnotatedType type = beanManager.createAnnotatedType(bean.getClass());
        final InjectionTarget target = beanManager.createInjectionTarget(type);
        final CreationalContext cct = beanManager.createCreationalContext(null);
        target.inject(bean, cct);
    }

}
