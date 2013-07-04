package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.*;

import uk.ac.manchester.cs.jfact.datatypes.*;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;

// XXX verify unused parameters
/** translation stuff */
public class TranslationMachinery {
    private volatile AxiomTranslator axiomTranslator;
    private volatile ClassExpressionTranslator classExpressionTranslator;
    private volatile DataRangeTranslator dataRangeTranslator;
    private volatile ObjectPropertyTranslator objectPropertyTranslator;
    private volatile DataPropertyTranslator dataPropertyTranslator;
    private volatile IndividualTranslator individualTranslator;
    private volatile EntailmentChecker entailmentChecker;
    private Map<OWLAxiom, AxiomInterface> axiom2PtrMap = new HashMap<OWLAxiom, AxiomInterface>();
    private Map<AxiomInterface, OWLAxiom> ptr2AxiomMap = new HashMap<AxiomInterface, OWLAxiom>();
    protected ReasoningKernel kernel;
    protected ExpressionManager em;
    protected OWLDataFactory df;
    DatatypeFactory datatypefactory;

    /** @param kernel
     * @param df
     * @param factory */
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

    /** @param axioms */
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

    /** @param axiom */
    public void retractAxiom(OWLAxiom axiom) {
        AxiomInterface ptr = axiom2PtrMap.get(axiom);
        if (ptr != null) {
            kernel.retract(ptr);
            axiom2PtrMap.remove(axiom);
        }
    }

    protected ConceptExpression toClassPointer(OWLClassExpression classExpression) {
        return classExpression.accept(classExpressionTranslator);
    }

    protected DataExpression toDataTypeExpressionPointer(OWLDataRange dataRange) {
        return dataRange.accept(dataRangeTranslator);
    }

