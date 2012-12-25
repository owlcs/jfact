package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.util.*;

import uk.ac.manchester.cs.jfact.helpers.*;
import conformance.PortedFrom;

@PortedFrom(file = "ConceptWithDep.h", name = "Concept")
public class Concept extends ClassifiableEntry {
    public static Concept getBOTTOM() {
        Concept toReturn = new Concept("BOTTOM");
        toReturn.setBottom();
        toReturn.setId(-1);
        toReturn.setpName(bpBOTTOM);
        toReturn.setpBody(bpBOTTOM);
        return toReturn;
    }

    public static Concept getTOP() {
        Concept toReturn = new Concept("TOP");
        toReturn.setTop();
        toReturn.setId(-1);
        toReturn.setpName(bpTOP);
        toReturn.setpBody(bpTOP);
        toReturn.setTsDepth(1);
        toReturn.setClassTag(CTTag.cttTrueCompletelyDefined);
        return toReturn;
    }

    public static Concept getTEMP() {
        Concept TEMP = new Concept(" ");
        TEMP.setId(-1);
        TEMP.setTsDepth(1);
        TEMP.setClassTag(CTTag.cttTrueCompletelyDefined);
        return TEMP;
    }

    /** type of concept wrt classifiability */
    public enum CTTag {
        /** not specified */
        cttUnspecified('u'),
        /** concept with all parents -- TCD */
        cttTrueCompletelyDefined('T'),
        /** concept w/o any told subsumers */
        cttOrphan('O'),
        /** concept with all parents -- LCD, TCD or Orptans */
        cttLikeCompletelyDefined('L'),
        /** concept with non-primitive TS */
        cttHasNonPrimitiveTS('N'),
        /** any other primitive concept */
        cttRegular('r'),
        /** any non-primitive concept (except synonyms) */
        cttNonPrimitive('n');
        private char c;

        private CTTag(char c) {
            this.c = c;
        }

        protected char getCTTagName() {
            return c;
        }
    }

    /** label to use in relevant-only checks */
    private long rel;
    /** classification type of concept: completely defined (true- or like-), no
     * TS, other */
    private CTTag classTag;
    /** depth of the concept wrt told subsumers */
    private int tsDepth;
    /** pointer to the entry in DAG with concept name */
    private int pName;
    /** pointer to the entry in DAG with concept definition */
    private int pBody;
    /** features for C */
    private LogicFeatures posFeatures = new LogicFeatures();
    /** features for ~C */
    private LogicFeatures negFeatures = new LogicFeatures();
    /** all extra rules for a given concept */
    private FastSet extraRules = FastSetFactory.create();
    protected DLTree description;

    /** adds concept as a told subsumer of current one; @return value for CDC
     * analisys */
    private boolean addToldSubsumer(Concept p) {
        if (p != this) {
            addParentIfNew(p);
            if (p.isSingleton() || p.isHasSP()) {
                setHasSP(true); // this has singleton parent
            }
        }
        // if non-primitive concept was found in a description, it's not CD
        return p.isPrimitive();
    }

    public Concept(String name) {
        super(name);
        rel = 0;
        classTag = CTTag.cttUnspecified;
        tsDepth = 0;
        pName = bpINVALID;
        pBody = bpINVALID;
        setPrimitive(true);
    }

    /** add index of a simple rule in TBox to the ER set */
    public void addExtraRule(int p) {
        extraRules.add(p);
        setCompletelyDefined(false);
    }

    /** check if a concept is in a disjoint relation with anything */
    public boolean hasExtraRules() {
        return !extraRules.isEmpty();
    }

    /** iterator for accessing DJ elements */
    public FastSet getExtraRules() {
        return extraRules;
    }

    public CTTag getClassTagPlain() {
        return classTag;
    }

    /** get value of a tag; determine it if unset */
    public CTTag getClassTag() {
        if (classTag == CTTag.cttUnspecified) {
            classTag = determineClassTag();
        }
        return classTag;
    }

    /** remove concept from its own definition (like in case C [= (or C ...) */
    public void removeSelfFromDescription() {
        description = replaceWithConstOld(description);
        this.initToldSubsumers();
    }

    /** remove concept description (to save space) */
    public void removeDescription() {
        description = null;
    }

    /** check whether it is possible to init this as a non-primitive concept with
     * DESC */
    public boolean canInitNonPrim(DLTree desc) {
        if (description == null) {
            return true;
        }
        if (!isPrimitive() && description.equals(desc)) {
            return true;
        }
        return false;
    }

    /** switch primitive concept to non-primitive with new definition; @return
     * old definition */
    public DLTree makeNonPrimitive(DLTree desc) {
        DLTree ret = description;
        removeDescription();
        addDesc(desc);
        setPrimitive(false);
        return ret;
    }

