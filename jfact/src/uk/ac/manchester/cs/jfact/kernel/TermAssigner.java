package uk.ac.manchester.cs.jfact.kernel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.*;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")
public class TermAssigner {
    /**
     * 
     */
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PassedVertice")
    private Set<QRVariable> PassedVertice;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
    private QRQuery Query;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Factory")
    private AtomicLong Factory = new AtomicLong();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "N")
    private int N = 0;

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")
    public TermAssigner(ConjunctiveQueryFolding conjunctiveQueryFolding, QRQuery query) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
        Query = new QRQuery(query);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Assign")
    public ConceptExpression Assign(QRAtom previousAtom, QRVariable v) {
        System.out.println("Assign:\n variable: " + v + "\n atom:" + previousAtom);
        PassedVertice.add(v);
        ConceptExpression t;
        if (Query.isFreeVar(conjunctiveQueryFolding.getNewVarMap().get(v))) {
            t = conjunctiveQueryFolding.getpEM().concept(
                    conjunctiveQueryFolding.getNewVarMap().get(v).getName() + ":"
                            + Factory.incrementAndGet());
        } else {
            t = conjunctiveQueryFolding.getpEM().top();
        }
        ConceptExpression s = conjunctiveQueryFolding.getpEM().top();
        for (QRAtom atomIterator : Query.getBody().begin()) {
            if (atomIterator instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) atomIterator;
                ObjectRoleExpression role = atom.getRole();
                QRVariable arg1 = (QRVariable) atom.getArg1();
                QRVariable arg2 = (QRVariable) atom.getArg2();
                if (atomIterator == previousAtom) {
                    continue;
                }
                if (arg1 == v) {
                    ConceptExpression p = Assign(atomIterator, arg2);
                    ConceptExpression p1 = conjunctiveQueryFolding.getpEM().exists(role,
                            p);
                    ConceptExpression newS = conjunctiveQueryFolding.getpEM().and(s, p1);
                    s = newS;
                }
                if (arg2 == v) {
                    ConceptExpression p = Assign(atomIterator, arg1);
                    ConceptExpression p1 = conjunctiveQueryFolding.getpEM().exists(
                            conjunctiveQueryFolding.getpEM().inverse(role), p);
                    ConceptExpression newS = conjunctiveQueryFolding.getpEM().and(s, p1);
                    s = newS;
                }
            }
            if (atomIterator instanceof QRConceptAtom) {
                QRConceptAtom atom = (QRConceptAtom) atomIterator;
                ConceptExpression concept = atom.getConcept();
                QRVariable arg = (QRVariable) atom.getArg();
                if (arg == v) {
                    ConceptExpression newS = conjunctiveQueryFolding.getpEM().and(s,
                            concept);
                    s = newS;
                }
            }
        }
        return conjunctiveQueryFolding.getpEM().and(t, s);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "DeleteFictiveVariables")
    public void DeleteFictiveVariables() {
        Set<QRVariable> RealFreeVars = new HashSet<QRVariable>();
        for (QRAtom atomIterator : Query.getBody().begin()) {
            if (atomIterator instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) atomIterator;
                QRVariable arg1 = (QRVariable) atom.getArg1();
                QRVariable arg2 = (QRVariable) atom.getArg2();
                if (Query.isFreeVar(arg1)) {
                    RealFreeVars.add(arg1);
                }
                if (Query.isFreeVar(arg2)) {
                    RealFreeVars.add(arg2);
                }
            }
        }
        Query.setFreeVars(RealFreeVars);
    }
}
