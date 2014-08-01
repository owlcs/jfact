package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptOneOf;
import uk.ac.manchester.cs.jfact.kernel.dl.DataRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.ObjectRoleName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import conformance.PortedFrom;

/**
 * Cache for expressions. This is the only stateful part of ExpressionManager
 */
public class ExpressionCache {

    /** nameset for concepts */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_C")
    private final NameSet<ConceptName, IRI> conceptNameset = new NameSet<>(
            new ConceptNameCreator());
    /** nameset for individuals */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_I")
    private final NameSet<IndividualName, IRI> individualNameset = new NameSet<>(
            new IndividualNameCreator());
    /** nameset for object roles */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_OR")
    private final NameSet<ObjectRoleName, IRI> objectRoleNameset = new NameSet<>(
            new ObjectroleNameCreator());
    /** nameset for data roles */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_DR")
    private final NameSet<DataRoleName, IRI> dataRoleNameset = new NameSet<>(
            new DataroleNameCreator());
    /** cache for the role inverses */
    @PortedFrom(file = "tExpressionManager.h", name = "InverseRoleCache")
    @Nonnull
    private final InverseRoleCache inverseRoleCache = new InverseRoleCache();
    /** cache for the one-of singletons */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOfCache")
    private final Map<IndividualExpression, ConceptExpression> OneOfCache = new HashMap<>();

    /**
     * @param name
     *        name
     * @return named concept
     */
    @PortedFrom(file = "tExpressionManager.h", name = "concept")
    @Nonnull
    public ConceptName concept(IRI name) {
        return conceptNameset.insert(name);
    }

    /**
     * @param l
     *        l
     * @return an n-ary one-of expression; take the arguments from the last
     *         argument list
     */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOf")
    @Nonnull
    public ConceptExpression oneOf(List<IndividualExpression> l) {
        // XXX optimize these creations
        if (l.size() == 1) {
            IndividualExpression i = l.get(0);
            ConceptExpression c = OneOfCache.get(i);
            if (c == null) {
                c = new ConceptOneOf<>(l);
                OneOfCache.put(i, c);
            }
            return c;
        }
        return new ConceptOneOf<>(l);
    }

    /**
     * @param R
     *        R
     * @return inverse of object role expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "inverse")
    @Nonnull
    public ObjectRoleExpression inverse(ObjectRoleExpression R) {
        return inverseRoleCache.get(R);
    }

    /**
     * @param I
     *        I
     * @return concept {I} for the individual I
     */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOf")
    @Nonnull
    public ConceptExpression oneOf(IndividualExpression I) {
        return oneOf(Arrays.<IndividualExpression> asList(I));
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

    // individuals
    /**
     * @param name
     *        name
     * @return named individual
     */
    @PortedFrom(file = "tExpressionManager.h", name = "individual")
    @Nonnull
    public IndividualName individual(IRI name) {
        return individualNameset.insert(name);
    }

    /**
     * @param name
     *        name
     * @return named object role
     */
    @PortedFrom(file = "tExpressionManager.h", name = "ObjectRole")
    @Nonnull
    public ObjectRoleName objectRole(IRI name) {
        return objectRoleNameset.insert(name);
    }

    /**
     * @param name
     *        name
     * @return named data role
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRole")
    @Nonnull
    public DataRoleName dataRole(IRI name) {
        return dataRoleNameset.insert(name);
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

    protected static class ConceptNameCreator implements
            NameCreator<ConceptName, IRI>, Serializable {

        private static final long serialVersionUID = 11000L;

        @Override
        public ConceptName makeEntry(IRI name) {
            return new ConceptName(name);
        }
    }

    /** Cache for the inverse roles */
    protected class InverseRoleCache implements Serializable {

        private static final long serialVersionUID = 11000L;
        /** map tail into an object head(tail) */
        private Map<ObjectRoleExpression, ObjectRoleExpression> map;

        /**
         * @param tail
         *        tail
         * @return an object corresponding to Head.Tail
         */
        @Nonnull
        public ObjectRoleExpression get(ObjectRoleExpression tail) {
            // try to find cached dep-set
            ObjectRoleExpression concat = null;
            if (map != null) {
                concat = map.get(tail);
            }
            if (concat != null) {
                return concat;
            }
            // no cached entry -- create a new one and cache it
            concat = build(tail);
            if (map == null) {
                map = new HashMap<>();
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

        @Nonnull
        public ObjectRoleExpression build(ObjectRoleExpression tail) {
            return new ObjectRoleInverse(tail);
        }
    }

    protected static class DataroleNameCreator implements
            NameCreator<DataRoleName, IRI>, Serializable {

        private static final long serialVersionUID = 11000L;

        @Override
        public DataRoleName makeEntry(IRI name) {
            return new DataRoleName(name);
        }
    }
}
