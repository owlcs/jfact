package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;

/** @author ignazio
 * @param <R> */
public class DataTypeSituation<R extends Comparable<R>> {
    /** positive type appearance */
    private DepSet pType;
    /** negative type appearance */
    private DepSet nType;
    /** interval of possible values */
    private Set<DepInterval<R>> constraints = new HashSet<DepInterval<R>>();
    /** accumulated dep-set */
    private final DepSet accDep = DepSet.create();
    /** dep-set for the clash */
    private final DataTypeReasoner reasoner;
    private final Datatype<R> type;
    private final List<Literal<?>> literals = new ArrayList<Literal<?>>();

    protected DataTypeSituation(Datatype<R> p, DataTypeReasoner dep) {
        if (p == null) {
            throw new IllegalArgumentException("p cannot be null");
        }
        this.type = p;
        this.reasoner = dep;
        this.constraints.add(new DepInterval<R>());
    }

    /** update and add a single interval I to the constraints.
     * 
     * @return true iff clash occurs */
    private boolean addUpdatedInterval(DepInterval<R> i, Datatype<R> interval,
            DepSet localDep) {
        if (!i.consistent(interval)) {
            localDep.add(i.locDep);
            this.reasoner.reportClash(localDep, "C-IT");
            return true;
        }
        if (!i.update(interval, localDep)) {
            this.constraints.add(i);
        }
        if (!this.hasPType()) {
            this.constraints.add(i);
        }
        if (!i.checkMinMaxClash()) {
            this.constraints.add(i);
        } else {
            this.accDep.add(i.locDep);
        }
        return false;
    }

    /** @return type */
    public Datatype<?> getType() {
        return this.type;
    }

    /** add restrictions [POS]INT to intervals
     * 
     * @param pos
     * @param interval
     * @param dep
     * @return true if clash occurs */
    public boolean addInterval(boolean pos, Datatype<R> interval, DepSet dep) {
        if (interval instanceof DatatypeEnumeration) {
            this.literals.addAll(interval.listValues());
        }
        Datatype<R> realInterval = pos ? interval : new DatatypeNegation<R>(interval);
        Set<DepInterval<R>> c = this.constraints;
        this.constraints = new HashSet<DepInterval<R>>();
        if (!c.isEmpty()) {
            for (DepInterval<R> d : c) {
                if (this.addUpdatedInterval(d, realInterval, DepSet.create(dep))) {
                    return true;
                }
            }
        }
        if (this.constraints.isEmpty()) {
            this.reasoner.reportClash(this.accDep, "C-MM");
            return true;
        }
        return false;
    }

    /** @return true iff PType and NType leads to clash */
    public boolean checkPNTypeClash() {
        if (this.hasNType() && this.hasPType()) {
            this.reasoner.reportClash(DepSet.plus(this.pType, this.nType), "TNT");
            return true;
        }
        for (DepInterval<R> d : this.constraints) {
            boolean checkMinMaxClash = d.checkMinMaxClash();
            if (checkMinMaxClash) {
                this.accDep.add(d.locDep);
                this.reasoner.reportClash(this.accDep, "C-MM");
            }
            return checkMinMaxClash;
        }
        return false;
    }

    private boolean emptyConstraints() {
        return this.constraints.isEmpty() || this.constraints.iterator().next().e == null;
    }

    /** @param other
     * @return true if compatible */
    public boolean checkCompatibleValue(DataTypeSituation<?> other) {
        if (!this.type.isCompatible(other.type)) {
            return false;
        }
        if (this.emptyConstraints() || other.emptyConstraints()) {
            return true;
        }
        List<Literal<?>> allLiterals = new ArrayList<Literal<?>>(this.literals);
        allLiterals.addAll(other.literals);
        List<Datatype<?>> allRestrictions = new ArrayList<Datatype<?>>();
        for (DepInterval<?> d : other.constraints) {
            if (d.e != null) {
                allRestrictions.add(d.e);
            }
        }
        for (DepInterval<?> d : this.constraints) {
            if (d.e != null) {
                allRestrictions.add(d.e);
            }
        }
        for (Literal<?> l : allLiterals) {
            if (!this.type.isCompatible(l) || !other.type.isCompatible(l)) {
                return false;
            }
            for (Datatype<?> d : allRestrictions) {
                if (!d.isCompatible(l)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** data interval with dep-sets */
    static class DepInterval<R extends Comparable<R>> {
        DatatypeExpression<R> e;
        /** local dep-set */
        FastSetSimple locDep;

        @Override
        public String toString() {
            return "depInterval{" + this.e + "}";
        }

        /** update MIN border of an TYPE's interval with VALUE wrt EXCL
         * 
         * @param value
         * @param dep
         * @return true if updated */
        public boolean update(Datatype<R> value, DepSet dep) {
            if (this.e == null) {
                if (value.isExpression()) {
                    this.e = value.asExpression();
                } else {
                    this.e = DatatypeFactory.getDatatypeExpression(value);
                }
                this.locDep = dep == null ? null : dep.getDelegate();
                return false;
            } else {
                // TODO compare value spaces
                if (this.e instanceof DatatypeEnumeration
                        || this.e instanceof DatatypeNegation) {
                    // cannot update an enumeration
                    return false;
                }
                for (Map.Entry<Facet, Object> f : value.getKnownFacetValues().entrySet()) {
                    this.e = this.e.addFacet(f.getKey(), f.getValue());
                }
            }
            // TODO needs to return false if the new expression has the same
            // value space as the old one
            this.locDep = dep == null ? null : dep.getDelegate();
            return true;
        }

        /** check if the interval is consistent wrt given type
         * 
         * @param type
         * @return true if consistent */
        public boolean consistent(Datatype<R> type) {
            return this.e == null || this.e.isCompatible(type);
        }

        public boolean checkMinMaxClash() {
            if (this.e == null) {
                return false;
            }
            return this.e.emptyValueSpace();
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            }
            if (obj instanceof DepInterval) {
                return (this.e == null ? ((DepInterval<?>) obj).e == null : this.e
                        .equals(((DepInterval<?>) obj).e)) && this.locDep == null ? ((DepInterval<?>) obj).locDep == null
                        : this.locDep.equals(((DepInterval<?>) obj).locDep);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.e == null ? 0 : this.e.hashCode())
                    + (this.locDep == null ? 0 : this.locDep.hashCode());
        }
    }

    // presence interface
    /** check if type is present positively in the node
     * 
     * @return true if pType not null */
    public boolean hasPType() {
        return this.pType != null;
    }

    /** check if type is present negatively in the node
     * 
     * @return true if nType not null */
    public boolean hasNType() {
        return this.nType != null;
    }

    /** set the precense of the PType
     * 
     * @param type */
    public void setPType(DepSet type) {
        this.pType = type;
    }

    /** @param t */
    public void setNType(DepSet t) {
        this.nType = t;
    }

    /** @return pType */
    public DepSet getPType() {
        return this.pType;
    }

    /** @return nType */
    public DepSet getNType() {
        return this.nType;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + this.constraints;
    }
}
