package rest.x.jaxrs;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import io.vertx.core.json.JsonArray;

/**
 *
 */
@Provider
@Produces("application/json")
public class VertxJsonArrayMessageBodyWriter implements MessageBodyWriter<JsonArray> {

    @Override
    public boolean isWriteable(final Class<?> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType) {

        return JsonArray.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(final JsonArray entries,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType) {

        return -1;
    }

    @Override
    public void writeTo(final JsonArray entries,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders,
                        final OutputStream entityStream) throws IOException, WebApplicationException {

        entityStream.write(entries.encode().getBytes(Charset.forName("UTF-8")));
    }
}
