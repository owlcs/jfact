package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import conformance.Original;
import conformance.PortedFrom;

/** role master */
@PortedFrom(file = "RoleMaster.h", name = "RoleMaster")
public class RoleMaster implements Serializable {

    private static final long serialVersionUID = 11000L;

    protected static class RoleCreator implements NameCreator<Role, IRI>,
            Serializable {

        private static final long serialVersionUID = 11000L;

        @Override
        public Role makeEntry(IRI name) {
            return new Role(name);
        }
    }

    /** number of the last registered role */
    @PortedFrom(file = "RoleMaster.h", name = "newRoleId")
    private int newRoleId;
    /** all registered roles */
    @PortedFrom(file = "RoleMaster.h", name = "Roles")
    private final List<Role> roles = new ArrayList<>();
    /** internal empty role (bottom in the taxonomy) */
    @PortedFrom(file = "RoleMaster.h", name = "emptyRole")
    private final Role emptyRole;
    /** internal universal role (top in the taxonomy) */
    @PortedFrom(file = "RoleMaster.h", name = "universalRole")
    private final Role universalRole;
    /** roles nameset */
    @PortedFrom(file = "RoleMaster.h", name = "roleNS")
    private final NameSet<Role, IRI> roleNS;
    /** Taxonomy of roles */
    @PortedFrom(file = "RoleMaster.h", name = "pTax")
    private final Taxonomy pTax;
    /** two halves of disjoint roles axioms */
    @PortedFrom(file = "RoleMaster.h", name = "DJRolesA")
    private final List<Role> disjointRolesA = new ArrayList<>();
    @PortedFrom(file = "RoleMaster.h", name = "DJRolesB")
    private final List<Role> disjointRolesB = new ArrayList<>();
    /** flag whether to create data roles or not */
    @PortedFrom(file = "RoleMaster.h", name = "DataRoles")
    private final boolean dataRoles;
    /** flag if it is possible to introduce new names */
    @PortedFrom(file = "RoleMaster.h", name = "useUndefinedNames")
    private boolean useUndefinedNames;
    @Original
    private static final int firstRoleIndex = 2;

    /**
     * TRole and its inverse in RoleBox
     * 
     * @param r
     *        r
     */
    @PortedFrom(file = "RoleMaster.h", name = "registerRole")
    private void registerRole(Role r) {
        assert r != null && r.getInverse() == null; // sanity check
        assert r.getId() == 0; // only call it for the new roles
        if (dataRoles) {
            r.setDataRole(true);
        }
        roles.add(r);
        r.setId(newRoleId);
        // create new role which would be inverse of R
        Role ri = new Role(IRI.create("-" + r.getName()));
        // set up inverse
        r.setInverse(ri);
        ri.setInverse(r);
        roles.add(ri);
        ri.setId(-newRoleId);
        ++newRoleId;
    }

    /**
     * @param p
     *        p
     * @return true if P is a role that is registered in the RM
     */
    @PortedFrom(file = "RoleMaster.h", name = "isRegisteredRole")
    private boolean isRegisteredRole(NamedEntry p) {
        if (!(p instanceof Role)) {
            return false;
        }
        Role R = (Role) p;
        int ind = R.getAbsoluteIndex();
        return ind >= firstRoleIndex && ind < roles.size()
                && roles.get(ind).equals(p);
    }

    /** @return number of roles */
    @PortedFrom(file = "RoleMaster.h", name = "size")
    public int size() {
        return roles.size() / 2 - 1;
    }

    /**
     * @param d
     *        d
     * @param TopRoleName
     *        TopRoleName
     * @param BotRoleName
     *        BotRoleName
     * @param c
     *        c
     */
    public RoleMaster(boolean d, IRI TopRoleName, IRI BotRoleName,
            JFactReasonerConfiguration c) {
        newRoleId = 1;
        emptyRole = new Role(BotRoleName);
        universalRole = new Role(TopRoleName);
        roleNS = new NameSet<>(new RoleCreator());
        dataRoles = d;
        useUndefinedNames = true;
        // no zero-named roles allowed
        roles.add(null);
        roles.add(null);
        // setup empty role
        emptyRole.setId(0);
        emptyRole.setInverse(emptyRole);
        emptyRole.setDataRole(dataRoles);
        emptyRole.setBPDomain(Helper.bpBOTTOM);
        emptyRole.setBottom();
        // setup universal role
        universalRole.setId(0);
        universalRole.setInverse(universalRole);
        universalRole.setDataRole(dataRoles);
        universalRole.setBPDomain(Helper.bpTOP);
        universalRole.setTop();
        // FIXME!! now it is not transitive => simple
        universalRole.getAutomaton().setCompleted(true);
        // create roles taxonomy
        pTax = new Taxonomy(universalRole, emptyRole, c);
    }

