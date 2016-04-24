package uk.ac.manchester.cs.jfact.kernel;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.chainsaw.FastSet;
import uk.ac.manchester.cs.chainsaw.FastSetFactory;
import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;

/** concept */
@PortedFrom(file = "ConceptWithDep.h", name = "Concept")
public class Concept extends ClassifiableEntry {

    /** temp concept iri */
    public static final IRI temp = IRI.create("urn:jfact#", "temp");
    /** query concept iri */
    public static final IRI query = IRI.create("FaCT++.default");
    /** nothing concept iri */
    public static final IRI nothing = OWLRDFVocabulary.OWL_NOTHING.getIRI();
    /** thing concept iri */
    public static final IRI thing = OWLRDFVocabulary.OWL_THING.getIRI();

    /** type of concept wrt classifiability */
    public enum CTTag {
        //@formatter:off
        /** not specified */                                    UNSPECIFIED             ('u'),
        /** concept with all parents -- TCD */                  COMPLETELYDEFINED       ('T'),
        /** concept w/o any told subsumers */                   ORPHAN                  ('O'),
        /** concept with all parents -- LCD, TCD or Orphans */  LIKECOMPLETELYDEFINED   ('L'),
        /** concept with non-primitive TS */                    HASNONPRIMITIVETS       ('N'),
        /** any other primitive concept */                      REGULAR                 ('r'),
        /** any non-primitive concept (except synonyms) */      NONPRIMITIVE            ('n');
        //@formatter:on
        private final char c;

        private CTTag(char c) {
            this.c = c;
        }

        protected char getCTTagName() {
            return c;
        }
    }

    /** label to use in relevant-only checks */
    @PortedFrom(file = "tConcept.h", name = "rel") private long rel;
    /**
     * classification type of concept: completely defined (true- or like-), no
     * TS, other
     */
    @PortedFrom(file = "tConcept.h", name = "classTag") private CTTag classTag;
    /** depth of the concept wrt told subsumers */
    @PortedFrom(file = "tConcept.h", name = "tsDepth") private int tsDepth;
    /** pointer to the entry in DAG with concept name */
    @PortedFrom(file = "tConcept.h", name = "pName") private int pName;
    /** pointer to the entry in DAG with concept definition */
    @PortedFrom(file = "tConcept.h", name = "pBody") private int pBody;
    /** features for C */
    @PortedFrom(file = "tConcept.h", name = "posFeatures") private final LogicFeatures posFeatures = new LogicFeatures();
    /** features for ~C */
    @PortedFrom(file = "tConcept.h", name = "negFeatures") private final LogicFeatures negFeatures = new LogicFeatures();
    /** all extra rules for a given concept */
    @PortedFrom(file = "tConcept.h", name = "erSet") private final FastSet extraRules = FastSetFactory.create();
    @PortedFrom(file = "tConcept.h", name = "Description") protected DLTree description;
    @Original private static final EnumSet<Token> replacements = EnumSet.of(CNAME, INAME, RNAME, DNAME);
    @Original private boolean primitive;
    @Original private boolean hasSP;
    @Original private boolean nominal;
    @Original private boolean singleton;

    /**
     * @param name
     *        name
     */
    public Concept(IRI name) {
        super(name);
        rel = 0;
        classTag = CTTag.UNSPECIFIED;
        tsDepth = 0;
        pName = BP_INVALID;
        pBody = BP_INVALID;
        setPrimitive(true);
    }

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
     * @param df
     *        data factory
     * @return bottom concept
     */
    @Original
    public static Concept getBOTTOM(OWLDataFactory df) {
        Concept toReturn = new Concept(nothing);
        toReturn.setBottom();
        toReturn.setId(-1);
        toReturn.setpName(BP_BOTTOM);
        toReturn.setpBody(BP_BOTTOM);
        toReturn.setEntity(new ConceptName(df.getOWLNothing()));
        return toReturn;
    }

