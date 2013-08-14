package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.*;

import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import uk.ac.manchester.cs.jfact.kernel.TBox.IterableElem;
import uk.ac.manchester.cs.jfact.kernel.actors.ActorImpl;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.*;
import conformance.Original;
import conformance.PortedFrom;

/** conjunctive query folding */
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ConjunctiveQueryFolding")
public class ConjunctiveQueryFolding implements Serializable {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "pEM")
    private final ExpressionManager pEM = new ExpressionManager();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "VarFact")
    private final VariableFactory VarFact = new VariableFactory();
    /** map between new vars and original vars */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "NewVarMap")
    private final Map<QRVariable, QRVariable> NewVarMap = new HashMap<QRVariable, QRVariable>();
    /** query to term transformation support */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "NewNominals")
    private final Set<ConceptExpression> NewNominals = new HashSet<ConceptExpression>();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "VarRestrictions")
    private final Map<String, ConceptExpression> VarRestrictions = new HashMap<String, ConceptExpression>();

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "isNominal")
    public boolean isNominal(ConceptExpression expr) {
        return NewNominals.contains(expr);
    }

    @Original
    public void addNominal(ConceptExpression concept) {
        NewNominals.add(concept);
    }

    /** @param query
     * @return concept removal */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "RemoveCFromQuery")
    public QRQuery RemoveCFromQuery(QRQuery query) {
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
                    ret.addAtom(atom.clone());
                }
            } else {
                ret.addAtom(p.clone());
            }
        }
        // System.out.println("after\n" + ret);
        return ret;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildQueryFigure2")
    private void buildQueryFigure2(QRQuery query) {
        QRVariable x = VarFact.getNewVar("x");
        QRVariable y = VarFact.getNewVar("y");
        QRVariable z = VarFact.getNewVar("z");
        QRVariable w = VarFact.getNewVar("v");
        query.setVarFree(x);
        query.setVarFree(y);
        ObjectRoleExpression R1 = pEM.objectRole("R1");
        ObjectRoleExpression R2 = pEM.objectRole("R2");
        ObjectRoleExpression R3 = pEM.objectRole("R3");
        ObjectRoleExpression R4 = pEM.objectRole("R4");
        ObjectRoleExpression R5 = pEM.objectRole("R5");
        ObjectRoleExpression R6 = pEM.objectRole("R6");
        query.addAtom(new QRRoleAtom(R1, x, z));
        query.addAtom(new QRRoleAtom(R2, x, w));
        query.addAtom(new QRRoleAtom(R3, z, y));
        query.addAtom(new QRRoleAtom(R4, y, w));
        query.addAtom(new QRRoleAtom(R5, z, w));
        query.addAtom(new QRRoleAtom(R6, y, y));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildSimpleQuery")
    private void buildSimpleQuery(QRQuery query) {
        QRVariable x = VarFact.getNewVar("x");
        QRVariable y = VarFact.getNewVar("y");
        query.setVarFree(x);
        query.setVarFree(y);
        ObjectRoleExpression R1 = pEM.objectRole("R1");
        ObjectRoleExpression R2 = pEM.objectRole("R2");
        query.addAtom(new QRRoleAtom(R1, x, y));
        query.addAtom(new QRRoleAtom(R2, y, x));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildVerySimpleQuery")
    private void buildVerySimpleQuery(QRQuery query) {
        QRVariable x = VarFact.getNewVar("x");
        query.setVarFree(x);
        ObjectRoleExpression R1 = pEM.objectRole("R1");
        query.addAtom(new QRRoleAtom(R1, x, x));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildVerySimpleQueryLUBM1")
    private void buildVerySimpleQueryLUBM1(QRQuery query) {
        QRVariable x = VarFact.getNewVar("x");
        query.setVarFree(x);
        QRVariable y = VarFact.getNewVar("y");
        query.setVarFree(y);
        ObjectRoleExpression R1 = pEM.objectRole("R1");
        ConceptExpression C1 = pEM.concept("C1");
        query.addAtom(new QRRoleAtom(R1, x, y));
        query.addAtom(new QRConceptAtom(C1, x));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildLUBM2Query")
    private void buildLUBM2Query(int n, QRQuery query) {
        if (n == 1) {
            QRVariable v0 = VarFact.getNewVar("v0");
            QRVariable v1 = VarFact.getNewVar("v1");
            QRVariable v2 = VarFact.getNewVar("v2");
            QRVariable v3 = VarFact.getNewVar("v3");
            query.setVarFree(v0);
            query.setVarFree(v2);
            ConceptExpression Student = pEM.concept("Student");
            ConceptExpression Course = pEM.concept("Course");
            ConceptExpression Faculty = pEM.concept("Faculty");
            ConceptExpression Department = pEM.concept("Department");
            ObjectRoleExpression takesCourse = pEM.objectRole("takesCourse");
            ObjectRoleExpression teacherOf = pEM.objectRole("teacherOf");
            ObjectRoleExpression worksFor = pEM.objectRole("worksFor");
            ObjectRoleExpression memberOf = pEM.objectRole("memberOf");
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

    /** support for query decycling */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PossiblyReplaceAtom")
    private boolean PossiblyReplaceAtom(QRQuery query, int atomIterator, QRAtom newAtom,
            QRVariable newArg, Set<QRAtom> passedAtoms) {
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

    /** init vars map */
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

    /** create a new var which is a copy of an existing one */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QRVariable")
    private QRVariable getNewCopyVar(QRVariable old, int suffix) {
        String buf = "_" + suffix;
        QRVariable var = VarFact.getNewVar(old.getName() + buf);
        NewVarMap.put(var, old);
        return var;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "transformQueryPhase1")
    public QRQuery transformQueryPhase1(QRQuery query) {
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

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "deleteFictiveVariables")
    public void deleteFictiveVariables(QRQuery query) {
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

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "createConceptByVar")
    public ConceptExpression createConceptByVar(QRVariable v) {
        return VarRestrictions.get(NewVarMap.get(v).getName());
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "BuildAproximation")
    public void buildApproximation(QRQuery query) {
        QueryApproximation app = new QueryApproximation(this, query);
        Map<QRVariable, ConceptExpression> approx = new HashMap<QRVariable, ConceptExpression>();
        for (QRVariable p : NewVarMap.values()) {
            approx.put(p, pEM.top());
        }
        for (QRVariable v : query.getFreeVars()) {
            QRVariable var = NewVarMap.get(v);
            approx.put(var, pEM.and(approx.get(var), app.Assign(query, null, v)));
        }
        for (Map.Entry<QRVariable, ConceptExpression> e : approx.entrySet()) {
            VarRestrictions.put(e.getKey().getName(),
                    pEM.and(VarRestrictions.get(e.getKey().getName()), e.getValue()));
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "transformQueryPhase2")
    public ConceptExpression transformQueryPhase2(QRQuery query) {
        NewNominals.clear();
        TermAssigner assigner = new TermAssigner(this, query);
        deleteFictiveVariables(query);
        QRVariable next = query.getFreeVars().iterator().next();
        // System.out.println("Assigner initialised; var: " + next);
        return assigner.Assign(query, null, next);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "doQuery")
    private void doQuery(QRQuery query, ReasoningKernel kernel) {
        TQueryToConceptsTransformer transformer = new TQueryToConceptsTransformer(this,
                query);
        transformer.Run();
        transformer.printResult();
        kernel.evaluateQuery(transformer.getResult());
    }

    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "Var2I")
    private final Map<String, Integer> Var2I = new HashMap<String, Integer>();
    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "I2Var")
    private final List<String> I2Var = new ArrayList<String>();

    /** fills in variable index */
    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "fillVarIndex")
    private void fillVarIndex(MultiMap<String, ConceptExpression> query) {
        int n = 0;
        Var2I.clear();
        I2Var.clear();
        for (String p : query.keySet()) {
            if (!Var2I.containsKey(p)) {
                // new name
                Var2I.put(p, n++);
                I2Var.add(p);
            }
        }
        assert I2Var.size() == n;
    }

    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "evaluateQuery")
    public void evaluateQuery(MultiMap<String, ConceptExpression> query,
            ReasoningKernel kernel) {
        // make index of all vars
        fillVarIndex(query);
        if (I2Var.isEmpty()) {
            // System.out.println("No query variables\n");
            return;
        }
        // for every var: create an expression of vars
        List<DLTree> Concepts = new ArrayList<DLTree>();
        // System.out.println("Tuple <");
        for (int i = 0; i < I2Var.size(); ++i) {
            String var = I2Var.get(i);
            if (i != 0) {
                // System.out.println(", ");
            }
            // System.out.println(var);
            List<ConceptExpression> list = new ArrayList<ConceptExpression>(
                    query.get(var));
            Concepts.add(kernel.e(pEM.and(list)));
        }
        // System.out.println(">\n");
        fillIVec(kernel);
        kernel.getTBox().answerQuery(Concepts);
    }

    @PortedFrom(file = "ConjunctiveQuery.cpp", name = "fillIVec")
    private void fillIVec(ReasoningKernel kernel) {
        // System.out.print("Creating iterables...");
        kernel.getTBox().getIV().clear();
        for (int i = 0; i < I2Var.size(); i++) {
            // The i'th var is I2Var[i]; get its concept
            ConceptExpression C = VarRestrictions.get(I2Var.get(i));
            // get all instances of C
            ActorImpl a = new ActorImpl();
            a.needIndividuals();
            kernel.getInstances(C, a);
            List<Individual> individuals = new ArrayList<Individual>();
            for (ClassifiableEntry e : a.getElements1D()) {
                individuals.add((Individual) e);
            }
            kernel.getTBox().getIV().add(new IterableElem<Individual>(individuals));
        }
        // System.out.println(" done");
    }

    public ExpressionManager getpEM() {
        return pEM;
    }

    public Map<QRVariable, QRVariable> getNewVarMap() {
        return NewVarMap;
    }
}
