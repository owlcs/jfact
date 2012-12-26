package conformance;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Mark a field, method, method argument or type as original - not ported from
 * FaCT++, or with no simple correspondence
 * 
 * @author ignazio */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Original {}
