package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;

/** knowledge explorer */
@PortedFrom(file = "KnowledgeExplorer.h", name = "KnowledgeExplorer")
public class KnowledgeExplorer implements Serializable {

    /** map concept into set of its synonyms */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Cs") private final Multimap<NamedEntity, Concept> cs = LinkedHashMultimap
        .create();
    /** map individual into set of its synonyms */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Is") private final Multimap<NamedEntity, Individual> is = LinkedHashMultimap
        .create();
    /** map object role to the set of its super-roles (self included) */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "ORs") private final Multimap<NamedEntity, Role> ors = LinkedHashMultimap
        .create();
    /** map data role to the set of its super-roles (self included) */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "DRs") private final Multimap<NamedEntity, Role> drs = LinkedHashMultimap
        .create();
    /** dag-2-interface translator used in knowledge exploration */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "D2I") private final TDag2Interface d2i;
    /** node vector to return */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Nodes") private final List<DlCompletionTree> nodes = new ArrayList<>();
    /** concept vector to return */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Concepts") private final List<Expression> concepts = new ArrayList<>();

    /**
     * @param box
     *        box
     * @param pEM
     *        pEM
     */
    public KnowledgeExplorer(TBox box, ExpressionCache pEM) {
        d2i = new TDag2Interface(box.getDag(), pEM);
        // init all concepts
        box.getConcepts().forEach(c -> addConceptsAndIndividuals(cs, c));
        // init all individuals
        box.individuals().forEach(i -> addConceptsAndIndividuals(is, i));
        // init all object roles
        box.getORM().getRoles().forEach(r -> addRoles(ors, r));
        // init all data roles
        box.getDRM().getRoles().forEach(r -> addRoles(drs, r));
    }

    /**
     * adds an entity as a synonym to a map MAP
     * 
     * @param map
     *        map
     * @param entry
     *        entry
     */
    @SuppressWarnings("unchecked")
    @PortedFrom(file = "KnowledgeExplorer.h", name = "addE")
    private static <E extends ClassifiableEntry> void addE(Multimap<E, E> map, E entry) {
        map.put(entry, entry);
        if (entry.isSynonym()) {
            map.put((E) entry.getSynonym(), entry);
        }
    }

    <T extends ClassifiableEntry> void addConceptsAndIndividuals(Multimap<NamedEntity, T> m, T t) {
        m.put(t.getEntity(), t);
        if (t.isSynonym()) {
            m.put(t.getSynonym().getEntity(), t);
        }
    }

    void addRoles(Multimap<NamedEntity, Role> m, Role t) {
        m.put(t.getEntity(), t);
        if (t.isSynonym()) {
            m.put(t.getSynonym().getEntity(), t);
        }
        m.putAll(t.getEntity(), t.getAncestor());
    }

    /**
     * add concept-like entity E (possibly with synonyms) to CONCEPTS
     * 
     * @param e
     *        e
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "addC")
    private void addC(Expression e) {
        // check named concepts
        if (e instanceof ConceptName) {
            cs.get((ConceptName) e).forEach(p -> concepts.add(d2i.getCExpr(p.getId())));
            return;
        }
        // check named individuals
        if (e instanceof IndividualName) {
            is.get((IndividualName) e).forEach(p -> concepts.add(d2i.getCExpr(p.getId())));
            return;
        }
        concepts.add(e);
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return set of data roles
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getDataRoles")
    public Set<DataRoleExpression> getDataRoles(DlCompletionTree node, boolean onlyDet) {
        return asSet(node.getNeighbour().stream().filter(p -> notBlockedData(onlyDet, p))
            .flatMap(p -> drs.get(p.getRole().getEntity()).stream())
            .map(r -> (DataRoleExpression) d2i.getDataRoleExpression(r)));
    }

    protected boolean notBlockedData(boolean onlyDet, DlCompletionTreeArc p) {
        return !p.isIBlocked() && p.getArcEnd().isDataNode() && (!onlyDet || p.getDep().isEmpty());
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @param needIncoming
     *        needIncoming
     * @return set of object neighbours of a NODE; incoming edges are counted
     *         iff NEEDINCOMING is true
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getObjectRoles")
    public Set<ObjectRoleExpression> getObjectRoles(DlCompletionTree node, boolean onlyDet, boolean needIncoming) {
        return asSet(node.getNeighbour().stream().filter(p -> notBlockedNotData(onlyDet, needIncoming, p))
            .flatMap(p -> ors.get(p.getRole().getEntity()).stream())
            .map(r -> (ObjectRoleExpression) d2i.getObjectRoleExpression(r)));
    }

    protected boolean notBlockedNotData(boolean onlyDet, boolean needIncoming, DlCompletionTreeArc p) {
        return !p.isIBlocked() && !p.getArcEnd().isDataNode() && (!onlyDet || p.getDep().isEmpty())
            && (needIncoming || p.isSuccEdge());
    }

    /**
     * @param node
     *        node
     * @param no
     *        R
     * @return set of neighbours of a NODE via role ROLE; put the resulting list
     *         into RESULT
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getNeighbours")
    public List<DlCompletionTree> getNeighbours(DlCompletionTree node, Role no) {
        nodes.clear();
        node.getNeighbour().stream().filter(p -> !p.isIBlocked() && p.isNeighbour(no))
            .forEach(p -> nodes.add(p.getArcEnd()));
        return nodes;
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return all the data expressions from the NODE label
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getLabel")
    public List<ConceptExpression> getObjectLabel(DlCompletionTree node, boolean onlyDet) {
        // prepare D2I translator
        d2i.ensureDagSize();
        assert !node.isDataNode();
        concepts.clear();
        Stream.concat(node.simpleConcepts().stream(), node.complexConcepts().stream())
            .filter(p -> !onlyDet || p.getDep().isEmpty()).forEach(p -> addC(d2i.getExpr(p.getConcept(), false)));
        return asList(concepts.stream().filter(e -> e instanceof ConceptExpression).map(e -> (ConceptExpression) e));
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return list of data labels
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getLabel")
    public List<DataExpression> getDataLabel(DlCompletionTree node, boolean onlyDet) {
        // prepare D2I translator
        d2i.ensureDagSize();
        assert node.isDataNode();
        concepts.clear();
        Stream.concat(node.simpleConcepts().stream(), node.complexConcepts().stream())
            .filter(p -> !onlyDet || p.getDep().isEmpty()).forEach(p -> addC(d2i.getExpr(p.getConcept(), true)));
        return asList(concepts.stream().filter(e -> e instanceof DataExpression).map(e -> (DataExpression) e));
    }

    /**
     * @param node
     *        node
     * @return blocker of a blocked node NODE or NULL if node is not blocked
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getBlocker")
    public DlCompletionTree getBlocker(DlCompletionTree node) {
        return node.getBlocker();
    }
}
