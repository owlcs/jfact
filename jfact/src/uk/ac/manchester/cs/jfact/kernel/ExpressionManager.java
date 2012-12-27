package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;
import conformance.Original;
import conformance.PortedFrom;

@PortedFrom(file = "tExpressionManager.h", name = "TExpressionManager")
public class ExpressionManager {
    /** Cache for the inverse roles */
    protected class InverseRoleCache {
        /** map tail into an object head(tail) */
        private Map<ObjectRoleExpression, ObjectRoleExpression> map;

        /** get an object corresponding to Head.Tail */
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

    protected static class DataroleNameCreator implements NameCreator<DataRoleName> {
        @Override
        public DataRoleName makeEntry(String name) {
            return new DataRoleName(name);
        }
    }

    protected static class ObjectroleNameCreator implements NameCreator<ObjectRoleName> {
        @Override
        public ObjectRoleName makeEntry(String name) {
            return new ObjectRoleName(name);
        }
    }

    protected static class IndividualNameCreator implements NameCreator<IndividualName> {
        @Override
        public IndividualName makeEntry(String name) {
            return new IndividualName(name);
        }
    }

    protected static class ConceptNameCreator implements NameCreator<ConceptName> {
        @Override
        public ConceptName makeEntry(String name) {
            return new ConceptName(name);
        }
    }

    /** nameset for concepts */
    private NameSet<ConceptName> conceptNameset = new NameSet<ConceptName>(
            new ConceptNameCreator());
    /** nameset for individuals */
    private NameSet<IndividualName> individualNameset = new NameSet<IndividualName>(
            new IndividualNameCreator());
    /** nameset for object roles */
    private NameSet<ObjectRoleName> objectRoleNameset = new NameSet<ObjectRoleName>(
            new ObjectroleNameCreator());
    /** nameset for data roles */
    private NameSet<DataRoleName> dataRoleNameset = new NameSet<DataRoleName>(
            new DataroleNameCreator());
    /** TOP concept */
    private ConceptTop top = new ConceptTop();
    /** BOTTOM concept */
    private ConceptBottom bottom = new ConceptBottom();
    private DataTop DTop = new DataTop();
    /** TOP object role */
    private ObjectRoleExpression objectRoleTop = new ObjectRoleTop();
    /** BOTTOM object role */
    private ObjectRoleExpression objectRoleBottom = new ObjectRoleBottom();
    /** TOP data role */
    private DataRoleExpression dataRoleTop = new DataRoleTop();
    /** BOTTOM data role */
    private DataRoleExpression dataRoleBottom = new DataRoleBottom();
    /** TOP data element */
    private DataTop dataTop = new DataTop();
    /** BOTTOM data element */
    private DataBottom dataBottom = new DataBottom();
    /** cache for the role inverses */
    private InverseRoleCache inverseRoleCache = new InverseRoleCache();
    // / cache for the one-of singletons
    private Map<IndividualExpression, ConceptExpression> OneOfCache = new HashMap<IndividualExpression, ConceptExpression>();

    /** set Top/Bot properties */
    @PortedFrom(file = "tExpressionManager.h", name = "setTopBottomRoles")
    public void setTopBottomRoles(String topORoleName, String botORoleName,
            String topDRoleName, String botDRoleName) {
        objectRoleTop = new ObjectRoleName(topORoleName);
        objectRoleBottom = new ObjectRoleName(botORoleName);
        dataRoleTop = new DataRoleName(topDRoleName);
        dataRoleBottom = new DataRoleName(botDRoleName);
    }

    @PortedFrom(file = "tExpressionManager.h", name = "clearNameCache")
    public void clearNameCache(NameSet<?> ns) {
        ns.clear();
    }

    @PortedFrom(file = "tExpressionManager.h", name = "clearNameCache")
    public void clearNameCache() {
        clearNameCache(conceptNameset);
        clearNameCache(objectRoleNameset);
        clearNameCache(dataRoleNameset);
        clearNameCache(individualNameset);
    }

    // top/bottom roles
    /** @return true iff R is a top data/object role */
    @PortedFrom(file = "tExpressionManager.h", name = "isUniversalRole")
    public boolean isUniversalRole(RoleExpression R) {
        return R.equals(dataRoleTop) || R.equals(objectRoleTop);
    }

    /** @return true iff R is a bottom data/object role */
    @PortedFrom(file = "tExpressionManager.h", name = "isEmptyRole")
    public boolean isEmptyRole(RoleExpression R) {
        return R.equals(dataRoleBottom) || R.equals(objectRoleBottom);
    }

    /** get number of registered concepts */
    @PortedFrom(file = "tExpressionManager.h", name = "nConcepts")
    public int nConcepts() {
        return conceptNameset.size();
    }

