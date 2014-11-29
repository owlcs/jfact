package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import java.io.Serializable;

import javax.annotation.Nonnull;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRAtom;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRConceptAtom;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRQuery;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRRoleAtom;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRVariable;
import conformance.PortedFrom;

/** build elio concept */
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "BuildELIOConcept")
public abstract class BuildELIOConcept implements Serializable {

    private static final long serialVersionUID = 11000L;
    protected final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
    protected final QRQuery Query;

    // @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PassedVertice")
    // private final Set<QRVariable> visited = new TreeSet<QRVariable>();
    protected BuildELIOConcept(ConjunctiveQueryFolding c, QRQuery query) {
        conjunctiveQueryFolding = c;
        Query = query;
    }

    /**
     * assign the concept to a term
     * 
     * @param query
     *        query
     * @param previousAtom
     *        previousAtom
     * @param v
     *        v
     * @return assigned concept
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Assign")
    @Nonnull
    public ConceptExpression Assign(QRQuery query, QRAtom previousAtom,
            QRVariable v) {
        // System.out.println("Assign:\n variable: " + v + "\n atom:" +
        // previousAtom);
        // XXX unused?
        // visited.add(v);
        ConceptExpression t = createConceptByVar(v);
        ConceptExpression s = top();
        for (QRAtom atomIterator : Query.getBody().begin()) {
            if (atomIterator instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) atomIterator;
                ObjectRoleExpression role = atom.getRole();
                QRVariable arg1 = (QRVariable) atom.getArg1();
                QRVariable arg2 = (QRVariable) atom.getArg2();
                if (atomIterator == previousAtom) {
                    continue;
                }
                if (arg1.equals(v)) {
                    ConceptExpression p = Assign(query, atomIterator, arg2);
                    p = exists(role, p);
                    s = and(s, p);
                }
                if (arg2.equals(v)) {
                    ConceptExpression p = Assign(query, atomIterator, arg1);
                    p = exists(conjunctiveQueryFolding.getpEM().inverse(role),
                            p);
                    s = and(s, p);
                }
            }
            if (atomIterator instanceof QRConceptAtom) {
                QRConceptAtom atom = (QRConceptAtom) atomIterator;
                ConceptExpression concept = atom.getConcept();
                QRVariable arg = (QRVariable) atom.getArg();
                if (arg.equals(v)) {
                    s = and(s, concept);
                }
            }
        }
        return and(t, s);
    }

    @Nonnull
    protected abstract ConceptExpression createConceptByVar(QRVariable v);
}
