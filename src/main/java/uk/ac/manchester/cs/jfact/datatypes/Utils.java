package uk.ac.manchester.cs.jfact.datatypes;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/** @author ignazio */
public class Utils implements Serializable {

    private Utils() {}

    /**
     * @param facets
     *        facets
     * @return set of facets
     */
    public static Set<Facet> getFacets(Facet... facets) {
        return asSet(Stream.of(facets));
    }

    /**
     * @param facets
     *        facets
     * @return set of facets
     */
    public static Set<Facet> getFacets(Facet[]... facets) {
        return asSet(Stream.of(facets).flatMap(Stream::of));
    }

    /**
     * @param d
     *        d
     * @return ancestors
     */
    public static Set<Datatype<?>> generateAncestors(Datatype<?> d) {
        Set<Datatype<?>> toReturn = new HashSet<>(d.getAncestors());
        toReturn.add(d);
        return toReturn;
    }
}
