package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static java.util.stream.Collectors.toSet;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.Axioms;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;

/** translation stuff */
public class TranslationMachinery implements Serializable {

    private static final long serialVersionUID = 11000L;
    @Nonnull
    private final AxiomTranslator axiomTranslator;
    @Nonnull
    private final ClassExpressionTranslator classExpressionTranslator;
    @Nonnull
    private final DataRangeTranslator dataRangeTranslator;
    @Nonnull
    private final ObjectPropertyTranslator objectPropertyTranslator;
    @Nonnull
    private final DataPropertyTranslator dataPropertyTranslator;
    @Nonnull
    private final IndividualTranslator individualTranslator;
    @Nonnull
    private final EntailmentChecker entailmentChecker;
    @Nonnull
    private final Map<OWLAxiom, AxiomInterface> axiom2PtrMap = new HashMap<>();
    @Nonnull
    private final Map<AxiomInterface, OWLAxiom> ptr2AxiomMap = new HashMap<>();
    protected final ReasoningKernel kernel;
    protected final ExpressionCache em;
    protected final OWLDataFactory df;
    protected final DatatypeFactory datatypefactory;

    /**
     * @param kernel
     *        kernel
     * @param df
     *        df
     * @param factory
     *        factory
     */
    public TranslationMachinery(ReasoningKernel kernel, OWLDataFactory df,
            DatatypeFactory factory) {
        this.kernel = kernel;
        datatypefactory = factory;
        em = kernel.getExpressionManager();
        this.df = df;
        axiomTranslator = new AxiomTranslator(kernel.getOntology(), df, this);
        classExpressionTranslator = new ClassExpressionTranslator(em, df, this);
        dataRangeTranslator = new DataRangeTranslator(em, df, this,
                datatypefactory);
        objectPropertyTranslator = new ObjectPropertyTranslator(em, df, this);
        dataPropertyTranslator = new DataPropertyTranslator(em, df, this);
        individualTranslator = new IndividualTranslator(em, df, this);
        entailmentChecker = new EntailmentChecker(kernel, df, this);
    }

    /**
     * @param signature
     *        signature
     * @return expressions
     */
    public List<Expression> translateExpressions(Set<OWLEntity> signature) {
        List<Expression> list = new ArrayList<>();
        for (OWLEntity entity : signature) {
            Expression ex = entity.accept(new EntityVisitorEx(this));
            if (ex != Axioms.dummyExpression()) {
                list.add(ex);
            }
        }
        return list;
    }

    /**
     * @param axioms
     *        axioms
     */
    public void loadAxioms(Collection<OWLAxiom> axioms) {
        for (OWLAxiom axiom : axioms) {
            // TODO check valid axioms, such as those involving topDataProperty
            if (!axiom2PtrMap.containsKey(axiom)) {
                AxiomInterface axiomPointer = axiom.accept(axiomTranslator);
                if (axiomPointer != Axioms.dummy()) {
                    axiom2PtrMap.put(axiom, axiomPointer);
                    ptr2AxiomMap.put(axiomPointer, axiom);
                }
            }
        }
    }

    /**
     * @param axiom
     *        axiom
     */
    public void retractAxiom(OWLAxiom axiom) {
        AxiomInterface ptr = axiom2PtrMap.get(axiom);
        if (ptr != null) {
            kernel.getOntology().retract(ptr);
            axiom2PtrMap.remove(axiom);
            ptr2AxiomMap.remove(ptr);
        }
    }

    @Nonnull
    protected ConceptExpression pointer(OWLClassExpression classExpression) {
        return classExpression.accept(classExpressionTranslator);
    }

    @Nonnull
    protected DataExpression pointer(OWLDataRange dataRange) {
        return dataRange.accept(dataRangeTranslator);
    }

    @Nonnull
    protected ObjectRoleExpression pointer(
            OWLObjectPropertyExpression propertyExpression) {
        OWLObjectPropertyExpression simp = propertyExpression.getSimplified();
        if (simp.isAnonymous()) {
            OWLObjectInverseOf inv = (OWLObjectInverseOf) simp;
            return em.inverse(objectPropertyTranslator.getPointerFromEntity(inv
                    .getInverse().asOWLObjectProperty()));
        } else {
            return objectPropertyTranslator.getPointerFromEntity(simp
                    .asOWLObjectProperty());
        }
    }

    @Nonnull
    protected DataRoleExpression pointer(
            OWLDataPropertyExpression propertyExpression) {
        return dataPropertyTranslator.getPointerFromEntity(propertyExpression
                .asOWLDataProperty());
    }

    @Nonnull
    protected synchronized IndividualName pointer(OWLIndividual individual) {
        if (!individual.isAnonymous()) {
            return individualTranslator.getPointerFromEntity(individual
                    .asOWLNamedIndividual());
        } else {
            return em.individual(IRI.create(individual.toStringID()));
        }
    }

    @Nonnull
    protected synchronized Datatype<?> pointer(OWLDatatype datatype) {
        checkNotNull(datatype, "datatype cannot be null");
        return datatypefactory.getKnownDatatype(datatype.getIRI());
    }

    @Nonnull
    protected synchronized Literal<?> pointer(OWLLiteral literal) {
        String value = literal.getLiteral();
        if (literal.isRDFPlainLiteral()) {
            value = value + '@' + literal.getLang();
        }
        IRI string = literal.getDatatype().getIRI();
        Datatype<?> knownDatatype = datatypefactory.getKnownDatatype(string);
        return knownDatatype.buildLiteral(value);
    }

    @Nonnull
    protected NodeSet<OWLNamedIndividual> translateNodeSet(
            Iterable<IndividualExpression> pointers) {
        OWLNamedIndividualNodeSet ns = new OWLNamedIndividualNodeSet();
        for (IndividualExpression pointer : pointers) {
            if (pointer instanceof IndividualName) {
                OWLNamedIndividual ind = individualTranslator
                        .getEntityFromPointer((IndividualName) pointer);
                // XXX skipping anonymous individuals - counterintuitive but
                // that's the specs for you
                if (ind != null) {
                    ns.addEntity(ind);
                }
            }
        }
        return ns;
    }

    /**
     * @param inds
     *        inds
     * @return individual set
     */
    public List<IndividualExpression> translate(
            Stream<? extends OWLIndividual> inds) {
        return asList(inds.map(i -> pointer(i)));
    }

    /** @return class translation */
    public ClassExpressionTranslator getClassExpressionTranslator() {
        return classExpressionTranslator;
    }

    /** @return data range translator */
    public DataRangeTranslator getDataRangeTranslator() {
        return dataRangeTranslator;
    }

    /** @return object property translator */
    public ObjectPropertyTranslator getObjectPropertyTranslator() {
        return objectPropertyTranslator;
    }

    /** @return data property translator */
    public DataPropertyTranslator getDataPropertyTranslator() {
        return dataPropertyTranslator;
    }

    /** @return individual translator */
    public IndividualTranslator getIndividualTranslator() {
        return individualTranslator;
    }

    /** @return entailemnt checker */
    @Nonnull
    public EntailmentChecker getEntailmentChecker() {
        return entailmentChecker;
    }

    /**
     * @param trace
     *        trace
     * @return trnslated set
     */
    public Set<OWLAxiom> translateTAxiomSet(Stream<AxiomInterface> trace) {
        return trace.map(ap -> ptr2AxiomMap.get(ap)).collect(toSet());
    }
}
