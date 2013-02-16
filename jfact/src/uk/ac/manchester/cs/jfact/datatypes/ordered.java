package uk.ac.manchester.cs.jfact.datatypes;

/** ordered */
public enum ordered {
    /** not ordered */
    FALSE("false"),
    /** partially ordered */
    PARTIAL("partial"),
    /** totally ordered */
    TOTAL("total");
    /** @param string
     * @return ordered */
    public static ordered parse(String string) {
        for (ordered o : values()) {
            if (o.name.equals(string)) {
                return o;
            }
        }
        return null;
    }

    private String name;

    private ordered(String s) {
        name = s;
    }
}