    /**
     * @param name
     *        name
     * @return role entry with given name
     */
    @PortedFrom(file = "RoleMaster.h", name = "ensureRoleName")
    public NamedEntry ensureRoleName(IRI name) {
        // check for the Top/Bottom names
        if (name.equals(emptyRole.getName())) {
            return emptyRole;
        }
        if (name.equals(universalRole.getName())) {
            return universalRole;
        }
        // new name from NS
        Role p = roleNS.insert(name);
        if (isRegisteredRole(p)) {
            return p;
        }
        // not registered but has non-null ID
        if (p.getId() != 0 || !useUndefinedNames) {
            throw new OWLRuntimeException("Unable to register '" + name
                    + "' as a " + (dataRoles ? "data role" : "role"));
        }
        registerRole(p);
        return p;
    }

    /**
     * add synonym to existing role
     * 
     * @param role
     *        role
     * @param syn
     *        syn
     */
    @PortedFrom(file = "RoleMaster.h", name = "addRoleSynonym")
    public static void addRoleSynonym(Role role, Role syn) {
        // no synonyms
        // FIXME!! 1st call can make one of them a synonym of a const
        addRoleParentProper(ClassifiableEntry.resolveSynonym(role),
                ClassifiableEntry.resolveSynonym(syn));
        addRoleParentProper(ClassifiableEntry.resolveSynonym(syn),
                ClassifiableEntry.resolveSynonym(role));
    }

    /**
     * add parent for the input role
     * 
     * @param role
     *        role
     * @param parent
     *        parent
     */
    @PortedFrom(file = "RoleMaster.h", name = "addRoleParentProper")
    public static void addRoleParentProper(Role role, Role parent) {
        assert !role.isSynonym() && !parent.isSynonym();
        if (role.equals(parent)) {
            return;
        }
        if (role.isDataRole() != parent.isDataRole()) {
            throw new ReasonerInternalException(
                    "Mixed object and data roles in role subsumption axiom");
        }
        // check the inconsistency case *UROLE* [= *EROLE*
        if (role.isTop() && parent.isBottom()) {
            throw new InconsistentOntologyException();
        }
        // *UROLE* [= R means R (and R-) are synonym of *UROLE*
        if (role.isTop()) {
            parent.setSynonym(role);
            parent.inverse().setSynonym(role);
            return;
        }
        // R [= *EROLE* means R (and R-) are synonyms of *EROLE*
        if (parent.isBottom()) {
            role.setSynonym(parent);
            role.inverse().setSynonym(parent);
            return;
        }
        role.addParent(parent);
        role.inverse().addParent(parent.inverse());
    }

    /**
     * a pair of disjoint roles
     * 
     * @param R
     *        R
     * @param S
     *        S
     */
    @PortedFrom(file = "RoleMaster.h", name = "addDisjointRoles")
    public void addDisjointRoles(Role R, Role S) {
        // object- and data roles are always disjoint
        if (R.isDataRole() != S.isDataRole()) {
            return;
        }
        disjointRolesA.add(R);
        disjointRolesB.add(S);
    }

    /**
     * change the undefined names usage policy
     * 
     * @param val
     *        val
     */
    @PortedFrom(file = "RoleMaster.h", name = "setUndefinedNames")
    public void setUndefinedNames(boolean val) {
        useUndefinedNames = val;
    }

    /** @return list of roles */
    @PortedFrom(file = "RoleMaster.h", name = "begin")
    public List<Role> getRoles() {
        return roles.subList(firstRoleIndex, roles.size());
    }

    /** @return taxonomy */
    @PortedFrom(file = "RoleMaster.h", name = "getTaxonomy")
    public Taxonomy getTaxonomy() {
        return pTax;
    }

    public Stream<Role> roles() {
        return roles.stream().skip(firstRoleIndex);
    }

    /**
     * @param o
     *        o
     * @param type
     *        type
     */
    @PortedFrom(file = "RoleMaster.h", name = "Print")
    public void print(LogAdapter o, String type) {
        if (size() == 0) {
            return;
        }
        o.print(type, " Roles (").print(size()).print("):\n");
        o.print(emptyRole);
        roles().forEach(p -> p.print(o));
    }

    /** @return true if there are reflexive roles */
    @PortedFrom(file = "RoleMaster.h", name = "hasReflexiveRoles")
    public boolean hasReflexiveRoles() {
        return roles().anyMatch(p -> p.isReflexive());
    }

    /**
     * @param RR
     *        RR
     */
    @PortedFrom(file = "RoleMaster.h", name = "fillReflexiveRoles")
    public void fillReflexiveRoles(List<Role> RR) {
        RR.clear();
        roles().filter(p -> !p.isSynonym() && p.isReflexive()).forEach(
                p -> RR.add(p));
    }

    /** add parent for the input role */
    @PortedFrom(file = "RoleMaster.h", name = "addRoleParent")
    public static void addRoleParent(Role role, Role parent) {
        addRoleParentProper(ClassifiableEntry.resolveSynonym(role),
                ClassifiableEntry.resolveSynonym(parent));
    }

