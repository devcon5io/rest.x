package rest.x.cdi;

import static java.util.stream.Collectors.toList;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Util class for interacting with the CDI container and bean manager
 */
public final class CDIUtils {
    private CDIUtils(){}

    private static final Default DEFAULT_LITERAL = new Default(){

        @Override
        public Class<? extends Annotation> annotationType() {

            return Default.class;
        }
    };

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

    /**
     * Fetches the type-level qualifier annotations of a specific instance. If the instance's type has
     * no qualifiers, array is 0-sized..
     * @param instance
     *  the instance to fetch the annotations from
     * @return
     *  the qualifier annotations of the instance
     */
    public static Annotation[] getQualifiers(Object instance){
        Class<?> type = instance.getClass();
        return Stream.of(type.getAnnotations())
                                            .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                                            .collect(toList()).toArray(new Annotation[0]);
    }

    /**
     * Selects an injected instance from the {@link javax.enterprise.inject.Instance} field that matches the qualifiers.
     * If no matching instance was found, the default supplier is used to create default instance.
     * @param instance
     *  the instance injection container
     * @param defaultSupplier
     *  default supplier that provides a default instance in case no injected bean satisfies the selection
     * @param qualifiers
     *  optional qualifier to select the instance
     * @param <T>
     *  the type of the bean to fetch
     * @return
     *  a bean instance.
     */
    public static <T> T getInstanceOrDefault(Instance<T> instance, Supplier<T> defaultSupplier, Annotation... qualifiers){
        if(instance.isUnsatisfied()){
            return defaultSupplier.get();
        } else {
            return instance.select(qualifiers).get();
        }
    }

}
