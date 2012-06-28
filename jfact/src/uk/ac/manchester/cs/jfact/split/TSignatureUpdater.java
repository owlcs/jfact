package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDifferentIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointUnion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomFairnessConstraint;
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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/// update signature by adding the signature of a given axiom to it
public class TSignatureUpdater implements DLAxiomVisitor {
    /// helper with expressions
    TExpressionSignatureUpdater Updater;

    /// helper for the expression processing
    void v(final Expression E) {
        E.accept(Updater);
    }

    /// helper for the [begin,end) interval
    void v(final Collection<? extends Expression> arg) {
        for (Expression e : arg) {
            v(e);
        }
    }

    public void visit(final AxiomDeclaration axiom) {
        v(axiom.getDeclaration());
    }

    public void visit(final AxiomEquivalentConcepts axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomDisjointConcepts axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomDisjointUnion axiom) {
        v(axiom.getC());
        v(axiom.getArguments());
    }

    public void visit(final AxiomEquivalentORoles axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomEquivalentDRoles axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomDisjointORoles axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomDisjointDRoles axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomSameIndividuals axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomDifferentIndividuals axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomFairnessConstraint axiom) {
        v(axiom.getArguments());
    }

    public void visit(final AxiomRoleInverse axiom) {
        v(axiom.getRole());
        v(axiom.getInvRole());
    }

    public void visit(final AxiomORoleSubsumption axiom) {
        v(axiom.getRole());
        v(axiom.getSubRole());
    }

    public void visit(final AxiomDRoleSubsumption axiom) {
        v(axiom.getRole());
        v(axiom.getSubRole());
    }

    public void visit(final AxiomORoleDomain axiom) {
        v(axiom.getRole());
        v(axiom.getDomain());
    }

    public void visit(final AxiomDRoleDomain axiom) {
        v(axiom.getRole());
        v(axiom.getDomain());
    }

    public void visit(final AxiomORoleRange axiom) {
        v(axiom.getRole());
        v(axiom.getRange());
    }

    public void visit(final AxiomDRoleRange axiom) {
        v(axiom.getRole());
        v(axiom.getRange());
    }

    public void visit(final AxiomRoleTransitive axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomRoleReflexive axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomRoleIrreflexive axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomRoleSymmetric axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomRoleAsymmetric axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomORoleFunctional axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomDRoleFunctional axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomRoleInverseFunctional axiom) {
        v(axiom.getRole());
    }

    public void visit(final AxiomConceptInclusion axiom) {
        v(axiom.getSubConcept());
        v(axiom.getSupConcept());
    }

    public void visit(final AxiomInstanceOf axiom) {
        v(axiom.getIndividual());
        v(axiom.getC());
    }

    public void visit(final AxiomRelatedTo axiom) {
        v(axiom.getIndividual());
        v(axiom.getRelation());
        v(axiom.getRelatedIndividual());
    }

    public void visit(final AxiomRelatedToNot axiom) {
        v(axiom.getIndividual());
        v(axiom.getRelation());
        v(axiom.getRelatedIndividual());
    }

    public void visit(final AxiomValueOf axiom) {
        v(axiom.getIndividual());
        v(axiom.getAttribute());
    }

    public void visit(final AxiomValueOfNot axiom) {
        v(axiom.getIndividual());
        v(axiom.getAttribute());
    }

    public TSignatureUpdater(final TSignature sig) {
        Updater = new TExpressionSignatureUpdater(sig);
    }

    /// load ontology to a given KB
    public void visitOntology(final Ontology ontology) {
        for (Axiom p : ontology.getAxioms()) {
            if (p.isUsed()) {
                p.accept(this);
            }
        }
    }
}