    /**
     * @param tree
     *        tree
     * @param parent
     *        parent
     */
    @PortedFrom(file = "RoleMaster.h", name = "addRoleParent")
    public static void addRoleParent(DLTree tree, Role parent) {
        if (tree == null) {
            return;
        }
        if (tree.token() == RCOMPOSITION) {
            parent.addComposition(tree);
            DLTree inv = DLTreeFactory.inverseComposition(tree);
            parent.inverse().addComposition(inv);
        } else if (tree.token() == PROJINTO) {
            // here -R.C became -PARENT.
            // encode this as PROJFROM(R-,PROJINTO(PARENT-,C)),
            // added to the range of R
            Role R = Role.resolveRole(tree.getLeft());
            // can't do anything ATM for the data roles
            if (R.isDataRole()) {
                throw new ReasonerInternalException(
                        "Projection into not implemented for the data role");
            }
            DLTree C = tree.getRight().copy();
            DLTree InvP = DLTreeFactory.buildTree(new Lexeme(RNAME, parent
                    .inverse()));
            DLTree InvR = DLTreeFactory
                    .buildTree(new Lexeme(RNAME, R.inverse()));
            // C = PROJINTO(PARENT-,C)
            C = DLTreeFactory.buildTree(new Lexeme(PROJINTO), InvP, C);
            // C = PROJFROM(R-,PROJINTO(PARENT-,C))
            C = DLTreeFactory.buildTree(new Lexeme(PROJFROM), InvR, C);
            R.setRange(C);
        } else if (tree.token() == PROJFROM) {
            // here C-R. became -PARENT.
            // encode this as PROJFROM(R,PROJINTO(PARENT,C)),
            // added to the domain of R
            Role R = Role.resolveRole(tree.getLeft());
            DLTree C = tree.getRight().copy();
            DLTree P = DLTreeFactory.buildTree(new Lexeme(RNAME, parent));
            // C = PROJINTO(PARENT,C)
            C = DLTreeFactory.buildTree(new Lexeme(PROJINTO), P, C);
            // C = PROJFROM(R,PROJINTO(PARENT,C))
            C = DLTreeFactory.buildTree(new Lexeme(PROJFROM), tree.getLeft()
                    .copy(), C);
            R.setDomain(C);
        } else {
            addRoleParent(Role.resolveRole(tree), parent);
        }
    }

    /** init ancestor description */
    @PortedFrom(file = "RoleMaster.h", name = "initAncDesc")
    public void initAncDesc() {
        int nRoles = roles.size();
        roles().forEach(p -> p.eliminateToldCycles());
        roles().filter(p -> p.isSynonym()).forEach(p -> {
            p.canonicaliseSynonym();
            p.addFeaturesToSynonym();
        });
        roles().filter(p -> !p.isSynonym()).forEach(
                p -> p.removeSynonymsFromParents());
        // here TOP-role has no children yet, so it's safe to complete the
        // automaton
        universalRole.completeAutomaton(nRoles);
        roles().filter(p -> !p.isSynonym() && !p.hasToldSubsumers()).forEach(
                p -> p.addParent(universalRole));
        TaxonomyCreator taxCreator = new TaxonomyCreator(pTax);
        taxCreator.setCompletelyDefined(true);
        roles().filter(p -> !p.isClassified()).forEach(
                p -> taxCreator.classifyEntry(p));
        roles().filter(p -> !p.isSynonym()).forEach(
                p -> p.initADbyTaxonomy(pTax, nRoles));
        roles().filter(p -> !p.isSynonym()).forEach(
                p -> p.completeAutomaton(nRoles));
        pTax.finalise();
        if (!disjointRolesA.isEmpty()) {
            for (int i = 0; i < disjointRolesA.size(); i++) {
                Role q = disjointRolesA.get(i);
                Role r = disjointRolesB.get(i);
                Role R = ClassifiableEntry.resolveSynonym(q);
                Role S = ClassifiableEntry.resolveSynonym(r);
                R.addDisjointRole(S);
                S.addDisjointRole(R);
                R.inverse().addDisjointRole(S.inverse());
                S.inverse().addDisjointRole(R.inverse());
            }
            roles().filter(p -> !p.isSynonym() && p.isDisjoint()).forEach(
                    p -> p.checkHierarchicalDisjoint());
        }
        roles().filter(p -> !p.isSynonym()).forEach(p -> p.postProcess());
        roles().filter(p -> !p.isSynonym()).forEach(p -> p.consistent());
    }

    /** @return pointer to a TOP role */
    @PortedFrom(file = "RoleMaster.h", name = "getTopRole")
    public Role getTopRole() {
        return universalRole;
    }

    /** @return pointer to a BOTTOM role */
    @PortedFrom(file = "RoleMaster.h", name = "getBotRole")
    public Role getBotRole() {
        return emptyRole;
    }
}
