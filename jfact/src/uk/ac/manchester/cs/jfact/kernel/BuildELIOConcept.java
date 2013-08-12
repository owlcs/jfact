package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.*;
import conformance.PortedFrom;

@SuppressWarnings("javadoc")
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "BuildELIOConcept")
public abstract class BuildELIOConcept implements Serializable {
    private static final long serialVersionUID = 11000L;
    protected final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
    protected final QRQuery Query;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PassedVertice")
    private final Set<QRVariable> PassedVertice = new TreeSet<QRVariable>();

    protected BuildELIOConcept(ConjunctiveQueryFolding c, QRQuery query) {
        conjunctiveQueryFolding = c;
        Query = query;
    }

    /** assign the concept to a term */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Assign")
    public ConceptExpression Assign(QRQuery query, QRAtom previousAtom, QRVariable v) {
        // System.out.println("Assign:\n variable: " + v + "\n atom:" +
        // previousAtom);
        PassedVertice.add(v);
        ConceptExpression t = createConceptByVar(v);
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
                    ConceptExpression p = Assign(query, atomIterator, arg2);
                    p = conjunctiveQueryFolding.getpEM().exists(role, p);
                    s = conjunctiveQueryFolding.getpEM().and(s, p);
                }
                if (arg2 == v) {
                    ConceptExpression p = Assign(query, atomIterator, arg1);
                    p = conjunctiveQueryFolding.getpEM().exists(
                            conjunctiveQueryFolding.getpEM().inverse(role), p);
                    s = conjunctiveQueryFolding.getpEM().and(s, p);
                }
            }
            if (atomIterator instanceof QRConceptAtom) {
                QRConceptAtom atom = (QRConceptAtom) atomIterator;
                ConceptExpression concept = atom.getConcept();
                QRVariable arg = (QRVariable) atom.getArg();
                if (arg == v) {
                    s = conjunctiveQueryFolding.getpEM().and(s, concept);
                }
            }
        }
        return conjunctiveQueryFolding.getpEM().and(t, s);
    }

    protected abstract ConceptExpression createConceptByVar(QRVariable v);
}
