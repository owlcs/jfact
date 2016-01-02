package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;

/** expression factory */
@PortedFrom(file = "tExpressionManager.h", name = "TExpressionManager")
public class ExpressionManager implements Serializable {

    /** TOP concept */
    @PortedFrom(file = "tExpressionManager.h", name = "CTop") @Nonnull private static final ConceptTop top = new ConceptTop();
    /** BOTTOM concept */
    @PortedFrom(file = "tExpressionManager.h", name = "CBottom") @Nonnull private static final ConceptBottom bottom = new ConceptBottom();
    /** TOP data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DTop") @Nonnull private static final DataTop dataTop = new DataTop();
    /** TOP object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ORTop") @Nonnull private static final ObjectRoleExpression objectRoleTop = new ObjectRoleTop();
    /** BOTTOM object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ORBottom") @Nonnull private static final ObjectRoleExpression objectRoleBottom = new ObjectRoleBottom();
    /** TOP data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DRTop") @Nonnull private static final DataRoleExpression dataRoleTop = new DataRoleTop();
    /** BOTTOM data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DRBottom") @Nonnull private static final DataRoleExpression dataRoleBottom = new DataRoleBottom();
    @PortedFrom(file = "tExpressionManager.h", name = "DBottom") @Nonnull private static final DataBottom dataBottom = new DataBottom();

    private ExpressionManager() {}

    // top/bottom roles
    /**
     * @param r
     *        R
     * @return true iff R is a top data/object role
     */
    @PortedFrom(file = "tExpressionManager.h", name = "isUniversalRole")
    public static boolean isUniversalRole(RoleExpression r) {
        return r.equals(dataRoleTop) || r.equals(objectRoleTop);
    }

    /**
     * @param r
     *        R
     * @return true iff R is a bottom data/object role
     */
    @PortedFrom(file = "tExpressionManager.h", name = "isEmptyRole")
    public static boolean isEmptyRole(RoleExpression r) {
        return r.equals(dataRoleBottom) || r.equals(objectRoleBottom);
    }

    /** @return TOP concept */
    @PortedFrom(file = "tExpressionManager.h", name = "top")
    public static ConceptTop top() {
        return top;
    }

    /** @return BOTTOM concept */
    @PortedFrom(file = "tExpressionManager.h", name = "bottom")
    public static ConceptBottom bottom() {
        return bottom;
    }

