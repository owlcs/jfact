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

import org.junit.Ignore;
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
@Ignore
public class DebugWine2 extends TestBase {

    @Rule public Timeout timeout = new Timeout(15000);

    protected JFactReasonerConfiguration config(OutputStream o) {
        return new JFactReasonerConfiguration().setAbsorptionLoggingActive(true).setAbsorptionLog(o);
    }

    protected JFactReasonerConfiguration noconfig() {
        return new JFactReasonerConfiguration();
    }

    @Ignore
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

    @Ignore
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
        // classAssertions(axioms);
        axioms.add(ObjectPropertyRange(madeFromGrape, WineGrape));
        axioms.add(DifferentIndividuals(Dry, OffDry, Sweet));
        // axioms.add(DifferentIndividuals(Delicate, Moderate, Strong));
        axioms.add(DifferentIndividuals(Full, Light, Medium));
        axioms.add(DifferentIndividuals(Red, Rose, White));
        equivalentClasses(axioms);
        disjointClasses(axioms);
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
    // private OWLObjectProperty madeFromFruit = ObjectProperty(IRI.create(ns +
    // "madeFromFruit"));
    private OWLObjectProperty madeFromGrape = ObjectProperty(IRI.create(ns + "madeFromGrape"));
    // private OWLObjectProperty course = ObjectProperty(IRI.create(ns +
    // "course"));
    private OWLObjectProperty producesWine = ObjectProperty(IRI.create(ns + "producesWine"));
    private OWLObjectProperty madeIntoWine = ObjectProperty(IRI.create(ns + "madeIntoWine"));
    // private OWLObjectProperty hasFood = ObjectProperty(IRI.create(ns +
    // "hasFood"));
    private OWLObjectProperty hasColor = ObjectProperty(IRI.create(ns + "hasColor"));
    // private OWLObjectProperty hasDrink = ObjectProperty(IRI.create(ns +
    // "hasDrink"));
    // private OWLObjectProperty hasVintageYear = ObjectProperty(IRI.create(ns +
    // "hasVintageYear"));
    // private OWLClass Pauillac = Class(IRI.create(ns + "Pauillac"));
    // private OWLClass CaliforniaWine = Class(IRI.create(ns +
    // "CaliforniaWine"));
    // private OWLClass SweetDessertCourse = Class(IRI.create(ns +
    // "SweetDessertCourse"));
    // private OWLClass DessertCourse = Class(IRI.create(ns + "DessertCourse"));
    // private OWLClass Margaux = Class(IRI.create(ns + "Margaux"));
    // private OWLClass ShellfishCourse = Class(IRI.create(ns +
    // "ShellfishCourse"));
    // private OWLClass PinotBlanc = Class(IRI.create(ns + "PinotBlanc"));
    // private OWLClass CotesDOr = Class(IRI.create(ns + "CotesDOr"));
    // private OWLClass CheninBlanc = Class(IRI.create(ns + "CheninBlanc"));
    // private OWLClass OtherTomatoBasedFoodCourse = Class(IRI.create(ns +
    // "OtherTomatoBasedFoodCourse"));
    // private OWLClass SweetWine = Class(IRI.create(ns + "SweetWine"));
    // private OWLClass Anjou = Class(IRI.create(ns + "Anjou"));
    // private OWLClass DryRedWine = Class(IRI.create(ns + "DryRedWine"));
    // private OWLClass WhiteLoire = Class(IRI.create(ns + "WhiteLoire"));
    // private OWLClass Meursault = Class(IRI.create(ns + "Meursault"));
    // private OWLClass RoseWine = Class(IRI.create(ns + "RoseWine"));
    // private OWLClass FullBodiedWine = Class(IRI.create(ns +
    // "FullBodiedWine"));
    // private OWLClass AlsatianWine = Class(IRI.create(ns + "AlsatianWine"));
    // private OWLClass TableWine = Class(IRI.create(ns + "TableWine"));
    // private OWLClass GermanWine = Class(IRI.create(ns + "GermanWine"));
    private OWLClass CabernetSauvignon = Class(IRI.create(ns + "CabernetSauvignon"));
    // private OWLClass NonOysterShellfishCourse = Class(IRI.create(ns +
    // "NonOysterShellfishCourse"));
    private OWLClass WhiteWine = Class(IRI.create(ns + "WhiteWine"));
    // private OWLClass RedMeatCourse = Class(IRI.create(ns + "RedMeatCourse"));
    // private OWLClass RedTableWine = Class(IRI.create(ns + "RedTableWine"));
    // private OWLClass Burgundy = Class(IRI.create(ns + "Burgundy"));
    // private OWLClass SeafoodCourse = Class(IRI.create(ns + "SeafoodCourse"));
    // private OWLClass Seafood = Class(IRI.create(ns + "Seafood"));
    // private OWLClass NonBlandFishCourse = Class(IRI.create(ns +
    // "NonBlandFishCourse"));
    private OWLClass WineColor = Class(IRI.create(ns + "WineColor"));
    private OWLClass WineDescriptor = Class(IRI.create(ns + "WineDescriptor"));
    // private OWLClass SemillonOrSauvignonBlanc = Class(IRI.create(ns +
    // "SemillonOrSauvignonBlanc"));
    // private OWLClass StEmilion = Class(IRI.create(ns + "StEmilion"));
    private OWLClass WineSugar = Class(IRI.create(ns + "WineSugar"));
    // private OWLClass Tours = Class(IRI.create(ns + "Tours"));
    // private OWLClass IceWine = Class(IRI.create(ns + "IceWine"));
    // private OWLClass Medoc = Class(IRI.create(ns + "Medoc"));
    // private OWLClass SweetRiesling = Class(IRI.create(ns + "SweetRiesling"));
    private OWLClass WineFlavor = Class(IRI.create(ns + "WineFlavor"));
    // private OWLClass RedBurgundy = Class(IRI.create(ns + "RedBurgundy"));
    // private OWLClass ItalianWine = Class(IRI.create(ns + "ItalianWine"));
    private OWLClass WhiteBurgundy = Class(IRI.create(ns + "WhiteBurgundy"));
    // private OWLClass PinotNoir = Class(IRI.create(ns + "PinotNoir"));
    // private OWLClass LightMeatFowlCourse = Class(IRI.create(ns +
    // "LightMeatFowlCourse"));
    // private OWLClass NonSweetFruitCourse = Class(IRI.create(ns +
    // "NonSweetFruitCourse"));
    // private OWLClass Zinfandel = Class(IRI.create(ns + "Zinfandel"));
    private OWLClass WhiteNonSweetWine = Class(IRI.create(ns + "WhiteNonSweetWine"));
    // private OWLClass SpicyRedMeatCourse = Class(IRI.create(ns +
    // "SpicyRedMeatCourse"));
    // private OWLClass DryRiesling = Class(IRI.create(ns + "DryRiesling"));
    private OWLClass WineBody = Class(IRI.create(ns + "WineBody"));
    // private OWLClass NonRedMeatCourse = Class(IRI.create(ns +
    // "NonRedMeatCourse"));
    // private OWLClass NonSpicyRedMeatCourse = Class(IRI.create(ns +
    // "NonSpicyRedMeatCourse"));
    // private OWLClass Muscadet = Class(IRI.create(ns + "Muscadet"));
    // private OWLClass Beaujolais = Class(IRI.create(ns + "Beaujolais"));
    // private OWLClass SauvignonBlanc = Class(IRI.create(ns +
    // "SauvignonBlanc"));
    // private OWLClass AmericanWine = Class(IRI.create(ns + "AmericanWine"));
    // private OWLClass OysterShellfishCourse = Class(IRI.create(ns +
    // "OysterShellfishCourse"));
    private OWLClass Chardonnay = Class(IRI.create(ns + "Chardonnay"));
    private OWLClass FrenchWine = Class(IRI.create(ns + "FrenchWine"));
    private OWLClass NonConsumableThing = Class(IRI.create(ns + "NonConsumableThing"));
    // private OWLClass WhiteTableWine = Class(IRI.create(ns +
    // "WhiteTableWine"));
    // private OWLClass Meritage = Class(IRI.create(ns + "Meritage"));
    // private OWLClass Riesling = Class(IRI.create(ns + "Riesling"));
    // private OWLClass RedBordeaux = Class(IRI.create(ns + "RedBordeaux"));
    // private OWLClass RedWine = Class(IRI.create(ns + "RedWine"));
    // private OWLClass FruitCourse = Class(IRI.create(ns + "FruitCourse"));
    private OWLClass CabernetFranc = Class(IRI.create(ns + "CabernetFranc"));
    // private OWLClass Gamay = Class(IRI.create(ns + "Gamay"));
    // private OWLClass Fruit = Class(IRI.create(ns + "Fruit"));
    // private OWLClass Merlot = Class(IRI.create(ns + "Merlot"));
    // private OWLClass CheeseNutsDessertCourse = Class(IRI.create(ns +
    // "CheeseNutsDessertCourse"));
    // private OWLClass Bordeaux = Class(IRI.create(ns + "Bordeaux"));
    private OWLClass DryWine = Class(IRI.create(ns + "DryWine"));
    // private OWLClass WhiteBordeaux = Class(IRI.create(ns + "WhiteBordeaux"));
    private OWLClass DryWhiteWine = Class(IRI.create(ns + "DryWhiteWine"));
    // private OWLClass Sancerre = Class(IRI.create(ns + "Sancerre"));
    // private OWLClass FishCourse = Class(IRI.create(ns + "FishCourse"));
    // private OWLClass TexasWine = Class(IRI.create(ns + "TexasWine"));
    // private OWLClass SweetFruitCourse = Class(IRI.create(ns +
    // "SweetFruitCourse"));
    // private OWLClass Loire = Class(IRI.create(ns + "Loire"));
    // private OWLClass Semillon = Class(IRI.create(ns + "Semillon"));
    // private OWLClass DarkMeatFowlCourse = Class(IRI.create(ns +
    // "DarkMeatFowlCourse"));
    private OWLClass PetiteSyrah = Class(IRI.create(ns + "PetiteSyrah"));
    // private OWLClass BlandFishCourse = Class(IRI.create(ns +
    // "BlandFishCourse"));
    private OWLClass Wine = Class(IRI.create(ns + "Wine"));
    private OWLClass PotableLiquid = Class(IRI.create(ns + "PotableLiquid"));
    // private OWLClass MealCourse = Class(IRI.create(ns + "MealCourse"));
    private OWLClass EdibleThing = Class(IRI.create(ns + "EdibleThing"));
    private OWLClass WineGrape = Class(IRI.create(ns + "WineGrape"));
    private OWLClass Region = Class(IRI.create(ns + "Region"));
    // private OWLClass SweetDessert = Class(IRI.create(ns + "SweetDessert"));
    // private OWLClass Dessert = Class(IRI.create(ns + "Dessert"));
    // private OWLClass Shellfish = Class(IRI.create(ns + "Shellfish"));
    // private OWLClass Fish = Class(IRI.create(ns + "Fish"));
    private OWLClass ConsumableThing = Class(IRI.create(ns + "ConsumableThing"));
    // private OWLClass Juice = Class(IRI.create(ns + "Juice"));
    // private OWLClass BlandFish = Class(IRI.create(ns + "BlandFish"));
    private OWLClass Chianti = Class(IRI.create(ns + "Chianti"));
    private OWLClass Meal = Class(IRI.create(ns + "Meal"));
    // private OWLClass NonSpicyRedMeat = Class(IRI.create(ns +
    // "NonSpicyRedMeat"));
    // private OWLClass OysterShellfish = Class(IRI.create(ns +
    // "OysterShellfish"));
    // private OWLClass DarkMeatFowl = Class(IRI.create(ns + "DarkMeatFowl"));
    // private OWLClass NonSweetFruit = Class(IRI.create(ns + "NonSweetFruit"));
    // private OWLClass NonRedMeat = Class(IRI.create(ns + "NonRedMeat"));
    // private OWLClass SpicyRedMeat = Class(IRI.create(ns + "SpicyRedMeat"));
    private OWLClass Port = Class(IRI.create(ns + "Port"));
    // private OWLClass Vintage = Class(IRI.create(ns + "Vintage"));
    // private OWLClass LightMeatFowl = Class(IRI.create(ns + "LightMeatFowl"));
    // private OWLClass Meat = Class(IRI.create(ns + "Meat"));
    // private OWLClass RedMeat = Class(IRI.create(ns + "RedMeat"));
    // private OWLClass NonBlandFish = Class(IRI.create(ns + "NonBlandFish"));
    private OWLClass LateHarvest = Class(IRI.create(ns + "LateHarvest"));
    // private OWLClass NonOysterShellfish = Class(IRI.create(ns +
    // "NonOysterShellfish"));
    private OWLClass Sauterne = Class(IRI.create(ns + "Sauterne"));
    private OWLClass Grape = Class(IRI.create(ns + "Grape"));
    private OWLClass WineTaste = Class(IRI.create(ns + "WineTaste"));
    private OWLClass DessertWine = Class(IRI.create(ns + "DessertWine"));
    private OWLClass EatingGrape = Class(IRI.create(ns + "EatingGrape"));
    // private OWLClass SweetFruit = Class(IRI.create(ns + "SweetFruit"));
    // private OWLClass CheeseNutsDessert = Class(IRI.create(ns +
    // "CheeseNutsDessert"));
    private OWLClass EarlyHarvest = Class(IRI.create(ns + "EarlyHarvest"));
    private OWLClass OtherTomatoBasedFood = Class(IRI.create(ns + "OtherTomatoBasedFood"));
    // private OWLClass Fowl = Class(IRI.create(ns + "Fowl"));
    // private OWLClass Winery = Class(IRI.create(ns + "Winery"));
    // private OWLNamedIndividual PauillacRegion = NamedIndividual(IRI.create(ns
    // + "PauillacRegion"));
    // private OWLNamedIndividual CaliforniaRegion =
    // NamedIndividual(IRI.create(ns + "CaliforniaRegion"));
    private OWLNamedIndividual Sweet = NamedIndividual(IRI.create(ns + "Sweet"));
    // private OWLNamedIndividual MargauxRegion = NamedIndividual(IRI.create(ns
    // + "MargauxRegion"));
    // private OWLNamedIndividual PinotBlancGrape =
    // NamedIndividual(IRI.create(ns + "PinotBlancGrape"));
    // private OWLNamedIndividual CotesDOrRegion = NamedIndividual(IRI.create(ns
    // + "CotesDOrRegion"));
    // private OWLNamedIndividual CheninBlancGrape =
    // NamedIndividual(IRI.create(ns + "CheninBlancGrape"));
    // private OWLNamedIndividual AnjouRegion = NamedIndividual(IRI.create(ns +
    // "AnjouRegion"));
    private OWLNamedIndividual Rose = NamedIndividual(IRI.create(ns + "Rose"));
    // private OWLNamedIndividual PinotNoirGrape = NamedIndividual(IRI.create(ns
    // + "PinotNoirGrape"));
    // private OWLNamedIndividual GamayGrape = NamedIndividual(IRI.create(ns +
    // "GamayGrape"));
    private OWLNamedIndividual Light = NamedIndividual(IRI.create(ns + "Light"));
    private OWLNamedIndividual White = NamedIndividual(IRI.create(ns + "White"));
    // private OWLNamedIndividual PetiteVerdotGrape =
    // NamedIndividual(IRI.create(ns + "PetiteVerdotGrape"));
    private OWLNamedIndividual Medium = NamedIndividual(IRI.create(ns + "Medium"));
    // private OWLNamedIndividual StEmilionRegion =
    // NamedIndividual(IRI.create(ns + "StEmilionRegion"));
    private OWLNamedIndividual Strong = NamedIndividual(IRI.create(ns + "Strong"));
    // private OWLNamedIndividual BordeauxRegion = NamedIndividual(IRI.create(ns
    // + "BordeauxRegion"));
    // private OWLNamedIndividual SancerreRegion = NamedIndividual(IRI.create(ns
    // + "SancerreRegion"));
    private OWLNamedIndividual ChiantiRegion = NamedIndividual(IRI.create(ns + "ChiantiRegion"));
    private OWLNamedIndividual Red = NamedIndividual(IRI.create(ns + "Red"));
    // private OWLNamedIndividual USRegion = NamedIndividual(IRI.create(ns +
    // "USRegion"));
    // private OWLNamedIndividual MalbecGrape = NamedIndividual(IRI.create(ns +
    // "MalbecGrape"));
    private OWLNamedIndividual Dry = NamedIndividual(IRI.create(ns + "Dry"));
    private OWLNamedIndividual CabernetFrancGrape = NamedIndividual(IRI.create(ns + "CabernetFrancGrape"));
    // private OWLNamedIndividual Delicate = NamedIndividual(IRI.create(ns +
    // "Delicate"));
    // private OWLNamedIndividual MeursaultRegion =
    // NamedIndividual(IRI.create(ns + "MeursaultRegion"));
    // private OWLNamedIndividual PortugalRegion = NamedIndividual(IRI.create(ns
    // + "PortugalRegion"));
    // private OWLNamedIndividual BourgogneRegion =
    // NamedIndividual(IRI.create(ns + "BourgogneRegion"));
    // private OWLNamedIndividual MerlotGrape = NamedIndividual(IRI.create(ns +
    // "MerlotGrape"));
    // private OWLNamedIndividual LoireRegion = NamedIndividual(IRI.create(ns +
    // "LoireRegion"));
    private OWLNamedIndividual FrenchRegion = NamedIndividual(IRI.create(ns + "FrenchRegion"));
    // private OWLNamedIndividual MuscadetRegion = NamedIndividual(IRI.create(ns
    // + "MuscadetRegion"));
    private OWLNamedIndividual ChardonnayGrape = NamedIndividual(IRI.create(ns + "ChardonnayGrape"));
    // private OWLNamedIndividual SauvignonBlancGrape =
    // NamedIndividual(IRI.create(ns + "SauvignonBlancGrape"));
    // private OWLNamedIndividual BeaujolaisRegion =
    // NamedIndividual(IRI.create(ns + "BeaujolaisRegion"));
    // private OWLNamedIndividual PetiteSyrahGrape =
    // NamedIndividual(IRI.create(ns + "PetiteSyrahGrape"));
    // private OWLNamedIndividual TexasRegion = NamedIndividual(IRI.create(ns +
    // "TexasRegion"));
    // private OWLNamedIndividual SemillonGrape = NamedIndividual(IRI.create(ns
    // + "SemillonGrape"));
    // private OWLNamedIndividual SangioveseGrape =
    // NamedIndividual(IRI.create(ns + "SangioveseGrape"));
    // private OWLNamedIndividual RieslingGrape = NamedIndividual(IRI.create(ns
    // + "RieslingGrape"));
    private OWLNamedIndividual OffDry = NamedIndividual(IRI.create(ns + "OffDry"));
    private OWLNamedIndividual CabernetSauvignonGrape = NamedIndividual(IRI.create(ns + "CabernetSauvignonGrape"));
    private OWLNamedIndividual Full = NamedIndividual(IRI.create(ns + "Full"));
    // private OWLNamedIndividual ZinfandelGrape = NamedIndividual(IRI.create(ns
    // + "ZinfandelGrape"));
    // private OWLNamedIndividual MedocRegion = NamedIndividual(IRI.create(ns +
    // "MedocRegion"));
    // private OWLNamedIndividual ItalianRegion = NamedIndividual(IRI.create(ns
    // + "ItalianRegion"));
    // private OWLNamedIndividual GermanyRegion = NamedIndividual(IRI.create(ns
    // + "GermanyRegion"));
    // private OWLNamedIndividual ToursRegion = NamedIndividual(IRI.create(ns +
    // "ToursRegion"));
    // private OWLNamedIndividual Moderate = NamedIndividual(IRI.create(ns +
    // "Moderate"));
    // private OWLNamedIndividual AlsaceRegion = NamedIndividual(IRI.create(ns +
    // "AlsaceRegion"));
    // private OWLNamedIndividual SauterneRegion = NamedIndividual(IRI.create(ns
    // + "SauterneRegion"));

