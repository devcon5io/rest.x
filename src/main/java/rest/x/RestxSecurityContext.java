package rest.x;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

import io.vertx.ext.auth.User;
import org.jboss.weld.exceptions.UnsupportedOperationException;

/**
 *
 */
public class RestxSecurityContext implements SecurityContext {

    private final RestxPrincipal principal;

    public RestxSecurityContext(User user) {

        this.principal = new RestxPrincipal(user);
    }

    @Override
    public Principal getUserPrincipal() {

        return this.principal;
    }

    @Override
    public boolean isUserInRole(final String role) {

        throw new UnsupportedOperationException("isUserInRole not supported by this context");
    }

    @Override
    public boolean isSecure() {

        return true;
    }

    @Override
    public String getAuthenticationScheme() {

        return "VERTX";
    }

}
