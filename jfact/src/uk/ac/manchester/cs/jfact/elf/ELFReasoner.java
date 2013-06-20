package uk.ac.manchester.cs.jfact.elf;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import conformance.PortedFrom;

/** elf reasoner */
@PortedFrom(file = "ELFReasoner.h", name = "ELFReasoner")
public class ELFReasoner {
    /** S(C) structure */
    @PortedFrom(file = "ELFReasoner.h", name = "CVec")
    List<TELFConcept> CVec;
    /** set or all concepts */
    @PortedFrom(file = "ELFReasoner.h", name = "CMap")
    Map<ConceptExpression, TELFConcept> CMap;
    /** TOP Concept */
    @PortedFrom(file = "ELFReasoner.h", name = "CTop")
    TELFConcept CTop = null;
    /** BOTTOM Concept */
    @PortedFrom(file = "ELFReasoner.h", name = "CBot")
    TELFConcept CBot = null;
    /** map between roles and structures */
    @PortedFrom(file = "ELFReasoner.h", name = "RMap")
    Map<ObjectRoleExpression, TELFRole> RMap;
    /** queue of actions to perform */
    @PortedFrom(file = "ELFReasoner.h", name = "queue")
    List<ELFAction> queue;
    /** stat counters */
    @PortedFrom(file = "ELFReasoner.h", name = "nC2R")
    int nC2C;
    @PortedFrom(file = "ELFReasoner.h", name = "nA2C")
    int nA2C;
    @PortedFrom(file = "ELFReasoner.h", name = "nC2E")
    int nC2E;
    @PortedFrom(file = "ELFReasoner.h", name = "nE2C")
    int nE2C;
    @PortedFrom(file = "ELFReasoner.h", name = "nR2R")
    int nR2R;
    @PortedFrom(file = "ELFReasoner.h", name = "nC2R")
    int nC2R;

    /** get concept (expression) corresponding to a given DL expression */
    @PortedFrom(file = "ELFReasoner.h", name = "getC")
    protected TELFConcept getC(ConceptExpression p) {
        TELFConcept i = CMap.get(p);
        if (i != null) {
            return i;
        }
        // add new concept
        TELFConcept ret = new TELFConcept(p);
        CMap.put(p, ret);
        return ret;
    }

    /** get role (expression, but actually just a name) */
    @PortedFrom(file = "ELFReasoner.h", name = "getR")
    TELFRole getR(ObjectRoleExpression p) {
        TELFRole r = RMap.get(p);
        if (r != null) {
            return r;
        }
        TELFRole ret = new TELFRole(p);
        RMap.put(p, ret);
        return ret;
    }

    // process different normalized axioms
    // process original axioms
    /** c'tor: take the ontology and init internal structures
     * 
     * @param c
     * @param ont */
    public ELFReasoner(JFactReasonerConfiguration c, Ontology ont) {
        nC2C = 0;
        nA2C = 0;
        nC2E = 0;
        nE2C = 0;
        nR2R = 0;
        nC2R = 0;
        // init top- and bottom entities
        CBot = getC(ont.getExpressionManager().bottom());
        CTop = getC(ont.getExpressionManager().top());
        for (AxiomInterface p : ont.getAxioms()) {
            if (p.isUsed()) {
                if (p instanceof AxiomConceptInclusion) {
                    processCI((AxiomConceptInclusion) p);
                } else if (p instanceof AxiomORoleSubsumption) {
                    processRI((AxiomORoleSubsumption) p);
                } else {
                    processDeclaration((AxiomDeclaration) p);
                }
            }
        }
        // now prepare rules for \bot with roles (if role filler is \bot, then
        // so do domain)
        initBotRules();
        // dump statistics
        c.getLog().print("\nELFReasoner.ELFReasoner() \nFound ").print(nC2C)
                .print(" axioms in the form C [= D\nFound ").print(nA2C)
                .print(" axioms in the form C1/\\C2 [= D\nFound ").print(nC2E)
                .print(" axioms in the form C [= ER.D\nFound ").print(nE2C)
                .print(" axioms in the form ER.C [= D\nFound ").print(nR2R)
                .print(" axioms in the form R [= S\nFound ").print(nC2R)
                .print(
                " axioms in the form R o S [= T\n");
    }

    /** add action to a queue */
    @PortedFrom(file = "ELFReasoner.h", name = "addAction")
    void addAction(ELFAction action) {
        queue.add(action);
    }

    /** classification method */
    @PortedFrom(file = "ELFReasoner.h", name = "classify")
    public void classify() {
        // init all CIs
        for (TELFConcept C : CMap.values()) {
            C.addC(CTop);
            C.addC(C);
        }
        // apply all rules
        while (!queue.isEmpty()) {
            queue.remove(0).apply();
        }
    }

    // inline ELFReasoner implementation
    /** process axiom C [= D */
    @PortedFrom(file = "ELFReasoner.h", name = "processC2C")
    void processC2C(TELFConcept C, TELFConcept D) {
        ++nC2C;
        C.addRule(new CSubRule(this, D));
    }

