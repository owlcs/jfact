package bugs.debug;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InferenceType;

import uk.ac.manchester.cs.jfact.JFactReasoner;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import bugs.VerifyComplianceBase;

@SuppressWarnings("javadoc")
public class JFactTest extends VerifyComplianceBase {

    String in = "Prefix(:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n"
            + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n"
            + "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n"
            + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
            + "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n"
            + "\n"
            + "\n"
            + "Ontology(\n"
            + "Declaration(Class(<urn:process#Any-Order>))\nDeclaration(Class(<urn:process#AtomicProcess>))\nDeclaration(Class(<urn:process#Binding>))\nDeclaration(Class(<urn:process#Choice>))\nDeclaration(Class(<urn:process#CompositeProcess>))\nDeclaration(Class(<urn:process#ConditionalEffect>))\nDeclaration(Class(<urn:process#ConditionalOutput>))\nDeclaration(Class(<urn:process#ControlConstruct>))\nDeclaration(Class(<urn:process#ControlConstructBag>))\nDeclaration(Class(<urn:process#ControlConstructList>))\nDeclaration(Class(<urn:process#If-Then-Else>))\nDeclaration(Class(<urn:process#Input>))\nDeclaration(Class(<urn:process#InputBinding>))\nDeclaration(Class(<urn:process#Iterate>))\nDeclaration(Class(<urn:process#Local>))\nDeclaration(Class(<urn:process#Output>))\nDeclaration(Class(<urn:process#OutputBinding>))\nDeclaration(Class(<urn:process#Parameter>))\nDeclaration(Class(<urn:process#Participant>))\nDeclaration(Class(<urn:process#Perform>))\nDeclaration(Class(<urn:process#Process>))\nDeclaration(Class(<urn:process#ProcessComponent>))\nDeclaration(Class(<urn:process#Produce>))\nDeclaration(Class(<urn:process#Repeat-Until>))\nDeclaration(Class(<urn:process#Repeat-While>))\nDeclaration(Class(<urn:process#Result>))\nDeclaration(Class(<urn:process#ResultVar>))\nDeclaration(Class(<urn:process#Sequence>))\nDeclaration(Class(<urn:process#SimpleProcess>))\nDeclaration(Class(<urn:process#Split>))\nDeclaration(Class(<urn:process#Split-Join>))\nDeclaration(Class(<urn:process#Unordered>))\nDeclaration(Class(<urn:process#ValueOf>))\nDeclaration(Class(<urn:profile#Profile>))\nDeclaration(Class(<urn:profile#ServiceCategory>))\nDeclaration(Class(<urn:profile#ServiceParameter>))\nDeclaration(Class(<urn:Service.owl#Service>))\nDeclaration(Class(<urn:Service.owl#ServiceGrounding>))\nDeclaration(Class(<urn:Service.owl#ServiceModel>))\nDeclaration(Class(<urn:Service.owl#ServiceProfile>))\nDeclaration(Class(<urn:expr#Condition>))\nDeclaration(Class(<urn:expr#DRS-Condition>))\nDeclaration(Class(<urn:expr#DRS-Expression>))\nDeclaration(Class(<urn:expr#Expression>))\nDeclaration(Class(<urn:expr#KIF-Condition>))\nDeclaration(Class(<urn:expr#KIF-Expression>))\nDeclaration(Class(<urn:expr#LogicLanguage>))\nDeclaration(Class(<urn:expr#SWRL-Condition>))\nDeclaration(Class(<urn:expr#SWRL-Expression>))\nDeclaration(Class(<urn:generic/ObjectList.owl#List>))\nDeclaration(Class(<urn:timeentry#IntervalThing>))\nDeclaration(Class(<http://www.w3.org/2003/11/swrl#AtomList>))\nDeclaration(Class(<http://www.w3.org/2003/11/swrl#Variable>))\nDeclaration(ObjectProperty(<urn:process#collapse>))\nDeclaration(ObjectProperty(<urn:process#collapsesTo>))\nDeclaration(ObjectProperty(<urn:process#components>))\nDeclaration(ObjectProperty(<urn:process#composedOf>))\nDeclaration(ObjectProperty(<urn:process#computedEffect>))\nDeclaration(ObjectProperty(<urn:process#computedInput>))\nDeclaration(ObjectProperty(<urn:process#computedOutput>))\nDeclaration(ObjectProperty(<urn:process#computedPrecondition>))\nDeclaration(ObjectProperty(<urn:process#else>))\nDeclaration(ObjectProperty(<urn:process#expand>))\nDeclaration(ObjectProperty(<urn:process#expandsTo>))\nDeclaration(ObjectProperty(<urn:process#fromProcess>))\nDeclaration(ObjectProperty(<urn:process#hasClient>))\nDeclaration(ObjectProperty(<urn:process#hasDataFrom>))\nDeclaration(ObjectProperty(<urn:process#hasEffect>))\nDeclaration(ObjectProperty(<urn:process#hasInput>))\nDeclaration(ObjectProperty(<urn:process#hasLocal>))\nDeclaration(ObjectProperty(<urn:process#hasOutput>))\nDeclaration(ObjectProperty(<urn:process#hasParameter>))\nDeclaration(ObjectProperty(<urn:process#hasParticipant>))\nDeclaration(ObjectProperty(<urn:process#hasPrecondition>))\nDeclaration(ObjectProperty(<urn:process#hasResult>))\nDeclaration(ObjectProperty(<urn:process#hasResultVar>))\nDeclaration(ObjectProperty(<urn:process#ifCondition>))\nDeclaration(ObjectProperty(<urn:process#inCondition>))\nDeclaration(ObjectProperty(<urn:process#performedBy>))\nDeclaration(ObjectProperty(<urn:process#process>))\nDeclaration(ObjectProperty(<urn:process#producedBinding>))\nDeclaration(ObjectProperty(<urn:process#realizedBy>))\nDeclaration(ObjectProperty(<urn:process#realizes>))\nDeclaration(ObjectProperty(<urn:process#theVar>))\nDeclaration(ObjectProperty(<urn:process#then>))\nDeclaration(ObjectProperty(<urn:process#timeout>))\nDeclaration(ObjectProperty(<urn:process#toParam>))\nDeclaration(ObjectProperty(<urn:process#untilCondition>))\nDeclaration(ObjectProperty(<urn:process#untilProcess>))\nDeclaration(ObjectProperty(<urn:process#valueSource>))\nDeclaration(ObjectProperty(<urn:process#whileCondition>))\nDeclaration(ObjectProperty(<urn:process#whileProcess>))\nDeclaration(ObjectProperty(<urn:process#withOutput>))\nDeclaration(ObjectProperty(<urn:profile#contactInformation>))\nDeclaration(ObjectProperty(<urn:profile#hasInput>))\nDeclaration(ObjectProperty(<urn:profile#hasOutput>))\nDeclaration(ObjectProperty(<urn:profile#hasParameter>))\nDeclaration(ObjectProperty(<urn:profile#hasPrecondition>))\nDeclaration(ObjectProperty(<urn:profile#hasResult>))\nDeclaration(ObjectProperty(<urn:profile#has_process>))\nDeclaration(ObjectProperty(<urn:profile#sParameter>))\nDeclaration(ObjectProperty(<urn:profile#serviceCategory>))\nDeclaration(ObjectProperty(<urn:profile#serviceParameter>))\nDeclaration(ObjectProperty(<urn:Service.owl#describedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#describes>))\nDeclaration(ObjectProperty(<urn:Service.owl#isDescribedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#isPresentedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#isSupportedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#presentedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#presents>))\nDeclaration(ObjectProperty(<urn:Service.owl#providedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#provides>))\nDeclaration(ObjectProperty(<urn:Service.owl#supportedBy>))\nDeclaration(ObjectProperty(<urn:Service.owl#supports>))\nDeclaration(ObjectProperty(<urn:expr#expressionLanguage>))\nDeclaration(ObjectProperty(<urn:generic/ObjectList.owl#first>))\nDeclaration(ObjectProperty(<urn:generic/ObjectList.owl#rest>))\nDeclaration(DataProperty(<urn:process#invocable>))\nDeclaration(DataProperty(<urn:process#name>))\nDeclaration(DataProperty(<urn:process#parameterType>))\nDeclaration(DataProperty(<urn:process#parameterValue>))\nDeclaration(DataProperty(<urn:process#valueData>))\nDeclaration(DataProperty(<urn:process#valueForm>))\nDeclaration(DataProperty(<urn:process#valueFunction>))\nDeclaration(DataProperty(<urn:process#valueSpecifier>))\nDeclaration(DataProperty(<urn:process#valueType>))\nDeclaration(DataProperty(<urn:profile#categoryName>))\nDeclaration(DataProperty(<urn:profile#code>))\nDeclaration(DataProperty(<urn:profile#serviceClassification>))\nDeclaration(DataProperty(<urn:profile#serviceName>))\nDeclaration(DataProperty(<urn:profile#serviceParameterName>))\nDeclaration(DataProperty(<urn:profile#serviceProduct>))\nDeclaration(DataProperty(<urn:profile#taxonomy>))\nDeclaration(DataProperty(<urn:profile#textDescription>))\nDeclaration(DataProperty(<urn:profile#value>))\nDeclaration(DataProperty(<urn:expr#expressionBody>))\nDeclaration(DataProperty(<urn:expr#refURI>))\nDeclaration(NamedIndividual(<urn:process#TheClient>))\nDeclaration(NamedIndividual(<urn:process#TheParentPerform>))\nDeclaration(NamedIndividual(<urn:process#TheServer>))\nDeclaration(NamedIndividual(<urn:process#ThisPerform>))\nDeclaration(NamedIndividual(<urn:expr#AlwaysTrue>))\nDeclaration(NamedIndividual(<urn:expr#DRS>))\nDeclaration(NamedIndividual(<urn:expr#KIF>))\nDeclaration(NamedIndividual(<urn:expr#SWRL>))\nDeclaration(NamedIndividual(<urn:generic/ObjectList.owl#nil>))\n"
            + "SubClassOf(<urn:process#ControlConstruct> ObjectMaxCardinality(1 <urn:process#timeout>))\n"
            + "SubClassOf(<urn:process#ControlConstructBag> <urn:generic/ObjectList.owl#List>)\n"
            + "SubClassOf(<urn:process#ControlConstructBag> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#first> <urn:process#ControlConstruct>))\n"
            + "SubClassOf(<urn:process#ControlConstructBag> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#rest> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#ControlConstructList> <urn:generic/ObjectList.owl#List>)\n"
            + "SubClassOf(<urn:process#ControlConstructList> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#first> <urn:process#ControlConstruct>))\n"
            + "SubClassOf(<urn:process#ControlConstructList> ObjectAllValuesFrom(<urn:generic/ObjectList.owl#rest> <urn:process#ControlConstructList>))\n"
            + "SubClassOf(<urn:process#If-Then-Else> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#If-Then-Else> ObjectExactCardinality(1 <urn:process#ifCondition>))\n"
            + "SubClassOf(<urn:process#If-Then-Else> ObjectExactCardinality(1 <urn:process#then>))\n"
            + "SubClassOf(<urn:process#If-Then-Else> ObjectMaxCardinality(1 <urn:process#else>))\n"
            + "SubClassOf(<urn:process#Input> <urn:process#Parameter>)\n"
            + "DisjointClasses(<urn:process#Input> <urn:process#Local>)\n"
            + "DisjointClasses(<urn:process#Input> <urn:process#Output>)\n"
            + "DisjointClasses(<urn:process#Input> <urn:process#ResultVar>)\n"
            + "EquivalentClasses(<urn:process#InputBinding> ObjectIntersectionOf(ObjectAllValuesFrom(<urn:process#toParam> <urn:process#Input>) <urn:process#Binding>))\n"
            + "SubClassOf(<urn:process#InputBinding> <urn:process#Binding>)\n"
            + "SubClassOf(<urn:process#Iterate> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Local> <urn:process#Parameter>)\n"
            + "DisjointClasses(<urn:process#Local> <urn:process#Output>)\n"
            + "DisjointClasses(<urn:process#Local> <urn:process#ResultVar>)\n"
            + "SubClassOf(<urn:process#Output> <urn:process#Parameter>)\n"
            + "DisjointClasses(<urn:process#Output> <urn:process#ResultVar>)\n"
            + "EquivalentClasses(<urn:process#OutputBinding> ObjectIntersectionOf(ObjectAllValuesFrom(<urn:process#toParam> <urn:process#Output>) <urn:process#Binding>))\n"
            + "SubClassOf(<urn:process#OutputBinding> <urn:process#Binding>)\n"
            + "SubClassOf(<urn:process#Parameter> <http://www.w3.org/2003/11/swrl#Variable>)\n"
            + "SubClassOf(<urn:process#Parameter> DataMinCardinality(1 <urn:process#parameterType>))\n"
            + "SubClassOf(<urn:process#Perform> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Perform> ObjectExactCardinality(1 <urn:process#process>))\n"
            + "EquivalentClasses(<urn:process#Process> ObjectUnionOf(<urn:process#AtomicProcess> <urn:process#CompositeProcess> <urn:process#SimpleProcess>))\n"
            + "SubClassOf(<urn:process#Process> <urn:Service.owl#ServiceModel>)\n"
            + "SubClassOf(<urn:process#Process> DataMaxCardinality(1 <urn:process#name>))\n"
            + "SubClassOf(<urn:process#Produce> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Repeat-Until> <urn:process#Iterate>)\n"
            + "SubClassOf(<urn:process#Repeat-Until> ObjectExactCardinality(1 <urn:process#untilCondition>))\n"
            + "SubClassOf(<urn:process#Repeat-Until> ObjectExactCardinality(1 <urn:process#untilProcess>))\n"
            + "SubClassOf(<urn:process#Repeat-While> <urn:process#Iterate>)\n"
            + "SubClassOf(<urn:process#Repeat-While> ObjectExactCardinality(1 <urn:process#whileCondition>))\n"
            + "SubClassOf(<urn:process#Repeat-While> ObjectExactCardinality(1 <urn:process#whileProcess>))\n"
            + "SubClassOf(<urn:process#ResultVar> <urn:process#Parameter>)\n"
            + "SubClassOf(<urn:process#Sequence> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Sequence> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructList>))\n"
            + "SubClassOf(<urn:process#Sequence> ObjectExactCardinality(1 <urn:process#components>))\n"
            + "SubClassOf(<urn:process#SimpleProcess> <urn:process#Process>)\n"
            + "SubClassOf(<urn:process#Split> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Split> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#Split> ObjectExactCardinality(1 <urn:process#components>))\n"
            + "SubClassOf(<urn:process#Split-Join> <urn:process#ControlConstruct>)\n"
            + "SubClassOf(<urn:process#Split-Join> ObjectAllValuesFrom(<urn:process#components> <urn:process#ControlConstructBag>))\n"
            + "SubClassOf(<urn:process#ValueOf> ObjectExactCardinality(1 <urn:process#theVar>))\n"
            + "SubClassOf(<urn:process#ValueOf> ObjectMaxCardinality(1 <urn:process#fromProcess>))\n"
            + "SubClassOf(<urn:profile#Profile> <urn:Service.owl#ServiceProfile>)\n"
            + "SubClassOf(<urn:profile#Profile> DataExactCardinality(1 <urn:profile#serviceName>))\n"
            + "SubClassOf(<urn:profile#Profile> DataExactCardinality(1 <urn:profile#textDescription>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#categoryName>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#code>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#taxonomy>))\n"
            + "SubClassOf(<urn:profile#ServiceCategory> DataExactCardinality(1 <urn:profile#value>))\n"
            + "SubClassOf(<urn:profile#ServiceParameter> ObjectExactCardinality(1 <urn:profile#sParameter>))\n"
            + "SubClassOf(<urn:profile#ServiceParameter> DataExactCardinality(1 <urn:profile#serviceParameterName>))\n"
            + "SubClassOf(<urn:Service.owl#Service> ObjectMaxCardinality(1 <urn:Service.owl#describedBy>))\n"
            + "SubClassOf(<urn:Service.owl#ServiceGrounding> ObjectExactCardinality(1 <urn:Service.owl#supportedBy>))\n"
            + "SubClassOf(<urn:expr#Condition> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#DRS-Condition> <urn:expr#Condition>)\n"
            + "SubClassOf(<urn:expr#DRS-Condition> <urn:expr#DRS-Expression>)\n"
            + "SubClassOf(<urn:expr#DRS-Expression> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#DRS-Expression> ObjectHasValue(<urn:expr#expressionLanguage> <urn:expr#DRS>))\n"
            + "SubClassOf(<urn:expr#DRS-Expression> DataAllValuesFrom(<urn:expr#expressionBody> rdf:XMLLiteral))\n"
            + "SubClassOf(<urn:expr#Expression> ObjectExactCardinality(1 <urn:expr#expressionLanguage>))\n"
            + "SubClassOf(<urn:expr#Expression> DataExactCardinality(1 <urn:expr#expressionBody>))\n"
            + "SubClassOf(<urn:expr#KIF-Condition> <urn:expr#Condition>)\n"
            + "SubClassOf(<urn:expr#KIF-Condition> <urn:expr#KIF-Expression>)\n"
            + "SubClassOf(<urn:expr#KIF-Expression> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#KIF-Expression> ObjectHasValue(<urn:expr#expressionLanguage> <urn:expr#KIF>))\n"
            + "SubClassOf(<urn:expr#SWRL-Condition> <urn:expr#Condition>)\n"
            + "SubClassOf(<urn:expr#SWRL-Condition> <urn:expr#SWRL-Expression>)\n"
            + "SubClassOf(<urn:expr#SWRL-Expression> <urn:expr#Expression>)\n"
            + "SubClassOf(<urn:expr#SWRL-Expression> ObjectHasValue(<urn:expr#expressionLanguage> <urn:expr#SWRL>))\n"
            + "SubClassOf(<urn:expr#SWRL-Expression> DataAllValuesFrom(<urn:expr#expressionBody> rdf:XMLLiteral))\n"
            + "EquivalentObjectProperties(<urn:process#collapse> <urn:process#collapsesTo>)\n"
            + "InverseObjectProperties(<urn:process#collapsesTo> <urn:process#expandsTo>)\n"
            + "ObjectPropertyDomain(<urn:process#collapsesTo> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#collapsesTo> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#components> ObjectUnionOf(<urn:process#Any-Order> <urn:process#Choice> <urn:process#Sequence> <urn:process#Split> <urn:process#Split-Join>))\n"
            + "ObjectPropertyDomain(<urn:process#composedOf> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#composedOf> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#computedEffect> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedEffect> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#computedInput> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedInput> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#computedOutput> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedOutput> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#computedPrecondition> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyRange(<urn:process#computedPrecondition> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:process#else> <urn:process#If-Then-Else>)\n"
            + "ObjectPropertyRange(<urn:process#else> <urn:process#ControlConstruct>)\n"
            + "EquivalentObjectProperties(<urn:process#expand> <urn:process#expandsTo>)\n"
            + "ObjectPropertyDomain(<urn:process#expandsTo> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyRange(<urn:process#expandsTo> <urn:process#CompositeProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#fromProcess> <urn:process#ValueOf>)\n"
            + "ObjectPropertyRange(<urn:process#fromProcess> <urn:process#Perform>)\n"
            + "SubObjectPropertyOf(<urn:process#hasClient> <urn:process#hasParticipant>)\n"
            + "ObjectPropertyDomain(<urn:process#hasClient> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:process#hasDataFrom> <urn:process#Perform>)\n"
            + "ObjectPropertyRange(<urn:process#hasDataFrom> <urn:process#Binding>)\n"
            + "ObjectPropertyDomain(<urn:process#hasEffect> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#hasEffect> <urn:expr#Expression>)\n"
            + "SubObjectPropertyOf(<urn:process#hasInput> <urn:process#hasParameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasInput> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasInput> <urn:process#Input>)\n"
            + "SubObjectPropertyOf(<urn:process#hasLocal> <urn:process#hasParameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasLocal> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasLocal> <urn:process#Local>)\n"
            + "SubObjectPropertyOf(<urn:process#hasOutput> <urn:process#hasParameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasOutput> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasOutput> <urn:process#Output>)\n"
            + "ObjectPropertyDomain(<urn:process#hasParameter> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasParameter> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:process#hasParticipant> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasParticipant> <urn:process#Participant>)\n"
            + "ObjectPropertyDomain(<urn:process#hasPrecondition> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasPrecondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#hasResult> <urn:process#Process>)\n"
            + "ObjectPropertyRange(<urn:process#hasResult> <urn:process#Result>)\n"
            + "ObjectPropertyDomain(<urn:process#hasResultVar> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#hasResultVar> <urn:process#ResultVar>)\n"
            + "ObjectPropertyDomain(<urn:process#ifCondition> <urn:process#If-Then-Else>)\n"
            + "ObjectPropertyRange(<urn:process#ifCondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#inCondition> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#inCondition> <urn:expr#Condition>)\n"
            + "SubObjectPropertyOf(<urn:process#performedBy> <urn:process#hasParticipant>)\n"
            + "ObjectPropertyDomain(<urn:process#performedBy> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:process#process> <urn:process#Perform>)\n"
            + "ObjectPropertyRange(<urn:process#process> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:process#producedBinding> <urn:process#Produce>)\n"
            + "ObjectPropertyRange(<urn:process#producedBinding> <urn:process#OutputBinding>)\n"
            + "InverseObjectProperties(<urn:process#realizedBy> <urn:process#realizes>)\n"
            + "ObjectPropertyDomain(<urn:process#realizedBy> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyRange(<urn:process#realizedBy> <urn:process#AtomicProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#realizes> <urn:process#AtomicProcess>)\n"
            + "ObjectPropertyRange(<urn:process#realizes> <urn:process#SimpleProcess>)\n"
            + "ObjectPropertyDomain(<urn:process#theVar> <urn:process#ValueOf>)\n"
            + "ObjectPropertyRange(<urn:process#theVar> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:process#then> <urn:process#If-Then-Else>)\n"
            + "ObjectPropertyRange(<urn:process#then> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#timeout> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyRange(<urn:process#timeout> <urn:timeentry#IntervalThing>)\n"
            + "ObjectPropertyDomain(<urn:process#toParam> <urn:process#Binding>)\n"
            + "ObjectPropertyRange(<urn:process#toParam> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:process#untilCondition> <urn:process#Repeat-Until>)\n"
            + "ObjectPropertyRange(<urn:process#untilCondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#untilProcess> <urn:process#Repeat-Until>)\n"
            + "ObjectPropertyRange(<urn:process#untilProcess> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#valueSource> <urn:process#Binding>)\n"
            + "ObjectPropertyRange(<urn:process#valueSource> <urn:process#ValueOf>)\n"
            + "ObjectPropertyDomain(<urn:process#whileCondition> <urn:process#Repeat-While>)\n"
            + "ObjectPropertyRange(<urn:process#whileCondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:process#whileProcess> <urn:process#Repeat-While>)\n"
            + "ObjectPropertyRange(<urn:process#whileProcess> <urn:process#ControlConstruct>)\n"
            + "ObjectPropertyDomain(<urn:process#withOutput> <urn:process#Result>)\n"
            + "ObjectPropertyRange(<urn:process#withOutput> <urn:process#OutputBinding>)\n"
            + "ObjectPropertyDomain(<urn:profile#contactInformation> <urn:profile#Profile>)\n"
            + "SubObjectPropertyOf(<urn:profile#hasInput> <urn:profile#hasParameter>)\n"
            + "ObjectPropertyRange(<urn:profile#hasInput> <urn:process#Input>)\n"
            + "SubObjectPropertyOf(<urn:profile#hasOutput> <urn:profile#hasParameter>)\n"
            + "ObjectPropertyRange(<urn:profile#hasOutput> <urn:process#Output>)\n"
            + "ObjectPropertyDomain(<urn:profile#hasParameter> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#hasParameter> <urn:process#Parameter>)\n"
            + "ObjectPropertyDomain(<urn:profile#hasPrecondition> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#hasPrecondition> <urn:expr#Condition>)\n"
            + "ObjectPropertyDomain(<urn:profile#hasResult> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#hasResult> <urn:process#Result>)\n"
            + "FunctionalObjectProperty(<urn:profile#has_process>)\n"
            + "ObjectPropertyDomain(<urn:profile#has_process> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#has_process> <urn:process#Process>)\n"
            + "ObjectPropertyDomain(<urn:profile#sParameter> <urn:profile#ServiceParameter>)\n"
            + "ObjectPropertyRange(<urn:profile#sParameter> owl:Thing)\n"
            + "ObjectPropertyDomain(<urn:profile#serviceCategory> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#serviceCategory> <urn:profile#ServiceCategory>)\n"
            + "ObjectPropertyDomain(<urn:profile#serviceParameter> <urn:profile#Profile>)\n"
            + "ObjectPropertyRange(<urn:profile#serviceParameter> <urn:profile#ServiceParameter>)\n"
            + "EquivalentObjectProperties(<urn:Service.owl#describedBy> <urn:Service.owl#isDescribedBy>)\n"
            + "InverseObjectProperties(<urn:Service.owl#describedBy> <urn:Service.owl#describes>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#describedBy> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#describedBy> <urn:Service.owl#ServiceModel>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#describes> <urn:Service.owl#ServiceModel>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#describes> <urn:Service.owl#Service>)\n"
            + "EquivalentObjectProperties(<urn:Service.owl#isPresentedBy> <urn:Service.owl#presentedBy>)\n"
            + "EquivalentObjectProperties(<urn:Service.owl#isSupportedBy> <urn:Service.owl#supportedBy>)\n"
            + "InverseObjectProperties(<urn:Service.owl#presents> <urn:Service.owl#presentedBy>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#presentedBy> <urn:Service.owl#ServiceProfile>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#presentedBy> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyDomain(<urn:Service.owl#presents> <urn:Service.owl#Service>)\n"
            + "ObjectPropertyRange(<urn:Service.owl#presents> <urn:Service.owl#ServiceProfile>)\n"
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