    /**
     * @param df
     *        data factory
     * @return top concept
     */
    @Original
    public static Concept getTOP(OWLDataFactory df) {
        Concept toReturn = new Concept(thing);
        toReturn.setTop();
        toReturn.setId(-1);
        toReturn.setpName(BP_TOP);
        toReturn.setpBody(BP_TOP);
        toReturn.setTsDepth(1);
        toReturn.setClassTag(CTTag.COMPLETELYDEFINED);
        toReturn.setEntity(new ConceptName(df.getOWLThing()));
        return toReturn;
    }

    /**
     * @return temporary concept
     */
    @Original
    public static Concept getTEMP() {
        Concept temporary = new Concept(temp);
        temporary.setId(-1);
        temporary.setTsDepth(1);
        temporary.setClassTag(CTTag.COMPLETELYDEFINED);
        return temporary;
    }

    /**
     * @return query concept
     */
    public static Concept getQuery() {
        Concept p = new Concept(query);
        p.setSystem();
        return p;
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
        // FIXME!! double check this!
        setCompletelyDefined(false);
    }

    /**
     * @return if a concept is in a disjoint relation with anything
     */
    @PortedFrom(file = "tConcept.h", name = "hasExtraRules")
    public boolean hasExtraRules() {
        return !extraRules.isEmpty();
    }

    /**
     * @return accessing DJ elements
     */
    @PortedFrom(file = "tConcept.h", name = "er_begin")
    public FastSet getExtraRules() {
        return extraRules;
    }

    /**
     * @return class tag
     */
    @Original
    public CTTag getClassTagPlain() {
        return classTag;
    }

