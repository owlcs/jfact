package conformance;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Changed {
    String reason() default "Not OWL 2 Compliant";
}