    /** process axiom C1 and C2 [= D */
    @PortedFrom(file = "ELFReasoner.h", name = "processA2C")
    void processA2C(TELFConcept C1, TELFConcept C2, TELFConcept D) {
        ++nA2C;
        C1.addRule(new CAndSubRule(this, C2, D));
        C2.addRule(new CAndSubRule(this, C1, D));
    }

    /** process axiom C [= \ER.D */
    @PortedFrom(file = "ELFReasoner.h", name = "processC2E")
    void processC2E(TELFConcept C, TELFRole R, TELFConcept D) {
        ++nC2E;
        C.addRule(new RAddRule(this, R, D));
    }

    /** process axiom \ER.C [= D */
    @PortedFrom(file = "ELFReasoner.h", name = "processE2C")
    void processE2C(TELFRole R, TELFConcept C, TELFConcept D) {
        ++nE2C;
        // C from existential will have a C-adder rule
        C.addRule(new CAddFillerRule(this, R, D));
        // R from the existential will have a C-adder here
        R.addRule(new CExistSubRule(this, C, D));
    }

    /** process axiom R [= S */
    @PortedFrom(file = "ELFReasoner.h", name = "processR2R")
    void processR2R(TELFRole R, TELFRole S) {
        ++nR2R;
        R.addRule(new RSubRule(this, S));
    }

    /** process axiom R1 o R2 [= S */
    @PortedFrom(file = "ELFReasoner.h", name = "processC2R")
    void processC2R(TELFRole R1, TELFRole R2, TELFRole S) {
        ++nC2R;
        R1.addRule(new RChainLRule(this, R2, S));
        R2.addRule(new RChainRRule(this, R1, S));
    }

    /** process concept inclusion axiom into the internal structures */
    @PortedFrom(file = "ELFReasoner.h", name = "processCI")
    protected void processCI(AxiomConceptInclusion axiom) {
        assert axiom != null;
        // deal with existentials
        if (axiom.getSupConcept() instanceof ConceptObjectExists) // C [= \E R.D
        {
            ConceptObjectExists Exists = (ConceptObjectExists) axiom.getSupConcept();
            processC2E(getC(axiom.getSubConcept()), getR(Exists.getOR()),
                    getC(Exists.getConcept()));
            return;
        }
        // now RHS is a concept name or \bottom; record it
        TELFConcept D = getC(axiom.getSupConcept());
        // try to check if LHS is existential
        if (axiom.getSubConcept() instanceof ConceptObjectExists) // \E R.C [= D
        {
            ConceptObjectExists Exists = (ConceptObjectExists) axiom.getSubConcept();
            processE2C(getR(Exists.getOR()), getC(Exists.getConcept()), D);
            return;
        }
        if (axiom.getSubConcept() instanceof ConceptAnd) // C1 \and C2 [= D
        {
            ConceptAnd And = (ConceptAnd) axiom.getSubConcept();
            assert And.size() == 2;
            processA2C(getC(And.getArguments().get(0)), getC(And.getArguments().get(1)),
                    D);
            return;
        }
        // the only possible thing here is C [= D
        processC2C(getC(axiom.getSubConcept()), D);
    }

    /** process role inclusion axiom into the internal structures */
    @PortedFrom(file = "ELFReasoner.h", name = "processRI")
    void processRI(AxiomORoleSubsumption axiom) {
        TELFRole rhs = getR(axiom.getRole());
        if (axiom.getSubRole() instanceof ObjectRoleChain) // R o S [= T
        {
            ObjectRoleChain Chain = (ObjectRoleChain) axiom.getSubRole();
            assert Chain.size() == 2;
            processC2R(getR(Chain.getArguments().get(0)), getR(Chain.getArguments()
                    .get(1)), rhs);
        } else {
            ObjectRoleExpression R = (ObjectRoleExpression) axiom.getSubRole();
            assert R != null;
            processR2R(getR(R), rhs);
        }
    }

    /** process declaration axiom */
    @PortedFrom(file = "ELFReasoner.h", name = "processDeclaration")
    protected void processDeclaration(AxiomDeclaration axiom) {
        assert axiom != null;
        if (axiom.getDeclaration() instanceof ConceptExpression) {
            getC((ConceptExpression) axiom.getDeclaration());
            return;
        }
        if (axiom.getDeclaration() instanceof ObjectRoleExpression) {
            getR((ObjectRoleExpression) axiom.getDeclaration());
            return;
        }
        // nothing else could be here
        throw new UnreachableSituationException();
    }

    /** helper that inits \bot-related rules */
    @PortedFrom(file = "ELFReasoner.h", name = "initBotRules")
    void initBotRules() {
        for (TELFRole i : RMap.values()) {
            // for every R add listener that checks whether for R(C,D) S(D)
            // contains \bot
            i.addRule(new RBotRule(this, CBot));
            // add rule that adds \bot for every C in R(C,D), if S(D) contains
            // \bot
            CBot.addRule(new CAddFillerRule(this, i, CBot));
        }
    }
}
