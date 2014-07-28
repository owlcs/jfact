package bugs.debug;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import bugs.VerifyComplianceOWLSNewFeatures;

@SuppressWarnings("javadoc")
public class VerifyComplianceOWLSNewFeaturesBroken extends
        VerifyComplianceOWLSNewFeatures {

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedBytrue() {
        OWLClass Service = C("http://www.daml.org/services/owl-s/1.1/Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("urn:Service.owl#isPresentedBy");
        // expected Service
        // actual__ isPresentedBy, true
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, true), Service);
    }

    @Test
    public void shouldPassgetDataPropertyValuesKIFrefURI() {
        OWLNamedIndividual KIF = df.getOWLNamedIndividual(IRI
                .create("urn:expr#KIF"));
        OWLDataProperty refURI = DP("urn:expr#refURI");
        // expected ["http://logic.stanford.edu/kif/kif.html"^^xsd:anyURI]
        // actual__ KIF, refURI
        assertEquals(
                reasoner.getDataPropertyValues(KIF, refURI),
                new HashSet<>(
                        Arrays.asList(df
                                .getOWLLiteral("http://logic.stanford.edu/kif/kif.html"))));
    }

    @Test
    public void shouldPassgetDataPropertyValuesDRSrefURI() {
        OWLNamedIndividual DRS = df.getOWLNamedIndividual(IRI
                .create("urn:expr#DRS"));
        OWLDataProperty refURI = DP("urn:expr#refURI");
        // expected
        // ["http://www.daml.org/services/owl-s/1.1/generic/drs.owl"^^xsd:anyURI]
        // actual__ DRS, refURI
        assertEquals(
                reasoner.getDataPropertyValues(DRS, refURI),
                new HashSet<>(
                        Arrays.asList(df
                                .getOWLLiteral("http://www.daml.org/services/owl-s/1.1/generic/drs.owl"))));
    }

    @Test
    public void shouldPassgetDataPropertyValuesSWRLrefURI() {
        OWLNamedIndividual SWRL = df.getOWLNamedIndividual(IRI
                .create("urn:expr#SWRL"));
        OWLDataProperty refURI = DP("urn:expr#refURI");
        // expected ["http://www.w3.org/2003/11/swrl"^^xsd:anyURI]
        // actual__ SWRL, refURI
        assertEquals(
                reasoner.getDataPropertyValues(SWRL, refURI),
                new HashSet<>(Arrays.asList(df
                        .getOWLLiteral("http://www.w3.org/2003/11/swrl"))));
    }
}
