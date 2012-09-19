package uk.ac.manchester.cs.jfact.datatypes;

//equal facet: implemented by the equals() method on values
public enum ordered {
    FALSE("false"), PARTIAL("partial"), TOTAL("total");
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
