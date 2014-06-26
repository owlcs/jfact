package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.split.TSignature;
import uk.ac.manchester.cs.jfact.split.TSplitVars;
import conformance.Original;
import conformance.PortedFrom;

/** ontology */
@PortedFrom(file = "tOntology.h", name = "TOntology")
public class Ontology implements Serializable {

    private static final long serialVersionUID = 11000L;
    /** all the axioms */
    @PortedFrom(file = "tOntology.h", name = "Axioms")
    private final List<AxiomInterface> axioms = new ArrayList<>();
    /** all the axioms */
    @PortedFrom(file = "tOntology.h", name = "Retracted")
    private final List<AxiomInterface> retracted = new ArrayList<>();
    /** expression manager that builds all the expressions for the axioms */
    @PortedFrom(file = "tOntology.h", name = "EManager")
    private final ExpressionManager expressionManager = new ExpressionManager();
    /** id to be given to the next axiom */
    @PortedFrom(file = "tOntology.h", name = "axiomId")
    private int axiomId;
    /** true iff ontology was changed */
    @PortedFrom(file = "tOntology.h", name = "changed")
    private boolean changed;
    @PortedFrom(file = "tOntology.h", name = "Splits")
    private final TSplitVars Splits = new TSplitVars();

    /** default constructor */
    public Ontology() {
        axiomId = 0;
        changed = false;
    }

    /** @return splits */
    @Original
    public TSplitVars getSplits() {
        return Splits;
    }

    /**
     * @param i
     *        i
     * @return axiom in position i
     */
    @PortedFrom(file = "tOntology.h", name = "get")
    public AxiomInterface get(int i) {
        return axioms.get(i);
    }

    /** @return true iff the ontology was changed since its last load */
    @PortedFrom(file = "tOntology.h", name = "isChanged")
    public boolean isChanged() {
        return changed;
    }

    /** set the processed marker to the end of the ontology */
    @PortedFrom(file = "tOntology.h", name = "setProcessed")
    public void setProcessed() {
        retracted.clear();
        changed = false;
    }

    /**
     * add given axiom to the ontology
     * 
     * @param p
     *        p
     * @return p
     */
    @PortedFrom(file = "tOntology.h", name = "add")
    @Nonnull
    public AxiomInterface add(AxiomInterface p) {
        p.setId(++axiomId);
        axioms.add(p);
        changed = true;
        return p;
    }

    /**
     * retract given axiom to the ontology
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "tOntology.h", name = "retract")
    public void retract(AxiomInterface p) {
        changed = true;
        retracted.add(p);
        p.setUsed(false);
    }

    /** clear the ontology */
    @PortedFrom(file = "tOntology.h", name = "clear")
    public void clear() {
        safeClear();
        retracted.clear();
        expressionManager.clear();
        changed = false;
    }

    /** safe clear the ontology (do not remove axioms) */
    @PortedFrom(file = "tOntology.h", name = "safeClear")
    public void safeClear() {
        axioms.clear();
    }

    // access to axioms
    /** @return expression manager */
    @PortedFrom(file = "tOntology.h", name = "getExpressionManager")
    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    /** @return axioms for the whole ontology */
    @PortedFrom(file = "tOntology.h", name = "getAxioms")
    public List<AxiomInterface> getAxioms() {
        return axioms;
    }

    /** @return size of the ontology */
    @PortedFrom(file = "tOntology.h", name = "size")
    public int size() {
        return axioms.size();
    }

    /** @return list of retracted axioms */
    @Original
    public List<AxiomInterface> getRetracted() {
        return retracted;
    }

    /** @return signature of all ontology axioms */
    @PortedFrom(file = "tOntology.h", name = "getSignature")
    public TSignature getSignature() {
        TSignature sig = new TSignature();
        for (AxiomInterface p : axioms) {
            if (p.isUsed()) {
                sig.add(p.getSignature());
            }
        }
        return sig;
    }
}