    /**
     * @param r
     *        C
     * @return negation of a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Not")
    public static ConceptExpression not(ConceptExpression r) {
        return new ConceptNot(r);
    }

    /**
     * @param l
     *        l
     * @return an n-ary conjunction expression; take the arguments from the last
     *         argument list
     */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    public static ConceptExpression and(List<ConceptExpression> l) {
        return new ConceptAnd(l);
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return C and D
     */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    public static ConceptExpression and(ConceptExpression c, ConceptExpression d) {
        if (c.equals(d)) {
            return c;
        }
        if (c instanceof ConceptTop) {
            return d;
        }
        if (d instanceof ConceptTop) {
            return c;
        }
        return and(Arrays.<ConceptExpression> asList(c, d));
    }

    /**
     * @param c
     *        C
     * @param d
     *        D
     * @return C or D
     */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    public static ConceptExpression or(ConceptExpression c, ConceptExpression d) {
        return or(Arrays.<ConceptExpression> asList(c, d));
    }

    /**
     * @param l
     *        l
     * @return an n-ary disjunction expression; take the arguments from the last
     *         argument list
     */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    public static ConceptExpression or(List<ConceptExpression> l) {
        return new ConceptOr(l);
    }

    /**
     * @param r
     *        R
     * @return self-reference restriction of an object role R
     */
    @PortedFrom(file = "tExpressionManager.h", name = "SelfReference")
    public static ConceptExpression selfReference(ObjectRoleExpression r) {
        return new ConceptObjectSelf(r);
    }

    /**
     * @param r
     *        R
     * @param i
     *        I
     * @return value restriction wrt an object role R and an individual I
     */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    public static ConceptExpression value(ObjectRoleExpression r, IndividualExpression i) {
        return new ConceptObjectValue(r, i);
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return existential restriction wrt an object role R and a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    public static ConceptExpression exists(ObjectRoleExpression r, ConceptExpression c) {
        return new ConceptObjectExists(r, c);
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return universal restriction wrt an object role R and a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    public static ConceptExpression forall(ObjectRoleExpression r, ConceptExpression c) {
        return new ConceptObjectForall(r, c);
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     * @return min cardinality restriction wrt number N, an object role R and a
     *         concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    public static ConceptExpression minCardinality(int n, ObjectRoleExpression r, ConceptExpression c) {
        return new ConceptObjectMinCardinality(n, r, c);
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     * @return max cardinality restriction wrt number N, an object role R and a
     *         concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    public static ConceptExpression maxCardinality(int n, ObjectRoleExpression r, ConceptExpression c) {
        return new ConceptObjectMaxCardinality(n, r, c);
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param c
     *        C
     * @return exact cardinality restriction wrt number N, an object role R and
     *         a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    public static ConceptExpression cardinality(int n, ObjectRoleExpression r, ConceptExpression c) {
        return new ConceptObjectExactCardinality(n, r, c);
    }

    /**
     * @param r
     *        R
     * @param v
     *        V
     * @return value restriction wrt a data role R and a data value V
     */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    public static ConceptExpression value(DataRoleExpression r, Literal<?> v) {
        return new ConceptDataValue(r, v);
    }

    /**
     * @param r
     *        R
     * @param e
     *        E
     * @return existential restriction wrt a data role R and a data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    public static ConceptExpression exists(DataRoleExpression r, DataExpression e) {
        return new ConceptDataExists(r, e);
    }

    /**
     * @param r
     *        R
     * @param e
     *        E
     * @return universal restriction wrt a data role R and a data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    public static ConceptExpression forall(DataRoleExpression r, DataExpression e) {
        return new ConceptDataForall(r, e);
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param e
     *        E
     * @return min cardinality restriction wrt number N, a data role R and a
     *         data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    public static ConceptExpression minCardinality(int n, DataRoleExpression r, DataExpression e) {
        return new ConceptDataMinCardinality(n, r, e);
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param e
     *        E
     * @return max cardinality restriction wrt number N, a data role R and a
     *         data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    public static ConceptExpression maxCardinality(int n, DataRoleExpression r, DataExpression e) {
        return new ConceptDataMaxCardinality(n, r, e);
    }

    /**
     * @param n
     *        n
     * @param r
     *        R
     * @param e
     *        E
     * @return exact cardinality restriction wrt number N, a data role R and a
     *         data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    public static ConceptExpression cardinality(int n, DataRoleExpression r, DataExpression e) {
        return new ConceptDataExactCardinality(n, r, e);
    }

    // object roles
    /** @return TOP object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleTop")
    public static ObjectRoleExpression objectRoleTop() {
        return objectRoleTop;
    }

    /** @return BOTTOM object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleBottom")
    public static ObjectRoleExpression objectRoleBottom() {
        return objectRoleBottom;
    }

    /**
     * @param l
     *        l
     * @return a role chain corresponding to R1 o ... o Rn; take the arguments
     *         from the last argument list
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Compose")
    public static ObjectRoleComplexExpression compose(List<ObjectRoleExpression> l) {
        return new ObjectRoleChain(l);
    }

    /**
     * @param e1
     *        e1
     * @param e2
     *        e2
     * @return ObjectRoleChain
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Compose")
    public static ObjectRoleComplexExpression compose(ObjectRoleExpression e1, ObjectRoleExpression e2) {
        return new ObjectRoleChain(Arrays.asList(e1, e2));
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return a expression corresponding to R projected from C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectFrom")
    public static ObjectRoleComplexExpression projectFrom(ObjectRoleExpression r, ConceptExpression c) {
        return new ObjectRoleProjectionFrom(r, c);
    }

    /**
     * @param r
     *        R
     * @param c
     *        C
     * @return a expression corresponding to R projected into C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectInto")
    public static ObjectRoleComplexExpression projectInto(ObjectRoleExpression r, ConceptExpression c) {
        return new ObjectRoleProjectionInto(r, c);
    }

    // data roles
    /** @return TOP data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleTop")
    public static DataRoleExpression dataRoleTop() {
        return dataRoleTop;
    }

    /** @return BOTTOM data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleBottom")
    public static DataRoleExpression dataRoleBottom() {
        return dataRoleBottom;
    }

    // data expressions
    /** @return TOP data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataTop")
    public static DataExpression dataTop() {
        return dataTop;
    }

    /** @return BOTTOM data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataBottom")
    public static DataBottom dataBottom() {
        return dataBottom;
    }

    /**
     * @param rm
     *        E
     * @return negation of a data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataNot")
    public static DataExpression dataNot(DataExpression rm) {
        return new DataNot(rm);
    }

    /**
     * @param l
     *        l
     * @return an n-ary data conjunction expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataAnd")
    public static DataExpression dataAnd(List<DataExpression> l) {
        return new DataAnd(l);
    }

    /**
     * @param l
     *        l
     * @return an n-ary data disjunction expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOr")
    public static DataExpression dataOr(List<DataExpression> l) {
        return new DataOr(l);
    }

    /**
     * @param l
     *        l
     * @return an n-ary data one-of expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOneOf")
    public static DataExpression dataOneOf(List<Literal<?>> l) {
        return new DataOneOf(l);
    }
}
