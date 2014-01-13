package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptDataValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptNot;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExactCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectExists;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectForall;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMaxCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectMinCardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectSelf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptObjectValue;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOr;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataAnd;
import uk.ac.manchester.cs.jfact.kernel.dl.DataBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataNot;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.DataOr;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleTop;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleBottom;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleChain;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
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
import conformance.Original;
import conformance.PortedFrom;

/** expression factory */
@PortedFrom(file = "tExpressionManager.h", name = "TExpressionManager")
public class ExpressionManager implements Serializable {
    private static final long serialVersionUID = 11000L;

    /** Cache for the inverse roles */
    protected class InverseRoleCache implements Serializable {
        private static final long serialVersionUID = 11000L;
        /** map tail into an object head(tail) */
        private Map<ObjectRoleExpression, ObjectRoleExpression> map;

        /** @param tail
         *            tail
         * @return an object corresponding to Head.Tail */
        public ObjectRoleExpression get(ObjectRoleExpression tail) {
            // try to find cached dep-set
            if (map != null && map.containsKey(tail)) {
                return map.get(tail);
            }
            // no cached entry -- create a new one and cache it
            ObjectRoleExpression concat = build(tail);
            if (map == null) {
                map = new HashMap<ObjectRoleExpression, ObjectRoleExpression>();
            }
            map.put(tail, concat);
            return concat;
        }

        /** clear the cache */
        public void clear() {
            if (map != null) {
                map.clear();
            }
        }

        public ObjectRoleExpression build(ObjectRoleExpression tail) {
            return new ObjectRoleInverse(tail);
        }
    }

    protected static class DataroleNameCreator implements NameCreator<DataRoleName, IRI>,
            Serializable {
        private static final long serialVersionUID = 11000L;

        @Override
        public DataRoleName makeEntry(IRI name) {
            return new DataRoleName(name);
        }
    }

    protected static class ObjectroleNameCreator implements
            NameCreator<ObjectRoleName, IRI>, Serializable {
        private static final long serialVersionUID = 11000L;

        @Override
        public ObjectRoleName makeEntry(IRI name) {
            return new ObjectRoleName(name);
        }
    }

    protected static class IndividualNameCreator implements
            NameCreator<IndividualName, IRI>, Serializable {
        private static final long serialVersionUID = 11000L;

        @Override
        public IndividualName makeEntry(IRI name) {
            return new IndividualName(name);
        }
    }

    protected static class ConceptNameCreator implements NameCreator<ConceptName, IRI>,
            Serializable {
        private static final long serialVersionUID = 11000L;

        @Override
        public ConceptName makeEntry(IRI name) {
            return new ConceptName(name);
        }
    }

    /** nameset for concepts */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_C")
    private final NameSet<ConceptName, IRI> conceptNameset = new NameSet<ConceptName, IRI>(
            new ConceptNameCreator());
    /** nameset for individuals */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_I")
    private final NameSet<IndividualName, IRI> individualNameset = new NameSet<IndividualName, IRI>(
            new IndividualNameCreator());
    /** nameset for object roles */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_OR")
    private final NameSet<ObjectRoleName, IRI> objectRoleNameset = new NameSet<ObjectRoleName, IRI>(
            new ObjectroleNameCreator());
    /** nameset for data roles */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_DR")
    private final NameSet<DataRoleName, IRI> dataRoleNameset = new NameSet<DataRoleName, IRI>(
            new DataroleNameCreator());
    /** TOP concept */
    @PortedFrom(file = "tExpressionManager.h", name = "CTop")
    private final ConceptTop top = new ConceptTop();
    /** BOTTOM concept */
    @PortedFrom(file = "tExpressionManager.h", name = "CBottom")
    private final ConceptBottom bottom = new ConceptBottom();
    /** TOP data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DTop")
    private final DataTop dataTop = new DataTop();
    /** TOP object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ORTop")
    private ObjectRoleExpression objectRoleTop = new ObjectRoleTop();
    /** BOTTOM object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ORBottom")
    private ObjectRoleExpression objectRoleBottom = new ObjectRoleBottom();
    /** TOP data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DRTop")
    private DataRoleExpression dataRoleTop = new DataRoleTop();
    /** BOTTOM data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DRBottom")
    private DataRoleExpression dataRoleBottom = new DataRoleBottom();
    @PortedFrom(file = "tExpressionManager.h", name = "DBottom")
    private final DataBottom dataBottom = new DataBottom();
    /** cache for the role inverses */
    @PortedFrom(file = "tExpressionManager.h", name = "InverseRoleCache")
    private final InverseRoleCache inverseRoleCache = new InverseRoleCache();
    /** cache for the one-of singletons */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOfCache")
    private final Map<IndividualExpression, ConceptExpression> OneOfCache = new HashMap<IndividualExpression, ConceptExpression>();

