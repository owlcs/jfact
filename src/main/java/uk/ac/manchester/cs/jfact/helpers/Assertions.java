package uk.ac.manchester.cs.jfact.helpers;

import javax.annotation.Nullable;

/**
 * @author ignazio Assertions utility.
 */
public final class Assertions {
    private Assertions() {
        // no instances
    }

    /**
     * Check that the argument is not null; if the argument is null, throw an IllegalStateException.
     * This method is meant to be used to verify conditions on member variables rather than input
     * parameters.
     *
     * @param object reference to check
     * @param <T> reference type
     * @return the input reference if not null
     * @throws IllegalStateException if object is null
     */
    public static <T> T verifyNotNull(@Nullable T object) {
        return verifyNotNull(object, "value cannot be null at this stage");
    }

    /**
     * Check that the argument is not null; if the argument is null, throw an IllegalStateException.
     * This method is meant to be used to verify conditions on member variables rather than input
     * parameters.
     *
     * @param object reference to check
     * @param message message to use for the error
     * @param <T> reference type
     * @return the input reference if not null
     * @throws IllegalStateException if object is null
     */
    public static <T> T verifyNotNull(@Nullable T object, String message) {
        if (object == null) {
            throw new IllegalStateException(message);
        }
        return object;
    }
}
