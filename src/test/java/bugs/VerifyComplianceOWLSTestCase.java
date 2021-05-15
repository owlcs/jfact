package bugs;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

class VerifyComplianceOWLSTestCase extends VerifyComplianceBase {

    @Override
    protected String input() {
        return "/AF_OWLS.owl.xml";
    }

    @Test
    void shouldPassgetSuperObjectPropertiesisSupportedByfalse() {
        OWLObjectProperty isSupportedBy =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#isSupportedBy");
        // expected topObjectProperty
        // actual__ isSupportedBy, false
        equal(reasoner.getSuperObjectProperties(isSupportedBy, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiesisSupportedBytrue() {
        OWLObjectProperty isSupportedBy =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#isSupportedBy");
        // expected topObjectProperty
        // actual__ isSupportedBy, true
        equal(reasoner.getSuperObjectProperties(isSupportedBy, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiescollapsesTofalse() {
        OWLObjectProperty collapsesTo =
            OP("http://www.daml.org/services/owl-s/1.1/Process.owl#collapsesTo");
        // expected topObjectProperty
        // actual__ collapsesTo, false
        equal(reasoner.getSuperObjectProperties(collapsesTo, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiescollapsesTotrue() {
        OWLObjectProperty collapsesTo =
            OP("http://www.daml.org/services/owl-s/1.1/Process.owl#collapsesTo");
        // expected topObjectProperty
        // actual__ collapsesTo, true
        equal(reasoner.getSuperObjectProperties(collapsesTo, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiescollapsefalse() {
        OWLObjectProperty collapse =
            OP("http://www.daml.org/services/owl-s/1.1/Process.owl#collapse");
        // expected topObjectProperty
        // actual__ collapse, false
        equal(reasoner.getSuperObjectProperties(collapse, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiescollapsetrue() {
        OWLObjectProperty collapse =
            OP("http://www.daml.org/services/owl-s/1.1/Process.owl#collapse");
        // expected topObjectProperty
        // actual__ collapse, true
        equal(reasoner.getSuperObjectProperties(collapse, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiessupportsfalse() {
        OWLObjectProperty supports =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#supports");
        // expected topObjectProperty
        // actual__ supports, false
        equal(reasoner.getSuperObjectProperties(supports, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiessupportstrue() {
        OWLObjectProperty supports =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#supports");
        // expected topObjectProperty
        // actual__ supports, true
        equal(reasoner.getSuperObjectProperties(supports, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiespresentsfalse() {
        OWLObjectProperty presents =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#presents");
        // expected topObjectProperty
        // actual__ presents, false
        equal(reasoner.getSuperObjectProperties(presents, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiespresentstrue() {
        OWLObjectProperty presents =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#presents");
        // expected topObjectProperty
        // actual__ presents, true
        equal(reasoner.getSuperObjectProperties(presents, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiesexpandfalse() {
        OWLObjectProperty expand = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#expand");
        // expected topObjectProperty
        // actual__ expand, false
        equal(reasoner.getSuperObjectProperties(expand, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiesexpandtrue() {
        OWLObjectProperty expand = OP("http://www.daml.org/services/owl-s/1.1/Process.owl#expand");
        // expected topObjectProperty
        // actual__ expand, true
        equal(reasoner.getSuperObjectProperties(expand, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiesexpandsTofalse() {
        OWLObjectProperty expandsTo =
            OP("http://www.daml.org/services/owl-s/1.1/Process.owl#expandsTo");
        // expected topObjectProperty
        // actual__ expandsTo, false
        equal(reasoner.getSuperObjectProperties(expandsTo, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiesexpandsTotrue() {
        OWLObjectProperty expandsTo =
            OP("http://www.daml.org/services/owl-s/1.1/Process.owl#expandsTo");
        // expected topObjectProperty
        // actual__ expandsTo, true
        equal(reasoner.getSuperObjectProperties(expandsTo, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiessupportedByfalse() {
        OWLObjectProperty supportedBy =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#supportedBy");
        // expected topObjectProperty
        // actual__ supportedBy, false
        equal(reasoner.getSuperObjectProperties(supportedBy, false), topObjectProperty);
    }

    @Test
    void shouldPassgetSuperObjectPropertiessupportedBytrue() {
        OWLObjectProperty supportedBy =
            OP("http://www.daml.org/services/owl-s/1.1/Service.owl#supportedBy");
        // expected topObjectProperty
        // actual__ supportedBy, true
        equal(reasoner.getSuperObjectProperties(supportedBy, true), topObjectProperty);
    }

    @Test
    void shouldPassgetSubClassesParticipanttrue() {
        OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
        OWLClass Participant = C("http://www.daml.org/services/owl-s/1.1/Process.owl#Participant");
        // expected Nothing
        // actual__ Participant, true
        equal(reasoner.getSubClasses(Participant, true), Nothing);
    }

    @Test
    void shouldPassgetSubClassesSWRLConditiontrue() {
        OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
        OWLClass SWRLCondition =
            C("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#SWRL-Condition");
        // expected Nothing
        // actual__ SWRL-Condition, true
        equal(reasoner.getSubClasses(SWRLCondition, true), Nothing);
    }

    @Test
    void shouldPassgetSubClassesLogicLanguagetrue() {
        OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
        OWLClass LogicLanguage =
            C("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#LogicLanguage");
        // expected Nothing
        // actual__ LogicLanguage, true
        equal(reasoner.getSubClasses(LogicLanguage, true), Nothing);
    }

    @Test
    void shouldPassgetSubClassesPerformtrue() {
        OWLClass Nothing = C("http://www.w3.org/2002/07/owl#Nothing");
        OWLClass Perform = C("http://www.daml.org/services/owl-s/1.1/Process.owl#Perform");
        // expected Nothing
        // actual__ Perform, true
        equal(reasoner.getSubClasses(Perform, true), Nothing);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceProductfalse() {
        OWLDataProperty serviceProduct =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        // expected bottomDataProperty
        // actual__ serviceProduct, false
        equal(reasoner.getSubDataProperties(serviceProduct, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceProductfalse() {
        OWLDataProperty serviceProduct =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        // expected topDataProperty
        // actual__ serviceProduct, false
        equal(reasoner.getSuperDataProperties(serviceProduct, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceProducttrue() {
        OWLDataProperty serviceProduct =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        // expected bottomDataProperty
        // actual__ serviceProduct, true
        equal(reasoner.getSubDataProperties(serviceProduct, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceProducttrue() {
        OWLDataProperty serviceProduct =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceProduct");
        // expected topDataProperty
        // actual__ serviceProduct, true
        equal(reasoner.getSuperDataProperties(serviceProduct, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiescodefalse() {
        OWLDataProperty code = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#code");
        // expected bottomDataProperty
        // actual__ code, false
        equal(reasoner.getSubDataProperties(code, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiescodefalse() {
        OWLDataProperty code = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#code");
        // expected topDataProperty
        // actual__ code, false
        equal(reasoner.getSuperDataProperties(code, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainscodefalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty code = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#code");
        // expected Thing, ServiceCategory
        // actual__ code, false
        equal(reasoner.getDataPropertyDomains(code, false), Thing, ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiescodetrue() {
        OWLDataProperty code = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#code");
        // expected bottomDataProperty
        // actual__ code, true
        equal(reasoner.getSubDataProperties(code, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiescodetrue() {
        OWLDataProperty code = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#code");
        // expected topDataProperty
        // actual__ code, true
        equal(reasoner.getSuperDataProperties(code, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainscodetrue() {
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty code = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#code");
        // expected ServiceCategory
        // actual__ code, true
        equal(reasoner.getDataPropertyDomains(code, true), ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceClassificationfalse() {
        OWLDataProperty serviceClassification =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        // expected bottomDataProperty
        // actual__ serviceClassification, false
        equal(reasoner.getSubDataProperties(serviceClassification, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceClassificationfalse() {
        OWLDataProperty serviceClassification =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        // expected topDataProperty
        // actual__ serviceClassification, false
        equal(reasoner.getSuperDataProperties(serviceClassification, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceClassificationtrue() {
        OWLDataProperty serviceClassification =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        // expected bottomDataProperty
        // actual__ serviceClassification, true
        equal(reasoner.getSubDataProperties(serviceClassification, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceClassificationtrue() {
        OWLDataProperty serviceClassification =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceClassification");
        // expected topDataProperty
        // actual__ serviceClassification, true
        equal(reasoner.getSuperDataProperties(serviceClassification, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesrefURIfalse() {
        OWLDataProperty refURI =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        // expected bottomDataProperty
        // actual__ refURI, false
        equal(reasoner.getSubDataProperties(refURI, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesrefURIfalse() {
        OWLDataProperty refURI =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        // expected topDataProperty
        // actual__ refURI, false
        equal(reasoner.getSuperDataProperties(refURI, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesrefURItrue() {
        OWLDataProperty refURI =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        // expected bottomDataProperty
        // actual__ refURI, true
        equal(reasoner.getSubDataProperties(refURI, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesrefURItrue() {
        OWLDataProperty refURI =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#refURI");
        // expected topDataProperty
        // actual__ refURI, true
        equal(reasoner.getSuperDataProperties(refURI, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceParameterNamefalse() {
        OWLDataProperty serviceParameterName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceParameterName");
        // expected bottomDataProperty
        // actual__ serviceParameterName, false
        equal(reasoner.getSubDataProperties(serviceParameterName, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceParameterNamefalse() {
        OWLDataProperty serviceParameterName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceParameterName");
        // expected topDataProperty
        // actual__ serviceParameterName, false
        equal(reasoner.getSuperDataProperties(serviceParameterName, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsserviceParameterNamefalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass ServiceParameter =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceParameter");
        OWLDataProperty serviceParameterName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceParameterName");
        // expected Thing, ServiceParameter
        // actual__ serviceParameterName, false
        equal(reasoner.getDataPropertyDomains(serviceParameterName, false), Thing,
            ServiceParameter);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceParameterNametrue() {
        OWLDataProperty serviceParameterName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceParameterName");
        // expected bottomDataProperty
        // actual__ serviceParameterName, true
        equal(reasoner.getSubDataProperties(serviceParameterName, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceParameterNametrue() {
        OWLDataProperty serviceParameterName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceParameterName");
        // expected topDataProperty
        // actual__ serviceParameterName, true
        equal(reasoner.getSuperDataProperties(serviceParameterName, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsserviceParameterNametrue() {
        OWLClass ServiceParameter =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceParameter");
        OWLDataProperty serviceParameterName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceParameterName");
        // expected ServiceParameter
        // actual__ serviceParameterName, true
        equal(reasoner.getDataPropertyDomains(serviceParameterName, true), ServiceParameter);
    }

    @Test
    void shouldPassgetSubDataPropertiestextDescriptionfalse() {
        OWLDataProperty textDescription =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#textDescription");
        // expected bottomDataProperty
        // actual__ textDescription, false
        equal(reasoner.getSubDataProperties(textDescription, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiestextDescriptionfalse() {
        OWLDataProperty textDescription =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#textDescription");
        // expected topDataProperty
        // actual__ textDescription, false
        equal(reasoner.getSuperDataProperties(textDescription, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainstextDescriptionfalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Profile = C("http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile");
        OWLClass ServiceProfile =
            C("http://www.daml.org/services/owl-s/1.1/Service.owl#ServiceProfile");
        OWLDataProperty textDescription =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#textDescription");
        // expected Thing, Profile, ServiceProfile
        // actual__ textDescription, false
        equal(reasoner.getDataPropertyDomains(textDescription, false), Thing, Profile,
            ServiceProfile);
    }

    @Test
    void shouldPassgetSubDataPropertiestextDescriptiontrue() {
        OWLDataProperty textDescription =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#textDescription");
        // expected bottomDataProperty
        // actual__ textDescription, true
        equal(reasoner.getSubDataProperties(textDescription, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiestextDescriptiontrue() {
        OWLDataProperty textDescription =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#textDescription");
        // expected topDataProperty
        // actual__ textDescription, true
        equal(reasoner.getSuperDataProperties(textDescription, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainstextDescriptiontrue() {
        OWLClass Profile = C("http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile");
        OWLDataProperty textDescription =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#textDescription");
        // expected Profile
        // actual__ textDescription, true
        equal(reasoner.getDataPropertyDomains(textDescription, true), Profile);
    }

    @Test
    void shouldPassgetSubDataPropertiesexpressionBodyfalse() {
        OWLDataProperty expressionBody =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#expressionBody");
        // expected bottomDataProperty
        // actual__ expressionBody, false
        equal(reasoner.getSubDataProperties(expressionBody, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesexpressionBodyfalse() {
        OWLDataProperty expressionBody =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#expressionBody");
        // expected topDataProperty
        // actual__ expressionBody, false
        equal(reasoner.getSuperDataProperties(expressionBody, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsexpressionBodyfalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Expression =
            C("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#Expression");
        OWLDataProperty expressionBody =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#expressionBody");
        // expected Thing, Expression
        // actual__ expressionBody, false
        equal(reasoner.getDataPropertyDomains(expressionBody, false), Thing, Expression);
    }

    @Test
    void shouldPassgetSubDataPropertiesexpressionBodytrue() {
        OWLDataProperty expressionBody =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#expressionBody");
        // expected bottomDataProperty
        // actual__ expressionBody, true
        equal(reasoner.getSubDataProperties(expressionBody, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesexpressionBodytrue() {
        OWLDataProperty expressionBody =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#expressionBody");
        // expected topDataProperty
        // actual__ expressionBody, true
        equal(reasoner.getSuperDataProperties(expressionBody, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsexpressionBodytrue() {
        OWLClass Expression =
            C("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#Expression");
        OWLDataProperty expressionBody =
            DP("http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#expressionBody");
        // expected Expression
        // actual__ expressionBody, true
        equal(reasoner.getDataPropertyDomains(expressionBody, true), Expression);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueFunctionfalse() {
        OWLDataProperty valueFunction =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        // expected bottomDataProperty
        // actual__ valueFunction, false
        equal(reasoner.getSubDataProperties(valueFunction, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvalueFunctionfalse() {
        OWLDataProperty valueSpecifier =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueSpecifier");
        OWLDataProperty valueFunction =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        // expected valueSpecifier, topDataProperty
        // actual__ valueFunction, false
        equal(reasoner.getSuperDataProperties(valueFunction, false), valueSpecifier,
            topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueFunctiontrue() {
        OWLDataProperty valueFunction =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        // expected bottomDataProperty
        // actual__ valueFunction, true
        equal(reasoner.getSubDataProperties(valueFunction, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiestaxonomyfalse() {
        OWLDataProperty taxonomy =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#taxonomy");
        // expected bottomDataProperty
        // actual__ taxonomy, false
        equal(reasoner.getSubDataProperties(taxonomy, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiestaxonomyfalse() {
        OWLDataProperty taxonomy =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#taxonomy");
        // expected topDataProperty
        // actual__ taxonomy, false
        equal(reasoner.getSuperDataProperties(taxonomy, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainstaxonomyfalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty taxonomy =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#taxonomy");
        // expected Thing, ServiceCategory
        // actual__ taxonomy, false
        equal(reasoner.getDataPropertyDomains(taxonomy, false), Thing, ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiestaxonomytrue() {
        OWLDataProperty taxonomy =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#taxonomy");
        // expected bottomDataProperty
        // actual__ taxonomy, true
        equal(reasoner.getSubDataProperties(taxonomy, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiestaxonomytrue() {
        OWLDataProperty taxonomy =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#taxonomy");
        // expected topDataProperty
        // actual__ taxonomy, true
        equal(reasoner.getSuperDataProperties(taxonomy, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainstaxonomytrue() {
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty taxonomy =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#taxonomy");
        // expected ServiceCategory
        // actual__ taxonomy, true
        equal(reasoner.getDataPropertyDomains(taxonomy, true), ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueSpecifierfalse() {
        OWLDataProperty valueForm =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        OWLDataProperty valueFunction =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueFunction");
        OWLDataProperty valueType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        OWLDataProperty valueSpecifier =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueSpecifier");
        // expected valueForm, bottomDataProperty, valueFunction, valueType
        // actual__ valueSpecifier, false
        equal(reasoner.getSubDataProperties(valueSpecifier, false), valueForm, bottomDataProperty,
            valueFunction, valueType);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvalueSpecifierfalse() {
        OWLDataProperty valueSpecifier =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueSpecifier");
        // expected topDataProperty
        // actual__ valueSpecifier, false
        equal(reasoner.getSuperDataProperties(valueSpecifier, false), topDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvalueSpecifiertrue() {
        OWLDataProperty valueSpecifier =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueSpecifier");
        // expected topDataProperty
        // actual__ valueSpecifier, true
        equal(reasoner.getSuperDataProperties(valueSpecifier, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiescategoryNamefalse() {
        OWLDataProperty categoryName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#categoryName");
        // expected bottomDataProperty
        // actual__ categoryName, false
        equal(reasoner.getSubDataProperties(categoryName, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiescategoryNamefalse() {
        OWLDataProperty categoryName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#categoryName");
        // expected topDataProperty
        // actual__ categoryName, false
        equal(reasoner.getSuperDataProperties(categoryName, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainscategoryNamefalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty categoryName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#categoryName");
        // expected Thing, ServiceCategory
        // actual__ categoryName, false
        equal(reasoner.getDataPropertyDomains(categoryName, false), Thing, ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiescategoryNametrue() {
        OWLDataProperty categoryName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#categoryName");
        // expected bottomDataProperty
        // actual__ categoryName, true
        equal(reasoner.getSubDataProperties(categoryName, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiescategoryNametrue() {
        OWLDataProperty categoryName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#categoryName");
        // expected topDataProperty
        // actual__ categoryName, true
        equal(reasoner.getSuperDataProperties(categoryName, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainscategoryNametrue() {
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty categoryName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#categoryName");
        // expected ServiceCategory
        // actual__ categoryName, true
        equal(reasoner.getDataPropertyDomains(categoryName, true), ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiesnamefalse() {
        OWLDataProperty name = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#name");
        // expected bottomDataProperty
        // actual__ name, false
        equal(reasoner.getSubDataProperties(name, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesnamefalse() {
        OWLDataProperty name = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#name");
        // expected topDataProperty
        // actual__ name, false
        equal(reasoner.getSuperDataProperties(name, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesnametrue() {
        OWLDataProperty name = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#name");
        // expected bottomDataProperty
        // actual__ name, true
        equal(reasoner.getSubDataProperties(name, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesnametrue() {
        OWLDataProperty name = DP("http://www.daml.org/services/owl-s/1.1/Process.owl#name");
        // expected topDataProperty
        // actual__ name, true
        equal(reasoner.getSuperDataProperties(name, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueTypefalse() {
        OWLDataProperty valueType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        // expected bottomDataProperty
        // actual__ valueType, false
        equal(reasoner.getSubDataProperties(valueType, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvalueTypefalse() {
        OWLDataProperty valueSpecifier =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueSpecifier");
        OWLDataProperty valueType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        // expected valueSpecifier, topDataProperty
        // actual__ valueType, false
        equal(reasoner.getSuperDataProperties(valueType, false), valueSpecifier, topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueTypetrue() {
        OWLDataProperty valueType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueType");
        // expected bottomDataProperty
        // actual__ valueType, true
        equal(reasoner.getSubDataProperties(valueType, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesinvocablefalse() {
        OWLDataProperty invocable =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        // expected bottomDataProperty
        // actual__ invocable, false
        equal(reasoner.getSubDataProperties(invocable, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesinvocablefalse() {
        OWLDataProperty invocable =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        // expected topDataProperty
        // actual__ invocable, false
        equal(reasoner.getSuperDataProperties(invocable, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesinvocabletrue() {
        OWLDataProperty invocable =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        // expected bottomDataProperty
        // actual__ invocable, true
        equal(reasoner.getSubDataProperties(invocable, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesinvocabletrue() {
        OWLDataProperty invocable =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#invocable");
        // expected topDataProperty
        // actual__ invocable, true
        equal(reasoner.getSuperDataProperties(invocable, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueFormfalse() {
        OWLDataProperty valueForm =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        // expected bottomDataProperty
        // actual__ valueForm, false
        equal(reasoner.getSubDataProperties(valueForm, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvalueFormfalse() {
        OWLDataProperty valueSpecifier =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueSpecifier");
        OWLDataProperty valueForm =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        // expected valueSpecifier, topDataProperty
        // actual__ valueForm, false
        equal(reasoner.getSuperDataProperties(valueForm, false), valueSpecifier, topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueFormtrue() {
        OWLDataProperty valueForm =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueForm");
        // expected bottomDataProperty
        // actual__ valueForm, true
        equal(reasoner.getSubDataProperties(valueForm, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesparameterValuefalse() {
        OWLDataProperty parameterValue =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        // expected bottomDataProperty
        // actual__ parameterValue, false
        equal(reasoner.getSubDataProperties(parameterValue, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesparameterValuefalse() {
        OWLDataProperty parameterValue =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        // expected topDataProperty
        // actual__ parameterValue, false
        equal(reasoner.getSuperDataProperties(parameterValue, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesparameterValuetrue() {
        OWLDataProperty parameterValue =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        // expected bottomDataProperty
        // actual__ parameterValue, true
        equal(reasoner.getSubDataProperties(parameterValue, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesparameterValuetrue() {
        OWLDataProperty parameterValue =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterValue");
        // expected topDataProperty
        // actual__ parameterValue, true
        equal(reasoner.getSuperDataProperties(parameterValue, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesvaluefalse() {
        OWLDataProperty value = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#value");
        // expected bottomDataProperty
        // actual__ value, false
        equal(reasoner.getSubDataProperties(value, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvaluefalse() {
        OWLDataProperty value = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#value");
        // expected topDataProperty
        // actual__ value, false
        equal(reasoner.getSuperDataProperties(value, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsvaluefalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty value = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#value");
        // expected Thing, ServiceCategory
        // actual__ value, false
        equal(reasoner.getDataPropertyDomains(value, false), Thing, ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiesvaluetrue() {
        OWLDataProperty value = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#value");
        // expected bottomDataProperty
        // actual__ value, true
        equal(reasoner.getSubDataProperties(value, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvaluetrue() {
        OWLDataProperty value = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#value");
        // expected topDataProperty
        // actual__ value, true
        equal(reasoner.getSuperDataProperties(value, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsvaluetrue() {
        OWLClass ServiceCategory =
            C("http://www.daml.org/services/owl-s/1.1/Profile.owl#ServiceCategory");
        OWLDataProperty value = DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#value");
        // expected ServiceCategory
        // actual__ value, true
        equal(reasoner.getDataPropertyDomains(value, true), ServiceCategory);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceNamefalse() {
        OWLDataProperty serviceName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName");
        // expected bottomDataProperty
        // actual__ serviceName, false
        equal(reasoner.getSubDataProperties(serviceName, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceNamefalse() {
        OWLDataProperty serviceName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName");
        // expected topDataProperty
        // actual__ serviceName, false
        equal(reasoner.getSuperDataProperties(serviceName, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsserviceNamefalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Profile = C("http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile");
        OWLClass ServiceProfile =
            C("http://www.daml.org/services/owl-s/1.1/Service.owl#ServiceProfile");
        OWLDataProperty serviceName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName");
        // expected Thing, Profile, ServiceProfile
        // actual__ serviceName, false
        equal(reasoner.getDataPropertyDomains(serviceName, false), Thing, Profile, ServiceProfile);
    }

    @Test
    void shouldPassgetSubDataPropertiesserviceNametrue() {
        OWLDataProperty serviceName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName");
        // expected bottomDataProperty
        // actual__ serviceName, true
        equal(reasoner.getSubDataProperties(serviceName, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesserviceNametrue() {
        OWLDataProperty serviceName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName");
        // expected topDataProperty
        // actual__ serviceName, true
        equal(reasoner.getSuperDataProperties(serviceName, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsserviceNametrue() {
        OWLClass Profile = C("http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile");
        OWLDataProperty serviceName =
            DP("http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName");
        // expected Profile
        // actual__ serviceName, true
        equal(reasoner.getDataPropertyDomains(serviceName, true), Profile);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueDatafalse() {
        OWLDataProperty valueData =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueData");
        // expected bottomDataProperty
        // actual__ valueData, false
        equal(reasoner.getSubDataProperties(valueData, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvalueDatafalse() {
        OWLDataProperty valueData =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueData");
        // expected topDataProperty
        // actual__ valueData, false
        equal(reasoner.getSuperDataProperties(valueData, false), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesvalueDatatrue() {
        OWLDataProperty valueData =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueData");
        // expected bottomDataProperty
        // actual__ valueData, true
        equal(reasoner.getSubDataProperties(valueData, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesvalueDatatrue() {
        OWLDataProperty valueData =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#valueData");
        // expected topDataProperty
        // actual__ valueData, true
        equal(reasoner.getSuperDataProperties(valueData, true), topDataProperty);
    }

    @Test
    void shouldPassgetSubDataPropertiesparameterTypefalse() {
        OWLDataProperty parameterType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        // expected bottomDataProperty
        // actual__ parameterType, false
        equal(reasoner.getSubDataProperties(parameterType, false), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesparameterTypefalse() {
        OWLDataProperty parameterType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        // expected topDataProperty
        // actual__ parameterType, false
        equal(reasoner.getSuperDataProperties(parameterType, false), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsparameterTypefalse() {
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Parameter = C("http://www.daml.org/services/owl-s/1.1/Process.owl#Parameter");
        OWLClass Variable = C("http://www.w3.org/2003/11/swrl#Variable");
        OWLDataProperty parameterType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        // expected Thing, Parameter, Variable
        // actual__ parameterType, false
        equal(reasoner.getDataPropertyDomains(parameterType, false), Thing, Parameter, Variable);
    }

    @Test
    void shouldPassgetSubDataPropertiesparameterTypetrue() {
        OWLDataProperty parameterType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        // expected bottomDataProperty
        // actual__ parameterType, true
        equal(reasoner.getSubDataProperties(parameterType, true), bottomDataProperty);
    }

    @Test
    void shouldPassgetSuperDataPropertiesparameterTypetrue() {
        OWLDataProperty parameterType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        // expected topDataProperty
        // actual__ parameterType, true
        equal(reasoner.getSuperDataProperties(parameterType, true), topDataProperty);
    }

    @Test
    void shouldPassgetDataPropertyDomainsparameterTypetrue() {
        OWLClass Parameter = C("http://www.daml.org/services/owl-s/1.1/Process.owl#Parameter");
        OWLDataProperty parameterType =
            DP("http://www.daml.org/services/owl-s/1.1/Process.owl#parameterType");
        // expected Parameter
        // actual__ parameterType, true
        equal(reasoner.getDataPropertyDomains(parameterType, true), Parameter);
    }
}
