package rest.x;

import java.io.IOException;

import io.netty.buffer.ByteBufInputStream;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.embedded.SimpleSecurityDomain;
import org.jboss.resteasy.plugins.server.vertx.VertxHttpRequest;
import org.jboss.resteasy.plugins.server.vertx.VertxHttpResponse;
import org.jboss.resteasy.plugins.server.vertx.VertxUtil;
import org.jboss.resteasy.plugins.server.vertx.i18n.LogMessages;
import org.jboss.resteasy.plugins.server.vertx.i18n.Messages;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.ResteasyDeployment;

//modified version of the VertxResteasyHandler, sets a default security domain and uses the RestxRequestDispatcher
/**
 *
 */
public class RestxHandlerImpl implements RestxHandler {

    protected final Vertx vertx;
    private final RestxRequestDispatcher dispatcher;
    private final String servletMappingPrefix;

    public RestxHandlerImpl(final Vertx vertx, final ResteasyDeployment deployment) {

        this.vertx = vertx;
        this.servletMappingPrefix = "";
        SecurityDomain domain = new SimpleSecurityDomain();
        this.dispatcher = new RestxRequestDispatcher((SynchronousDispatcher) deployment.getDispatcher(),
                                                     deployment.getProviderFactory(),
                                                     domain);
    }

    @Override
    public void handle(final RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = request.response();
        Context ctx = this.vertx.getOrCreateContext();
        VertxHttpResponse vertxResponse = new VertxHttpResponse(response, this.dispatcher.getProviderFactory());
        VertxHttpRequest vertxRequest = new VertxHttpRequest(ctx,
                                                             VertxUtil.extractHttpHeaders(request),
                                                             VertxUtil.extractUriInfo(request, this.servletMappingPrefix),
                                                             request.rawMethod(),
                                                             this.dispatcher.getDispatcher(),
                                                             vertxResponse,
                                                             false);
        populateInputStream(routingContext, vertxRequest);

        try {
            this.dispatcher.service(routingContext, ctx, request, response, vertxRequest, vertxResponse, true);
        } catch (Failure failure) {
            vertxResponse.setStatus(failure.getErrorCode());
        } catch (Exception e) {
            vertxResponse.setStatus(500);
            LogMessages.LOGGER.error(Messages.MESSAGES.unexpected(), e);
        }

        if (!vertxRequest.getAsyncContext().isSuspended()) {
            try {
                vertxResponse.finish();
            } catch (IOException var10) {
                var10.printStackTrace();
            }
        }

    }

    private void populateInputStream(final RoutingContext routingContext, final VertxHttpRequest vertxRequest) {

        Buffer buff = routingContext.getBody();
        if (buff.length() > 0) {
            ByteBufInputStream e = new ByteBufInputStream(buff.getByteBuf());
            vertxRequest.setInputStream(e);
        }
    }
}
