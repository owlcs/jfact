package bugs;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

@SuppressWarnings("javadoc")
public class VerifyComplianceOWLSNewFeatures extends VerifyComplianceBase {

    @Override
    protected String input() {
        return "/AF_OWLS.owl.xml";
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasOutput1() {
        OWLObjectProperty hasLocal = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasLocal");
        OWLObjectProperty hasInput = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasInput");
        OWLObjectProperty hasInput1 = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasInput");
        OWLObjectProperty hasResultVar = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasResultVar");
        OWLObjectProperty hasOutput = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasOutput");
        // expected hasLocal, hasInput, hasInput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasOutput
        equal(reasoner.getDisjointObjectProperties(hasOutput), hasLocal,
                hasInput, hasInput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasInput2() {
        OWLObjectProperty hasOutput = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasOutput");
        OWLObjectProperty hasLocal = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasLocal");
        OWLObjectProperty hasOutput1 = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasOutput");
        OWLObjectProperty hasResultVar = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasResultVar");
        OWLObjectProperty hasInput = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasInput");
        // expected hasOutput, hasLocal, hasOutput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasInput
        equal(reasoner.getDisjointObjectProperties(hasInput), hasOutput,
                hasLocal, hasOutput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasResultVar() {
        OWLObjectProperty hasOutput = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasOutput");
        OWLObjectProperty hasLocal = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasLocal");
        OWLObjectProperty hasInput = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasInput");
        OWLObjectProperty hasOutput1 = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasOutput");
        OWLObjectProperty hasInput1 = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasInput");
        OWLObjectProperty hasResultVar = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasResultVar");
        // expected hasOutput, hasLocal, hasInput, hasOutput, hasInput,
        // bottomObjectProperty
        // actual__ hasResultVar
        equal(reasoner.getDisjointObjectProperties(hasResultVar), hasOutput,
                hasLocal, hasInput, hasOutput1, hasInput1, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasLocal() {
        OWLObjectProperty hasOutput = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasOutput");
        OWLObjectProperty hasInput = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasInput");
        OWLObjectProperty hasOutput1 = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasOutput");
        OWLObjectProperty hasInput1 = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasInput");
        OWLObjectProperty hasResultVar = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasResultVar");
        OWLObjectProperty hasLocal = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasLocal");
        // expected hasOutput, hasInput, hasOutput, hasInput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasLocal
        equal(reasoner.getDisjointObjectProperties(hasLocal), hasOutput,
                hasInput, hasOutput1, hasInput1, hasResultVar,
                bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasInput() {
        OWLObjectProperty hasOutput = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasOutput");
        OWLObjectProperty hasLocal = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasLocal");
        OWLObjectProperty hasOutput1 = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasOutput");
        OWLObjectProperty hasResultVar = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasResultVar");
        OWLObjectProperty hasInput = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasInput");
        // expected hasOutput, hasLocal, hasOutput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasInput
        equal(reasoner.getDisjointObjectProperties(hasInput), hasOutput,
                hasLocal, hasOutput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedByfalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Service = C("http://www.daml.org/services/owl-s/1.1/Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("http://www.daml.org/services/owl-s/1.1/Service.owl#isPresentedBy");
        // expected Thing, Service
        // actual__ isPresentedBy, false
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, false), Thing,
                Service);
    }

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedBytrue() {
        OWLClass Service = C("http://www.daml.org/services/owl-s/1.1/Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("http://www.daml.org/services/owl-s/1.1/Service.owl#isPresentedBy");
        // expected Service
        // actual__ isPresentedBy, true
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, true), Service);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointObjectPropertieshasOutput() {
        OWLObjectProperty hasLocal = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasLocal");
        OWLObjectProperty hasInput = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasInput");
        OWLObjectProperty hasInput1 = OP("http://www.daml.org/services/owl-s/1.1/Profile.owl#hasInput");
        OWLObjectProperty hasResultVar = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasResultVar");
        OWLObjectProperty hasOutput = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#hasOutput");
        // expected hasLocal, hasInput, hasInput, hasResultVar,
        // bottomObjectProperty
        // actual__ hasOutput
        equal(reasoner.getDisjointObjectProperties(hasOutput), hasLocal,
                hasInput, hasInput1, hasResultVar, bottomObjectProperty);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesserviceProduct() {
        OWLDataProperty valueForm = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        OWLDataProperty parameterValue = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        OWLDataProperty valueFunction = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty serviceProduct = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ serviceProduct
        equal(reasoner.getDisjointDataProperties(serviceProduct), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesserviceClassification() {
        OWLDataProperty valueForm = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        OWLDataProperty parameterValue = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        OWLDataProperty valueFunction = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty serviceClassification = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ serviceClassification
        equal(reasoner.getDisjointDataProperties(serviceClassification),
                valueForm, parameterValue, bottomDataProperty, valueFunction,
                invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesrefURI() {
        OWLDataProperty valueForm = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        OWLDataProperty parameterValue = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        OWLDataProperty valueFunction = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ refURI
        equal(reasoner.getDisjointDataProperties(refURI), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesvalueFunction() {
        OWLDataProperty serviceProduct = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        OWLDataProperty serviceClassification = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        OWLDataProperty valueType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        OWLDataProperty parameterType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty valueFunction = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        // expected serviceProduct, serviceClassification, refURI,
        // bottomDataProperty, valueType, parameterType, invocable
        // actual__ valueFunction
        equal(reasoner.getDisjointDataProperties(valueFunction),
                serviceProduct, serviceClassification, refURI,
                bottomDataProperty, valueType, parameterType, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesvalueType() {
        OWLDataProperty valueForm = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        OWLDataProperty parameterValue = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        OWLDataProperty valueFunction = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty valueType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ valueType
        equal(reasoner.getDisjointDataProperties(valueType), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesinvocable() {
        OWLDataProperty serviceProduct = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        OWLDataProperty valueForm = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        OWLDataProperty serviceClassification = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        OWLDataProperty parameterValue = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        OWLDataProperty valueFunction = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        OWLDataProperty valueType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        OWLDataProperty parameterType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        // expected serviceProduct, valueForm, serviceClassification,
        // parameterValue, refURI, bottomDataProperty, valueFunction, valueType,
        // parameterType
        // actual__ invocable
        equal(reasoner.getDisjointDataProperties(invocable), serviceProduct,
                valueForm, serviceClassification, parameterValue, refURI,
                bottomDataProperty, valueFunction, valueType, parameterType);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesvalueForm() {
        OWLDataProperty serviceProduct = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        OWLDataProperty serviceClassification = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        OWLDataProperty valueType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        OWLDataProperty parameterType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty valueForm = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        // expected serviceProduct, serviceClassification, refURI,
        // bottomDataProperty, valueType, parameterType, invocable
        // actual__ valueForm
        equal(reasoner.getDisjointDataProperties(valueForm), serviceProduct,
                serviceClassification, refURI, bottomDataProperty, valueType,
                parameterType, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesparameterValue() {
        OWLDataProperty serviceProduct = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        OWLDataProperty serviceClassification = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        OWLDataProperty valueType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        OWLDataProperty parameterType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty parameterValue = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        // expected serviceProduct, serviceClassification, refURI,
        // bottomDataProperty, valueType, parameterType, invocable
        // actual__ parameterValue
        equal(reasoner.getDisjointDataProperties(parameterValue),
                serviceProduct, serviceClassification, refURI,
                bottomDataProperty, valueType, parameterType, invocable);
    }

    @Test
    @Ignore("disjoint properties not supported")
    public void shouldPassgetDisjointDataPropertiesparameterType() {
        OWLDataProperty valueForm = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        OWLDataProperty parameterValue = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        OWLDataProperty valueFunction = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        OWLDataProperty invocable = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        OWLDataProperty parameterType = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        // expected valueForm, parameterValue, bottomDataProperty,
        // valueFunction, invocable
        // actual__ parameterType
        equal(reasoner.getDisjointDataProperties(parameterType), valueForm,
                parameterValue, bottomDataProperty, valueFunction, invocable);
    }

    @Test
    public void shouldPassgetDataPropertyValuesKIFrefURI() {
        OWLNamedIndividual KIF = df
                .getOWLNamedIndividual(IRI
                        .create("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#KIF"));
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
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
        OWLNamedIndividual DRS = df
                .getOWLNamedIndividual(IRI
                        .create("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#DRS"));
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
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
        OWLNamedIndividual SWRL = df
                .getOWLNamedIndividual(IRI
                        .create("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#SWRL"));
        OWLDataProperty refURI = DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        // expected ["http://www.w3.org/2003/11/swrl"^^xsd:anyURI]
        // actual__ SWRL, refURI
        assertEquals(
                reasoner.getDataPropertyValues(SWRL, refURI),
                new HashSet<>(Arrays.asList(df
                        .getOWLLiteral("http://www.w3.org/2003/11/swrl"))));
    }
}
