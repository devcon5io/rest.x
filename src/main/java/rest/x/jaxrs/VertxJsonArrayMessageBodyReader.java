package rest.x.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import io.vertx.core.json.JsonArray;
import org.apache.commons.io.IOUtils;

/**
 *
 */
@Provider
@Consumes("application/json")
public class VertxJsonArrayMessageBodyReader implements MessageBodyReader<JsonArray>{

    @Override
    public boolean isReadable(final Class<?> type,
                              final Type genericType,
                              final Annotation[] annotations,
                              final MediaType mediaType) {

        return JsonArray.class.isAssignableFrom(type);
    }

    @Override
    public JsonArray readFrom(final Class<JsonArray> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType,
                               final MultivaluedMap<String, String> httpHeaders,
                               final InputStream entityStream) throws IOException, WebApplicationException {

        String jsonString = IOUtils.toString(entityStream, Charset.forName("UTF-8"));
        return new JsonArray(jsonString);
    }
}
