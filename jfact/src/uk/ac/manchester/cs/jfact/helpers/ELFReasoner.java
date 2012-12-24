package uk.ac.manchester.cs.jfact.helpers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

public class ELFReasoner {
    /** S(C) structure */
    List<TELFConcept> CVec;
    /** set or all concepts */
    Map<ConceptExpression, TELFConcept> CMap;
    /** TOP Concept */
    TELFConcept CTop = null;
    /** BOTTOM Concept */
    TELFConcept CBot = null;
    /** map between roles and structures */
    Map<ObjectRoleExpression, TELFRole> RMap;
    /** queue of actions to perform */
    List<ELFAction> queue;
    /** stat counters */
    int nC2C, nA2C, nC2E, nE2C, nR2R, nC2R;

    /** get concept (expression) corresponding to a given DL expression */
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
    /** c'tor: take the ontology and init internal structures */
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
        for (Axiom p : ont.getAxioms()) {
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
        c.getLog().print("\nELFReasoner.ELFReasoner() \nFound ", nC2C,
                " axioms in the form C [= D\nFound ", nA2C,
                " axioms in the form C1/\\C2 [= D\nFound ", nC2E,
                " axioms in the form C [= ER.D\nFound ", nE2C,
                " axioms in the form ER.C [= D\nFound ", nR2R,
                " axioms in the form R [= S\nFound ", nC2R,
                " axioms in the form R o S [= T\n");
    }

    /** add action to a queue */
    void addAction(ELFAction action) {
        queue.add(action);
    }

    /** classification method */
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

    // -------------------------------------------------------------
    // inline ELFReasoner implementation
    // -------------------------------------------------------------
    /** process axiom C [= D */
    void processC2C(TELFConcept C, TELFConcept D) {
        ++nC2C;
        C.addRule(new CSubRule(this, D));
    }

    /** process axiom C1 and C2 [= D */
    void processA2C(TELFConcept C1, TELFConcept C2, TELFConcept D) {
        ++nA2C;
        C1.addRule(new CAndSubRule(this, C2, D));
        C2.addRule(new CAndSubRule(this, C1, D));
    }

    /** process axiom C [= \ER.D */
    void processC2E(TELFConcept C, TELFRole R, TELFConcept D) {
        ++nC2E;
        C.addRule(new RAddRule(this, R, D));
    }

    /** process axiom \ER.C [= D */
    void processE2C(TELFRole R, TELFConcept C, TELFConcept D) {
        ++nE2C;
        // C from existential will have a C-adder rule
        C.addRule(new CAddFillerRule(this, R, D));
        // R from the existential will have a C-adder here
        R.addRule(new CExistSubRule(this, C, D));
    }

    /** process axiom R [= S */
    void processR2R(TELFRole R, TELFRole S) {
        ++nR2R;
        R.addRule(new RSubRule(this, S));
    }

    /** process axiom R1 o R2 [= S */
    void processC2R(TELFRole R1, TELFRole R2, TELFRole S) {
        ++nC2R;
        R1.addRule(new RChainLRule(this, R2, S));
        R2.addRule(new RChainRRule(this, R1, S));
    }

    /** process concept inclusion axiom into the internal structures */
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

    // -------------------------------------------------------------
    // Rule for C [= D case; CR1
    // -------------------------------------------------------------
    /** the rule for C [= D case */
    class CSubRule extends TELFRule {
        /** super of a concept; it would be added to S(C) */
        TELFConcept Sup = null;

        /** init c'tor: remember D */
        CSubRule(ELFReasoner ER, TELFConcept D) {
            super(ER);
            Sup = D;
        }

        /** apply a method with a given S(C) */
        @Override
        void apply(TELFConcept addedC) {
            if (!addedC.hasSuper(Sup)) {
                ER.addAction(new ELFAction(addedC, Sup));
            }
        }
    }

    // -------------------------------------------------------------
    // Rule for C1 and C2 [= D case; CR2
    // -------------------------------------------------------------
    /** the rule for C1 and C2 [= D case */
    class CAndSubRule extends TELFRule {
        /** concept to find in order to fire a rule */
        TELFConcept Conj;
        /** super of a concept; it would be added to S(C) */
        TELFConcept Sup;

        /** init c'tor: remember D */
        CAndSubRule(ELFReasoner ER, TELFConcept C, TELFConcept D) {
            super(ER);
            Conj = C;
            Sup = D;
        }

        /** apply a method with a given S(C) */
        @Override
        void apply(TELFConcept C) {
            if (C.hasSuper(Conj) && !C.hasSuper(Sup)) {
                ER.addAction(new ELFAction(C, Sup));
            }
        }
    }

    // -------------------------------------------------------------
    // Rule for C [= \Er.D case; CR3
    // -------------------------------------------------------------
    /** the rule for C [= \ER.D case */
    class RAddRule extends TELFRule {
        /** role to add the pair */
        TELFRole R;
        /** filler (D) of the existential */
        TELFConcept Filler;

        /** init c'tor: remember D */
        RAddRule(ELFReasoner ER, TELFRole r, TELFConcept C) {
            super(ER);
            R = r;
            Filler = C;
        }

        /** apply a method with a given source S(C) */
        @Override
        void apply(TELFConcept Source) {
            // if ( !R.hasLabel ( Source, Filler ) )
            ER.addAction(new ELFAction(R, Source, Filler));
        }
    }

