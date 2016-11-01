package rest.x.jaxrs;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
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


/**
 *
 */
@Provider
@Produces("application/json")
public class JavaxJsonMessageBodyWriter implements MessageBodyWriter<JsonStructure>{

    @Override
    public boolean isWriteable(final Class<?> type,
                               final Type genericType,
                               final Annotation[] annotations,
                               final MediaType mediaType) {

        return JsonObject.class.isAssignableFrom(type) || JsonArray.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(final JsonStructure jsonObject,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType) {

        return -1;
    }

    @Override
    public void writeTo(final JsonStructure jsonObject,
                        final Class<?> type,
                        final Type genericType,
                        final Annotation[] annotations,
                        final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders,
                        final OutputStream entityStream) throws IOException, WebApplicationException {

        try(JsonWriter writer = Json.createWriter(entityStream)){
            writer.write(jsonObject);
        }
    }
}
