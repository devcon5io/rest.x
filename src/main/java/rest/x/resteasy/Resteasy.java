package rest.x.resteasy;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualifier for Resteasy Verticle to be used to define custom configurations.
 * For example:
 * <pre><code>
 *     @Produces
 *     @Resteasy
 *     DeploymentOptions getHttpConfiguration(){
 *         return new DeploymentOptions(new JsonObject().put("httpPort", 8080));
 *     }
 * </code></pre>
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Resteasy {

}
