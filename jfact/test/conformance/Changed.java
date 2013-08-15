package conformance;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** denotes a change in tests
 * 
 * @author ignazio */
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Changed {
    /** @return reason for the change to have happened */
    String reason() default "Not OWL 2 Compliant";
}
