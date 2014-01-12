package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDatatypeNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDatatypeNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.Facet;
import uk.ac.manchester.cs.jfact.datatypes.Facets;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomConceptInclusion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDRoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDifferentIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDisjointUnion;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentConcepts;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentDRoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomEquivalentORoles;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomInstanceOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleDomain;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleRange;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomORoleSubsumption;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedTo;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRelatedToNot;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleAsymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverse;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleInverseFunctional;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleIrreflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleReflexive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleSymmetric;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomRoleTransitive;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomSameIndividuals;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOf;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomValueOfNot;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Entity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleComplexExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;

/** translation stuff */
public class TranslationMachinery implements Serializable {
    private static final long serialVersionUID = 11000L;
    private final AxiomTranslator axiomTranslator;
    private final ClassExpressionTranslator classExpressionTranslator;
    private final DataRangeTranslator dataRangeTranslator;
    private final ObjectPropertyTranslator objectPropertyTranslator;
    private final DataPropertyTranslator dataPropertyTranslator;
    private final IndividualTranslator individualTranslator;
    private final EntailmentChecker entailmentChecker;
    private final Map<OWLAxiom, AxiomInterface> axiom2PtrMap = new HashMap<OWLAxiom, AxiomInterface>();
    private final Map<AxiomInterface, OWLAxiom> ptr2AxiomMap = new HashMap<AxiomInterface, OWLAxiom>();
    protected final ReasoningKernel kernel;
    protected final ExpressionManager em;
    protected final OWLDataFactory df;
    protected final DatatypeFactory datatypefactory;

    /** @param kernel
     *            kernel
     * @param df
     *            df
     * @param factory
     *            factory */
    public TranslationMachinery(ReasoningKernel kernel, OWLDataFactory df,
            DatatypeFactory factory) {
        this.kernel = kernel;
        datatypefactory = factory;
        em = kernel.getExpressionManager();
        this.df = df;
        axiomTranslator = new AxiomTranslator();
        classExpressionTranslator = new ClassExpressionTranslator();
        dataRangeTranslator = new DataRangeTranslator();
        objectPropertyTranslator = new ObjectPropertyTranslator();
        dataPropertyTranslator = new DataPropertyTranslator();
        individualTranslator = new IndividualTranslator();
        entailmentChecker = new EntailmentChecker();
    }

    /** @return top object property */
    public ObjectRoleExpression getTopObjectProperty() {
        return em.objectRole(Vocabulary.TOP_OBJECT_PROPERTY);
    }

    /** @return bottom object property */
    public ObjectRoleExpression getBottomObjectProperty() {
        return em.objectRole(Vocabulary.BOTTOM_OBJECT_PROPERTY);
    }

    /** @return top data property */
    public DataRoleExpression getTopDataProperty() {
        return em.dataRole(Vocabulary.TOP_DATA_PROPERTY);
    }

    /** @return bottom data property */
    public DataRoleExpression getBottomDataProperty() {
        return em.dataRole(Vocabulary.BOTTOM_DATA_PROPERTY);
    }

    /** @param axioms
     *            axioms */
    public void loadAxioms(Collection<OWLAxiom> axioms) {
        for (OWLAxiom axiom : axioms) {
            // TODO check valid axioms, such as those involving topDataProperty
            if (!axiom2PtrMap.containsKey(axiom)) {
                AxiomInterface axiomPointer = axiom.accept(axiomTranslator);
                if (axiomPointer != null) {
                    axiom2PtrMap.put(axiom, axiomPointer);
                }
            }
        }
    }

    /** @param axiom
     *            axiom */
    public void retractAxiom(OWLAxiom axiom) {
        AxiomInterface ptr = axiom2PtrMap.get(axiom);
        if (ptr != null) {
            kernel.getOntology().retract(ptr);
            axiom2PtrMap.remove(axiom);
        }
    }

    protected ConceptExpression pointer(OWLClassExpression classExpression) {
        return classExpression.accept(classExpressionTranslator);
    }

    protected DataExpression pointer(OWLDataRange dataRange) {
        return dataRange.accept(dataRangeTranslator);
    }