    @Override
    public void setUp() throws OWLOntologyCreationException {}

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedByfalse1()
            throws OWLOntologyCreationException {
        reasoner = (JFactReasoner) factory().createReasoner(load(input()),
                new JFactReasonerConfiguration()
        // .setAbsorptionLoggingActive(true)
        // .setUSE_REASONING_STATISTICS(true)
        // .setLoggingActive(true)
                );
        reasoner.precomputeInferences(InferenceType.values());
        // reasoner.getConfiguration()
        // .setTMP_PRINT_TAXONOMY_INFO(true)
        // .setAbsorptionLoggingActive(true)
        // .setUSE_REASONING_STATISTICS(true).setLoggingActive(true);
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Service = C("urn:Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("urn:Service.owl#isPresentedBy");
        // expected Thing, Service
        // actual__ isPresentedBy, false
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, false), Thing,
                Service);
    }

    @Test
    public void shouldPassgetObjectPropertyRangesisPresentedByfalse2()
            throws OWLOntologyCreationException {
        OWLOntology load = load(input());
        List<OWLAxiom> axioms = JFactReasoner.importsIncluded(load);
        Collections.sort(axioms);
        reasoner = new JFactReasoner(load, axioms,
                new JFactReasonerConfiguration()
                // .setAbsorptionLoggingActive(true)
                // .setUSE_REASONING_STATISTICS(true)
                // .setLoggingActive(true)
                , BufferingMode.BUFFERING);
        reasoner.precomputeInferences(InferenceType.values());
        // List<AxiomInterface> list = ((JFactReasoner)
        // reasoner).kernel.ontology.axioms;
        // Collections.sort(list, new Comparator<AxiomInterface>() {
        //
        // @Override
        // public int compare(AxiomInterface o1, AxiomInterface o2) {
        // return o1.toString().compareTo(o2.toString());
        // }
        // });
        // XStream xStream = new XStream();
        // System.out
        // .println("JFactTest.shouldPassgetObjectPropertyRangesisPresentedByfalse2()\n");
        // System.out.println(xStream.toXML(reasoner));
        OWLClass Thing = C("http://www.w3.org/2002/07/owl#Thing");
        OWLClass Service = C("urn:Service.owl#Service");
        OWLObjectProperty isPresentedBy = OP("urn:Service.owl#isPresentedBy");
        // expected Thing, Service
        // actual__ isPresentedBy, false
        equal(reasoner.getObjectPropertyRanges(isPresentedBy, false), Thing,
                Service);
    }
}
