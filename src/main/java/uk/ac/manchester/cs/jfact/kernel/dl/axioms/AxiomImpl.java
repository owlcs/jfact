package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapitools.decomposition.AxiomWrapper;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

/**
 * Axiom implementation
 */
@PortedFrom(file = "tDLAxiom.h", name = "TDLAxiom")
public abstract class AxiomImpl extends AxiomWrapper implements Serializable {

    /**
     * @param ax
     *        axiom
     */
    public AxiomImpl(@Nullable OWLAxiom ax) {
        super(ax);
    }

    /**
     * accept method for the visitor pattern
     * 
     * @param visitor
     *        visitor
     */
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    public abstract void accept(DLAxiomVisitor visitor);

    /**
     * @param visitor
     *        visitor
     * @param <O>
     *        visitor return type
     * @return visitor result
     */
    @Nullable
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    public abstract <O> O accept(DLAxiomVisitorEx<O> visitor);

    /** @return named entities included in this axiom */
    public abstract Stream<Expression> namedEntitySignature();
}
