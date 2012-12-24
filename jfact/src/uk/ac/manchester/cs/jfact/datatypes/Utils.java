package uk.ac.manchester.cs.jfact.datatypes;

import java.util.HashSet;
import java.util.Set;

/** @author ignazio */
public class Utils {
    /** @param facets
     * @return set of facets */
    public static Set<Facet> getFacets(Facet... facets) {
        Set<Facet> toReturn = new HashSet<Facet>();
        for (Facet f : facets) {
            toReturn.add(f);
        }
        return toReturn;
    }

    /** @param facets
     * @return set of facets */
    public static Set<Facet> getFacets(Facet[]... facets) {
        Set<Facet> toReturn = new HashSet<Facet>();
        for (Facet[] fac : facets) {
            for (Facet f : fac) {
                toReturn.add(f);
            }
        }
        return toReturn;
    }

    /** @param d
     * @return ancestors */
    public static Set<Datatype<?>> generateAncestors(Datatype<?> d) {
        Set<Datatype<?>> toReturn = new HashSet<Datatype<?>>(d.getAncestors());
        toReturn.add(d);
        return toReturn;
    }
}
