package uk.ac.manchester.cs.jfact;

import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.compose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

/** axiom translator */
public class AxiomTranslator implements OWLAxiomVisitorEx<AxiomInterface>,
        Serializable {

    private static final long serialVersionUID = 11000L;
    @Nonnull
    private final DeclarationVisitorEx v;
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
    public AxiomTranslator(Ontology o, OWLDataFactory df,
            TranslationMachinery tr) {
        v = new DeclarationVisitorEx(o, df, tr);
        this.o = o;
        this.tr = tr;
    }

    @Override
    public AxiomInterface visit(OWLSubClassOfAxiom axiom) {
        return o.add(new AxiomConceptInclusion(axiom, tr.pointer(axiom
                .getSubClass()), tr.pointer(axiom.getSuperClass())));
    }

    @Override
    public AxiomInterface visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return o.add(new AxiomRelatedToNot(axiom,
                tr.pointer(axiom.getSubject()),
                tr.pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleAsymmetric(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleReflexive(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLDisjointClassesAxiom axiom) {
        return o.add(new AxiomDisjointConcepts(axiom,
                translateClassExpressionSet(axiom.getClassExpressions())));
    }

    private List<ConceptExpression> translateClassExpressionSet(
            Set<OWLClassExpression> classExpressions) {
        List<ConceptExpression> l = new ArrayList<>();
        for (OWLClassExpression ce : classExpressions) {
            l.add(tr.pointer(ce));
        }
        return l;
    }

    @Override
    public AxiomInterface visit(OWLDataPropertyDomainAxiom axiom) {
        return o.add(new AxiomDRoleDomain(axiom,
                tr.pointer(axiom.getProperty()), tr.pointer(axiom.getDomain())));
    }

    @Override
    public AxiomInterface visit(OWLObjectPropertyDomainAxiom axiom) {
        return o.add(new AxiomORoleDomain(axiom,
                tr.pointer(axiom.getProperty()), tr.pointer(axiom.getDomain())));
    }

    @Override
    public AxiomInterface visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        return o.add(new AxiomEquivalentORoles(axiom,
                translateObjectPropertySet(axiom.getProperties())));
    }

    private List<ObjectRoleExpression> translateObjectPropertySet(
            Collection<OWLObjectPropertyExpression> properties) {
        List<ObjectRoleExpression> l = new ArrayList<>();
        for (OWLObjectPropertyExpression property : properties) {
            l.add(tr.pointer(property));
        }
        return l;
    }

    @Override
    public AxiomInterface visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return o.add(new AxiomValueOfNot(axiom, tr.pointer(axiom.getSubject()),
                tr.pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLDifferentIndividualsAxiom axiom) {
        return o.add(new AxiomDifferentIndividuals(axiom, tr.translate(axiom
                .getIndividuals())));
    }

    @Override
    public AxiomInterface visit(OWLDisjointDataPropertiesAxiom axiom) {
        return o.add(new AxiomDisjointDRoles(axiom,
                translateDataPropertySet(axiom.getProperties())));
    }

    private List<DataRoleExpression> translateDataPropertySet(
            Set<OWLDataPropertyExpression> properties) {
        List<DataRoleExpression> l = new ArrayList<>();
        for (OWLDataPropertyExpression property : properties) {
            l.add(tr.pointer(property));
        }
        return l;
    }

    @Override
    public AxiomInterface visit(OWLDisjointObjectPropertiesAxiom axiom) {
        return o.add(new AxiomDisjointORoles(axiom,
                translateObjectPropertySet(axiom.getProperties())));
    }

    @Override
    public AxiomInterface visit(OWLObjectPropertyRangeAxiom axiom) {
        return o.add(new AxiomORoleRange(axiom,
                tr.pointer(axiom.getProperty()), tr.pointer(axiom.getRange())));
    }

    @Override
    public AxiomInterface visit(OWLObjectPropertyAssertionAxiom axiom) {
        return o.add(new AxiomRelatedTo(axiom, tr.pointer(axiom.getSubject()),
                tr.pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return o.add(new AxiomORoleFunctional(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLSubObjectPropertyOfAxiom axiom) {
        return o.add(new AxiomORoleSubsumption(axiom, tr.pointer(axiom
                .getSubProperty()), tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomInterface visit(OWLDisjointUnionAxiom axiom) {
        return o.add(new AxiomDisjointUnion(axiom, tr.pointer(axiom
                .getOWLClass()), translateClassExpressionSet(axiom
                .getClassExpressions())));
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
        return o.add(new AxiomRoleSymmetric(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLDataPropertyRangeAxiom axiom) {
        return o.add(new AxiomDRoleRange(axiom,
                tr.pointer(axiom.getProperty()), tr.pointer(axiom.getRange())));
    }

    @Override
    public AxiomInterface visit(OWLFunctionalDataPropertyAxiom axiom) {
        return o.add(new AxiomDRoleFunctional(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLEquivalentDataPropertiesAxiom axiom) {
        return o.add(new AxiomEquivalentDRoles(axiom,
                translateDataPropertySet(axiom.getProperties())));
    }

    @Override
    public AxiomInterface visit(OWLClassAssertionAxiom axiom) {
        return o.add(new AxiomInstanceOf(axiom, tr.pointer(axiom
                .getIndividual()), tr.pointer(axiom.getClassExpression())));
    }

    @Override
    public AxiomInterface visit(OWLEquivalentClassesAxiom axiom) {
        return o.add(new AxiomEquivalentConcepts(axiom,
                translateClassExpressionSet(axiom.getClassExpressions())));
    }

    @Override
    public AxiomInterface visit(OWLDataPropertyAssertionAxiom axiom) {
        return o.add(new AxiomValueOf(axiom, tr.pointer(axiom.getSubject()), tr
                .pointer(axiom.getProperty()), tr.pointer(axiom.getObject())));
    }

    @Override
    public AxiomInterface visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleTransitive(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleIrreflexive(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLSubDataPropertyOfAxiom axiom) {
        return o.add(new AxiomDRoleSubsumption(axiom, tr.pointer(axiom
                .getSubProperty()), tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomInterface visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return o.add(new AxiomRoleInverseFunctional(axiom, tr.pointer(axiom
                .getProperty())));
    }

    @Override
    public AxiomInterface visit(OWLSameIndividualAxiom axiom) {
        return o.add(new AxiomSameIndividuals(axiom, tr.translate(axiom
                .getIndividuals())));
    }

    @Override
    public AxiomInterface visit(OWLSubPropertyChainOfAxiom axiom) {
        return o.add(new AxiomORoleSubsumption(axiom,
                compose(translateObjectPropertySet(axiom.getPropertyChain())),
                tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public AxiomInterface visit(OWLInverseObjectPropertiesAxiom axiom) {
        return o.add(new AxiomRoleInverse(axiom, tr.pointer(axiom
                .getFirstProperty()), tr.pointer(axiom.getSecondProperty())));
    }

    @Override
    public AxiomInterface visit(OWLHasKeyAxiom axiom) {
        // translateObjectPropertySet(axiom.getObjectPropertyExpressions());
        // TDLObjectRoleExpression objectPropertyPointer = kernel
        // .getObjectPropertyKey();
        // translateDataPropertySet(axiom.getDataPropertyExpressions());
        throw new ReasonerInternalException(
                "JFact Kernel: unsupported operation 'getDataPropertyKey' "
                        + axiom);
        // TDLDataRoleExpression dataPropertyPointer = kernel
        // .getDataPropertyKey();
        // return kernel.tellHasKey(
        // toClassPointer(axiom.getClassExpression()),
        // dataPropertyPointer, objectPropertyPointer);
    }

    @Override
    public AxiomInterface visit(OWLDatatypeDefinitionAxiom axiom) {
        throw new ReasonerInternalException(
                "JFact Kernel: unsupported operation 'OWLDatatypeDefinitionAxiom' "
                        + axiom);
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
