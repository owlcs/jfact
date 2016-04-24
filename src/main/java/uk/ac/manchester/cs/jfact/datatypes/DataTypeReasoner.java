package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeClashes.DT_TT;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.Nullable;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.Templates;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** @author ignazio Datatype reasoner implementation */
@PortedFrom(file = "DataReasoning.h", name = "DataTypeReasoner")
public final class DataTypeReasoner implements Serializable {

    /** map Type.pName.Type appearance */
    @PortedFrom(file = "DataReasoning.h", name = "Map") private final Map<Datatype<?>, DataTypeSituation<?>> map = new HashMap<>();
    /** dep-set for the clash for *all* the types */
    @PortedFrom(file = "DataReasoning.h", name = "clashDep") private Optional<DepSet> clashDep = Optional.empty();
    @Original private final JFactReasonerConfiguration options;

    /**
     * @param config
     *        config
     */
    public DataTypeReasoner(JFactReasonerConfiguration config) {
        options = config;
    }

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
        clashDep = Optional.ofNullable(dep);
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
    private <R extends Comparable<R>> DataTypeSituation<R> getType(Datatype<R> datatype) {
        DataTypeSituation<R> appearance = (DataTypeSituation<R>) map.get(datatype);
        if (appearance != null) {
            return appearance;
        }
        appearance = new DataTypeSituation<>(datatype, this);
        map.put(datatype, appearance);
        return appearance;
    }

    /**
     * get clash-set
     * 
     * @return clash set
     */
    @Nullable
    @PortedFrom(file = "DataReasoning.h", name = "getClashSet")
    public DepSet getClashSet() {
        return clashDep.orElse(null);
    }

    /**
     * @param positive
     *        positive
     * @param dagtag
     *        type
     * @param entry
     *        entry
     * @param dep
     *        dep
     * @return false if datatype, true otherwise
     */
    @PortedFrom(file = "DataReasoning.h", name = "addDataEntry")
    public boolean addDataEntry(boolean positive, DagTag dagtag, NamedEntry entry, DepSet dep) {
        switch (dagtag) {
            case DATATYPE:
                return dataType(positive, ((DatatypeEntry) entry).getDatatype(), dep);
            case DATAVALUE:
                return dataValue(positive, ((LiteralEntry) entry).getLiteral(), dep);
            case DATAEXPR:
                return dataExpression(positive, ((DatatypeEntry) entry).getDatatype().asExpression(), dep);
            case AND:
                return false;
            case BAD:
            case CHOOSE:
            case COLLECTION:
            case FORALL:
            case IRR:
            case LE:
            case NCONCEPT:
            case NN:
            case NSINGLETON:
            case PCONCEPT:
            case PROJ:
            case PSINGLETON:
            case TOP:
            default:
                throw new UnreachableSituationException(dagtag.toString());
        }
    }

    @Original
    private <R extends Comparable<R>> boolean dataExpression(boolean positive, DatatypeExpression<R> c, DepSet dep) {
        Datatype<R> typeToIndex = c.getHostType();
        if (c instanceof DatatypeEnumeration) {
            typeToIndex = c;
        }
        DataTypeSituation<R> type = this.getType(typeToIndex);
        if (positive) {
            type.setPType(dep);
        } else {
            type.setNType(dep);
        }
        if (options.isLoggingActive()) {
            options.getLog().printTemplate(Templates.INTERVAL, positive ? "+" : "-", c, "", "", "");
        }
        return type.addInterval(c, dep);
    }

    @Original
    private <R extends Comparable<R>> boolean dataType(boolean positive, Datatype<R> c, DepSet dep) {
        if (options.isLoggingActive()) {
            options.getLog().printTemplate(Templates.INTERVAL, positive ? "+" : "-", c, "", "", "");
        }
        if (positive) {
            this.getType(c).setPType(dep);
        } else {
            this.getType(c).setNType(dep);
        }
        return false;
    }

