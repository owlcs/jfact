package bugs;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

@SuppressWarnings("javadoc")
public class VerifyCompliancePeople extends VerifyComplianceBase {

    String in = "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + '\n'
            + '\n'
            + "Ontology(<urn:people.owl>\n"
            + '\n'
            + "Declaration(Class(<urn:people#adult>))\n"
            + "Declaration(Class(<urn:people#animal>))\n"
            + "Declaration(Class(<urn:people#animal_lover>))\n"
            + "Declaration(Class(<urn:people#bicycle>))\n"
            + "Declaration(Class(<urn:people#bone>))\n"
            + "Declaration(Class(<urn:people#brain>))\n"
            + "Declaration(Class(<urn:people#broadsheet>))\n"
            + "Declaration(Class(<urn:people#bus>))\n"
            + "Declaration(Class(<urn:people#bus_company>))\n"
            + "Declaration(Class(<urn:people#bus_driver>))\n"
            + "Declaration(Class(<urn:people#car>))\n"
            + "Declaration(Class(<urn:people#cat>))\n"
            + "Declaration(Class(<urn:people#cat_liker>))\n"
            + "Declaration(Class(<urn:people#cat_owner>))\n"
            + "Declaration(Class(<urn:people#company>))\n"
            + "Declaration(Class(<urn:people#cow>))\n"
            + "Declaration(Class(<urn:people#dog>))\n"
            + "Declaration(Class(<urn:people#dog_liker>))\n"
            + "Declaration(Class(<urn:people#dog_owner>))\n"
            + "Declaration(Class(<urn:people#driver>))\n"
            + "Declaration(Class(<urn:people#duck>))\n"
            + "Declaration(Class(<urn:people#elderly>))\n"
            + "Declaration(Class(<urn:people#female>))\n"
            + "Declaration(Class(<urn:people#giraffe>))\n"
            + "Declaration(Class(<urn:people#grass>))\n"
            + "Declaration(Class(<urn:people#grownup>))\n"
            + "Declaration(Class(<urn:people#haulage_company>))\n"
            + "Declaration(Class(<urn:people#haulage_truck_driver>))\n"
            + "Declaration(Class(<urn:people#haulage_worker>))\n"
            + "Declaration(Class(<urn:people#kid>))\n"
            + "Declaration(Class(<urn:people#leaf>))\n"
            + "Declaration(Class(<urn:people#lorry>))\n"
            + "Declaration(Class(<urn:people#lorry_driver>))\n"
            + "Declaration(Class(<urn:people#mad_cow>))\n"
            + "Declaration(Class(<urn:people#magazine>))\n"
            + "Declaration(Class(<urn:people#male>))\n"
            + "Declaration(Class(<urn:people#man>))\n"
            + "Declaration(Class(<urn:people#newspaper>))\n"
            + "Declaration(Class(<urn:people#old_lady>))\n"
            + "Declaration(Class(<urn:people#person>))\n"
            + "Declaration(Class(<urn:people#pet>))\n"
            + "Declaration(Class(<urn:people#pet_owner>))\n"
            + "Declaration(Class(<urn:people#plant>))\n"
            + "Declaration(Class(<urn:people#publication>))\n"
            + "Declaration(Class(<urn:people#quality_broadsheet>))\n"
            + "Declaration(Class(<urn:people#red_top>))\n"
            + "Declaration(Class(<urn:people#sheep>))\n"
            + "Declaration(Class(<urn:people#tabloid>))\n"
            + "Declaration(Class(<urn:people#tiger>))\n"
            + "Declaration(Class(<urn:people#tree>))\n"
            + "Declaration(Class(<urn:people#truck>))\n"
            + "Declaration(Class(<urn:people#van>))\n"
            + "Declaration(Class(<urn:people#van_driver>))\n"
            + "Declaration(Class(<urn:people#vegetarian>))\n"
            + "Declaration(Class(<urn:people#vehicle>))\n"
            + "Declaration(Class(<urn:people#white_thing>))\n"
            + "Declaration(Class(<urn:people#white_van_man>))\n"
            + "Declaration(Class(<urn:people#woman>))\n"
            + "Declaration(Class(<urn:people#young>))\n"
            + "Declaration(Class(owl:Thing))\n"
            + "Declaration(ObjectProperty(<urn:people#drives>))\n"
            + "Declaration(ObjectProperty(<urn:people#eaten_by>))\n"
            + "Declaration(ObjectProperty(<urn:people#eats>))\n"
            + "Declaration(ObjectProperty(<urn:people#has_child>))\n"
            + "Declaration(ObjectProperty(<urn:people#has_father>))\n"
            + "Declaration(ObjectProperty(<urn:people#has_mother>))\n"
            + "Declaration(ObjectProperty(<urn:people#has_parent>))\n"
            + "Declaration(ObjectProperty(<urn:people#has_part>))\n"
            + "Declaration(ObjectProperty(<urn:people#has_pet>))\n"
            + "Declaration(ObjectProperty(<urn:people#is_pet_of>))\n"
            + "Declaration(ObjectProperty(<urn:people#likes>))\n"
            + "Declaration(ObjectProperty(<urn:people#part_of>))\n"
            + "Declaration(ObjectProperty(<urn:people#reads>))\n"
            + "Declaration(ObjectProperty(<urn:people#works_for>))\n"
            + "Declaration(NamedIndividual(<urn:people#Daily_Mirror>))\n"
            + "Declaration(NamedIndividual(<urn:people#Dewey>))\n"
            + "Declaration(NamedIndividual(<urn:people#Fido>))\n"
            + "Declaration(NamedIndividual(<urn:people#Flossie>))\n"
            + "Declaration(NamedIndividual(<urn:people#Fluffy>))\n"
            + "Declaration(NamedIndividual(<urn:people#Fred>))\n"
            + "Declaration(NamedIndividual(<urn:people#Huey>))\n"
            + "Declaration(NamedIndividual(<urn:people#Joe>))\n"
            + "Declaration(NamedIndividual(<urn:people#Kevin>))\n"
            + "Declaration(NamedIndividual(<urn:people#Louie>))\n"
            + "Declaration(NamedIndividual(<urn:people#Mick>))\n"
            + "Declaration(NamedIndividual(<urn:people#Minnie>))\n"
            + "Declaration(NamedIndividual(<urn:people#Pete>))\n"
            + "Declaration(NamedIndividual(<urn:people#Q123_ABC>))\n"
            + "Declaration(NamedIndividual(<urn:people#Rex>))\n"
            + "Declaration(NamedIndividual(<urn:people#Spike>))\n"
            + "Declaration(NamedIndividual(<urn:people#The_Guardian>))\n"
            + "Declaration(NamedIndividual(<urn:people#The_Sun>))\n"
            + "Declaration(NamedIndividual(<urn:people#The_Times>))\n"
            + "Declaration(NamedIndividual(<urn:people#Tibbs>))\n"
            + "Declaration(NamedIndividual(<urn:people#Tom>))\n"
            + "Declaration(NamedIndividual(<urn:people#Walt>))\n"
            + "DisjointClasses(<urn:people#adult> <urn:people#young>)\n"
            + "SubClassOf(<urn:people#animal> ObjectSomeValuesFrom(<urn:people#eats> owl:Thing))\n"
            + "EquivalentClasses(<urn:people#animal_lover> ObjectIntersectionOf(ObjectMinCardinality(3 <urn:people#has_pet>) <urn:people#person>))\n"
            + "SubClassOf(<urn:people#bicycle> <urn:people#vehicle>)\n"
            + "SubClassOf(<urn:people#broadsheet> <urn:people#newspaper>)\n"
            + "DisjointClasses(<urn:people#broadsheet> <urn:people#tabloid>)\n"
            + "SubClassOf(<urn:people#bus> <urn:people#vehicle>)\n"
            + "SubClassOf(<urn:people#bus_company> <urn:people#company>)\n"
            + "EquivalentClasses(<urn:people#bus_driver> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#drives> <urn:people#bus>) <urn:people#person>))\n"
            + "SubClassOf(<urn:people#car> <urn:people#vehicle>)\n"
            + "SubClassOf(<urn:people#cat> <urn:people#animal>)\n"
            + "DisjointClasses(<urn:people#cat> <urn:people#dog>)\n"
            + "EquivalentClasses(<urn:people#cat_liker> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#likes> <urn:people#cat>) <urn:people#person>))\n"
            + "EquivalentClasses(<urn:people#cat_owner> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#has_pet> <urn:people#cat>) <urn:people#person>))\n"
            + "SubClassOf(<urn:people#cow> <urn:people#vegetarian>)\n"
            + "SubClassOf(<urn:people#dog> ObjectSomeValuesFrom(<urn:people#eats> <urn:people#bone>))\n"
            + "EquivalentClasses(<urn:people#dog_liker> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#likes> <urn:people#dog>) <urn:people#person>))\n"
            + "EquivalentClasses(<urn:people#dog_owner> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#has_pet> <urn:people#dog>) <urn:people#person>))\n"
            + "EquivalentClasses(<urn:people#driver> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#drives> <urn:people#vehicle>) <urn:people#person>))\n"
            + "SubClassOf(<urn:people#driver> <urn:people#adult>)\n"
            + "SubClassOf(<urn:people#duck> <urn:people#animal>)\n"
            + "SubClassOf(<urn:people#elderly> <urn:people#adult>)\n"
            + "SubClassOf(<urn:people#giraffe> <urn:people#animal>)\n"
            + "SubClassOf(<urn:people#giraffe> ObjectAllValuesFrom(<urn:people#eats> <urn:people#leaf>))\n"
            + "SubClassOf(<urn:people#grass> <urn:people#plant>)\n"
            + "EquivalentClasses(<urn:people#grownup> ObjectIntersectionOf(<urn:people#person> <urn:people#adult>))\n"
            + "SubClassOf(<urn:people#haulage_company> <urn:people#company>)\n"
            + "EquivalentClasses(<urn:people#haulage_truck_driver> ObjectIntersectionOf(<urn:people#person> ObjectSomeValuesFrom(<urn:people#drives> <urn:people#truck>) ObjectSomeValuesFrom(<urn:people#works_for> ObjectSomeValuesFrom(<urn:people#part_of> <urn:people#haulage_company>))))\n"
            + "EquivalentClasses(<urn:people#haulage_worker> ObjectSomeValuesFrom(<urn:people#works_for> ObjectUnionOf(ObjectSomeValuesFrom(<urn:people#part_of> <urn:people#haulage_company>) <urn:people#haulage_company>)))\n"
            + "EquivalentClasses(<urn:people#kid> ObjectIntersectionOf(<urn:people#young> <urn:people#person>))\n"
            + "SubClassOf(<urn:people#leaf> ObjectSomeValuesFrom(<urn:people#part_of> <urn:people#tree>))\n"
            + "SubClassOf(<urn:people#lorry> <urn:people#vehicle>)\n"
            + "EquivalentClasses(<urn:people#lorry_driver> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#drives> <urn:people#lorry>) <urn:people#person>))\n"
            + "EquivalentClasses(<urn:people#mad_cow> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#eats> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#part_of> <urn:people#sheep>) <urn:people#brain>)) <urn:people#cow>))\n"
            + "SubClassOf(<urn:people#magazine> <urn:people#publication>)\n"
            + "EquivalentClasses(<urn:people#man> ObjectIntersectionOf(<urn:people#adult> <urn:people#male> <urn:people#person>))\n"
            + "SubClassOf(<urn:people#newspaper> <urn:people#publication>)\n"
            + "SubClassOf(<urn:people#newspaper> ObjectUnionOf(<urn:people#tabloid> <urn:people#broadsheet>))\n"
            + "EquivalentClasses(<urn:people#old_lady> ObjectIntersectionOf(<urn:people#elderly> <urn:people#female> <urn:people#person>))\n"
            + "SubClassOf(<urn:people#old_lady> ObjectIntersectionOf(ObjectAllValuesFrom(<urn:people#has_pet> <urn:people#cat>) ObjectSomeValuesFrom(<urn:people#has_pet> <urn:people#animal>)))\n"
            + "SubClassOf(<urn:people#person> <urn:people#animal>)\n"
            + "EquivalentClasses(<urn:people#pet> ObjectSomeValuesFrom(<urn:people#is_pet_of> owl:Thing))\n"
            + "EquivalentClasses(<urn:people#pet_owner> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#has_pet> <urn:people#animal>) <urn:people#person>))\n"
            + "SubClassOf(<urn:people#quality_broadsheet> <urn:people#broadsheet>)\n"
            + "SubClassOf(<urn:people#red_top> <urn:people#tabloid>)\n"
            + "SubClassOf(<urn:people#sheep> <urn:people#animal>)\n"
            + "SubClassOf(<urn:people#sheep> ObjectAllValuesFrom(<urn:people#eats> <urn:people#grass>))\n"
            + "SubClassOf(<urn:people#tabloid> <urn:people#newspaper>)\n"
            + "SubClassOf(<urn:people#tiger> <urn:people#animal>)\n"
            + "SubClassOf(<urn:people#tree> <urn:people#plant>)\n"
            + "SubClassOf(<urn:people#truck> <urn:people#vehicle>)\n"
            + "SubClassOf(<urn:people#van> <urn:people#vehicle>)\n"
            + "EquivalentClasses(<urn:people#van_driver> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#drives> <urn:people#van>) <urn:people#person>))\n"
            + "EquivalentClasses(<urn:people#vegetarian> ObjectIntersectionOf(<urn:people#animal> ObjectAllValuesFrom(<urn:people#eats> ObjectComplementOf(<urn:people#animal>)) ObjectAllValuesFrom(<urn:people#eats> ObjectComplementOf(ObjectSomeValuesFrom(<urn:people#part_of> <urn:people#animal>)))))\n"
            + "EquivalentClasses(<urn:people#white_van_man> ObjectIntersectionOf(ObjectSomeValuesFrom(<urn:people#drives> ObjectIntersectionOf(<urn:people#white_thing> <urn:people#van>)) <urn:people#man>))\n"
            + "SubClassOf(<urn:people#white_van_man> ObjectAllValuesFrom(<urn:people#reads> <urn:people#tabloid>))\n"
            + "EquivalentClasses(<urn:people#woman> ObjectIntersectionOf(<urn:people#adult> <urn:people#female> <urn:people#person>))\n"
            + "InverseObjectProperties(<urn:people#eaten_by> <urn:people#eats>)\n"
            + "ObjectPropertyDomain(<urn:people#eats> <urn:people#animal>)\n"
            + "SubObjectPropertyOf(<urn:people#has_father> <urn:people#has_parent>)\n"
            + "ObjectPropertyRange(<urn:people#has_father> <urn:people#man>)\n"
            + "SubObjectPropertyOf(<urn:people#has_mother> <urn:people#has_parent>)\n"
            + "ObjectPropertyRange(<urn:people#has_mother> <urn:people#woman>)\n"
            + "InverseObjectProperties(<urn:people#has_part> <urn:people#part_of>)\n"
            + "SubObjectPropertyOf(<urn:people#has_pet> <urn:people#likes>)\n"
            + "InverseObjectProperties(<urn:people#has_pet> <urn:people#is_pet_of>)\n"
            + "ObjectPropertyDomain(<urn:people#has_pet> <urn:people#person>)\n"
            + "ObjectPropertyRange(<urn:people#has_pet> <urn:people#animal>)\n"
            + "ObjectPropertyRange(<urn:people#reads> <urn:people#publication>)\n"
            + "ClassAssertion(owl:Thing <urn:people#Daily_Mirror>)\n"
            + "ClassAssertion(<urn:people#duck> <urn:people#Dewey>)\n"
            + "ClassAssertion(<urn:people#dog> <urn:people#Fido>)\n"
            + "ClassAssertion(<urn:people#cow> <urn:people#Flossie>)\n"
            + "ClassAssertion(<urn:people#tiger> <urn:people#Fluffy>)\n"
            + "ClassAssertion(<urn:people#person> <urn:people#Fred>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Fred> <urn:people#Tibbs>)\n"
            + "ClassAssertion(<urn:people#duck> <urn:people#Huey>)\n"
            + "ClassAssertion(<urn:people#person> <urn:people#Joe>)\n"
            + "ClassAssertion(ObjectMaxCardinality(1 <urn:people#has_pet>) <urn:people#Joe>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Joe> <urn:people#Fido>)\n"
            + "ClassAssertion(<urn:people#person> <urn:people#Kevin>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Kevin> <urn:people#Fluffy>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Kevin> <urn:people#Flossie>)\n"
            + "ClassAssertion(<urn:people#duck> <urn:people#Louie>)\n"
            + "ClassAssertion(<urn:people#male> <urn:people#Mick>)\n"
            + "ObjectPropertyAssertion(<urn:people#drives> <urn:people#Mick> <urn:people#Q123_ABC>)\n"
            + "ObjectPropertyAssertion(<urn:people#reads> <urn:people#Mick> <urn:people#Daily_Mirror>)\n"
            + "ClassAssertion(<urn:people#elderly> <urn:people#Minnie>)\n"
            + "ClassAssertion(<urn:people#female> <urn:people#Minnie>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Minnie> <urn:people#Tom>)\n"
            + "ClassAssertion(owl:Thing <urn:people#Pete>)\n"
            + "ClassAssertion(<urn:people#van> <urn:people#Q123_ABC>)\n"
            + "ClassAssertion(<urn:people#white_thing> <urn:people#Q123_ABC>)\n"
            + "ClassAssertion(<urn:people#dog> <urn:people#Rex>)\n"
            + "ObjectPropertyAssertion(<urn:people#is_pet_of> <urn:people#Rex> <urn:people#Mick>)\n"
            + "ClassAssertion(owl:Thing <urn:people#Spike>)\n"
            + "ObjectPropertyAssertion(<urn:people#is_pet_of> <urn:people#Spike> <urn:people#Pete>)\n"
            + "ClassAssertion(<urn:people#broadsheet> <urn:people#The_Guardian>)\n"
            + "ClassAssertion(<urn:people#tabloid> <urn:people#The_Sun>)\n"
            + "ClassAssertion(<urn:people#broadsheet> <urn:people#The_Times>)\n"
            + "ClassAssertion(<urn:people#cat> <urn:people#Tibbs>)\n"
            + "ClassAssertion(owl:Thing <urn:people#Tom>)\n"
            + "ClassAssertion(<urn:people#person> <urn:people#Walt>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Walt> <urn:people#Louie>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Walt> <urn:people#Dewey>)\n"
            + "ObjectPropertyAssertion(<urn:people#has_pet> <urn:people#Walt> <urn:people#Huey>)\n"
            + "DifferentIndividuals(<urn:people#Dewey> <urn:people#Fido> <urn:people#Flossie> <urn:people#Fluffy> <urn:people#Fred> <urn:people#Huey> <urn:people#Joe> <urn:people#Kevin> <urn:people#Louie> <urn:people#Mick> <urn:people#Minnie> <urn:people#Q123_ABC> <urn:people#Rex> <urn:people#The_Guardian> <urn:people#The_Sun> <urn:people#The_Times> <urn:people#Tibbs> <urn:people#Walt>)\n"
            + "DisjointClasses(ObjectUnionOf(ObjectSomeValuesFrom(<urn:people#part_of> <urn:people#plant>) <urn:people#plant>) ObjectUnionOf(ObjectSomeValuesFrom(<urn:people#part_of> <urn:people#animal>) <urn:people#animal>)))";

