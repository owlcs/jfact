package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TReplacer")
class TReplacer extends DLExpressionVisitorAdapter {

    private static final long serialVersionUID = 11000L;
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ReplaceResult")
    private final Map<ConceptExpression, ConceptExpression> ReplaceResult = new HashMap<>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ExpressionToReplace")
    private final ConceptExpression ExpressionToReplace;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PropositionalVariable")
    private final ConceptExpression PropositionalVariable;

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TReplacer")
    public TReplacer(ConjunctiveQueryFolding conjunctiveQueryFolding,
            ConceptExpression expression, IRI propositionalVariable) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
        ExpressionToReplace = expression;
        PropositionalVariable = this.conjunctiveQueryFolding.getpEM().concept(
                propositionalVariable);
    }

    // concept expressions
    @Override
    public void visit(ConceptTop expr) {
        ReplaceResult.put(expr, expr);
    }

    @Override
    public void visit(ConceptName expr) {
        if (expr.equals(ExpressionToReplace)) {
            ReplaceResult.put(expr, PropositionalVariable);
        } else {
            ReplaceResult.put(expr, expr);
        }
    }

    @Override
    public void visit(ConceptAnd expr) {
        if (expr.equals(ExpressionToReplace)) {
            ReplaceResult.put(expr, PropositionalVariable);
        } else {
            ConceptExpression s = null;
            for (ConceptExpression p : expr.getArguments()) {
                p.accept(this);
                if (p.equals(expr.getArguments().get(0))) {
                    s = ReplaceResult.get(p);
                } else {
                    s = and(s, ReplaceResult.get(p));
                }
            }
            ReplaceResult.put(expr, s);
        }
    }

    @Override
    public void visit(ConceptObjectExists expr) {
        if (expr.equals(ExpressionToReplace)) {
            ReplaceResult.put(expr, PropositionalVariable);
        } else {
            ObjectRoleExpression role = expr.getOR();
            expr.getConcept().accept(this);
            ReplaceResult.put(expr,
                    exists(role, ReplaceResult.get(expr.getConcept())));
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getReplaceResult")
    public
            ConceptExpression getReplaceResult(ConceptExpression c) {
        return ReplaceResult.get(c);
    }
}
