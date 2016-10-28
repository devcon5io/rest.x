package rest.x;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import java.util.Set;

import io.tourniquet.junit.inject.CdiInjection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CDIVerticleTest {

    @Mock
    private BeanManager beanManager;

    @Mock
    private TestBean testBean;

    @Mock
    private Bean bean;

    @Mock
    private CreationalContext creationalContext;

    @Mock
    private Set<Bean<?>> beans;

    @Mock
    private AnnotatedType annotatedType;

    @Mock
    private InjectionTarget injectionTarget;


    /**
     * The class under test
     */
    private CDIVerticle subject;

    @Before
    public void setUp() throws Exception {
        subject = new CDIVerticle() {};
        new CdiInjection(beanManager).into(subject);
    }

    @Test
    public void getBeanInstance() throws Exception {

        when(beanManager.getBeans(TestBean.class)).thenReturn(beans);
        when(beanManager.resolve(beans)).thenReturn(bean);
        when(beanManager.createCreationalContext(bean)).thenReturn(creationalContext);
        when(beanManager.getReference(bean, TestBean.class, creationalContext)).thenReturn(testBean);

        TestBean tb = subject.getBeanInstance(TestBean.class);

        assertNotNull(tb);
    }

    @Test
    public void addBeanInstance() throws Exception {

        when(beanManager.createAnnotatedType(any(Class.class))).thenReturn(annotatedType);
        when(beanManager.createInjectionTarget(annotatedType)).thenReturn(injectionTarget);
        when(beanManager.createCreationalContext(any())).thenReturn(creationalContext);

        subject.addBeanInstance(testBean);

        verify(injectionTarget).inject(testBean, creationalContext);
    }

    public static interface TestBean{}
}
