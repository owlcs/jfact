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
    private BitSet flags = new BitSet();

    /** set any flag */
    private void setX(int val) {
        flags.set(val);
    }

    /** get value of any flag */
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
    public void or(LogicFeatures lf) {
        flags.or(lf.flags);
    }

    public boolean hasInverseRole() {
        return getX(lfBothRoles);
    }

    private boolean hasRoleHierarchy() {
        return getX(lfRolesSubsumption);
    }

    private boolean hasTransitiveRole() {
        return getX(lfTransitiveRoles);
    }

    public boolean hasSomeAll() {
        return getX(lfSomeConstructor);
    }

    public boolean hasFunctionalRestriction() {
        return getX(lfFConstructor) || getX(lfFunctionalRoles);
    }

    public boolean hasNumberRestriction() {
        return getX(lfNConstructor);
    }

    public boolean hasQNumberRestriction() {
        return getX(lfQConstructor);
    }

    public boolean hasSingletons() {
        return getX(lfSingleton);
    }

    public boolean hasSelfRef() {
        return getX(lfSelfRef);
    }

    public boolean hasTopRole() {
        return getX(lfTopRole);
    }

    // overall state
    /** check whether no flags are set */
    public boolean isEmpty() {
        return flags.isEmpty();
    }

    /** build bothRoles from single Roles flags */
    public void mergeRoles() {
        if (getX(lfDirectRoles) && getX(lfInverseRoles)) {
            setX(lfBothRoles);
        }
    }

    /** allow user to set presence of inverse roles */
    public void setInverseRoles() {
        setX(lfBothRoles);
    }

    public static LogicFeatures plus(LogicFeatures f1, LogicFeatures f2) {
        LogicFeatures f = new LogicFeatures(f1);
        f.flags.or(f2.flags);
        return f;
    }

    public void fillConceptData(Concept p) {
        if (p.isSingleton()) {
            setX(lfSingleton);
        }
    }

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

    // private static int lfInvalid = 0;
    // role description
    private static int lfTransitiveRoles = 1;
    private static int lfRolesSubsumption = 2;
    private static int lfDirectRoles = 3;
    private static int lfInverseRoles = 4;
    private static int lfRangeAndDomain = 5;
    private static int lfFunctionalRoles = 6;
    // concept description
    private static int lfSomeConstructor = 7;
    private static int lfFConstructor = 8;
    private static int lfNConstructor = 9;
    private static int lfQConstructor = 10;
    private static int lfSingleton = 11;
    // global description
    private static int lfGeneralAxioms = 12;
    private static int lfBothRoles = 13;
    // new constructions
    private static int lfSelfRef = 14;
    private static int lfTopRole = 15;
    // private int value;
    //
    // LFEnum(int v) {
    // value = v;
    // }
    //
    // protected int getValue() {
    // return value;
    // }
    // }
}