    /**
     * @return value of a tag; determine it if unset
     */
    @PortedFrom(file = "tConcept.h", name = "getClassTag")
    public CTTag getClassTag() {
        if (classTag == CTTag.UNSPECIFIED) {
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
        return description.equals(desc);
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

    /** init told subsumers of the concept by its description */
    @PortedFrom(file = "tConcept.h", name = "initToldSubsumers")
    public void initToldSubsumers() {
        toldSubsumers = null;
        setHasSP(false);
        // normalise description if the only parent is TOP
        if (isPrimitive() && description != null && description.isTOP()) {
            removeDescription();
        }
        boolean cd = !hasExtraRules() && isPrimitive();
        // not a completely defined if there are extra rules
        if (description != null) {
            cd &= this.initToldSubsumers(description, new HashSet<Role>());
        }
        setCompletelyDefined(cd);
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

    /**
     * @return resolve synonym id
     */
    @PortedFrom(file = "tConcept.h", name = "resolveId")
    public int resolveId() {
        if (pName == BP_INVALID) {
            return pBody;
        }
        if (isSynonym()) {
            return resolveSynonym(this).resolveId();
        }
        // return concept's name
        return pName;
    }

    /**
     * @param desc
     *        Desc
     */
    @PortedFrom(file = "tConcept.h", name = "addDesc")
    public void addDesc(@Nullable DLTree desc) {
        if (desc == null) {
            return;
        }
        if (description == null) {
            description = desc.copy();
            return;
        }
        if (desc.isAND()) {
            if (description.isAND()) {
                description.addFirstChildren(desc.getChildren());
            } else {
                // if it's not an AND then a new AND must be created
                DLTree t = description;
                description = desc.copy();
                description.addChild(t);
            }
        } else {
            if (description.isAND()) {
                description.addFirstChild(desc);
            } else {
                description = DLTreeFactory.createSNFAnd(desc, description);
            }
        }
    }

    /**
     * @param desc
     *        Desc
     */
    @Original
    public void addLeaves(Collection<DLTree> desc) {
        if (description == null) {
            description = DLTreeFactory.createSNFAnd(desc);
        } else {
            if (description.isAND()) {
                desc.forEach(description::addChild);
            } else {
                List<DLTree> l = new ArrayList<>(desc);
                l.add(description);
                description = DLTreeFactory.createSNFAnd(l);
            }
        }
    }

    @PortedFrom(file = "tConcept.h", name = "determineClassTag")
    @SuppressWarnings("incomplete-switch")
    private CTTag determineClassTag() {
        // for synonyms -- set tag as a primer's one
        if (isSynonym()) {
            return resolveSynonym(this).getClassTag();
        }
        // check if it is non-primitive
        if (!isPrimitive()) {
            return CTTag.NONPRIMITIVE;
        }
        // no told subsumers
        if (!hasToldSubsumers()) {
            return CTTag.ORPHAN;
        }
        // now need to check all the told subsumers
        boolean hasLCD = false;
        boolean hasOther = false;
        boolean hasNP = false;
        for (ClassifiableEntry p : toldSubsumers) {
            switch (((Concept) p).getClassTag()) {
                case COMPLETELYDEFINED:
                    break;
                case ORPHAN:
                case LIKECOMPLETELYDEFINED:
                    hasLCD = true;
                    break;
                case REGULAR:
                    hasOther = true;
                    break;
                case HASNONPRIMITIVETS:
                case NONPRIMITIVE:
                    hasNP = true;
                    break;
                default:
                    throw new UnreachableSituationException();
            }
        }
        // there are non-primitive TS
        if (hasNP) {
            return CTTag.HASNONPRIMITIVETS;
        }
        // has something different from CD-like ones (and not CD)
        if (hasOther || !isCompletelyDefined()) {
            return CTTag.REGULAR;
        }
        // no more 'other' concepts here, and the CD-like structure
        if (hasLCD) {
            return CTTag.LIKECOMPLETELYDEFINED;
        }
        return CTTag.COMPLETELYDEFINED;
    }

    /**
     * @param stack
     *        stack
     * @param current
     *        current
     */
    @Original
    public void push(Deque<DLTree> stack, DLTree current) {
        // push subtrees: stack size increases by one or two, or current is a
        // leaf
        current.getChildren().stream().filter(p -> p != null).forEach(stack::push);
    }

    @Nullable
    @PortedFrom(file = "tConcept.h", name = "replaceSelfWithConst")
    private DLTree replaceWithConstOld(@Nullable DLTree t) {
        if (t == null) {
            return null;
        }
        Token token = t.token();
        // the three ifs are actually exclusive
        if (replacements.contains(token) && resolveSynonym((ClassifiableEntry) t.elem().getNE()).equals(this)) {
            return DLTreeFactory.createTop();
        }
        if (token == AND) {
            return DLTreeFactory.createSNFAnd(asList(t.getChildren().stream().map(this::replaceWithConstOld)), t);
        }
        if (token == NOT && (t.getChild().isAND() || replacements.contains(t.getChild().token()))) {
            return DLTreeFactory.createSNFNot(replaceWithConstOld(t.getChild()));
        }
        return t;
    }

    @PortedFrom(file = "tConcept.h", name = "hasSelfInDesc")
    private boolean hasSelfInDesc(@Nullable DLTree t) {
        if (t == null) {
            return false;
        }
        Token token = t.token();
        // the three ifs are actually exclusive
        if (replacements.contains(token)) {
            return resolveSynonym((ClassifiableEntry) t.elem().getNE()).equals(this);
        }
        if (token == AND) {
            return t.getChildren().stream().anyMatch(this::hasSelfInDesc);
        }
        if (token == NOT && (t.getChild().isAND() || replacements.contains(t.getChild().token()))) {
            return hasSelfInDesc(t.getChild());
        }
        return false;
    }

    /**
     * init told subsumers of the concept by given DESCription;
     * 
     * @param desc
     *        _desc
     * @param rolesProcessed
     *        RolesProcessed
     * @return TRUE iff concept is CD
     */
    @PortedFrom(file = "tConcept.h", name = "initToldSubsumers")
    public boolean initToldSubsumers(@Nullable DLTree desc, Set<Role> rolesProcessed) {
        if (desc == null || desc.isTOP()) {
            return true;
        }
        DLTree tree = desc;
        Token token = tree.token();
        if (replacements.contains(token)) {
            return addToldSubsumer((Concept) tree.elem().getNE());
        }
        if (token == NOT) {
            if (tree.getChild().token() == FORALL || tree.getChild().token() == LE) {
                searchTSbyRoleAndSupers(Role.resolveRole(tree.getChild().getLeft()), rolesProcessed);
            }
            return false;
        }
        if (token == SELF) {
            Role r = Role.resolveRole(tree.getChild());
            searchTSbyRoleAndSupers(r, rolesProcessed);
            searchTSbyRoleAndSupers(r.inverse(), rolesProcessed);
            return false;
        }
        if (token == AND) {
            // push all AND children on the list and traverse the list removing
            // n-th level ANDs and pushing their children in turn; ends up with
            // the leaves of the AND subtree
            AtomicBoolean b = new AtomicBoolean(true);
            tree.getChildren().stream().map(t -> Boolean.valueOf(initToldSubsumers(t, rolesProcessed))).forEach(x -> b
                .compareAndSet(true, x.booleanValue()));
            return b.get();
        }
        return false;
    }

    @PortedFrom(file = "tConcept.h", name = "SearchTSbyRole")
    private void searchTSbyRole(Role r, Set<Role> rolesProcessed) {
        if (rolesProcessed.contains(r)) {
            return;
        }
        DLTree domain = r.getTDomain();
        if (domain == null || domain.isConst()) {
            return;
        }
        rolesProcessed.add(r);
        this.initToldSubsumers(domain, rolesProcessed);
    }

    /**
     * @param r
     *        r
     * @param rolesProcessed
     *        RolesProcessed
     */
    @PortedFrom(file = "tConcept.h", name = "SearchTSbyRoleAndSupers")
    public void searchTSbyRoleAndSupers(Role r, Set<Role> rolesProcessed) {
        searchTSbyRole(r, rolesProcessed);
        r.getAncestor().forEach(q -> searchTSbyRole(q, rolesProcessed));
    }

    /**
     * @return told subsumers depth
     */
    @PortedFrom(file = "tConcept.h", name = "calculateTSDepth")
    public int calculateTSDepth() {
        if (tsDepth > 0) {
            return tsDepth;
        }
        if (hasToldSubsumers()) {
            tsDepth = toldSubsumers.stream().mapToInt(p -> ((Concept) p).calculateTSDepth()).max().orElse(1);
        } else {
            tsDepth = 1;
        }
        return tsDepth;
    }

    /** clear concept */
    @PortedFrom(file = "tConcept.h", name = "clear")
    public void clear() {
        // TNamedEntry clean
        setId(0);
        // ClassifiableEntry clean
        taxVertex = null;
        toldSubsumers = null;
        setCompletelyDefined(false);
        pSynonym = null;
        // TConcept clean
        removeDescription();
        setPrimitive(true);
        pBody = BP_INVALID;
        pName = BP_INVALID;
    }

    /**
     * @return true iff description contains top-level references to THIS
     *         concept
     */
    @PortedFrom(file = "tConcept.h", name = "hasSelfInDesc")
        boolean hasSelfInDesc() {
        return hasSelfInDesc(description);
    }

    /**
     * @return p name
     */
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

    /**
     * @return p body
     */
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

    /**
     * @return description
     */
    @Nullable
    @Original
    public DLTree getDescription() {
        return description;
    }

    /**
     * @return told subsumers depth
     */
    @Original
    public int getTsDepth() {
        return tsDepth;
    }

    @Original
    private void setTsDepth(int tsDepth) {
        this.tsDepth = tsDepth;
    }

    /**
     * @return neg features
     */
    @Original
    public LogicFeatures getNegFeatures() {
        return negFeatures;
    }

    /**
     * @return pos features
     */
    @Original
    public LogicFeatures getPosFeatures() {
        return posFeatures;
    }

    @Original
    @PortedFrom(file = "tConcept.h", name = "classTag")
    private void setClassTag(CTTag classTag) {
        this.classTag = classTag;
    }

    /**
     * @return true if primitive
     */
    @Original
    public boolean isPrimitive() {
        return primitive;
    }

    /**
     * @return false if primitive
     */
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

    /**
     * @return HasSingletonParent flag
     */
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

    /**
     * @return nominal
     */
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

    /**
     * @return singleton
     */
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
