package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeClashes.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.FastSetSimple;

/**
 * @author ignazio
 * @param <R>
 *        type
 */
public class DataTypeSituation<R extends Comparable<R>> implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** positive type appearance */
    private DepSet pType;
    /** negative type appearance */
    private DepSet nType;
    /** interval of possible values */
    private Set<DepInterval<R>> constraints = new HashSet<>();
    /** accumulated dep-set */
    private final DepSet accDep = DepSet.create();
    /** dep-set for the clash */
    private final DataTypeReasoner reasoner;
    private final Datatype<R> type;
    private final List<Literal<?>> literals = new ArrayList<>();

    protected DataTypeSituation(Datatype<R> p, DataTypeReasoner dep) {
        if (p == null) {
            throw new IllegalArgumentException("p cannot be null");
        }
        this.type = p;
        this.reasoner = dep;
        this.constraints.add(new DepInterval<R>());
    }

    /**
     * update and add a single interval I to the constraints.
     * 
     * @param i
     *        dependency interval
     * @param interval
     *        datatype interval
     * @param localDep
     *        localDep
     * @return true iff clash occurs
     */
    private boolean addUpdatedInterval(DepInterval<R> i, Datatype<R> interval,
            DepSet localDep) {
        if (!i.consistent(interval)) {
            localDep.add(i.locDep);
            this.reasoner.reportClash(localDep, DT_C_IT);
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

    /**
     * add restrictions [POS]INT to intervals
     * 
     * @param pos
     *        pos
     * @param interval
     *        interval
     * @param dep
     *        dep
     * @return true if clash occurs
     */
    public boolean addInterval(boolean pos, Datatype<R> interval, DepSet dep) {
        if (interval.emptyValueSpace()) {
            this.reasoner.reportClash(this.accDep, DT_Empty_interval);
            return true;
        }
        if (interval instanceof DatatypeEnumeration) {
            this.literals.addAll(interval.listValues());
        }
        Datatype<R> realInterval = pos ? interval : new DatatypeNegation<>(
                interval);
        Set<DepInterval<R>> c = this.constraints;
        this.constraints = new HashSet<>();
        for (DepInterval<R> d : c) {
            if (this.addUpdatedInterval(d, realInterval, DepSet.create(dep))) {
                return true;
            }
        }
        if (this.constraints.isEmpty()) {
            this.reasoner.reportClash(this.accDep, DT_C_MM);
            return true;
        }
        return false;
    }

    /** @return true iff PType and NType leads to clash */
    public boolean checkPNTypeClash() {
        if (this.hasNType() && this.hasPType() && getNType().equals(getPType())) {
            this.reasoner.reportClash(this.pType, this.nType, DT_TNT);
            return true;
        }
        for (DepInterval<R> d : this.constraints) {
            boolean checkMinMaxClash = d.checkMinMaxClash();
            if (checkMinMaxClash) {
                d.checkMinMaxClash();
                this.accDep.add(d.locDep);
                this.reasoner.reportClash(this.accDep, DT_C_MM);
                return checkMinMaxClash;
            }
        }
        return false;
    }

    private boolean emptyConstraints() {
        return this.constraints.isEmpty()
                || this.constraints.iterator().next().e == null;
    }

    /**
     * @param other
     *        other
     * @return true if compatible
     */
    public boolean checkCompatibleValue(DataTypeSituation<?> other) {
        if (this.type.equals(DatatypeFactory.LITERAL) && emptyConstraints()
                || other.type.equals(DatatypeFactory.LITERAL)
                && other.emptyConstraints()) {
            return true;
        }
        if (!this.type.isCompatible(other.type)) {
            return false;
        }
        if (this.emptyConstraints() && other.emptyConstraints()) {
            return true;
        }
        if (other.literals.isEmpty() && other.emptyConstraints()) {
            return true;
        }
        if (literals.isEmpty() && emptyConstraints()) {
            return true;
        }
        List<Literal<?>> allLiterals = new ArrayList<>(this.literals);
        allLiterals.addAll(other.literals);
        List<Datatype<?>> allRestrictions = new ArrayList<>();
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
        boolean toReturn = compareLiterals(other, allLiterals, allRestrictions);
        // if signs are the same, return the comparison
        if (hasNType() == other.hasNType() || hasPType() == other.hasPType()) {
            return toReturn;
        }
        if (!allRestrictions.isEmpty()) {
            return toReturn;
        }
        // otherwise signs differ and there are no constraints; return the
        // opposite
        // example: -short and {0}
        return !toReturn;
    }

    private boolean compareLiterals(DataTypeSituation<?> other,
            List<Literal<?>> allLiterals, List<Datatype<?>> allRestrictions) {
        boolean toReturn = true;
        for (Literal<?> l : allLiterals) {
            if (!this.type.isCompatible(l) || !other.type.isCompatible(l)) {
                toReturn = false;
            }
            for (Datatype<?> d : allRestrictions) {
                if (!d.isCompatible(l)) {
                    toReturn = false;
                }
            }
        }
        return toReturn;
    }

    /**
     * data interval with dep-sets
     * 
     * @param <R>
     *        type
     */
    static class DepInterval<R extends Comparable<R>> implements Serializable {

        private static final long serialVersionUID = 11000L;
        protected DatatypeExpression<R> e;
        /** local dep-set */
        protected FastSetSimple locDep;

        @Override
        public String toString() {
            return "depInterval{" + this.e + '}';
        }

        /**
         * update MIN border of an TYPE's interval with VALUE wrt EXCL
         * 
         * @param value
         *        value
         * @param dep
         *        dep
         * @return true if updated
         */
        @SuppressWarnings("rawtypes")
        public boolean update(Datatype<R> value, DepSet dep) {
            if (this.e == null) {
                if (value.isExpression()) {
                    this.e = value.asExpression();
                } else {
                    this.e = DatatypeFactory.getDatatypeExpression(value);
                }
                if (locDep == null) {
                    locDep = dep == null ? null : dep.getDelegate();
                } else if (dep != null) {
                    locDep.addAll(dep.getDelegate());
                }
                return false;
            } else {
                // TODO compare value spaces
                if (this.e instanceof DatatypeEnumeration
                        || this.e instanceof DatatypeNegation) {
                    // cannot update an enumeration
                    return false;
                }
                for (Map.Entry<Facet, Comparable> f : value
                        .getKnownNumericFacetValues().entrySet()) {
                    this.e = this.e.addNumericFacet(f.getKey(), f.getValue());
                }
                for (Map.Entry<Facet, Comparable> f : value
                        .getKnownNonNumericFacetValues().entrySet()) {
                    this.e = this.e
                            .addNonNumericFacet(f.getKey(), f.getValue());
                }
            }
            // TODO needs to return false if the new expression has the same
            // value space as the old one
            if (locDep == null) {
                locDep = dep == null ? null : dep.getDelegate();
            } else if (dep != null) {
                locDep.addAll(dep.getDelegate());
            }
            return true;
        }

        /**
         * @param interval
         *        the interval to check
         * @return true if this interval can be updated to include interval
         */
        public boolean updateable(Datatype<R> interval) {
            if (this.e == null) {
                return true;
            } else {
                // TODO compare value spaces
                if (this.e instanceof DatatypeNegation) {
                    // cannot update a negation
                    return false;
                }
                // XXX this needs to be more general
                if (e instanceof DatatypeEnumeration
                        && interval instanceof DatatypeEnumeration) {
                    return true;
                }
            }
            return true;
        }

        /**
         * check if the interval is consistent wrt given type
         * 
         * @param type
         *        type
         * @return true if consistent
         */
        private boolean consistent(Datatype<R> type) {
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
                return (this.e == null ? ((DepInterval<?>) obj).e == null
                        : this.e.equals(((DepInterval<?>) obj).e))
                        && this.locDep == null ? ((DepInterval<?>) obj).locDep == null
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
    /**
     * check if type is present positively in the node
     * 
     * @return true if pType not null
     */
    public boolean hasPType() {
        return this.pType != null;
    }

    /**
     * check if type is present negatively in the node
     * 
     * @return true if nType not null
     */
    public boolean hasNType() {
        return this.nType != null;
    }

    /**
     * set the precense of the PType
     * 
     * @param type
     *        type
     */
    public void setPType(DepSet type) {
        this.pType = type;
    }

    /**
     * @param t
     *        depset for negative type
     */
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
        return this.getClass().getSimpleName() + ' ' + type + ' '
                + this.constraints;
    }
}
