package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TEquationSolver")
class TEquationSolver implements Serializable {

    private static final long serialVersionUID = 11000L;
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "LeftPart")
    private ConceptExpression LeftPart;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "RightPart")
    private ConceptExpression RightPart;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ExpressionMarker")
    private final TExpressionMarker ExpressionMarker;

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TEquationSolver")
    public TEquationSolver(ConjunctiveQueryFolding conjunctiveQueryFolding,
            ConceptExpression leftPart, IRI propositionalVariable,
            TExpressionMarker expressionMarker) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
        LeftPart = leftPart;
        RightPart = this.conjunctiveQueryFolding.getpEM().concept(
                propositionalVariable);
        ExpressionMarker = expressionMarker;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Solve")
    public void Solve() {
        while (!conjunctiveQueryFolding.isNominal(LeftPart)) {
            if (LeftPart instanceof ConceptObjectExists) {
                ConceptObjectExists leftDiamond = (ConceptObjectExists) LeftPart;
                ObjectRoleInverse invRole = (ObjectRoleInverse) leftDiamond
                        .getOR();
                ObjectRoleExpression role = invRole.getOR();
                ConceptExpression newLeftPart = leftDiamond.getConcept();
                ConceptExpression newRightPart = conjunctiveQueryFolding
                        .getpEM().forall(role, RightPart);
                LeftPart = newLeftPart;
                RightPart = newRightPart;
            } else if (LeftPart instanceof ConceptAnd) {
                ConceptAnd leftAnd = (ConceptAnd) LeftPart;
                ConceptExpression arg1 = leftAnd.getArguments().get(0);
                ConceptExpression arg2 = leftAnd.getArguments().get(1);
                if (!ExpressionMarker.KnownToBeSimple(arg1)) {
                    ConceptExpression t;
                    t = arg1;
                    arg1 = arg2;
                    arg2 = t;
                }
                ConceptExpression newLeftPart = arg1;
                ConceptExpression newRightPart;
                if (arg2 instanceof ConceptTop) {
                    newRightPart = RightPart;
                } else {
                    newRightPart = conjunctiveQueryFolding.getpEM().or(
                            conjunctiveQueryFolding.getpEM().not(arg2),
                            RightPart);
                }
                LeftPart = newLeftPart;
                RightPart = newRightPart;
            }
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getNominal")
    public String getNominal() {
        IRI longNominal = ((ConceptName) LeftPart).getName();
        int colon = longNominal.toString().lastIndexOf(':');
        return longNominal.toString().substring(0, colon);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getPhi")
    public ConceptExpression getPhi() {
        return RightPart;
    }
}
