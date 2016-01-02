package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.compose;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

/** axiom translator */
public class AxiomTranslator implements OWLAxiomVisitorEx<AxiomInterface>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxiomTranslator.class);

    @Nonnull private final DeclarationVisitorEx v;
    private Ontology o;
    private TranslationMachinery tr;

    /**
     * @param o
     *        o
     * @param df
     *        df
     * @param tr
     *        tr
     */
    public AxiomTranslator(Ontology o, OWLDataFactory df, TranslationMachinery tr) {
        v = new DeclarationVisitorEx(o, df, tr);
        this.o = o;
        this.tr = tr;
    }

    @Override
    public AxiomInterface visit(OWLSubClassOfAxiom axiom) {
        return o
            .add(new AxiomConceptInclusion(axiom, tr.pointer(axiom.getSubClass()), tr.pointer(axiom.getSuperClass())));
    }

    @Override
    public AxiomInterface visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return o.add(new AxiomRelatedToNot(axiom, tr.pointer(axiom.getSubject()), tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleAsymmetric(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleReflexive(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLDisjointClassesAxiom axiom) {
        return o.add(new AxiomDisjointConcepts(axiom, translateClassExpressionSet(axiom.classExpressions())));
    }

    private List<ConceptExpression> translateClassExpressionSet(Stream<? extends OWLClassExpression> classExpressions) {
        return asList(classExpressions.map(tr::pointer));
    }

    @Override
    public AxiomInterface visit(OWLDataPropertyDomainAxiom axiom) {
        return o.add(new AxiomDRoleDomain(axiom, tr.pointer(axiom.getProperty()), tr.pointer(axiom.getDomain())));
    }

    @Override
    public AxiomInterface visit(OWLObjectPropertyDomainAxiom axiom) {
        return o.add(new AxiomORoleDomain(axiom, tr.pointer(axiom.getProperty()), tr.pointer(axiom.getDomain())));
    }

    @Override
    public AxiomInterface visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        return o.add(new AxiomEquivalentORoles(axiom, translateObjectPropertySet(axiom.properties())));
    }

    private List<ObjectRoleExpression> translateObjectPropertySet(Stream<OWLObjectPropertyExpression> properties) {
        return asList(properties.map(tr::pointer));
    }

    @Override
    public AxiomInterface visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return o.add(new AxiomValueOfNot(axiom, tr.pointer(axiom.getSubject()), tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLDifferentIndividualsAxiom axiom) {
        return o.add(new AxiomDifferentIndividuals(axiom, tr.translate(axiom.individuals())));
    }

    @Override
    public AxiomInterface visit(OWLDisjointDataPropertiesAxiom axiom) {
        return o.add(new AxiomDisjointDRoles(axiom, translateDataPropertySet(axiom.properties())));
    }

    private List<DataRoleExpression> translateDataPropertySet(Stream<OWLDataPropertyExpression> properties) {
        return asList(properties.map(tr::pointer));
    }

    @Override
    public AxiomInterface visit(OWLDisjointObjectPropertiesAxiom axiom) {
        return o.add(new AxiomDisjointORoles(axiom, translateObjectPropertySet(axiom.properties())));
    }

    @Override
    public AxiomInterface visit(OWLObjectPropertyRangeAxiom axiom) {
        return o.add(new AxiomORoleRange(axiom, tr.pointer(axiom.getProperty()), tr.pointer(axiom.getRange())));
    }

    @Override
    public AxiomInterface visit(OWLObjectPropertyAssertionAxiom axiom) {
        return o.add(new AxiomRelatedTo(axiom, tr.pointer(axiom.getSubject()), tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return o.add(new AxiomORoleFunctional(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLSubObjectPropertyOfAxiom axiom) {
        return o.add(
            new AxiomORoleSubsumption(axiom, tr.pointer(axiom.getSubProperty()), tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomInterface visit(OWLDisjointUnionAxiom axiom) {
        return o.add(new AxiomDisjointUnion(axiom, tr.pointer(axiom.getOWLClass()),
            translateClassExpressionSet(axiom.classExpressions())));
    }

    @Override
    public AxiomInterface visit(OWLDeclarationAxiom axiom) {
        OWLEntity entity = axiom.getEntity();
        return entity.accept(v);
    }

    @Override
    public AxiomInterface visit(OWLAnnotationAssertionAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomInterface visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleSymmetric(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLDataPropertyRangeAxiom axiom) {
        return o.add(new AxiomDRoleRange(axiom, tr.pointer(axiom.getProperty()), tr.pointer(axiom.getRange())));
    }

    @Override
    public AxiomInterface visit(OWLFunctionalDataPropertyAxiom axiom) {
        return o.add(new AxiomDRoleFunctional(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLEquivalentDataPropertiesAxiom axiom) {
        return o.add(new AxiomEquivalentDRoles(axiom, translateDataPropertySet(axiom.properties())));
    }

    @Override
    public AxiomInterface visit(OWLClassAssertionAxiom axiom) {
        return o
            .add(new AxiomInstanceOf(axiom, tr.pointer(axiom.getIndividual()), tr.pointer(axiom.getClassExpression())));
    }

    @Override
    public AxiomInterface visit(OWLEquivalentClassesAxiom axiom) {
        return o.add(new AxiomEquivalentConcepts(axiom, translateClassExpressionSet(axiom.classExpressions())));
    }

    @Override
    public AxiomInterface visit(OWLDataPropertyAssertionAxiom axiom) {
        return o.add(new AxiomValueOf(axiom, tr.pointer(axiom.getSubject()), tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleTransitive(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleIrreflexive(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLSubDataPropertyOfAxiom axiom) {
        return o.add(
            new AxiomDRoleSubsumption(axiom, tr.pointer(axiom.getSubProperty()), tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomInterface visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleInverseFunctional(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLSameIndividualAxiom axiom) {
        return o.add(new AxiomSameIndividuals(axiom, tr.translate(axiom.individuals())));
    }

    @Override
    public AxiomInterface visit(OWLSubPropertyChainOfAxiom axiom) {
        return o.add(
            new AxiomORoleSubsumption(axiom, compose(translateObjectPropertySet(axiom.getPropertyChain().stream())),
                tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomInterface visit(OWLInverseObjectPropertiesAxiom axiom) {
        return o.add(
            new AxiomRoleInverse(axiom, tr.pointer(axiom.getFirstProperty()), tr.pointer(axiom.getSecondProperty())));
    }

    @Override
    public AxiomInterface visit(OWLHasKeyAxiom axiom) {
        // translateObjectPropertySet(axiom.getObjectPropertyExpressions());
        // TDLObjectRoleExpression objectPropertyPointer = kernel
        // .getObjectPropertyKey();
        // translateDataPropertySet(axiom.getDataPropertyExpressions());
        LOGGER.error("JFact Kernel: unsupported operation 'getDataPropertyKey' {}", axiom);
        return Axioms.dummy();
        // TDLDataRoleExpression dataPropertyPointer = kernel
        // .getDataPropertyKey();
        // return kernel.tellHasKey(
        // toClassPointer(axiom.getClassExpression()),
        // dataPropertyPointer, objectPropertyPointer);
    }

    @Override
    public AxiomInterface visit(OWLDatatypeDefinitionAxiom axiom) {
        LOGGER.error("JFact Kernel: unsupported operation 'OWLDatatypeDefinitionAxiom' {}", axiom);
        return Axioms.dummy();
        // kernel.getDataSubType(axiom.getDatatype().getIRI().toString(),
        // toDataTypeExpressionPointer(axiom.getDataRange()));
    }

    @Override
    public AxiomInterface visit(SWRLRule rule) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomInterface visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomInterface visit(OWLAnnotationPropertyDomainAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomInterface visit(OWLAnnotationPropertyRangeAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }
}