    // -------------------------------------------------------------
    // Rules for \Er.C [= D case; CR4
    // -------------------------------------------------------------
    /** rule that checks an addition of C to S(Y) and checks whether there is X */
    // s.t. R(X,Y)
    class CAddFillerRule extends TELFRule {
        /** role to add the pair */
        TELFRole R;
        /** super (E) of the existential */
        TELFConcept Sup;

        /** init c'tor: remember E */
        CAddFillerRule(ELFReasoner ER, TELFRole r, TELFConcept C) {
            super(ER);
            R = r;
            Sup = C;
        }

        /** apply a method with a given source S(C) */
        @Override
        void apply(TELFConcept Source) {
            Set<TELFConcept> SupSet = R.getPredSet(Source);
            if (!SupSet.isEmpty()) {
                for (TELFConcept p : SupSet) {
                    if (!p.hasSuper(Sup)) {
                        ER.addAction(new ELFAction(p, Sup));
                    }
                }
            }
        }
    }

    /** rule that checks the addition of (X,Y) to R and finds a C in S(Y) */
    class CExistSubRule extends TELFRule {
        /** filler of an existential */
        TELFConcept Filler;
        /** super of an axiom concept; it would be added to S(C) */
        TELFConcept Sup;

        /** init c'tor: remember D */
        CExistSubRule(ELFReasoner ER, TELFConcept filler, TELFConcept sup) {
            super(ER);
            Filler = filler;
            Sup = sup;
        }

        /** apply a method with an added pair (C,D) */
        @Override
        void apply(TELFConcept addedC, TELFConcept addedD) {
            if (addedD.hasSuper(Filler) && !addedC.hasSuper(Sup)) {
                ER.addAction(new ELFAction(addedC, Sup));
            }
        }
    }

    // -------------------------------------------------------------
    // Rule for R(C,D) with \bot\in S(D) case; CR5
    // -------------------------------------------------------------
    // rule that checks whether for R(C,D) S(D) contains \bot
    class RBotRule extends TELFRule {
        /** remember the Bottom concept */
        TELFConcept ConceptBot;

        /** init c'tor: remember E */
        RBotRule(ELFReasoner ER, TELFConcept bot) {
            super(ER);
            ConceptBot = bot;
        }

        /** apply a method with a given new pair (C,D) */
        @Override
        void apply(TELFConcept addedC, TELFConcept addedD) {
            // it seems like every other pair is already processed, either via
            // that rule or via add(\bot)
            if (addedD.hasSuper(ConceptBot) && !addedC.hasSuper(ConceptBot)) {
                ER.addAction(new ELFAction(addedC, ConceptBot));
            }
        }
    }

    // -------------------------------------------------------------
    // Rule for R [= S case; CR10
    // -------------------------------------------------------------
    /** the rule for R [= S case */
    class RSubRule extends TELFRule {
        /** role to add the pair */
        TELFRole S;

        /** init c'tor: remember S */
        RSubRule(ELFReasoner ER, TELFRole s) {
            super(ER);
            S = s;
        }

        /** apply a method with a given pair (C,D) */
        @Override
        void apply(TELFConcept addedC, TELFConcept addedD) {
            // if ( !S.hasLabel ( addedC, addedD ) )
            ER.addAction(new ELFAction(S, addedC, addedD));
        }
    }

    // -------------------------------------------------------------
    // Rules for R o S [= T case; CR11
    // -------------------------------------------------------------
    /** the rule for R in R o S [= T case */
    class RChainLRule extends TELFRule {
        /** role to check the chain */
        TELFRole S;
        /** role to add the pair */
        TELFRole T;

        /** init c'tor: remember S and T */
        RChainLRule(ELFReasoner ER, TELFRole s, TELFRole t) {
            super(ER);
            S = s;
            T = t;
        }

        /** apply a method with a given pair (C,D) */
        @Override
        void apply(TELFConcept addedC, TELFConcept addedD) {
            // we have R(C,D); so for all E in range(S), if S(D,E) then add
            // T(C,E)
            for (Map.Entry<TELFConcept, Set<TELFConcept>> i : S.begin()) {
                if (i.getValue().contains(addedD)) {
                    TELFConcept E = i.getKey();
                    // if ( !T.hasLabel ( addedC, E ) )
                    ER.addAction(new ELFAction(T, addedC, E));
                }
            }
        }
    }

    /** the rule for S in R o S [= T case */
    class RChainRRule extends TELFRule {
        /** role to check the chain */
        TELFRole R;
        /** role to add the pair */
        TELFRole T;

        /** init c'tor: remember R and T */
        RChainRRule(ELFReasoner ER, TELFRole r, TELFRole t) {
            super(ER);
            R = r;
            T = t;
        }

        /** apply a method with a given pair (C,D) */
        @Override
        void apply(TELFConcept addedC, TELFConcept addedD) {
            // we have S(C,D); so for all E in domain(R), if R(E,C) then add
            // T(E,D)
            Set<TELFConcept> SupSet = R.getPredSet(addedC);
            if (!SupSet.isEmpty()) {
                for (TELFConcept p : SupSet) {
                    // if ( !T.hasLabel ( *p, addedD ) )
                    ER.addAction(new ELFAction(T, p, addedD));
                }
            }
        }
    }
}
