package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import testbase.TestBase;

class TopObjectPropertyTestCase extends TestBase {

    @Test
    void testReasoner3() throws OWLOntologyCreationException {
        OWLOntology ont = m.createOntology();
        OWLDataProperty p = df.getOWLDataProperty(IRI.create("http://example.com/p"));
        // just so p is known in the ontology
        m.applyChange(new AddAxiom(ont, df.getOWLFunctionalDataPropertyAxiom(p)));
        OWLReasonerFactory fac = factory();
        OWLReasoner r = fac.createReasoner(ont);
        assertTrue(r.isEntailed(
            df.getOWLSubClassOfAxiom(df.getOWLDataSomeValuesFrom(p, df.getIntegerOWLDatatype()),
                df.getOWLDataSomeValuesFrom(p, df.getTopDatatype()))));
    }

    @Test
    void testReasoner4() throws OWLOntologyCreationException {
        OWLOntology ont = m.createOntology();
        OWLReasonerFactory fac = factory();
        OWLReasoner r = fac.createReasoner(ont);
        assertTrue(r.getTopDataPropertyNode().entities().findAny().isPresent());
    }
}