    private void disjointClasses(List<OWLAxiom> axioms) {
        // axioms.add(DisjointClasses(MealCourse, PotableLiquid));
        // axioms.add(DisjointClasses(Fish, Shellfish));
        // axioms.add(DisjointClasses(DarkMeatFowl, LightMeatFowl));
        // axioms.add(DisjointClasses(CheeseNutsDessert, SweetDessert));
        // axioms.add(DisjointClasses(EarlyHarvest, LateHarvest));
        // axioms.add(DisjointClasses(EdibleThing, PotableLiquid));
        // axioms.add(DisjointClasses(Meat, Seafood));
        // axioms.add(DisjointClasses(EdibleThing, Meal));
        // axioms.add(DisjointClasses(NonOysterShellfish, OysterShellfish));
        // axioms.add(DisjointClasses(Dessert, Fruit));
        // axioms.add(DisjointClasses(NonRedMeat, RedMeat));
        // axioms.add(DisjointClasses(Fruit, OtherTomatoBasedFood));
        // axioms.add(DisjointClasses(Dessert, OtherTomatoBasedFood));
        // axioms.add(DisjointClasses(Fruit, Seafood));
        // axioms.add(DisjointClasses(NonSpicyRedMeat, SpicyRedMeat));
        // axioms.add(DisjointClasses(Meal, PotableLiquid));
        // axioms.add(DisjointClasses(Fruit, Meat));
        // axioms.add(DisjointClasses(Fowl, Meat));
        // axioms.add(DisjointClasses(OtherTomatoBasedFood, Seafood));
        // axioms.add(DisjointClasses(Dessert, Seafood));
        // axioms.add(DisjointClasses(Fowl, OtherTomatoBasedFood));
        // axioms.add(DisjointClasses(EdibleThing, MealCourse));
        // axioms.add(DisjointClasses(Fowl, Seafood));
        // axioms.add(DisjointClasses(Meal, MealCourse));
        // axioms.add(DisjointClasses(Fowl, Fruit));
        // axioms.add(DisjointClasses(Meat, OtherTomatoBasedFood));
        // axioms.add(DisjointClasses(Dessert, Fowl));
        // axioms.add(DisjointClasses(BlandFish, NonBlandFish));
        // axioms.add(DisjointClasses(NonSweetFruit, SweetFruit));
        // axioms.add(DisjointClasses(Dessert, Meat));
    }

