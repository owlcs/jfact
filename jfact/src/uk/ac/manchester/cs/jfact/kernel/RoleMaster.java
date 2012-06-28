package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.helpers.DLTreeFactory;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

public final class RoleMaster {
    protected static final class RoleCreator implements NameCreator<Role> {
        public Role makeEntry(final String name) {
            return new Role(name);
        }
    }

    /** number of the last registered role */
    private int newRoleId;
    /** all registered roles */
    private final List<Role> roles = new ArrayList<Role>();
    /** internal empty role (bottom in the taxonomy) */
    private final Role emptyRole;
    /** internal universal role (top in the taxonomy) */
    private final Role universalRole;
    /** roles nameset */
    private final NameSet<Role> roleNS;
    /** Taxonomy of roles */
    private final Taxonomy pTax;
    /** two halves of disjoint roles axioms */
    private final List<Role> disjointRolesA = new ArrayList<Role>();
    private final List<Role> disjointRolesB = new ArrayList<Role>();
    /** flag whether to create data roles or not */
    private final boolean dataRoles;
    /** flag if it is possible to introduce new names */
    private boolean useUndefinedNames;
    private static final int firstRoleIndex = 2;

    /** TRole and it's inverse in RoleBox */
    private void registerRole(final Role r) {
        assert r != null && r.getInverse() == null; // sanity check
        assert r.getId() == 0; // only call it for the new roles
        if (dataRoles) {
            r.setDataRole();
        }
        roles.add(r);
        r.setId(newRoleId);
        // create new role which would be inverse of R
        String iname = "-";
        iname += r.getName();
        Role ri = new Role(iname);
        // set up inverse
        r.setInverse(ri);
        ri.setInverse(r);
        roles.add(ri);
        ri.setId(-newRoleId);
        ++newRoleId;
    }

    /** @return true if P is a role that is registered in the RM */
    private boolean isRegisteredRole(final NamedEntry p) {
        if (!(p instanceof Role)) {
            return false;
        }
        final Role R = (Role) p;
        int ind = R.getAbsoluteIndex();
        return ind >= firstRoleIndex && ind < roles.size() && roles.get(ind).equals(p);
    }

    /** get number of roles */
    public int size() {
        return roles.size() / 2 - 1;
    }

    public RoleMaster(final boolean d, final String TopRoleName,
            final String BotRoleName, final JFactReasonerConfiguration c) {
        newRoleId = 1;
        emptyRole = new Role(BotRoleName.equals("") ? "emptyRole" : BotRoleName);
        universalRole = new Role(TopRoleName.equals("") ? "universalRole" : TopRoleName);
        roleNS = new NameSet<Role>(new RoleCreator());
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
        // OWL 2 says Bottom role is non-simple, but this is unnecessary
        // and leads to inconsistent results wrt presence of an axiom Bot [= R
        emptyRole.setSimple();
        // setup universal role
        universalRole.setId(0);
        universalRole.setInverse(universalRole);
        universalRole.setDataRole(dataRoles);
        universalRole.setBPDomain(Helper.bpTOP);
        universalRole.setTop();
        // create roles taxonomy
        pTax = new Taxonomy(universalRole, emptyRole, c);
    }

    /** create role entry with given name */
    public NamedEntry ensureRoleName(final String name) {
        // check for the Top/Bottom names
        if (name.equals(emptyRole.getName())) {
            return emptyRole;
        }
        if (name.equals(universalRole.getName())) {
            return universalRole;
        }
        // new name from NS
        Role p = roleNS.insert(name);
        // check what happens
        if (p == null) {
            throw new OWLRuntimeException("Unable to register '" + name + "' as a "
                    + (dataRoles ? "data role" : "role"));
        }
        if (isRegisteredRole(p)) {
            return p;
        }
        if (p.getId() != 0 || // not registered but has non-null ID
                !useUndefinedNames) {
            throw new OWLRuntimeException("Unable to register '" + name + "' as a "
                    + (dataRoles ? "data role" : "role"));
        }
        registerRole(p);
        return p;
    }

    /** add parent for the input role */
    public void addRoleParent(final Role role, final Role parent) {
        if (role.isDataRole() != parent.isDataRole()) {
            throw new ReasonerInternalException(
                    "Mixed object and data roles in role subsumption axiom");
        }
        role.addParent(parent);
        role.getInverse().addParent(parent.getInverse());
    }

