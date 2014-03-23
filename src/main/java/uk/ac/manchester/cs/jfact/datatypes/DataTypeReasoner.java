package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeClashes.DT_TT;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.Reference;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import conformance.Original;
import conformance.PortedFrom;

/** @author ignazio Datatype reasoner implementation */
@PortedFrom(file = "DataReasoning.h", name = "DataTypeReasoner")
public final class DataTypeReasoner implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** map Type.pName.Type appearance */
    @PortedFrom(file = "DataReasoning.h", name = "Map")
    private final Map<Datatype<?>, DataTypeSituation<?>> map = new HashMap<Datatype<?>, DataTypeSituation<?>>();
    /** dep-set for the clash for *all* the types */
    @PortedFrom(file = "DataReasoning.h", name = "clashDep")
    private final Reference<DepSet> clashDep = new Reference<DepSet>();
    @Original
    private final JFactReasonerConfiguration options;

    /**
     * set clash dep-set to DEP, report with given REASON
     * 
     * @param dep
     *        dep
     * @param reason
     *        reason
     */
    @PortedFrom(file = "DataReasoning.h", name = "reportClash")
    public void reportClash(DepSet dep, DatatypeClashes reason) {
        // inform about clash...
        options.getLog().print(reason);
        clashDep.setReference(dep);
    }

    /**
     * set clash dep-set to DEP, report with given REASON
     * 
     * @param dep1
     *        dep1
     * @param dep2
     *        dep2
     * @param reason
     *        reason
     */
    @PortedFrom(file = "DataReasoning.h", name = "reportClash")
    public void reportClash(DepSet dep1, DepSet dep2, DatatypeClashes reason) {
        reportClash(DepSet.plus(dep1, dep2), reason);
    }

    /**
     * @param config
     *        config
     */
    public DataTypeReasoner(JFactReasonerConfiguration config) {
        options = config;
    }

    // managing DTR
    /**
     * add data type to the reasoner
     * 
     * @param datatype
     *        datatype
     * @return datatype situation
     */
    @SuppressWarnings("unchecked")
    @PortedFrom(file = "DataReasoning.h", name = "registerDataType")
    private <R extends Comparable<R>> DataTypeSituation<R> getType(
            Datatype<R> datatype) {
        if (map.containsKey(datatype)) {
            return (DataTypeSituation<R>) map.get(datatype);
        }
        DataTypeSituation<R> dataTypeAppearance = new DataTypeSituation<R>(
                datatype, this);
        map.put(datatype, dataTypeAppearance);
        return dataTypeAppearance;
    }

    /**
     * get clash-set
     * 
     * @return clash set
     */
    @PortedFrom(file = "DataReasoning.h", name = "getClashSet")
    public DepSet getClashSet() {
        return clashDep.getReference();
    }

    /**
     * @param positive
     *        positive
     * @param type
     *        type
     * @param entry
     *        entry
     * @param dep
     *        dep
     * @return false if datatype, true otherwise
     */
    @PortedFrom(file = "DataReasoning.h", name = "addDataEntry")
    public boolean addDataEntry(boolean positive, DagTag type,
            NamedEntry entry, DepSet dep) {
        switch (type) {
            case dtDataType:
                return dataType(positive,
                        ((DatatypeEntry) entry).getDatatype(), dep);
            case dtDataValue:
                return this.dataValue(positive,
                        ((LiteralEntry) entry).getLiteral(), dep);
            case dtDataExpr:
                return this.dataExpression(positive, ((DatatypeEntry) entry)
                        .getDatatype().asExpression(), dep);
            case dtAnd:
                return false;
            default:
                throw new UnreachableSituationException(type.toString());
        }
    }

    @Original
    private <R extends Comparable<R>> boolean dataExpression(boolean positive,
            DatatypeExpression<R> c, DepSet dep) {
        Datatype<R> typeToIndex = c.getHostType();
        if (c instanceof DatatypeEnumeration) {
            typeToIndex = c;
        }
        if (positive) {
            this.getType(typeToIndex).setPType(dep);
        }
        options.getLog().printTemplate(Templates.INTERVAL,
                positive ? "+" : "-", c, "", "", "");
        return this.getType(typeToIndex).addInterval(positive, c, dep);
    }

    @Original
    private <R extends Comparable<R>> boolean dataType(boolean positive,
            Datatype<R> c, DepSet dep) {
        if (options.isLoggingActive()) {
            options.getLog().printTemplate(Templates.INTERVAL,
                    positive ? "+" : "-", c, "", "", "");
        }
        if (positive) {
            this.getType(c).setPType(dep);
        } else {
            this.getType(c).setNType(dep);
        }
        return false;// this.getType(c).addInterval(positive, c, dep);
    }

    @PortedFrom(file = "DataReasoning.h", name = "dataValue")
    private <R extends Comparable<R>> boolean dataValue(boolean positive,
            Literal<R> c1, DepSet dep) {
        Datatype<R> d = c1.getDatatypeExpression();
        DatatypeExpression<R> interval = d.isNumericDatatype() ? new DatatypeNumericEnumeration<R>(
                d.asNumericDatatype(), c1) : new DatatypeEnumeration<R>(d, c1);
        options.getLog().printTemplate(Templates.INTERVAL,
                positive ? "+" : "-", interval, "", "", "");
        return dataExpression(positive, interval, dep);
    }

    // try to find contradiction:
    // -- if we have 2 same elements or direct contradiction (like "p" and
    // "(not p)")
    // then addConcept() will eliminate this;
    // => negations are not interesting also (p & ~p are eliminated; ~p means
    // "all except p").
    // -- all cases with 2 different values of the same class are found in
    // previous search;
    // -- The remaining problems are
    // - check if there are 2 different positive classes
    // - check if some value is present together with negation of its class
    // - check if some value is present together with the other class
    // - check if two values of different classes are present at the same time
    /** @return true if clash */
    @PortedFrom(file = "DataReasoning.h", name = "checkClash")
    public boolean checkClash() {
        int size = map.size();
        if (size == 0) {
            // empty, nothing to do
            return false;
        }
        if (size == 1) {
            // only one, positive or negative - just check it
            return map.values().iterator().next().checkPNTypeClash();
        }
        // check if any value is already clashing with itself
        List<DataTypeSituation<?>> types = new ArrayList<DataTypeSituation<?>>(
                map.values());
        if (findClash(types, size)) {
            return true;
        }
        // for every two datatypes, they must either be disjoint and
        // opposite, or one subdatatype of the other
        // if a subtype b, then b and not a, otherwise clash
        // a subtype b => b compatible a (all a are b) but not a compatible
        // b (some b might not be a)
        for (int i = 0; i < size; i++) {
            DataTypeSituation<?> ds1 = types.get(i);
            for (int j = i + 1; j < size; j++) {
                DataTypeSituation<?> ds2 = types.get(j);
                boolean p1 = ds1.hasPType();
                boolean n2 = ds2.hasNType();
                boolean p2 = ds2.hasPType();
                boolean n1 = ds1.hasNType();
                Datatype<?> t1 = ds1.getType();
                Datatype<?> t2 = ds2.getType();
                boolean t1subTypet2 = t1.isSubType(t2);
                boolean t2subTypet1 = t2.isSubType(t1);
                if (findClash(p1, n2, p2, n1, t1subTypet2, t2subTypet1,
                        ds1.getPType(), ds2.getNType())) {
                    return true;
                }
                // what if the supertype is an enum?
                // check that values in the supertype are acceptable for
                // the subtype
                boolean not21 = !ds2.checkCompatibleValue(ds1);
                boolean not12 = !ds1.checkCompatibleValue(ds2);
                if (t1subTypet2) {
                    if (findClash(t1subTypet2, ds1.getPType(), ds2.getNType(),
                            not21)) {
                        return true;
                    }
                } else if (t2subTypet1) {
                    if (findClash(t2subTypet1, ds1.getPType(), ds2.getNType(),
                            not12)) {
                        return true;
                    }
                }
                if (findClash(p1, p2, t1, t2, ds1.getPType(), ds2.getPType(),
                        not21, not12)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param p1
     *        p1
     * @param p2
     *        p2
     * @param t1
     *        t1
     * @param t2
     *        t2
     * @param pType1
     *        pType1
     * @param pType2
     *        pType2
     * @param not12
     *        not12
     * @param not21
     *        not21
     * @return true if clash
     */
    private boolean findClash(boolean p1, boolean p2, Datatype<?> t1,
            Datatype<?> t2, DepSet pType1, DepSet pType2, boolean not12,
            boolean not21) {
        // they're disjoint: they can't be both positive (but can be
        // both negative)
        if (p1 && p2) {
            // special case: disjoint datatypes with overlapping
            // value spaces, e.g., nonneginteger, and nonposinteger
            // and value = 0
            if (not12 || not21) {
                reportClash(pType1, pType2, DT_TT);
                return true;
            } else {
                // in this case, disjoint datatypes which have some
                // common values do not rise a clash, but they are
                // determining a new interval that should be added
                // to the reasoner.
                // XXX need to design a proper general solution
                if (t1.equals(DatatypeFactory.NONNEGATIVEINTEGER)
                        && t2.equals(DatatypeFactory.NONPOSITIVEINTEGER)
                        || t2.equals(DatatypeFactory.NONNEGATIVEINTEGER)
                        && t1.equals(DatatypeFactory.NONPOSITIVEINTEGER)) {
                    map.remove(t1);
                    map.remove(t2);
                    DatatypeEnumeration<BigInteger> enumeration = new DatatypeEnumeration<BigInteger>(
                            DatatypeFactory.INTEGER,
                            Collections.singletonList(DatatypeFactory.INTEGER
                                    .buildLiteral("0")));
                    this.dataExpression(true, enumeration,
                            DepSet.plus(pType1, pType2));
                    return checkClash();
                }
            }
        }
        return false;
    }

    /**
     * @param p1
     *        p1
     * @param n2
     *        n2
     * @param p2
     *        p2
     * @param n1
     *        n1
     * @param t1subTypet2
     *        t1subTypet2
     * @param t2subTypet1
     *        t2subTypet1
     * @param pType1
     *        pType1
     * @param nType2
     *        nType2
     * @return true if clash
     */
    private boolean findClash(boolean p1, boolean n2, boolean p2, boolean n1,
            boolean t1subTypet2, boolean t2subTypet1, DepSet pType1,
            DepSet nType2) {
        if (t1subTypet2 && p1 && n2 || t2subTypet1 && p2 && n1) {
            // cannot be NOT supertype AND subtype
            reportClash(pType1, nType2, DT_TT);
            return true;
        }
        return false;
    }

    /**
     * @param t2subTypet1
     *        t2subTypet1
     * @param pType1
     *        pType1
     * @param nType2
     *        nType2
     * @param not12
     *        not12
     * @return true if clash happens
     */
    private boolean findClash(boolean t2subTypet1, DepSet pType1,
            DepSet nType2, boolean not12) {
        // check that values in the supertype are acceptable for
        // the subtype
        if (t2subTypet1 && not12) {
            reportClash(pType1, nType2, DT_TT);
            return true;
        }
        return false;
    }

    /**
     * @param types
     *        types
     * @param size
     *        size
     * @return true if a clash is found
     */
    private boolean findClash(List<DataTypeSituation<?>> types, int size) {
        for (int i = 0; i < size; i++) {
            if (types.get(i).checkPNTypeClash()) {
                reportClash(types.get(i).getPType(), types.get(i).getNType(),
                        DT_TT);
                return true;
            }
        }
        return false;
    }
}
