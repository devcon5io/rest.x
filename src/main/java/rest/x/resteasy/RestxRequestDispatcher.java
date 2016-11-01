package rest.x.resteasy;

import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.vertx.VertxSecurityContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import rest.x.security.RestxSecurityContext;

/**
 * Helper/delegate class to unify Servlet and Filter dispatcher implementations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Norman Maurer
 * @author Gerald M&uuml;cke
 */
public class RestxRequestDispatcher {

    protected final SynchronousDispatcher dispatcher;
    protected final ResteasyProviderFactory providerFactory;
    protected final SecurityDomain domain;

    public RestxRequestDispatcher(SynchronousDispatcher dispatcher,
                                  ResteasyProviderFactory providerFactory,
                                  SecurityDomain domain) {

        this.dispatcher = dispatcher;
        this.providerFactory = providerFactory;
        this.domain = domain;
    }

    public SynchronousDispatcher getDispatcher() {

        return dispatcher;
    }

    public SecurityDomain getDomain() {

        return domain;
    }

    public ResteasyProviderFactory getProviderFactory() {

        return providerFactory;
    }

    public void service(RoutingContext routingContext,
                        Context context,
                        HttpServerRequest req,
                        HttpServerResponse resp,
                        HttpRequest vertxReq,
                        HttpResponse vertxResp,
                        boolean handleNotFound) throws IOException {

        try {
            ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
            if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                ThreadLocalResteasyProviderFactory.push(providerFactory);
            }
            SecurityContext securityContext;
            if (domain != null) {
                securityContext = basicAuthentication(vertxReq, vertxResp);
                if (securityContext == null) // not authenticated
                {
                    //added by Gerald Muecke, create vertx security context, for pre-authenticated user
                    securityContext = new RestxSecurityContext(routingContext.user());
                }
            } else {
                securityContext = new VertxSecurityContext();
            }
            try {

                ResteasyProviderFactory.pushContext(SecurityContext.class, securityContext);
                ResteasyProviderFactory.pushContext(Context.class, context);
                ResteasyProviderFactory.pushContext(HttpServerRequest.class, req);
                ResteasyProviderFactory.pushContext(HttpServerResponse.class, resp);
                ResteasyProviderFactory.pushContext(Vertx.class, context.owner());
                if (handleNotFound) {
                    dispatcher.invoke(vertxReq, vertxResp);
                } else {
                    dispatcher.invokePropagateNotFound(vertxReq, vertxResp);
                }
            } finally {
                ResteasyProviderFactory.clearContextData();
            }
        } finally {
            ResteasyProviderFactory defaultInstance = ResteasyProviderFactory.getInstance();
            if (defaultInstance instanceof ThreadLocalResteasyProviderFactory) {
                ThreadLocalResteasyProviderFactory.pop();
            }

        }
    }

    private SecurityContext basicAuthentication(HttpRequest request, HttpResponse response) throws IOException {

        List<String> headers = request.getHttpHeaders().getRequestHeader(HttpHeaderNames.AUTHORIZATION);
        if (!headers.isEmpty()) {
            String auth = headers.get(0);
            if (auth.length() > 5) {
                String type = auth.substring(0, 5);
                type = type.toLowerCase();
                if ("basic".equals(type)) {
                    String cookie = auth.substring(6);
                    cookie = new String(Base64.decodeBase64(cookie.getBytes()));
                    String[] split = cookie.split(":");
                    Principal user = null;
                    try {
                        user = domain.authenticate(split[0], split[1]);
                        return new VertxSecurityContext(user,domain,"BASIC",true);
                    } catch (SecurityException e) {
                        response.sendError(HttpResponseCodes.SC_UNAUTHORIZED);
                        return null;
                    }
                } else {
                    response.sendError(HttpResponseCodes.SC_UNAUTHORIZED);
                    return null;
                }
            }
        }
        return null;
    }

}
