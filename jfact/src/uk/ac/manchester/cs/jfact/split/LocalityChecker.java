package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.PortedFrom;

/** locality checker */
@PortedFrom(file = "LocalityChecker.h", name = "LocalityChecker")
public interface LocalityChecker {
    /** @param axiom
     * @return true if local */
    @PortedFrom(file = "LocalityChecker.h", name = "local")
    boolean local(AxiomInterface axiom);

    /** allow the checker to preprocess an ontology if necessary
     * 
     * @param vec */
    @PortedFrom(file = "LocalityChecker.h", name = "preprocessOntology")
    void preprocessOntology(Collection<AxiomInterface> vec);

    /** @param sig */
    @PortedFrom(file = "LocalityChecker.h", name = "setSignatureValue")
    void setSignatureValue(TSignature sig);

    /** @return signature */
    @PortedFrom(file = "LocalityChecker.h", name = "getSignature")
    TSignature getSignature();
}
