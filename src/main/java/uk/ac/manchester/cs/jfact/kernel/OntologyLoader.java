package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;
import uk.ac.manchester.cs.jfact.split.SplitVarEntry;
import uk.ac.manchester.cs.jfact.split.TSplitVar;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import conformance.PortedFrom;

/** ontology loader */
@PortedFrom(file = "tOntologyLoader.h", name = "TOntologyLoader")
public class OntologyLoader implements DLAxiomVisitor, Serializable {
    private static final long serialVersionUID = 11000L;
    /** KB to load the ontology */
    @PortedFrom(file = "tOntologyLoader.h", name = "kb")
    private final TBox tbox;
    /** Transforms TDLExpression hierarchy to the DLTree */
    @PortedFrom(file = "tOntologyLoader.h", name = "ETrans")
    private final ExpressionTranslator expressionTranslator;

    /** get role by the DLTree; throw exception if unable */
    @PortedFrom(file = "tOntologyLoader.h", name = "getRole")
    private Role getRole(RoleExpression r, String reason) {
        try {
            return Role.resolveRole(r.accept(expressionTranslator));
        } catch (OWLRuntimeException e) {
            throw new ReasonerInternalException(reason + "\t" + e.getMessage(), e);
        }
    }

    /** @param I
     * @param reason
     * @return an individual be the DLTree; throw exception if unable */
    @PortedFrom(file = "tOntologyLoader.h", name = "getIndividual")
    public Individual getIndividual(IndividualExpression I, String reason) {
        DLTree i = I.accept(expressionTranslator);
        if (i == null) {
            throw new ReasonerInternalException(reason);
        }
        return (Individual) tbox.getCI(i);
    }

    /** ensure that the expression EXPR has its named entities linked to the KB
     * ones
     * 
     * @param Expr */
    @PortedFrom(file = "tOntologyLoader.h", name = "ensureNames")
    public void ensureNames(Expression Expr) {
        assert Expr != null; // temporarily
    }

    /** prepare arguments for the [begin,end) interval */
    @PortedFrom(file = "tOntologyLoader.h", name = "prepareArgList")
    private <T extends Expression> List<DLTree> prepareArgList(Collection<T> c) {
        List<DLTree> ArgList = new ArrayList<DLTree>();
        for (T t : c) {
            ensureNames(t);
            ArgList.add(t.accept(expressionTranslator));
        }
        return ArgList;
    }

    @PortedFrom(file = "tOntologyLoader.h", name = "fillSplit")
    private void fillSplit(TSplitVar sv) {
        sv.setC(tbox.getConcept(sv.getOldName().getName()));
        sv.getC().setNonClassifiable(true);
        for (SplitVarEntry p : sv.getEntries()) {
            Concept C = tbox.getConcept(p.name.getName());
            C.setSystem();
            p.concept = C;
        }
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
        List<DLTree> ArgList = new ArrayList<DLTree>();
        ensureNames(axiom.getConcept());
        ArgList.add(axiom.getConcept().accept(expressionTranslator));
        List<DLTree> list = new ArrayList<DLTree>();
        for (Expression p : axiom.getArguments()) {
            list.add(p.accept(expressionTranslator));
        }
        ArgList.add(DLTreeFactory.createSNFOr(list));
        tbox.processEquivalentC(ArgList);
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
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Role Inverse axiom");
        Role iR = getRole(axiom.getInvRole(),
                "Role expression expected in Role Inverse axiom");
        tbox.getRM(R).addRoleSynonym(iR.inverse(), R);
    }

