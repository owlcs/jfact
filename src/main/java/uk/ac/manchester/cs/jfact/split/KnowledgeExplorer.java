package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTreeArc;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.Individual;
import uk.ac.manchester.cs.jfact.kernel.Role;
import uk.ac.manchester.cs.jfact.kernel.TBox;
import uk.ac.manchester.cs.jfact.kernel.TDag2Interface;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import conformance.PortedFrom;

/** knowledge explorer */
@PortedFrom(file = "KnowledgeExplorer.h", name = "KnowledgeExplorer")
public class KnowledgeExplorer implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** map concept into set of its synonyms */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Cs")
    private final Multimap<NamedEntity, Concept> Cs = LinkedHashMultimap
            .create();
    /** map individual into set of its synonyms */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Is")
    private final Multimap<NamedEntity, Individual> Is = LinkedHashMultimap
            .create();
    /** map object role to the set of its super-roles (self included) */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "ORs")
    private final Multimap<NamedEntity, Role> ORs = LinkedHashMultimap.create();
    /** map data role to the set of its super-roles (self included) */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "DRs")
    private final Multimap<NamedEntity, Role> DRs = LinkedHashMultimap.create();
    /** dag-2-interface translator used in knowledge exploration */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "D2I")
    private final TDag2Interface D2I;
    /** node vector to return */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Nodes")
    private final List<DlCompletionTree> Nodes = new ArrayList<>();
    /** concept vector to return */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "Concepts")
    private final List<Expression> Concepts = new ArrayList<>();

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
    private static <E extends ClassifiableEntry> void addE(Multimap<E, E> map,
            E entry) {
        map.put(entry, entry);
        if (entry.isSynonym()) {
            map.put((E) entry.getSynonym(), entry);
        }
    }

    /**
     * @param box
     *        box
     * @param pEM
     *        pEM
     */
    public KnowledgeExplorer(TBox box, ExpressionCache pEM) {
        D2I = new TDag2Interface(box.getDag(), pEM);
        // init all concepts
        box.getConcepts().forEach(c -> addConceptsAndIndividuals(Cs, c));
        // init all individuals
        box.i_begin().forEach(i -> addConceptsAndIndividuals(Is, i));
        // init all object roles
        box.getORM().getRoles().forEach(r -> addRoles(ORs, r));
        // init all data roles
        box.getDRM().getRoles().forEach(r -> addRoles(DRs, r));
    }

    <T extends ClassifiableEntry> void addConceptsAndIndividuals(
            Multimap<NamedEntity, T> m, T t) {
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
            Cs.get((ConceptName) e).forEach(
                    p -> Concepts.add(D2I.getCExpr(p.getId())));
            return;
        }
        // check named individuals
        if (e instanceof IndividualName) {
            Is.get((IndividualName) e).forEach(
                    p -> Concepts.add(D2I.getCExpr(p.getId())));
            return;
        }
        Concepts.add(e);
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return set of data roles
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getDataRoles")
    public Set<DataRoleExpression> getDataRoles(DlCompletionTree node,
            boolean onlyDet) {
        return node.getNeighbour().stream()
                .filter(p -> notBlockedData(onlyDet, p))
                .flatMap(p -> DRs.get(p.getRole().getEntity()).stream())
                .map(r -> (DataRoleExpression) D2I.getDataRoleExpression(r))
                .collect(toSet());
    }

    protected boolean notBlockedData(boolean onlyDet, DlCompletionTreeArc p) {
        return !p.isIBlocked() && p.getArcEnd().isDataNode()
                && (!onlyDet || p.getDep().isEmpty());
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
    public Set<ObjectRoleExpression> getObjectRoles(DlCompletionTree node,
            boolean onlyDet, boolean needIncoming) {
        return node
                .getNeighbour()
                .stream()
                .filter(p -> notBlockedNotData(onlyDet, needIncoming, p))
                .flatMap(p -> ORs.get(p.getRole().getEntity()).stream())
                .map(r -> (ObjectRoleExpression) D2I.getObjectRoleExpression(r))
                .collect(toSet());
    }

    protected boolean notBlockedNotData(boolean onlyDet, boolean needIncoming,
            DlCompletionTreeArc p) {
        return !p.isIBlocked() && !p.getArcEnd().isDataNode()
                && (!onlyDet || p.getDep().isEmpty())
                && (needIncoming || p.isSuccEdge());
    }

    /**
     * @param node
     *        node
     * @param R
     *        R
     * @return set of neighbours of a NODE via role ROLE; put the resulting list
     *         into RESULT
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getNeighbours")
    public List<DlCompletionTree> getNeighbours(DlCompletionTree node, Role R) {
        Nodes.clear();
        node.getNeighbour().stream()
                .filter(p -> !p.isIBlocked() && p.isNeighbour(R))
                .forEach(p -> Nodes.add(p.getArcEnd()));
        return Nodes;
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return all the data expressions from the NODE label
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getLabel")
    public List<ConceptExpression> getObjectLabel(DlCompletionTree node,
            boolean onlyDet) {
        // prepare D2I translator
        D2I.ensureDagSize();
        assert !node.isDataNode();
        Concepts.clear();
        Stream.concat(node.beginl_sc().stream(), node.beginl_cc().stream())
                .filter(p -> !onlyDet || p.getDep().isEmpty())
                .forEach(p -> addC(D2I.getExpr(p.getConcept(), false)));
        return Concepts.stream().filter(e -> e instanceof ConceptExpression)
                .map(e -> (ConceptExpression) e).collect(toList());
    }

    /**
     * @param node
     *        node
     * @param onlyDet
     *        onlyDet
     * @return list of data labels
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getLabel")
    public List<DataExpression> getDataLabel(DlCompletionTree node,
            boolean onlyDet) {
        // prepare D2I translator
        D2I.ensureDagSize();
        assert node.isDataNode();
        Concepts.clear();
        Stream.concat(node.beginl_sc().stream(), node.beginl_cc().stream())
                .filter(p -> !onlyDet || p.getDep().isEmpty())
                .forEach(p -> addC(D2I.getExpr(p.getConcept(), true)));
        return Concepts.stream().filter(e -> e instanceof DataExpression)
                .map(e -> (DataExpression) e).collect(toList());
    }

    /**
     * @param node
     *        node
     * @return blocker of a blocked node NODE or NULL if node is not blocked
     */
    @PortedFrom(file = "KnowledgeExplorer.h", name = "getBlocker")
    @Nonnull
    public DlCompletionTree getBlocker(DlCompletionTree node) {
        return node.getBlocker();
    }
}
