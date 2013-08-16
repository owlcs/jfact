package uk.ac.manchester.cs.jfact.kernel;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRQuery;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRVariable;
import conformance.PortedFrom;

/** @author ignazio */
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryApproximation")
public class QueryApproximation extends BuildELIOConcept {
    private static final long serialVersionUID = 11000L;

    /** @param conjunctiveQueryFolding
     * @param query */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryApproximation")
    public QueryApproximation(ConjunctiveQueryFolding conjunctiveQueryFolding,
            QRQuery query) {
        super(conjunctiveQueryFolding, query);
    }

    @Override
    protected ConceptExpression createConceptByVar(QRVariable v) {
        return conjunctiveQueryFolding.createConceptByVar(v);
    }
}
