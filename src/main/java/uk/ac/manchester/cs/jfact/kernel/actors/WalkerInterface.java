package uk.ac.manchester.cs.jfact.kernel.actors;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.Taxonomy;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;

/**
 * Base class for taxonomy walkers that provide necessary interface.
 */
@PortedFrom(file = "WalkerInterface.h", name = "WalkerInterface")
public interface WalkerInterface {

    /**
     * Taxonomy walking method.
     * 
     * @param v
     *        taxonomy vertex
     * @return true if node was processed, false if node can not be processed in
     *         current settings
     */
    boolean apply(TaxonomyVertex v);

    /**
     * Remove indirect nodes in the given taxonomy according to direction.
     * 
     * @param v
     *        taxonomy
     * @param direction
     *        up or down
     */
    @SuppressWarnings("unused")
    default void removeIndirect(Taxonomy v, boolean direction) {}
}