    @Override
    protected String input() {
        return "/AF_people.owl.xml";
    }

    @Override
    protected OWLOntology load(String input)
            throws OWLOntologyCreationException {
        return loadFromString(in);
    }

    OWLClass mad_cow = C("urn:people#mad_cow");
    OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
    OWLClass truck = C("urn:people#truck");
    OWLClass lorry = C("urn:people#lorry");
    OWLClass white_van_man = C("urn:people#white_van_man");
    OWLClass car = C("urn:people#car");
    OWLClass tiger = C("urn:people#tiger");
    OWLClass cat = C("urn:people#cat");
    OWLClass van = C("urn:people#van");
    OWLClass magazine = C("urn:people#magazine");
    OWLClass pet = C("urn:people#pet");
    OWLClass dog = C("urn:people#dog");
    OWLClass bus_driver = C("urn:people#bus_driver");
    OWLClass haulage_truck_driver = C("urn:people#haulage_truck_driver");
    OWLClass cow = C("urn:people#cow");
    OWLClass tree = C("urn:people#tree");
    OWLClass dog_owner = C("urn:people#dog_owner");
    OWLClass giraffe = C("urn:people#giraffe");
    OWLClass red_top = C("urn:people#red_top");
    OWLClass brain = C("urn:people#brain");
    OWLClass bus = C("urn:people#bus");
    OWLClass quality_broadsheet = C("urn:people#quality_broadsheet");
    OWLClass lorry_driver = C("urn:people#lorry_driver");
    OWLClass bicycle = C("urn:people#bicycle");
    OWLClass sheep = C("urn:people#sheep");
    OWLClass old_lady = C("urn:people#old_lady");
    OWLClass kid = C("urn:people#kid");
    OWLClass bus_company = C("urn:people#bus_company");
    OWLClass duck = C("urn:people#duck");
    OWLClass haulage_company = C("urn:people#haulage_company");
    OWLClass animal_lover = C("urn:people#animal_lover");
    OWLClass white_thing = C("urn:people#white_thing");
    OWLClass leaf = C("urn:people#leaf");
    OWLClass bone = C("urn:people#bone");
    OWLClass grass = C("urn:people#grass");
    OWLClass animal = C("urn:people#animal");
    OWLClass pet_owner = C("urn:people#pet_owner");
    OWLClass cat_owner = C("urn:people#cat_owner");
    OWLNamedIndividual Fluffy = I("urn:people#Fluffy");
    OWLNamedIndividual Fred = I("urn:people#Fred");
    OWLNamedIndividual Kevin = I("urn:people#Kevin");
    OWLNamedIndividual Spike = I("urn:people#Spike");
    OWLNamedIndividual Tom = I("urn:people#Tom");
    OWLNamedIndividual Huey = I("urn:people#Huey");
    OWLNamedIndividual Joe = I("urn:people#Joe");
    OWLNamedIndividual Walt = I("urn:people#Walt");
    OWLNamedIndividual Dewey = I("urn:people#Dewey");
    OWLNamedIndividual Louie = I("urn:people#Louie");
    OWLNamedIndividual Mick = I("urn:people#Mick");
    OWLNamedIndividual Minnie = I("urn:people#Minnie");
    OWLNamedIndividual Pete = I("urn:people#Pete");
    OWLNamedIndividual Rex = I("urn:people#Rex");
    OWLNamedIndividual Tibbs = I("urn:people#Tibbs");
    OWLNamedIndividual Fido = I("urn:people#Fido");