    @Override
    public String toString() {
        return extName;
    }

    /** init told subsumers of the concept by it's description */
    public void initToldSubsumers() {
        toldSubsumers.clear();
        setHasSP(false);
        // normalise description if the only parent is TOP
        if (isPrimitive() && description != null && description.isTOP()) {
            removeDescription();
        }
        boolean CD = !hasExtraRules() && isPrimitive(); // not a completely
                                                        // defined if there are
                                                        // extra rules
        if (description != null) {
            CD &= this.initToldSubsumers(description, new HashSet<Role>());
        }
        setCompletelyDefined(CD);
    }

    /** init TOP told subsumer if necessary */
    public void setToldTop(Concept top) {
        if (description == null && !hasToldSubsumers()) {
            addParent(top);
        }
    }

    public int resolveId() {
        if (pName == bpINVALID) {
            return pBody;
        }
        if (isSynonym()) {
            Concept r = resolveSynonym(this);
            if (r != this) {
                return r.resolveId();
            }
        }
        return pName; // return concept's name
    }

    public void addDesc(DLTree Desc) {
        if (Desc == null) {
            return;
        }
        // assert this.isPrimitive();
        if (description == null) {
            description = Desc.copy();
            return;
        }
        if (Desc.isAND()) {
            if (description.isAND()) {
                description.addFirstChildren(Desc.getChildren());
            } else {
                // if it's not an AND then a new AND must be created
                DLTree temp = description;
                description = Desc.copy();
                description.addChild(temp);
            }
        } else {
            if (description.isAND()) {
                description.addFirstChild(Desc);
            } else {
                description = DLTreeFactory.createSNFAnd(Desc, description);
            }
        }
    }

    public void addLeaves(Collection<DLTree> Desc) {
        // assert this.isPrimitive();
        if (description == null) {
            description = DLTreeFactory.createSNFAnd(Desc);
        } else {
            if (description.isAND()) {
                for (DLTree d : Desc) {
                    description.addChild(d);
                }
            } else {
                List<DLTree> l = new ArrayList<DLTree>(Desc);
                l.add(description);
                description = DLTreeFactory.createSNFAnd(l);
            }
        }
    }

    private CTTag determineClassTag() {
        if (isSynonym()) {
            return resolveSynonym(this).getClassTag();
        }
        if (!isPrimitive()) {
            return CTTag.cttNonPrimitive;
        }
        if (!hasToldSubsumers()) {
            return CTTag.cttOrphan;
        }
        boolean hasLCD = false;
        boolean hasOther = false;
        boolean hasNP = false;
        for (ClassifiableEntry p : toldSubsumers) {
            switch (((Concept) p).getClassTag()) {
                case cttTrueCompletelyDefined:
                    break;
                case cttOrphan:
                case cttLikeCompletelyDefined:
                    hasLCD = true;
                    break;
                case cttRegular:
                    hasOther = true;
                    break;
                case cttHasNonPrimitiveTS:
                case cttNonPrimitive:
                    hasNP = true;
                    break;
                default:
                    throw new UnreachableSituationException();
            }
        }
        // there are non-primitive TS
        if (hasNP) {
            return CTTag.cttHasNonPrimitiveTS;
        }
        // has something different from CD-like ones (and not CD)
        if (hasOther || !isCompletelyDefined()) {
            return CTTag.cttRegular;
        }
        // no more 'other' concepts here, and the CD-like structure
        if (hasLCD) {
            return CTTag.cttLikeCompletelyDefined;
        }
        return CTTag.cttTrueCompletelyDefined;
    }

    private static EnumSet<Token> replacements = EnumSet.of(CNAME, INAME, RNAME, DNAME);

    public void push(LinkedList<DLTree> stack, DLTree current) {
        // push subtrees: stack size increases by one or two, or current is a
        // leaf
        for (DLTree t : current.getChildren()) {
            if (t != null) {
                stack.push(t);
            }
        }
    }

    private DLTree replaceWithConstOld(DLTree t) {
        if (t == null) {
            return null;
        }
        Token token = t.token();
        // the three ifs are actually exclusive
        if (replacements.contains(token)
                && resolveSynonym((ClassifiableEntry) t.elem().getNE()).equals(this)) {
            return DLTreeFactory.createTop();
        }
        if (token == AND) {
            List<DLTree> l = new ArrayList<DLTree>();
            for (DLTree d : t.getChildren()) {
                l.add(replaceWithConstOld(d));
            }
            return DLTreeFactory.createSNFAnd(l, t);
        }
        if (token == NOT) {
            if (t.getChild().isAND() || replacements.contains(t.getChild().token())) {
                return DLTreeFactory.createSNFNot(replaceWithConstOld(t.getChild()));
            }
        }
        return t;
    }

