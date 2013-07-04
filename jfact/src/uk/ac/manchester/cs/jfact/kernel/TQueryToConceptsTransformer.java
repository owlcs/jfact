package uk.ac.manchester.cs.jfact.kernel;

import org.semanticweb.owlapi.util.MultiMap;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRQuery;
import conformance.PortedFrom;

@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TQueryToConceptsTransformer")
class TQueryToConceptsTransformer {
    /**
     * 
     */
    private final ConjunctiveQueryFolding conjunctiveQueryFolding;
    /** query to transform */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Query")
    private QRQuery Query;
    /** transformation result */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Result")
    // XXX verify the order is not important
    private MultiMap<String, ConceptExpression> Result = new MultiMap<String, ConceptExpression>();

    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TQueryToConceptsTransformer")
    public TQueryToConceptsTransformer(ConjunctiveQueryFolding conjunctiveQueryFolding,
            QRQuery query) {
        this.conjunctiveQueryFolding = conjunctiveQueryFolding;
        Query = new QRQuery(query);
    }

    /** main method to do the work */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Run")
    public void Run() {
        conjunctiveQueryFolding.transformQueryPhase1(Query);
        System.out.println("After Phase 1\n" + Query);
        ConceptExpression term = conjunctiveQueryFolding.transformQueryPhase2(Query);
        String propositionalVariable = null;
        String lastNominal = null;
        for (int i = 1; true; ++i) {
            System.out.println("Expression:");
            System.out.print(term);
            System.out.println("; i = " + i + "\n");
            System.out.println("Depth Measuring:");
            TDepthMeasurer depthMeasurer = new TDepthMeasurer(conjunctiveQueryFolding);
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
            TExpressionMarker expressionMarker = new TExpressionMarker(
                    conjunctiveQueryFolding, nominal);
            term.accept(expressionMarker);
            System.out.println("Simple ?" + expressionMarker.KnownToBeSimple(term));
            expressionMarker.PrintPath();
            System.out.println("Going to replace subterm ");
            System.out.print(expressionMarker.getSubterm());
            System.out.println();
            System.out.println("Initializing Replacer...\n");
            propositionalVariable = "P" + i;
            TReplacer replacer = new TReplacer(conjunctiveQueryFolding,
                    expressionMarker.getSubterm(), propositionalVariable);
            System.out.println("Running Replacer...\n");
            term.accept(replacer);
            System.out.println("Replace Result :\n");
            System.out.print(replacer.getReplaceResult(term));
            System.out.println();
            System.out.println("Initializing Solver...\n");
            TEquationSolver equationSolver = new TEquationSolver(conjunctiveQueryFolding,
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
        Result.put(
                lastNominal,
                conjunctiveQueryFolding.getpEM().not(
                        conjunctiveQueryFolding.getpEM().concept(propositionalVariable)));
    }

    /** get the result */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "getResult")
    public MultiMap<String, ConceptExpression> getResult() {
        return Result;
    }

    /** print the result */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "printResult")
    public void printResult() {
        int i = 0;
        for (ConceptExpression e : Result.getAllValues()) {
            System.err.println(i + ": ");
            i++;
            System.err.println(e);
        }
    }
}
