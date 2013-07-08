package uk.ac.manchester.cs.jfact.kernel;

import java.util.concurrent.atomic.AtomicLong;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRQuery;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRVariable;
import conformance.PortedFrom;


@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")

public class TermAssigner extends BuildELIOConcept {

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "N")
    private int N = 0;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Factory")
    private AtomicLong Factory = new AtomicLong();

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")
    public TermAssigner(ConjunctiveQueryFolding conjunctiveQueryFolding, QRQuery query) {
        super(conjunctiveQueryFolding, query);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "createConceptByVar")
    protected ConceptExpression createConceptByVar(QRVariable v) {
        if (Query.isFreeVar(v)) {
            ConceptExpression concept = conjunctiveQueryFolding.getpEM().concept(
                    conjunctiveQueryFolding.getNewVarMap().get(v).getName() + ":"
                            + Factory.incrementAndGet());
            conjunctiveQueryFolding.addNominal(concept);
            return concept;
        }
        return conjunctiveQueryFolding.getpEM().top();
    }


}
