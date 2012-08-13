package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/// semantic locality checker for DL axioms
public class SemanticLocalityChecker implements DLAxiomVisitor, LocalityChecker {
    // / Reasoner to detect the tautology
    ReasoningKernel Kernel;
    // / Expression manager of a kernel
    ExpressionManager pEM;
    // / map between axioms and concept expressions
    Map<Axiom, ConceptExpression> ExprMap = new HashMap<Axiom, ConceptExpression>();

    // / @return expression necessary to build query for a given type of an
    // axiom; @return NULL if none necessary
    protected ConceptExpression getExpr(Axiom axiom) {
        if (axiom instanceof AxiomRelatedTo) {
            return pEM.value(((AxiomRelatedTo) axiom).getRelation(),
                    ((AxiomRelatedTo) axiom).getRelatedIndividual());
        }
        if (axiom instanceof AxiomValueOf) {
            return pEM.value(((AxiomValueOf) axiom).getAttribute(),
                    ((AxiomValueOf) axiom).getValue());
        }
        if (axiom instanceof AxiomORoleDomain) {
            return pEM.exists(((AxiomORoleDomain) axiom).getRole(), pEM.top());
        }
        if (axiom instanceof AxiomORoleRange) {
            return pEM.exists(((AxiomORoleRange) axiom).getRole(),
                    pEM.not(((AxiomORoleRange) axiom).getRange()));
        }
        if (axiom instanceof AxiomDRoleDomain) {
            return pEM.exists(((AxiomDRoleDomain) axiom).getRole(), pEM.dataTop());
        }
        if (axiom instanceof AxiomDRoleRange) {
            return pEM.exists(((AxiomDRoleRange) axiom).getRole(),
                    pEM.dataNot(((AxiomDRoleRange) axiom).getRange()));
        }
        if (axiom instanceof AxiomRelatedToNot) {
            return pEM.not(pEM.value(((AxiomRelatedToNot) axiom).getRelation(),
                    ((AxiomRelatedToNot) axiom).getRelatedIndividual()));
        }
        if (axiom instanceof AxiomValueOfNot) {
            return pEM.not(pEM.value(((AxiomValueOfNot) axiom).getAttribute(),
                    ((AxiomValueOfNot) axiom).getValue()));
        }
        // everything else doesn't require expression to be build
        return null;
    }

    // / signature to keep
    TSignature sig;

    public TSignature getSignature() {
        return sig;
    }

    // / set a new value of a signature (without changing a locality parameters)
    public void setSignatureValue(TSignature Sig) {
        sig = Sig;
    }

    // / remember the axiom locality value here
    boolean isLocal;

    // / init c'tor
    public SemanticLocalityChecker(ReasoningKernel k, TSignature s) {
        Kernel = k;
        sig = s;
        isLocal = true;
        pEM = Kernel.getExpressionManager();
        // for tests we will need TB names to be from the OWL 2 namespace
        pEM.setTopBottomRoles("http://www.w3.org/2002/07/owl#topObjectProperty",
                "http://www.w3.org/2002/07/owl#bottomObjectProperty",
                "http://www.w3.org/2002/07/owl#topDataProperty",
                "http://www.w3.org/2002/07/owl#bottomDataProperty");
        Kernel.setSignature(sig);
    }

    // set fields
    // / @return true iff an AXIOM is local wrt defined policy
    public boolean local(Axiom axiom) {
        axiom.accept(this);
        return isLocal;
    }

    // / init kernel with the ontology signature
    public void preprocessOntology(Collection<Axiom> axioms) {
        TSignature s = new TSignature();
        ExprMap.clear();
        for (Axiom q : axioms) {
            ExprMap.put(q, getExpr(q));
            s.add(q.getSignature());
        }
        Kernel.clearKB();
        // register all the objects in the ontology signature
        for (NamedEntity p : s.begin()) {
            Kernel.declare(null, (Expression) p);
        }
        // prepare the reasoner to check tautologies
        Kernel.realiseKB();
        // after TBox appears there, set signature to translate
        Kernel.setSignature(getSignature());
        // disallow usage of the expression cache as same expressions will lead
        // to different translations
        Kernel.setIgnoreExprCache(true);
    }

    // / load ontology to a given KB
    public void visitOntology(Ontology ontology) {
        for (Axiom p : ontology.getAxioms()) {
            if (p.isUsed()) {
                p.accept(this);
            }
        }
    }

    public void visit(AxiomDeclaration axiom) {
        isLocal = true;
    }

    public void visit(AxiomEquivalentConcepts axiom) {
        isLocal = false;
        List<ConceptExpression> arguments = axiom.getArguments();
        int size = arguments.size();
        ConceptExpression C = arguments.get(0);
        for (int i = 1; i < size; i++) {
            ConceptExpression p = arguments.get(i);
            if (!Kernel.isEquivalent(C, p)) {
                return;
            }
        }
        isLocal = true;
    }

    public void visit(AxiomDisjointConcepts axiom) {
        isLocal = false;
        List<ConceptExpression> arguments = axiom.getArguments();
        int size = arguments.size();
        for (int i = 0; i < size; i++) {
            ConceptExpression p = arguments.get(i);
            for (int j = i + 1; j < size; j++) {
                ConceptExpression q = arguments.get(j);
                if (!Kernel.isDisjoint(p, q)) {
                    return;
                }
            }
        }
        isLocal = true;
    }

