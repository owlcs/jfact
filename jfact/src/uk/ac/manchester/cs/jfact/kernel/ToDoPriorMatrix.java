package uk.ac.manchester.cs.jfact.kernel;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import conformance.PortedFrom;

@PortedFrom(file = "PriorityMatrix.h", name = "ToDoPriorMatrix")
class ToDoPriorMatrix {
    // regular operation indexes
    @PortedFrom(file = "PriorityMatrix.h", name = "iAnd")
    private int indexAnd;
    @PortedFrom(file = "PriorityMatrix.h", name = "iOr")
    private int indexOr;
    @PortedFrom(file = "PriorityMatrix.h", name = "iExists")
    private int indexExists;
    @PortedFrom(file = "PriorityMatrix.h", name = "iForAll")
    private int indexForall;
    @PortedFrom(file = "PriorityMatrix.h", name = "iLE")
    private int indexLE;
    @PortedFrom(file = "PriorityMatrix.h", name = "iGE")
    private int indexGE;

    public ToDoPriorMatrix() {}

    /** number of regular options (o- and NN-rules are not included) */
    @PortedFrom(file = "PriorityMatrix.h", name = "nRegularOps")
    protected static final int nRegularOptions = 7;
    /** priority index for o- and ID operations (note that these ops have the
     * highest priority) */
    @PortedFrom(file = "PriorityMatrix.h", name = "iId")
    protected static final int priorityIndexID = nRegularOptions + 1;
    /** priority index for <= operation in nominal node */
    @PortedFrom(file = "PriorityMatrix.h", name = "iNN")
    protected static final int priorityIndexNominalNode = nRegularOptions + 2;

    /** Auxiliary class to get priorities on operations */
    @PortedFrom(file = "PriorityMatrix.h", name = "initPriorities")
    public void initPriorities(String options, String optionName) {
        // check for correctness
        if (options.length() < 7) {
            throw new ReasonerInternalException(
                    "ToDo List option string should have length 7");
        }
        // init values by symbols loaded
        indexAnd = options.charAt(1) - '0';
        indexOr = options.charAt(2) - '0';
        indexExists = options.charAt(3) - '0';
        indexForall = options.charAt(4) - '0';
        indexLE = options.charAt(5) - '0';
        indexGE = options.charAt(6) - '0';
        // correctness checking
        if (indexAnd >= nRegularOptions || indexOr >= nRegularOptions
                || indexExists >= nRegularOptions || indexForall >= nRegularOptions
                || indexGE >= nRegularOptions || indexLE >= nRegularOptions) {
            throw new ReasonerInternalException("ToDo List option out of range");
        }
    }

    @PortedFrom(file = "PriorityMatrix.h", name = "getIndex")
    public int getIndex(DagTag Op, boolean Sign, boolean NominalNode) {
        switch (Op) {
            case dtAnd:
                return Sign ? indexAnd : indexOr;
            case dtSplitConcept:
                return indexAnd;
            case dtForall:
            case dtIrr: // process local (ir-)reflexivity as a FORALL
                return Sign ? indexForall : indexExists;
            case dtProj: // it should be the lowest priority but now just OR's
                         // one
            case dtChoose:
                return indexOr;
            case dtLE:
                return Sign ? NominalNode ? priorityIndexNominalNode : indexLE : indexGE;
            case dtDataType:
            case dtDataValue:
            case dtDataExpr:
            case dtNN:
            case dtTop: // no need to process these ops
                return nRegularOptions;
            case dtPSingleton:
            case dtPConcept: // no need to process neg of PC
                return Sign ? priorityIndexID : nRegularOptions;
            case dtNSingleton:
            case dtNConcept: // both NC and neg NC are processed
                return priorityIndexID;
            default: // safety check
                throw new UnreachableSituationException();
        }
    }
}
