package conformancetests;

import static org.junit.Assert.assertTrue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

@SuppressWarnings("javadoc")
@Ignore
public class DebugWine extends TestBase {

    protected JFactReasonerConfiguration config(OutputStream o) {
        return new JFactReasonerConfiguration().setAbsorptionLoggingActive(true).setAbsorptionLog(o);
    }

    protected JFactReasonerConfiguration noconfig() {
        return new JFactReasonerConfiguration();
    }

    @Test
    public void shouldBeFastWithOldOrder() throws OWLOntologyCreationException {
        System.out.println("WebOnt_miscellaneous_wineTestCase.shouldBeFastWithOldOrder() ");
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        // java7order().forEach(ax -> System.out.println(ax));
        OWLReasoner r = new JFactReasoner(o, java7order(), config(System.out), BufferingMode.BUFFERING);
        assertTrue(r.isConsistent());
    }

    @Test
    public void shouldBeFastWithanyOrder() throws OWLOntologyCreationException {
        System.out.println("WebOnt_miscellaneous_wineTestCase.shouldBeFastWithanyOrder() ");
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        // java8order().forEach(ax -> System.out.println(ax));
        OWLReasoner r = new JFactReasoner(o, java8order(), config(System.out), BufferingMode.BUFFERING);
        assertTrue(r.isConsistent());
    }

    @Ignore
    @Test
    public void shouldBeFastWithOldOrderBuffered() throws OWLOntologyCreationException {
        System.out.println("WebOnt_miscellaneous_wineTestCase.shouldBeFastWithOldOrder() ");
        actualrun(java7order());
    }

    protected void actualrun(List<OWLAxiom> java7order) throws OWLOntologyCreationException {
        OWLOntology o = OWLManager.createOWLOntologyManager().createOntology();
        List<OWLAxiom> l = new ArrayList<>();
        for (OWLAxiom ax : java7order) {
            o.getOWLOntologyManager().addAxiom(o, ax);
            l.add(ax);
            System.out.println("\nDebugWine.actualrun() " + ax);
            OWLReasoner r = new JFactReasoner(o, l, config(System.out), BufferingMode.NON_BUFFERING);
            r.flush();
            assertTrue(r.isConsistent());
        }
    }

    @Ignore
    @Test
    public void shouldBeFastWithanyOrderBuffered() throws OWLOntologyCreationException {
        System.out.println("WebOnt_miscellaneous_wineTestCase.shouldBeFastWithanyOrder() ");
        actualrun(java8order());
    }

    public List<OWLAxiom> java7order() {
        List<OWLAxiom> axioms = new ArrayList<>();
        axioms.add(EquivalentClasses(CabernetFranc, ObjectIntersectionOf(Wine, ObjectHasValue(madeFromGrape,
            CabernetFrancGrape), ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasColor, Red)));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasBody, Medium)));
        return axioms;
    }

    public List<OWLAxiom> java8order() {
        List<OWLAxiom> axioms = new ArrayList<>();
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasColor, Red)));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasSugar, Dry)));
        axioms.add(EquivalentClasses(CabernetFranc, ObjectIntersectionOf(Wine, ObjectHasValue(madeFromGrape,
            CabernetFrancGrape), ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasBody, Medium)));
        return axioms;
    }

    private OWLDataFactory df = OWLManager.getOWLDataFactory();
    private String ns = "urn:";
    private OWLObjectProperty hasSugar = ObjectProperty(IRI.create(ns + "hasSugar"));
    private OWLObjectProperty hasBody = ObjectProperty(IRI.create(ns + "hasBody"));
    private OWLObjectProperty madeFromGrape = ObjectProperty(IRI.create(ns + "madeFromGrape"));
    private OWLObjectProperty hasColor = ObjectProperty(IRI.create(ns + "hasColor"));
    private OWLClass CabernetFranc = Class(IRI.create(ns + "CabernetFranc"));
    private OWLClass Wine = Class(IRI.create(ns + "Wine"));
    private OWLNamedIndividual Medium = NamedIndividual(IRI.create(ns + "Medium"));
    private OWLNamedIndividual Red = NamedIndividual(IRI.create(ns + "Red"));
    private OWLNamedIndividual Dry = NamedIndividual(IRI.create(ns + "Dry"));
    private OWLNamedIndividual CabernetFrancGrape = NamedIndividual(IRI.create(ns + "CabernetFrancGrape"));
}
