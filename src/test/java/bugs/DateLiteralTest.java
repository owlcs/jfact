package bugs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import uk.ac.manchester.cs.jfact.JFactFactory;

class DateLiteralTest {
    @Test
    void shouldTruncateToMillisecondsButAccebtArbitraryPrecision()
        throws OWLOntologyCreationException {
        String s = "Prefix(:=<http://www.ubbc.co.uk/ontologies/hermitBug#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "Ontology(<http://www.ubbc.co.uk/ontologies/hermitBug>\n" + "\n"
            + "Declaration(Class(:C))\n" + "Declaration(Class(:D))\n"
            + "Declaration(DataProperty(:hasDate))\n" + "Declaration(NamedIndividual(:test))\n"
            + "Declaration(NamedIndividual(:test2))\n" + "Declaration(NamedIndividual(:test3))\n"
            + "Declaration(NamedIndividual(:test4))\n" + "Declaration(NamedIndividual(:test5))\n"
            + "Declaration(NamedIndividual(:today))\n" + "\n" + "############################\n"
            + "#   Named Individuals\n" + "############################\n" + "\n"
            + "# Individual: :test (:test)\n"
            + "DataPropertyAssertion(:hasDate :test \"2020-01-07T12:32:00Z\"^^xsd:dateTime)\n"
            + "# Individual: :test2 (:test2)\n"
            + "DataPropertyAssertion(:hasDate :test2 \"2020-01-07T11:22:33-01:00\"^^xsd:dateTimeStamp)\n"
            + "# Individual: :test3 (:test3)\n"
            + "DataPropertyAssertion(:hasDate :test3 \"2020-02-02T00:00:00\"^^xsd:dateTime)\n"
            + "# Individual: :test4 (:test4)\n"
            + "DataPropertyAssertion(:hasDate :test4 \"2020-01-07T11:32:00-01:00\"^^xsd:dateTime)\n"
            + "# Individual: :test5 (:test5)\n"
            + "DataPropertyAssertion(:hasDate :test5 \"2020-01-07T11:32:00+01:00\"^^xsd:dateTime)\n"
            + "SubClassOf(DataSomeValuesFrom(:hasDate DataOneOf(\"2020-01-07T12:32:00+00:00\"^^xsd:dateTime)) :D)\n"
            + "SubClassOf(DataSomeValuesFrom(:hasDate DatatypeRestriction(xsd:dateTime xsd:minExclusive \"2020-01-31T00:00:00\"^^xsd:dateTime)) :C)\n"
            + ")";
        // DateTime uses millisecond precision, regardless of precision of
        // input.
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = man.getOWLDataFactory();
        OWLNamedIndividual i =
            df.getOWLNamedIndividual(IRI.create("http://www.ubbc.co.uk/ontologies/hermitBug#test"));
        OWLClass c = df.getOWLClass(IRI.create("http://www.ubbc.co.uk/ontologies/hermitBug#D"));
        OWLOntology ont =
            man.loadOntologyFromOntologyDocument(new ByteArrayInputStream(s.getBytes()));

        OWLReasoner reasoner = new JFactFactory().createReasoner(ont);
        assertTrue(reasoner.isConsistent());
        assertTrue(reasoner.getTypes(i).containsEntity(c));
    }
}
