package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.FastSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetFactory;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import conformance.Original;
import conformance.PortedFrom;

/** concept */
@PortedFrom(file = "ConceptWithDep.h", name = "Concept")
public class Concept extends ClassifiableEntry {

    private static final long serialVersionUID = 11000L;
    /** temp concept iri */
    public static final IRI temp = IRI.create("urn:jfact#", "temp");
    /** query concept iri */
    public static final IRI query = IRI.create("FaCT++.default");
    /** nothing concept iri */
    public static final IRI nothing = OWLRDFVocabulary.OWL_NOTHING.getIRI();
    /** thing concept iri */
    public static final IRI thing = OWLRDFVocabulary.OWL_THING.getIRI();

    /** @return bottom concept */
    @Original
    public static Concept getBOTTOM() {
        Concept toReturn = new Concept(nothing);
        toReturn.setBottom();
        toReturn.setId(-1);
        toReturn.setpName(bpBOTTOM);
        toReturn.setpBody(bpBOTTOM);
        return toReturn;
    }

    /** @return top concept */
    @Original
    public static Concept getTOP() {
        Concept toReturn = new Concept(thing);
        toReturn.setTop();
        toReturn.setId(-1);
        toReturn.setpName(bpTOP);
        toReturn.setpBody(bpTOP);
        toReturn.setTsDepth(1);
        toReturn.setClassTag(CTTag.cttTrueCompletelyDefined);
        return toReturn;
    }

    /** @return temporary concept */
    @Original
    public static Concept getTEMP() {
        Concept TEMP = new Concept(temp);
        TEMP.setId(-1);
        TEMP.setTsDepth(1);
        TEMP.setClassTag(CTTag.cttTrueCompletelyDefined);
        return TEMP;
    }

    /** @return query concept */
    public static Concept getQuery() {
        Concept p = new Concept(query);
        p.setSystem();
        return p;
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

        private final char c;

        private CTTag(char c) {
            this.c = c;
        }

        protected char getCTTagName() {
            return c;
        }
    }

    /** label to use in relevant-only checks */
    @PortedFrom(file = "tConcept.h", name = "rel")
    private long rel;
    /**
     * classification type of concept: completely defined (true- or like-), no
     * TS, other
     */
    @PortedFrom(file = "tConcept.h", name = "classTag")
    private CTTag classTag;
    /** depth of the concept wrt told subsumers */
    @PortedFrom(file = "tConcept.h", name = "tsDepth")
    private int tsDepth;
    /** pointer to the entry in DAG with concept name */
    @PortedFrom(file = "tConcept.h", name = "pName")
    private int pName;
    /** pointer to the entry in DAG with concept definition */
    @PortedFrom(file = "tConcept.h", name = "pBody")
    private int pBody;
    /** features for C */
    @PortedFrom(file = "tConcept.h", name = "posFeatures")
    private final LogicFeatures posFeatures = new LogicFeatures();
    /** features for ~C */
    @PortedFrom(file = "tConcept.h", name = "negFeatures")
    private final LogicFeatures negFeatures = new LogicFeatures();
    /** all extra rules for a given concept */
    @PortedFrom(file = "tConcept.h", name = "erSet")
    private final FastSet extraRules = FastSetFactory.create();
    @PortedFrom(file = "tConcept.h", name = "Description")
    protected DLTree description;

    /**
     * adds concept as a told subsumer of current one;
     * 
     * @param concept
     *        concept
     * @return value for CDC analisys
     */
    @PortedFrom(file = "tConcept.h", name = "addToldSubsumer")
    private boolean addToldSubsumer(Concept concept) {
        if (concept != this) {
            addParentIfNew(concept);
            if (concept.isSingleton() || concept.isHasSP()) {
                setHasSP(true);
                // this has singleton parent
            }
        }
        // if non-primitive concept was found in a description, it's not CD
        return concept.isPrimitive();
    }

    /**
     * @param name
     *        name
     */
    public Concept(IRI name) {
        super(name);
        rel = 0;
        classTag = CTTag.cttUnspecified;
        tsDepth = 0;
        pName = bpINVALID;
        pBody = bpINVALID;
        setPrimitive(true);
    }

    /**
     * add index of a simple rule in TBox to the ER set
     * 
     * @param ruleIndex
     *        ruleIndex
     */
    @PortedFrom(file = "tConcept.h", name = "addExtraRule")
    public void addExtraRule(int ruleIndex) {
        extraRules.add(ruleIndex);
        setCompletelyDefined(false);
    }

    /** @return if a concept is in a disjoint relation with anything */
    @PortedFrom(file = "tConcept.h", name = "hasExtraRules")
    public boolean hasExtraRules() {
        return !extraRules.isEmpty();
    }

    /** @return accessing DJ elements */
    @PortedFrom(file = "tConcept.h", name = "er_begin")
    public FastSet getExtraRules() {
        return extraRules;
    }

    /** @return class tag */
    @Original
    public CTTag getClassTagPlain() {
        return classTag;
    }