    protected ObjectRoleExpression toObjectPropertyPointer(
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

    protected DataRoleExpression toDataPropertyPointer(
            OWLDataPropertyExpression propertyExpression) {
        return dataPropertyTranslator.getPointerFromEntity(propertyExpression
                .asOWLDataProperty());
    }

    protected synchronized IndividualName toIndividualPointer(OWLIndividual individual) {
        if (!individual.isAnonymous()) {
            return individualTranslator.getPointerFromEntity(individual
                    .asOWLNamedIndividual());
        } else {
            return em.individual(individual.toStringID());
        }
    }

    protected synchronized Datatype<?> toDataTypePointer(OWLDatatype datatype) {
        if (datatype == null) {
            throw new IllegalArgumentException("datatype cannot be null");
        }
        return datatypefactory.getKnownDatatype(datatype.getIRI().toString());
    }

    protected synchronized Literal<?> toDataValuePointer(OWLLiteral literal) {
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
            if (pointer != null && pointer instanceof IndividualName) {
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

    protected synchronized List<IndividualExpression> translateIndividualSet(
            Set<OWLIndividual> inds) {
        List<IndividualExpression> l = new ArrayList<IndividualExpression>();
        for (OWLIndividual ind : inds) {
            l.add(toIndividualPointer(ind));
        }
        return l;
    }

    class EntailmentChecker implements OWLAxiomVisitorEx<Boolean> {
        public EntailmentChecker() {}

        @Override
        public Boolean visit(OWLSubClassOfAxiom axiom) {
            if (axiom.getSuperClass().equals(df.getOWLThing())
                    || axiom.getSubClass().equals(df.getOWLNothing())) {
                return Boolean.TRUE;
            }
            return kernel.isSubsumedBy(toClassPointer(axiom.getSubClass()),
                    toClassPointer(axiom.getSuperClass()));
        }

        @Override
        public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            return kernel.isAsymmetric(toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
            return kernel.isReflexive(toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLDisjointClassesAxiom axiom) {
            Set<OWLClassExpression> classExpressions = axiom.getClassExpressions();
            if (classExpressions.size() == 2) {
                Iterator<OWLClassExpression> it = classExpressions.iterator();
                return kernel.isDisjoint(toClassPointer(it.next()),
                        toClassPointer(it.next()));
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
                    if (!kernel.isDisjointRoles(toDataPropertyPointer(l.get(i)),
                            toDataPropertyPointer(l.get(i)))) {
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
                    if (!kernel.isDisjointRoles(toObjectPropertyPointer(l.get(i)),
                            toObjectPropertyPointer(l.get(i)))) {
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
            return kernel.isFunctional(toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
            return kernel.isSubRoles(toObjectPropertyPointer(axiom.getSubProperty()),
                    toObjectPropertyPointer(axiom.getSuperProperty()));
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
            return kernel.isSymmetric(toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
            return axiom.asOWLSubClassOfAxiom().accept(this);
        }

        @Override
        public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
            return kernel.isFunctional(toDataPropertyPointer(axiom.getProperty()));
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
            return kernel.isInstance(toIndividualPointer(axiom.getIndividual()),
                    toClassPointer(axiom.getClassExpression()));
        }

        @Override
        public Boolean visit(OWLEquivalentClassesAxiom axiom) {
            Set<OWLClassExpression> classExpressionSet = axiom.getClassExpressions();
            if (classExpressionSet.size() == 2) {
                Iterator<OWLClassExpression> it = classExpressionSet.iterator();
                return kernel.isEquivalent(toClassPointer(it.next()),
                        toClassPointer(it.next()));
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
            return kernel.isTransitive(toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            return kernel.isIrreflexive(toObjectPropertyPointer(axiom.getProperty()));
        }

        // TODO: this is incomplete
        @Override
        public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
            return kernel.isSubRoles(toDataPropertyPointer(axiom.getSubProperty()),
                    toDataPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            return kernel
                    .isInverseFunctional(toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public Boolean visit(OWLSameIndividualAxiom axiom) {
            for (OWLSameIndividualAxiom ax : axiom.asPairwiseAxioms()) {
                Iterator<OWLIndividual> it = ax.getIndividuals().iterator();
                OWLIndividual indA = it.next();
                OWLIndividual indB = it.next();
                if (!kernel.isSameIndividuals(toIndividualPointer(indA),
                        toIndividualPointer(indB))) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        @Override
        public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
            List<ObjectRoleExpression> l = new ArrayList<ObjectRoleExpression>();
            for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
                l.add(toObjectPropertyPointer(p));
            }
            return kernel
                    .isSubChain(toObjectPropertyPointer(axiom.getSuperProperty()), l);
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

    abstract class OWLEntityTranslator<E extends OWLObject, T extends Entity> {
        private Map<E, T> entity2dlentity = new HashMap<E, T>();
        private Map<T, E> dlentity2entity = new HashMap<T, E>();

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

    protected class DeclarationVisitorEx implements OWLEntityVisitorEx<AxiomInterface> {
        @Override
        public AxiomInterface visit(OWLClass cls) {
            return kernel.declare(df.getOWLDeclarationAxiom(cls), toClassPointer(cls));
        }

        @Override
        public AxiomInterface visit(OWLObjectProperty property) {
            return kernel.declare(df.getOWLDeclarationAxiom(property),
                    toObjectPropertyPointer(property));
        }

        @Override
        public AxiomInterface visit(OWLDataProperty property) {
            return kernel.declare(df.getOWLDeclarationAxiom(property),
                    toDataPropertyPointer(property));
        }

        @Override
        public AxiomInterface visit(OWLNamedIndividual individual) {
            return kernel.declare(df.getOWLDeclarationAxiom(individual),
                    toIndividualPointer(individual));
        }

        @Override
        public AxiomInterface visit(OWLDatatype datatype) {
            return kernel.declare(df.getOWLDeclarationAxiom(datatype),
                    toDataTypePointer(datatype));
        }

        @Override
        public AxiomInterface visit(OWLAnnotationProperty property) {
            return null;
        }
    }

    class AxiomTranslator implements OWLAxiomVisitorEx<AxiomInterface> {
        private DeclarationVisitorEx v;

        public AxiomTranslator() {
            v = new DeclarationVisitorEx();
        }

        @Override
        public AxiomInterface visit(OWLSubClassOfAxiom axiom) {
            return kernel.impliesConcepts(axiom, toClassPointer(axiom.getSubClass()),
                    toClassPointer(axiom.getSuperClass()));
        }

        @Override
        public AxiomInterface visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            return kernel.relatedToNot(axiom, toIndividualPointer(axiom.getSubject()),
                    toObjectPropertyPointer(axiom.getProperty()),
                    toIndividualPointer(axiom.getObject()));
        }

        @Override
        public AxiomInterface visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            return kernel.setAsymmetric(axiom,
                    toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLReflexiveObjectPropertyAxiom axiom) {
            return kernel.setReflexive(axiom,
                    toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLDisjointClassesAxiom axiom) {
            return kernel.disjointConcepts(axiom,
                    translateClassExpressionSet(axiom.getClassExpressions()));
        }

        private List<ConceptExpression> translateClassExpressionSet(
                Set<OWLClassExpression> classExpressions) {
            List<ConceptExpression> l = new ArrayList<ConceptExpression>();
            for (OWLClassExpression ce : classExpressions) {
                l.add(toClassPointer(ce));
            }
            return l;
        }

        @Override
        public AxiomInterface visit(OWLDataPropertyDomainAxiom axiom) {
            return kernel.setDDomain(axiom, toDataPropertyPointer(axiom.getProperty()),
                    toClassPointer(axiom.getDomain()));
        }

        @Override
        public AxiomInterface visit(OWLObjectPropertyDomainAxiom axiom) {
            return kernel.setODomain(axiom, toObjectPropertyPointer(axiom.getProperty()),
                    toClassPointer(axiom.getDomain()));
        }

        @Override
        public AxiomInterface visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            return kernel.equalORoles(axiom,
                    translateObjectPropertySet(axiom.getProperties()));
        }

        private List<ObjectRoleExpression> translateObjectPropertySet(
                Collection<OWLObjectPropertyExpression> properties) {
            List<ObjectRoleExpression> l = new ArrayList<ObjectRoleExpression>();
            for (OWLObjectPropertyExpression property : properties) {
                l.add(toObjectPropertyPointer(property));
            }
            return l;
        }

        @Override
        public AxiomInterface visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            return kernel.valueOfNot(axiom, toIndividualPointer(axiom.getSubject()),
                    toDataPropertyPointer(axiom.getProperty()),
                    toDataValuePointer(axiom.getObject()));
        }

        @Override
        public AxiomInterface visit(OWLDifferentIndividualsAxiom axiom) {
            return kernel.processDifferent(axiom,
                    translateIndividualSet(axiom.getIndividuals()));
        }

        @Override
        public AxiomInterface visit(OWLDisjointDataPropertiesAxiom axiom) {
            return kernel.disjointDRoles(axiom,
                    translateDataPropertySet(axiom.getProperties()));
        }

        private List<DataRoleExpression> translateDataPropertySet(
                Set<OWLDataPropertyExpression> properties) {
            List<DataRoleExpression> l = new ArrayList<DataRoleExpression>();
            for (OWLDataPropertyExpression property : properties) {
                l.add(toDataPropertyPointer(property));
            }
            return l;
        }

        @Override
        public AxiomInterface visit(OWLDisjointObjectPropertiesAxiom axiom) {
            return kernel.disjointORoles(axiom,
                    translateObjectPropertySet(axiom.getProperties()));
        }

        @Override
        public AxiomInterface visit(OWLObjectPropertyRangeAxiom axiom) {
            return kernel.setORange(axiom, toObjectPropertyPointer(axiom.getProperty()),
                    toClassPointer(axiom.getRange()));
        }

        @Override
        public AxiomInterface visit(OWLObjectPropertyAssertionAxiom axiom) {
            return kernel.relatedTo(axiom, toIndividualPointer(axiom.getSubject()),
                    toObjectPropertyPointer(axiom.getProperty()),
                    toIndividualPointer(axiom.getObject()));
        }

        @Override
        public AxiomInterface visit(OWLFunctionalObjectPropertyAxiom axiom) {
            return kernel.setOFunctional(axiom,
                    toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLSubObjectPropertyOfAxiom axiom) {
            return kernel.impliesORoles(axiom,
                    toObjectPropertyPointer(axiom.getSubProperty()),
                    toObjectPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public AxiomInterface visit(OWLDisjointUnionAxiom axiom) {
            return kernel.disjointUnion(axiom, toClassPointer(axiom.getOWLClass()),
                    translateClassExpressionSet(axiom.getClassExpressions()));
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
            return kernel.setSymmetric(axiom,
                    toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLDataPropertyRangeAxiom axiom) {
            return kernel.setDRange(axiom, toDataPropertyPointer(axiom.getProperty()),
                    toDataTypeExpressionPointer(axiom.getRange()));
        }

        @Override
        public AxiomInterface visit(OWLFunctionalDataPropertyAxiom axiom) {
            return kernel.setDFunctional(axiom,
                    toDataPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLEquivalentDataPropertiesAxiom axiom) {
            return kernel.equalDRoles(axiom,
                    translateDataPropertySet(axiom.getProperties()));
        }

        @Override
        public AxiomInterface visit(OWLClassAssertionAxiom axiom) {
            return kernel.instanceOf(axiom, toIndividualPointer(axiom.getIndividual()),
                    toClassPointer(axiom.getClassExpression()));
        }

        @Override
        public AxiomInterface visit(OWLEquivalentClassesAxiom axiom) {
            return kernel.equalConcepts(axiom,
                    translateClassExpressionSet(axiom.getClassExpressions()));
        }

        @Override
        public AxiomInterface visit(OWLDataPropertyAssertionAxiom axiom) {
            return kernel.valueOf(axiom, toIndividualPointer(axiom.getSubject()),
                    toDataPropertyPointer(axiom.getProperty()),
                    toDataValuePointer(axiom.getObject()));
        }

        @Override
        public AxiomInterface visit(OWLTransitiveObjectPropertyAxiom axiom) {
            return kernel.setTransitive(axiom,
                    toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            return kernel.setIrreflexive(axiom,
                    toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLSubDataPropertyOfAxiom axiom) {
            return kernel.impliesDRoles(axiom,
                    toDataPropertyPointer(axiom.getSubProperty()),
                    toDataPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public AxiomInterface visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            return kernel.setInverseFunctional(axiom,
                    toObjectPropertyPointer(axiom.getProperty()));
        }

        @Override
        public AxiomInterface visit(OWLSameIndividualAxiom axiom) {
            return kernel.processSame(axiom,
                    translateIndividualSet(axiom.getIndividuals()));
        }

        @Override
        public AxiomInterface visit(OWLSubPropertyChainOfAxiom axiom) {
            return kernel.impliesORoles(axiom,
                    em.compose(translateObjectPropertySet(axiom.getPropertyChain())),
                    toObjectPropertyPointer(axiom.getSuperProperty()));
        }

        @Override
        public AxiomInterface visit(OWLInverseObjectPropertiesAxiom axiom) {
            return kernel.setInverseRoles(axiom,
                    toObjectPropertyPointer(axiom.getFirstProperty()),
                    toObjectPropertyPointer(axiom.getSecondProperty()));
        }

        @Override
        public AxiomInterface visit(OWLHasKeyAxiom axiom) {
            // translateObjectPropertySet(axiom.getObjectPropertyExpressions());
            // TDLObjectRoleExpression objectPropertyPointer = kernel
            // .getObjectPropertyKey();
            // translateDataPropertySet(axiom.getDataPropertyExpressions());
            throw new ReasonerInternalException(
                    "JFact Kernel: unsupported operation 'getDataPropertyKey'");
            // TDLDataRoleExpression dataPropertyPointer = kernel
            // .getDataPropertyKey();
            // return kernel.tellHasKey(
            // toClassPointer(axiom.getClassExpression()),
            // dataPropertyPointer, objectPropertyPointer);
        }

        @Override
        public AxiomInterface visit(OWLDatatypeDefinitionAxiom axiom) {
            throw new ReasonerInternalException(
                    "JFact Kernel: unsupported operation 'OWLDatatypeDefinitionAxiom'");
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
            return em.exists(toObjectPropertyPointer(desc.getProperty()), desc
                    .getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectAllValuesFrom desc) {
            return em.forall(toObjectPropertyPointer(desc.getProperty()), desc
                    .getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectHasValue desc) {
            return em.value(toObjectPropertyPointer(desc.getProperty()),
                    toIndividualPointer(desc.getValue()));
        }

        @Override
        public ConceptExpression visit(OWLObjectMinCardinality desc) {
            return em.minCardinality(desc.getCardinality(),
                    toObjectPropertyPointer(desc.getProperty()),
                    desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectExactCardinality desc) {
            return em.cardinality(desc.getCardinality(),
                    toObjectPropertyPointer(desc.getProperty()),
                    desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectMaxCardinality desc) {
            return em.maxCardinality(desc.getCardinality(),
                    toObjectPropertyPointer(desc.getProperty()),
                    desc.getFiller().accept(this));
        }

        @Override
        public ConceptExpression visit(OWLObjectHasSelf desc) {
            return em.selfReference(toObjectPropertyPointer(desc.getProperty()));
        }

        @Override
        public ConceptExpression visit(OWLObjectOneOf desc) {
            return em.oneOf(translateIndividualSet(desc.getIndividuals()));
        }

        @Override
        public ConceptExpression visit(OWLDataSomeValuesFrom desc) {
            return em.exists(toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataAllValuesFrom desc) {
            return em.forall(toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataHasValue desc) {
            return em.value(toDataPropertyPointer(desc.getProperty()),
                    toDataValuePointer(desc.getValue()));
        }

        @Override
        public ConceptExpression visit(OWLDataMinCardinality desc) {
            return em.minCardinality(desc.getCardinality(),
                    toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataExactCardinality desc) {
            return em.cardinality(desc.getCardinality(),
                    toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }

        @Override
        public ConceptExpression visit(OWLDataMaxCardinality desc) {
            return em.maxCardinality(desc.getCardinality(),
                    toDataPropertyPointer(desc.getProperty()),
                    toDataTypeExpressionPointer(desc.getFiller()));
        }
    }

    class DataPropertyTranslator extends
            OWLEntityTranslator<OWLDataProperty, DataRoleExpression> {
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
                l.add(toDataValuePointer(literal));
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
                Literal<?> dv = toDataValuePointer(restriction.getFacetValue());
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
     * @return trnslated set */
    public Set<OWLAxiom> translateTAxiomSet(Collection<AxiomInterface> trace) {
        Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
        for (AxiomInterface ap : trace) {
            ret.add(ptr2AxiomMap.get(ap));
        }
        return ret;
    }
}
