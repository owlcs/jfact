package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.BP_TOP;

import java.io.Serializable;
import java.util.BitSet;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

/** logic features */
@PortedFrom(file = "LogicFeature.h", name = "LogicFeatures")
public class LogicFeatures implements Serializable {

    /** all flags in one long */
    @PortedFrom(file = "LogicFeature.h", name = "flags") private final BitSet flags = new BitSet();
    //@formatter:off
    // role description
    @PortedFrom(file = "LogicFeature.h", name = "lfTransitiveRoles")    private static final int TRANSITIVEROLES  = 1;
    @PortedFrom(file = "LogicFeature.h", name = "lfRolesSubsumption")   private static final int ROLESSUBSUMPTION = 2;
    @PortedFrom(file = "LogicFeature.h", name = "lfDirectRoles")        private static final int DIRECTROLES      = 3;
    @PortedFrom(file = "LogicFeature.h", name = "lfInverseRoles")       private static final int INVERSEROLES     = 4;
    @PortedFrom(file = "LogicFeature.h", name = "lfRangeAndDomain")     private static final int RANGEANDDOMAIN   = 5;
    @PortedFrom(file = "LogicFeature.h", name = "lfFunctionalRoles")    private static final int FUNCTIONALROLES  = 6;
    // concept description
    @PortedFrom(file = "LogicFeature.h", name = "lfSomeConstructor")    private static final int SOMECONSTRUCTOR  = 7;
    @PortedFrom(file = "LogicFeature.h", name = "lfFConstructor")       private static final int FCONSTRUCTOR     = 8;
    @PortedFrom(file = "LogicFeature.h", name = "lfNConstructor")       private static final int NCONSTRUCTOR     = 9;
    @PortedFrom(file = "LogicFeature.h", name = "lfQConstructor")       private static final int QCONSTRUCTOR     = 10;
    @PortedFrom(file = "LogicFeature.h", name = "lfSingleton")          private static final int SINGLETON        = 11;
    // global description
    @PortedFrom(file = "LogicFeature.h", name = "lfGeneralAxioms")      private static final int GENERALAXIOMS    = 12;
    @PortedFrom(file = "LogicFeature.h", name = "lfBothRoles")          private static final int BOTHROLES        = 13;
    // new constructions
    @PortedFrom(file = "LogicFeature.h", name = "lfSelfRef")            private static final int SELFREF          = 14;
    @PortedFrom(file = "LogicFeature.h", name = "lfTopRole")            private static final int TOPROLE          = 15;
    //@formatter:on
    /** default c'tor */
    public LogicFeatures() {
        super();
    }

    /**
     * copy c'tor
     * 
     * @param lf
     *        lf
     */
    public LogicFeatures(LogicFeatures lf) {
        flags.or(lf.flags);
    }

    /**
     * set any flag
     * 
     * @param val
     *        val
     */
    @PortedFrom(file = "LogicFeature.h", name = "setX")
    private void setX(int val) {
        flags.set(val);
    }

    /**
     * @param val
     *        val
     * @return value of any flag
     */
    @PortedFrom(file = "LogicFeature.h", name = "getX")
    private boolean getX(int val) {
        return flags.get(val);
    }

    /**
     * operator add
     * 
     * @param lf
     *        lf
     */
    @PortedFrom(file = "LogicFeature.h", name = "or")
    public void or(LogicFeatures lf) {
        flags.or(lf.flags);
    }

