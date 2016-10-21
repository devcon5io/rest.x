package rest.x;

import java.security.Principal;

import io.vertx.ext.auth.User;

/**
 *
 */
public class RestxPrincipal implements Principal {

    private final User user;

    public RestxPrincipal(final User user) {

        this.user = user;
    }

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
