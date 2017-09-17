package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.DLVertex;
import uk.ac.manchester.cs.jfact.helpers.Helper;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;

/** class to translate DAG entities into the TDL* expressions */
@PortedFrom(file = "tDag2Interface.h", name = "TDag2Interface")
public class TDag2Interface implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** DAG to be translated */
    @PortedFrom(file = "tDag2Interface.h", name = "Dag")
    private final DLDag Dag;
    /** expression manager */
    @PortedFrom(file = "tDag2Interface.h", name = "Manager")
    private final ExpressionCache cache;
    /** vector of cached expressions */
    @PortedFrom(file = "tDag2Interface.h", name = "TransC")
    private final List<ConceptExpression> TransConcept = new ArrayList<>();
    @PortedFrom(file = "tDag2Interface.h", name = "TransD")
    private final List<DataExpression> TransData = new ArrayList<>();

    /**
     * @param v
     *        v
     * @return build concept expression by a vertex V
     */
    @PortedFrom(file = "tDag2Interface.h", name = "buildCExpr")
    @SuppressWarnings("incomplete-switch")
    public ConceptExpression buildCExpr(DLVertex v) {
        switch (v.getType()) {
            case dtTop:
                return top();
            case dtNConcept:
            case dtPConcept:
                return cache.concept(v.getConcept().getName());
            case dtPSingleton:
            case dtNSingleton:
                return cache.oneOf(cache.individual(v.getConcept().getName()));
            case dtAnd:
            case dtCollection:
                List<ConceptExpression> list = new ArrayList<>();
                for (int p : v.begin()) {
                    list.add(getCExpr(p));
                }
                return and(cache, list);
            case dtForall:
                if (v.getRole().isDataRole()) {
                    return forall(cache, cache.dataRole(v.getRole().getName()),
                            getDExpr(v.getConceptIndex()));
                } else {
                    return forall(cache, cache.objectRole(v.getRole().getName()),
                            getCExpr(v.getConceptIndex()));
                }
            case dtLE:
                if (v.getRole().isDataRole()) {
                    return maxCardinality(cache, v.getNumberLE(),
                            cache.dataRole(v.getRole().getName()),
                            getDExpr(v.getConceptIndex()));
                } else {
                    return maxCardinality(cache, v.getNumberLE(),
                            cache.objectRole(v.getRole().getName()),
                            getCExpr(v.getConceptIndex()));
                }
            case dtIrr:
                return not(cache, selfReference(cache, cache
                        .objectRole(v.getRole().getName())));
            case dtProj:
            case dtNN:
            case dtChoose:
                // these are artificial constructions and shouldn't be visible
                return top();
            default:
                throw new UnreachableSituationException();
        }
    }

    /**
     * @param v
     *        v
     * @return build data expression by a vertex V
     */
    @PortedFrom(file = "tDag2Interface.h", name = "buildDExpr")
    @SuppressWarnings("incomplete-switch")
    public DataExpression buildDExpr(DLVertex v) {
        switch (v.getType()) {
            case dtTop:
                return dataTop();
            case dtDataType:
            case dtDataValue:
            case dtDataExpr: // TODO: no data stuff yet
                return dataTop();
            case dtAnd:
            case dtCollection:
                List<DataExpression> list = new ArrayList<>();
                for (int p : v.begin()) {
                    list.add(getDExpr(p));
                }
                return dataAnd(cache, list);
            default:
                throw new UnreachableSituationException();
        }
    }

    /**
     * init c'tor
     * 
     * @param dag
     *        dag
     * @param manager
     *        manager
     */
    public TDag2Interface(DLDag dag, ExpressionCache manager) {
        Dag = dag;
        cache = manager;
        Helper.resize(TransConcept, dag.size());
        Helper.resize(TransData, dag.size());
    }

    /**
     * @param r
     *        r
     * @return data role expression
     */
    @Original
    public RoleExpression getDataRoleExpression(Role r) {
        return cache.dataRole(r.getName());
    }

    /**
     * @param r
     *        r
     * @return object role expression
     */
    @Original
    public RoleExpression getObjectRoleExpression(Role r) {
        return cache.objectRole(r.getName());
    }

    /** make sure that size of expression cache is the same as the size of a DAG */
    @PortedFrom(file = "tDag2Interface.h", name = "ensureDagSize")
    public void ensureDagSize() {
        int ds = Dag.size(), ts = TransConcept.size();
        if (ds == ts) {
            return;
        }
        Helper.resize(TransConcept, ds);
        Helper.resize(TransData, ds);
    }

    /**
     * @param p
     *        p
     * @return concept expression corresponding index of vertex
     */
    @PortedFrom(file = "tDag2Interface.h", name = "getCExpr")
    public ConceptExpression getCExpr(int p) {
        if (p < 0) {
            return not(cache, getCExpr(-p));
        }
        if (TransConcept.get(p) == null) {
            TransConcept.set(p, buildCExpr(Dag.get(p)));
        }
        return TransConcept.get(p);
    }

    /**
     * @param p
     *        p
     * @return data expression corresponding index of vertex
     */
    @PortedFrom(file = "tDag2Interface.h", name = "getDExpr")
    public DataExpression getDExpr(int p) {
        if (p < 0) {
            return dataNot(cache, getDExpr(-p));
        }
        DataExpression expression = TransData.get(p);
        if (expression == null) {
            expression = buildDExpr(Dag.get(p));
            TransData.set(p, expression);
        }
        return expression;
    }

    /**
     * @param p
     *        p
     * @param data
     *        data
     * @return expression
     */
    @PortedFrom(file = "tDag2Interface.h", name = "getExpr")
    public Expression getExpr(int p, boolean data) {
        if (data) {
            return getDExpr(p);
        } else {
            return getCExpr(p);
        }
    }
}