    /** get number of registered individuals */
    @PortedFrom(file = "tExpressionManager.h", name = "nIndividuals")
    public int nIndividuals() {
        return individualNameset.size();
    }

    /** get number of registered object roles */
    @PortedFrom(file = "tExpressionManager.h", name = "nORoles")
    public int nORoles() {
        return objectRoleNameset.size();
    }

    /** get number of registered data roles */
    @PortedFrom(file = "tExpressionManager.h", name = "nDRoles")
    public int nDRoles() {
        return dataRoleNameset.size();
    }

    /** get TOP concept */
    @PortedFrom(file = "tExpressionManager.h", name = "top")
    public ConceptExpression top() {
        return top;
    }

    /** get BOTTOM concept */
    @PortedFrom(file = "tExpressionManager.h", name = "bottom")
    public ConceptExpression bottom() {
        return bottom;
    }

    /** get named concept */
    @PortedFrom(file = "tExpressionManager.h", name = "concept")
    public ConceptExpression concept(String name) {
        return conceptNameset.insert(name);
    }

    /** get negation of a concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "Not")
    public ConceptExpression not(ConceptExpression C) {
        return new ConceptNot(C);
    }

    /** get an n-ary conjunction expression; take the arguments from the last
     * argument list */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    public ConceptExpression and(List<Expression> l) {
        return new ConceptAnd(l);
    }

    /** @return C and D */
    @PortedFrom(file = "tExpressionManager.h", name = "and")
    public ConceptExpression and(ConceptExpression C, ConceptExpression D) {
        return and(Arrays.<Expression> asList(C, D));
    }

    /** @return C or D */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    public ConceptExpression or(ConceptExpression C, ConceptExpression D) {
        return or(Arrays.<Expression> asList(C, D));
    }

    /** get an n-ary disjunction expression; take the arguments from the last
     * argument list */
    @PortedFrom(file = "tExpressionManager.h", name = "or")
    public ConceptExpression or(List<Expression> l) {
        return new ConceptOr(l);
    }

    /** get an n-ary one-of expression; take the arguments from the last argument
     * list */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOf")
    public ConceptExpression oneOf(List<Expression> l) {
        if (l.size() == 1) {
            IndividualExpression i = (IndividualExpression) l.get(0);
            ConceptExpression c = OneOfCache.get(i);
            if (c == null) {
                c = new ConceptOneOf(l);
                OneOfCache.put(i, c);
            }
            return c;
        }
        return new ConceptOneOf(l);
    }

    @PortedFrom(file = "tExpressionManager.h", name = "inverse")
    public ObjectRoleExpression inverse(ObjectRoleExpression R) {
        return inverseRoleCache.get(R);
    }

    /** @return concept {I} for the individual I */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOf")
    public ConceptExpression oneOf(IndividualExpression I) {
        return oneOf(Arrays.<Expression> asList(I));
    }

    /** get self-reference restriction of an object role R */
    @PortedFrom(file = "tExpressionManager.h", name = "SelfReference")
    public ConceptExpression selfReference(ObjectRoleExpression R) {
        return new ConceptObjectSelf(R);
    }

    /** get value restriction wrt an object role R and an individual I */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    public ConceptExpression value(ObjectRoleExpression R, IndividualExpression I) {
        return new ConceptObjectValue(R, I);
    }

    /** get existential restriction wrt an object role R and a concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    public ConceptExpression exists(ObjectRoleExpression R, ConceptExpression C) {
        return new ConceptObjectExists(R, C);
    }

    /** get universal restriction wrt an object role R and a concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    public ConceptExpression forall(ObjectRoleExpression R, ConceptExpression C) {
        return new ConceptObjectForall(R, C);
    }

    /** get min cardinality restriction wrt number N, an object role R and a
     * concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    public ConceptExpression minCardinality(int n, ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectMinCardinality(n, R, C);
    }

    /** get max cardinality restriction wrt number N, an object role R and a
     * concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    public ConceptExpression maxCardinality(int n, ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectMaxCardinality(n, R, C);
    }

    /** get exact cardinality restriction wrt number N, an object role R and a
     * concept C */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    public ConceptExpression cardinality(int n, ObjectRoleExpression R,
            ConceptExpression C) {
        return new ConceptObjectExactCardinality(n, R, C);
    }

    /** get value restriction wrt a data role R and a data value V */
    @PortedFrom(file = "tExpressionManager.h", name = "value")
    public ConceptExpression value(DataRoleExpression R, Literal<?> V) {
        return new ConceptDataValue(R, V);
    }