    /** @return value of a tag; determine it if unset */
    @PortedFrom(file = "tConcept.h", name = "getClassTag")
    public CTTag getClassTag() {
        if (classTag == CTTag.cttUnspecified) {
            classTag = determineClassTag();
        }
        return classTag;
    }

    /** remove concept from its own definition (like in case C [= (or C ...) */
    @PortedFrom(file = "tConcept.h", name = "removeSelfFromDescription")
    public void removeSelfFromDescription() {
        if (hasSelfInDesc()) {
            description = replaceWithConstOld(description);
        }
        this.initToldSubsumers();
    }

    /** remove concept description (to save space) */
    @PortedFrom(file = "tConcept.h", name = "removeDescription")
    public void removeDescription() {
        description = null;
    }

    /**
     * @param desc
     *        desc
     * @return whether it is possible to init this as a non-primitive concept
     *         with DESC
     */
    @PortedFrom(file = "tConcept.h", name = "canInitNonPrim")
    public boolean canInitNonPrim(DLTree desc) {
        if (description == null) {
            return true;
        }
        if (!isPrimitive() && description.equals(desc)) {
            return true;
        }
        return false;
    }

    /**
     * switch primitive concept to non-primitive with new definition;
     * 
     * @param desc
     *        desc
     * @return old definition
     */
    @PortedFrom(file = "tConcept.h", name = "makeNonPrimitive")
    public DLTree makeNonPrimitive(DLTree desc) {
        DLTree ret = description;
        removeDescription();
        addDesc(desc);
        setPrimitive(false);
        return ret;
    }

    @Override
    public String toString() {
        return extName.toString();
    }

    /** init told subsumers of the concept by it's description */
    @PortedFrom(file = "tConcept.h", name = "initToldSubsumers")
    public void initToldSubsumers() {
        toldSubsumers.clear();
        setHasSP(false);
        // normalise description if the only parent is TOP
        if (isPrimitive() && description != null && description.isTOP()) {
            removeDescription();
        }
        boolean CD = !hasExtraRules() && isPrimitive();
        // not a completely defined if there are extra rules
        if (description != null) {
            CD &= this.initToldSubsumers(description, new HashSet<Role>());
        }
        setCompletelyDefined(CD);
    }

    /**
     * init TOP told subsumer if necessary
     * 
     * @param top
     *        top
     */
    @PortedFrom(file = "tConcept.h", name = "setToldTop")
    public void setToldTop(Concept top) {
        if (description == null && !hasToldSubsumers()) {
            addParent(top);
        }
    }

