package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.compose;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapitools.decomposition.AxiomWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDifferentIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointUnion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomInstanceOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedTo;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedToNot;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleAsymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverseFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleIrreflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleReflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleSymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleTransitive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomSameIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOfNot;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.Axioms;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

/** axiom translator */
public class AxiomTranslator implements OWLAxiomVisitorEx<AxiomWrapper>, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxiomTranslator.class);
    @Nonnull
    private final DeclarationVisitorEx v;
    private Ontology o;
    private TranslationMachinery tr;

    /**
     * @param o o
     * @param df df
     * @param tr tr
     */
    public AxiomTranslator(Ontology o, OWLDataFactory df, TranslationMachinery tr) {
        v = new DeclarationVisitorEx(o, df, tr);
        this.o = o;
        this.tr = tr;
    }

    @Override
    public AxiomWrapper visit(OWLSubClassOfAxiom axiom) {
        return o.add(new AxiomConceptInclusion(axiom, tr.pointer(axiom.getSubClass()),
            tr.pointer(axiom.getSuperClass())));
    }

    @Override
    public AxiomWrapper visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return o.add(new AxiomRelatedToNot(axiom, tr.pointer(axiom.getSubject()),
            tr.pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomWrapper visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleAsymmetric(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleReflexive(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLDisjointClassesAxiom axiom) {
        return o.add(new AxiomDisjointConcepts(axiom,
            translateClassExpressionSet(axiom.classExpressions())));
    }

    private List<ConceptExpression> translateClassExpressionSet(
        Stream<? extends OWLClassExpression> classExpressions) {
        return asList(classExpressions.map(tr::pointer));
    }

    @Override
    public AxiomWrapper visit(OWLDataPropertyDomainAxiom axiom) {
        return o.add(new AxiomDRoleDomain(axiom, tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getDomain())));
    }

    @Override
    public AxiomWrapper visit(OWLObjectPropertyDomainAxiom axiom) {
        return o.add(new AxiomORoleDomain(axiom, tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getDomain())));
    }

    @Override
    public AxiomWrapper visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        return o
            .add(new AxiomEquivalentORoles(axiom, translateObjectPropertySet(axiom.properties())));
    }

    private List<ObjectRoleExpression> translateObjectPropertySet(
        Stream<OWLObjectPropertyExpression> properties) {
        return asList(properties.map(tr::pointer));
    }

    @Override
    public AxiomWrapper visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return o.add(new AxiomValueOfNot(axiom, tr.pointer(axiom.getSubject()),
            tr.pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomWrapper visit(OWLDifferentIndividualsAxiom axiom) {
        return o.add(new AxiomDifferentIndividuals(axiom, tr.translate(axiom.individuals())));
    }

    @Override
    public AxiomWrapper visit(OWLDisjointDataPropertiesAxiom axiom) {
        return o.add(new AxiomDisjointDRoles(axiom, translateDataPropertySet(axiom.properties())));
    }

    private List<DataRoleExpression> translateDataPropertySet(
        Stream<OWLDataPropertyExpression> properties) {
        return asList(properties.map(tr::pointer));
    }

    @Override
    public AxiomWrapper visit(OWLDisjointObjectPropertiesAxiom axiom) {
        return o
            .add(new AxiomDisjointORoles(axiom, translateObjectPropertySet(axiom.properties())));
    }

    @Override
    public AxiomWrapper visit(OWLObjectPropertyRangeAxiom axiom) {
        return o.add(new AxiomORoleRange(axiom, tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getRange())));
    }

    @Override
    public AxiomWrapper visit(OWLObjectPropertyAssertionAxiom axiom) {
        return o.add(new AxiomRelatedTo(axiom, tr.pointer(axiom.getSubject()),
            tr.pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomWrapper visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return o.add(new AxiomORoleFunctional(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLSubObjectPropertyOfAxiom axiom) {
        return o.add(new AxiomORoleSubsumption(axiom, tr.pointer(axiom.getSubProperty()),
            tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLDisjointUnionAxiom axiom) {
        return o.add(new AxiomDisjointUnion(axiom, tr.pointer(axiom.getOWLClass()),
            translateClassExpressionSet(axiom.classExpressions())));
    }

    @Override
    public AxiomWrapper visit(OWLDeclarationAxiom axiom) {
        OWLEntity entity = axiom.getEntity();
        return entity.accept(v);
    }

    @Override
    public AxiomWrapper visit(OWLAnnotationAssertionAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomWrapper visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleSymmetric(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLDataPropertyRangeAxiom axiom) {
        return o.add(new AxiomDRoleRange(axiom, tr.pointer(axiom.getProperty()),
            tr.pointer(axiom.getRange())));
    }

    @Override
    public AxiomWrapper visit(OWLFunctionalDataPropertyAxiom axiom) {
        return o.add(new AxiomDRoleFunctional(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLEquivalentDataPropertiesAxiom axiom) {
        return o
            .add(new AxiomEquivalentDRoles(axiom, translateDataPropertySet(axiom.properties())));
    }

    @Override
    public AxiomWrapper visit(OWLClassAssertionAxiom axiom) {
        return o.add(new AxiomInstanceOf(axiom, tr.pointer(axiom.getIndividual()),
            tr.pointer(axiom.getClassExpression())));
    }

    @Override
    public AxiomWrapper visit(OWLEquivalentClassesAxiom axiom) {
        return o.add(new AxiomEquivalentConcepts(axiom,
            translateClassExpressionSet(axiom.classExpressions())));
    }

    @Override
    public AxiomWrapper visit(OWLDataPropertyAssertionAxiom axiom) {
        return o.add(new AxiomValueOf(axiom, tr.pointer(axiom.getSubject()),
            tr.pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomWrapper visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleTransitive(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleIrreflexive(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLSubDataPropertyOfAxiom axiom) {
        return o.add(new AxiomDRoleSubsumption(axiom, tr.pointer(axiom.getSubProperty()),
            tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleInverseFunctional(axiom, tr.pointer(axiom.getProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLSameIndividualAxiom axiom) {
        return o.add(new AxiomSameIndividuals(axiom, tr.translate(axiom.individuals())));
    }

    @Override
    public AxiomWrapper visit(OWLSubPropertyChainOfAxiom axiom) {
        if (axiom.getPropertyChain().size() < 2) {
            LOGGER.error("Invalid axiom ignored: {}", axiom);
            return Axioms.dummy();
        }
        return o.add(new AxiomORoleSubsumption(axiom,
            compose(translateObjectPropertySet(axiom.getPropertyChain().stream())),
            tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLInverseObjectPropertiesAxiom axiom) {
        return o.add(new AxiomRoleInverse(axiom, tr.pointer(axiom.getFirstProperty()),
            tr.pointer(axiom.getSecondProperty())));
    }

    @Override
    public AxiomWrapper visit(OWLHasKeyAxiom axiom) {
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
    public AxiomWrapper visit(OWLDatatypeDefinitionAxiom axiom) {
        LOGGER.error("JFact Kernel: unsupported operation 'OWLDatatypeDefinitionAxiom' {}", axiom);
        return Axioms.dummy();
        // kernel.getDataSubType(axiom.getDatatype().getIRI().toString(),
        // toDataTypeExpressionPointer(axiom.getDataRange()));
    }

    @Override
    public AxiomWrapper visit(SWRLRule rule) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomWrapper visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomWrapper visit(OWLAnnotationPropertyDomainAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }

    @Override
    public AxiomWrapper visit(OWLAnnotationPropertyRangeAxiom axiom) {
        // Ignore
        return Axioms.dummy();
    }
}
