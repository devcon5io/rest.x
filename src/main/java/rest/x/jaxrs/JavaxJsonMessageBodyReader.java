package rest.x.jaxrs;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
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

/**
 *
 */
@Provider
@Consumes("application/json")
public class JavaxJsonMessageBodyReader implements MessageBodyReader<JsonStructure> {

    @Override
    public boolean isReadable(final Class<?> type,
                              final Type genericType,
                              final Annotation[] annotations,
                              final MediaType mediaType) {

        return JsonObject.class.isAssignableFrom(type) || JsonArray.class.isAssignableFrom(type);
    }

    @Override
    public JsonStructure readFrom(final Class<JsonStructure> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType,
                               final MultivaluedMap<String, String> httpHeaders,
                               final InputStream entityStream) throws IOException, WebApplicationException {

        try(JsonReader reader = Json.createReader(entityStream)){
            return reader.read();
        }
    }
}
