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
    /// S(C) structure
    List<TELFConcept> CVec;
    /// set or all concepts
    Map<ConceptExpression, TELFConcept> CMap;
    /// TOP Concept
    TELFConcept CTop = null;
    /// BOTTOM Concept
    TELFConcept CBot = null;
    /// map between roles and structures
    Map<ObjectRoleExpression, TELFRole> RMap;
    /// queue of actions to perform
    List<ELFAction> queue;
    /// stat counters
    int nC2C, nA2C, nC2E, nE2C, nR2R, nC2R;

    /// get concept (expression) corresponding to a given DL expression
    protected final TELFConcept getC(final ConceptExpression p) {
        TELFConcept i = this.CMap.get(p);
        if (i != null) {
            return i;
        }
        // add new concept
        TELFConcept ret = new TELFConcept(p);
        this.CMap.put(p, ret);
        return ret;
    }

    /// get role (expression, but actually just a name)
    TELFRole getR(final ObjectRoleExpression p) {
        TELFRole r = this.RMap.get(p);
        if (r != null) {
            return r;
        }
        TELFRole ret = new TELFRole(p);
        this.RMap.put(p, ret);
        return ret;
    }

    // process different normalized  axioms
    // process original axioms
    /// c'tor: take the ontology and init internal structures
    public ELFReasoner(final JFactReasonerConfiguration c, final Ontology ont) {
        this.nC2C = 0;
        this.nA2C = 0;
        this.nC2E = 0;
        this.nE2C = 0;
        this.nR2R = 0;
        this.nC2R = 0;
        // init top- and bottom entities
        this.CBot = this.getC(ont.getExpressionManager().bottom());
        this.CTop = this.getC(ont.getExpressionManager().top());
        for (Axiom p : ont.getAxioms()) {
            if (p.isUsed()) {
                if (p instanceof AxiomConceptInclusion) {
                    this.processCI((AxiomConceptInclusion) p);
                } else if (p instanceof AxiomORoleSubsumption) {
                    this.processRI((AxiomORoleSubsumption) p);
                } else {
                    this.processDeclaration((AxiomDeclaration) p);
                }
            }
        }
        // now prepare rules for \bot with roles (if role filler is \bot, then so do domain)
        this.initBotRules();
        // dump statistics
        c.getLog().print("\nELFReasoner.ELFReasoner() \nFound ", this.nC2C,
                " axioms in the form C [= D\nFound ", this.nA2C,
                " axioms in the form C1/\\C2 [= D\nFound ", this.nC2E,
                " axioms in the form C [= ER.D\nFound ", this.nE2C,
                " axioms in the form ER.C [= D\nFound ", this.nR2R,
                " axioms in the form R [= S\nFound ", this.nC2R,
                " axioms in the form R o S [= T\n");
    }

    /// add action to a queue
    void addAction(final ELFAction action) {
        this.queue.add(action);
    }

    /// classification method
    public void classify() {
        // init all CIs
        for (TELFConcept C : this.CMap.values()) {
            C.addC(this.CTop);
            C.addC(C);
        }
        // apply all rules
        while (!this.queue.isEmpty()) {
            this.queue.remove(0).apply();
        }
    }

    //-------------------------------------------------------------
    // inline ELFReasoner implementation
    //-------------------------------------------------------------
    /// process axiom C [= D
    void processC2C(final TELFConcept C, final TELFConcept D) {
        ++this.nC2C;
        C.addRule(new CSubRule(this, D));
    }

    /// process axiom C1 and C2 [= D
    void processA2C(final TELFConcept C1, final TELFConcept C2, final TELFConcept D) {
        ++this.nA2C;
        C1.addRule(new CAndSubRule(this, C2, D));
        C2.addRule(new CAndSubRule(this, C1, D));
    }

    /// process axiom C [= \ER.D
    void processC2E(final TELFConcept C, final TELFRole R, final TELFConcept D) {
        ++this.nC2E;
        C.addRule(new RAddRule(this, R, D));
    }

    /// process axiom \ER.C [= D
    void processE2C(final TELFRole R, final TELFConcept C, final TELFConcept D) {
        ++this.nE2C;
        // C from existential will have a C-adder rule
        C.addRule(new CAddFillerRule(this, R, D));
        // R from the existential will have a C-adder here
        R.addRule(new CExistSubRule(this, C, D));
    }

    /// process axiom R [= S
    void processR2R(final TELFRole R, final TELFRole S) {
        ++this.nR2R;
        R.addRule(new RSubRule(this, S));
    }

    /// process axiom R1 o R2 [= S
    void processC2R(final TELFRole R1, final TELFRole R2, final TELFRole S) {
        ++this.nC2R;
        R1.addRule(new RChainLRule(this, R2, S));
        R2.addRule(new RChainRRule(this, R1, S));
    }

    /// process concept inclusion axiom into the internal structures
    protected final void processCI(final AxiomConceptInclusion axiom) {
        assert axiom != null;
        // deal with existentials
        if (axiom.getSupConcept() instanceof ConceptObjectExists) // C [= \E R.D
        {
            ConceptObjectExists Exists = (ConceptObjectExists) axiom.getSupConcept();
            this.processC2E(this.getC(axiom.getSubConcept()), this.getR(Exists.getOR()),
                    this.getC(Exists.getConcept()));
            return;
        }
        // now RHS is a concept name or \bottom; record it
        TELFConcept D = this.getC(axiom.getSupConcept());
        // try to check if LHS is existential
        if (axiom.getSubConcept() instanceof ConceptObjectExists) // \E R.C [= D
        {
            ConceptObjectExists Exists = (ConceptObjectExists) axiom.getSubConcept();
            this.processE2C(this.getR(Exists.getOR()), this.getC(Exists.getConcept()), D);
            return;
        }
        if (axiom.getSubConcept() instanceof ConceptAnd) // C1 \and C2 [= D
        {
            ConceptAnd And = (ConceptAnd) axiom.getSubConcept();
            assert And.size() == 2;
            this.processA2C(this.getC(And.getArguments().get(0)),
                    this.getC(And.getArguments().get(1)), D);
            return;
        }
        // the only possible thing here is C [= D
        this.processC2C(this.getC(axiom.getSubConcept()), D);
    }

    /// process role inclusion axiom into the internal structures
    void processRI(final AxiomORoleSubsumption axiom) {
        TELFRole rhs = this.getR(axiom.getRole());
        if (axiom.getSubRole() instanceof ObjectRoleChain) // R o S [= T
        {
            ObjectRoleChain Chain = (ObjectRoleChain) axiom.getSubRole();
            assert Chain.size() == 2;
            this.processC2R(this.getR(Chain.getArguments().get(0)),
                    this.getR(Chain.getArguments().get(1)), rhs);
        } else {
            ObjectRoleExpression R = (ObjectRoleExpression) axiom.getSubRole();
            assert R != null;
            this.processR2R(this.getR(R), rhs);
        }
    }

    /// process declaration axiom
    protected final void processDeclaration(final AxiomDeclaration axiom) {
        assert axiom != null;
        if (axiom.getDeclaration() instanceof ConceptExpression) {
            this.getC((ConceptExpression) axiom.getDeclaration());
            return;
        }
        if (axiom.getDeclaration() instanceof ObjectRoleExpression) {
            this.getR((ObjectRoleExpression) axiom.getDeclaration());
            return;
        }
        // nothing else could be here
        throw new UnreachableSituationException();
    }

    /// helper that inits \bot-related rules
    void initBotRules() {
        for (TELFRole i : this.RMap.values()) {
            // for every R add listener that checks whether for R(C,D) S(D) contains \bot
            i.addRule(new RBotRule(this, this.CBot));
            // add rule that adds \bot for every C in R(C,D), if S(D) contains \bot
            this.CBot.addRule(new CAddFillerRule(this, i, this.CBot));
        }
    }

    //-------------------------------------------------------------
    //Rule for C [= D case; CR1
    //-------------------------------------------------------------
    /// the rule for C [= D case
    class CSubRule extends TELFRule {
        /// super of a concept; it would be added to S(C)
        TELFConcept Sup = null;

        /// init c'tor: remember D
        CSubRule(final ELFReasoner ER, final TELFConcept D) {
            super(ER);
            this.Sup = D;
        }

        /// apply a method with a given S(C)
        @Override
        void apply(final TELFConcept addedC) {
            if (!addedC.hasSuper(this.Sup)) {
                this.ER.addAction(new ELFAction(addedC, this.Sup));
            }
        }
    }

    //-------------------------------------------------------------
    //Rule for C1 and C2 [= D case; CR2
    //-------------------------------------------------------------
    /// the rule for C1 and C2 [= D case
    class CAndSubRule extends TELFRule {
        /// concept to find in order to fire a rule
        TELFConcept Conj;
        /// super of a concept; it would be added to S(C)
        TELFConcept Sup;

        /// init c'tor: remember D
        CAndSubRule(final ELFReasoner ER, final TELFConcept C, final TELFConcept D) {
            super(ER);
            this.Conj = C;
            this.Sup = D;
        }

        /// apply a method with a given S(C)
        @Override
        void apply(final TELFConcept C) {
            if (C.hasSuper(this.Conj) && !C.hasSuper(this.Sup)) {
                this.ER.addAction(new ELFAction(C, this.Sup));
            }
        }
    }

    //-------------------------------------------------------------
    //Rule for C [= \Er.D case; CR3
    //-------------------------------------------------------------
    /// the rule for C [= \ER.D case
    class RAddRule extends TELFRule {
        /// role to add the pair
        TELFRole R;
        /// filler (D) of the existential
        TELFConcept Filler;

        /// init c'tor: remember D
        RAddRule(final ELFReasoner ER, final TELFRole r, final TELFConcept C) {
            super(ER);
            this.R = r;
            this.Filler = C;
        }

        /// apply a method with a given source S(C)
        @Override
        void apply(final TELFConcept Source) {
            //		if ( !R.hasLabel ( Source, Filler ) )
            this.ER.addAction(new ELFAction(this.R, Source, this.Filler));
        }
    }

    //-------------------------------------------------------------
    //Rules for \Er.C [= D case; CR4
    //-------------------------------------------------------------
    /// rule that checks an addition of C to S(Y) and checks whether there is X s.t. R(X,Y)
    class CAddFillerRule extends TELFRule {
        /// role to add the pair
        TELFRole R;
        /// super (E) of the existential
        TELFConcept Sup;

        /// init c'tor: remember E
        CAddFillerRule(final ELFReasoner ER, final TELFRole r, final TELFConcept C) {
            super(ER);
            this.R = r;
            this.Sup = C;
        }

        /// apply a method with a given source S(C)
        @Override
        void apply(final TELFConcept Source) {
            Set<TELFConcept> SupSet = this.R.getPredSet(Source);
            if (!SupSet.isEmpty()) {
                for (TELFConcept p : SupSet) {
                    if (!p.hasSuper(this.Sup)) {
                        this.ER.addAction(new ELFAction(p, this.Sup));
                    }
                }
            }
        }
    }

    /// rule that checks the addition of (X,Y) to R and finds a C in S(Y)
    class CExistSubRule extends TELFRule {
        /// filler of an existential
        TELFConcept Filler;
        /// super of an axiom concept; it would be added to S(C)
        TELFConcept Sup;

        /// init c'tor: remember D
        CExistSubRule(final ELFReasoner ER, final TELFConcept filler,
                final TELFConcept sup) {
            super(ER);
            this.Filler = filler;
            this.Sup = sup;
        }

        /// apply a method with an added pair (C,D)
        @Override
        void apply(final TELFConcept addedC, final TELFConcept addedD) {
            if (addedD.hasSuper(this.Filler) && !addedC.hasSuper(this.Sup)) {
                this.ER.addAction(new ELFAction(addedC, this.Sup));
            }
        }
    }

    //-------------------------------------------------------------
    //Rule for R(C,D) with \bot\in S(D) case; CR5
    //-------------------------------------------------------------
    //rule that checks whether for R(C,D) S(D) contains \bot
    class RBotRule extends TELFRule {
        /// remember the Bottom concept
        TELFConcept ConceptBot;

        /// init c'tor: remember E
        RBotRule(final ELFReasoner ER, final TELFConcept bot) {
            super(ER);
            this.ConceptBot = bot;
        }

        /// apply a method with a given new pair (C,D)
        @Override
        void apply(final TELFConcept addedC, final TELFConcept addedD) {
            // it seems like every other pair is already processed, either via that rule or via add(\bot)
            if (addedD.hasSuper(this.ConceptBot) && !addedC.hasSuper(this.ConceptBot)) {
                this.ER.addAction(new ELFAction(addedC, this.ConceptBot));
            }
        }
    }

    //-------------------------------------------------------------
    //Rule for R [= S case; CR10
    //-------------------------------------------------------------
    /// the rule for R [= S case
    class RSubRule extends TELFRule {
        /// role to add the pair
        TELFRole S;

        /// init c'tor: remember S
        RSubRule(final ELFReasoner ER, final TELFRole s) {
            super(ER);
            this.S = s;
        }

        /// apply a method with a given pair (C,D)
        @Override
        void apply(final TELFConcept addedC, final TELFConcept addedD) {
            //		if ( !S.hasLabel ( addedC, addedD ) )
            this.ER.addAction(new ELFAction(this.S, addedC, addedD));
        }
    }

    //-------------------------------------------------------------
    //Rules for R o S [= T case; CR11
    //-------------------------------------------------------------
    /// the rule for R in R o S [= T case
    class RChainLRule extends TELFRule {
        /// role to check the chain
        TELFRole S;
        /// role to add the pair
        TELFRole T;

        /// init c'tor: remember S and T
        RChainLRule(final ELFReasoner ER, final TELFRole s, final TELFRole t) {
            super(ER);
            this.S = s;
            this.T = t;
        }

        /// apply a method with a given pair (C,D)
        @Override
        void apply(final TELFConcept addedC, final TELFConcept addedD) {
            // we have R(C,D); so for all E in range(S), if S(D,E) then add T(C,E)
            for (Map.Entry<TELFConcept, Set<TELFConcept>> i : this.S.begin()) {
                if (i.getValue().contains(addedD)) {
                    TELFConcept E = i.getKey();
                    //				if ( !T.hasLabel ( addedC, E ) )
                    this.ER.addAction(new ELFAction(this.T, addedC, E));
                }
            }
        }
    }

    /// the rule for S in R o S [= T case
    class RChainRRule extends TELFRule {
        /// role to check the chain
        TELFRole R;
        /// role to add the pair
        TELFRole T;

        /// init c'tor: remember R and T
        RChainRRule(final ELFReasoner ER, final TELFRole r, final TELFRole t) {
            super(ER);
            this.R = r;
            this.T = t;
        }

        /// apply a method with a given pair (C,D)
        @Override
        void apply(final TELFConcept addedC, final TELFConcept addedD) {
            // we have S(C,D); so for all E in domain(R), if R(E,C) then add T(E,D)
            Set<TELFConcept> SupSet = this.R.getPredSet(addedC);
            if (!SupSet.isEmpty()) {
                for (TELFConcept p : SupSet) {
                    //				if ( !T.hasLabel ( *p, addedD ) )
                    this.ER.addAction(new ELFAction(this.T, p, addedD));
                }
            }
        }
    }
}