    /** set Top/Bot properties
     * 
     * @param topORoleName
     *            topORoleName
     * @param botORoleName
     *            botORoleName
     * @param topDRoleName
     *            topDRoleName
     * @param botDRoleName
     *            botDRoleName */
    @PortedFrom(file = "tExpressionManager.h", name = "setTopBottomRoles")
    public void setTopBottomRoles(IRI topORoleName, IRI botORoleName, IRI topDRoleName,
            IRI botDRoleName) {
        objectRoleTop = new ObjectRoleName(topORoleName);
        objectRoleBottom = new ObjectRoleName(botORoleName);
        dataRoleTop = new DataRoleName(topDRoleName);
        dataRoleBottom = new DataRoleName(botDRoleName);
    }

    /** clear name caches */
    @PortedFrom(file = "tExpressionManager.h", name = "clearNameCache")
    public void clearNameCache() {
        conceptNameset.clear();
        objectRoleNameset.clear();
        dataRoleNameset.clear();
        individualNameset.clear();
    }

    /** clear the maps */
    @PortedFrom(file = "tExpressionManager.h", name = "clear")
    public void clear() {
        clearNameCache();
        inverseRoleCache.clear();
        OneOfCache.clear();
    }

    // top/bottom roles
    /** @param R
     *            R
     * @return true iff R is a top data/object role */
    @PortedFrom(file = "tExpressionManager.h", name = "isUniversalRole")
    public boolean isUniversalRole(RoleExpression R) {
        return R.equals(dataRoleTop) || R.equals(objectRoleTop);
    }

    /** @param R
     *            R
     * @return true iff R is a bottom data/object role */
    @PortedFrom(file = "tExpressionManager.h", name = "isEmptyRole")
    public boolean isEmptyRole(RoleExpression R) {
        return R.equals(dataRoleBottom) || R.equals(objectRoleBottom);
    }

    /** @return number of registered concepts */
    @PortedFrom(file = "tExpressionManager.h", name = "nConcepts")
    public int nConcepts() {
        return conceptNameset.size();
    }

    /** @return number of registered individuals */
    @PortedFrom(file = "tExpressionManager.h", name = "nIndividuals")
    public int nIndividuals() {
        return individualNameset.size();
    }

    /** @return number of registered object roles */
    @PortedFrom(file = "tExpressionManager.h", name = "nORoles")
    public int nORoles() {
        return objectRoleNameset.size();
    }

    /** @return number of registered data roles */
    @PortedFrom(file = "tExpressionManager.h", name = "nDRoles")
    public int nDRoles() {
        return dataRoleNameset.size();
    }

    /** @return TOP concept */
    @PortedFrom(file = "tExpressionManager.h", name = "top")
    public ConceptTop top() {
        return top;
    }

    /** @return BOTTOM concept */
    @PortedFrom(file = "tExpressionManager.h", name = "bottom")
    public ConceptBottom bottom() {
        return bottom;
    }

    /** @param name
     *            name
     * @return named concept */
    @PortedFrom(file = "tExpressionManager.h", name = "concept")
    public ConceptName concept(IRI name) {
        return conceptNameset.insert(name);
    }

    /** @param C
     *            C
     * @return negation of a concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "Not")
    public ConceptExpression not(ConceptExpression C) {
        return new ConceptNot(C);
    }

    /** @param l
     *            l
     * @return an n-ary conjunction expression; take the arguments from the last
     *         argument list */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    public ConceptExpression and(List<ConceptExpression> l) {
        return new ConceptAnd(l);
    }

