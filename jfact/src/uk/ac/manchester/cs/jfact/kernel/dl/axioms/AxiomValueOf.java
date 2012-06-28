package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

public final class AxiomValueOf extends AxiomIndividual {
    private final DataRoleExpression dataRoleExpression;
    private final Literal<?> value;

    public AxiomValueOf(final OWLAxiom ax, final IndividualExpression i,
            final DataRoleExpression a, final Literal<?> v) {
        super(ax, i);
        dataRoleExpression = a;
        value = v;
    }

    public void accept(final DLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(final DLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    /** access to role */
    public final DataRoleExpression getAttribute() {
        return dataRoleExpression;
    }

    /** access to value */
    public final Literal<?> getValue() {
        return value;
    }
}
