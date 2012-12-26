package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import conformance.PortedFrom;

@PortedFrom(file = "LocalityChecker.h", name = "LocalityChecker")
public interface LocalityChecker {
@PortedFrom(file="LocalityChecker.h",name="local")
    boolean local(Axiom axiom);

    /** allow the checker to preprocess an ontology if necessary */
@PortedFrom(file="LocalityChecker.h",name="preprocessOntology")
    void preprocessOntology(Collection<Axiom> vec);

@PortedFrom(file="LocalityChecker.h",name="setSignatureValue")
    void setSignatureValue(TSignature sig);

@PortedFrom(file="LocalityChecker.h",name="getSignature")
    TSignature getSignature();
}
