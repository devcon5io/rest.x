package rest.x;

import javax.enterprise.inject.spi.BeanManager;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import rest.x.cdi.CDIUtils;

/**
 * Launcher and Container reference.
 */
public class Restx {

    private static AtomicReference<WeldContainer> CURRENT = new AtomicReference<>();

    /**
     * Provides access to the current CDI Container.
     * @return
     *  the current CDI container.
     */
    public static WeldContainer container(){
        return CURRENT.get();
    }

    private static void setContainer(WeldContainer container){
        if(!CURRENT.compareAndSet(null, container)){
            throw new IllegalStateException("Container already initialized");
        }
    }

    public static void main(String[] args) {

        setContainer(new Weld().initialize());
        BeanManager beanManager = CURRENT.get().getBeanManager();
        CDIUtils.addBeanInstance(beanManager, new Args(args));
    }

}
