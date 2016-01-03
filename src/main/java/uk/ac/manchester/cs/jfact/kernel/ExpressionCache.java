package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

/**
 * Cache for expressions. This is the only stateful part of ExpressionManager
 */
public class ExpressionCache implements Serializable {

    /** nameset for concepts */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_C") private final NameSet<ConceptName, IRI> conceptNameset = new NameSet<>(
        name -> new ConceptName(name));
    /** nameset for individuals */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_I") private final NameSet<IndividualName, IRI> individualNameset = new NameSet<>(
        name -> new IndividualName(name));
    /** nameset for object roles */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_OR") private final NameSet<ObjectRoleName, IRI> objectRoleNameset = new NameSet<>(
        name -> new ObjectRoleName(name));
    /** nameset for data roles */
    @PortedFrom(file = "tExpressionManager.h", name = "NS_DR") private final NameSet<DataRoleName, IRI> dataRoleNameset = new NameSet<>(
        name -> new DataRoleName(name));
    /** cache for the role inverses */
    @PortedFrom(file = "tExpressionManager.h", name = "InverseRoleCache") @Nonnull private final InverseRoleCache inverseRoleCache = new InverseRoleCache();
    /** cache for the one-of singletons */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOfCache") private final Map<IndividualExpression, ConceptExpression> oneOfCache = new HashMap<>();

    /**
     * @param name
     *        name
     * @return named concept
     */
    @PortedFrom(file = "tExpressionManager.h", name = "concept")
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
    public ConceptExpression oneOf(List<IndividualExpression> l) {
        // XXX optimize these creations
        if (l.size() == 1) {
            IndividualExpression i = l.get(0);
            ConceptExpression c = oneOfCache.get(i);
            if (c == null) {
                c = new ConceptOneOf<>(l);
                oneOfCache.put(i, c);
            }
            return c;
        }
        return new ConceptOneOf<>(l);
    }

    /**
     * @param r
     *        R
     * @return inverse of object role expression
     */
    @PortedFrom(file = "tExpressionManager.h", name = "inverse")
    public ObjectRoleExpression inverse(ObjectRoleExpression r) {
        return inverseRoleCache.get(r);
    }

    /**
     * @param i
     *        I
     * @return concept {I} for the individual I
     */
    @PortedFrom(file = "tExpressionManager.h", name = "OneOf")
    public ConceptExpression oneOf(IndividualExpression i) {
        return oneOf(Arrays.<IndividualExpression> asList(i));
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
        oneOfCache.clear();
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
    public ObjectRoleName objectRole(IRI name) {
        return objectRoleNameset.insert(name);
    }

    /**
     * @param name
     *        name
     * @return named data role
     */
    @PortedFrom(file = "tExpressionManager.h", name = "DataRole")
    public DataRoleName dataRole(IRI name) {
        return dataRoleNameset.insert(name);
    }

    /** Cache for the inverse roles */
    protected static class InverseRoleCache implements Serializable {

        /** map tail into an object head(tail) */
        private Map<ObjectRoleExpression, ObjectRoleExpression> map;

        /**
         * @param tail
         *        tail
         * @return an object corresponding to Head.Tail
         */
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

        public ObjectRoleExpression build(ObjectRoleExpression tail) {
            return new ObjectRoleInverse(tail);
        }
    }
}
