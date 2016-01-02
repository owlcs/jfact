package uk.ac.manchester.cs.jfact.kernel.todolist;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.UnreachableSituationException;
import uk.ac.manchester.cs.jfact.kernel.DagTag;

/** priority matrix */
@PortedFrom(file = "PriorityMatrix.h", name = "ToDoPriorMatrix")
public class ToDoPriorMatrix implements Serializable {

    /** number of regular options (o- and NN-rules are not included) */
    @PortedFrom(file = "PriorityMatrix.h", name = "nRegularOps") public static final int NREGULAROPTIONS = 7;
    /**
     * priority index for o- and ID operations (note that these ops have the
     * highest priority)
     */
    @PortedFrom(file = "PriorityMatrix.h", name = "iId") protected static final int PRIORITYINDEXID = NREGULAROPTIONS
        + 1;
    /** priority index for lesser than or equal operation in nominal node */
    @PortedFrom(file = "PriorityMatrix.h", name = "iNN") protected static final int PRIORITYINDEXNOMINALNODE = NREGULAROPTIONS
        + 2;
    // regular operation indexes
    @PortedFrom(file = "PriorityMatrix.h", name = "iAnd") private int indexAnd;
    @PortedFrom(file = "PriorityMatrix.h", name = "iOr") private int indexOr;
    @PortedFrom(file = "PriorityMatrix.h", name = "iExists") private int indexExists;
    @PortedFrom(file = "PriorityMatrix.h", name = "iForAll") private int indexForall;
    @PortedFrom(file = "PriorityMatrix.h", name = "iLE") private int indexLE;
    @PortedFrom(file = "PriorityMatrix.h", name = "iGE") private int indexGE;

    /**
     * Auxiliary class to get priorities on operations
     * 
     * @param options
     *        options
     */
    @PortedFrom(file = "PriorityMatrix.h", name = "initPriorities")
    public void initPriorities(String options) {
        // check for correctness
        if (options.length() < 7) {
            throw new ReasonerInternalException("ToDo List option string should have length 7");
        }
        // init values by symbols loaded
        indexAnd = options.charAt(1) - '0';
        indexOr = options.charAt(2) - '0';
        indexExists = options.charAt(3) - '0';
        indexForall = options.charAt(4) - '0';
        indexLE = options.charAt(5) - '0';
        indexGE = options.charAt(6) - '0';
        // correctness checking
        if (indexAnd >= NREGULAROPTIONS || indexOr >= NREGULAROPTIONS || indexExists >= NREGULAROPTIONS
            || indexForall >= NREGULAROPTIONS || indexGE >= NREGULAROPTIONS || indexLE >= NREGULAROPTIONS) {
            throw new ReasonerInternalException("ToDo List option out of range");
        }
    }

    /**
     * @param op
     *        Op
     * @param sign
     *        Sign
     * @param nominalNode
     *        NominalNode
     * @return index
     */
    @PortedFrom(file = "PriorityMatrix.h", name = "getIndex")
    @SuppressWarnings("incomplete-switch")
    public int getIndex(DagTag op, boolean sign, boolean nominalNode) {
        switch (op) {
            case AND:
                return sign ? indexAnd : indexOr;
            case FORALL:
            case IRR: // process local (ir-)reflexivity as a FORALL
                return sign ? indexForall : indexExists;
            case PROJ:
                // it should be the lowest priority but now just OR's one
            case CHOOSE:
                return indexOr;
            case LE:
                return sign ? nominalNode ? PRIORITYINDEXNOMINALNODE : indexLE : indexGE;
            case DATATYPE:
            case DATAVALUE:
            case DATAEXPR:
            case NN:
            case TOP: // no need to process these ops
                return NREGULAROPTIONS;
            case PSINGLETON:
            case PCONCEPT: // no need to process neg of PC
                return sign ? PRIORITYINDEXID : NREGULAROPTIONS;
            case NSINGLETON:
            case NCONCEPT: // both NC and neg NC are processed
                return PRIORITYINDEXID;
            default: // safety check
                throw new UnreachableSituationException("Error: " + op);
        }
    }
}
