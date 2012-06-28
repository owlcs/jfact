package uk.ac.manchester.cs.jfact.datatypes;

public enum cardinality {
    FINITE, COUNTABLYINFINITE;
    public static cardinality parse(final String string) {
        if (string.equals("countably infinite")) {
            return COUNTABLYINFINITE;
        }
        //XXX not the best solution but should work
        return FINITE;
    }
}
