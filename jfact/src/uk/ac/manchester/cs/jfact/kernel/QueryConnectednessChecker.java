package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.*;
import conformance.PortedFrom;

/** connectivity checker */
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryConnectednessChecker")
public class QueryConnectednessChecker implements Serializable {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PassedVertice")
    private final Set<QRVariable> PassedVertice = new TreeSet<QRVariable>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
    private final QRQuery Query;

    /** @param query */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryConnectednessChecker")
    public QueryConnectednessChecker(QRQuery query) {
        Query = new QRQuery(query);
    }

    /** @return true if connected */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "isConnected")
    public boolean isConnected() {
        QRAtom a = Query.getBody().begin().iterator().next();
        if (a instanceof QRRoleAtom) {
            MarkVertex((QRVariable) ((QRRoleAtom) a).getArg1());
        } else if (a instanceof QRConceptAtom) {
            MarkVertex((QRVariable) ((QRConceptAtom) a).getArg());
        } else {
            throw new UnreachableSituationException("Unsupported atom in query rewriting");
        }
        for (QRAtom atomIterator : Query.getBody().begin()) {
            if (atomIterator instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) atomIterator;
                QRVariable arg1 = (QRVariable) atom.getArg1();
                QRVariable arg2 = (QRVariable) atom.getArg2();
                if (!PassedVertice.contains(arg1) || !PassedVertice.contains(arg2)) {
                    return false;
                }
            } else if (atomIterator instanceof QRConceptAtom) {
                QRConceptAtom atom = (QRConceptAtom) atomIterator;
                if (!PassedVertice.contains(atom.getArg())) {
                    return false;
                }
            } else {
                throw new UnreachableSituationException(
                        "Unsupported atom in query rewriting");
            }
        }
        return true;
    }

    /** @param var */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "MarkVertex")
    public void MarkVertex(QRVariable var) {
        PassedVertice.add(var);
        for (QRAtom atomIterator : Query.getBody().begin()) {
            if (atomIterator instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) atomIterator;
                QRVariable arg1 = (QRVariable) atom.getArg1();
                QRVariable arg2 = (QRVariable) atom.getArg2();
                if (!arg1.equals(var) && !arg2.equals(var) || arg1.equals(var)
                        && arg2.equals(var)) {
                    continue;
                }
                QRVariable neighbour;
                if (arg1.equals(var)) {
                    neighbour = arg2;
                } else {
                    neighbour = arg1;
                }
                if (PassedVertice.contains(neighbour)) {
                    continue;
                }
                MarkVertex(neighbour);
            }
        }
    }
}