    /** @return resolve synonym id */
    @PortedFrom(file = "tConcept.h", name = "resolveId")
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
        // return concept's name
        return pName;
    }

    /**
     * @param Desc
     *        Desc
     */
    @PortedFrom(file = "tConcept.h", name = "addDesc")
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
                DLTree t = description;
                description = Desc.copy();
                description.addChild(t);
            }
        } else {
            if (description.isAND()) {
                description.addFirstChild(Desc);
            } else {
                description = DLTreeFactory.createSNFAnd(Desc, description);
            }
        }
    }

    /**
     * @param Desc
     *        Desc
     */
    @Original
    public void addLeaves(Collection<DLTree> Desc) {
        // assert isPrimitive();
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

    @PortedFrom(file = "tConcept.h", name = "determineClassTag")
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

    @Original
    private static final EnumSet<Token> replacements = EnumSet.of(CNAME, INAME,
            RNAME, DNAME);

    /**
     * @param stack
     *        stack
     * @param current
     *        current
     */
    @Original
    public void push(LinkedList<DLTree> stack, DLTree current) {
        // push subtrees: stack size increases by one or two, or current is a
        // leaf
        for (DLTree t : current.getChildren()) {
            if (t != null) {
                stack.push(t);
            }
        }
    }

    @PortedFrom(file = "tConcept.h", name = "replaceSelfWithConst")
    private DLTree replaceWithConstOld(DLTree t) {
        if (t == null) {
            return null;
        }
        Token token = t.token();
        // the three ifs are actually exclusive
        if (replacements.contains(token)
                && resolveSynonym((ClassifiableEntry) t.elem().getNE()).equals(
                        this)) {
            return DLTreeFactory.createTop();
        }
        if (token == AND) {
            List<DLTree> l = new ArrayList<DLTree>();
            for (DLTree d : t.getChildren()) {
                l.add(replaceWithConstOld(d));
            }
            return DLTreeFactory.createSNFAnd(l, t);
        }
        if (token == NOT
                && (t.getChild().isAND() || replacements.contains(t.getChild()
                        .token()))) {
            return DLTreeFactory
                    .createSNFNot(replaceWithConstOld(t.getChild()));
        }
        return t;
    }

    @PortedFrom(file = "tConcept.h", name = "hasSelfInDesc")
    private boolean hasSelfInDesc(DLTree t) {
        if (t == null) {
            return false;
        }
        Token token = t.token();
        // the three ifs are actually exclusive
        if (replacements.contains(token)) {
            return resolveSynonym((ClassifiableEntry) t.elem().getNE()).equals(
                    this);
        }
        if (token == AND) {
            for (DLTree d : t.getChildren()) {
                if (hasSelfInDesc(d)) {
                    return true;
                }
            }
            return false;
        }
        if (token == NOT
                && (t.getChild().isAND() || replacements.contains(t.getChild()
                        .token()))) {
            return hasSelfInDesc(t.getChild());
        }
        return false;
    }

    /**
     * init told subsumers of the concept by given DESCription;
     * 
     * @param _desc
     *        _desc
     * @param RolesProcessed
     *        RolesProcessed
     * @return TRUE iff concept is CD
     */
    @PortedFrom(file = "tConcept.h", name = "initToldSubsumers")
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
            if (desc.getChild().token() == FORALL
                    || desc.getChild().token() == LE) {
                searchTSbyRoleAndSupers(
                        Role.resolveRole(desc.getChild().getLeft()),
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

    @PortedFrom(file = "tConcept.h", name = "SearchTSbyRole")
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

    /**
     * @param r
     *        r
     * @param RolesProcessed
     *        RolesProcessed
     */
    @PortedFrom(file = "tConcept.h", name = "SearchTSbyRoleAndSupers")
    public void searchTSbyRoleAndSupers(Role r, Set<Role> RolesProcessed) {
        searchTSbyRole(r, RolesProcessed);
        List<Role> list = r.getAncestor();
        for (int i = 0; i < list.size(); i++) {
            Role q = list.get(i);
            searchTSbyRole(q, RolesProcessed);
        }
    }

    /** @return told subsumers depth */
    @PortedFrom(file = "tConcept.h", name = "calculateTSDepth")
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
            }
            // else both nodes are each other subsumers: same depth?
        }
        tsDepth = max + 1;
        return tsDepth;
    }

    /** clear concept */
    @PortedFrom(file = "tConcept.h", name = "clear")
    public void clear() {
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
        pBody = bpINVALID;
        pName = bpINVALID;
    }

    /**
     * @return true iff description contains top-level references to THIS
     *         concept
     */
    @PortedFrom(file = "tConcept.h", name = "hasSelfInDesc")
    boolean hasSelfInDesc() {
        return hasSelfInDesc(description);
    }

    /** @return p name */
    @Original
    public int getpName() {
        return pName;
    }

    /**
     * @param pName
     *        pName
     */
    @Original
    public void setpName(int pName) {
        this.pName = pName;
    }

    /** @return p body */
    @Original
    public int getpBody() {
        return pBody;
    }

    /**
     * @param pBody
     *        pBody
     */
    @Original
    public void setpBody(int pBody) {
        this.pBody = pBody;
    }

    /** @return description */
    @Original
    public DLTree getDescription() {
        return description;
    }

    /** @return told subsumers depth */
    @Original
    public int getTsDepth() {
        return tsDepth;
    }

    @Original
    private void setTsDepth(int tsDepth) {
        this.tsDepth = tsDepth;
    }

    /** @return neg features */
    @Original
    public LogicFeatures getNegFeatures() {
        return negFeatures;
    }

    /** @return pos features */
    @Original
    public LogicFeatures getPosFeatures() {
        return posFeatures;
    }

    @Original
    @PortedFrom(file = "tConcept.h", name = "classTag")
    private void setClassTag(CTTag classTag) {
        this.classTag = classTag;
    }

    @Original
    private boolean primitive;

    /** @return true if primitive */
    @Original
    public boolean isPrimitive() {
        return primitive;
    }

    /** @return false if primitive */
    @Original
    public boolean isNonPrimitive() {
        return !isPrimitive();
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setPrimitive(boolean action) {
        primitive = action;
    }

    @Original
    private boolean hasSP;

    /** @return HasSingletonParent flag */
    @Original
    public boolean isHasSP() {
        return hasSP;
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setHasSP(boolean action) {
        hasSP = action;
    }

    @Original
    private boolean nominal;

    /** @return nominal */
    @Original
    public boolean isNominal() {
        return nominal;
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setNominal(boolean action) {
        nominal = action;
    }

    @Original
    private boolean singleton;

    /** @return singleton */
    @Original
    public boolean isSingleton() {
        return singleton;
    }

    /**
     * @param action
     *        action
     */
    @Original
    public void setSingleton(boolean action) {
        singleton = action;
    }

    // relevance part
    /**
     * @param lab
     *        lab
     * @return is given concept relevant to given Labeller's state
     */
    @PortedFrom(file = "tConcept.h", name = "isRelevant")
    public boolean isRelevant(long lab) {
        return lab == rel;
    }

    /**
     * make given concept relevant to given Labeller's state
     * 
     * @param lab
     *        lab
     */
    @PortedFrom(file = "tConcept.h", name = "setRelevant")
    public void setRelevant(long lab) {
        rel = lab;
    }

    /** make given concept irrelevant to given Labeller's state */
    @PortedFrom(file = "tConcept.h", name = "dropRelevant")
    public void dropRelevant() {
        rel = 0;
    }
}
