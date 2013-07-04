package uk.ac.manchester.cs.jfact.kernel;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.actors.Actor;
import uk.ac.manchester.cs.jfact.kernel.actors.RIActor;
import uk.ac.manchester.cs.jfact.kernel.dl.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.*;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ConjunctiveQueryFolding")
    public class    ConjunctiveQueryFolding{
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "pEM")
    private ExpressionManager pEM = new ExpressionManager();
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "VarFact")
    private VariableFactory VarFact = new VariableFactory();

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
    public   void buildVerySimpleQuery(QRQuery query) {
        QRVariable x = VarFact.getNewVar("x");
        query.setVarFree(x);
        ObjectRoleExpression R1 = pEM.objectRole("R1");
        query.addAtom(new QRRoleAtom(R1, x, x));
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "buildVerySimpleQueryLUBM1")
    public   void buildVerySimpleQueryLUBM1(QRQuery query) {
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
    public   void buildLUBM2Query(int n, QRQuery query) {
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
    public   QRQuery createQuery() {
        QRQuery query = new QRQuery();
        buildLUBM2Query(1, query);
        return query;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "printAtom")
    public  void printAtom(QRAtom queryAtom) {
        if (queryAtom == null) {
            System.out.println("NULL\n");
        } else {
            QRRoleAtom atom = (QRRoleAtom) queryAtom;
            QRVariable arg1 = (QRVariable) atom.getArg1();
            QRVariable arg2 = (QRVariable) atom.getArg2();
            System.out.println("ReasoningKernel.printAtom() "
                    + ((ObjectRoleName) atom.getRole()).getName() + "  " + arg1.getName()
                    + " (" + arg1 + ") " + arg2.getName() + " (" + arg2 + ")\n");
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "printQuery")
    public   void printQuery(QRQuery query) {
        for (QRAtom _atom : query.getBody().begin()) {
            if (_atom instanceof QRRoleAtom) {
                QRRoleAtom atom = (QRRoleAtom) _atom;
                QRVariable arg1 = (QRVariable) atom.getArg1();
                QRVariable arg2 = (QRVariable) atom.getArg2();
                System.out.println(((ObjectRoleName) atom.getRole()).getName() + "  ");
                System.out.println(arg1.getName() + " (" + arg1 + ") ");
                System.out.println(arg2.getName() + " (" + arg2 + ") ");
                System.out.println("\n");
            }

            if (_atom instanceof QRConceptAtom) {
                QRConceptAtom atom = (QRConceptAtom) _atom;
                QRVariable arg = (QRVariable) atom.getArg();
                System.out.println(((ConceptName) atom.getConcept()).getName() + "  ");
                System.out.println(arg.getName() + " (" + arg + ") ");
                System.out.println("\n");
            }

        }
        System.out.println("END OF PRINT QUERY\n");
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "printVariable")
    public   void printVariable(QRVariable var) {
        System.out.println("ReasoningKernel.printVariable()" + var.getName() + " (" + var
                + ") ");
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryConnectednessChecker")
    public  class QueryConnectednessChecker {
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PassedVertice")
        public    Set<QRVariable> PassedVertice = new HashSet<QRVariable>();
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
        public   QRQuery Query;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "QueryConnectednessChecker")
        public  QueryConnectednessChecker(QRQuery query) {
            Query = new QRQuery(query);
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "isConnected")
        public   boolean isConnected() {
            QRAtom a = Query.getBody().begin().iterator().next();
            if (a instanceof QRRoleAtom) {
                MarkVertex((QRVariable) ((QRRoleAtom) a).getArg1());
            } else if (a instanceof QRConceptAtom) {
                MarkVertex((QRVariable) ((QRConceptAtom) a).getArg());
            } else {
                throw new UnreachableSituationException(
                        "Unsupported atom in query rewriting");
            }
            for (QRAtom atomIterator : Query.getBody().begin()) {
                if (atomIterator instanceof QRRoleAtom) {
                    QRRoleAtom atom = (QRRoleAtom) atomIterator;
                    QRVariable arg1 = (QRVariable) atom.getArg1();
                    QRVariable arg2 = (QRVariable) atom.getArg2();
                    if (!PassedVertice.contains(arg1) || !PassedVertice.contains(arg2)) {
                        return false;
                    }
                } else if (atomIterator instanceof QRConceptAtom) {
                    QRConceptAtom atom = (QRConceptAtom) atomIterator;
                    if (!PassedVertice.contains(atom.getArg())) {
                        return false;
                    }
                } else {
                    throw new UnreachableSituationException(
                            "Unsupported atom in query rewriting");
                }
            }
            return true;
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "MarkVertex")
        public    void MarkVertex(QRVariable var) {
            PassedVertice.add(var);
            for (QRAtom atomIterator : Query.getBody().begin()) {
                if (atomIterator instanceof QRRoleAtom) {
                    QRRoleAtom atom = (QRRoleAtom) atomIterator;
                    QRVariable arg1 = (QRVariable) atom.getArg1();
                    QRVariable arg2 = (QRVariable) atom.getArg2();
                    if (!arg1.equals(var) && !arg2.equals(var) || arg1.equals(var)
                            && arg2.equals(var)) {
                        continue;
                    }
                    QRVariable neighbour;
                    if (arg1 == var) {
                        neighbour = arg2;
                    } else {
                        neighbour = arg1;
                    }
                    if (PassedVertice.contains(neighbour)) {
                        continue;
                    }
                    MarkVertex(neighbour);
                }
            }
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PossiblyReplaceAtom")
    public  boolean PossiblyReplaceAtom(QRQuery query, int atomIterator, QRAtom newAtom,
            QRVariable newArg, Set<QRAtom> passedAtoms) {
        System.out.println("Modified code starts here!\nBefore replacing in copy.");
        printQuery(query);
        QRAtom oldAtom = query.getBody().replaceAtom(atomIterator, newAtom);
        query.setVarFree(newArg);
        System.out.println("Running Checker");
        QueryConnectednessChecker checker = new QueryConnectednessChecker(query);
        boolean ret;
        if (checker.isConnected()) {
            System.out.println("Connected\nAfter replacing in Query");
            printQuery(query);
            ret = true;
        } else {
            System.out.println("Disconnected");
            // restore the old query
            newAtom = oldAtom;
            oldAtom = query.getBody().replaceAtom(atomIterator, oldAtom);
            query.getFreeVars().remove(newArg);
            ret = false;
        }
        passedAtoms.add(newAtom);
        return ret;
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "transformQueryPhase1")
    public  void transformQueryPhase1(QRQuery query) {
        Set<QRAtom> passedAtoms = new HashSet<QRAtom>();
        System.out.println("Phase 1 starts");

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
                QRVariable newArg = VarFact.getNewVar(arg2.getName());
                QRAtom newAtom = new QRRoleAtom(role, arg1, newArg);
                if (PossiblyReplaceAtom(query, i, newAtom, newArg, passedAtoms)) {
                    continue;
                }
            } else if (query.getFreeVars().contains(arg1)) {
                QRVariable newArg = VarFact.getNewVar(arg1.getName());
                QRAtom newAtom = new QRRoleAtom(role, newArg, arg2);
                if (PossiblyReplaceAtom(query, i, newAtom, newArg, passedAtoms)) {
                    continue;
                }
            }
        }
        // #endif
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "NumberFactory")
    public  class NumberFactory {
        int N = 0;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "GetNextNumber")
        public     int GetNextNumber() {
            return ++N;
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")
    public   class TermAssigner {
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PassedVertice")
        public  Set<QRVariable> PassedVertice;
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
        public   QRQuery Query;
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Factory")
        public AtomicLong Factory = new AtomicLong();
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "N")
        public   int N = 0;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")
        public   TermAssigner(QRQuery query) {
            Query = new QRQuery(query);
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Assign")
        public  ConceptExpression Assign(QRAtom previousAtom, QRVariable v) {
            System.out.println("Assign:\n variable: ");
            printVariable(v);
            System.out.println("\n atom:");
            printAtom(previousAtom);
            System.out.println();
            PassedVertice.add(v);
            ConceptExpression t;
            if (Query.isFreeVar(v)) {
                t = pEM.concept(v.getName() + ":" + Factory.incrementAndGet());
            } else {
                t = pEM.top();
            }
            ConceptExpression s = pEM.top();
            for (QRAtom atomIterator : Query.getBody().begin()) {
                if (atomIterator instanceof QRRoleAtom) {
                    QRRoleAtom atom = (QRRoleAtom) atomIterator;
                    ObjectRoleExpression role = atom.getRole();
                    QRVariable arg1 = (QRVariable) atom.getArg1();
                    QRVariable arg2 = (QRVariable) atom.getArg2();
                    if (atomIterator == previousAtom) {
                        continue;
                    }
                    if (arg1 == v) {
                        ConceptExpression p = Assign(atomIterator, arg2);
                        ConceptExpression p1 = pEM.exists(role, p);
                        ConceptExpression newS = pEM.and(s, p1);
                        s = newS;
                    }
                    if (arg2 == v) {
                        ConceptExpression p = Assign(atomIterator, arg1);
                        ConceptExpression p1 = pEM.exists(pEM.inverse(role), p);
                        ConceptExpression newS = pEM.and(s, p1);
                        s = newS;
                    }
                }
                if (atomIterator instanceof QRConceptAtom) {
                    QRConceptAtom atom = (QRConceptAtom) atomIterator;
                    ConceptExpression concept = atom.getConcept();
                    QRVariable arg = (QRVariable) atom.getArg();
                    if (arg == v) {
                        ConceptExpression newS = pEM.and(s, concept);
                        s = newS;
                    }
                }
            }
            return pEM.and(t, s);
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "DeleteFictiveVariables")
        public    void DeleteFictiveVariables() {
            Set<QRVariable> RealFreeVars = new HashSet<QRVariable>();
            for (QRAtom atomIterator : Query.getBody().begin()) {
                if (atomIterator instanceof QRRoleAtom) {
                    QRRoleAtom atom = (QRRoleAtom) atomIterator;
                    QRVariable arg1 = (QRVariable) atom.getArg1();
                    QRVariable arg2 = (QRVariable) atom.getArg2();
                    if (Query.isFreeVar(arg1)) {
                        RealFreeVars.add(arg1);
                    }
                    if (Query.isFreeVar(arg2)) {
                        RealFreeVars.add(arg2);
                    }
                }
            }
            Query.setFreeVars(RealFreeVars);
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "transformQueryPhase2")
    public   ConceptExpression transformQueryPhase2(QRQuery query) {
        TermAssigner assigner = new TermAssigner(query);
        assigner.DeleteFictiveVariables();
        System.out.println("Assigner initialised; var: ");
        QRVariable next = query.getFreeVars().iterator().next();
        printVariable(next);
        System.out.println("\n");
        return assigner.Assign(null, next);
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "IsNominal")
    public     boolean IsNominal(ConceptExpression expr) {
        if (expr instanceof ConceptName) {
            ConceptName conceptName = (ConceptName) expr;
            if (conceptName.getName().charAt(0) >= 'a'
                    && conceptName.getName().charAt(0) <= 'z') {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TDepthMeasurer")
    public   class TDepthMeasurer extends DLExpressionVisitorAdapter {
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "DepthOfNominalOccurences")
        public   Map<ConceptExpression, Integer> DepthOfNominalOccurences = new HashMap<ConceptExpression, Integer>();
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "CurrentDepth")
        public   int CurrentDepth = 0;
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TotalNominalOccurences")
        public   int TotalNominalOccurences = 0;

        @Override
        public void visit(ConceptTop expr) {}

        @Override
        public void visit(ConceptName expr) {
            if (IsNominal(expr)) {
                DepthOfNominalOccurences.put(expr, CurrentDepth);
                ++TotalNominalOccurences;
            }
        }

        @Override
        public void visit(ConceptAnd expr) {
            for (ConceptExpression p : expr.getArguments()) {
                p.accept(this);
            }
        }

        @Override
        public void visit(ConceptObjectExists expr) {
            ObjectRoleExpression role1 = expr.getOR();
            if (role1 instanceof ObjectRoleName) {
                ++CurrentDepth;
                expr.getConcept().accept(this);
                --CurrentDepth;
            } else if (role1 instanceof ObjectRoleInverse) {
                expr.getConcept().accept(this);
            }
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getMaxDepth")
        public   int getMaxDepth() {
            int max = -1;
            for (Integer i : DepthOfNominalOccurences.values()) {
                if (i.intValue() > max) {
                    max = i;
                }
            }
            return max;
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getNominalWithMaxDepth")
        public   ConceptExpression getNominalWithMaxDepth() {
            ConceptExpression max = null;
            int maxvalue = -1;
            for (Map.Entry<ConceptExpression, Integer> e : DepthOfNominalOccurences
                    .entrySet()) {
                if (e.getValue() >= maxvalue) {
                    max = e.getKey();
                    maxvalue = e.getValue();
                }
            }
            return max;
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PrintDepthTable")
        public   void PrintDepthTable() {
            System.out.println("Total nominal occurrences: " + TotalNominalOccurences
                    + "\n");
            for (Map.Entry<ConceptExpression, Integer> e : DepthOfNominalOccurences
                    .entrySet()) {
                System.out.print(e.getKey());
                System.out.println(" has depth " + e.getValue() + "\n");
            }
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TExpressionMarker")
    class TExpressionMarker extends DLExpressionVisitorAdapter {
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "GoodTerms")
        public         Map<ConceptExpression, Boolean> GoodTerms = new HashMap<ConceptExpression, Boolean>();
        // A term is called good, if all of its subterms don't contain nominals
        // different from x
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "SimpleTerms")
        public Map<ConceptExpression, Boolean> SimpleTerms = new HashMap<ConceptExpression, Boolean>();
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Path")
        public List<ConceptExpression> Path = new ArrayList<ConceptExpression>();
        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Nominal")
        public ConceptExpression Nominal;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TExpressionMarker")
        public TExpressionMarker(ConceptExpression nominal) {
            Nominal = nominal;
        }

        // concept expressions
        @Override
        public void visit(ConceptTop expr) {
            SimpleTerms.put(expr, false);
            GoodTerms.put(expr, true);
        }

        @Override
        public void visit(ConceptName expr) {
            SimpleTerms.put(expr, IsNominal(expr));
            if (expr == Nominal) {
                GoodTerms.put(expr, true);
                Path.add(expr);
            } else {
                GoodTerms.put(expr, !IsNominal(expr));
            }
        }

        @Override
        public void visit(ConceptAnd expr) {
            boolean simple = false;
            boolean good = true;
            boolean onPath = false;
            for (ConceptExpression p : expr.getArguments()) {
                p.accept(this);
                if (KnownToBeSimple(p)) {
                    simple = true;
                }
                if (!KnownToBeGood(p)) {
                    good = false;
                }
                if (KnownToBeOnPath(p)) {
                    onPath = true;
                }
            }
            SimpleTerms.put(expr, simple);
            GoodTerms.put(expr, good);
            if (onPath && good && simple) {
                Path.add(expr);
            }
        }

        @Override
        public void visit(ConceptObjectExists expr) {
            ObjectRoleExpression role1 = expr.getOR();
            if (role1 instanceof ObjectRoleName) {
                expr.getConcept().accept(this);
                SimpleTerms.put(expr, false);
            } else if (role1 instanceof ObjectRoleInverse) {
                expr.getConcept().accept(this);
                if (KnownToBeSimple(expr.getConcept())) {
                    SimpleTerms.put(expr, true);
                } else {
                    SimpleTerms.put(expr, false);
                }
            } else {
                throw new UnreachableSituationException();
            }
            GoodTerms.put(expr, KnownToBeGood(expr.getConcept()));
            if (KnownToBeOnPath(expr.getConcept()) && KnownToBeGood(expr)
                    && KnownToBeSimple(expr)) {
                Path.add(expr);
            }
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "KnownToBeSimple")
        public        boolean KnownToBeSimple(ConceptExpression expr) {
            return SimpleTerms.containsKey(expr);
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "KnownToBeGood")
        public boolean KnownToBeGood(ConceptExpression expr) {
            return GoodTerms.containsKey(expr);
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "KnownToBeOnPath")
        public boolean KnownToBeOnPath(ConceptExpression expr) {
            return Path.size() >= 1 && Path.get(Path.size() - 1).equals(expr);
        }


        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PrintPath")
        public   void PrintPath() {
            for (int i = 0; i < Path.size(); ++i) {
                System.out.println("Expression on depth " + i + " :\n");
                System.out.println(Path.get(i));
            }
        }

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getSubterm")
        public
        ConceptExpression getSubterm() {
            if (Path.size() >= 1) {
                return Path.get(Path.size() - 1);
            } else {
                return null;
            }
        }
    }

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TReplacer")
    private   class TReplacer extends DLExpressionVisitorAdapter {

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ReplaceResult")
        private   Map<ConceptExpression, ConceptExpression> ReplaceResult;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ExpressionToReplace")
        private   ConceptExpression ExpressionToReplace;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "PropositionalVariable")
        private   ConceptExpression PropositionalVariable;


        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TReplacer")
        public   TReplacer(ConceptExpression expression, String propositionalVariable) {
            ExpressionToReplace = expression;
            PropositionalVariable = pEM.concept(propositionalVariable);
        }

        // concept expressions
        @Override
        public void visit(ConceptTop expr) {
            ReplaceResult.put(expr, expr);
        }

        @Override
        public void visit(ConceptName expr) {
            if (expr.equals(ExpressionToReplace)) {
                ReplaceResult.put(expr, PropositionalVariable);
            } else {
                ReplaceResult.put(expr, expr);
            }
        }

        @Override
        public void visit(ConceptAnd expr) {
            if (expr.equals(ExpressionToReplace)) {
                ReplaceResult.put(expr, PropositionalVariable);
            } else {
                ConceptExpression s = null;
                for (ConceptExpression p : expr.getArguments()) {
                    p.accept(this);
                    if (p == expr.getArguments().get(0)) {
                        s = ReplaceResult.get(p);
                    } else {
                        s = pEM.and(s, ReplaceResult.get(p));
                    }
                }
                ReplaceResult.put(expr, s);
            }
        }

        @Override
        public void visit(ConceptObjectExists expr) {
            if (expr.equals(ExpressionToReplace)) {
                ReplaceResult.put(expr, PropositionalVariable);
            } else {
                ObjectRoleExpression role = expr.getOR();
                expr.getConcept().accept(this);
                ReplaceResult.put(expr,
                        pEM.exists(role, ReplaceResult.get(expr.getConcept())));
            }
        }


        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getReplaceResult")
   public ConceptExpression getReplaceResult(ConceptExpression c) {
            return ReplaceResult.get(c);
        }
    }


    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TEquationSolver")
    private   class TEquationSolver {

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "LeftPart")
        private  ConceptExpression LeftPart;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "RightPart")
        private ConceptExpression RightPart;

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "ExpressionMarker")
        private TExpressionMarker ExpressionMarker;


        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TEquationSolver")
        public TEquationSolver(ConceptExpression leftPart, String propositionalVariable,
                TExpressionMarker expressionMarker) {
            LeftPart = leftPart;
            RightPart = pEM.concept(propositionalVariable);
            ExpressionMarker = expressionMarker;
        }


        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Solve")
        public     void Solve() {
            while (!IsNominal(LeftPart)) {
                if (LeftPart instanceof ConceptObjectExists) {
                    ConceptObjectExists leftDiamond = (ConceptObjectExists) LeftPart;
                    ObjectRoleInverse invRole = (ObjectRoleInverse) leftDiamond.getOR();
                    ObjectRoleExpression role = invRole.getOR();
                    ConceptExpression newLeftPart = leftDiamond.getConcept();
                    ConceptExpression newRightPart = pEM.forall(role, RightPart);
                    LeftPart = newLeftPart;
                    RightPart = newRightPart;
                } else if (LeftPart instanceof ConceptAnd) {
                    ConceptAnd leftAnd = (ConceptAnd) LeftPart;
                    ConceptExpression arg1 = leftAnd.getArguments().get(0);
                    ConceptExpression arg2 = leftAnd.getArguments().get(1);
                    if (!ExpressionMarker.KnownToBeSimple(arg1)) {
                        ConceptExpression t;
                        t = arg1;
                        arg1 = arg2;
                        arg2 = t;
                    }
                    ConceptExpression newLeftPart = arg1;
                    ConceptExpression newRightPart;
                    if (arg2 instanceof ConceptTop) {
                        newRightPart = RightPart;
                    } else {
                        newRightPart = pEM.or(pEM.not(arg2), RightPart);
                    }
                    LeftPart = newLeftPart;
                    RightPart = newRightPart;
                }
            }
        }


        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getNominal")
        private   String getNominal() {
            String longNominal = ((ConceptName) LeftPart).getName();
            int colon = longNominal.indexOf(":");
            return longNominal.substring(0, colon);
        }


        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getPhi")
        private   ConceptExpression getPhi() {
            return RightPart;
        }
    }


    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TQueryToConceptsTransformer")
    private   class TQueryToConceptsTransformer {
        // / query to transform

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
        private   QRQuery Query;
        // / transformation result

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Result")
        // XXX verify the order is not important
        private   MultiMap<String, ConceptExpression> Result = new MultiMap<String, ConceptExpression>();

        // / init c'tor

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TQueryToConceptsTransformer")
        public    TQueryToConceptsTransformer(QRQuery query) {
            Query = new QRQuery(query);
        }

        // / main method to do the work

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Run")
        public   void Run() {
            transformQueryPhase1(Query);
            System.out.println("After Phase 1\n");
            printQuery(Query);
            ConceptExpression term = transformQueryPhase2(Query);
            String propositionalVariable = null;
            String lastNominal = null;
            for (int i = 1; true; ++i) {
                System.out.println("Expression:");
                System.out.print(term);
                System.out.println("; i = " + i + "\n");
                System.out.println("Depth Measuring:");
                TDepthMeasurer depthMeasurer = new TDepthMeasurer();
                term.accept(depthMeasurer);
                System.out.println(depthMeasurer.getMaxDepth());
                if (depthMeasurer.getMaxDepth() == -1) {
                    break;
                }
                ConceptExpression nominal = depthMeasurer.getNominalWithMaxDepth();
                System.out.println("Chosen nominal :");
                System.out.print(nominal);
                System.out.println("\n");
                // depthMeasurer.PrintDepthTable();
                TExpressionMarker expressionMarker = new TExpressionMarker(nominal);
                term.accept(expressionMarker);
                System.out.println("Simple ?" + expressionMarker.KnownToBeSimple(term));
                expressionMarker.PrintPath();
                System.out.println("Going to replace subterm ");
                System.out.print(expressionMarker.getSubterm());
                System.out.println();
                System.out.println("Initializing Replacer...\n");
                propositionalVariable = "P" + i;
                TReplacer replacer = new TReplacer(expressionMarker.getSubterm(),
                        propositionalVariable);
                System.out.println("Running Replacer...\n");
                term.accept(replacer);
                System.out.println("Replace Result :\n");
                System.out.print(replacer.getReplaceResult(term));
                System.out.println();
                System.out.println("Initializing Solver...\n");
                TEquationSolver equationSolver = new TEquationSolver(
                        expressionMarker.getSubterm(), propositionalVariable,
                        expressionMarker);
                System.out.println("Running Solver...\n");
                equationSolver.Solve();
                System.out.println("Phi : ");
                System.out.print(equationSolver.getPhi());
                Result.put(equationSolver.getNominal(), equationSolver.getPhi());
                System.out.println("\nNominal: " + equationSolver.getNominal());
                lastNominal = equationSolver.getNominal();
                term = replacer.getReplaceResult(term);
            }
            Result.put(lastNominal, pEM.not(pEM.concept(propositionalVariable)));
        }

        // / get the result

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getResult")
        public    MultiMap<String, ConceptExpression> getResult() {
            return Result;
        }

        // / print the result

        @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "printResult")
        public   void printResult() {
            int i = 0;
            for (ConceptExpression e : Result.getAllValues()) {
                System.err.println(i + ": ");
                i++;
                System.err.println(e);
            }
        }
    }

    @PortedFrom(file="ConjunctiveQueryFolding.cpp",name="doQuery")
    public void doQuery(QRQuery query, ReasoningKernel kernel) {
        System.out.println("Next query: ");
        printQuery(query);
        QueryConnectednessChecker cc1 = new QueryConnectednessChecker(query);
        System.out.println("Connected? " + cc1.isConnected());
        TQueryToConceptsTransformer transformer = new TQueryToConceptsTransformer(query);
        transformer.Run();
        transformer.printResult();
        kernel.evaluateQuery(transformer.getResult());
    }
    @PortedFrom(file="ConjunctiveQuery.cpp",name="Var2I")
private    Map<String, Integer> Var2I = new HashMap<String, Integer>();
    @PortedFrom(file="ConjunctiveQuery.cpp",name="I2Var")
private    List<String> I2Var = new ArrayList<String>();

    /** fills in variable index*/
    @PortedFrom(file="ConjunctiveQuery.cpp",name="fillVarIndex")
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
    
    @PortedFrom(file="ConjunctiveQuery.cpp",name="evaluateQuery")
public 
 void evaluateQuery(MultiMap<String, ConceptExpression> query,
            ReasoningKernel kernel) {
        // make index of all vars
        fillVarIndex(query);
        if (I2Var.isEmpty()) {
            System.out.println("No query variables\n");
            return;
        }
        // for every var: create an expression of vars
        List<ConceptExpression> Concepts = new ArrayList<ConceptExpression>();
        System.out.println("Tuple <");
        for (int i = 0; i < I2Var.size(); ++i) {
            String var = I2Var.get(i);
            if (i != 0) {
                System.out.println(", ");
            }
            System.out.println(var);
            List<ConceptExpression> list = new ArrayList<ConceptExpression>(
                    query.get(var));
            Concepts.add(pEM.and(list));
        }
        System.out.println(">\n");
        if (I2Var.size() == 1) {
            // tree-like query
            Actor a = new RIActor();
            a.needIndividuals();
            kernel.getInstances(Concepts.get(Concepts.size() - 1), a);
            List<String> names = a.getElements1D();
            for (String name : names) {
                System.out.println(name + "\n");
            }
            System.out.println();
            return;
        }
    }
}
