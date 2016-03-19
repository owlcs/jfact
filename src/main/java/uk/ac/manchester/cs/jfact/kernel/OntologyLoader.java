package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.helpers.DLTreeFactory.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;

/** ontology loader */
@PortedFrom(file = "tOntologyLoader.h", name = "TOntologyLoader")
public class OntologyLoader implements DLAxiomVisitor, Serializable {

    /** KB to load the ontology */
    @PortedFrom(file = "tOntologyLoader.h", name = "kb") private final TBox tbox;
    /** Transforms TDLExpression hierarchy to the DLTree */
    @PortedFrom(file = "tOntologyLoader.h", name = "ETrans") private final ExpressionTranslator expressionTranslator;

    /**
     * @param kb
     *        KB
     */
    public OntologyLoader(TBox kb) {
        tbox = kb;
        expressionTranslator = new ExpressionTranslator(kb);
    }

    /**
     * get role by the DLTree; throw exception if unable
     * 
     * @param r
     *        r
     * @param reason
     *        reason
     * @return role
     */
    @PortedFrom(file = "tOntologyLoader.h", name = "getRole")
    private Role getRole(RoleExpression r, String reason) {
        try {
            return Role.resolveRole(r.accept(expressionTranslator));
        } catch (OWLRuntimeException e) {
            throw new ReasonerInternalException(reason + '\t' + e.getMessage(), e);
        }
    }

    /**
     * @param i
     *        I
     * @param reason
     *        reason
     * @return an individual be the DLTree; throw exception if unable
     */
    @PortedFrom(file = "tOntologyLoader.h", name = "getIndividual")
    public Individual getIndividual(IndividualExpression i, String reason) {
        try {
            DLTree individual = i.accept(expressionTranslator);
            return (Individual) tbox.getCI(individual);
        } catch (OWLRuntimeException e) {
            throw new ReasonerInternalException(reason + '\t' + e.getMessage(), e);
        }
    }

    /**
     * ensure that the expression EXPR has its named entities linked to the KB
     * ones
     * 
     * @param expr
     *        Expr
     * @return verified input
     */
    @PortedFrom(file = "tOntologyLoader.h", name = "ensureNames")
    public Expression ensureNames(Expression expr) {
        assert expr != null; // TODO temporarily
        return expr;
    }

    /**
     * prepare arguments for the [begin,end) interval
     * 
     * @param c
     *        c
     * @param <T>
     *        type
     * @return list of arguments
     */
    @PortedFrom(file = "tOntologyLoader.h", name = "prepareArgList")
    private <T extends Expression> List<DLTree> prepareArgList(Collection<T> c) {
        return asList(c.stream().map(t -> ensureNames(t).accept(expressionTranslator)));
    }

    @Override
    public void visit(AxiomDeclaration axiom) {
        ensureNames(axiom.getDeclaration());
        // names in the KB
        axiom.getDeclaration().accept(expressionTranslator);
    }

