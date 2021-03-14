package conformancetests;

import static org.junit.Assert.assertTrue;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Rule;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.junit.Test;
import org.junit.rules.Timeout;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import testbase.TestBase;
import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

@SuppressWarnings("javadoc")
public class DebugWineTest extends TestBase {

    @Rule public Timeout timeout = new Timeout(5_000);

    protected JFactReasonerConfiguration config(OutputStream o) {
        return new JFactReasonerConfiguration().setAbsorptionLoggingActive(true).setAbsorptionLog(o);
    }

    protected JFactReasonerConfiguration noconfig() {
        return new JFactReasonerConfiguration();
    }

    @Test
    public void shouldBeFastWithOldOrder() throws OWLOntologyCreationException, IOException {
        System.out.println("WebOnt_miscellaneous_wineTestCase.shouldBeFastWithOldOrder() ");
        OWLOntology o = m.createOntology();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.out.println("DebugWine.shouldBeFastWithanyOrder() " + new HashSet<>(java7order()).size());
        OWLReasoner r = new JFactReasoner(o, new LinkedHashSet<>(java7order()), config(out), BufferingMode.BUFFERING);
        assertTrue(r.isConsistent());
        out.flush();
        String marker = "Absorption dealt with ";
        String string = out.toString();
        System.out.println("DebugWine.shouldBeFastWithOldOrder() " + string.substring(string.indexOf(marker)));
    }

    @Test
    public void shouldBeFastWithanyOrder() throws OWLOntologyCreationException, IOException {
        System.out.println("WebOnt_miscellaneous_wineTestCase.shouldBeFastWithanyOrder() ");
        OWLOntology o = m.createOntology();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.out.println("DebugWine.shouldBeFastWithanyOrder() " + new HashSet<>(java7order()).size());
        OWLReasoner r = new JFactReasoner(o, new HashSet<>(java7order()), config(out), BufferingMode.BUFFERING);
        assertTrue(r.isConsistent());
        out.flush();
        String marker = "Absorption dealt with ";
        String string = out.toString();
        System.out.println("DebugWine.shouldBeFastWithanyOrder() " + string.substring(string.indexOf(marker)));
    }

    public List<OWLAxiom> java7order() {
        List<OWLAxiom> axioms = new ArrayList<>();
        axioms.add(ObjectPropertyRange(madeFromGrape, WineGrape));
        axioms.add(DifferentIndividuals(Dry, OffDry, Sweet));
        axioms.add(DifferentIndividuals(Full, Light, Medium));
        axioms.add(DifferentIndividuals(Red, Rose, White));
        equivalentClasses(axioms);
        subclasses(axioms);
        axioms.add(InverseObjectProperties(hasMaker, producesWine));
        axioms.add(InverseObjectProperties(madeFromGrape, madeIntoWine));
        axioms.add(ObjectPropertyDomain(hasColor, Wine));
        axioms.add(ObjectPropertyDomain(madeFromGrape, Wine));
        return axioms;
    }