    /** init told subsumers of the concept by given DESCription; @return TRUE iff
     * concept is CD */
    public boolean initToldSubsumers(DLTree _desc, Set<Role> RolesProcessed) {
        if (_desc == null || _desc.isTOP()) {
            return true;
        }
        DLTree desc = _desc;
        Token token = desc.token();
        if (replacements.contains(token)) {
            return addToldSubsumer((Concept) desc.elem().getNE());
        }
        if (token == NOT) {
            if (desc.getChild().token() == FORALL || desc.getChild().token() == LE) {
                searchTSbyRoleAndSupers(Role.resolveRole(desc.getChild().getLeft()),
                        RolesProcessed);
            }
            return false;
        }
        if (token == SELF) {
            Role R = Role.resolveRole(desc.getChild());
            searchTSbyRoleAndSupers(R, RolesProcessed);
            searchTSbyRoleAndSupers(R.inverse(), RolesProcessed);
            return false;
        }
        if (token == AND) {
            // push all AND children on the list and traverse the list removing
            // n-th level ANDs and pushing their children in turn; ends up with
            // the leaves of the AND subtree
            boolean toReturn = true;
            for (DLTree t : desc.getChildren()) {
                toReturn &= this.initToldSubsumers(t, RolesProcessed);
            }
            return toReturn;
        }
        return false;
    }

    private void searchTSbyRole(Role R, Set<Role> rolesProcessed) {
        if (rolesProcessed.contains(R)) {
            return;
        }
        DLTree Domain = R.getTDomain();
        if (Domain == null || Domain.isConst()) {
            return;
        }
        rolesProcessed.add(R);
        this.initToldSubsumers(Domain, rolesProcessed);
    }

    public void searchTSbyRoleAndSupers(Role r, Set<Role> RolesProcessed) {
        searchTSbyRole(r, RolesProcessed);
        List<Role> list = r.getAncestor();
        for (int i = 0; i < list.size(); i++) {
            Role q = list.get(i);
            searchTSbyRole(q, RolesProcessed);
        }
    }

    public int calculateTSDepth() {
        if (tsDepth > 0) {
            return tsDepth;
        }
        int max = 0;
        for (ClassifiableEntry p : toldSubsumers) {
            // XXX should not be needed
            if (!p.getToldSubsumers().contains(this)) {
                int cur = ((Concept) p).calculateTSDepth();
                if (max < cur) {
                    max = cur;
                }
            }// else both nodes are each other subsumers: same depth?
        }
        return tsDepth = max + 1;
    }

    void clear() {
        // TNamedEntry clean
        setId(0);
        // ClassifiableEntry clean
        taxVertex = null;
        toldSubsumers.clear();
        setCompletelyDefined(false);
        pSynonym = null;
        // TConcept clean
        removeDescription();
        setPrimitive(true);
        pName = pBody = bpINVALID;
    }

    public int getpName() {
        return pName;
    }

    public void setpName(int pName) {
        this.pName = pName;
    }

    public int getpBody() {
        return pBody;
    }

    public void setpBody(int pBody) {
        this.pBody = pBody;
    }

    public DLTree getDescription() {
        return description;
    }

    public int getTsDepth() {
        return tsDepth;
    }

    private void setTsDepth(int tsDepth) {
        this.tsDepth = tsDepth;
    }

    public LogicFeatures getNegFeatures() {
        return negFeatures;
    }

    public LogicFeatures getPosFeatures() {
        return posFeatures;
    }

    private void setClassTag(CTTag classTag) {
        this.classTag = classTag;
    }

    private boolean primitive;

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean action) {
        primitive = action;
    }

    private boolean hasSP;

    /** a HasSingletonParent flag */
    public boolean isHasSP() {
        return hasSP;
    }

    public void setHasSP(boolean action) {
        hasSP = action;
    }

    private boolean nominal;

    public boolean isNominal() {
        return nominal;
    }

    public void setNominal(boolean action) {
        nominal = action;
    }

    private boolean singleton;

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean action) {
        singleton = action;
    }

    // relevance part
    /** is given concept relevant to given Labeller's state */
    public boolean isRelevant(long lab) {
        return lab == rel;
    }

    /** make given concept relevant to given Labeller's state */
    public void setRelevant(long lab) {
        rel = lab;
    }

    /** make given concept irrelevant to given Labeller's state */
    public void dropRelevant(long lab) {
        rel = 0;
    }
}
