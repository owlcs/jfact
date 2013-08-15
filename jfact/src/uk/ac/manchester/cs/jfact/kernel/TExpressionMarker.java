package uk.ac.manchester.cs.jfact.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TExpressionMarker")
class TExpressionMarker extends DLExpressionVisitorAdapter {
    private static final long serialVersionUID = 11000L;
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "GoodTerms")
    private final Map<ConceptExpression, Boolean> GoodTerms = new HashMap<ConceptExpression, Boolean>();
    // A term is called good, if all of its subterms don't contain nominals
    // different from x
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "SimpleTerms")
    private final Map<ConceptExpression, Boolean> SimpleTerms = new HashMap<ConceptExpression, Boolean>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Path")
    private final List<ConceptExpression> Path = new ArrayList<ConceptExpression>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Nominal")
    private final ConceptExpression Nominal;

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TExpressionMarker")
    public TExpressionMarker(ConjunctiveQueryFolding conjunctiveQueryFolding,
            ConceptExpression nominal) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
        Nominal = nominal;
    }

    // concept expressions
    @Override
    public void visit(ConceptTop expr) {
        SimpleTerms.put(expr, false);
        GoodTerms.put(expr, true);
    }

    @Override
    public void visit(ConceptName expr) {
        SimpleTerms.put(expr, conjunctiveQueryFolding.isNominal(expr));
        if (expr.equals(Nominal)) {
            GoodTerms.put(expr, true);
            Path.add(expr);
        } else {
            GoodTerms.put(expr, !conjunctiveQueryFolding.isNominal(expr));
        }
    }

    @Override
    public void visit(ConceptAnd expr) {
        boolean simple = false;
        boolean good = true;
        boolean onPath = false;
        for (ConceptExpression p : expr.getArguments()) {
            p.accept(this);
            if (KnownToBeSimple(p)) {
                simple = true;
            }
            if (!KnownToBeGood(p)) {
                good = false;
            }
            if (KnownToBeOnPath(p)) {
                onPath = true;
            }
        }
        SimpleTerms.put(expr, simple);
        GoodTerms.put(expr, good);
        if (onPath && good && simple) {
            Path.add(expr);
        }
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        ObjectRoleExpression role1 = expr.getOR();
        if (role1 instanceof ObjectRoleName) {
            expr.getConcept().accept(this);
            SimpleTerms.put(expr, false);
        } else if (role1 instanceof ObjectRoleInverse) {
            expr.getConcept().accept(this);
            if (KnownToBeSimple(expr.getConcept())) {
                SimpleTerms.put(expr, true);
            } else {
                SimpleTerms.put(expr, false);
            }
        } else {
            throw new UnreachableSituationException();
        }
        GoodTerms.put(expr, KnownToBeGood(expr.getConcept()));
        if (KnownToBeOnPath(expr.getConcept()) && KnownToBeGood(expr)
                && KnownToBeSimple(expr)) {
            Path.add(expr);
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "KnownToBeSimple")
    public boolean KnownToBeSimple(ConceptExpression expr) {
        return SimpleTerms.containsKey(expr);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "KnownToBeGood")
    public boolean KnownToBeGood(ConceptExpression expr) {
        return GoodTerms.containsKey(expr);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "KnownToBeOnPath")
    public boolean KnownToBeOnPath(ConceptExpression expr) {
        return Path.size() >= 1 && Path.get(Path.size() - 1).equals(expr);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PrintPath")
    public void PrintPath() {
        for (int i = 0; i < Path.size(); ++i) {
            System.out.println("Expression on depth " + i + " :\n");
            System.out.println(Path.get(i));
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getSubterm")
    public ConceptExpression getSubterm() {
        if (Path.size() >= 1) {
            return Path.get(Path.size() - 1);
        } else {
            return null;
        }
    }
}
