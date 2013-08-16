package uk.ac.manchester.cs.jfact.kernel;

import java.util.HashMap;
import java.util.Map;

import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.PortedFrom;

/** @author ignazio */
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TDepthMeasurer")
public class TDepthMeasurer extends DLExpressionVisitorAdapter {
    private static final long serialVersionUID = 11000L;
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;

    /** @param conjunctiveQueryFolding */
    public TDepthMeasurer(ConjunctiveQueryFolding conjunctiveQueryFolding) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "DepthOfNominalOccurences")
    private final Map<ConceptExpression, Integer> DepthOfNominalOccurences = new HashMap<ConceptExpression, Integer>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "CurrentDepth")
    private int CurrentDepth = 0;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TotalNominalOccurences")
    private int TotalNominalOccurences = 0;

    @Override
    public void visit(ConceptTop expr) {}

    @Override
    public void visit(ConceptName expr) {
        if (conjunctiveQueryFolding.isNominal(expr)) {
            DepthOfNominalOccurences.put(expr, CurrentDepth);
            ++TotalNominalOccurences;
        }
    }

    @Override
    public void visit(ConceptAnd expr) {
        for (ConceptExpression p : expr.getArguments()) {
            p.accept(this);
        }
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        ObjectRoleExpression role1 = expr.getOR();
        if (role1 instanceof ObjectRoleName) {
            ++CurrentDepth;
            expr.getConcept().accept(this);
            --CurrentDepth;
        } else if (role1 instanceof ObjectRoleInverse) {
            expr.getConcept().accept(this);
        }
    }

    /** @return max depth */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getMaxDepth")
    public int getMaxDepth() {
        int max = -1;
        for (Integer i : DepthOfNominalOccurences.values()) {
            if (i.intValue() > max) {
                max = i;
            }
        }
        return max;
    }

    /** @return nominal with max depth */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getNominalWithMaxDepth")
    public ConceptExpression getNominalWithMaxDepth() {
        ConceptExpression max = null;
        int maxvalue = -1;
        for (Map.Entry<ConceptExpression, Integer> e : DepthOfNominalOccurences
                .entrySet()) {
            if (e.getValue() >= maxvalue) {
                max = e.getKey();
                maxvalue = e.getValue();
            }
        }
        return max;
    }

    /**
     * 
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PrintDepthTable")
    public void PrintDepthTable() {
        System.out.println("Total nominal occurrences: " + TotalNominalOccurences + "\n");
        for (Map.Entry<ConceptExpression, Integer> e : DepthOfNominalOccurences
                .entrySet()) {
            System.out.print(e.getKey());
            System.out.println(" has depth " + e.getValue() + "\n");
        }
    }
}
