package conformance;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

@SuppressWarnings("javadoc")
public class WebOnt_miscellaneous_002 {
    @Test
    public void testWebOnt_miscellaneous_002() {
        String conclusion = "";
        String id = "WebOnt_miscellaneous_002";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Food example taken from the guide. Note that this is the same as the ontology http://www.w3.org/2002/03owlt/miscellaneous/consistent002 imported in other tests.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }

    public void _testWebOnt_miscellaneous_002(String s1, String s2)
            throws OWLOntologyCreationException {
        String conclusion = "";
        String id = "WebOnt_miscellaneous_002";
        TestClasses tc = TestClasses.valueOf("CONSISTENCY");
        String d = "Food example taken from the guide. Note that this is the same as the ontology http://www.w3.org/2002/03owlt/miscellaneous/consistent002 imported in other tests.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.getConfiguration().setorSortSat(s1);
        r.getConfiguration().setorSortSub(s2);
        // r.getConfiguration().setLoggingActive(true);
        // r.setReasonerFactory(Factory.factory());
        try {
            long start = System.currentTimeMillis();
            OWLReasoner reasoner = Factory.factory().createReasoner(r.getPremise(),
                    r.getConfiguration());
            System.out.println("WebOnt_miscellaneous_002.testWebOnt_miscellaneous_002() "
                    + reasoner.isConsistent());
            // r.run();
            System.out
                    .println("WebOnt_miscellaneous_002.testWebOnt_miscellaneous_002() done in "
                            + (System.currentTimeMillis() - start));
        } catch (RuntimeException e) {
            System.out.println("WebOnt_miscellaneous_002.testWebOnt_miscellaneous_002() "
                    + e.getMessage());
        }
    }

    private static String[] options = new String[] { "Sap", "Sdp", "San", "Sdn", "Dap",
            "Ddp", "Dan", "Ddn", "Fap", "Fdp", "Fan", "Fdn", "Bap", "Bdp", "Ban", "Bdn",
            "Gap", "Gdp", "Gan", "Gdn" };

    public static void main(String[] args) throws OWLOntologyCreationException {
        WebOnt_miscellaneous_002 t = new WebOnt_miscellaneous_002();
        for (String s1 : options) {
            for (String s2 : options) {
                t._testWebOnt_miscellaneous_002(s1, s2);
            }
        }
    }

    private static String premise = "<!DOCTYPE owl [<!ENTITY vin  \"http://www.w3.org/2002/03owlt/miscellaneous/consistent001#\" ><!ENTITY food \"http://www.w3.org/2002/03owlt/miscellaneous/consistent002#\" ><!ENTITY xsd  \"http://www.w3.org/2001/XMLSchema#\" >\n"
            + "   ]>\n"
            + "<rdf:RDF xmlns     = \"&food;\" xmlns:food= \"&food;\"\n"
            + "  xml:base  = \"&food;\" xmlns:vin = \"&vin;\" xmlns:owl = \"http://www.w3.org/2002/07/owl#\" xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\">\n"
            + "  <owl:Ontology rdf:about=\"\"><rdfs:comment>\n"
            + "      Derived from the DAML Wine ontology at \n"
            + "      http://ontolingua.stanford.edu/doc/chimaera/ontologies/wines.daml\n"
            + "      Substantially modified.</rdfs:comment> <owl:imports rdf:resource=\"http://www.w3.org/2002/03owlt/miscellaneous/consistent001\"/></owl:Ontology>\n"
            + "  <owl:Class rdf:ID=\"ConsumableThing\" />\n"
            + "  <owl:Class rdf:ID=\"NonConsumableThing\"><owl:complementOf rdf:resource=\"#ConsumableThing\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"EdibleThing\"><rdfs:subClassOf rdf:resource=\"#ConsumableThing\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PotableLiquid\"><rdfs:subClassOf rdf:resource=\"#ConsumableThing\" /><owl:disjointWith rdf:resource=\"#EdibleThing\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Wine\"><owl:equivalentClass rdf:resource=\"&vin;Wine\"/></owl:Class>\n"
            + "  <owl:ObjectProperty rdf:ID=\"madeFromFruit\"><rdfs:domain rdf:resource=\"#ConsumableThing\" /><rdfs:range rdf:resource=\"#Fruit\" /></owl:ObjectProperty>\n"
            + "  <owl:Class rdf:ID=\"Juice\"><rdfs:subClassOf rdf:resource=\"#PotableLiquid\" /><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#madeFromFruit\" />\n"
            + "  <owl:minCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:ObjectProperty rdf:ID=\"hasDrink\"><rdfs:domain rdf:resource=\"#MealCourse\" /><rdfs:range rdf:resource=\"#PotableLiquid\" /></owl:ObjectProperty>\n"
            + "  <owl:Thing rdf:ID=\"White\"><owl:sameAs rdf:resource=\"&vin;White\"/></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Rose\"><owl:sameAs rdf:resource=\"&vin;Rose\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Red\"><owl:sameAs rdf:resource=\"&vin;Red\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Sweet\"><owl:sameAs rdf:resource=\"&vin;Sweet\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"OffDry\"><owl:sameAs rdf:resource=\"&vin;OffDry\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Dry\"><owl:sameAs rdf:resource=\"&vin;Dry\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Delicate\"><owl:sameAs rdf:resource=\"&vin;Delicate\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Moderate\"><owl:sameAs rdf:resource=\"&vin;Moderate\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Strong\"><owl:sameAs rdf:resource=\"&vin;Strong\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Light\"><owl:sameAs rdf:resource=\"&vin;Light\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Medium\"><owl:sameAs rdf:resource=\"&vin;Medium\" /></owl:Thing>\n"
            + "  <owl:Thing rdf:ID=\"Full\"><owl:sameAs rdf:resource=\"&vin;Full\" /></owl:Thing>\n"
            + "  <owl:ObjectProperty rdf:ID=\"course\"><rdfs:domain rdf:resource=\"#Meal\" /><rdfs:range rdf:resource=\"#MealCourse\" /></owl:ObjectProperty>\n"
            + "  <owl:Class rdf:ID=\"Grape\"><rdfs:subClassOf rdf:resource=\"#SweetFruit\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Pasta\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /><owl:disjointWith rdf:resource=\"#Meat\" /><owl:disjointWith rdf:resource=\"#Fowl\" /><owl:disjointWith rdf:resource=\"#Seafood\" /><owl:disjointWith rdf:resource=\"#Dessert\" /><owl:disjointWith rdf:resource=\"#Fruit\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithWhiteSauce\"><rdfs:subClassOf rdf:resource=\"#Pasta\" /><owl:disjointWith rdf:resource=\"#PastaWithRedSauce\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithSpicyRedSauceCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#PastaWithSpicyRedSauce\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Full\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Strong\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithSpicyRedSauce\"><rdfs:subClassOf rdf:resource=\"#PastaWithRedSauce\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithRedSauce\"><rdfs:subClassOf rdf:resource=\"#Pasta\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithNonSpicyRedSauceCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#PastaWithNonSpicyRedSauce\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Moderate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithNonSpicyRedSauce\"><rdfs:subClassOf rdf:resource=\"#PastaWithRedSauce\" /><owl:disjointWith rdf:resource=\"#PastaWithSpicyRedSauce\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithLightCreamSauce\"><rdfs:subClassOf rdf:resource=\"#PastaWithWhiteSauce\" /><owl:disjointWith rdf:resource=\"#PastaWithHeavyCreamSauce\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithLightCreamCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#PastaWithLightCreamSauce\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#White\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Light\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Delicate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithHeavyCreamSauce\"><rdfs:subClassOf rdf:resource=\"#PastaWithWhiteSauce\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"PastaWithHeavyCreamCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#PastaWithHeavyCreamSauce\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#White\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Moderate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"OysterShellfishCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#OysterShellfish\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Sweet\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"OysterShellfish\"><rdfs:subClassOf rdf:resource=\"#Shellfish\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"OtherTomatoBasedFoodCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#OtherTomatoBasedFood\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Moderate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"OtherTomatoBasedFood\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /><owl:disjointWith rdf:resource=\"#Pasta\" /><owl:disjointWith rdf:resource=\"#Meat\" /><owl:disjointWith rdf:resource=\"#Fowl\" /><owl:disjointWith rdf:resource=\"#Seafood\" /><owl:disjointWith rdf:resource=\"#Dessert\" /><owl:disjointWith rdf:resource=\"#Fruit\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonSweetFruitCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#NonSweetFruit\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Delicate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#OffDry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"SweetFruitCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#SweetFruit\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Moderate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Sweet\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonSweetFruit\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /><owl:disjointWith rdf:resource=\"#SweetFruit\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonSpicyRedMeatCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#NonSpicyRedMeat\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:allValuesFrom><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"#Strong\" /><owl:Thing rdf:about=\"#Moderate\" /></owl:oneOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonSpicyRedMeat\"><rdfs:subClassOf rdf:resource=\"#RedMeat\" /><owl:disjointWith rdf:resource=\"#SpicyRedMeat\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonRedMeatCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#NonRedMeat\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#White\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Strong\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonRedMeat\"><rdfs:subClassOf rdf:resource=\"#Meat\" /><owl:disjointWith rdf:resource=\"#RedMeat\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonOysterShellfishCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#NonOysterShellfish\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonOysterShellfish\"><rdfs:subClassOf rdf:resource=\"#Shellfish\" /><owl:disjointWith rdf:resource=\"#OysterShellfish\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonBlandFishCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#NonBlandFish\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom><owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Moderate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"NonBlandFish\"><rdfs:subClassOf rdf:resource=\"#Fish\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Meat\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /><owl:disjointWith rdf:resource=\"#Fowl\" /><owl:disjointWith rdf:resource=\"#Seafood\" /><owl:disjointWith rdf:resource=\"#Dessert\" /><owl:disjointWith rdf:resource=\"#Fruit\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"MealCourse\"><rdfs:subClassOf rdf:resource=\"#ConsumableThing\" /><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:minCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:minCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom rdf:resource=\"#Wine\" /></owl:Restriction></rdfs:subClassOf><owl:disjointWith rdf:resource=\"#PotableLiquid\" /><owl:disjointWith rdf:resource=\"#EdibleThing\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Meal\"><rdfs:subClassOf rdf:resource=\"#ConsumableThing\" /><rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#course\" /><owl:minCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#course\" /><owl:allValuesFrom rdf:resource=\"#MealCourse\" /></owl:Restriction></rdfs:subClassOf><owl:disjointWith rdf:resource=\"#MealCourse\" /><owl:disjointWith rdf:resource=\"#PotableLiquid\" /><owl:disjointWith rdf:resource=\"#EdibleThing\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"LightMeatFowlCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#LightMeatFowl\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#White\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Moderate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"LightMeatFowl\"><rdfs:subClassOf rdf:resource=\"#Fowl\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"FruitCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#Fruit\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#White\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Fruit\"><owl:unionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#SweetFruit\" /><owl:Class rdf:about=\"#NonSweetFruit\" /></owl:unionOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Fowl\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /><owl:disjointWith rdf:resource=\"#Seafood\" /><owl:disjointWith rdf:resource=\"#Dessert\" /><owl:disjointWith rdf:resource=\"#Fruit\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"FishCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#Fish\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Medium\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"DessertCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#Dessert\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Full\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Strong\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Sweet\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Dessert\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /><owl:disjointWith rdf:resource=\"#Fruit\" /></owl:Class>\n"
            + "   <owl:Class rdf:ID=\"SweetFruit\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"SweetDessertCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#SweetDessert\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#White\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"SweetDessert\"><rdfs:subClassOf rdf:resource=\"#Dessert\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"DarkMeatFowlCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#DarkMeatFowl\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Light\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Delicate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"DarkMeatFowl\"><rdfs:subClassOf rdf:resource=\"#Fowl\" /><owl:disjointWith rdf:resource=\"#LightMeatFowl\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"CheeseNutsDessertCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#CheeseNutsDessert\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"CheeseNutsDessert\"><rdfs:subClassOf rdf:resource=\"#Dessert\" /><owl:disjointWith rdf:resource=\"#SweetDessert\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"BlandFishCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#BlandFish\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Delicate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"BlandFish\"><rdfs:subClassOf rdf:resource=\"#Fish\" /><owl:disjointWith rdf:resource=\"#NonBlandFish\" /></owl:Class>\n"
            + "  <owl:ObjectProperty rdf:ID=\"hasFood\"><rdfs:domain rdf:resource=\"#MealCourse\" /><rdfs:range rdf:resource=\"#EdibleThing\" /></owl:ObjectProperty>\n"
            + "  <owl:Class rdf:ID=\"Fish\"><rdfs:subClassOf rdf:resource=\"#Seafood\" /><owl:disjointWith rdf:resource=\"#Shellfish\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"SpicyRedMeat\"><rdfs:subClassOf rdf:resource=\"#RedMeat\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"ShellfishCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#Shellfish\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:allValuesFrom>\n"
            + "              <owl:Class><owl:oneOf rdf:parseType=\"Collection\"><owl:Thing rdf:about=\"#Moderate\" /><owl:Thing rdf:about=\"#Strong\" /></owl:oneOf></owl:Class></owl:allValuesFrom></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf>\n"
            + "    <rdfs:subClassOf><owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Full\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Shellfish\"><rdfs:subClassOf rdf:resource=\"#Seafood\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"SeafoodCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#Seafood\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#White\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"Seafood\"><rdfs:subClassOf rdf:resource=\"#EdibleThing\" /><owl:disjointWith rdf:resource=\"#Dessert\" /><owl:disjointWith rdf:resource=\"#Fruit\" /></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"RedMeatCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#RedMeat\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <owl:Class rdf:ID=\"RedMeat\"><rdfs:subClassOf rdf:resource=\"#Meat\" /></owl:Class>\n"
            + "  <SweetDessert rdf:ID=\"Cake\" />\n"
            + "  <SweetFruit rdf:ID=\"Bananas\" />\n"
            + "  <SweetFruit rdf:ID=\"MixedFruit\" />\n"
            + "  <NonOysterShellfish rdf:ID=\"Mussels\" />\n"
            + "  <CheeseNutsDessert rdf:ID=\"Nuts\" />\n"
            + "  <OysterShellfish rdf:ID=\"Oysters\" />\n"
            + "  <PastaWithLightCreamSauce rdf:ID=\"PastaWithWhiteClamSauce\" />\n"
            + "  <SweetFruit rdf:ID=\"Peaches\" />\n"
            + "  <SweetDessert rdf:ID=\"Pie\" />\n"
            + "  <OtherTomatoBasedFood rdf:ID=\"Pizza\" />\n"
            + "  <NonRedMeat rdf:ID=\"Pork\" />\n"
            + "  <NonSpicyRedMeat rdf:ID=\"RoastBeef\" />\n"
            + "  <BlandFish rdf:ID=\"Scrod\" />\n"
            + "  <PastaWithNonSpicyRedSauce rdf:ID=\"SpaghettiWithTomatoSauce\" />\n"
            + "  <NonSpicyRedMeat rdf:ID=\"Steak\" />\n"
            + "  <NonBlandFish rdf:ID=\"Swordfish\" />\n"
            + "  <EatingGrape rdf:ID=\"ThompsonSeedless\" />\n"
            + "  <owl:Class rdf:ID=\"EatingGrape\"><rdfs:subClassOf rdf:resource=\"#Grape\" /></owl:Class>\n"
            + "  <NonBlandFish rdf:ID=\"Tuna\" />\n"
            + "  <LightMeatFowl rdf:ID=\"Turkey\" />\n"
            + "  <NonSpicyRedMeat rdf:ID=\"Veal\" />\n"
            + "  <owl:Class rdf:ID=\"SpicyRedMeatCourse\"><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#MealCourse\" /><owl:Restriction><owl:onProperty rdf:resource=\"#hasFood\" /><owl:allValuesFrom rdf:resource=\"#SpicyRedMeat\" /></owl:Restriction></owl:intersectionOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasColor\" /><owl:hasValue rdf:resource=\"#Red\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasBody\" /><owl:hasValue rdf:resource=\"#Full\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasFlavor\" /><owl:hasValue rdf:resource=\"#Moderate\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf>\n"
            + "      <owl:Restriction><owl:onProperty rdf:resource=\"#hasDrink\" /><owl:allValuesFrom>\n"
            + "          <owl:Restriction><owl:onProperty rdf:resource=\"&vin;hasSugar\" /><owl:hasValue rdf:resource=\"#Dry\" /></owl:Restriction></owl:allValuesFrom></owl:Restriction></rdfs:subClassOf></owl:Class>\n"
            + "  <SpicyRedMeat rdf:ID=\"BeefCurry\" />\n"
            + "  <LightMeatFowl rdf:ID=\"Chicken\" />\n"
            + "  <NonOysterShellfish rdf:ID=\"Clams\" />\n"
            + "  <DarkMeatFowl rdf:ID=\"Duck\" />\n"
            + "  <SpicyRedMeat rdf:ID=\"GarlickyRoast\" />\n"
            + "  <DarkMeatFowl rdf:ID=\"Goose\" />\n"
            + "  <BlandFish rdf:ID=\"Halibut\" />\n"
            + "  <NonOysterShellfish rdf:ID=\"Crab\" />\n"
            + "  <BlandFish rdf:ID=\"Flounder\" />\n"
            + "  <NonOysterShellfish rdf:ID=\"Lobster\" />\n"
            + "  <PastaWithHeavyCreamSauce rdf:ID=\"FettucineAlfRedo\" />\n"
            + "  <PastaWithSpicyRedSauce rdf:ID=\"FraDiavolo\" />\n"
            + "  <CheeseNutsDessert rdf:ID=\"Cheese\" />\n" + "</rdf:RDF>";
}
