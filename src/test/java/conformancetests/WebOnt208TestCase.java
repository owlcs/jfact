package conformancetests;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import testbase.TestBase;

@SuppressWarnings("javadoc")
public class WebOnt208TestCase extends TestBase {

    @Test
    public void testWebOnt_description_logic_208() throws OWLOntologyCreationException {
        String ns = "http://oiled.man.example.net/test#";
        OWLOntology conclusions = m.createOntology();
        OWLClass c38 = df.getOWLClass(ns, "C38");
        conclusions.add(df.getOWLClassAssertionAxiom(c38, df.getOWLNamedIndividual(ns, "V16457")));
        OWLClass c44 = df.getOWLClass(ns, "C44");
        OWLNamedIndividual v16464 = df.getOWLNamedIndividual(ns, "V16464");
        conclusions.add(df.getOWLClassAssertionAxiom(c44, v16464));
        OWLClass c56 = df.getOWLClass(ns, "C56");
        conclusions.add(df.getOWLClassAssertionAxiom(c56, v16464));
        OWLNamedIndividual v16461 = df.getOWLNamedIndividual(ns, "V16461");
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C42"), v16461));
        conclusions.add(df.getOWLClassAssertionAxiom(c44, v16461));
        conclusions.add(df.getOWLClassAssertionAxiom(c56, v16461));
        OWLNamedIndividual v16440 = df.getOWLNamedIndividual(ns, "V16440");
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C80"), v16440));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C82"), v16440));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C98"), v16440));
        OWLNamedIndividual v16455 = df.getOWLNamedIndividual(ns, "V16455");
        conclusions.add(df.getOWLClassAssertionAxiom(c44, v16455));
        conclusions.add(df.getOWLClassAssertionAxiom(c56, v16455));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C42"), v16464));
        OWLNamedIndividual v16453 = df.getOWLNamedIndividual(ns, "V16453");
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C62"), v16453));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C76"), v16453));
        conclusions.add(df.getOWLClassAssertionAxiom(c38, df.getOWLNamedIndividual(ns, "V16462")));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C42"), v16455));
        OWLNamedIndividual v16463 = df.getOWLNamedIndividual(ns, "V16463");
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C76"), v16463));
        conclusions.add(df.getOWLClassAssertionAxiom(c38, df.getOWLNamedIndividual(ns, "V16460")));
        conclusions.add(df.getOWLClassAssertionAxiom(c38, df.getOWLNamedIndividual(ns, "V16465")));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C60"), v16453));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C62"), v16463));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C60"), v16463));
        OWLNamedIndividual v16459 = df.getOWLNamedIndividual(ns, "V16459");
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C34"), v16459));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C68"), v16459));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C88"), v16459));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C50"), v16459));
        conclusions.add(df.getOWLClassAssertionAxiom(c38, df.getOWLNamedIndividual(ns, "V16448")));
        OWLNamedIndividual v16439 = df.getOWLNamedIndividual(ns, "V16439");
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C130"), v16439));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C28"), v16439));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C108"), v16439));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C106"), v16439));
        conclusions.add(df.getOWLClassAssertionAxiom(df.getOWLClass(ns, "C104"), v16439));
        JUnitRunner r = new JUnitRunner(m, asString(m, "/WebOnt_description_logic_208.owl"),
            conclusions, "WebOnt_description_logic_208", TestClasses.POSITIVE_IMPL,
            "DL Test: k_poly\n" + "ABox test from DL98 systems comparison.");
        r.setReasonerFactory(factory());
        r.run();
    }
}