    @Test
    public void shouldPassgetSubClassestigertrue() {
        equal(reasoner.getSubClasses(tiger, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClassescattrue() {
        equal(reasoner.getSubClasses(cat, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassIsSatisfiableMadCow() {
        assertFalse(reasoner.isSatisfiable(mad_cow));
    }

    @Test
    public void shouldPassgetSubClassespettrue() {
        equal(reasoner.getSubClasses(pet, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClasseswhite_van_mantrue() {
        equal(reasoner.getSubClasses(white_van_man, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClassesvantrue() {
        equal(reasoner.getSubClasses(van, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSuperClassesmad_cowtrue() {
        // expected white_van_man, tiger, cat, van, pet, dog, cow, dog_owner,
        // old_lady, duck, animal_lover, white_thing
        // actual__ , , , , , , , , , , , , , , , , , , , ,
        equal(reasoner.getSuperClasses(mad_cow, true), truck, lorry,
                white_van_man, car, tiger, cat, van, magazine, pet, dog,
                bus_driver, haulage_truck_driver, cow, tree, dog_owner,
                giraffe, red_top, brain, bus, quality_broadsheet, lorry_driver,
                bicycle, sheep, old_lady, kid, bus_company, duck,
                haulage_company, animal_lover, white_thing, leaf, bone, grass);
    }

    @Test
    public void shouldPassgetSubClassesdogtrue() {
        equal(reasoner.getSubClasses(dog, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClassescowtrue() {
        equal(reasoner.getSubClasses(cow, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClassesdog_ownertrue() {
        equal(reasoner.getSubClasses(dog_owner, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClassesold_ladytrue() {
        equal(reasoner.getSubClasses(old_lady, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClassesducktrue() {
        equal(reasoner.getSubClasses(duck, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClassesanimal_lovertrue() {
        equal(reasoner.getSubClasses(animal_lover, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetSubClasseswhite_thingtrue() {
        equal(reasoner.getSubClasses(white_thing, true), mad_cow, Nothing);
    }

    @Test
    public void shouldPassgetTypesTomtrue() {
        equal(reasoner.getTypes(Tom, true), pet, cat);
    }

    @Test
    public void shouldPassgetTypesHueytrue() {
        equal(reasoner.getTypes(Huey, true), duck, pet);
    }

    @Test
    public void shouldPassgetTypesJoetrue() {
        equal(reasoner.getTypes(Joe, true), dog_owner);
    }

    @Test
    public void shouldPassgetTypesMicktrue() {
        equal(reasoner.getTypes(Mick, true), dog_owner, white_van_man);
    }

    @Test
    public void shouldPassgetTypesLouietrue() {
        equal(reasoner.getTypes(Louie, true), duck, pet);
    }

    @Test
    public void shouldPassgetTypesDeweytrue() {
        equal(reasoner.getTypes(Dewey, true), duck, pet);
    }

    @Test
    public void shouldPassgetTypesWalttrue() {
        equal(reasoner.getTypes(Walt, true), animal_lover);
    }

    @Test
    public void shouldPassgetTypesMinnietrue() {
        equal(reasoner.getTypes(Minnie, true), old_lady);
    }

    @Test
    public void shouldPassgetTypesPetetrue() {
        equal(reasoner.getTypes(Pete, true), pet_owner);
    }

    @Test
    public void shouldPassgetTypesRextrue() {
        equal(reasoner.getTypes(Rex, true), pet, dog);
    }

    @Test
    public void shouldPassgetTypesTibbstrue() {
        equal(reasoner.getTypes(Tibbs, true), pet, cat);
    }

    @Test
    public void shouldPassgetTypesFidotrue() {
        equal(reasoner.getTypes(Fido, true), pet, dog);
    }

    @Test
    public void shouldPassgetTypesFluffytrue() {
        equal(reasoner.getTypes(Fluffy, true), pet, tiger);
    }

    @Test
    public void shouldPassgetTypesFredtrue() {
        equal(reasoner.getTypes(Fred, true), cat_owner);
    }

    @Test
    public void shouldPassgetTypesKevintrue() {
        equal(reasoner.getTypes(Kevin, true), pet_owner);
    }

    @Test
    public void shouldPassgetTypesSpiketrue() {
        equal(reasoner.getTypes(Spike, true), pet);
    }
}
