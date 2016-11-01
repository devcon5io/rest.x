package rest.x.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import io.vertx.ext.auth.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rest.x.security.RestxPrincipal;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RestxPrincipalTest {

    /**
     * The class under test
     */
    private RestxPrincipal subject;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private User user;

    @Before
    public void setUp() throws Exception {
        subject = new RestxPrincipal(user);
    }

    @Test
    public void getUser() throws Exception {
        User u = subject.getUser();

        assertSame(user, u);
    }

    @Test
    public void getName() throws Exception {

        when(user.principal().getString("username")).thenReturn("testuser");

        String name = subject.getName();

        assertEquals("testuser", subject.getName());
    }

    @Test
    public void testToString() throws Exception {
        when(user.principal().getString("username")).thenReturn("testuser");

        String name = subject.getName();

        assertEquals("VertxUser[testuser]", subject.toString());
    }

}
