package uk.ac.manchester.cs.jfact.split;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;

public class KnowledgeExplorer {
    /**  map concept into set of its synonyms */
    MultiMap<NamedEntity, Concept> Cs = new MultiMap<NamedEntity, Concept>();
    /**  map individual into set of its synonyms */
    MultiMap<NamedEntity, Individual> Is = new MultiMap<NamedEntity, Individual>();
    /**  map object role to the set of its super-roles (self included) */
    MultiMap<NamedEntity, Role> ORs = new MultiMap<NamedEntity, Role>();
    /**  map data role to the set of its super-roles (self included) */
    MultiMap<NamedEntity, Role> DRs = new MultiMap<NamedEntity, Role>();
    /**  dag-2-interface translator used in knowledge exploration */
    TDag2Interface D2I;
    /**  node vector to return */
    List<DlCompletionTree> Nodes = new ArrayList<DlCompletionTree>();
    /**  role set to return */
    Set<RoleExpression> Roles = new HashSet<RoleExpression>();
    /**  concept vector to return */
    List<Expression> Concepts = new ArrayList<Expression>();

    /**  adds an entity as a synonym to a map MAP */
    <E extends ClassifiableEntry> void addE(MultiMap<E, E> map, E entry) {
        map.put(entry, entry);
        if (entry.isSynonym()) {
            map.put((E) entry.getSynonym(), entry);
        }
    }

    public KnowledgeExplorer(TBox box, ExpressionManager pEM) {
        D2I = new TDag2Interface(box.getDag(), pEM);
        // init all concepts
        for (Concept c : box.c_begin()) {
            Cs.put(c.getEntity(), c);
            if (c.isSynonym()) {
                Cs.put(c.getSynonym().getEntity(), c);
            }
        }
        // init all individuals
        for (Individual i : box.i_begin()) {
            Is.put(i.getEntity(), i);
            if (i.isSynonym()) {
                Is.put(i.getSynonym().getEntity(), i);
            }
        }
        // init all object roles
        for (Role R : box.getORM().getRoles()) {
            ORs.put(R.getEntity(), R);
            if (R.isSynonym()) {
                ORs.put(R.getSynonym().getEntity(), R);
            }
            ORs.putAll(R.getEntity(), R.getAncestor());
        }
        // init all data roles
        for (Role R : box.getDRM().getRoles()) {
            DRs.put(R.getEntity(), R);
            if (R.isSynonym()) {
                DRs.put(R.getSynonym().getEntity(), R);
            }
            DRs.putAll(R.getEntity(), R.getAncestor());
        }
    }

    /*
     * # measurement update H.show() x.show() y =
     * matrix([[measurements[n]]]).__sub__(H.__mul__(x)) y.show() S =
     * R.__add__(H.__mul__(P.__mul__(H.transpose()))) K =
     * P.__mul__(H.transpose().__mul__(S.inverse())) K.show() # y is constant yy
     * = y.__repr__() print yy[0][0] x = x.__add__(K.__mul__(yy)) P =
     * (I.__sub(K.__mul__(H))).__mul__(P) # prediction x =
     * u.__add__(F.__mul__(x)) P = F.__mul__(P.__mul__(transpose(F))) print 'x=
     * ' x.show() print 'P= ' P.show()
     */
    /**  add concept-like entity E (possibly with synonyms) to CONCEPTS */
    void addC(Expression e) {
        // check named concepts
        if (e instanceof ConceptName) {
            ConceptName C = (ConceptName) e;
            for (Concept p : Cs.get(C)) {
                if (p == null) {
                    System.err
                            .println("Null found while processing class " + C.getName());
                } else {
                    Concepts.add(D2I.getCExpr(p.getId()));
                }
            }
            return;
        }
        // check named individuals
        if (e instanceof IndividualName) {
            IndividualName I = (IndividualName) e;
            for (Individual p : Is.get(I)) {
                if (p == null) {
                    System.err.println("Null found while processing individual "
                            + I.getName());
                } else {
                    Concepts.add(D2I.getCExpr(p.getId()));
                }
            }
            return;
        }
        Concepts.add(e);
    }

    public Set<RoleExpression> getDataRoles(DlCompletionTree node, boolean onlyDet) {
        Roles.clear();
        for (DlCompletionTreeArc p : node.getNeighbour()) {
            if (!p.isIBlocked() && p.getArcEnd().isDataNode()
                    && (!onlyDet || p.getDep().isEmpty())) {
                for (Role r : DRs.get(p.getRole().getEntity())) {
                    Roles.add(D2I.getDataRoleExpression(r));
                }
            }
        }
        // Roles.addAll(DRs.get(p.getRole().getEntity()));
        return Roles;
    }

    /**  build the set of object neighbours of a NODE; incoming edges are */
    // counted iff NEEDINCOMING is true
    public Set<RoleExpression> getObjectRoles(DlCompletionTree node, boolean onlyDet,
            boolean needIncoming) {
        Roles.clear();
        for (DlCompletionTreeArc p : node.getNeighbour()) {
            if (!p.isIBlocked() && !p.getArcEnd().isDataNode()
                    && (!onlyDet || p.getDep().isEmpty())
                    && (needIncoming || p.isSuccEdge())) {
                for (Role r : ORs.get(p.getRole().getEntity())) {
                    Roles.add(D2I.getObjectRoleExpression(r));
                }
            }
        }
        return Roles;
    }

    /**  build the set of neighbours of a NODE via role ROLE; put the resulting */
    // list into RESULT
    public List<DlCompletionTree> getNeighbours(DlCompletionTree node, Role R) {
        Nodes.clear();
        for (DlCompletionTreeArc p : node.getNeighbour()) {
            if (!p.isIBlocked() && p.isNeighbour(R)) {
                Nodes.add(p.getArcEnd());
            }
        }
        return Nodes;
    }

    /**  put into RESULT all the data expressions from the NODE label */
    public List<ConceptExpression> getObjectLabel(DlCompletionTree node, boolean onlyDet) {
        // prepare D2I translator
        D2I.ensureDagSize();
        assert !node.isDataNode();
        Concepts.clear();
        for (ConceptWDep p : node.beginl_sc()) {
            if (!onlyDet || p.getDep().isEmpty()) {
                addC(D2I.getExpr(p.getConcept(), false));
            }
        }
        for (ConceptWDep p : node.beginl_cc()) {
            if (!onlyDet || p.getDep().isEmpty()) {
                addC(D2I.getExpr(p.getConcept(), false));
            }
        }
        List<ConceptExpression> toReturn = new ArrayList<ConceptExpression>();
        for (Expression e : Concepts) {
            if (e instanceof ConceptExpression) {
                toReturn.add((ConceptExpression) e);
            }
        }
        return toReturn;
    }

    public List<DataExpression> getDataLabel(DlCompletionTree node, boolean onlyDet) {
        // prepare D2I translator
        D2I.ensureDagSize();
        assert node.isDataNode();
        Concepts.clear();
        for (ConceptWDep p : node.beginl_sc()) {
            if (!onlyDet || p.getDep().isEmpty()) {
                addC(D2I.getExpr(p.getConcept(), true));
            }
        }
        for (ConceptWDep p : node.beginl_cc()) {
            if (!onlyDet || p.getDep().isEmpty()) {
                addC(D2I.getExpr(p.getConcept(), true));
            }
        }
        List<DataExpression> toReturn = new ArrayList<DataExpression>();
        for (Expression e : Concepts) {
            if (e instanceof DataExpression) {
                toReturn.add((DataExpression) e);
            }
        }
        return toReturn;
    }
}
