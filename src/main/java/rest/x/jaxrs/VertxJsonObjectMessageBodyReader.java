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

import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;

/**
 *
 */
@Provider
@Consumes("application/json")
public class VertxJsonObjectMessageBodyReader implements MessageBodyReader<JsonObject>{

    @Override
    public boolean isReadable(final Class<?> type,
                              final Type genericType,
                              final Annotation[] annotations,
                              final MediaType mediaType) {

        return JsonObject.class.isAssignableFrom(type);
    }

    @Override
    public JsonObject readFrom(final Class<JsonObject> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType,
                               final MultivaluedMap<String, String> httpHeaders,
                               final InputStream entityStream) throws IOException, WebApplicationException {

        String jsonString = IOUtils.toString(entityStream, Charset.forName("UTF-8"));
        return new JsonObject(jsonString);
    }
}