    protected ObjectRoleExpression
            pointer(OWLObjectPropertyExpression propertyExpression) {
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

    protected DataRoleExpression pointer(OWLDataPropertyExpression propertyExpression) {
        return dataPropertyTranslator.getPointerFromEntity(propertyExpression
                .asOWLDataProperty());
    }

    protected synchronized IndividualName pointer(OWLIndividual individual) {
        if (!individual.isAnonymous()) {
            return individualTranslator.getPointerFromEntity(individual
                    .asOWLNamedIndividual());
        } else {
            return em.individual(individual.toStringID());
        }
    }

    protected synchronized Datatype<?> pointer(OWLDatatype datatype) {
        if (datatype == null) {
            throw new IllegalArgumentException("datatype cannot be null");
        }
        return datatypefactory.getKnownDatatype(datatype.getIRI().toString());
    }

    protected synchronized Literal<?> pointer(OWLLiteral literal) {
        String value = literal.getLiteral();
        if (literal.isRDFPlainLiteral()) {
            value = value + "@" + literal.getLang();
        }
        String string = literal.getDatatype().getIRI().toString();
        Datatype<?> knownDatatype = datatypefactory.getKnownDatatype(string);
        return knownDatatype.buildLiteral(value);
    }

    protected NodeSet<OWLNamedIndividual> translateIndividualPointersToNodeSet(
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

    /** @param inds
     *            inds
     * @return individual set */
    public List<IndividualExpression> translateIndividualSet(Set<OWLIndividual> inds) {
        List<IndividualExpression> l = new ArrayList<IndividualExpression>();
        for (OWLIndividual ind : inds) {
            l.add(pointer(ind));
        }
        return l;
    }

    class EntailmentChecker implements OWLAxiomVisitorEx<Boolean>, Serializable {
        private static final long serialVersionUID = 11000L;

        public EntailmentChecker() {}

        @Override
        public Boolean visit(OWLSubClassOfAxiom axiom) {
            if (axiom.getSuperClass().equals(df.getOWLThing())
                    || axiom.getSubClass().equals(df.getOWLNothing())) {
                return Boolean.TRUE;
            }
            return kernel.isSubsumedBy(pointer(axiom.getSubClass()),
                    pointer(axiom.getSuperClass()));
        }

        @Override
        public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            return kernel.isAsymmetric(pointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
            return kernel.isReflexive(pointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLDisjointClassesAxiom axiom) {
            Set<OWLClassExpression> classExpressions = axiom.getClassExpressions();
            if (classExpressions.size() == 2) {
                Iterator<OWLClassExpression> it = classExpressions.iterator();
                return kernel.isDisjoint(pointer(it.next()), pointer(it.next()));
            } else {
                for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
                    if (!ax.accept(this)) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }
        }

        @Override
        public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
                if (!ax.accept(this)) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
            for (OWLSubClassOfAxiom ax : axiom.asOWLSubClassOfAxioms()) {
                if (!ax.accept(this)) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        // TODO: this check is incomplete
        @Override
        public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
            List<OWLDataPropertyExpression> l = new ArrayList<OWLDataPropertyExpression>(
                    axiom.getProperties());
            for (int i = 0; i < l.size() - 1; i++) {
                for (int j = i + 1; j < l.size(); j++) {
                    if (!kernel.isDisjointRoles(pointer(l.get(i)), pointer(l.get(i)))) {
                        return Boolean.FALSE;
                    }
                }
            }
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
            List<OWLObjectPropertyExpression> l = new ArrayList<OWLObjectPropertyExpression>(
                    axiom.getProperties());
            for (int i = 0; i < l.size() - 1; i++) {
                for (int j = i + 1; j < l.size(); j++) {
                    if (!kernel.isDisjointRoles(pointer(l.get(i)), pointer(l.get(i)))) {
                        return Boolean.FALSE;
                    }
                }
            }
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
            return kernel.isFunctional(pointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
            return kernel.isSubRoles(pointer(axiom.getSubProperty()),
                    pointer(axiom.getSuperProperty()));
        }

        @Override
        public Boolean visit(OWLDisjointUnionAxiom axiom) {
            return axiom.getOWLEquivalentClassesAxiom().accept(this)
                    && axiom.getOWLDisjointClassesAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLDeclarationAxiom axiom) {
            return Boolean.FALSE;
        }

        @Override
        public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
            return Boolean.FALSE;
        }

        @Override
        public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
            return kernel.isSymmetric(pointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
            return kernel.isFunctional(pointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
            for (OWLAxiom ax : axiom.asSubDataPropertyOfAxioms()) {
                if (!ax.accept(this)) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(OWLClassAssertionAxiom axiom) {
            return kernel.isInstance(pointer(axiom.getIndividual()),
                    pointer(axiom.getClassExpression()));
        }

        @Override
        public Boolean visit(OWLEquivalentClassesAxiom axiom) {
            Set<OWLClassExpression> classExpressionSet = axiom.getClassExpressions();
            if (classExpressionSet.size() == 2) {
                Iterator<OWLClassExpression> it = classExpressionSet.iterator();
                return kernel.isEquivalent(pointer(it.next()), pointer(it.next()));
            } else {
                for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
                    if (!ax.accept(this)) {
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;
            }
        }

        @Override
        public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
            return kernel.isTransitive(pointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            return kernel.isIrreflexive(pointer(axiom.getProperty()));
        }

        // TODO: this is incomplete
        @Override
        public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
            return kernel.isSubRoles(pointer(axiom.getSubProperty()),
                    pointer(axiom.getSuperProperty()));
        }

        @Override
        public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            return kernel.isInverseFunctional(pointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLSameIndividualAxiom axiom) {
            for (OWLSameIndividualAxiom ax : axiom.asPairwiseAxioms()) {
                Iterator<OWLIndividual> it = ax.getIndividuals().iterator();
                OWLIndividual indA = it.next();
                OWLIndividual indB = it.next();
                if (!kernel.isSameIndividuals(pointer(indA), pointer(indB))) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
            List<ObjectRoleExpression> l = new ArrayList<ObjectRoleExpression>();
            for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
                l.add(pointer(p));
            }
            return kernel.isSubChain(pointer(axiom.getSuperProperty()), l);
        }

        @Override
        public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
            for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
                if (!ax.accept(this)) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(OWLHasKeyAxiom axiom) {
            // FIXME!! unsupported by FaCT++ ATM
            // return null;
            throw new UnsupportedEntailmentTypeException(axiom);
        }

        @Override
        public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
            // FIXME!! unsupported by FaCT++ ATM
            // return null;
            throw new UnsupportedEntailmentTypeException(axiom);
        }

        @Override
        public Boolean visit(SWRLRule rule) {
            // FIXME!! unsupported by FaCT++ ATM
            // return null;
            throw new UnsupportedEntailmentTypeException(rule);
        }

        @Override
        public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            return Boolean.FALSE;
        }

        @Override
        public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
            return Boolean.FALSE;
        }

        @Override
        public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
            return Boolean.FALSE;
        }
    }

    abstract class OWLEntityTranslator<E extends OWLObject, T extends Entity> implements
            Serializable {
        private static final long serialVersionUID = 11000L;
        private final Map<E, T> entity2dlentity = new HashMap<E, T>();
        private final Map<T, E> dlentity2entity = new HashMap<T, E>();

        protected void fillMaps(E entity, T dlentity) {
            this.entity2dlentity.put(entity, dlentity);
            this.dlentity2entity.put(dlentity, entity);
        }

        protected OWLEntityTranslator() {
            E topEntity = this.getTopEntity();
            if (topEntity != null) {
                this.fillMaps(topEntity, this.getTopEntityPointer());
            }
            E bottomEntity = this.getBottomEntity();
            if (bottomEntity != null) {
                this.fillMaps(bottomEntity, this.getBottomEntityPointer());
            }
        }

        protected T registerNewEntity(E entity) {
            T pointer = this.createPointerForEntity(entity);
            this.fillMaps(entity, pointer);
            return pointer;
        }

        public E getEntityFromPointer(T pointer) {
            return this.dlentity2entity.get(pointer);
        }

        public T getPointerFromEntity(E entity) {
            T pointer = this.entity2dlentity.get(entity);
            if (pointer == null) {
                pointer = this.registerNewEntity(entity);
            }
            return pointer;
        }

        public Node<E> getNodeFromPointers(Collection<T> pointers) {
            DefaultNode<E> node = this.createDefaultNode();
            for (T pointer : pointers) {
                node.add(this.getEntityFromPointer(pointer));
            }
            return node;
        }

        public NodeSet<E> getNodeSetFromPointers(Collection<Collection<T>> pointers) {
            DefaultNodeSet<E> nodeSet = this.createDefaultNodeSet();
            for (Collection<T> pointerArray : pointers) {
                nodeSet.addNode(this.getNodeFromPointers(pointerArray));
            }
            return nodeSet;
        }

        protected abstract DefaultNode<E> createDefaultNode();

        protected abstract DefaultNodeSet<E> createDefaultNodeSet();

        protected abstract T getTopEntityPointer();

        protected abstract T getBottomEntityPointer();

        protected abstract T createPointerForEntity(E entity);

        protected abstract E getTopEntity();

        protected abstract E getBottomEntity();
    }

    class ObjectPropertyTranslator extends
            OWLEntityTranslator<OWLObjectPropertyExpression, ObjectRoleExpression> {
        private static final long serialVersionUID = 11000L;

        public ObjectPropertyTranslator() {}

        @Override
        protected ObjectRoleExpression getTopEntityPointer() {
            return getTopObjectProperty();
        }

        @Override
        protected ObjectRoleExpression getBottomEntityPointer() {
            return getBottomObjectProperty();
        }

        @Override
        protected ObjectRoleExpression registerNewEntity(
                OWLObjectPropertyExpression entity) {
            ObjectRoleExpression pointer = createPointerForEntity(entity);
            fillMaps(entity, pointer);
            OWLObjectPropertyExpression inverseentity = entity.getInverseProperty()
                    .getSimplified();
            fillMaps(inverseentity, createPointerForEntity(inverseentity));
            return pointer;
        }

        @Override
        protected ObjectRoleExpression createPointerForEntity(
                OWLObjectPropertyExpression entity) {
            // FIXME!! think later!!
            ObjectRoleExpression p = em
                    .objectRole(entity.getNamedProperty().toStringID());
            if (entity.isAnonymous()) {
                p = em.inverse(p);
            }
            return p;
        }

        @Override
        protected OWLObjectProperty getTopEntity() {
            return df.getOWLTopObjectProperty();
        }

        @Override
        protected OWLObjectProperty getBottomEntity() {
            return df.getOWLBottomObjectProperty();
        }

        @Override
        protected DefaultNode<OWLObjectPropertyExpression> createDefaultNode() {
            return new OWLObjectPropertyNode();
        }

        @Override
        protected DefaultNodeSet<OWLObjectPropertyExpression> createDefaultNodeSet() {
            return new OWLObjectPropertyNodeSet();
        }
    }

    class ComplexObjectPropertyTranslator extends
            OWLEntityTranslator<OWLObjectPropertyExpression, ObjectRoleComplexExpression> {
        private static final long serialVersionUID = 11000L;

        public ComplexObjectPropertyTranslator() {}

        @Override
        protected ObjectRoleComplexExpression getTopEntityPointer() {
            return getTopObjectProperty();
        }

        @Override
        protected ObjectRoleComplexExpression getBottomEntityPointer() {
            return getBottomObjectProperty();
        }

        @Override
        protected ObjectRoleComplexExpression registerNewEntity(
                OWLObjectPropertyExpression entity) {
            ObjectRoleComplexExpression pointer = createPointerForEntity(entity);
            fillMaps(entity, pointer);
            OWLObjectPropertyExpression inverseentity = entity.getInverseProperty()
                    .getSimplified();
            fillMaps(inverseentity, createPointerForEntity(inverseentity));
            return pointer;
        }

        @Override
        protected ObjectRoleComplexExpression createPointerForEntity(
                OWLObjectPropertyExpression entity) {
            ObjectRoleComplexExpression p = em.objectRole(entity.getNamedProperty()
                    .toStringID());
            return p;
        }

        @Override
        protected OWLObjectProperty getTopEntity() {
            return df.getOWLTopObjectProperty();
        }

        @Override
        protected OWLObjectProperty getBottomEntity() {
            return df.getOWLBottomObjectProperty();
        }

        @Override
        protected DefaultNode<OWLObjectPropertyExpression> createDefaultNode() {
            return new OWLObjectPropertyNode();
        }

        @Override
        protected DefaultNodeSet<OWLObjectPropertyExpression> createDefaultNodeSet() {
            return new OWLObjectPropertyNodeSet();
        }
    }

    protected class DeclarationVisitorEx implements OWLEntityVisitorEx<AxiomInterface>,
            Serializable {
        private static final long serialVersionUID = 11000L;

        @Override
        public AxiomInterface visit(OWLClass cls) {
            return kernel.getOntology().add(
                    new AxiomDeclaration(df.getOWLDeclarationAxiom(cls), pointer(cls)));
        }

        @Override
        public AxiomInterface visit(OWLObjectProperty property) {
            return kernel.getOntology().add(
                    new AxiomDeclaration(df.getOWLDeclarationAxiom(property),
                            pointer(property)));
        }

        @Override
        public AxiomInterface visit(OWLDataProperty property) {
            return kernel.getOntology().add(
                    new AxiomDeclaration(df.getOWLDeclarationAxiom(property),
                            pointer(property)));
        }

        @Override
        public AxiomInterface visit(OWLNamedIndividual individual) {
            return kernel.getOntology().add(
                    new AxiomDeclaration(df.getOWLDeclarationAxiom(individual),
                            pointer(individual)));
        }

        @Override
        public AxiomInterface visit(OWLDatatype datatype) {
            return kernel.getOntology().add(
                    new AxiomDeclaration(df.getOWLDeclarationAxiom(datatype),
                            pointer(datatype)));
        }

        @Override
        public AxiomInterface visit(OWLAnnotationProperty property) {
            return null;
        }
    }

    class AxiomTranslator implements OWLAxiomVisitorEx<AxiomInterface>, Serializable {
        private static final long serialVersionUID = 11000L;
        private final DeclarationVisitorEx v;

        public AxiomTranslator() {
            v = new DeclarationVisitorEx();
        }

        @Override
        public AxiomInterface visit(OWLSubClassOfAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomConceptInclusion(axiom, pointer(axiom.getSubClass()),
                            pointer(axiom.getSuperClass())));
        }

        @Override
        public AxiomInterface visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRelatedToNot(axiom, pointer(axiom.getSubject()),
                            pointer(axiom.getProperty()), pointer(axiom.getObject())));
        }

