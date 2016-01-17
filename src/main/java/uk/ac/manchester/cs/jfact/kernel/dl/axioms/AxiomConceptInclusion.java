package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

import java.util.stream.Stream;

import javax.annotation.Nullable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLAxiom;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

/** SubClassOf */
@PortedFrom(file = "tDLAxiom.h", name = "TDLAxiomConceptInclusion")
public class AxiomConceptInclusion extends AxiomImpl {

    @PortedFrom(file = "tDLAxiom.h", name = "Sub") private final ConceptExpression subConcept;
    @PortedFrom(file = "tDLAxiom.h", name = "Sup") private final ConceptExpression superConcept;

    /**
     * @param ax
     *        ax
     * @param sub
     *        sub
     * @param sup
     *        sup
     */
    public AxiomConceptInclusion(OWLAxiom ax, ConceptExpression sub, ConceptExpression sup) {
        super(ax);
        subConcept = sub;
        superConcept = sup;
    }

    @Override
    public Stream<Expression> namedEntitySignature() {
        return Stream.of(subConcept, superConcept);
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    public void accept(DLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    public <O> O accept(DLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    /** @return sub concept */
    @PortedFrom(file = "tDLAxiom.h", name = "getSubC")
    public ConceptExpression getSubConcept() {
        return subConcept;
    }

    /** @return super concept */
    @PortedFrom(file = "tDLAxiom.h", name = "getSupC")
    public ConceptExpression getSupConcept() {
        return superConcept;
    }

    @Override
    public String toString() {
        return subConcept.toString() + " subClassOf " + superConcept.toString();
    }
}