    private void subclasses(List<OWLAxiom> axioms) {
        // axioms.add(SubClassOf(Meal, ObjectAllValuesFrom(course,
        // MealCourse)));
        // axioms.add(SubClassOf(Port, ObjectHasValue(locatedIn,
        // PortugalRegion)));
        // axioms.add(SubClassOf(CheeseNutsDessertCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasColor, Red))));
        // axioms.add(SubClassOf(Margaux, ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing())));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(LightMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar, Dry))));
        // axioms.add(SubClassOf(Sauterne, ObjectHasValue(locatedIn,
        // SauterneRegion)));
        // axioms.add(SubClassOf(Tours, ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing())));
        // axioms.add(SubClassOf(Grape, SweetFruit));
        // axioms.add(SubClassOf(RedBurgundy, ObjectMaxCardinality(1,
        // madeFromGrape, df.getOWLThing())));
        // axioms.add(SubClassOf(LateHarvest, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Moderate, Strong))));
        axioms.add(SubClassOf(Wine, PotableLiquid));
        axioms.add(SubClassOf(PetiteSyrah, ObjectHasValue(hasColor, Red)));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasSugar, Dry)));
        // axioms.add(SubClassOf(CheninBlanc, ObjectHasValue(hasFlavor,
        // Moderate)));
        axioms.add(SubClassOf(OtherTomatoBasedFood, EdibleThing));
        // axioms.add(SubClassOf(PinotBlanc, ObjectHasValue(hasColor, White)));
        axioms.add(SubClassOf(Sauterne, LateHarvest));
        // axioms.add(SubClassOf(NonSpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasColor, Red))));
        // axioms.add(SubClassOf(DessertCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasSugar, Sweet))));
        axioms.add(SubClassOf(DessertWine, Wine));
        // axioms.add(SubClassOf(Zinfandel, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Moderate, Strong))));
        axioms.add(SubClassOf(PetiteSyrah, ObjectHasValue(hasSugar, Dry)));
        // axioms.add(SubClassOf(OysterShellfish, Shellfish));
        // axioms.add(SubClassOf(SweetRiesling, ObjectHasValue(hasBody, Full)));
        // axioms.add(SubClassOf(SweetFruitCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasFlavor, Moderate))));
        // axioms.add(SubClassOf(StEmilion, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(Zinfandel, ObjectHasValue(hasSugar, Dry)));
        // axioms.add(SubClassOf(StEmilion, ObjectMaxCardinality(1,
        // madeFromGrape, df.getOWLThing())));
        // axioms.add(SubClassOf(NonSpicyRedMeat, RedMeat));
        // axioms.add(SubClassOf(RedMeatCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasColor, Red))));
        // axioms.add(SubClassOf(SpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasFlavor, Moderate))));
        axioms.add(SubClassOf(DessertWine, ObjectAllValuesFrom(hasSugar, ObjectOneOf(OffDry, Sweet))));
        // axioms.add(SubClassOf(RedBurgundy, ObjectHasValue(madeFromGrape,
        // PinotNoirGrape)));
        // axioms.add(SubClassOf(Sancerre, ObjectHasValue(hasFlavor,
        // Delicate)));
        // axioms.add(SubClassOf(Anjou, ObjectHasValue(hasFlavor, Delicate)));
        // axioms.add(SubClassOf(DarkMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasFlavor, Delicate))));
        // axioms.add(SubClassOf(Meal, ObjectMinCardinality(1, course,
        // df.getOWLThing())));
        axioms.add(SubClassOf(EatingGrape, Grape));
        // axioms.add(SubClassOf(IceWine, ObjectAllValuesFrom(hasBody,
        // ObjectOneOf(Full, Medium))));
        // axioms.add(SubClassOf(DarkMeatFowl, Fowl));
        axioms.add(SubClassOf(Sauterne, ObjectHasValue(hasColor, White)));
        // axioms.add(SubClassOf(MealCourse, ObjectMinCardinality(1, hasDrink,
        // df.getOWLThing())));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasFlavor, df.getOWLThing())));
        // axioms.add(SubClassOf(NonOysterShellfishCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar, Dry))));
        // axioms.add(SubClassOf(Zinfandel, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(DessertCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasFlavor, Strong))));
        // axioms.add(SubClassOf(MealCourse, ConsumableThing));
        // axioms.add(SubClassOf(Fowl, EdibleThing));
        // axioms.add(SubClassOf(Chardonnay, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Moderate, Strong))));
        // axioms.add(SubClassOf(Muscadet, ObjectHasValue(hasFlavor,
        // Delicate)));
        // axioms.add(SubClassOf(OtherTomatoBasedFoodCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar,
        // Dry))));
        // axioms.add(SubClassOf(Tours, ObjectHasValue(madeFromGrape,
        // CheninBlancGrape)));
        // axioms.add(SubClassOf(CheeseNutsDessert, Dessert));
        // axioms.add(SubClassOf(CheninBlanc, ObjectAllValuesFrom(hasSugar,
        // ObjectOneOf(Dry, OffDry))));
        // axioms.add(SubClassOf(CotesDOr, ObjectHasValue(hasFlavor,
        // Moderate)));
        // axioms.add(SubClassOf(NonRedMeatCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasBody, Medium))));
        // axioms.add(SubClassOf(SemillonOrSauvignonBlanc,
        // ObjectHasValue(hasColor, White)));
        // axioms.add(SubClassOf(CheninBlanc, ObjectHasValue(hasColor, White)));
        // axioms.add(SubClassOf(Merlot, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(FishCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasSugar, Dry))));
        // axioms.add(SubClassOf(NonBlandFishCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasFlavor, Moderate))));
        axioms.add(SubClassOf(Chianti, ObjectHasValue(locatedIn, ChiantiRegion)));
        // axioms.add(SubClassOf(Medoc, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(Meal, ConsumableThing));
        // axioms.add(SubClassOf(SpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasColor, Red))));
        // axioms.add(SubClassOf(DessertCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasBody, Full))));
        // axioms.add(SubClassOf(Sancerre, ObjectHasValue(madeFromGrape,
        // SauvignonBlancGrape)));
        // axioms.add(SubClassOf(OtherTomatoBasedFoodCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasColor,
        // Red))));
        // axioms.add(SubClassOf(SpicyRedMeat, RedMeat));
        // axioms.add(SubClassOf(Sancerre, ObjectMaxCardinality(1,
        // madeFromGrape, df.getOWLThing())));
        axioms.add(SubClassOf(Chianti, ObjectAllValuesFrom(hasBody, ObjectOneOf(Light, Medium))));
        // axioms.add(SubClassOf(SpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar, Dry))));
        // axioms.add(SubClassOf(Pauillac, ObjectHasValue(hasFlavor, Strong)));
        // axioms.add(SubClassOf(NonRedMeatCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasSugar, Dry))));
        // axioms.add(SubClassOf(Muscadet, ObjectMaxCardinality(1,
        // madeFromGrape, df.getOWLThing())));
        // axioms.add(SubClassOf(Anjou, ObjectHasValue(hasBody, Light)));
        // axioms.add(SubClassOf(NonSpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar, Dry))));
        // axioms.add(SubClassOf(DarkMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasBody, Light))));
        // axioms.add(SubClassOf(BlandFish, Fish));
        // axioms.add(SubClassOf(Anjou, ObjectHasValue(hasColor, Rose)));
        // axioms.add(SubClassOf(Dessert, EdibleThing));
        // axioms.add(SubClassOf(Muscadet, ObjectHasValue(hasSugar, Dry)));
        // axioms.add(SubClassOf(DarkMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasColor, Red))));
        // axioms.add(SubClassOf(StEmilion, ObjectHasValue(madeFromGrape,
        // CabernetSauvignonGrape)));
        // axioms.add(SubClassOf(PetiteSyrah, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Moderate, Strong))));
        // axioms.add(SubClassOf(DryRiesling, ObjectHasValue(hasColor, White)));
        // axioms.add(SubClassOf(Beaujolais, ObjectHasValue(hasFlavor,
        // Delicate)));
        axioms.add(SubClassOf(PetiteSyrah, ObjectAllValuesFrom(hasBody, ObjectOneOf(Full, Medium))));
        // axioms.add(SubClassOf(Muscadet, ObjectHasValue(hasBody, Light)));
        // axioms.add(SubClassOf(FishCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasBody, Medium))));
        axioms.add(SubClassOf(PotableLiquid, ConsumableThing));
        axioms.add(SubClassOf(CabernetSauvignon, ObjectHasValue(hasSugar, Dry)));
        // axioms.add(SubClassOf(DarkMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar, Dry))));
        // axioms.add(SubClassOf(Zinfandel, ObjectAllValuesFrom(hasBody,
        // ObjectOneOf(Full, Medium))));
        // axioms.add(SubClassOf(NonSpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Moderate, Strong)))));
        axioms.add(SubClassOf(Port, ObjectHasValue(hasFlavor, Strong)));
        // axioms.add(SubClassOf(Sancerre, ObjectHasValue(hasSugar, OffDry)));
        axioms.add(SubClassOf(EdibleThing, ConsumableThing));
        // axioms.add(SubClassOf(Seafood, EdibleThing));
        axioms.add(SubClassOf(Port, ObjectHasValue(hasSugar, Sweet)));
        // axioms.add(SubClassOf(Chianti, ObjectHasValue(hasFlavor, Moderate)));
        // axioms.add(SubClassOf(Wine, ObjectAllValuesFrom(hasMaker, Winery)));
        axioms.add(SubClassOf(LateHarvest, ObjectHasValue(hasSugar, Sweet)));
        // axioms.add(SubClassOf(Margaux, ObjectHasValue(madeFromGrape,
        // MerlotGrape)));
        // axioms.add(SubClassOf(FruitCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasColor, White))));
        axioms.add(SubClassOf(EarlyHarvest, ObjectAllValuesFrom(hasSugar, ObjectOneOf(Dry, OffDry))));
        axioms.add(SubClassOf(EarlyHarvest, Wine));
        // axioms.add(SubClassOf(SweetRiesling, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Moderate, Strong))));
        // axioms.add(SubClassOf(NonSweetFruitCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar, OffDry))));
        // axioms.add(SubClassOf(SemillonOrSauvignonBlanc,
        // ObjectAllValuesFrom(hasBody, ObjectOneOf(Full, Medium))));
        // axioms.add(SubClassOf(SweetFruit, EdibleThing));
        axioms.add(SubClassOf(WineFlavor, WineTaste));
        // axioms.add(SubClassOf(LightMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasFlavor, Moderate))));
        // axioms.add(SubClassOf(WhiteLoire, ObjectAllValuesFrom(madeFromGrape,
        // ObjectOneOf(CheninBlancGrape,
        // PinotBlancGrape, SauvignonBlancGrape))));
        // axioms.add(SubClassOf(Beaujolais, ObjectMaxCardinality(1,
        // madeFromGrape, df.getOWLThing())));
        axioms.add(SubClassOf(Port, ObjectHasValue(hasBody, Full)));
        // axioms.add(SubClassOf(SweetRiesling, DessertWine));
        // axioms.add(SubClassOf(NonBlandFish, Fish));
        // axioms.add(SubClassOf(NonRedMeatCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasColor, White))));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasSugar, df.getOWLThing())));
        // axioms.add(SubClassOf(Chianti, ItalianWine));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasColor, df.getOWLThing())));
        // axioms.add(SubClassOf(OtherTomatoBasedFoodCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasFlavor,
        // Moderate))));
        // axioms.add(SubClassOf(Beaujolais, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(Chardonnay, ObjectAllValuesFrom(hasBody, ObjectOneOf(Full, Medium))));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasMaker, df.getOWLThing())));
        // axioms.add(SubClassOf(Port, RedWine));
        // axioms.add(SubClassOf(LightMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasColor, White))));
        // axioms.add(SubClassOf(Beaujolais, ObjectHasValue(hasBody, Light)));
        // axioms.add(SubClassOf(RedMeat, Meat));
        axioms.add(SubClassOf(Chianti, ObjectHasValue(hasSugar, Dry)));
        axioms.add(SubClassOf(Sauterne, ObjectHasValue(hasBody, Medium)));
        // axioms.add(SubClassOf(MealCourse, ObjectAllValuesFrom(hasDrink,
        // Wine)));
        // axioms.add(SubClassOf(Pauillac, ObjectMaxCardinality(1,
        // madeFromGrape, df.getOWLThing())));
        // axioms.add(SubClassOf(DryRiesling, ObjectAllValuesFrom(hasBody,
        // ObjectOneOf(Light, Medium))));
        // axioms.add(SubClassOf(Beaujolais, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(Muscadet, ObjectHasValue(madeFromGrape,
        // PinotBlancGrape)));
        // axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasFlavor,
        // Moderate)));
        // axioms.add(SubClassOf(NonOysterShellfish, Shellfish));
        // axioms.add(SubClassOf(Pauillac, ObjectHasValue(hasBody, Full)));
        axioms.add(SubClassOf(Chardonnay, ObjectHasValue(hasColor, White)));
        axioms.add(SubClassOf(Chianti, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(PinotNoir, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(Juice, PotableLiquid));
        // axioms.add(SubClassOf(LightMeatFowl, Fowl));
        // axioms.add(SubClassOf(Meursault, ObjectHasValue(hasBody, Full)));
        // axioms.add(SubClassOf(FruitCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasBody, Medium))));
        // axioms.add(SubClassOf(Beaujolais, ObjectHasValue(madeFromGrape,
        // GamayGrape)));
        axioms.add(SubClassOf(WineColor, WineDescriptor));
        axioms.add(SubClassOf(WineTaste, WineDescriptor));
        axioms.add(SubClassOf(WineGrape, Grape));
        // axioms.add(SubClassOf(CabernetSauvignon,
        // ObjectAllValuesFrom(hasFlavor, ObjectOneOf(Moderate, Strong))));
        // axioms.add(SubClassOf(OysterShellfishCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasSugar, Sweet))));
        // axioms.add(SubClassOf(SweetDessert, Dessert));
        // axioms.add(SubClassOf(NonRedMeatCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasFlavor, Strong))));
        // axioms.add(SubClassOf(MealCourse, ObjectMinCardinality(1, hasFood,
        // df.getOWLThing())));
        // axioms.add(SubClassOf(Fish, Seafood));
        axioms.add(SubClassOf(Wine, ObjectSomeValuesFrom(locatedIn, Region)));
        // axioms.add(SubClassOf(DryRiesling, ObjectHasValue(hasFlavor,
        // Delicate)));
        // axioms.add(SubClassOf(OtherTomatoBasedFoodCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasBody,
        // Medium))));
        // axioms.add(SubClassOf(BlandFishCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasFlavor, Delicate))));
        // axioms.add(SubClassOf(NonSpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasBody, Medium))));
        // axioms.add(SubClassOf(Meat, EdibleThing));
        axioms.add(SubClassOf(WineSugar, WineTaste));
        axioms.add(SubClassOf(LateHarvest, Wine));
        // axioms.add(SubClassOf(Pauillac, ObjectHasValue(madeFromGrape,
        // CabernetSauvignonGrape)));
        // axioms.add(SubClassOf(Merlot, ObjectHasValue(hasSugar, Dry)));
        // axioms.add(SubClassOf(Anjou, ObjectHasValue(hasSugar, OffDry)));
        // axioms.add(SubClassOf(Juice, ObjectMinCardinality(1, madeFromFruit,
        // df.getOWLThing())));
        // axioms.add(SubClassOf(IceWine, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Moderate, Strong))));
        // axioms.add(SubClassOf(StEmilion, ObjectHasValue(hasFlavor, Strong)));
        // axioms.add(SubClassOf(Meritage, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(WhiteBordeaux,
        // ObjectAllValuesFrom(madeFromGrape, ObjectOneOf(SauvignonBlancGrape,
        // SemillonGrape))));
        axioms.add(SubClassOf(Wine, ObjectExactCardinality(1, hasBody, df.getOWLThing())));
        axioms.add(SubClassOf(Wine, ObjectMinCardinality(1, madeFromGrape, df.getOWLThing())));
        // axioms.add(SubClassOf(SweetDessertCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasColor, White))));
        // axioms.add(SubClassOf(Shellfish, Seafood));
        axioms.add(SubClassOf(WhiteBurgundy, ObjectHasValue(madeFromGrape, ChardonnayGrape)));
        // axioms.add(SubClassOf(NonSweetFruitCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasFlavor, Delicate))));
        // axioms.add(SubClassOf(Burgundy, ObjectHasValue(hasSugar, Dry)));
        // axioms.add(SubClassOf(CheninBlanc, ObjectAllValuesFrom(hasBody,
        // ObjectOneOf(Full, Medium))));
        // axioms.add(SubClassOf(NonSweetFruit, EdibleThing));
        axioms.add(SubClassOf(CabernetFranc, ObjectHasValue(hasBody, Medium)));
        // axioms.add(SubClassOf(Margaux, ObjectHasValue(hasFlavor, Delicate)));
        // axioms.add(SubClassOf(Vintage, ObjectExactCardinality(1,
        // hasVintageYear, df.getOWLThing())));
        // axioms.add(SubClassOf(Sancerre, ObjectHasValue(hasBody, Medium)));
        // axioms.add(SubClassOf(Chianti, ObjectHasValue(madeFromGrape,
        // SangioveseGrape)));
        axioms.add(SubClassOf(CabernetSauvignon, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(Medoc, ObjectHasValue(hasColor, Red)));
        // axioms.add(SubClassOf(LightMeatFowlCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasBody, Medium))));
        // axioms.add(SubClassOf(ShellfishCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectAllValuesFrom(hasFlavor, ObjectOneOf(
        // Moderate, Strong)))));
        // axioms.add(SubClassOf(ShellfishCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasBody, Full))));
        // axioms.add(SubClassOf(SweetFruitCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasSugar, Sweet))));
        // axioms.add(SubClassOf(Merlot, ObjectAllValuesFrom(hasBody,
        // ObjectOneOf(Light, Medium))));
        // axioms.add(SubClassOf(Merlot, ObjectAllValuesFrom(hasFlavor,
        // ObjectOneOf(Delicate, Moderate))));
        // axioms.add(SubClassOf(SeafoodCourse, ObjectAllValuesFrom(hasDrink,
        // ObjectHasValue(hasColor, White))));
        // axioms.add(SubClassOf(Riesling, ObjectHasValue(hasColor, White)));
        axioms.add(SubClassOf(WhiteBurgundy, ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing())));
        // axioms.add(SubClassOf(NonRedMeat, Meat));
        axioms.add(SubClassOf(WineBody, WineTaste));
        // axioms.add(SubClassOf(Sauterne, Bordeaux));
        // axioms.add(SubClassOf(SpicyRedMeatCourse,
        // ObjectAllValuesFrom(hasDrink, ObjectHasValue(hasBody, Full))));
        axioms.add(SubClassOf(CabernetSauvignon, ObjectAllValuesFrom(hasBody, ObjectOneOf(Full, Medium))));
        // axioms.add(SubClassOf(RedBordeaux, ObjectAllValuesFrom(madeFromGrape,
        // ObjectOneOf(CabernetSauvignonGrape,
        // MerlotGrape))));
    }

    private void equivalentClasses(List<OWLAxiom> axioms) {
        // axioms.add(EquivalentClasses(Pauillac, ObjectIntersectionOf(Medoc,
        // ObjectHasValue(locatedIn, PauillacRegion))));
        // axioms.add(EquivalentClasses(CaliforniaWine,
        // ObjectIntersectionOf(Wine, ObjectHasValue(locatedIn,
        // CaliforniaRegion))));
        // axioms.add(EquivalentClasses(SweetDessertCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // SweetDessert))));
        // axioms.add(EquivalentClasses(DessertCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // Dessert))));
        // axioms.add(EquivalentClasses(Margaux, ObjectIntersectionOf(Medoc,
        // ObjectHasValue(locatedIn, MargauxRegion))));
        // axioms.add(EquivalentClasses(ShellfishCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // Shellfish))));
        // axioms.add(EquivalentClasses(PinotBlanc, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape,
        // PinotBlancGrape), ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing()))));
        // axioms.add(EquivalentClasses(CotesDOr,
        // ObjectIntersectionOf(RedBurgundy, ObjectHasValue(locatedIn,
        // CotesDOrRegion))));
        // axioms.add(EquivalentClasses(CheninBlanc, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape,
        // CheninBlancGrape), ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing()))));
        // axioms.add(EquivalentClasses(OtherTomatoBasedFoodCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(
        // hasFood, OtherTomatoBasedFood))));
        // axioms.add(EquivalentClasses(SweetWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(hasSugar, Sweet))));
        // axioms.add(EquivalentClasses(Anjou, ObjectIntersectionOf(Loire,
        // ObjectHasValue(locatedIn, AnjouRegion))));
        // axioms.add(EquivalentClasses(DryRedWine,
        // ObjectIntersectionOf(DryWine, RedWine)));
        // axioms.add(EquivalentClasses(WhiteLoire, ObjectIntersectionOf(Loire,
        // WhiteWine)));
        // axioms.add(EquivalentClasses(Meursault,
        // ObjectIntersectionOf(WhiteBurgundy, ObjectHasValue(locatedIn,
        // MeursaultRegion))));
        // axioms.add(EquivalentClasses(RoseWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(hasColor, Rose))));
        // axioms.add(EquivalentClasses(FullBodiedWine,
        // ObjectIntersectionOf(Wine, ObjectHasValue(hasBody, Full))));
        // axioms.add(EquivalentClasses(Loire, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn, LoireRegion))));
        // axioms.add(EquivalentClasses(Semillon,
        // ObjectIntersectionOf(SemillonOrSauvignonBlanc, ObjectHasValue(
        // madeFromGrape, SemillonGrape), ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing()))));
        // axioms.add(EquivalentClasses(DarkMeatFowlCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // DarkMeatFowl))));
        // axioms.add(EquivalentClasses(PetiteSyrah, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape,
        // PetiteSyrahGrape), ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing()))));
        // axioms.add(EquivalentClasses(BlandFishCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // BlandFish))));
        // axioms.add(EquivalentClasses(RedBordeaux,
        // ObjectIntersectionOf(Bordeaux, RedWine)));
        // axioms.add(EquivalentClasses(RedWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(hasColor, Red))));
        // axioms.add(EquivalentClasses(FruitCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // Fruit))));
        axioms.add(EquivalentClasses(CabernetFranc,
            ObjectIntersectionOf(Wine, ObjectHasValue(madeFromGrape, CabernetFrancGrape),
                ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        // axioms.add(EquivalentClasses(TexasWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn, TexasRegion))));
        // axioms.add(EquivalentClasses(WhiteBordeaux,
        // ObjectIntersectionOf(Bordeaux, WhiteWine)));
        // axioms.add(EquivalentClasses(CheeseNutsDessertCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(
        // hasFood, CheeseNutsDessert))));
        // axioms.add(EquivalentClasses(FishCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // Fish))));
        // axioms.add(EquivalentClasses(Gamay, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape, GamayGrape),
        // ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        // axioms.add(EquivalentClasses(Merlot, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape, MerlotGrape),
        // ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        axioms.add(EquivalentClasses(DryWine, ObjectIntersectionOf(Wine, ObjectHasValue(hasSugar, Dry))));
        // axioms.add(EquivalentClasses(Fruit, ObjectUnionOf(NonSweetFruit,
        // SweetFruit)));
        // axioms.add(EquivalentClasses(Sancerre, ObjectIntersectionOf(Loire,
        // ObjectHasValue(locatedIn, SancerreRegion))));
        axioms.add(EquivalentClasses(DryWhiteWine, ObjectIntersectionOf(DryWine, WhiteWine)));
        // axioms.add(EquivalentClasses(Bordeaux, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn, BordeauxRegion))));
        // axioms.add(EquivalentClasses(AlsatianWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn,
        // AlsaceRegion))));
        // axioms.add(EquivalentClasses(TableWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(hasSugar, Dry))));
        // axioms.add(EquivalentClasses(GermanWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn, GermanyRegion))));
        axioms.add(EquivalentClasses(CabernetSauvignon,
            ObjectIntersectionOf(Wine, ObjectHasValue(madeFromGrape, CabernetSauvignonGrape),
                ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        // axioms.add(EquivalentClasses(NonOysterShellfishCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(
        // hasFood, NonOysterShellfish))));
        axioms.add(EquivalentClasses(WhiteWine, ObjectIntersectionOf(Wine, ObjectHasValue(hasColor, White))));
        // axioms.add(EquivalentClasses(RedMeatCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // RedMeat))));
        // axioms.add(EquivalentClasses(RedTableWine,
        // ObjectIntersectionOf(TableWine, ObjectHasValue(hasColor, Red))));
        // axioms.add(EquivalentClasses(NonBlandFishCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // NonBlandFish))));
        // axioms.add(EquivalentClasses(Burgundy, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn, BourgogneRegion))));
        // axioms.add(EquivalentClasses(SeafoodCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // Seafood))));
        axioms.add(EquivalentClasses(WineColor, ObjectOneOf(Red, Rose, White)));
        // axioms.add(EquivalentClasses(StEmilion,
        // ObjectIntersectionOf(Bordeaux, ObjectHasValue(locatedIn,
        // StEmilionRegion))));
        axioms.add(EquivalentClasses(WineSugar, ObjectOneOf(Dry, OffDry, Sweet)));
        // axioms.add(EquivalentClasses(Tours, ObjectIntersectionOf(Loire,
        // ObjectHasValue(locatedIn, ToursRegion))));
        // axioms.add(EquivalentClasses(IceWine,
        // ObjectIntersectionOf(DessertWine, LateHarvest,
        // ObjectHasValue(hasColor,
        // White))));
        // axioms.add(EquivalentClasses(Meritage, ObjectIntersectionOf(Wine,
        // ObjectAllValuesFrom(madeFromGrape,
        // ObjectOneOf(CabernetFrancGrape, CabernetSauvignonGrape, MalbecGrape,
        // MerlotGrape, PetiteVerdotGrape)),
        // ObjectMinCardinality(2, madeFromGrape, df.getOWLThing()))));
        // axioms.add(EquivalentClasses(Medoc, ObjectIntersectionOf(Bordeaux,
        // ObjectHasValue(locatedIn, MedocRegion))));
        // axioms.add(EquivalentClasses(SweetRiesling,
        // ObjectIntersectionOf(Riesling, ObjectHasValue(hasSugar, Sweet))));
        // axioms.add(EquivalentClasses(RedBurgundy,
        // ObjectIntersectionOf(Burgundy, RedWine)));
        // axioms.add(EquivalentClasses(WineFlavor, ObjectOneOf(Delicate,
        // Moderate, Strong)));
        // axioms.add(EquivalentClasses(SemillonOrSauvignonBlanc,
        // ObjectIntersectionOf(Wine, ObjectAllValuesFrom(
        // madeFromGrape, ObjectOneOf(SauvignonBlancGrape, SemillonGrape)))));
        // axioms.add(EquivalentClasses(Riesling, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape, RieslingGrape),
        // ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        // axioms.add(EquivalentClasses(SweetFruitCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // SweetFruit))));
        axioms.add(EquivalentClasses(WineDescriptor, ObjectUnionOf(WineColor, WineTaste)));
        // axioms.add(EquivalentClasses(ItalianWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn,
        // ItalianRegion))));
        // axioms.add(EquivalentClasses(WhiteBurgundy,
        // ObjectIntersectionOf(Burgundy, WhiteWine)));
        // axioms.add(EquivalentClasses(PinotNoir, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape,
        // PinotNoirGrape), ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing()))));
        // axioms.add(EquivalentClasses(LightMeatFowlCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // LightMeatFowl))));
        // axioms.add(EquivalentClasses(NonSweetFruitCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // NonSweetFruit))));
        // axioms.add(EquivalentClasses(Zinfandel, ObjectIntersectionOf(Wine,
        // ObjectHasValue(madeFromGrape,
        // ZinfandelGrape), ObjectMaxCardinality(1, madeFromGrape,
        // df.getOWLThing()))));
        axioms.add(EquivalentClasses(WhiteNonSweetWine,
            ObjectIntersectionOf(WhiteWine, ObjectAllValuesFrom(hasSugar, ObjectOneOf(Dry, OffDry)))));
        // axioms.add(EquivalentClasses(SpicyRedMeatCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // SpicyRedMeat))));
        // axioms.add(EquivalentClasses(DryRiesling,
        // ObjectIntersectionOf(Riesling, ObjectHasValue(hasSugar, Dry))));
        axioms.add(EquivalentClasses(WineBody, ObjectOneOf(Full, Light, Medium)));
        // axioms.add(EquivalentClasses(NonRedMeatCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(hasFood,
        // NonRedMeat))));
        // axioms.add(EquivalentClasses(NonSpicyRedMeatCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(
        // hasFood, NonSpicyRedMeat))));
        // axioms.add(EquivalentClasses(Muscadet, ObjectIntersectionOf(Loire,
        // ObjectHasValue(locatedIn, MuscadetRegion))));
        // axioms.add(EquivalentClasses(Beaujolais, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn,
        // BeaujolaisRegion))));
        // axioms.add(EquivalentClasses(SauvignonBlanc,
        // ObjectIntersectionOf(SemillonOrSauvignonBlanc, ObjectHasValue(
        // madeFromGrape, SauvignonBlancGrape), ObjectMaxCardinality(1,
        // madeFromGrape, df.getOWLThing()))));
        // axioms.add(EquivalentClasses(AmericanWine, ObjectIntersectionOf(Wine,
        // ObjectHasValue(locatedIn, USRegion))));
        // axioms.add(EquivalentClasses(OysterShellfishCourse,
        // ObjectIntersectionOf(MealCourse, ObjectAllValuesFrom(
        // hasFood, OysterShellfish))));
        axioms.add(EquivalentClasses(Chardonnay, ObjectIntersectionOf(Wine,
            ObjectHasValue(madeFromGrape, ChardonnayGrape), ObjectMaxCardinality(1, madeFromGrape, df.getOWLThing()))));
        axioms.add(EquivalentClasses(FrenchWine, ObjectIntersectionOf(Wine, ObjectHasValue(locatedIn, FrenchRegion))));
        axioms.add(EquivalentClasses(NonConsumableThing, ObjectComplementOf(ConsumableThing)));
        // axioms.add(EquivalentClasses(WhiteTableWine,
        // ObjectIntersectionOf(TableWine, ObjectHasValue(hasColor, White))));
    }
}
