package uk.ac.manchester.cs.jfact.kernel;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRQuery;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRVariable;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryApproximation")
public class QueryApproximation extends BuildELIOConcept
{
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryApproximation")
    public QueryApproximation(ConjunctiveQueryFolding conjunctiveQueryFolding, QRQuery query) {
        super(conjunctiveQueryFolding, query);
    }
    
protected ConceptExpression createConceptByVar (  QRVariable v )
    {
        return conjunctiveQueryFolding.getpEM().top();
    }
}
