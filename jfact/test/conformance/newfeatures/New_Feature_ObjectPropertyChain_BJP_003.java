package conformance.newfeatures;

import org.junit.Test;

import conformance.Factory;
import conformance.JUnitRunner;
import conformance.TestClasses;

public class New_Feature_ObjectPropertyChain_BJP_003 {
    @Test
    public void testNew_Feature_ObjectPropertyChain_BJP_003() {
        //		OWLOntologyManager m=OWLManager.createOWLOntologyManager();
        //		OWLOntology o=m.createOntology();
        //		OWLDataFactory f=m.getOWLDataFactory();
        //		OWLObjectProperty p=f.getOWLObjectProperty(IRI.create("urn:test#p"));
        //		OWLNamedIndividual a=f.getOWLNamedIndividual(IRI.create("urn:test#a"));
        //		OWLNamedIndividual c=f.getOWLNamedIndividual(IRI.create("urn:test#c"));
        //		m.addAxiom(o, f.getOWLObjectPropertyAssertionAxiom(p, a, c));
        //		m.addAxiom(o, f.getOWLDeclarationAxiom(p));
        //		StringDocumentTarget t=new StringDocumentTarget();
        //	m.saveOntology(o, t);
        //		System.out.println(t);
        //		OWL2DLProfile profile = new OWL2DLProfile();
        //		OWLProfileReport report = profile
        //				.checkOntology(o);
        //		if (report.getViolations().size() > 0) {
        //			System.out
        //					.println("violations:\n"
        //							+ report.toString());
        //
        //		}
        //XXX test modified because of ontology not compliant with OWL 2
        String premise = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "    xml:base  = \"http://example.org/\"\n"
                + "    xmlns     = \"http://example.org/\"\n"
                + "    xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:rdfs= \"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "    xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "\n"
                + "<owl:Ontology/>\n"
                + "\n"
                //added
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org/p\"/>\n"
                //XXX this is a bug, needs to be fixed
                + "<owl:Thing rdf:about=\"http://www.example.org/a\"/>\n"
                + "<owl:Thing rdf:about=\"http://www.example.org/c\"/>\n"
                //end added
                + "    <rdf:Description rdf:about=\"p\">\n"
                + "        <owl:propertyChainAxiom rdf:parseType=\"Collection\">\n"
                + "            <owl:ObjectProperty rdf:about=\"p\"/>\n"
                + "            <owl:ObjectProperty rdf:about=\"q\"/>\n"
                + "        </owl:propertyChainAxiom>\n" + "    </rdf:Description>\n"
                + "    \n" + "    <rdf:Description rdf:about=\"a\">\n"
                + "        <p rdf:resource=\"b\"/>\n" + "    </rdf:Description>\n"
                + "    \n" + "    <rdf:Description rdf:about=\"b\">\n"
                + "        <q rdf:resource=\"c\"/>\n" + "    </rdf:Description>\n"
                + "   \n" + "</rdf:RDF>";
        //XXX this should parse equal to the second example but does not
        String conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF\n"
                + "    xml:base  = \"http://example.org/\"\n"
                + "    xmlns     = \"http://example.org/\"\n"
                + "    xmlns:owl = \"http://www.w3.org/2002/07/owl#\"\n"
                + "    xmlns:rdf = \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "\n"
                + "    <owl:Ontology/>\n"
                //added
                + "<owl:ObjectProperty rdf:about=\"http://www.example.org/p\"/>\n"
                //end added
                + "    <owl:NamedIndividual rdf:about=\"a\">\n"
                + "        <p rdf:resource=\"c\"/>\n" + "    </owl:NamedIndividual>\n"
                + "\n" + "</rdf:RDF>";
        conclusion = "<?xml version=\"1.0\"?>\n"
                + "<rdf:RDF xmlns=\"http://www.w3.org/2002/07/owl#\"\n"
                + "xml:base=\"http://www.w3.org/2002/07/owl\"\n"
                + "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
                + "xmlns:test=\"http://www.example.org/\"\n"
                + "xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
                + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n"
                + "<ObjectProperty rdf:about=\"http://www.example.org/p\"/>\n"
                + "<NamedIndividual rdf:about=\"http://www.example.org/a\">\n"
                + "<test:p rdf:resource=\"http://www.example.org/c\"/>\n"
                + "</NamedIndividual>\n" + "</rdf:RDF>";
        String id = "New_Feature_ObjectPropertyChain_BJP_003";
        TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        String d = "A simple test of role chains and role hierarchy.";
        JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        r.setReasonerFactory(Factory.factory());
        r.run();
    }
}