    private String ns = "urn:test:wine#";
    private OWLObjectProperty hasMaker = ObjectProperty(IRI.create(ns + "hasMaker"));
    private OWLObjectProperty hasFlavor = ObjectProperty(IRI.create(ns + "hasFlavor"));
    private OWLObjectProperty hasSugar = ObjectProperty(IRI.create(ns + "hasSugar"));
    private OWLObjectProperty hasBody = ObjectProperty(IRI.create(ns + "hasBody"));
    private OWLObjectProperty locatedIn = ObjectProperty(IRI.create(ns + "locatedIn"));
    private OWLObjectProperty madeFromGrape = ObjectProperty(IRI.create(ns + "madeFromGrape"));
    private OWLObjectProperty producesWine = ObjectProperty(IRI.create(ns + "producesWine"));
    private OWLObjectProperty madeIntoWine = ObjectProperty(IRI.create(ns + "madeIntoWine"));
    private OWLObjectProperty hasColor = ObjectProperty(IRI.create(ns + "hasColor"));
    private OWLClass CabernetSauvignon = Class(IRI.create(ns + "CabernetSauvignon"));
    private OWLClass WhiteWine = Class(IRI.create(ns + "WhiteWine"));
    private OWLClass WineColor = Class(IRI.create(ns + "WineColor"));
    private OWLClass WineDescriptor = Class(IRI.create(ns + "WineDescriptor"));
    private OWLClass WineSugar = Class(IRI.create(ns + "WineSugar"));
    private OWLClass WineFlavor = Class(IRI.create(ns + "WineFlavor"));
    private OWLClass WhiteBurgundy = Class(IRI.create(ns + "WhiteBurgundy"));
    private OWLClass WhiteNonSweetWine = Class(IRI.create(ns + "WhiteNonSweetWine"));
    private OWLClass WineBody = Class(IRI.create(ns + "WineBody"));
    private OWLClass Chardonnay = Class(IRI.create(ns + "Chardonnay"));
    private OWLClass FrenchWine = Class(IRI.create(ns + "FrenchWine"));
    private OWLClass NonConsumableThing = Class(IRI.create(ns + "NonConsumableThing"));
    private OWLClass CabernetFranc = Class(IRI.create(ns + "CabernetFranc"));
    private OWLClass DryWine = Class(IRI.create(ns + "DryWine"));
    private OWLClass DryWhiteWine = Class(IRI.create(ns + "DryWhiteWine"));
    private OWLClass PetiteSyrah = Class(IRI.create(ns + "PetiteSyrah"));
    private OWLClass Wine = Class(IRI.create(ns + "Wine"));
    private OWLClass PotableLiquid = Class(IRI.create(ns + "PotableLiquid"));
    private OWLClass EdibleThing = Class(IRI.create(ns + "EdibleThing"));
    private OWLClass WineGrape = Class(IRI.create(ns + "WineGrape"));
    private OWLClass Region = Class(IRI.create(ns + "Region"));
    private OWLClass ConsumableThing = Class(IRI.create(ns + "ConsumableThing"));
    private OWLClass Chianti = Class(IRI.create(ns + "Chianti"));
    private OWLClass Meal = Class(IRI.create(ns + "Meal"));
    private OWLClass Port = Class(IRI.create(ns + "Port"));
    private OWLClass LateHarvest = Class(IRI.create(ns + "LateHarvest"));
    private OWLClass Sauterne = Class(IRI.create(ns + "Sauterne"));
    private OWLClass Grape = Class(IRI.create(ns + "Grape"));
    private OWLClass WineTaste = Class(IRI.create(ns + "WineTaste"));
    private OWLClass DessertWine = Class(IRI.create(ns + "DessertWine"));
    private OWLClass EatingGrape = Class(IRI.create(ns + "EatingGrape"));
    private OWLClass EarlyHarvest = Class(IRI.create(ns + "EarlyHarvest"));
    private OWLClass OtherTomatoBasedFood = Class(IRI.create(ns + "OtherTomatoBasedFood"));
    private OWLNamedIndividual Sweet = NamedIndividual(IRI.create(ns + "Sweet"));
    private OWLNamedIndividual Rose = NamedIndividual(IRI.create(ns + "Rose"));
    private OWLNamedIndividual Light = NamedIndividual(IRI.create(ns + "Light"));
    private OWLNamedIndividual White = NamedIndividual(IRI.create(ns + "White"));
    private OWLNamedIndividual Medium = NamedIndividual(IRI.create(ns + "Medium"));
    private OWLNamedIndividual Strong = NamedIndividual(IRI.create(ns + "Strong"));
    private OWLNamedIndividual ChiantiRegion = NamedIndividual(IRI.create(ns + "ChiantiRegion"));
    private OWLNamedIndividual Red = NamedIndividual(IRI.create(ns + "Red"));
    private OWLNamedIndividual Dry = NamedIndividual(IRI.create(ns + "Dry"));
    private OWLNamedIndividual CabernetFrancGrape = NamedIndividual(IRI.create(ns + "CabernetFrancGrape"));
    private OWLNamedIndividual FrenchRegion = NamedIndividual(IRI.create(ns + "FrenchRegion"));
    private OWLNamedIndividual ChardonnayGrape = NamedIndividual(IRI.create(ns + "ChardonnayGrape"));
    private OWLNamedIndividual OffDry = NamedIndividual(IRI.create(ns + "OffDry"));
    private OWLNamedIndividual CabernetSauvignonGrape = NamedIndividual(IRI.create(ns + "CabernetSauvignonGrape"));
    private OWLNamedIndividual Full = NamedIndividual(IRI.create(ns + "Full"));