    /** @return true if there are inverse roles */
    @PortedFrom(file = "LogicFeature.h", name = "hasInverseRole")
    public boolean hasInverseRole() {
        return getX(BOTHROLES);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasRoleHierarchy")
    private boolean hasRoleHierarchy() {
        return getX(ROLESSUBSUMPTION);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasTransitiveRole")
    private boolean hasTransitiveRole() {
        return getX(TRANSITIVEROLES);
    }

    /** @return true if some or all restrictions are present */
    @PortedFrom(file = "LogicFeature.h", name = "hasSomeAll")
    public boolean hasSomeAll() {
        return getX(SOMECONSTRUCTOR);
    }

    /** @return true if there are functional restrictions */
    @PortedFrom(file = "LogicFeature.h", name = "hasFunctionalRestriction")
    public boolean hasFunctionalRestriction() {
        return getX(FCONSTRUCTOR) || getX(FUNCTIONALROLES);
    }

    /** @return true if there are number restrictions */
    @PortedFrom(file = "LogicFeature.h", name = "hasNumberRestriction")
    public boolean hasNumberRestriction() {
        return getX(NCONSTRUCTOR);
    }

    /** @return true if there are qualified number restrictions */
    @PortedFrom(file = "LogicFeature.h", name = "hasQNumberRestriction")
    public boolean hasQNumberRestriction() {
        return getX(QCONSTRUCTOR);
    }

    /** @return true if there are singletons */
    @PortedFrom(file = "LogicFeature.h", name = "hasSingletons")
    public boolean hasSingletons() {
        return getX(SINGLETON);
    }

    /** @return true if there are self references */
    @PortedFrom(file = "LogicFeature.h", name = "hasSelfRef")
    public boolean hasSelfRef() {
        return getX(SELFREF);
    }

    /** @return true if there is a top role */
    @PortedFrom(file = "LogicFeature.h", name = "hasTopRole")
    public boolean hasTopRole() {
        return getX(TOPROLE);
    }

    // overall state
    /** @return whether no flags are set */
    @PortedFrom(file = "LogicFeature.h", name = "empty")
    public boolean isEmpty() {
        return flags.isEmpty();
    }

    /** build bothRoles from single Roles flags */
    @PortedFrom(file = "LogicFeature.h", name = "mergeRoles")
    public void mergeRoles() {
        if (getX(DIRECTROLES) && getX(INVERSEROLES)) {
            setX(BOTHROLES);
        }
    }

    /** allow user to set presence of inverse roles */
    @PortedFrom(file = "LogicFeature.h", name = "setInverseRoles")
    public void setInverseRoles() {
        setX(BOTHROLES);
    }

    /**
     * @param f1
     *        f1
     * @param f2
     *        f2
     * @return combination of the two objects
     */
    @PortedFrom(file = "LogicFeature.h", name = "+")
    public static LogicFeatures plus(LogicFeatures f1, LogicFeatures f2) {
        LogicFeatures f = new LogicFeatures(f1);
        f.flags.or(f2.flags);
        return f;
    }

    /**
     * @param p
     *        p
     */
    @PortedFrom(file = "LogicFeature.h", name = "fillConceptData")
    public void fillConceptData(Concept p) {
        if (p.isSingleton()) {
            setX(SINGLETON);
        }
    }

    /**
     * @param p
     *        p
     * @param both
     *        both
     */
    @PortedFrom(file = "LogicFeature.h", name = "fillRoleData")
    public void fillRoleData(Role p, boolean both) {
        if (p.isTop()) {
            if (!p.isDataRole()) {
                setX(TOPROLE);
            }
            return;
        }
        if (p.getId() > 0) {
            setX(DIRECTROLES);
        } else {
            setX(INVERSEROLES);
        }
        if (both) {
            setX(BOTHROLES);
        }
        if (p.isTransitive()) {
            setX(TRANSITIVEROLES);
        }
        if (p.hasToldSubsumers()) {
            setX(ROLESSUBSUMPTION);
        }
        if (p.isFunctional()) {
            setX(FUNCTIONALROLES);
        }
        if (p.getBPDomain() != BP_TOP || p.getBPRange() != BP_TOP) {
            setX(RANGEANDDOMAIN);
        }
    }

    /**
     * @param v
     *        v
     * @param pos
     *        pos
     */
    @PortedFrom(file = "LogicFeature.h", name = "fillDAGData")
    @SuppressWarnings("incomplete-switch")
    public void fillDAGData(DLVertex v, boolean pos) {
        switch (v.getType()) {
            case FORALL:
                setX(SOMECONSTRUCTOR);
                break;
            case LE:
                setX(NCONSTRUCTOR);
                if (v.getConceptIndex() != BP_TOP) {
                    setX(QCONSTRUCTOR);
                }
                break;
            case PSINGLETON:
            case NSINGLETON:
                setX(SINGLETON);
                break;
            case IRR:
                setX(SELFREF);
                break;
            default:
                break;
        }
    }

    /**
     * @param l
     *        l
     */
    @PortedFrom(file = "LogicFeature.h", name = "writeState")
    public void writeState(LogAdapter l) {
        String no = "NO ";
        String q = "qualified ";
        l.printTemplate(Templates.WRITE_STATE, hasInverseRole() ? "" : no, hasRoleHierarchy() ? "" : no,
            hasTransitiveRole() ? "" : no, hasTopRole() ? "" : no, hasSomeAll() ? "" : no,
            hasFunctionalRestriction() ? "" : no, hasNumberRestriction() ? hasQNumberRestriction() ? q : "" : no,
            hasSingletons() ? "" : no);
    }
}
