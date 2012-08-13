package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.split.TSplitVars;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

public class Ontology {
    /** all the axioms */
    private List<Axiom> axioms = new ArrayList<Axiom>();
    /** expression manager that builds all the expressions for the axioms */
    private ExpressionManager expressionManager = new ExpressionManager();
    /** id to be given to the next axiom */
    private int axiomId;
    /** true iff ontology was changed */
    private boolean changed;
    public TSplitVars Splits = new TSplitVars();

    public Ontology() {
        axiomId = 0;
        changed = false;
    }

    public Axiom get(int i) {
        return axioms.get(i);
    }

    /** @return true iff the ontology was changed since its last load */
    public boolean isChanged() {
        return changed;
    }

    /** set the processed marker to the end of the ontology */
    public void setProcessed() {
        changed = false;
    }

    /** add given axiom to the ontology */
    public Axiom add(Axiom p) {
        p.setId(++axiomId);
        axioms.add(p);
        changed = true;
        return p;
    }

    /** retract given axiom to the ontology */
    public void retract(Axiom p) {
        if (p.getId() <= axioms.size() && axioms.get(p.getId() - 1).equals(p)) {
            changed = true;
            p.setUsed(false);
        }
    }

    /** clear the ontology */
    public void clear() {
        axioms.clear();
        expressionManager.clear();
        changed = false;
    }

    // access to axioms
    /** get access to an expression manager */
    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    /** RW begin() for the whole ontology */
    public List<Axiom> getAxioms() {
        return axioms;
    }

    /** size of the ontology */
    public int size() {
        return axioms.size();
    }

    /** accept method for the visitor pattern */
    public void accept(DLAxiomVisitor visitor) {
        visitor.visitOntology(this);
    }

    public <O> O accept(DLAxiomVisitorEx<O> visitor) {
        return visitor.visitOntology(this);
    }
}
