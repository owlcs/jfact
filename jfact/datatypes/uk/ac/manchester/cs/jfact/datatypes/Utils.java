package uk.ac.manchester.cs.jfact.datatypes;

import java.util.HashSet;
import java.util.Set;

public class Utils {
    public static Set<Facet> getFacets(final Facet... facets) {
        Set<Facet> toReturn = new HashSet<Facet>();
        for (Facet f : facets) {
            toReturn.add(f);
        }
        return toReturn;
    }

    public static Set<Facet> getFacets(final Facet[]... facets) {
        Set<Facet> toReturn = new HashSet<Facet>();
        for (Facet[] fac : facets) {
            for (Facet f : fac) {
                toReturn.add(f);
            }
        }
        return toReturn;
    }

    public static Set<Datatype<?>> generateAncestors(final Datatype<?> d) {
        Set<Datatype<?>> toReturn = new HashSet<Datatype<?>>(d.getAncestors());
        toReturn.add(d);
        return toReturn;
    }
}
