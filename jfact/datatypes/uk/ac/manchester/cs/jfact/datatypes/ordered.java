package uk.ac.manchester.cs.jfact.datatypes;

//equal facet: implemented by the equals() method on values
public enum ordered {
	FALSE("false"), PARTIAL("partial"), TOTAL("total");
	public static ordered parse(final String string) {
		for (ordered o : values()) {
			if (o.name.equals(string)) {
				return o;
			}
		}
		return null;
	}

	private final String name;

	private ordered(final String s) {
		name = s;
	}
}
