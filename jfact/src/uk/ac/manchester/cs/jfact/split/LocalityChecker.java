package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import conformance.PortedFrom;

@PortedFrom(file = "LocalityChecker.h", name = "LocalityChecker")
public interface LocalityChecker {
    boolean local(Axiom axiom);

    /** allow the checker to preprocess an ontology if necessary */
    void preprocessOntology(Collection<Axiom> vec);

    void setSignatureValue(TSignature sig);

    TSignature getSignature();
}
