package rest.x.security;

import java.security.Principal;

import io.vertx.ext.auth.User;

/**
 * Principal to represent a Vert.x {@link io.vertx.ext.auth.User}.
 */
public class RestxPrincipal implements Principal {

    private final User user;

    public RestxPrincipal(final User user) {

        this.user = user;
    }

    /**
     * Provides access to the currently authenticated Vert.x user.
     * @return
     *  the current Vert.x user.
     */
    public User getUser() {

        return user;
    }

    @Override
    public String getName() {

        return user.principal().getString("username");
    }

    @Override
    public String toString() {

        return "VertxUser[" + user.principal().getString("username") + "]";
    }
}
