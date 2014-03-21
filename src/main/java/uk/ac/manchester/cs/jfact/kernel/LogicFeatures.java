package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.bpTOP;

import java.io.Serializable;
import java.util.BitSet;

import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import conformance.PortedFrom;

/** logic features */
@PortedFrom(file = "LogicFeature.h", name = "LogicFeatures")
public class LogicFeatures implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** all flags in one long */
    @PortedFrom(file = "LogicFeature.h", name = "flags")
    private final BitSet flags = new BitSet();

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

    /** default c'tor */
    public LogicFeatures() {}

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
        return getX(lfBothRoles);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasRoleHierarchy")
    private boolean hasRoleHierarchy() {
        return getX(lfRolesSubsumption);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasTransitiveRole")
    private boolean hasTransitiveRole() {
        return getX(lfTransitiveRoles);
    }

    /** @return true if some or all restrictions are present */
    @PortedFrom(file = "LogicFeature.h", name = "hasSomeAll")
    public boolean hasSomeAll() {
        return getX(lfSomeConstructor);
    }

    /** @return true if there are functional restrictions */
    @PortedFrom(file = "LogicFeature.h", name = "hasFunctionalRestriction")
    public boolean hasFunctionalRestriction() {
        return getX(lfFConstructor) || getX(lfFunctionalRoles);
    }

    /** @return true if there are number restrictions */
    @PortedFrom(file = "LogicFeature.h", name = "hasNumberRestriction")
    public boolean hasNumberRestriction() {
        return getX(lfNConstructor);
    }

    /** @return true if there are qualified number restrictions */
    @PortedFrom(file = "LogicFeature.h", name = "hasQNumberRestriction")
    public boolean hasQNumberRestriction() {
        return getX(lfQConstructor);
    }

    /** @return true if there are singletons */
    @PortedFrom(file = "LogicFeature.h", name = "hasSingletons")
    public boolean hasSingletons() {
        return getX(lfSingleton);
    }

    /** @return true if there are self references */
    @PortedFrom(file = "LogicFeature.h", name = "hasSelfRef")
    public boolean hasSelfRef() {
        return getX(lfSelfRef);
    }

    /** @return true if there is a top role */
    @PortedFrom(file = "LogicFeature.h", name = "hasTopRole")
    public boolean hasTopRole() {
        return getX(lfTopRole);
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
        if (getX(lfDirectRoles) && getX(lfInverseRoles)) {
            setX(lfBothRoles);
        }
    }

    /** allow user to set presence of inverse roles */
    @PortedFrom(file = "LogicFeature.h", name = "setInverseRoles")
    public void setInverseRoles() {
        setX(lfBothRoles);
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
            setX(lfSingleton);
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
                setX(lfTopRole);
            }
            return;
        }
        if (p.getId() > 0) {
            setX(lfDirectRoles);
        } else {
            setX(lfInverseRoles);
        }
        if (both) {
            setX(lfBothRoles);
        }
        if (p.isTransitive()) {
            setX(lfTransitiveRoles);
        }
        if (p.hasToldSubsumers()) {
            setX(lfRolesSubsumption);
        }
        if (p.isFunctional()) {
            setX(lfFunctionalRoles);
        }
        if (p.getBPDomain() != bpTOP || p.getBPRange() != bpTOP) {
            setX(lfRangeAndDomain);
        }
    }

    /**
     * @param v
     *        v
     * @param pos
     *        pos
     */
    @PortedFrom(file = "LogicFeature.h", name = "fillDAGData")
    public void fillDAGData(DLVertex v, boolean pos) {
        switch (v.getType()) {
            case dtForall:
                setX(lfSomeConstructor);
                break;
            case dtLE:
                setX(lfNConstructor);
                if (v.getConceptIndex() != bpTOP) {
                    setX(lfQConstructor);
                }
                break;
            case dtPSingleton:
            case dtNSingleton:
                setX(lfSingleton);
                break;
            case dtIrr:
                setX(lfSelfRef);
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
        String NO = "NO ";
        String Q = "qualified ";
        l.printTemplate(Templates.WRITE_STATE, hasInverseRole() ? "" : NO,
                hasRoleHierarchy() ? "" : NO, hasTransitiveRole() ? "" : NO,
                hasTopRole() ? "" : NO, hasSomeAll() ? "" : NO,
                hasFunctionalRestriction() ? "" : NO,
                hasNumberRestriction() ? hasQNumberRestriction() ? Q : "" : NO,
                hasSingletons() ? "" : NO);
    }

    // role description
    @PortedFrom(file = "LogicFeature.h", name = "lfTransitiveRoles")
    private static final int lfTransitiveRoles = 1;
    @PortedFrom(file = "LogicFeature.h", name = "lfRolesSubsumption")
    private static final int lfRolesSubsumption = 2;
    @PortedFrom(file = "LogicFeature.h", name = "lfDirectRoles")
    private static final int lfDirectRoles = 3;
    @PortedFrom(file = "LogicFeature.h", name = "lfInverseRoles")
    private static final int lfInverseRoles = 4;
    @PortedFrom(file = "LogicFeature.h", name = "lfRangeAndDomain")
    private static final int lfRangeAndDomain = 5;
    @PortedFrom(file = "LogicFeature.h", name = "lfFunctionalRoles")
    private static final int lfFunctionalRoles = 6;
    // concept description
    @PortedFrom(file = "LogicFeature.h", name = "lfSomeConstructor")
    private static final int lfSomeConstructor = 7;
    @PortedFrom(file = "LogicFeature.h", name = "lfFConstructor")
    private static final int lfFConstructor = 8;
    @PortedFrom(file = "LogicFeature.h", name = "lfNConstructor")
    private static final int lfNConstructor = 9;
    @PortedFrom(file = "LogicFeature.h", name = "lfQConstructor")
    private static final int lfQConstructor = 10;
    @PortedFrom(file = "LogicFeature.h", name = "lfSingleton")
    private static final int lfSingleton = 11;
    // global description
    @PortedFrom(file = "LogicFeature.h", name = "lfGeneralAxioms")
    private static final int lfGeneralAxioms = 12;
    @PortedFrom(file = "LogicFeature.h", name = "lfBothRoles")
    private static final int lfBothRoles = 13;
    // new constructions
    @PortedFrom(file = "LogicFeature.h", name = "lfSelfRef")
    private static final int lfSelfRef = 14;
    @PortedFrom(file = "LogicFeature.h", name = "lfTopRole")
    private static final int lfTopRole = 15;
}
