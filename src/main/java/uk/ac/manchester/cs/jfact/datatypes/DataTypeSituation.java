package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;
import static uk.ac.manchester.cs.jfact.datatypes.DatatypeClashes.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import uk.ac.manchester.cs.jfact.dep.DepSet;

/**
 * @author ignazio
 * @param <R>
 *        type
 */
public class DataTypeSituation<R extends Comparable<R>> implements Serializable {

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
    @Nonnull private final Datatype<R> type;
    private final List<Literal<?>> literals = new ArrayList<>();

    protected DataTypeSituation(Datatype<R> p, DataTypeReasoner dep) {
        this.type = checkNotNull(p);
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
    private boolean addUpdatedInterval(DepInterval<R> i, Datatype<R> interval, DepSet localDep) {
        if (!i.consistent(interval)) {
            localDep.add(i.locDep);
            this.reasoner.reportClash(localDep, DT_C_IT);
            return true;
        }
        if (!i.update(interval, localDep) || !this.hasPType() || !i.checkMinMaxClash()) {
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
     * @param interval
     *        interval
     * @param dep
     *        dep
     * @return true if clash occurs
     */
    public boolean addInterval(Datatype<R> interval, DepSet dep) {
        if (interval.emptyValueSpace()) {
            this.reasoner.reportClash(this.accDep, DT_EMPTY_INTERVAL);
            return true;
        }
        if (interval instanceof DatatypeEnumeration) {
            this.literals.addAll(interval.listValues());
        }
        Set<DepInterval<R>> c = this.constraints;
        this.constraints = new HashSet<>();
        if (c.stream().anyMatch(d -> addUpdatedInterval(d, interval, dep))) {
            return true;
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
            if (d.checkMinMaxClash()) {
                this.accDep.add(d.locDep);
                this.reasoner.reportClash(this.accDep, DT_C_MM);
                return true;
            }
        }
        return false;
    }

    private boolean emptyConstraints() {
        return this.constraints.isEmpty() || this.constraints.iterator().next().e == null;
    }

    /**
     * @param other
     *        other
     * @return true if compatible
     */
    public boolean checkCompatibleValue(DataTypeSituation<?> other) {
        if (this.type.equals(DatatypeFactory.LITERAL) && emptyConstraints() || other.type.equals(
            DatatypeFactory.LITERAL) && other.emptyConstraints()) {
            return true;
        }
        if (incompatible(other)) {
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
        other.constraints.stream().filter(d -> d.e != null).forEach(d -> allRestrictions.add(d.e));
        constraints.stream().filter(d -> d.e != null).forEach(d -> allRestrictions.add(d.e));
        boolean toReturn = compareLiterals(other, allLiterals, allRestrictions);
        // if signs are the same, return the comparison
        if (hasNType() == other.hasNType() || hasPType() == other.hasPType()) {
            return toReturn;
        }
        if (!allRestrictions.isEmpty()) {
            return toReturn;
        }
        // otherwise signs differ and there are no constraints; return the
        // opposite.
        // example: -short and {0}.
        return !toReturn;
    }

    protected boolean incompatible(DataTypeSituation<?> other) {
        return !this.type.isCompatible(other.type);
    }

    private boolean compareLiterals(DataTypeSituation<?> other, List<Literal<?>> allLiterals,
        List<Datatype<?>> allRestrictions) {
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

    /**
     * data interval with dep-sets
     * 
     * @param <R>
     *        type
     */
    static class DepInterval<R extends Comparable<R>> implements Serializable {

        protected DatatypeExpression<R> e;
        /** local dep-set */
        protected DepSet locDep;

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
        public boolean update(Datatype<R> value, @Nullable DepSet dep) {
            if (this.e == null) {
                if (value.isExpression()) {
                    this.e = value.asExpression();
                } else {
                    this.e = value.wrapAsDatatypeExpression();
                }
                if (locDep == null) {
                    locDep = dep;
                } else if (dep != null) {
                    locDep.add(dep);
                }
                return false;
            } else {
                if (this.e instanceof DatatypeEnumeration || this.e instanceof DatatypeNegation) {
                    // cannot update an enumeration
                    return false;
                }
                value.getKnownNumericFacetValues().forEach((k, v) -> e = e.addNumericFacet(k, v));
                value.getKnownNonNumericFacetValues().forEach((k, v) -> e = e.addNonNumericFacet(k, v));
            }
            // TODO needs to return false if the new expression has the same
            // value space as the old one
            if (locDep == null) {
                locDep = dep;
            } else if (dep != null) {
                locDep.add(dep);
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
                if (this.e instanceof DatatypeNegation) {
                    // cannot update a negation
                    return false;
                }
                // XXX this needs to be more general
                if (e instanceof DatatypeEnumeration && interval instanceof DatatypeEnumeration) {
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
        boolean consistent(@Nullable Datatype<?> type) {
            return this.e == null || type == null || this.e.isCompatible(type);
        }

        public boolean checkMinMaxClash() {
            if (this.e == null) {
                return false;
            }
            return this.e.emptyValueSpace();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (super.equals(obj)) {
                return true;
            }
            if (obj instanceof DepInterval) {
                return (this.e == null ? ((DepInterval<?>) obj).e == null : this.e.equals(((DepInterval<?>) obj).e))
                    && this.locDep == null ? ((DepInterval<?>) obj).locDep == null
                        : this.locDep.equals(((DepInterval<?>) obj).locDep);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.e == null ? 0 : this.e.hashCode()) + (this.locDep == null ? 0 : this.locDep.hashCode());
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
        return this.getClass().getSimpleName() + ' ' + type + ' ' + this.constraints;
    }

    private boolean hasNoConstraints() {
        return constraints.isEmpty() || constraints.stream().allMatch(c -> c.e == null);
    }

    private boolean hasConstraints() {
        return !constraints.isEmpty() || constraints.stream().anyMatch(c -> c.e != null);
    }

    /**
     * @param other
     *        situation to test
     * @return true if this situation represents a subtype of the other
     *         situation, i.e., type is a subtype of other type and all
     *         constraints in this situation are compatible with other
     *         constraints
     */
    public boolean isSubType(DataTypeSituation<?> other) {
        // if the types are not compatible, this is not subtype of the input
        if (incompatible(other)) {
            return false;
        }
        if (!type.isSubType(other.type)) {
            return false;
        }
        // if the supertype does nto have any constraints, the result must be
        // true
        if (other.hasNoConstraints()) {
            return true;
        }
        // same type but other has constraints and this does not: this cannot be
        // a subtype
        if (type.equals(other.type) && hasConstraints()) {
            return false;
        }
        // each constraint must be compatible with the supertype
        return constraints.stream().allMatch(c -> other.constraints.stream().allMatch(c1 -> c.consistent(c1.e)));
    }
}
