package conformance;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Mark a field, method, method argument or type with the original file and
 * original name in FaCT++
 * 
 * @author ignazio */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PortedFrom {
    /** the file with the definition */
    String file();

    /** the original name */
    String name();

    /** optional method name, for method arguments */
    String argumentFor() default "";
}
