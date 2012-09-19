package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
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

public final class DataTypeReasoner {
    /** map Type.pName.Type appearance */
    private final Map<Datatype<?>, DataTypeSituation<?>> map = new HashMap<Datatype<?>, DataTypeSituation<?>>();
    /** dep-set for the clash for *all* the types */
    private final Reference<DepSet> clashDep = new Reference<DepSet>();
    private final JFactReasonerConfiguration options;

    /** set clash dep-set to DEP, report with given REASON; @return true to
     * simplify callers */
    void reportClash(DepSet dep, String reason) {
        options.getLog().printTemplate(Templates.CLASH, reason); // inform about
                                                                 // clash...
        clashDep.setReference(dep);
    }

    public DataTypeReasoner(JFactReasonerConfiguration o) {
        options = o;
    }

    // managing DTR
    /** add data type to the reasoner */
    private <R extends Comparable<R>> DataTypeSituation<R> getType(Datatype<R> p) {
        if (map.containsKey(p)) {
            return (DataTypeSituation<R>) map.get(p);
        }
        DataTypeSituation<R> dataTypeAppearance = new DataTypeSituation<R>(p, this);
        map.put(p, dataTypeAppearance);
        return dataTypeAppearance;
    }

    private <R extends Comparable<R>> DataTypeSituation<R>
            getType(DatatypeExpression<R> p) {
        return this.getType(p.getHostType());
    }

    /** get clash-set */
    public DepSet getClashSet() {
        return clashDep.getReference();
    }

    public boolean addDataEntry(boolean positive, DagTag type, NamedEntry entry,
            DepSet dep) {
        switch (type) {
            case dtDataType: {
                Datatype<?> t = ((DatatypeEntry) entry).getDatatype();
                if (options.isLoggingActive()) {
                    options.getLog().printTemplate(Templates.INTERVAL,
                            positive ? "+" : "-", entry.getName(), "", "", "");
                }
                if (positive) {
                    this.getType(t).setPType(dep);
                } else {
                    this.getType(t).setNType(dep);
                }
                return false;
            }
            case dtDataValue:
                return this.dataValue(positive, ((LiteralEntry) entry).getLiteral(), dep);
            case dtDataExpr:
                return this.dataExpression(positive, ((DatatypeEntry) entry)
                        .getDatatype().asExpression(), dep);
            case dtAnd:
                return false;
            default:
                throw new UnreachableSituationException(type.toString());
        }
    }

    private <R extends Comparable<R>> boolean dataExpression(boolean positive,
            DatatypeExpression<R> c, DepSet dep) {
        if (c.getKnownFacetValues().isEmpty()) {
            return false;
        }
        if (positive) {
            this.getType(c.getHostType()).setPType(dep);
        }
        options.getLog().printTemplate(Templates.INTERVAL, positive ? "+" : "-", c, "",
                "", "");
        return this.getType(c).addInterval(positive, c, dep);
    }

    private <R extends Comparable<R>> boolean dataValue(boolean positive, Literal<R> c1,
            DepSet dep) {
        Datatype<R> d = c1.getDatatypeExpression();
        if (positive) {
            this.getType(d).setPType(dep);
        } else {
            this.getType(d).setNType(dep);
        }
        Datatype<R> interval = d.isNumericDatatype() ? new DatatypeNumericEnumeration<R>(
                d.asNumericDatatype(), c1) : new DatatypeEnumeration<R>(d, c1);
        options.getLog().printTemplate(Templates.INTERVAL, positive ? "+" : "-",
                interval, "", "", "");
        return this.getType(d).addInterval(positive, interval, dep);
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
    public boolean checkClash() {
        List<DataTypeSituation<?>> types = new ArrayList<DataTypeSituation<?>>(
                map.values());
        int size = types.size();
        if (size == 0) {
            // empty, nothing to do
            return false;
        }
        if (size == 1) {
            // only one, positive or negative - just check it
            return types.get(0).checkPNTypeClash();
        }
        if (size > 1) {
            // check if any value is already clashing with itself
            for (int i = 0; i < size; i++) {
                if (types.get(i).checkPNTypeClash()) {
                    options.getLog().print(" DT-TT");
                    clashDep.setReference(types.get(i).getPType());
                    return true;
                }
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
                    if (ds1.getType().isSubType(ds2.getType()) && ds1.hasPType()
                            && ds2.hasNType() || ds2.getType().isSubType(ds1.getType())
                            && ds2.hasPType() && ds1.hasNType()) {
                        // cannot be NOT supertype AND subtype
                        options.getLog().print(" DT-TT");
                        DepSet plus = DepSet.plus(ds1.getPType(), ds2.getNType());
                        clashDep.setReference(plus);
                        return true;
                    }
                    // they're disjoint: they can't be both positive (but can be
                    // both negative)
                    if (ds1.hasPType() && ds2.hasPType()) {
                        // special case: disjoint datatypes with overlapping
                        // value spaces, e.g., nonneginteger, and nonposinteger
                        // and value = 0
                        if (!ds1.checkCompatibleValue(ds2)) {
                            options.getLog().print(" DT-TT");
                            clashDep.setReference(DepSet.plus(ds1.getPType(),
                                    ds2.getPType()));
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        // this will never be reached because the previous ifs are a partition
        // of the possible
        // sizes for types, but the compiler is not smart enough to see this
        return false;
    }
}