    /** @param C
     *            C
     * @param D
     *            D
     * @return C and D */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    public ConceptExpression and(ConceptExpression C, ConceptExpression D) {
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

    /** @param C
     *            C
     * @param D
     *            D
     * @return C or D */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    public ConceptExpression or(ConceptExpression C, ConceptExpression D) {
        return or(Arrays.<ConceptExpression> asList(C, D));
    }

    /** @param l
     *            l
     * @return an n-ary disjunction expression; take the arguments from the last
     *         argument list */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    public ConceptExpression or(List<ConceptExpression> l) {
        return new ConceptOr(l);
    }

    /** @param l
     *            l
     * @return an n-ary one-of expression; take the arguments from the last
     *         argument list */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOf")
    public ConceptExpression oneOf(List<IndividualExpression> l) {
        if (l.size() == 1) {
            IndividualExpression i = l.get(0);
            ConceptExpression c = OneOfCache.get(i);
            if (c == null) {
                c = new ConceptOneOf<IndividualExpression>(l);
                OneOfCache.put(i, c);
            }
            return c;
        }
        return new ConceptOneOf<IndividualExpression>(l);
    }

    /** @param R
     *            R
     * @return inverse of object role expression */
    @PortedFrom(file = "tExpressionManager.h", name = "inverse")
    public ObjectRoleExpression inverse(ObjectRoleExpression R) {
        return inverseRoleCache.get(R);
    }

    /** @param I
     *            I
     * @return concept {I} for the individual I */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOf")
    public ConceptExpression oneOf(IndividualExpression I) {
        return oneOf(Arrays.<IndividualExpression> asList(I));
    }

    /** @param R
     *            R
     * @return self-reference restriction of an object role R */
    @PortedFrom(file = "tExpressionManager.h", name = "SelfReference")
    public ConceptExpression selfReference(ObjectRoleExpression R) {
        return new ConceptObjectSelf(R);
    }

    /** @param R
     *            R
     * @param I
     *            I
     * @return value restriction wrt an object role R and an individual I */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    public ConceptExpression value(ObjectRoleExpression R, IndividualExpression I) {
        return new ConceptObjectValue(R, I);
    }

    /** @param R
     *            R
     * @param C
     *            C
     * @return existential restriction wrt an object role R and a concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    public ConceptExpression exists(ObjectRoleExpression R, ConceptExpression C) {
        return new ConceptObjectExists(R, C);
    }

    /** @param R
     *            R
     * @param C
     *            C
     * @return universal restriction wrt an object role R and a concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    public ConceptExpression forall(ObjectRoleExpression R, ConceptExpression C) {
        return new ConceptObjectForall(R, C);
    }

    /** @param n
     *            n
     * @param R
     *            R
     * @param C
     *            C
     * @return min cardinality restriction wrt number N, an object role R and a
     *         concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    public ConceptExpression minCardinality(int n, ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectMinCardinality(n, R, C);
    }

    /** @param n
     *            n
     * @param R
     *            R
     * @param C
     *            C
     * @return max cardinality restriction wrt number N, an object role R and a
     *         concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    public ConceptExpression maxCardinality(int n, ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectMaxCardinality(n, R, C);
    }

    /** @param n
     *            n
     * @param R
     *            R
     * @param C
     *            C
     * @return exact cardinality restriction wrt number N, an object role R and
     *         a concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    public ConceptExpression cardinality(int n, ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectExactCardinality(n, R, C);
    }

    /** @param R
     *            R
     * @param V
     *            V
     * @return value restriction wrt a data role R and a data value V */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    public ConceptExpression value(DataRoleExpression R, Literal<?> V) {
        return new ConceptDataValue(R, V);
    }

    /** @param R
     *            R
     * @param E
     *            E
     * @return existential restriction wrt a data role R and a data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    public ConceptExpression exists(DataRoleExpression R, DataExpression E) {
        return new ConceptDataExists(R, E);
    }

    /** @param R
     *            R
     * @param E
     *            E
     * @return universal restriction wrt a data role R and a data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    public ConceptExpression forall(DataRoleExpression R, DataExpression E) {
        return new ConceptDataForall(R, E);
    }

    /** @param n
     *            n
     * @param R
     *            R
     * @param E
     *            E
     * @return min cardinality restriction wrt number N, a data role R and a
     *         data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    public ConceptExpression
            minCardinality(int n, DataRoleExpression R, DataExpression E) {
        return new ConceptDataMinCardinality(n, R, E);
    }

    /** @param n
     *            n
     * @param R
     *            R
     * @param E
     *            E
     * @return max cardinality restriction wrt number N, a data role R and a
     *         data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    public ConceptExpression
            maxCardinality(int n, DataRoleExpression R, DataExpression E) {
        return new ConceptDataMaxCardinality(n, R, E);
    }

    /** @param n
     *            n
     * @param R
     *            R
     * @param E
     *            E
     * @return exact cardinality restriction wrt number N, a data role R and a
     *         data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    public ConceptExpression cardinality(int n, DataRoleExpression R, DataExpression E) {
        return new ConceptDataExactCardinality(n, R, E);
    }

    // individuals
    /** @param name
     *            name
     * @return named individual */
    @PortedFrom(file = "tExpressionManager.h", name = "individual")
    public IndividualName individual(IRI name) {
        return individualNameset.insert(name);
    }

    // object roles
    /** @return TOP object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleTop")
    public ObjectRoleExpression objectRoleTop() {
        return objectRoleTop;
    }

    /** @return BOTTOM object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleBottom")
    public ObjectRoleExpression objectRoleBottom() {
        return objectRoleBottom;
    }

    /** @param name
     *            name
     * @return named object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRole")
    public ObjectRoleName objectRole(IRI name) {
        return objectRoleNameset.insert(name);
    }

    /** @param l
     *            l
     * @return a role chain corresponding to R1 o ... o Rn; take the arguments
     *         from the last argument list */
    @PortedFrom(file = "tExpressionManager.h", name = "Compose")
    public ObjectRoleComplexExpression compose(List<ObjectRoleExpression> l) {
        return new ObjectRoleChain(l);
    }

    /** @param e1
     *            e1
     * @param e2
     *            e2
     * @return ObjectRoleChain */
    @PortedFrom(file = "tExpressionManager.h", name = "Compose")
    public ObjectRoleComplexExpression compose(ObjectRoleExpression e1,
            ObjectRoleExpression e2) {
        return new ObjectRoleChain(Arrays.asList(e1, e2));
    }

    /** @param R
     *            R
     * @param C
     *            C
     * @return a expression corresponding to R projected from C */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectFrom")
    public ObjectRoleComplexExpression projectFrom(ObjectRoleExpression R,
            ConceptExpression C) {
        return new ObjectRoleProjectionFrom(R, C);
    }

    /** @param R
     *            R
     * @param C
     *            C
     * @return a expression corresponding to R projected into C */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectInto")
    public ObjectRoleComplexExpression projectInto(ObjectRoleExpression R,
            ConceptExpression C) {
        return new ObjectRoleProjectionInto(R, C);
    }

    // data roles
    /** @return TOP data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleTop")
    public DataRoleExpression dataRoleTop() {
        return dataRoleTop;
    }

    /** @return BOTTOM data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleBottom")
    public DataRoleExpression dataRoleBottom() {
        return dataRoleBottom;
    }

    /** @param name
     *            name
     * @return named data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRole")
    public DataRoleName dataRole(IRI name) {
        return dataRoleNameset.insert(name);
    }

    // data expressions
    /** @return TOP data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataTop")
    public DataExpression dataTop() {
        return dataTop;
    }

    /** @return BOTTOM data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataBottom")
    public DataBottom dataBottom() {
        return dataBottom;
    }

    /** @return basic string data type */
    @Original
    public String getDataTop() {
        // XXX there is no link between TDLDataTop and the uri
        return Vocabulary.LITERAL;
    }

    /** @param E
     *            E
     * @return negation of a data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "DataNot")
    public DataExpression dataNot(DataExpression E) {
        return new DataNot(E);
    }

    /** @param l
     *            l
     * @return an n-ary data conjunction expression */
    @PortedFrom(file = "tExpressionManager.h", name = "DataAnd")
    public DataExpression dataAnd(List<DataExpression> l) {
        return new DataAnd(l);
    }

    /** @param l
     *            l
     * @return an n-ary data disjunction expression */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOr")
    public DataExpression dataOr(List<DataExpression> l) {
        return new DataOr(l);
    }

    /** @param l
     *            l
     * @return an n-ary data one-of expression */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOneOf")
    public DataExpression dataOneOf(List<Literal<?>> l) {
        return new DataOneOf(l);
    }
}