    @Override
    public void visit(AxiomORoleSubsumption axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getSubRole());
        DLTree Sub = axiom.getSubRole().accept(expressionTranslator);
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Object Roles Subsumption axiom");
        tbox.getRM(R).addRoleParent(Sub, R);
    }

    @Override
    public void visit(AxiomDRoleSubsumption axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getSubRole());
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Data Roles Subsumption axiom");
        Role S = getRole(axiom.getSubRole(),
                "Role expression expected in Data Roles Subsumption axiom");
        tbox.getDRM().addRoleParentProper(S, R);
    }

    @Override
    public void visit(AxiomORoleDomain axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getDomain());
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Object Role Domain axiom");
        DLTree C = axiom.getDomain().accept(expressionTranslator);
        if (R.isTop()) {
            // add GCI
            tbox.addSubsumeAxiom(DLTreeFactory.createTop(), C);
        } else if (!R.isBottom()) {
            // nothing to do for bottom
            R.setDomain(C);
        }
    }

    @Override
    public void visit(AxiomDRoleDomain axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getDomain());
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Data Role Domain axiom");
        DLTree C = axiom.getDomain().accept(expressionTranslator);
        if (R.isTop()) {
            // add GCI
            tbox.addSubsumeAxiom(DLTreeFactory.createTop(), C);
        } else if (!R.isBottom()) {
            // nothing to do for bottom
            R.setDomain(C);
        }
    }

    @Override
    public void visit(AxiomORoleRange axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getRange());
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Object Role Range axiom");
        DLTree C = axiom.getRange().accept(expressionTranslator);
        if (R.isTop()) {
            // add GCI
            tbox.addSubsumeAxiom(DLTreeFactory.createTop(), C);
        } else if (!R.isBottom()) {
            // nothing to do for bottom
            R.setRange(C);
        }
    }

    @Override
    public void visit(AxiomDRoleRange axiom) {
        ensureNames(axiom.getRole());
        ensureNames(axiom.getRange());
        getRole(axiom.getRole(), "Role expression expected in Data Role Range axiom")
                .setRange(axiom.getRange().accept(expressionTranslator));
    }

    @Override
    public void visit(AxiomRoleTransitive axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(),
                "Role expression expected in Role Transitivity axiom");
        if (!role.isTop() && !role.isBottom()) {
            role.setTransitive(true);
        }
    }

    @Override
    public void visit(AxiomRoleReflexive axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(),
                "Role expression expected in Role Reflexivity axiom");
        if (role.isBottom()) {
            throw new InconsistentOntologyException();
        }
        if (!role.isTop()) {
            role.setReflexive(true);
        }
    }

    @Override
    public void visit(AxiomRoleIrreflexive axiom) {
        ensureNames(axiom.getRole());
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Role Irreflexivity axiom");
        if (R.isTop()) {
            throw new InconsistentOntologyException();
        }
        if (!R.isBottom()) {
            R.setDomain(DLTreeFactory.createSNFNot(DLTreeFactory.createSNFSelf(axiom
                    .getRole().accept(expressionTranslator))));
            R.setDomain(DLTreeFactory.createSNFNot(DLTreeFactory.buildTree(new Lexeme(
                    Token.SELF), axiom.getRole().accept(expressionTranslator))));
            R.setIrreflexive(true);
        }
    }

    @Override
    public void visit(AxiomRoleSymmetric axiom) {
        ensureNames(axiom.getRole());
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Role Symmetry axiom");
        if (!R.isTop() && !R.isBottom()) {
            R.setSymmetric(true);
            tbox.getORM().addRoleParentProper(R, R.inverse());
        }
    }

    @Override
    public void visit(AxiomRoleAsymmetric axiom) {
        ensureNames(axiom.getRole());
        Role R = getRole(axiom.getRole(),
                "Role expression expected in Role Asymmetry axiom");
        if (R.isTop()) {
            throw new InconsistentOntologyException();
        }
        if (!R.isBottom()) {
            R.setAsymmetric(true);
            tbox.getORM().addDisjointRoles(R, R.inverse());
        }
    }

    @Override
    public void visit(AxiomORoleFunctional axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(),
                "Role expression expected in Object Role Functionality axiom");
        if (role.isTop()) {
            throw new InconsistentOntologyException();
        }
        if (!role.isBottom()) {
            role.setFunctional();
        }
    }

    @Override
    public void visit(AxiomDRoleFunctional axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(),
                "Role expression expected in Data Role Functionality axiom");
        if (role.isTop()) {
            throw new InconsistentOntologyException();
        }
        if (!role.isBottom()) {
            role.setFunctional();
        }
    }

    @Override
    public void visit(AxiomRoleInverseFunctional axiom) {
        ensureNames(axiom.getRole());
        Role role = getRole(axiom.getRole(),
                "Role expression expected in Role Inverse Functionality axiom");
        if (role.isTop()) {
            throw new InconsistentOntologyException();
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
        DLTree C = axiom.getSubConcept().accept(expressionTranslator);
        DLTree D = axiom.getSupConcept().accept(expressionTranslator);
        tbox.addSubsumeAxiom(C, D);
    }

    @Override
    public void visit(AxiomInstanceOf axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getC());
        getIndividual(axiom.getIndividual(), "Individual expected in Instance axiom");
        DLTree I = axiom.getIndividual().accept(expressionTranslator);
        DLTree C = axiom.getC().accept(expressionTranslator);
        tbox.addSubsumeAxiom(I, C);
    }

    @Override
    public void visit(AxiomRelatedTo axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getRelation());
        ensureNames(axiom.getRelatedIndividual());
        Role R = getRole(axiom.getRelation(),
                "Role expression expected in Related To axiom");
        if (R.isBottom()) {
            throw new InconsistentOntologyException();
        }
        if (!R.isTop()) {
            Individual I = getIndividual(axiom.getIndividual(),
                    "Individual expected in Related To axiom");
            Individual J = getIndividual(axiom.getRelatedIndividual(),
                    "Individual expected in Related To axiom");
            tbox.registerIndividualRelation(I, R, J);
        }
    }

    @Override
    public void visit(AxiomRelatedToNot axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getRelation());
        ensureNames(axiom.getRelatedIndividual());
        Role R = getRole(axiom.getRelation(),
                "Role expression expected in Related To Not axiom");
        if (R.isTop()) {
            // inconsistent ontology
            throw new InconsistentOntologyException();
        }
        if (!R.isBottom()) {
            // make sure everything is consistent
            getIndividual(axiom.getIndividual(),
                    "Individual expected in Related To Not axiom");
            getIndividual(axiom.getRelatedIndividual(),
                    "Individual expected in Related To Not axiom");
            // make an axiom i:AR.\neg{j}
            tbox.addSubsumeAxiom(axiom.getIndividual().accept(expressionTranslator),
                    DLTreeFactory.createSNFForall(
                            axiom.getRelation().accept(expressionTranslator),
                            DLTreeFactory.createSNFNot(axiom.getRelatedIndividual()
                                    .accept(expressionTranslator))));
        }
    }

    @Override
    public void visit(AxiomValueOf axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getAttribute());
        getIndividual(axiom.getIndividual(), "Individual expected in Value Of axiom");
        Role R = getRole(axiom.getAttribute(),
                "Role expression expected in Value Of axiom");
        if (R.isBottom()) {
            throw new InconsistentOntologyException();
        }
        if (!R.isTop()) {
            // nothing to do for universal role
            // make an axiom i:EA.V
            tbox.addSubsumeAxiom(axiom.getIndividual().accept(expressionTranslator),
                    DLTreeFactory.createSNFExists(
                            axiom.getAttribute().accept(expressionTranslator), axiom
                                    .getValue().accept(expressionTranslator)));
        }
    }

    @Override
    public void visit(AxiomValueOfNot axiom) {
        ensureNames(axiom.getIndividual());
        ensureNames(axiom.getAttribute());
        getIndividual(axiom.getIndividual(), "Individual expected in Value Of Not axiom");
        Role R = getRole(axiom.getAttribute(),
                "Role expression expected in Value Of Not axiom");
        if (R.isTop()) {
            throw new InconsistentOntologyException();
        }
        if (!R.isBottom()) {
            // make an axiom i:AA.\neg V
            tbox.addSubsumeAxiom(axiom.getIndividual().accept(expressionTranslator),
                    DLTreeFactory.createSNFForall(
                            axiom.getAttribute().accept(expressionTranslator),
                            DLTreeFactory.createSNFNot(axiom.getValue().accept(
                                    expressionTranslator))));
        }
    }

    /** @param KB */
    public OntologyLoader(TBox KB) {
        tbox = KB;
        expressionTranslator = new ExpressionTranslator(KB);
    }

    /** load ontology to a given KB
     * 
     * @param ontology */
    @PortedFrom(file = "tOntologyLoader.h", name = "visitOntology")
    public void visitOntology(Ontology ontology) {
        for (AxiomInterface p : ontology.getAxioms()) {
            if (p.isUsed()) {
                p.accept(this);
            }
        }
        for (TSplitVar q : ontology.getSplits().getEntries()) {
            fillSplit(q);
        }
        tbox.setSplitVars(ontology.getSplits());
        tbox.finishLoading();
    }
}
