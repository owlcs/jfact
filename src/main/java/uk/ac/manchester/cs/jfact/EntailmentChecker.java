package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
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
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
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
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

/** entailment checker */
public class EntailmentChecker implements OWLAxiomVisitorEx<Boolean>,
        Serializable {

    private static final long serialVersionUID = 11000L;
    private final ReasoningKernel kernel;
    private final TranslationMachinery tr;
    private final OWLDataFactory df;

    @Nonnull
    private static Boolean b(boolean b) {
        return Boolean.valueOf(b);
    }

    /**
     * @param k
     *        k
     * @param df
     *        df
     * @param tr
     *        tr
     */
    public EntailmentChecker(ReasoningKernel k, OWLDataFactory df,
            TranslationMachinery tr) {
        kernel = k;
        this.tr = tr;
        this.df = df;
    }

    @Override
    public Boolean visit(OWLSubClassOfAxiom axiom) {
        if (axiom.getSuperClass().equals(df.getOWLThing())
                || axiom.getSubClass().equals(df.getOWLNothing())) {
            return b(true);
        }
        ConceptExpression sub = tr.pointer(axiom.getSubClass());
        if (!kernel.isSatisfiable(sub)) {
            return true;
        }
        ConceptExpression sup = tr.pointer(axiom.getSuperClass());
        return b(kernel.isSubsumedBy(sub, sup));
    }

    @Override
    public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
        return kernel.isAsymmetric(tr.pointer(axiom.getProperty()));
    }

    @Override
    public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
        return b(kernel.isReflexive(tr.pointer(axiom.getProperty())));
    }

    @Override
    public Boolean visit(OWLDisjointClassesAxiom axiom) {
        List<OWLClassExpression> classExpressions = asList(axiom
                .classExpressions());
        if (classExpressions.size() == 2) {
            return b(kernel.isDisjoint(tr.pointer(classExpressions.get(0)),
                    tr.pointer(classExpressions.get(1))));
        }
        for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
            if (!ax.accept(this)) {
                return b(false);
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
        for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
            if (!ax.accept(this)) {
                return b(false);
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
        for (OWLSubClassOfAxiom ax : axiom.asOWLSubClassOfAxioms()) {
            if (!ax.accept(this)) {
                return b(false);
            }
        }
        return b(true);
    }

    // TODO: this check is incomplete
    @Override
    public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
        List<OWLDataPropertyExpression> l = asList(axiom.properties());
        for (int i = 0; i < l.size() - 1; i++) {
            for (int j = i + 1; j < l.size(); j++) {
                if (!kernel.isDisjointRoles(tr.pointer(l.get(i)),
                        tr.pointer(l.get(i)))) {
                    return b(false);
                }
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
        List<OWLObjectPropertyExpression> l = asList(axiom.properties());
        for (int i = 0; i < l.size() - 1; i++) {
            for (int j = i + 1; j < l.size(); j++) {
                if (!kernel.isDisjointRoles(tr.pointer(l.get(i)),
                        tr.pointer(l.get(i)))) {
                    return b(false);
                }
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
        return b(kernel.isFunctional(tr.pointer(axiom.getProperty())));
    }

    @Override
    public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
        return b(kernel.isSubRoles(tr.pointer(axiom.getSubProperty()),
                tr.pointer(axiom.getSuperProperty())));
    }

    @Override
    public Boolean visit(OWLDisjointUnionAxiom axiom) {
        return axiom.getOWLEquivalentClassesAxiom().accept(this)
                && axiom.getOWLDisjointClassesAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLDeclarationAxiom axiom) {
        return b(false);
    }

    @Override
    public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
        return b(false);
    }

    @Override
    public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
        return b(kernel.isSymmetric(tr.pointer(axiom.getProperty())));
    }

    @Override
    public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
        return b(kernel.isFunctional(tr.pointer(axiom.getProperty())));
    }

    @Override
    public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
        for (OWLAxiom ax : axiom.asSubDataPropertyOfAxioms()) {
            if (!ax.accept(this)) {
                return b(false);
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLClassAssertionAxiom axiom) {
        return b(kernel.isInstance(tr.pointer(axiom.getIndividual()),
                tr.pointer(axiom.getClassExpression())));
    }

    @Override
    public Boolean visit(OWLEquivalentClassesAxiom axiom) {
        List<OWLClassExpression> classExpressions = asList(axiom
                .classExpressions());
        if (classExpressions.size() == 2) {
            return b(kernel.isEquivalent(tr.pointer(classExpressions.get(0)),
                    tr.pointer(classExpressions.get(1))));
        }
        for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
            if (!ax.accept(this)) {
                return b(false);
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
        return axiom.asOWLSubClassOfAxiom().accept(this);
    }

    @Override
    public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
        return b(kernel.isTransitive(tr.pointer(axiom.getProperty())));
    }

    @Override
    public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
        return b(kernel.isIrreflexive(tr.pointer(axiom.getProperty())));
    }

    // TODO: this is incomplete
    @Override
    public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
        return kernel.isSubRoles(tr.pointer(axiom.getSubProperty()),
                tr.pointer(axiom.getSuperProperty()));
    }

    @Override
    public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        return kernel.isInverseFunctional(tr.pointer(axiom.getProperty()));
    }

    @Override
    public Boolean visit(OWLSameIndividualAxiom axiom) {
        for (OWLSameIndividualAxiom ax : axiom.asPairwiseAxioms()) {
            Iterator<OWLIndividual> it = ax.individuals().iterator();
            OWLIndividual indA = it.next();
            OWLIndividual indB = it.next();
            if (!kernel.isSameIndividuals(tr.pointer(indA), tr.pointer(indB))) {
                return b(false);
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
        List<ObjectRoleExpression> l = new ArrayList<>();
        for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
            l.add(tr.pointer(p));
        }
        return b(kernel.isSubChain(tr.pointer(axiom.getSuperProperty()), l));
    }

    @Override
    public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
        for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
            if (!ax.accept(this)) {
                return b(false);
            }
        }
        return b(true);
    }

    @Override
    public Boolean visit(OWLHasKeyAxiom axiom) {
        // FIXME!! unsupported by FaCT++ ATM
        // return null;
        throw new UnsupportedEntailmentTypeException(axiom);
    }

    @Override
    public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
        // FIXME!! unsupported by FaCT++ ATM
        // return null;
        throw new UnsupportedEntailmentTypeException(axiom);
    }

    @Override
    public Boolean visit(SWRLRule rule) {
        // FIXME!! unsupported by FaCT++ ATM
        // return null;
        throw new UnsupportedEntailmentTypeException(rule);
    }

    @Override
    public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return b(false);
    }

    @Override
    public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return b(false);
    }

    @Override
    public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return b(false);
    }
}
