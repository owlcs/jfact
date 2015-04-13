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

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptNot;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectSelf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOr;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.DataBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataNot;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOr;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleProjectionInto;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleComplexExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;
import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;

/** expression factory */
@PortedFrom(file = "tExpressionManager.h", name = "TExpressionManager")
public class ExpressionManager implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** TOP concept */
    @PortedFrom(file = "tExpressionManager.h", name = "CTop")
    @Nonnull
    private static final ConceptTop top = new ConceptTop();
    /** BOTTOM concept */
    @PortedFrom(file = "tExpressionManager.h", name = "CBottom")
    @Nonnull
    private static final ConceptBottom bottom = new ConceptBottom();
    /** TOP data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DTop")
    @Nonnull
    private static final DataTop dataTop = new DataTop();
    /** TOP object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ORTop")
    @Nonnull
    private static final ObjectRoleExpression objectRoleTop = new ObjectRoleTop();
    /** BOTTOM object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ORBottom")
    @Nonnull
    private static final ObjectRoleExpression objectRoleBottom = new ObjectRoleBottom();
    /** TOP data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DRTop")
    @Nonnull
    private static final DataRoleExpression dataRoleTop = new DataRoleTop();
    /** BOTTOM data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DRBottom")
    @Nonnull
    private static final DataRoleExpression dataRoleBottom = new DataRoleBottom();
    @PortedFrom(file = "tExpressionManager.h", name = "DBottom")
    @Nonnull
    private static final DataBottom dataBottom = new DataBottom();

    private ExpressionManager() {}

    // top/bottom roles
    /**
     * @param R
     *        R
     * @return true iff R is a top data/object role
     */
    @PortedFrom(file = "tExpressionManager.h", name = "isUniversalRole")
    public static boolean isUniversalRole(RoleExpression R) {
        return R.equals(dataRoleTop) || R.equals(objectRoleTop);
    }

    /**
     * @param R
     *        R
     * @return true iff R is a bottom data/object role
     */
    @PortedFrom(file = "tExpressionManager.h", name = "isEmptyRole")
    public static boolean isEmptyRole(RoleExpression R) {
        return R.equals(dataRoleBottom) || R.equals(objectRoleBottom);
    }

    /** @return TOP concept */
    @PortedFrom(file = "tExpressionManager.h", name = "top")
    @Nonnull
    public static ConceptTop top() {
        return top;
    }

    /** @return BOTTOM concept */
    @PortedFrom(file = "tExpressionManager.h", name = "bottom")
    @Nonnull
    public static ConceptBottom bottom() {
        return bottom;
    }

    /**
     * @param C
     *        C
     * @return negation of a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Not")
    @Nonnull
    public static ConceptExpression not(ConceptExpression C) {
        return new ConceptNot(C);
    }

    /**
     * @param l
     *        l
     * @return an n-ary conjunction expression; take the arguments from the last
     *         argument list
     */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    @Nonnull
    public static ConceptExpression and(List<ConceptExpression> l) {
        return new ConceptAnd(l);
    }

    /**
     * @param C
     *        C
     * @param D
     *        D
     * @return C and D
     */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    @Nonnull
    public static ConceptExpression and(@Nonnull ConceptExpression C,
            @Nonnull ConceptExpression D) {
        if (C.equals(D)) {
            return C;
        }
        if (C instanceof ConceptTop) {
            return D;
        }
        if (D instanceof ConceptTop) {
            return C;
        }
        return and(Arrays.<ConceptExpression> asList(C, D));
    }

    /**
     * @param C
     *        C
     * @param D
     *        D
     * @return C or D
     */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    @Nonnull
    public static ConceptExpression
            or(ConceptExpression C, ConceptExpression D) {
        return or(Arrays.<ConceptExpression> asList(C, D));
    }

    /**
     * @param l
     *        l
     * @return an n-ary disjunction expression; take the arguments from the last
     *         argument list
     */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    @Nonnull
    public static ConceptExpression or(List<ConceptExpression> l) {
        return new ConceptOr(l);
    }

    /**
     * @param R
     *        R
     * @return self-reference restriction of an object role R
     */
    @PortedFrom(file = "tExpressionManager.h", name = "SelfReference")
    @Nonnull
    public static ConceptExpression selfReference(ObjectRoleExpression R) {
        return new ConceptObjectSelf(R);
    }

    /**
     * @param R
     *        R
     * @param I
     *        I
     * @return value restriction wrt an object role R and an individual I
     */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    @Nonnull
    public static ConceptExpression value(ObjectRoleExpression R,
            IndividualExpression I) {
        return new ConceptObjectValue(R, I);
    }

    /**
     * @param R
     *        R
     * @param C
     *        C
     * @return existential restriction wrt an object role R and a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    @Nonnull
    public static ConceptExpression exists(ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectExists(R, C);
    }

    /**
     * @param R
     *        R
     * @param C
     *        C
     * @return universal restriction wrt an object role R and a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    @Nonnull
    public static ConceptExpression forall(ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectForall(R, C);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return min cardinality restriction wrt number N, an object role R and a
     *         concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    @Nonnull
    public static ConceptExpression minCardinality(int n,
            ObjectRoleExpression R, ConceptExpression C) {
        return new ConceptObjectMinCardinality(n, R, C);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return max cardinality restriction wrt number N, an object role R and a
     *         concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    @Nonnull
    public static ConceptExpression maxCardinality(int n,
            ObjectRoleExpression R, ConceptExpression C) {
        return new ConceptObjectMaxCardinality(n, R, C);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param C
     *        C
     * @return exact cardinality restriction wrt number N, an object role R and
     *         a concept C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    @Nonnull
    public static ConceptExpression cardinality(int n, ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectExactCardinality(n, R, C);
    }

    /**
     * @param R
     *        R
     * @param V
     *        V
     * @return value restriction wrt a data role R and a data value V
     */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    @Nonnull
    public static ConceptExpression value(DataRoleExpression R, Literal<?> V) {
        return new ConceptDataValue(R, V);
    }

    /**
     * @param R
     *        R
     * @param E
     *        E
     * @return existential restriction wrt a data role R and a data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    @Nonnull
    public static ConceptExpression exists(DataRoleExpression R,
            DataExpression E) {
        return new ConceptDataExists(R, E);
    }

    /**
     * @param R
     *        R
     * @param E
     *        E
     * @return universal restriction wrt a data role R and a data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    @Nonnull
    public static ConceptExpression forall(DataRoleExpression R,
            DataExpression E) {
        return new ConceptDataForall(R, E);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param E
     *        E
     * @return min cardinality restriction wrt number N, a data role R and a
     *         data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    @Nonnull
    public static ConceptExpression minCardinality(int n, DataRoleExpression R,
            DataExpression E) {
        return new ConceptDataMinCardinality(n, R, E);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param E
     *        E
     * @return max cardinality restriction wrt number N, a data role R and a
     *         data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    @Nonnull
    public static ConceptExpression maxCardinality(int n, DataRoleExpression R,
            DataExpression E) {
        return new ConceptDataMaxCardinality(n, R, E);
    }

    /**
     * @param n
     *        n
     * @param R
     *        R
     * @param E
     *        E
     * @return exact cardinality restriction wrt number N, a data role R and a
     *         data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    @Nonnull
    public static ConceptExpression cardinality(int n, DataRoleExpression R,
            DataExpression E) {
        return new ConceptDataExactCardinality(n, R, E);
    }

    // object roles
    /** @return TOP object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleTop")
    @Nonnull
    public static ObjectRoleExpression objectRoleTop() {
        return objectRoleTop;
    }

    /** @return BOTTOM object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleBottom")
    @Nonnull
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
    @Nonnull
    public static ObjectRoleComplexExpression compose(
            List<ObjectRoleExpression> l) {
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
    @Nonnull
    public static ObjectRoleComplexExpression compose(ObjectRoleExpression e1,
            ObjectRoleExpression e2) {
        return new ObjectRoleChain(Arrays.asList(e1, e2));
    }

    /**
     * @param R
     *        R
     * @param C
     *        C
     * @return a expression corresponding to R projected from C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectFrom")
    @Nonnull
    public static ObjectRoleComplexExpression projectFrom(
            ObjectRoleExpression R, ConceptExpression C) {
        return new ObjectRoleProjectionFrom(R, C);
    }

    /**
     * @param R
     *        R
     * @param C
     *        C
     * @return a expression corresponding to R projected into C
     */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectInto")
    @Nonnull
    public static ObjectRoleComplexExpression projectInto(
            ObjectRoleExpression R, ConceptExpression C) {
        return new ObjectRoleProjectionInto(R, C);
    }

    // data roles
    /** @return TOP data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleTop")
    @Nonnull
    public static DataRoleExpression dataRoleTop() {
        return dataRoleTop;
    }

    /** @return BOTTOM data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleBottom")
    @Nonnull
    public static DataRoleExpression dataRoleBottom() {
        return dataRoleBottom;
    }

    // data expressions
    /** @return TOP data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataTop")
    @Nonnull
    public static DataExpression dataTop() {
        return dataTop;
    }

    /** @return BOTTOM data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataBottom")
    @Nonnull
    public static DataBottom dataBottom() {
        return dataBottom;
    }

    /** @return basic string data type */
    @Original
    @Nonnull
    public static String getDataTop() {
        // XXX there is no link between TDLDataTop and the uri
        return Vocabulary.LITERAL;
    }

    /**
     * @param E
     *        E
     * @return negation of a data expression E
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataNot")
    @Nonnull
    public static DataExpression dataNot(DataExpression E) {
        return new DataNot(E);
    }

    /**
     * @param l
     *        l
     * @return an n-ary data conjunction expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataAnd")
    @Nonnull
    public static DataExpression dataAnd(List<DataExpression> l) {
        return new DataAnd(l);
    }

    /**
     * @param l
     *        l
     * @return an n-ary data disjunction expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOr")
    @Nonnull
    public static DataExpression dataOr(List<DataExpression> l) {
        return new DataOr(l);
    }

    /**
     * @param l
     *        l
     * @return an n-ary data one-of expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOneOf")
    @Nonnull
    public static DataExpression dataOneOf(List<Literal<?>> l) {
        return new DataOneOf(l);
    }
}
