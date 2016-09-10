package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

/** bottom */
@PortedFrom(file = "tDLExpression.h", name = "TDLConceptBottom")
public class ConceptBottom implements ConceptExpression, Serializable {

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "accept")
    public void accept(DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    @PortedFrom(file = "tDLExpression.h", name = "accept")
    public <O> O accept(DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return getIRI().toString();
    }

    @Override
    public IRI getIRI() {
        return OWLRDFVocabulary.OWL_NOTHING.getIRI();
    }
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public boolean equals(Object obj) {
     if(obj==null) {
         return false;
     }
     if(this==obj) {
         return true;
     }
     return obj instanceof ConceptBottom;
    }
}