    /** add synonym to existing role */
    public void addRoleSynonym(final Role role, final Role syn) {
        if (!role.equals(syn)) {
            addRoleParent(role, syn);
            addRoleParent(syn, role);
        }
    }

    /** a pair of disjoint roles */
    public void addDisjointRoles(final Role R, final Role S) {
        // object- and data roles are always disjoint
        if (R.isDataRole() != S.isDataRole()) {
            return;
        }
        disjointRolesA.add(R);
        disjointRolesB.add(S);
    }

    /** change the undefined names usage policy */
    public void setUndefinedNames(final boolean val) {
        useUndefinedNames = val;
    }

    public List<Role> getRoles() {
        return roles.subList(firstRoleIndex, roles.size());
    }

    /** get access to the taxonomy */
    public Taxonomy getTaxonomy() {
        return pTax;
    }

    public void print(final LogAdapter o, final String type) {
        if (size() == 0) {
            return;
        }
        o.print(type, " Roles (", size(), "):\n");
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            p.print(o);
        }
    }

    public boolean hasReflexiveRoles() {
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (p.isReflexive()) {
                return true;
            }
        }
        return false;
    }

    public void fillReflexiveRoles(final List<Role> RR) {
        RR.clear();
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isSynonym() && p.isReflexive()) {
                RR.add(p);
            }
        }
    }

    public void addRoleParent(final DLTree tree, final Role parent) {
        if (tree == null) {
            return;
        }
        if (tree.token() == RCOMPOSITION) {
            parent.addComposition(tree);
            DLTree inv = DLTreeFactory.inverseComposition(tree);
            parent.inverse().addComposition(inv);
        } else if (tree.token() == PROJINTO) {
            Role R = Role.resolveRole(tree.getLeft());
            if (R.isDataRole()) {
                throw new ReasonerInternalException(
                        "Projection into not implemented for the data role");
            }
            DLTree C = tree.getRight().copy();
            DLTree InvP = DLTreeFactory.buildTree(new Lexeme(RNAME, parent.inverse()));
            DLTree InvR = DLTreeFactory.buildTree(new Lexeme(RNAME, R.inverse()));
            // C = PROJINTO(PARENT-,C)
            C = DLTreeFactory.buildTree(new Lexeme(PROJINTO), InvP, C);
            // C = PROJFROM(R-,PROJINTO(PARENT-,C))
            C = DLTreeFactory.buildTree(new Lexeme(PROJFROM), InvR, C);
            R.setRange(C);
        } else if (tree.token() == PROJFROM) {
            Role R = Role.resolveRole(tree.getLeft());
            DLTree C = tree.getRight().copy();
            DLTree P = DLTreeFactory.buildTree(new Lexeme(RNAME, parent));
            // C = PROJINTO(PARENT,C)
            C = DLTreeFactory.buildTree(new Lexeme(PROJINTO), P, C);
            // C = PROJFROM(R,PROJINTO(PARENT,C))
            C = DLTreeFactory.buildTree(new Lexeme(PROJFROM), tree.getLeft().copy(), C);
            R.setDomain(C);
        } else {
            addRoleParent(Role.resolveRole(tree), parent);
        }
    }

    public void initAncDesc() {
        int nRoles = roles.size();
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            p.eliminateToldCycles();
        }
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (p.isSynonym()) {
                p.canonicaliseSynonym();
                p.addFeaturesToSynonym();
            }
        }
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isSynonym()) {
                p.removeSynonymsFromParents();
            }
        }
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isSynonym() && !p.hasToldSubsumers()) {
                p.addParent(universalRole);
            }
        }
        pTax.setCompletelyDefined(true);
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isClassified()) {
                pTax.classifyEntry(p);
            }
        }
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isSynonym()) {
                p.initADbyTaxonomy(pTax, nRoles);
            }
        }
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isSynonym()) {
                p.completeAutomaton(nRoles);
            }
        }
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
            for (int i = firstRoleIndex; i < roles.size(); i++) {
                Role p = roles.get(i);
                if (!p.isSynonym() && p.isDisjoint()) {
                    p.checkHierarchicalDisjoint();
                }
            }
        }
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isSynonym()) {
                p.postProcess();
            }
        }
        for (int i = firstRoleIndex; i < roles.size(); i++) {
            Role p = roles.get(i);
            if (!p.isSynonym()) {
                p.consistent();
            }
        }
    }

    /// @return pointer to a TOP role
    Role getTopRole() {
        return universalRole;
    }

    /// @return pointer to a BOTTOM role
    Role getBotRole() {
        return emptyRole;
    }
}
