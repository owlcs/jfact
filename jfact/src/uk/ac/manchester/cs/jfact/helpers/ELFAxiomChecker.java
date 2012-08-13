package uk.ac.manchester.cs.jfact.helpers;

import java.util.*;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorAdapter;

@SuppressWarnings("unused")
// XXX verify unused parameters
public class ELFAxiomChecker extends DLAxiomVisitorAdapter {
    ELFExpressionChecker eCh = new ELFExpressionChecker();
    boolean value;

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
    public void visitOntology(Ontology ontology) {
        value = true;
        for (Axiom p : ontology.getAxioms()) {
            if (p.isUsed()) {
                p.accept(this);
            }
        }
    }

    public ELFAxiomChecker() {
        value = true;
    }

    public boolean value() {
        return value;
    }
}

// / pattern for the rule. Contains apply() method with updates of the monitored
// set
class TELFRule {
    // / reasoner that is used to add actions. The number of rules = the number
    // of axioms, so the price is not too bad memory-wise.
    ELFReasoner ER;

    // / init c'tor
    TELFRule(ELFReasoner er) {
        ER = er;
    }

    // / apply rule with fresh class C added to watching part
    void apply(TELFConcept addedC) {}

    // / apply rule with fresh pair (C,D) added to watching part
    void apply(TELFConcept addedC, TELFConcept addedD) {}
}

// -------------------------------------------------------------
// Concepts and roles, i.e. S(C) and R(C,D)
// -------------------------------------------------------------
// / aux class to support set of rules and rule applications
class TRuleSet {
    // / set of rules to apply on change
    List<TELFRule> Rules = new ArrayList<TELFRule>();

    // / apply all rules with a single argument
    void applyRules(TELFConcept addedC) {
        for (TELFRule p : Rules) {
            p.apply(addedC);
        }
    }

    // / apply all rules with two arguments
    void applyRules(TELFConcept addedC, TELFConcept addedD) {
        for (TELFRule p : Rules) {
            p.apply(addedC, addedD);
        }
    }

    // / add rule to a set
    void addRule(TELFRule rule) {
        Rules.add(rule);
    }
}

// / concept, set S(C) and aux things
class TELFConcept extends TRuleSet {
    // / original concept (if any)
    ConceptExpression Origin;
    // / set of supers (built during classification)
    Set<TELFConcept> Supers = new HashSet<TELFConcept>();

    // / add C to supers
    void addSuper(TELFConcept C) {
        Supers.add(C);
    }

    // / empty c'tor
    TELFConcept() {
        Origin = null;
    }

    // / init c'tor
    TELFConcept(ConceptExpression origin) {
        Origin = origin;
    }

    // / check whether concept C is contained in supers
    boolean hasSuper(TELFConcept C) {
        return Supers.contains(C);
    }

    // / add an super concept
    void addC(TELFConcept C) {
        if (hasSuper(C)) {
            return;
        }
        addSuper(C);
        this.applyRules(C);
    }
}

// / role, set R(C,D)
class TELFRole extends TRuleSet {
    // / original role (if any)
    ObjectRoleExpression Origin;
    // / map itself
    Map<TELFConcept, Set<TELFConcept>> PredMap = new HashMap<TELFConcept, Set<TELFConcept>>();

    // / add (C,D) to label
    void addLabel(TELFConcept C, TELFConcept D) {
        PredMap.get(D).add(C);
    }

    // / empty c'tor
    TELFRole() {
        Origin = null;
    }

    // / init c'tor
    TELFRole(ObjectRoleExpression origin) {
        Origin = origin;
    }

    // / get the (possibly empty) set of predecessors of given D
    Set<TELFConcept> getPredSet(TELFConcept D) {
        return PredMap.get(D);
    }

    Iterable<Map.Entry<TELFConcept, Set<TELFConcept>>> begin() {
        return PredMap.entrySet();
    }

    // / check whether (C,D) is in the R-set
    boolean hasLabel(TELFConcept C, TELFConcept D) {
        return PredMap.get(D).contains(C);
    }

    // / add pair (C,D) to a set
    void addR(TELFConcept C, TELFConcept D) {
        if (hasLabel(C, D)) {
            return;
        }
        addLabel(C, D);
        this.applyRules(C, D);
    }
}

// -------------------------------------------------------------
// Action class
// -------------------------------------------------------------
// / single algorithm action (application of a rule)
class ELFAction {
    // / role R corresponded to R(C,D)
    TELFRole R = null;
    // / concept C; to add
    TELFConcept C = null;
    // / concept D; to add
    TELFConcept D = null;

    // / init c'tor for C action
    ELFAction(TELFConcept c, TELFConcept d) {
        R = null;
        C = c;
        D = d;
    }

    // / init c'tor for R action
    ELFAction(TELFRole r, TELFConcept c, TELFConcept d) {
        R = r;
        C = c;
        D = d;
    }

    // / action itself, depending on the R state
    void apply() {
        if (R != null) {
            R.addR(C, D);
        } else {
            C.addC(D);
        }
    }
}