        @Override
        public AxiomInterface visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRoleAsymmetric(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLReflexiveObjectPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRoleReflexive(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLDisjointClassesAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDisjointConcepts(axiom, translateClassExpressionSet(axiom
                            .getClassExpressions())));
        }

        private List<ConceptExpression> translateClassExpressionSet(
                Set<OWLClassExpression> classExpressions) {
            List<ConceptExpression> l = new ArrayList<ConceptExpression>();
            for (OWLClassExpression ce : classExpressions) {
                l.add(pointer(ce));
            }
            return l;
        }

        @Override
        public AxiomInterface visit(OWLDataPropertyDomainAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDRoleDomain(axiom, pointer(axiom.getProperty()),
                            pointer(axiom.getDomain())));
        }

        @Override
        public AxiomInterface visit(OWLObjectPropertyDomainAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomORoleDomain(axiom, pointer(axiom.getProperty()),
                            pointer(axiom.getDomain())));
        }

        @Override
        public AxiomInterface visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomEquivalentORoles(axiom, translateObjectPropertySet(axiom
                            .getProperties())));
        }

        private List<ObjectRoleExpression> translateObjectPropertySet(
                Collection<OWLObjectPropertyExpression> properties) {
            List<ObjectRoleExpression> l = new ArrayList<ObjectRoleExpression>();
            for (OWLObjectPropertyExpression property : properties) {
                l.add(pointer(property));
            }
            return l;
        }

        @Override
        public AxiomInterface visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomValueOfNot(axiom, pointer(axiom.getSubject()), pointer(axiom
                            .getProperty()), pointer(axiom.getObject())));
        }

        @Override
        public AxiomInterface visit(OWLDifferentIndividualsAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDifferentIndividuals(axiom, translateIndividualSet(axiom
                            .getIndividuals())));
        }

        @Override
        public AxiomInterface visit(OWLDisjointDataPropertiesAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDisjointDRoles(axiom, translateDataPropertySet(axiom
                            .getProperties())));
        }

        private List<DataRoleExpression> translateDataPropertySet(
                Set<OWLDataPropertyExpression> properties) {
            List<DataRoleExpression> l = new ArrayList<DataRoleExpression>();
            for (OWLDataPropertyExpression property : properties) {
                l.add(pointer(property));
            }
            return l;
        }

        @Override
        public AxiomInterface visit(OWLDisjointObjectPropertiesAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDisjointORoles(axiom, translateObjectPropertySet(axiom
                            .getProperties())));
        }

        @Override
        public AxiomInterface visit(OWLObjectPropertyRangeAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomORoleRange(axiom, pointer(axiom.getProperty()),
                            pointer(axiom.getRange())));
        }

        @Override
        public AxiomInterface visit(OWLObjectPropertyAssertionAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRelatedTo(axiom, pointer(axiom.getSubject()), pointer(axiom
                            .getProperty()), pointer(axiom.getObject())));
        }

        @Override
        public AxiomInterface visit(OWLFunctionalObjectPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomORoleFunctional(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLSubObjectPropertyOfAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomORoleSubsumption(axiom, pointer(axiom.getSubProperty()),
                            pointer(axiom.getSuperProperty())));
        }

        @Override
        public AxiomInterface visit(OWLDisjointUnionAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDisjointUnion(axiom, pointer(axiom.getOWLClass()),
                            translateClassExpressionSet(axiom.getClassExpressions())));
        }

        @Override
        public AxiomInterface visit(OWLDeclarationAxiom axiom) {
            OWLEntity entity = axiom.getEntity();
            return entity.accept(v);
        }

        @Override
        public AxiomInterface visit(OWLAnnotationAssertionAxiom axiom) {
            // Ignore
            return null;
        }

        @Override
        public AxiomInterface visit(OWLSymmetricObjectPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRoleSymmetric(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLDataPropertyRangeAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDRoleRange(axiom, pointer(axiom.getProperty()),
                            pointer(axiom.getRange())));
        }

        @Override
        public AxiomInterface visit(OWLFunctionalDataPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDRoleFunctional(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLEquivalentDataPropertiesAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomEquivalentDRoles(axiom, translateDataPropertySet(axiom
                            .getProperties())));
        }

        @Override
        public AxiomInterface visit(OWLClassAssertionAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomInstanceOf(axiom, pointer(axiom.getIndividual()),
                            pointer(axiom.getClassExpression())));
        }

        @Override
        public AxiomInterface visit(OWLEquivalentClassesAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomEquivalentConcepts(axiom, translateClassExpressionSet(axiom
                            .getClassExpressions())));
        }

        @Override
        public AxiomInterface visit(OWLDataPropertyAssertionAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomValueOf(axiom, pointer(axiom.getSubject()), pointer(axiom
                            .getProperty()), pointer(axiom.getObject())));
        }

        @Override
        public AxiomInterface visit(OWLTransitiveObjectPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRoleTransitive(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRoleIrreflexive(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLSubDataPropertyOfAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomDRoleSubsumption(axiom, pointer(axiom.getSubProperty()),
                            pointer(axiom.getSuperProperty())));
        }

        @Override
        public AxiomInterface visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRoleInverseFunctional(axiom, pointer(axiom.getProperty())));
        }

        @Override
        public AxiomInterface visit(OWLSameIndividualAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomSameIndividuals(axiom, translateIndividualSet(axiom
                            .getIndividuals())));
        }

        @Override
        public AxiomInterface visit(OWLSubPropertyChainOfAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomORoleSubsumption(axiom,
                            em.compose(translateObjectPropertySet(axiom
                                    .getPropertyChain())), pointer(axiom
                                    .getSuperProperty())));
        }

        @Override
        public AxiomInterface visit(OWLInverseObjectPropertiesAxiom axiom) {
            return kernel.getOntology().add(
                    new AxiomRoleInverse(axiom, pointer(axiom.getFirstProperty()),
                            pointer(axiom.getSecondProperty())));
        }

        @Override
        public AxiomInterface visit(OWLHasKeyAxiom axiom) {
            // translateObjectPropertySet(axiom.getObjectPropertyExpressions());
            // TDLObjectRoleExpression objectPropertyPointer = kernel
            // .getObjectPropertyKey();
            // translateDataPropertySet(axiom.getDataPropertyExpressions());
            throw new ReasonerInternalException(
                    "JFact Kernel: unsupported operation 'getDataPropertyKey' " + axiom);
            // TDLDataRoleExpression dataPropertyPointer = kernel
            // .getDataPropertyKey();
            // return kernel.tellHasKey(
            // toClassPointer(axiom.getClassExpression()),
            // dataPropertyPointer, objectPropertyPointer);
        }

        @Override
        public AxiomInterface visit(OWLDatatypeDefinitionAxiom axiom) {
            throw new ReasonerInternalException(
                    "JFact Kernel: unsupported operation 'OWLDatatypeDefinitionAxiom' "
                            + axiom);
            // kernel.getDataSubType(axiom.getDatatype().getIRI().toString(),
            // toDataTypeExpressionPointer(axiom.getDataRange()));
        }

        @Override
        public AxiomInterface visit(SWRLRule rule) {
            // Ignore
            return null;
        }

        @Override
        public AxiomInterface visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            // Ignore
            return null;
        }

        @Override
        public AxiomInterface visit(OWLAnnotationPropertyDomainAxiom axiom) {
            // Ignore
            return null;
        }

        @Override
        public AxiomInterface visit(OWLAnnotationPropertyRangeAxiom axiom) {
            // Ignore
            return null;
        }
    }

    class ClassExpressionTranslator extends
            OWLEntityTranslator<OWLClass, ConceptExpression> implements
            OWLClassExpressionVisitorEx<ConceptExpression> {
        private static final long serialVersionUID = 11000L;

        public ClassExpressionTranslator() {}

        @Override
        protected ConceptExpression getTopEntityPointer() {
            return em.top();
        }

        @Override
        protected ConceptExpression getBottomEntityPointer() {
            return em.bottom();
        }

        @Override
        protected OWLClass getTopEntity() {
            return df.getOWLThing();
        }

        @Override
        protected OWLClass getBottomEntity() {
            return df.getOWLNothing();
        }

        @Override
        protected ConceptExpression createPointerForEntity(OWLClass entity) {
            // XXX many entities would cause a lot of wasted memory
            return em.concept(entity.getIRI().toString());
        }

        @Override
        protected DefaultNode<OWLClass> createDefaultNode() {
            return new OWLClassNode();
        }

        @Override
        protected DefaultNodeSet<OWLClass> createDefaultNodeSet() {
            return new OWLClassNodeSet();
        }

        @Override
        public ConceptExpression visit(OWLClass desc) {
            return getPointerFromEntity(desc);
        }

        @Override
        public ConceptExpression visit(OWLObjectIntersectionOf desc) {
            return em.and(translateClassExpressionSet(desc.getOperands()));
        }

        private List<ConceptExpression> translateClassExpressionSet(
                Set<OWLClassExpression> classExpressions) {
            List<ConceptExpression> l = new ArrayList<ConceptExpression>();
            for (OWLClassExpression ce : classExpressions) {
                l.add(ce.accept(this));
            }
            return l;
        }

        @Override
        public ConceptExpression visit(OWLObjectUnionOf desc) {
            return em.or(translateClassExpressionSet(desc.getOperands()));
        }

        @Override
        public ConceptExpression visit(OWLObjectComplementOf desc) {
            return em.not(desc.getOperand().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectSomeValuesFrom desc) {
            return em.exists(pointer(desc.getProperty()), desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectAllValuesFrom desc) {
            return em.forall(pointer(desc.getProperty()), desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectHasValue desc) {
            return em.value(pointer(desc.getProperty()), pointer(desc.getValue()));
        }

        @Override
        public ConceptExpression visit(OWLObjectMinCardinality desc) {
            return em.minCardinality(desc.getCardinality(), pointer(desc.getProperty()),
                    desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectExactCardinality desc) {
            return em.cardinality(desc.getCardinality(), pointer(desc.getProperty()),
                    desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectMaxCardinality desc) {
            return em.maxCardinality(desc.getCardinality(), pointer(desc.getProperty()),
                    desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectHasSelf desc) {
            return em.selfReference(pointer(desc.getProperty()));
        }

        @Override
        public ConceptExpression visit(OWLObjectOneOf desc) {
            return em.oneOf(translateIndividualSet(desc.getIndividuals()));
        }

        @Override
        public ConceptExpression visit(OWLDataSomeValuesFrom desc) {
            return em.exists(pointer(desc.getProperty()), pointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataAllValuesFrom desc) {
            return em.forall(pointer(desc.getProperty()), pointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataHasValue desc) {
            return em.value(pointer(desc.getProperty()), pointer(desc.getValue()));
        }

        @Override
        public ConceptExpression visit(OWLDataMinCardinality desc) {
            return em.minCardinality(desc.getCardinality(), pointer(desc.getProperty()),
                    pointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataExactCardinality desc) {
            return em.cardinality(desc.getCardinality(), pointer(desc.getProperty()),
                    pointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataMaxCardinality desc) {
            return em.maxCardinality(desc.getCardinality(), pointer(desc.getProperty()),
                    pointer(desc.getFiller()));
        }
    }

    class DataPropertyTranslator extends
            OWLEntityTranslator<OWLDataProperty, DataRoleExpression> {
        private static final long serialVersionUID = 11000L;

        public DataPropertyTranslator() {}

        @Override
        protected DataRoleExpression getTopEntityPointer() {
            return getTopDataProperty();
        }

        @Override
        protected DataRoleExpression getBottomEntityPointer() {
            return getBottomDataProperty();
        }

        @Override
        protected DataRoleExpression createPointerForEntity(OWLDataProperty entity) {
            return em.dataRole(entity.toStringID());
        }

        @Override
        protected OWLDataProperty getTopEntity() {
            return df.getOWLTopDataProperty();
        }

        @Override
        protected OWLDataProperty getBottomEntity() {
            return df.getOWLBottomDataProperty();
        }

        @Override
        protected DefaultNode<OWLDataProperty> createDefaultNode() {
            return new OWLDataPropertyNode();
        }

        @Override
        protected DefaultNodeSet<OWLDataProperty> createDefaultNodeSet() {
            return new OWLDataPropertyNodeSet();
        }
    }

    class DataRangeTranslator extends OWLEntityTranslator<OWLDatatype, DataExpression>
            implements OWLDataRangeVisitorEx<DataExpression> {
        private static final long serialVersionUID = 11000L;

        public DataRangeTranslator() {}

        @Override
        protected DataExpression getTopEntityPointer() {
            return em.dataTop();
        }

        @Override
        protected DataExpression getBottomEntityPointer() {
            return null;
        }

        @Override
        protected DefaultNode<OWLDatatype> createDefaultNode() {
            return new OWLDatatypeNode();
        }

        @Override
        protected OWLDatatype getTopEntity() {
            return df.getTopDatatype();
        }

        @Override
        protected OWLDatatype getBottomEntity() {
            return null;
        }

        @Override
        protected DefaultNodeSet<OWLDatatype> createDefaultNodeSet() {
            return new OWLDatatypeNodeSet();
        }

        @Override
        protected DataExpression createPointerForEntity(OWLDatatype entity) {
            return datatypefactory.getKnownDatatype(entity.getIRI().toString());
        }

        @Override
        public Datatype<?> visit(OWLDatatype node) {
            return datatypefactory.getKnownDatatype(node.getIRI().toString());
        }

        @Override
        public DataExpression visit(OWLDataOneOf node) {
            List<Literal<?>> l = new ArrayList<Literal<?>>();
            for (OWLLiteral literal : node.getValues()) {
                l.add(pointer(literal));
            }
            return em.dataOneOf(l);
        }

        @Override
        public DataExpression visit(OWLDataComplementOf node) {
            return em.dataNot(node.getDataRange().accept(this));
        }

        @Override
        public DataExpression visit(OWLDataIntersectionOf node) {
            return em.dataAnd(translateDataRangeSet(node.getOperands()));
        }

        private List<DataExpression> translateDataRangeSet(Set<OWLDataRange> dataRanges) {
            List<DataExpression> l = new ArrayList<DataExpression>();
            for (OWLDataRange op : dataRanges) {
                l.add(op.accept(this));
            }
            return l;
        }

        @Override
        public DataExpression visit(OWLDataUnionOf node) {
            return em.dataOr(translateDataRangeSet(node.getOperands()));
        }

        @Override
        public DataExpression visit(OWLDatatypeRestriction node) {
            DatatypeExpression<?> toReturn = null;
            Datatype<?> type = datatypefactory.getKnownDatatype(node.getDatatype()
                    .getIRI().toString());
            Set<OWLFacetRestriction> facetRestrictions = node.getFacetRestrictions();
            if (facetRestrictions.isEmpty()) {
                return type;
            }
            if (type.isNumericDatatype()) {
                toReturn = DatatypeFactory.getNumericDatatypeExpression(type
                        .asNumericDatatype());
            } else if (type.isOrderedDatatype()) {
                toReturn = DatatypeFactory.getOrderedDatatypeExpression(type);
            } else {
                toReturn = DatatypeFactory.getDatatypeExpression(type);
            }
            for (OWLFacetRestriction restriction : facetRestrictions) {
                Literal<?> dv = pointer(restriction.getFacetValue());
                Facet facet = Facets.parse(restriction.getFacet());
                if (facet.isNumberFacet()) {
                    toReturn = toReturn.addNumericFacet(facet, dv.typedValue());
                } else {
                    toReturn = toReturn.addNonNumericFacet(facet, dv.typedValue());
                }
            }
            return toReturn;
        }
    }

    class IndividualTranslator extends
            OWLEntityTranslator<OWLNamedIndividual, IndividualName> {
        private static final long serialVersionUID = 11000L;

        public IndividualTranslator() {}

        @Override
        protected IndividualName getTopEntityPointer() {
            return null;
        }

        @Override
        protected IndividualName getBottomEntityPointer() {
            return null;
        }

        @Override
        protected IndividualName createPointerForEntity(OWLNamedIndividual entity) {
            return em.individual(entity.toStringID());
        }

        @Override
        protected OWLNamedIndividual getTopEntity() {
            return null;
        }

        @Override
        protected OWLNamedIndividual getBottomEntity() {
            return null;
        }

        @Override
        protected DefaultNode<OWLNamedIndividual> createDefaultNode() {
            return new OWLNamedIndividualNode();
        }

        @Override
        protected DefaultNodeSet<OWLNamedIndividual> createDefaultNodeSet() {
            return new OWLNamedIndividualNodeSet();
        }
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
    public EntailmentChecker getEntailmentChecker() {
        return entailmentChecker;
    }

    /** @param trace
     *            trace
     * @return trnslated set */
    public Set<OWLAxiom> translateTAxiomSet(Collection<AxiomInterface> trace) {
        Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
        for (AxiomInterface ap : trace) {
            ret.add(ptr2AxiomMap.get(ap));
        }
        return ret;
    }
}
