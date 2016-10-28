package rest.x;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.Principal;

import io.vertx.ext.auth.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 *
 */
public class RestxSecurityContextTest {

    /**
     * The class under test
     */
    private RestxSecurityContext subject;

    @Mock
    private User user;

    @Before
    public void setUp() throws Exception {
        subject = new RestxSecurityContext(user);
    }


    @Test
    public void getUserPrincipal() throws Exception {

        Principal p = subject.getUserPrincipal();

        assertNotNull(p);
        assertTrue(p instanceof RestxPrincipal);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isUserInRole() throws Exception {
        subject.isUserInRole("anyRole");
    }

    @Test
    public void isSecure() throws Exception {
        assertTrue(subject.isSecure());
    }

    @Test
    public void getAuthenticationScheme() throws Exception {
        assertEquals("VERTX", subject.getAuthenticationScheme());
    }

}
