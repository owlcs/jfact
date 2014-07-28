package bugs.debug;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import bugs.VerifyComplianceBase;

@SuppressWarnings("javadoc")
public class JFactTest2 extends VerifyComplianceBase {

    String in = "Prefix(:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "Ontology(\n"
            + "Declaration(Class(<urn:Service.owl#Service>))\n"
            + "Declaration(ObjectProperty(<urn:Service.owl#isPresentedBy>))\n"
            + "Declaration(ObjectProperty(<urn:Service.owl#presentedBy>))\n\n"
            + "EquivalentObjectProperties(<urn:Service.owl#isPresentedBy> <urn:Service.owl#presentedBy>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#presentedBy> <urn:Service.owl#Service>)\n"
            + ")";

    @Override
    protected OWLOntology load(String input)
            throws OWLOntologyCreationException {
        OWLOntology onto = OWLManager.createOWLOntologyManager()
                .loadOntologyFromOntologyDocument(new StringDocumentSource(in));
        return onto;
    }

    @Override
    protected String input() {
        return "";
    }

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedByfalse() {
        // reasoner.getConfiguration().setAbsorptionLoggingActive(true)
        // .setUSE_REASONING_STATISTICS(true).setLoggingActive(true);
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Service = C("urn:Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("urn:Service.owl#isPresentedBy");
        // expected Thing, Service
        // actual__ isPresentedBy, false
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, false), Thing,
                Service);
    }
}
