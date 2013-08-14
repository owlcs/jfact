package uk.ac.manchester.cs.jfact.kernel.todolist;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import conformance.PortedFrom;

@PortedFrom(file = "PriorityMatrix.h", name = "ToDoPriorMatrix")
public class ToDoPriorMatrix implements Serializable {
    private static final long serialVersionUID = 11000L;
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

    /** Auxiliary class to get priorities on operations
     * 
     * @param options
     * @param optionName */
    @PortedFrom(file = "PriorityMatrix.h", name = "initPriorities")
    public void initPriorities(String options) {
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
            case dtProj:
                // it should be the lowest priority but now just OR's one
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