    /** get existential restriction wrt a data role R and a data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "Exists")
    public ConceptExpression exists(DataRoleExpression R, DataExpression E) {
        return new ConceptDataExists(R, E);
    }

    /** get universal restriction wrt a data role R and a data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "Forall")
    public ConceptExpression forall(DataRoleExpression R, DataExpression E) {
        return new ConceptDataForall(R, E);
    }

    /** get min cardinality restriction wrt number N, a data role R and a data
     * expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "MinCardinality")
    public ConceptExpression
            minCardinality(int n, DataRoleExpression R, DataExpression E) {
        return new ConceptDataMinCardinality(n, R, E);
    }

    /** get max cardinality restriction wrt number N, a data role R and a data
     * expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "MaxCardinality")
    public ConceptExpression
            maxCardinality(int n, DataRoleExpression R, DataExpression E) {
        return new ConceptDataMaxCardinality(n, R, E);
    }

    /** get exact cardinality restriction wrt number N, a data role R and a data
     * expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "cardinality")
    public ConceptExpression cardinality(int n, DataRoleExpression R, DataExpression E) {
        return new ConceptDataExactCardinality(n, R, E);
    }

    // individuals
    /** get named individual */
    @PortedFrom(file = "tExpressionManager.h", name = "individual")
    public IndividualExpression individual(String name) {
        return individualNameset.insert(name);
    }

    // object roles
    /** get TOP object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleTop")
    public ObjectRoleExpression objectRoleTop() {
        return objectRoleTop;
    }

    /** get BOTTOM object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRoleBottom")
    public ObjectRoleExpression objectRoleBottom() {
        return objectRoleBottom;
    }

    /** get named object role */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRole")
    public ObjectRoleExpression objectRole(String name) {
        return objectRoleNameset.insert(name);
    }

    /** get a role chain corresponding to R1 o ... o Rn; take the arguments from
     * the last argument list */
    @PortedFrom(file = "tExpressionManager.h", name = "Compose")
    public ObjectRoleComplexExpression compose(List<Expression> l) {
        return new ObjectRoleChain(l);
    }

    @PortedFrom(file = "tExpressionManager.h", name = "Compose")
    public ObjectRoleComplexExpression compose(Expression e1, Expression e2) {
        return new ObjectRoleChain(Arrays.asList(e1, e2));
    }

    /** get a expression corresponding to R projected from C */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectFrom")
    public ObjectRoleComplexExpression projectFrom(ObjectRoleExpression R,
            ConceptExpression C) {
        return new ObjectRoleProjectionFrom(R, C);
    }

    /** get a expression corresponding to R projected into C */
    @PortedFrom(file = "tExpressionManager.h", name = "ProjectInto")
    public ObjectRoleComplexExpression projectInto(ObjectRoleExpression R,
            ConceptExpression C) {
        return new ObjectRoleProjectionInto(R, C);
    }

    // data roles
    /** get TOP data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleTop")
    public DataRoleExpression dataRoleTop() {
        return dataRoleTop;
    }

    /** get BOTTOM data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRoleBottom")
    public DataRoleExpression dataRoleBottom() {
        return dataRoleBottom;
    }

    /** get named data role */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRole")
    public DataRoleExpression dataRole(String name) {
        return dataRoleNameset.insert(name);
    }

    // data expressions
    /** get TOP data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataTop")
    public DataExpression dataTop() {
        return dataTop;
    }

    /** get BOTTOM data element */
    @PortedFrom(file = "tExpressionManager.h", name = "DataBottom")
    public DataExpression dataBottom() {
        return dataBottom;
    }

    /** get basic string data type */
    @Original
    public String getDataTop() {
        // XXX there is no link between TDLDataTop and the uri
        return Vocabulary.LITERAL;
    }

    /** get negation of a data expression E */
    @PortedFrom(file = "tExpressionManager.h", name = "DataNot")
    public DataExpression dataNot(DataExpression E) {
        return new DataNot(E);
    }

    /** get an n-ary data conjunction expression */
    @PortedFrom(file = "tExpressionManager.h", name = "DataAnd")
    public DataExpression dataAnd(List<Expression> l) {
        return new DataAnd(l);
    }

    /** get an n-ary data disjunction expression */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOr")
    public DataExpression dataOr(List<Expression> l) {
        return new DataOr(l);
    }

    /** get an n-ary data one-of expression */
    @PortedFrom(file = "tExpressionManager.h", name = "DataOneOf")
    public DataExpression dataOneOf(List<Expression> l) {
        return new DataOneOf(l);
    }

    @PortedFrom(file = "tExpressionManager.h", name = "clear")
    public void clear() {
        conceptNameset.clear();
        individualNameset.clear();
        objectRoleNameset.clear();
        dataRoleNameset.clear();
        inverseRoleCache.clear();
        OneOfCache.clear();
    }
}