    @PortedFrom(file = "DataReasoning.h", name = "dataValue")
    private <R extends Comparable<R>> boolean dataValue(boolean positive, Literal<R> c1, DepSet dep) {
        Datatype<R> d = c1.getDatatypeExpression();
        DatatypeExpression<R> interval = d.isNumericDatatype() ? new DatatypeNumericEnumeration<>(d.asNumericDatatype(),
            c1) : new DatatypeEnumeration<>(d, c1);
        options.getLog().printTemplate(Templates.INTERVAL, positive ? "+" : "-", interval, "", "", "");
        return dataExpression(positive, interval, dep);
    }

    // try to find contradiction:
    // -- if we have 2 same elements or direct contradiction (like "p" and
    // "(not p)") then addConcept() will eliminate this.
    // => negations are not interesting also (p & ~p are eliminated; ~p means
    // "all except p").
    // -- all cases with 2 different values of the same class are found in
    // previous search.
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
        List<DataTypeSituation<?>> types = new ArrayList<>(map.values());
        if (findClash(types)) {
            return true;
        }
        // for every two datatypes, they must either be disjoint and
        // opposite, or one subdatatype of the other
        // if a subtype b, then (b and a), (b and not a), (not b and not a) are
        // acceptable.
        // otherwise clash: only combination forbidden is (not b and a)
        // a subtype b => b compatible a (all a are b) but not a compatible
        // b (some b might not be a)
        for (int i = 0; i < size; i++) {
            DataTypeSituation<?> a = types.get(i);
            for (int j = i + 1; j < size; j++) {
                DataTypeSituation<?> b = types.get(j);
                boolean p1 = a.hasPType();
                boolean n1 = a.hasNType();
                boolean p2 = b.hasPType();
                boolean n2 = b.hasNType();
                boolean aSubtypeB = a.isSubType(b);
                boolean bSubtypeA = b.isSubType(a);
                if (findClash(p1, n2, p2, n1, aSubtypeB, bSubtypeA, a.getPType(), b.getNType())) {
                    return true;
                }
                // what if the supertype is an enum?
                // check that values in the supertype are acceptable for
                // the subtype
                boolean notBCompatibleA = !b.checkCompatibleValue(a);
                boolean notACompatibleB = !a.checkCompatibleValue(b);
                if (aSubtypeB && findClash(aSubtypeB, a.getPType(), b.getNType(), notBCompatibleA)) {
                    return true;
                } else if (bSubtypeA && findClash(bSubtypeA, a.getPType(), b.getNType(), notACompatibleB)) {
                    return true;
                }
                if (findClash(p1, p2, a.getType(), b.getType(), a.getPType(), b.getPType(), notBCompatibleA,
                    notACompatibleB)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean findClash(boolean p1, boolean p2, Datatype<?> t1, Datatype<?> t2, DepSet pType1, DepSet pType2,
        boolean not12, boolean not21) {
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
                if (t1.equals(DatatypeFactory.NONNEGATIVEINTEGER) && t2.equals(DatatypeFactory.NONPOSITIVEINTEGER) || t2
                    .equals(DatatypeFactory.NONNEGATIVEINTEGER) && t1.equals(DatatypeFactory.NONPOSITIVEINTEGER)) {
                    map.remove(t1);
                    map.remove(t2);
                    DatatypeEnumeration<BigInteger> enumeration = new DatatypeEnumeration<>(DatatypeFactory.INTEGER,
                        Collections.singletonList(DatatypeFactory.INTEGER.buildLiteral("0")));
                    this.dataExpression(true, enumeration, DepSet.plus(pType1, pType2));
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
    private boolean findClash(boolean p1, boolean n2, boolean p2, boolean n1, boolean t1subTypet2, boolean t2subTypet1,
        DepSet pType1, DepSet nType2) {
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
    private boolean findClash(boolean t2subTypet1, DepSet pType1, DepSet nType2, boolean not12) {
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
     * @return true if a clash is found
     */
    private boolean findClash(List<DataTypeSituation<?>> types) {
        Optional<DataTypeSituation<?>> findAny = types.stream().filter(p -> p.checkPNTypeClash()).findAny();
        if (findAny.isPresent()) {
            reportClash(findAny.get().getPType(), findAny.get().getNType(), DT_TT);
            return true;
        }
        return false;
    }
}
