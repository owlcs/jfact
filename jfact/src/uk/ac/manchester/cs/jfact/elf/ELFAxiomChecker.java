package uk.ac.manchester.cs.jfact.elf;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorAdapter;
import conformance.PortedFrom;

// XXX verify unused parameters
/** ELF axiom checker */
@PortedFrom(file = "ELFAxiomChecker.h", name = "ELFAxiomChecker")
public class ELFAxiomChecker extends DLAxiomVisitorAdapter {
    @PortedFrom(file = "ELFAxiomChecker.h", name = "eCh")
    ELFExpressionChecker eCh = new ELFExpressionChecker();
    @PortedFrom(file = "ELFAxiomChecker.h", name = "value")
    boolean value;

    @PortedFrom(file = "ELFAxiomChecker.h", name = "v")
    boolean v(Expression expr) {
        value = eCh.v(expr);
        return value;
    }

    @Override
    public void visit(AxiomDeclaration axiom) {
        v(axiom.getDeclaration());
    }

    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        value = false;
        for (Expression p : axiom.getArguments()) {
            if (!v(p)) {
                return;
            }
        }
        value = true;
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        value = false;
        for (Expression p : axiom.getArguments()) {
            if (!v(p)) {
                return;
            }
        }
        value = true;
    }

    @Override
    public void visit(AxiomDisjointUnion axiom) {
        value = axiom.size() > 1;
    }

    @Override
    public void visit(AxiomEquivalentORoles axiom) {
        value = false;
        for (Expression p : axiom.getArguments()) {
            if (!v(p)) {
                return;
            }
        }
        value = true;
    }

    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomSameIndividuals axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomDifferentIndividuals axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomFairnessConstraint axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRoleInverse axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        if (v(axiom.getSubRole())) {
            v(axiom.getRole());
        }
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        value = false;
    }

    // FIXME!! check later
    @Override
    public void visit(AxiomORoleDomain axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        value = false;
    }

    // FIXME!! check later
    @Override
    public void visit(AxiomORoleRange axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        value = true;
    }

    @Override
    public void visit(AxiomRoleReflexive axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomConceptInclusion axiom) {
        if (v(axiom.getSubConcept())) {
            v(axiom.getSupConcept());
        }
    }

    @Override
    public void visit(AxiomInstanceOf axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        value = false;
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        value = false;
    }

    @Override
    @PortedFrom(file = "ELFAxiomChecker.h", name = "visitOntology")
    public void visitOntology(Ontology ontology) {
        value = true;
        for (AxiomInterface p : ontology.getAxioms()) {
            if (p.isUsed()) {
                p.accept(this);
            }
        }
    }

    @SuppressWarnings("javadoc")
    public ELFAxiomChecker() {
        value = true;
    }

    /** @return true if check successful */
    // TODO turn into a visitorEx
    @PortedFrom(file = "ELFAxiomChecker.h", name = "value")
    public boolean value() {
        return value;
    }
}