    // n-ary axioms
    @Override
    public void visit(AxiomEquivalentConcepts axiom) {
        tbox.processEquivalentC(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomDisjointConcepts axiom) {
        tbox.processDisjointC(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomEquivalentORoles axiom) {
        tbox.processEquivalentR(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomEquivalentDRoles axiom) {
        tbox.processEquivalentR(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomDisjointORoles axiom) {
        tbox.processDisjointR(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomDisjointDRoles axiom) {
        tbox.processDisjointR(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomDisjointUnion axiom) {
        // first make a disjoint axiom
        tbox.processDisjointC(prepareArgList(axiom.getArguments()));
        // now define C as a union-of axiom
        List<DLTree> argList = new ArrayList<>();
        ensureNames(axiom.getConcept());
        argList.add(axiom.getConcept().accept(expressionTranslator));
        List<DLTree> list = asList(axiom.getArguments().stream().map(p -> p.accept(expressionTranslator)));
        argList.add(createSNFOr(list));
        tbox.processEquivalentC(argList);
    }

    @Override
    public void visit(AxiomSameIndividuals axiom) {
        tbox.processSame(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomDifferentIndividuals axiom) {
        tbox.processDifferent(prepareArgList(axiom.getArguments()));
    }

    @Override
    public void visit(AxiomFairnessConstraint axiom) {
        tbox.setFairnessConstraintDLTrees(prepareArgList(axiom.getArguments()));
    }

    // role axioms
    @Override
    public void visit(AxiomRoleInverse axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getInvRole());
        Role r = getRole(axiom.getRole(), "Role expression expected in Role Inverse axiom");
        Role iR = getRole(axiom.getInvRole(), "Role expression expected in Role Inverse axiom");
        tbox.getRM(r);
        RoleMaster.addRoleSynonym(iR.inverse(), r);
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getSubRole());
        DLTree sub = axiom.getSubRole().accept(expressionTranslator);
        Role r = getRole(axiom.getRole(), "Role expression expected in Object Roles Subsumption axiom");
        tbox.getRM(r);
        RoleMaster.addRoleParent(sub, r);
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getSubRole());
        Role r = getRole(axiom.getRole(), "Role expression expected in Data Roles Subsumption axiom");
        Role s = getRole(axiom.getSubRole(), "Role expression expected in Data Roles Subsumption axiom");
        tbox.getDRM();
        RoleMaster.addRoleParentProper(s, r);
    }

    @Override
    public void visit(AxiomORoleDomain axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getDomain());
        Role r = getRole(axiom.getRole(), "Role expression expected in Object Role Domain axiom");
        DLTree c = axiom.getDomain().accept(expressionTranslator);
        if (r.isTop()) {
            // add GCI
            tbox.addSubsumeAxiom(createTop(), c);
        } else if (!r.isBottom()) {
            // nothing to do for bottom
            r.setDomain(c);
        }
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getDomain());
        Role r = getRole(axiom.getRole(), "Role expression expected in Data Role Domain axiom");
        DLTree c = axiom.getDomain().accept(expressionTranslator);
        if (r.isTop()) {
            // add GCI
            tbox.addSubsumeAxiom(createTop(), c);
        } else if (!r.isBottom()) {
            // nothing to do for bottom
            r.setDomain(c);
        }
    }

    @Override
    public void visit(AxiomORoleRange axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getRange());
        Role r = getRole(axiom.getRole(), "Role expression expected in Object Role Range axiom");
        DLTree c = axiom.getRange().accept(expressionTranslator);
        if (r.isTop()) {
            // add GCI
            tbox.addSubsumeAxiom(createTop(), c);
        } else if (!r.isBottom()) {
            // nothing to do for bottom
            r.setRange(c);
        }
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getRange());
        getRole(axiom.getRole(), "Role expression expected in Data Role Range axiom").setRange(axiom.getRange().accept(
            expressionTranslator));
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(), "Role expression expected in Role Transitivity axiom");
        if (!role.isTop() && !role.isBottom()) {
            role.setTransitive(true);
        }
    }

    @Override
    public void visit(AxiomRoleReflexive axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(), "Role expression expected in Role Reflexivity axiom");
        if (role.isBottom()) {
            throw new InconsistentOntologyException("Top role used in reflexivity axiom " + axiom);
        }
        if (!role.isTop()) {
            role.setReflexive(true);
        }
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        ensureNames(axiom.getRole());
        Role r = getRole(axiom.getRole(), "Role expression expected in Role Irreflexivity axiom");
        if (r.isTop()) {
            throw new InconsistentOntologyException("Top role used in irreflexivity axiom " + axiom);
        }
        if (!r.isBottom()) {
            r.setDomain(createSNFNot(DLTreeFactory.createSNFSelf(axiom.getRole().accept(expressionTranslator))));
            r.setDomain(createSNFNot(buildTree(new Lexeme(Token.SELF), axiom.getRole().accept(expressionTranslator))));
            r.setIrreflexive(true);
        }
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        ensureNames(axiom.getRole());
        Role r = getRole(axiom.getRole(), "Role expression expected in Role Symmetry axiom");
        if (!r.isTop() && !r.isBottom()) {
            r.setSymmetric(true);
            tbox.getORM();
            RoleMaster.addRoleParentProper(r, r.inverse());
        }
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        ensureNames(axiom.getRole());
        Role r = getRole(axiom.getRole(), "Role expression expected in Role Asymmetry axiom");
        if (r.isTop()) {
            throw new InconsistentOntologyException("Top role used in asymmetry axiom " + axiom);
        }
        if (!r.isBottom()) {
            r.setAsymmetric(true);
            tbox.getORM().addDisjointRoles(r, r.inverse());
        }
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(), "Role expression expected in Object Role Functionality axiom");
        if (role.isTop()) {
            throw new InconsistentOntologyException("Top role used in role functionality axiom " + axiom);
        }
        if (!role.isBottom()) {
            role.setFunctional();
        }
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(), "Role expression expected in Data Role Functionality axiom");
        if (role.isTop()) {
            throw new InconsistentOntologyException("Top role used in role functionality axiom " + axiom);
        }
        if (!role.isBottom()) {
            role.setFunctional();
        }
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(), "Role expression expected in Role Inverse Functionality axiom");
        if (role.isTop()) {
            throw new InconsistentOntologyException("Top role used in inverse functional axiom " + axiom);
        }
        if (!role.isBottom()) {
            role.inverse().setFunctional();
        }
    }

    // concept/individual axioms
    @Override
    public void visit(AxiomConceptInclusion axiom) {
        ensureNames(axiom.getSubConcept());
        ensureNames(axiom.getSupConcept());
        DLTree c = axiom.getSubConcept().accept(expressionTranslator);
        DLTree d = axiom.getSupConcept().accept(expressionTranslator);
        tbox.addSubsumeAxiom(c, d);
    }

    @Override
    public void visit(AxiomInstanceOf axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getC());
        getIndividual(axiom.getIndividual(), "Individual expected in Instance axiom");
        DLTree i = axiom.getIndividual().accept(expressionTranslator);
        DLTree c = axiom.getC().accept(expressionTranslator);
        tbox.addSubsumeAxiom(i, c);
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getRelation());
        ensureNames(axiom.getRelatedIndividual());
        Role r = getRole(axiom.getRelation(), "Role expression expected in Related To axiom");
        if (r.isBottom()) {
            throw new InconsistentOntologyException("Top role used in assertion axiom " + axiom);
        }
        if (!r.isTop()) {
            Individual i = getIndividual(axiom.getIndividual(), "Individual expected in Related To axiom");
            Individual j = getIndividual(axiom.getRelatedIndividual(), "Individual expected in Related To axiom");
            tbox.registerIndividualRelation(i, r, j);
        }
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getRelation());
        ensureNames(axiom.getRelatedIndividual());
        Role r = getRole(axiom.getRelation(), "Role expression expected in Related To Not axiom");
        if (r.isTop()) {
            // inconsistent ontology
            throw new InconsistentOntologyException("Top role used in negatove assertion axiom " + axiom);
        }
        if (!r.isBottom()) {
            // make sure everything is consistent
            getIndividual(axiom.getIndividual(), "Individual expected in Related To Not axiom");
            getIndividual(axiom.getRelatedIndividual(), "Individual expected in Related To Not axiom");
            // make an axiom i:AR.\neg{j}
            tbox.addSubsumeAxiom(axiom.getIndividual().accept(expressionTranslator), createSNFForall(axiom.getRelation()
                .accept(expressionTranslator), createSNFNot(axiom.getRelatedIndividual().accept(
                    expressionTranslator))));
        }
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getAttribute());
        getIndividual(axiom.getIndividual(), "Individual expected in Value Of axiom");
        Role r = getRole(axiom.getAttribute(), "Role expression expected in Value Of axiom");
        if (r.isBottom()) {
            throw new InconsistentOntologyException("Top role used in property assertion axiom " + axiom);
        }
        if (!r.isTop()) {
            // nothing to do for universal role
            // make an axiom i:EA.V
            tbox.addSubsumeAxiom(axiom.getIndividual().accept(expressionTranslator), createSNFExists(axiom
                .getAttribute().accept(expressionTranslator), axiom.getValue().accept(expressionTranslator)));
        }
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getAttribute());
        getIndividual(axiom.getIndividual(), "Individual expected in Value Of Not axiom");
        Role r = getRole(axiom.getAttribute(), "Role expression expected in Value Of Not axiom");
        if (r.isTop()) {
            throw new InconsistentOntologyException("Top role used in negative property assertion axiom " + axiom);
        }
        if (!r.isBottom()) {
            // make an axiom i:AA.\neg V
            tbox.addSubsumeAxiom(axiom.getIndividual().accept(expressionTranslator), createSNFForall(axiom
                .getAttribute().accept(expressionTranslator), createSNFNot(axiom.getValue().accept(
                    expressionTranslator))));
        }
    }

    /**
     * load ontology to a given KB
     * 
     * @param ontology
     *        ontology
     */
    @PortedFrom(file = "tOntologyLoader.h", name = "visitOntology")
    public void visitOntology(Ontology ontology) {
        ontology.getAxioms().stream().filter(p -> p.isUsed()).forEach(p -> ((AxiomImpl) p).accept(this));
        tbox.finishLoading();
    }
}
