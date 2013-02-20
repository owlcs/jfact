package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;

/** taxonomy actor */
public class TaxonomyActor implements Actor {
    private ExpressionManager expressionManager;
    /** 2D array to return */
    private List<List<Expression>> acc = new ArrayList<List<Expression>>();
    /** 1D array to return */
    private List<Expression> plain = new ArrayList<Expression>();
    private Policy policy;

    /** @param p
     * @return try current entry */
    public List<Expression> tryEntry(ClassifiableEntry p) {
        List<Expression> toReturn = new ArrayList<Expression>();
        if (p.isSystem()) {
            return toReturn;
        }
        if (policy.applicable(p)) {
            toReturn.add(policy.buildTree(expressionManager, p));
        }
        return toReturn;
    }

    /** @param em
     * @param policy */
    public TaxonomyActor(ExpressionManager em, Policy policy) {
        expressionManager = em;
        this.policy = policy;
    }

    /** @return single vector of synonyms (necessary for Equivalents, for example) */
    public Collection<ConceptExpression> getClassSynonyms() {
        Collection<ConceptExpression> toReturn = new ArrayList<ConceptExpression>();
        if (!acc.isEmpty()) {
            for (Expression e : acc.get(0)) {
                toReturn.add((ConceptExpression) e);
            }
        }
        return toReturn;
    }

    /** @return individual synonyms */
    public Collection<IndividualName> getIndividualSynonyms() {
        Collection<IndividualName> toReturn = new ArrayList<IndividualName>();
        if (!acc.isEmpty()) {
            for (Expression e : acc.get(0)) {
                toReturn.add((IndividualName) e);
            }
        }
        return toReturn;
    }

    /** @return object property synonyms */
    public Collection<ObjectRoleExpression> getObjectPropertySynonyms() {
        Collection<ObjectRoleExpression> toReturn = new ArrayList<ObjectRoleExpression>();
        if (!acc.isEmpty()) {
            for (Expression e : acc.get(0)) {
                toReturn.add((ObjectRoleExpression) e);
            }
        }
        return toReturn;
    }

    /** @return data property synonyms */
    public Collection<DataRoleExpression> getDataPropertySynonyms() {
        Collection<DataRoleExpression> toReturn = new ArrayList<DataRoleExpression>();
        if (!acc.isEmpty()) {
            for (Expression e : acc.get(0)) {
                toReturn.add((DataRoleExpression) e);
            }
        }
        return toReturn;
    }

    /** @return plain individual */
    public Collection<IndividualExpression> getPlainIndividualElements() {
        Collection<IndividualExpression> toReturn = new ArrayList<IndividualExpression>(
                plain.size());
        for (Expression e : plain) {
            toReturn.add((IndividualExpression) e);
        }
        return toReturn;
    }

    /** @return plain class */
    public Collection<ConceptExpression> getPlainClassElements() {
        Collection<ConceptExpression> toReturn = new ArrayList<ConceptExpression>(
                plain.size());
        for (Expression e : plain) {
            toReturn.add((ConceptExpression) e);
        }
        return toReturn;
    }

    /** @return 2D array of all required elements of the taxonomy */
    public Collection<Collection<ConceptExpression>> getClassElements() {
        Collection<Collection<ConceptExpression>> toReturn = new ArrayList<Collection<ConceptExpression>>();
        for (List<Expression> l : acc) {
            List<ConceptExpression> list = new ArrayList<ConceptExpression>();
            for (Expression e : l) {
                list.add((ConceptExpression) e);
            }
            toReturn.add(list);
        }
        return toReturn;
    }

    /** @return 2D array of all required elements of the taxonomy */
    public Collection<Collection<ObjectRoleExpression>> getObjectPropertyElements() {
        Collection<Collection<ObjectRoleExpression>> toReturn = new ArrayList<Collection<ObjectRoleExpression>>();
        for (List<Expression> l : acc) {
            List<ObjectRoleExpression> list = new ArrayList<ObjectRoleExpression>();
            for (Expression e : l) {
                list.add((ObjectRoleExpression) e);
            }
            toReturn.add(list);
        }
        return toReturn;
    }

    /** @return data properties */
    public Collection<Collection<DataRoleExpression>> getDataPropertyElements() {
        Collection<Collection<DataRoleExpression>> toReturn = new ArrayList<Collection<DataRoleExpression>>();
        for (List<Expression> l : acc) {
            List<DataRoleExpression> list = new ArrayList<DataRoleExpression>();
            for (Expression e : l) {
                list.add((DataRoleExpression) e);
            }
            toReturn.add(list);
        }
        return toReturn;
    }

    /** @return 2D array of all required elements of the taxonomy */
    public Collection<Collection<IndividualExpression>> getIndividualElements() {
        Collection<Collection<IndividualExpression>> toReturn = new ArrayList<Collection<IndividualExpression>>();
        for (List<Expression> l : acc) {
            List<IndividualExpression> list = new ArrayList<IndividualExpression>();
            for (Expression e : l) {
                list.add((IndividualExpression) e);
            }
            toReturn.add(list);
        }
        return toReturn;
    }

    /** taxonomy walking method. */
    /** @return true if node was processed, and there is no need to go further,
     *         false if node can not be processed in current settings */
    @Override
    public boolean apply(TaxonomyVertex v) {
        List<Expression> syn = tryEntry(v.getPrimer());
        for (ClassifiableEntry p : v.begin_syn()) {
            syn.addAll(tryEntry(p));
        }
        /** no applicable elements were found */
        if (syn.isEmpty()) {
            return false;
        }
        if (policy.needPlain()) {
            plain.addAll(syn);
        } else {
            acc.add(syn);
        }
        return true;
    }
}
