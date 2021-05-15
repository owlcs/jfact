package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;

class WebOnt_description_logic_602_TestCase extends TestBase {

    @Test
    void testWebOnt_description_logic_602() throws OWLOntologyCreationException {
        OWLOntology o = m.createOntology();
        OWLClass A = df.getOWLClass(IRI.create("urn:A"));
        OWLClass C = df.getOWLClass(IRI.create("urn:C"));
        OWLClass D = df.getOWLClass(IRI.create("urn:D"));
        OWLClass B = df.getOWLClass(IRI.create("urn:B"));
        OWLClass U = df.getOWLClass(IRI.create("urn:U"));
        OWLObjectProperty p = df.getOWLObjectProperty(IRI.create("urn:p"));
        OWLObjectProperty r = df.getOWLObjectProperty(IRI.create("urn:r"));
        OWLObjectAllValuesFrom rAllC = df.getOWLObjectAllValuesFrom(r, C);
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(A, rAllC));
        m.addAxiom(o, df.getOWLSubClassOfAxiom(A, D));
        m.addAxiom(o, df.getOWLSubClassOfAxiom(U, C));
        m.addAxiom(o, df.getOWLSubClassOfAxiom(U, B));
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(C, rAllC));
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(D, df.getOWLObjectMaxCardinality(0, p)));
        m.addAxiom(o, df.getOWLEquivalentClassesAxiom(B, df.getOWLObjectMinCardinality(1, p)));
        OWLReasoner reasoner = factory().createReasoner(o);
        assertTrue(reasoner.isEntailed(df.getOWLDisjointClassesAxiom(D, B)),
            "cannot infer disjoint");
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(U, B)), "cannot infer U [= B");
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(U, C)), "cannot infer U [= C");
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(C, rAllC)),
            "cannot infer C [= r some C");
        assertTrue(reasoner.isEntailed(df.getOWLEquivalentClassesAxiom(rAllC, A)),
            "cannot infer r some C = A");
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(A, D)), "cannot infer A [= D");
        assertTrue(reasoner.isEntailed(df.getOWLSubClassOfAxiom(U, D)), "cannot infer U [= D");
        assertFalse(reasoner.isSatisfiable(U), "cannot find unsatisfiable class");
    }
}
