package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.util.BitSet;

import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import conformance.PortedFrom;

@PortedFrom(file = "LogicFeature.h", name = "LogicFeatures")
public class LogicFeatures {
    /** all flags in one long */
    @PortedFrom(file = "LogicFeature.h", name = "flags")
    private BitSet flags = new BitSet();

    /** set any flag */
    @PortedFrom(file = "LogicFeature.h", name = "setX")
    private void setX(int val) {
        flags.set(val);
    }

    /** get value of any flag */
    @PortedFrom(file = "LogicFeature.h", name = "getX")
    private boolean getX(int val) {
        return flags.get(val);
    }

    /** default c'tor */
    public LogicFeatures() {}

    /** copy c'tor */
    public LogicFeatures(LogicFeatures lf) {
        flags.or(lf.flags);
    }

    /** operator add */
    @PortedFrom(file = "LogicFeature.h", name = "or")
    public void or(LogicFeatures lf) {
        flags.or(lf.flags);
    }

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

    @PortedFrom(file = "LogicFeature.h", name = "hasSomeAll")
    public boolean hasSomeAll() {
        return getX(lfSomeConstructor);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasFunctionalRestriction")
    public boolean hasFunctionalRestriction() {
        return getX(lfFConstructor) || getX(lfFunctionalRoles);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasNumberRestriction")
    public boolean hasNumberRestriction() {
        return getX(lfNConstructor);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasQNumberRestriction")
    public boolean hasQNumberRestriction() {
        return getX(lfQConstructor);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasSingletons")
    public boolean hasSingletons() {
        return getX(lfSingleton);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasSelfRef")
    public boolean hasSelfRef() {
        return getX(lfSelfRef);
    }

    @PortedFrom(file = "LogicFeature.h", name = "hasTopRole")
    public boolean hasTopRole() {
        return getX(lfTopRole);
    }

    // overall state
    /** check whether no flags are set */
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

    @PortedFrom(file = "LogicFeature.h", name = "+")
    public static LogicFeatures plus(LogicFeatures f1, LogicFeatures f2) {
        LogicFeatures f = new LogicFeatures(f1);
        f.flags.or(f2.flags);
        return f;
    }

    @PortedFrom(file = "LogicFeature.h", name = "fillConceptData")
    public void fillConceptData(Concept p) {
        if (p.isSingleton()) {
            setX(lfSingleton);
        }
    }

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
    private static int lfTransitiveRoles = 1;
    @PortedFrom(file = "LogicFeature.h", name = "lfRolesSubsumption")
    private static int lfRolesSubsumption = 2;
    @PortedFrom(file = "LogicFeature.h", name = "lfDirectRoles")
    private static int lfDirectRoles = 3;
    @PortedFrom(file = "LogicFeature.h", name = "lfInverseRoles")
    private static int lfInverseRoles = 4;
    @PortedFrom(file = "LogicFeature.h", name = "lfRangeAndDomain")
    private static int lfRangeAndDomain = 5;
    @PortedFrom(file = "LogicFeature.h", name = "lfFunctionalRoles")
    private static int lfFunctionalRoles = 6;
    // concept description
    @PortedFrom(file = "LogicFeature.h", name = "lfSomeConstructor")
    private static int lfSomeConstructor = 7;
    @PortedFrom(file = "LogicFeature.h", name = "lfFConstructor")
    private static int lfFConstructor = 8;
    @PortedFrom(file = "LogicFeature.h", name = "lfNConstructor")
    private static int lfNConstructor = 9;
    @PortedFrom(file = "LogicFeature.h", name = "lfQConstructor")
    private static int lfQConstructor = 10;
    @PortedFrom(file = "LogicFeature.h", name = "lfSingleton")
    private static int lfSingleton = 11;
    // global description
    @PortedFrom(file = "LogicFeature.h", name = "lfGeneralAxioms")
    private static int lfGeneralAxioms = 12;
    @PortedFrom(file = "LogicFeature.h", name = "lfBothRoles")
    private static int lfBothRoles = 13;
    // new constructions
    @PortedFrom(file = "LogicFeature.h", name = "lfSelfRef")
    private static int lfSelfRef = 14;
    @PortedFrom(file = "LogicFeature.h", name = "lfTopRole")
    private static int lfTopRole = 15;

}
