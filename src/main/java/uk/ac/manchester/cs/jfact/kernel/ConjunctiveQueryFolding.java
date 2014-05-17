package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.kernel.TBox.IterableElem;
import uk.ac.manchester.cs.jfact.kernel.actors.ActorImpl;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRAtom;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRConceptAtom;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRQuery;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRRoleAtom;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRVariable;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.VariableFactory;
import conformance.Original;
import conformance.PortedFrom;

/** conjunctive query folding */
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ConjunctiveQueryFolding")
public class ConjunctiveQueryFolding implements Serializable {

    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "pEM")
    private final ExpressionManager pEM;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "VarFact")
    private final VariableFactory VarFact = new VariableFactory();
    /** map between new vars and original vars */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "NewVarMap")
    private final Map<QRVariable, QRVariable> NewVarMap = new HashMap<QRVariable, QRVariable>();
    /** query to term transformation support */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "NewNominals")
    private final Set<ConceptExpression> NewNominals = new HashSet<ConceptExpression>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "VarRestrictions")
    private final Map<IRI, ConceptExpression> VarRestrictions = new HashMap<IRI, ConceptExpression>();

    /**
     * @param em
     *        expression manager
     */
    public ConjunctiveQueryFolding(ExpressionManager em) {
        pEM = em;
    }

    /**
     * @param expr
     *        expr
     * @return true if expr is nominal
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "isNominal")
    public boolean isNominal(ConceptExpression expr) {
        return NewNominals.contains(expr);
    }

    /**
     * @param concept
     *        concept
     */
    @Original
    public void addNominal(ConceptExpression concept) {
        NewNominals.add(concept);
    }

    /**
     * @param query
     *        query
     * @return concept removal
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "RemoveCFromQuery")
    public
            QRQuery RemoveCFromQuery(QRQuery query) {
        // init VR with \top for all free vars
        VarRestrictions.clear();
        QRQuery ret = new QRQuery();
        for (QRVariable v : query.getFreeVars()) {
            ret.setVarFree(v);
            VarRestrictions.put(v.getName(), pEM.top());
        }
        // System.out.println("Remove C atoms from the query: before\n" +
        // query);
        // remove C(v) for free vars
        for (QRAtom p : query.getBody().begin()) {
            if (p instanceof QRConceptAtom) {
                QRConceptAtom atom = (QRConceptAtom) p;
                ConceptExpression C = atom.getConcept();
                if (atom.getArg() instanceof QRVariable
                        && query.isFreeVar((QRVariable) atom.getArg())) {
                    QRVariable var = (QRVariable) atom.getArg();
                    VarRestrictions.put(var.getName(),
                            pEM.and(C, VarRestrictions.get(var.getName())));
                } else {
                    ret.addAtom(atom);
                }
            } else {
                ret.addAtom(p);
            }
        }
        // System.out.println("after\n" + ret);
        return ret;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildQueryFigure2")
    private
            void buildQueryFigure2(QRQuery query) {
        QRVariable x = VarFact.getNewVar(IRI.create("urn:test#x"));
        QRVariable y = VarFact.getNewVar(IRI.create("urn:test#y"));
        QRVariable z = VarFact.getNewVar(IRI.create("urn:test#z"));
        QRVariable w = VarFact.getNewVar(IRI.create("urn:test#v"));
        query.setVarFree(x);
        query.setVarFree(y);
        ObjectRoleExpression R1 = pEM.objectRole(IRI.create("urn:test#R1"));
        ObjectRoleExpression R2 = pEM.objectRole(IRI.create("urn:test#R2"));
        ObjectRoleExpression R3 = pEM.objectRole(IRI.create("urn:test#R3"));
        ObjectRoleExpression R4 = pEM.objectRole(IRI.create("urn:test#R4"));
        ObjectRoleExpression R5 = pEM.objectRole(IRI.create("urn:test#R5"));
        ObjectRoleExpression R6 = pEM.objectRole(IRI.create("urn:test#R6"));
        query.addAtom(new QRRoleAtom(R1, x, z));
        query.addAtom(new QRRoleAtom(R2, x, w));
        query.addAtom(new QRRoleAtom(R3, z, y));
        query.addAtom(new QRRoleAtom(R4, y, w));
        query.addAtom(new QRRoleAtom(R5, z, w));
        query.addAtom(new QRRoleAtom(R6, y, y));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildSimpleQuery")
    private
            void buildSimpleQuery(QRQuery query) {
        QRVariable x = VarFact.getNewVar(IRI.create("urn:test#x"));
        QRVariable y = VarFact.getNewVar(IRI.create("urn:test#y"));
        query.setVarFree(x);
        query.setVarFree(y);
        ObjectRoleExpression R1 = pEM.objectRole(IRI.create("urn:test#R1"));
        ObjectRoleExpression R2 = pEM.objectRole(IRI.create("urn:test#R2"));
        query.addAtom(new QRRoleAtom(R1, x, y));
        query.addAtom(new QRRoleAtom(R2, y, x));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildVerySimpleQuery")
    private
            void buildVerySimpleQuery(QRQuery query) {
        QRVariable x = VarFact.getNewVar(IRI.create("urn:test#x"));
        query.setVarFree(x);
        ObjectRoleExpression R1 = pEM.objectRole(IRI.create("urn:test#R1"));
        query.addAtom(new QRRoleAtom(R1, x, x));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildVerySimpleQueryLUBM1")
    private
            void buildVerySimpleQueryLUBM1(QRQuery query) {
        QRVariable x = VarFact.getNewVar(IRI.create("urn:test#x"));
        query.setVarFree(x);
        QRVariable y = VarFact.getNewVar(IRI.create("urn:test#y"));
        query.setVarFree(y);
        ObjectRoleExpression R1 = pEM.objectRole(IRI.create("urn:test#R1"));
        ConceptExpression C1 = pEM.concept(IRI.create("urn:test#C1"));
        query.addAtom(new QRRoleAtom(R1, x, y));
        query.addAtom(new QRConceptAtom(C1, x));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildLUBM2Query")
    private void buildLUBM2Query(int n, QRQuery query) {
        if (n == 1) {
            QRVariable v0 = VarFact.getNewVar(IRI.create("urn:test#v0"));
            QRVariable v1 = VarFact.getNewVar(IRI.create("urn:test#v1"));
            QRVariable v2 = VarFact.getNewVar(IRI.create("urn:test#v2"));
            QRVariable v3 = VarFact.getNewVar(IRI.create("urn:test#v3"));
            query.setVarFree(v0);
            query.setVarFree(v2);
            ConceptExpression Student = pEM.concept(IRI
                    .create("urn:test#Student"));
            ConceptExpression Course = pEM.concept(IRI
                    .create("urn:test#Course"));
            ConceptExpression Faculty = pEM.concept(IRI
                    .create("urn:test#Faculty"));
            ConceptExpression Department = pEM.concept(IRI
                    .create("urn:test#Department"));
            ObjectRoleExpression takesCourse = pEM.objectRole(IRI
                    .create("urn:test#takesCourse"));
            ObjectRoleExpression teacherOf = pEM.objectRole(IRI
                    .create("urn:test#teacherOf"));
            ObjectRoleExpression worksFor = pEM.objectRole(IRI
                    .create("urn:test#worksFor"));
            ObjectRoleExpression memberOf = pEM.objectRole(IRI
                    .create("urn:test#memberOf"));
            query.addAtom(new QRConceptAtom(Student, v0));
            query.addAtom(new QRConceptAtom(Course, v1));
            query.addAtom(new QRConceptAtom(Faculty, v2));
            query.addAtom(new QRConceptAtom(Department, v3));
            query.addAtom(new QRRoleAtom(takesCourse, v0, v1));
            query.addAtom(new QRRoleAtom(teacherOf, v2, v1));
            query.addAtom(new QRRoleAtom(worksFor, v2, v3));
            query.addAtom(new QRRoleAtom(memberOf, v0, v3));
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "createQuery")
    private QRQuery createQuery() {
        QRQuery query = new QRQuery();
        buildLUBM2Query(1, query);
        return query;
    }

    /**
     * support for query decycling
     * 
     * @param query
     *        query
     * @param atomIterator
     *        atomIterator
     * @param newAtom
     *        newAtom
     * @param newArg
     *        newArg
     * @param passedAtoms
     *        passedAtoms
     * @return true if connected
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PossiblyReplaceAtom")
    private
            boolean PossiblyReplaceAtom(QRQuery query, int atomIterator,
                    QRAtom newAtom, QRVariable newArg, Set<QRAtom> passedAtoms) {
        // System.out.println("Modified code starts here!\nBefore replacing in copy.\n"
        // + query);
        QRAtom oldAtom = query.getBody().replaceAtom(atomIterator, newAtom);
        query.setVarFree(newArg);
        // System.out.println("Running Checker");
        QueryConnectednessChecker checker = new QueryConnectednessChecker(query);
        boolean ret;
        if (checker.isConnected()) {
            // System.out.println("Connected\nAfter replacing in Query\n" +
            // query);
            ret = true;
        } else {
            // System.out.println("Disconnected");
            // restore the old query
            newAtom = oldAtom;
            oldAtom = query.getBody().replaceAtom(atomIterator, oldAtom);
            query.getFreeVars().remove(newArg);
            ret = false;
        }
        passedAtoms.add(newAtom);
        return ret;
    }

    /**
     * init vars map
     * 
     * @param query
     *        query
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "initVarMap")
    private void initVarMap(QRQuery query) {
        NewVarMap.clear();
        for (QRAtom p : query.getBody().begin()) {
            if (p instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) p;
                if (atom.getArg1() instanceof QRVariable) {
                    NewVarMap.put((QRVariable) atom.getArg1(),
                            (QRVariable) atom.getArg1());
                }
                if (atom.getArg2() instanceof QRVariable) {
                    NewVarMap.put((QRVariable) atom.getArg2(),
                            (QRVariable) atom.getArg2());
                }
            }
        }
    }

    /**
     * create a new var which is a copy of an existing one
     * 
     * @param old
     *        old
     * @param suffix
     *        suffix
     * @return new var
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QRVariable")
    private QRVariable getNewCopyVar(QRVariable old, int suffix) {
        String buf = "_" + suffix;
        QRVariable var = VarFact.getNewVar(IRI.create(old.getName() + buf));
        NewVarMap.put(var, old);
        return var;
    }

    /**
     * @param query
     *        query
     * @return transformed query
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "transformQueryPhase1")
    public
            QRQuery transformQueryPhase1(QRQuery query) {
        Set<QRAtom> passedAtoms = new HashSet<QRAtom>();
        int n = 0;
        // remove C's
        query = RemoveCFromQuery(query);
        // clear the map and make identities
        initVarMap(query);
        // System.out.println("Phase 1 starts");
        List<QRAtom> body = query.getBody().begin();
        for (int i = 0; i < body.size(); i++) {
            QRRoleAtom atom = null;
            if (body.get(i) instanceof QRRoleAtom) {
                atom = (QRRoleAtom) body.get(i);
            }
            // atom is a new role atom
            if (atom == null || passedAtoms.contains(atom)) {
                continue;
            }
            ObjectRoleExpression role = atom.getRole();
            QRVariable arg1 = (QRVariable) atom.getArg1();
            QRVariable arg2 = (QRVariable) atom.getArg2();
            if (query.getFreeVars().contains(arg2)) {
                QRVariable newArg = getNewCopyVar(arg2, ++n);
                QRAtom newAtom = new QRRoleAtom(role, arg1, newArg);
                if (PossiblyReplaceAtom(query, i, newAtom, newArg, passedAtoms)) {
                    continue;
                }
            } else if (query.getFreeVars().contains(arg1)) {
                QRVariable newArg = getNewCopyVar(arg1, ++n);
                QRAtom newAtom = new QRRoleAtom(role, newArg, arg2);
                if (PossiblyReplaceAtom(query, i, newAtom, newArg, passedAtoms)) {
                    continue;
                }
            }
        }
        return query;
    }

    /**
     * @param query
     *        query
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "deleteFictiveVariables")
    public
            void deleteFictiveVariables(QRQuery query) {
        Set<QRVariable> RealFreeVars = new TreeSet<QRVariable>();
        for (QRAtom atomIterator : query.getBody().begin()) {
            if (atomIterator instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) atomIterator;
                QRVariable arg1 = (QRVariable) atom.getArg1();
                QRVariable arg2 = (QRVariable) atom.getArg2();
                if (query.isFreeVar(arg1)) {
                    RealFreeVars.add(arg1);
                }
                if (query.isFreeVar(arg2)) {
                    RealFreeVars.add(arg2);
                }
            }
        }
        query.setFreeVars(RealFreeVars);
    }

    /**
     * @param v
     *        v
     * @return concept for var
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "createConceptByVar")
    public
            ConceptExpression createConceptByVar(QRVariable v) {
        return VarRestrictions.get(NewVarMap.get(v).getName());
    }

    /**
     * @param query
     *        query
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "BuildAproximation")
    public
            void buildApproximation(QRQuery query) {
        QueryApproximation app = new QueryApproximation(this, query);
        Map<QRVariable, ConceptExpression> approx = new HashMap<QRVariable, ConceptExpression>();
        for (QRVariable p : NewVarMap.values()) {
            approx.put(p, pEM.top());
        }
        for (QRVariable v : query.getFreeVars()) {
            QRVariable var = NewVarMap.get(v);
            approx.put(var,
                    pEM.and(approx.get(var), app.Assign(query, null, v)));
        }
        for (Map.Entry<QRVariable, ConceptExpression> e : approx.entrySet()) {
            VarRestrictions.put(
                    e.getKey().getName(),
                    pEM.and(VarRestrictions.get(e.getKey().getName()),
                            e.getValue()));
        }
    }

    /**
     * @param query
     *        query
     * @return concept expression from query
     */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "transformQueryPhase2")
    public
            ConceptExpression transformQueryPhase2(QRQuery query) {
        NewNominals.clear();
        TermAssigner assigner = new TermAssigner(this, query);
        deleteFictiveVariables(query);
        QRVariable next = query.getFreeVars().iterator().next();
        // System.out.println("Assigner initialised; var: " + next);
        return assigner.Assign(query, null, next);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "doQuery")
    private void doQuery(QRQuery query, ReasoningKernel kernel,
            boolean artificialaBox) {
        TQueryToConceptsTransformer transformer = new TQueryToConceptsTransformer(
                this, query);
        transformer.Run();
        transformer.printResult();
        kernel.evaluateQuery(transformer.getResult(), artificialaBox);
    }

    private void
            runQueries(ConjunctiveQuerySet queries, ReasoningKernel kernel) {
        for (int i = 0; i < queries.size(); i++) {
            doQuery(queries.get(i), kernel, queries.isArtificialABox());
        }
    }

    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "Var2I")
    private final Map<IRI, Integer> Var2I = new HashMap<IRI, Integer>();
    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "I2Var")
    private final List<IRI> I2Var = new ArrayList<IRI>();

    /**
     * fills in variable index
     * 
     * @param query
     *        query
     */
    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "fillVarIndex")
    private void fillVarIndex(MultiMap<IRI, ConceptExpression> query) {
        int n = 0;
        Var2I.clear();
        I2Var.clear();
        for (IRI p : query.keySet()) {
            if (!Var2I.containsKey(p)) {
                // new name
                Var2I.put(p, n++);
                I2Var.add(p);
            }
        }
        assert I2Var.size() == n;
    }

    /**
     * @param query
     *        query
     * @param kernel
     *        kernel
     * @param artificialABox
     *        true if artificial abox
     */
    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "evaluateQuery")
    public void evaluateQuery(MultiMap<IRI, ConceptExpression> query,
            ReasoningKernel kernel, boolean artificialABox) {
        // make index of all vars
        fillVarIndex(query);
        if (I2Var.isEmpty()) {
            return;
        }
        // for every var: create an expression of vars
        List<DLTree> Concepts = new ArrayList<DLTree>();
        for (int i = 0; i < I2Var.size(); ++i) {
            IRI var = I2Var.get(i);
            List<ConceptExpression> list = new ArrayList<ConceptExpression>(
                    query.get(var));
            Concepts.add(kernel.e(pEM.and(list)));
        }
        fillIVec(kernel, artificialABox);
        kernel.getTBox().answerQuery(Concepts);
    }

    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "getABoxInstances")
    private void getABoxInstances(ReasoningKernel kernel, ConceptExpression C,
            boolean artificialABox) {
        // get all instances of C
        ActorImpl a = new ActorImpl();
        List<Individual> individuals = new ArrayList<Individual>();
        if (artificialABox) {
            // HACK: work only for our individualisation of NCIt/etc
            a.needConcepts();
            kernel.getConcepts(C, false, a, false);
            for (ClassifiableEntry p : a.getElements1D()) {
                IndividualName ind = pEM.individual(p.getName());
                individuals.add((Individual) ind.getEntry());
            }
        } else {
            a.needIndividuals();
            kernel.getInstances(C, a);
            for (ClassifiableEntry p : a.getElements1D()) {
                individuals.add((Individual) p);
            }
        }
        kernel.getTBox().getIV().add(new IterableElem<Individual>(individuals));
    }

    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "fillIVec")
    private void fillIVec(ReasoningKernel kernel, boolean artificialABox) {
        kernel.getTBox().getIV().clear();
        for (int i = 0; i < I2Var.size(); i++) {
            // The i'th var is I2Var[i]; get its concept
            ConceptExpression C = VarRestrictions.get(I2Var.get(i));
            getABoxInstances(kernel, C, artificialABox);
        }
    }

    /** @return expression manager */
    public ExpressionManager getpEM() {
        return pEM;
    }

    /** @return var map */
    public Map<QRVariable, QRVariable> getNewVarMap() {
        return NewVarMap;
    }
}
