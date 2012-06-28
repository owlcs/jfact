package uk.ac.manchester.cs.jfact.split;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
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
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/// semantic locality checker for DL axioms
public class SemanticLocalityChecker implements DLAxiomVisitor, LocalityChecker {
    /// Reasoner to detect the tautology
    ReasoningKernel Kernel;
    /// Expression manager of a kernel
    ExpressionManager pEM;
    /// map between axioms and concept expressions
    Map<Axiom, ConceptExpression> ExprMap = new HashMap<Axiom, ConceptExpression>();

    /// @return expression necessary to build query for a given type of an axiom; @return NULL if none necessary
    protected ConceptExpression getExpr(final Axiom axiom) {
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

    /// signature to keep
    TSignature sig;

    public TSignature getSignature() {
        return sig;
    }

    /// set a new value of a signature (without changing a locality parameters)
    public void setSignatureValue(final TSignature Sig) {
        sig = Sig;
    }

    /// remember the axiom locality value here
    boolean isLocal;

    /// init c'tor
    public SemanticLocalityChecker(final ReasoningKernel k, final TSignature s) {
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
    /// @return true iff an AXIOM is local wrt defined policy
    public boolean local(final Axiom axiom) {
        axiom.accept(this);
        return isLocal;
    }

    /// init kernel with the ontology signature
    public void preprocessOntology(final Collection<Axiom> axioms) {
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
        // disallow usage of the expression cache as same expressions will lead to different translations
        Kernel.setIgnoreExprCache(true);
    }

    /// load ontology to a given KB
    public void visitOntology(final Ontology ontology) {
        for (Axiom p : ontology.getAxioms()) {
            if (p.isUsed()) {
                p.accept(this);
            }
        }
    }

    public void visit(final AxiomDeclaration axiom) {
        isLocal = true;
    }

    public void visit(final AxiomEquivalentConcepts axiom) {
        isLocal = false;
        final List<ConceptExpression> arguments = axiom.getArguments();
        final int size = arguments.size();
        ConceptExpression C = arguments.get(0);
        for (int i = 1; i < size; i++) {
            ConceptExpression p = arguments.get(i);
            if (!Kernel.isEquivalent(C, p)) {
                return;
            }
        }
        isLocal = true;
    }

    public void visit(final AxiomDisjointConcepts axiom) {
        isLocal = false;
        final List<ConceptExpression> arguments = axiom.getArguments();
        final int size = arguments.size();
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

    /// FIXME!! fornow
    public void visit(final AxiomDisjointUnion axiom) {
        isLocal = true;
    }

    public void visit(final AxiomEquivalentORoles axiom) {
        isLocal = false;
        final List<ObjectRoleExpression> arguments = axiom.getArguments();
        final int size = arguments.size();
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
    public void visit(final AxiomEquivalentDRoles axiom) {
        isLocal = false;
        final List<DataRoleExpression> arguments = axiom.getArguments();
        DataRoleExpression R = arguments.get(0);
        for (int i = 1; i < arguments.size(); i++) {
            if (!(Kernel.isSubRoles(R, arguments.get(i)) && Kernel.isSubRoles(
                    arguments.get(i), R))) {
                return;
            }
        }
        isLocal = true;
    }

    public void visit(final AxiomDisjointORoles axiom) {
        isLocal = Kernel.isDisjointObjectRoles(axiom.getArguments());
    }

    public void visit(final AxiomDisjointDRoles axiom) {
        isLocal = Kernel.isDisjointDataRoles(axiom.getArguments());
    }

    // never local
    public void visit(final AxiomSameIndividuals axiom) {
        isLocal = false;
    }

    // never local
    public void visit(final AxiomDifferentIndividuals axiom) {
        isLocal = false;
    }

    /// there is no such axiom in OWL API, but I hope nobody would use Fairness here
    public void visit(final AxiomFairnessConstraint axiom) {
        isLocal = true;
    }

    // R = inverse(S) is tautology iff R [= S- and S [= R-
    public void visit(final AxiomRoleInverse axiom) {
        isLocal = Kernel.isSubRoles(axiom.getRole(), pEM.inverse(axiom.getInvRole()))
                && Kernel.isSubRoles(axiom.getInvRole(), pEM.inverse(axiom.getRole()));
    }

    public void visit(final AxiomORoleSubsumption axiom) {
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

    public void visit(final AxiomDRoleSubsumption axiom) {
        isLocal = Kernel.isSubRoles(axiom.getSubRole(), axiom.getRole());
    }

    // Domain(R) = C is tautology iff ER.Top [= C
    public void visit(final AxiomORoleDomain axiom) {
        isLocal = Kernel.isSubsumedBy(ExprMap.get(axiom), axiom.getDomain());
    }

    public void visit(final AxiomDRoleDomain axiom) {
        isLocal = Kernel.isSubsumedBy(ExprMap.get(axiom), axiom.getDomain());
    }

    // Range(R) = C is tautology iff ER.~C is unsatisfiable
    public void visit(final AxiomORoleRange axiom) {
        isLocal = !Kernel.isSatisfiable(ExprMap.get(axiom));
    }

    public void visit(final AxiomDRoleRange axiom) {
        isLocal = !Kernel.isSatisfiable(ExprMap.get(axiom));
    }

    public void visit(final AxiomRoleTransitive axiom) {
        isLocal = Kernel.isTransitive(axiom.getRole());
    }

    public void visit(final AxiomRoleReflexive axiom) {
        isLocal = Kernel.isReflexive(axiom.getRole());
    }

    public void visit(final AxiomRoleIrreflexive axiom) {
        isLocal = Kernel.isIrreflexive(axiom.getRole());
    }

    public void visit(final AxiomRoleSymmetric axiom) {
        isLocal = Kernel.isSymmetric(axiom.getRole());
    }

    public void visit(final AxiomRoleAsymmetric axiom) {
        isLocal = Kernel.isAsymmetric(axiom.getRole());
    }

    public void visit(final AxiomORoleFunctional axiom) {
        isLocal = Kernel.isFunctional(axiom.getRole());
    }

    public void visit(final AxiomDRoleFunctional axiom) {
        isLocal = Kernel.isFunctional(axiom.getRole());
    }

    public void visit(final AxiomRoleInverseFunctional axiom) {
        isLocal = Kernel.isInverseFunctional(axiom.getRole());
    }

    public void visit(final AxiomConceptInclusion axiom) {
        isLocal = Kernel.isSubsumedBy(axiom.getSubConcept(), axiom.getSupConcept());
    }

    // for top locality, this might be local
    public void visit(final AxiomInstanceOf axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), axiom.getC());
    }

    public void visit(final AxiomRelatedTo axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    public void visit(final AxiomRelatedToNot axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    public void visit(final AxiomValueOf axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }

    public void visit(final AxiomValueOfNot axiom) {
        isLocal = Kernel.isInstance(axiom.getIndividual(), ExprMap.get(axiom));
    }
}