    // / FIXME!! fornow
    public void visit(AxiomDisjointUnion axiom) {
        isLocal = true;
    }

    public void visit(AxiomEquivalentORoles axiom) {
        isLocal = false;
        List<ObjectRoleExpression> arguments = axiom.getArguments();
        int size = arguments.size();
        ObjectRoleExpression R = arguments.get(0);
        for (int i = 1; i < size; i++) {
            if (!(Kernel.isSubRoles(R, arguments.get(i)) && Kernel.isSubRoles(
                    arguments.get(i), R))) {
                return;
            }
        }
        isLocal = true;
    }

    // tautology if all the subsumptions Ri [= Rj holds
    public void visit(AxiomEquivalentDRoles axiom) {
        isLocal = false;
        List<DataRoleExpression> arguments = axiom.getArguments();
        DataRoleExpression R = arguments.get(0);
        for (int i = 1; i < arguments.size(); i++) {
            if (!(Kernel.isSubRoles(R, arguments.get(i)) && Kernel.isSubRoles(
                    arguments.get(i), R))) {
                return;
            }
        }
        isLocal = true;
    }

    public void visit(AxiomDisjointORoles axiom) {
        isLocal = Kernel.isDisjointObjectRoles(axiom.getArguments());
    }

    public void visit(AxiomDisjointDRoles axiom) {
        isLocal = Kernel.isDisjointDataRoles(axiom.getArguments());
    }

    // never local
    public void visit(AxiomSameIndividuals axiom) {
        isLocal = false;
    }

    // never local
    public void visit(AxiomDifferentIndividuals axiom) {
        isLocal = false;
    }

    // / there is no such axiom in OWL API, but I hope nobody would use Fairness
    // here
    public void visit(AxiomFairnessConstraint axiom) {
        isLocal = true;
    }

    // R = inverse(S) is tautology iff R [= S- and S [= R-
    public void visit(AxiomRoleInverse axiom) {
        isLocal = Kernel.isSubRoles(axiom.getRole(), pEM.inverse(axiom.getInvRole()))
                && Kernel.isSubRoles(axiom.getInvRole(), pEM.inverse(axiom.getRole()));
    }

    public void visit(AxiomORoleSubsumption axiom) {
        // check whether the LHS is a role chain
        if (axiom.getSubRole() instanceof ObjectRoleChain) {
            isLocal = Kernel.isSubChain(axiom.getRole(),
                    ((ObjectRoleChain) axiom.getSubRole()).getArguments());
            return;
        }
        // check whether the LHS is a plain rle or inverse
        if (axiom.getSubRole() instanceof ObjectRoleExpression) {
            isLocal = Kernel.isSubRoles(axiom.getSubRole(), axiom.getRole());
            return;
        }
        // here we have a projection expression. FIXME!! for now
        isLocal = true;
    }

    public void visit(AxiomDRoleSubsumption axiom) {
        isLocal = Kernel.isSubRoles(axiom.getSubRole(), axiom.getRole());
    }

    // Domain(R) = C is tautology iff ER.Top [= C
    public void visit(AxiomORoleDomain axiom) {
        isLocal = Kernel.isSubsumedBy(ExprMap.get(axiom), axiom.getDomain());
    }

    public void visit(AxiomDRoleDomain axiom) {
        isLocal = Kernel.isSubsumedBy(ExprMap.get(axiom), axiom.getDomain());
    }

    // Range(R) = C is tautology iff ER.~C is unsatisfiable
    public void visit(AxiomORoleRange axiom) {
        isLocal = !Kernel.isSatisfiable(ExprMap.get(axiom));
    }

    public void visit(AxiomDRoleRange axiom) {
        isLocal = !Kernel.isSatisfiable(ExprMap.get(axiom));
    }

    public void visit(AxiomRoleTransitive axiom) {
        isLocal = Kernel.isTransitive(axiom.getRole());
    }

    public void visit(AxiomRoleReflexive axiom) {
        isLocal = Kernel.isReflexive(axiom.getRole());
    }

    public void visit(AxiomRoleIrreflexive axiom) {
        isLocal = Kernel.isIrreflexive(axiom.getRole());
    }

    public void visit(AxiomRoleSymmetric axiom) {
        isLocal = Kernel.isSymmetric(axiom.getRole());
    }

    public void visit(AxiomRoleAsymmetric axiom) {
        isLocal = Kernel.isAsymmetric(axiom.getRole());
    }

    public void visit(AxiomORoleFunctional axiom) {
        isLocal = Kernel.isFunctional(axiom.getRole());
    }

    public void visit(AxiomDRoleFunctional axiom) {
        isLocal = Kernel.isFunctional(axiom.getRole());
    }

    public void visit(AxiomRoleInverseFunctional axiom) {
        isLocal = Kernel.isInverseFunctional(axiom.getRole());
    }

    public void visit(AxiomConceptInclusion axiom) {
        isLocal = Kernel.isSubsumedBy(axiom.getSubConcept(), axiom.getSupConcept());
    }

    // for top locality, this might be local
    public void visit(AxiomInstanceOf axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), axiom.getC());
    }

    public void visit(AxiomRelatedTo axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    public void visit(AxiomRelatedToNot axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    public void visit(AxiomValueOf axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    public void visit(AxiomValueOfNot axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }
}
