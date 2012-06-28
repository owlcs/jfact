package conformance.broken;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import conformance.Factory;

public class somevaluesfrom2bnode {
    @Test
    public void testsomevaluesfrom2bnode() throws OWLOntologyCreationException {
        //		String premise = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  \n"
        //				+ "          xmlns:ex=\"http://example.org/\"\n"
        //				+ "          xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        //				+ "          xml:base=\"http://example.org/\">\n"
        //				+ "  <owl:Ontology />\n"
        //				+ "  <owl:ObjectProperty rdf:about=\"p\"/>\n"
        //				+ "  <rdf:Description rdf:about=\"a\">\n"
        //				+ "        <rdf:type>\n"
        //				+ "            <owl:Restriction>\n"
        //				+ "                <owl:onProperty rdf:resource=\"p\"/>\n"
        //				+ "                <owl:someValuesFrom rdf:resource=\"http://www.w3.org/2002/07/owl#Thing\"/>\n"
        //				+ "            </owl:Restriction>\n"
        //				+ "        </rdf:type>\n"
        //				+ "    </rdf:Description>\n"
        //				+ "</rdf:RDF>";
        //		String conclusion = "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  \n"
        //				+ "          xmlns:ex=\"http://example.org/\"\n"
        //				+ "          xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
        //				+ "          xml:base=\"http://example.org/\">\n"
        //				+ "  <owl:Ontology />\n"
        //				+ "  <owl:ObjectProperty rdf:about=\"p\"/>\n"
        //				+ "  <rdf:Description rdf:about=\"a\">\n"
        //				+ "    <ex:p><rdf:Description/></ex:p> \n"
        //				+ "  </rdf:Description>\n" + "</rdf:RDF>";
        //
        //		String id = "somevaluesfrom2bnode";
        //		TestClasses tc = TestClasses.valueOf("POSITIVE_IMPL");
        //		String d = "Shows that a BNode is an existential variable.";
        //		JUnitRunner r = new JUnitRunner(premise, conclusion, id, tc, d);
        //		r.setReasonerFactory(Factory.factory());
        //		r.printPremise();
        //		r.printConsequence();
        //		r.run();
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLDataFactory f = m.getOWLDataFactory();
        OWLOntology o = m.createOntology();
        OWLObjectProperty p = f.getOWLObjectProperty(IRI.create("urn:p"));
        final OWLNamedIndividual a = f.getOWLNamedIndividual(IRI.create("urn:a"));
        final OWLObjectSomeValuesFrom c = f
                .getOWLObjectSomeValuesFrom(p, f.getOWLThing());
        m.addAxiom(o, f.getOWLClassAssertionAxiom(c, a));
        OWLReasoner r = Factory.factory().createReasoner(o);
        assertTrue(r.isEntailed(f.getOWLObjectPropertyAssertionAxiom(p, a,
                f.getOWLAnonymousIndividual())));
    }
}
