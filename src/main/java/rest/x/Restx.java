package rest.x;

import javax.enterprise.inject.spi.BeanManager;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Launcher and Container reference.
 */
public class Restx {

    private static AtomicReference<WeldContainer> CURRENT = new AtomicReference<>();
    private static AtomicReference<Args> ARGS = new AtomicReference<>();

    /**
     * Provides access to the current CDI Container.
     * @return
     *  the current CDI container.
     */
    public static WeldContainer container(){
        return CURRENT.get();
    }

    /**
     * Provices access to the raw command line arguments
     * @return
     *  the commandline arguments set during startup
     */
    public static Args args(){
        return ARGS.get();
    }

    private static <T> void setIfUnset(AtomicReference<T> reference, T value){
        if(!reference.compareAndSet(null, value)){
            throw new IllegalStateException("Value already initialized");
        }
    }

    private static void setContainer(WeldContainer container){
        setIfUnset(CURRENT, container);
    }

    private static void setArgs(Args args){
        setIfUnset(ARGS, args);
    }

    public static void main(String[] args) {

        setArgs(new Args(args));
        setContainer(new Weld().initialize());
        BeanManager beanManager = CURRENT.get().getBeanManager();
    }

}