    private void subclasses(List<OWLAxiom> axioms) {
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasColor, Red)));
        axioms.add(SubClassOf(Wine, PotableLiquid));
        axioms.add(SubClassOf(PetiteSyrah, ObjectHasValue(hasColor, Red)));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(OtherTomatoBasedFood, EdibleThing));
        axioms.add(SubClassOf(Sauterne, LateHarvest));
        axioms.add(SubClassOf(DessertWine, Wine));
        axioms.add(SubClassOf(PetiteSyrah, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(DessertWine, ObjectAllValuesFrom(hasSugar, ObjectOneOf(OffDry, Sweet))));
        axioms.add(SubClassOf(EatingGrape, Grape));
        axioms.add(SubClassOf(Sauterne, ObjectHasValue(hasColor, White)));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasFlavor, df.getOWLThing())));
        axioms.add(SubClassOf(Chianti, ObjectHasValue(locatedIn, ChiantiRegion)));
        axioms.add(SubClassOf(Meal, ConsumableThing));
        axioms.add(SubClassOf(Chianti, ObjectAllValuesFrom(hasBody, ObjectOneOf(Light, Medium))));
        axioms.add(SubClassOf(PetiteSyrah, ObjectAllValuesFrom(hasBody, ObjectOneOf(Full, Medium))));
        axioms.add(SubClassOf(PotableLiquid, ConsumableThing));
        axioms.add(SubClassOf(CabernetSauvignon, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(Port, ObjectHasValue(hasFlavor, Strong)));
        axioms.add(SubClassOf(EdibleThing, ConsumableThing));
        axioms.add(SubClassOf(Port, ObjectHasValue(hasSugar, Sweet)));
        axioms.add(SubClassOf(LateHarvest, ObjectHasValue(hasSugar, Sweet)));
        axioms.add(SubClassOf(EarlyHarvest, ObjectAllValuesFrom(hasSugar, ObjectOneOf(Dry, OffDry))));
        axioms.add(SubClassOf(EarlyHarvest, Wine));
        axioms.add(SubClassOf(WineFlavor, WineTaste));
        axioms.add(SubClassOf(Port, ObjectHasValue(hasBody, Full)));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasSugar, df.getOWLThing())));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasColor, df.getOWLThing())));
        axioms.add(SubClassOf(Chardonnay, ObjectAllValuesFrom(hasBody, ObjectOneOf(Full, Medium))));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasMaker, df.getOWLThing())));
        axioms.add(SubClassOf(Chianti, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(Sauterne, ObjectHasValue(hasBody, Medium)));
        axioms.add(SubClassOf(Chardonnay, ObjectHasValue(hasColor, White)));
        axioms.add(SubClassOf(Chianti, ObjectHasValue(hasColor, Red)));
        axioms.add(SubClassOf(WineColor, WineDescriptor));
        axioms.add(SubClassOf(WineTaste, WineDescriptor));
        axioms.add(SubClassOf(WineGrape, Grape));
        axioms.add(SubClassOf(Wine, ObjectSomeValuesFrom(locatedIn, Region)));
        axioms.add(SubClassOf(WineSugar, WineTaste));
        axioms.add(SubClassOf(LateHarvest, Wine));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasBody, df.getOWLThing())));
        axioms.add(SubClassOf(Wine, ObjectMinCardinality(1, madeFromGrape, df.getOWLThing())));
        axioms.add(SubClassOf(WhiteBurgundy, ObjectHasValue(madeFromGrape, ChardonnayGrape)));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasBody, Medium)));
        axioms.add(SubClassOf(CabernetSauvignon, ObjectHasValue(hasColor, Red)));
        axioms.add(SubClassOf(WhiteBurgundy, ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing())));
        axioms.add(SubClassOf(WineBody, WineTaste));
        axioms.add(SubClassOf(CabernetSauvignon, ObjectAllValuesFrom(hasBody, ObjectOneOf(Full, Medium))));
    }

    private void equivalentClasses(List<OWLAxiom> axioms) {
        axioms.add(EquivalentClasses(CabernetFranc, ObjectIntersectionOf(Wine, ObjectHasValue(madeFromGrape,
            CabernetFrancGrape), ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        axioms.add(EquivalentClasses(DryWine, ObjectIntersectionOf(Wine, ObjectHasValue(hasSugar, Dry))));
        axioms.add(EquivalentClasses(DryWhiteWine, ObjectIntersectionOf(DryWine, WhiteWine)));
        axioms.add(EquivalentClasses(CabernetSauvignon, ObjectIntersectionOf(Wine, ObjectHasValue(madeFromGrape,
            CabernetSauvignonGrape), ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        axioms.add(EquivalentClasses(WhiteWine, ObjectIntersectionOf(Wine, ObjectHasValue(hasColor, White))));
        axioms.add(EquivalentClasses(WineColor, ObjectOneOf(Red, Rose, White)));
        axioms.add(EquivalentClasses(WineSugar, ObjectOneOf(Dry, OffDry, Sweet)));
        axioms.add(EquivalentClasses(WineDescriptor, ObjectUnionOf(WineColor, WineTaste)));
        axioms.add(EquivalentClasses(WhiteNonSweetWine, ObjectIntersectionOf(WhiteWine, ObjectAllValuesFrom(hasSugar,
            ObjectOneOf(Dry, OffDry)))));
        axioms.add(EquivalentClasses(WineBody, ObjectOneOf(Full, Light, Medium)));
        axioms.add(EquivalentClasses(Chardonnay, ObjectIntersectionOf(Wine, ObjectHasValue(madeFromGrape,
            ChardonnayGrape), ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        axioms.add(EquivalentClasses(FrenchWine, ObjectIntersectionOf(Wine, ObjectHasValue(locatedIn, FrenchRegion))));
        axioms.add(EquivalentClasses(NonConsumableThing, ObjectComplementOf(ConsumableThing)));
    }
}
