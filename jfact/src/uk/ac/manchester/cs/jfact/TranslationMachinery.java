package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
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
import uk.ac.manchester.cs.jfact.datatypes.Facets;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Entity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleComplexExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;

@SuppressWarnings("unused")
//XXX verify unused parameters
public final class TranslationMachinery {
	private volatile AxiomTranslator axiomTranslator;
	private volatile ClassExpressionTranslator classExpressionTranslator;
	private volatile DataRangeTranslator dataRangeTranslator;
	private volatile ObjectPropertyTranslator objectPropertyTranslator;
	private volatile DataPropertyTranslator dataPropertyTranslator;
	private volatile IndividualTranslator individualTranslator;
	private volatile EntailmentChecker entailmentChecker;
	private final Map<OWLAxiom, Axiom> axiom2PtrMap = new HashMap<OWLAxiom, Axiom>();
	private final Map<Axiom, OWLAxiom> ptr2AxiomMap = new HashMap<Axiom, OWLAxiom>();
	protected final ReasoningKernel kernel;
	protected final ExpressionManager em;
	protected final OWLDataFactory df;
	final DatatypeFactory datatypefactory;

	public TranslationMachinery(final ReasoningKernel kernel, final OWLDataFactory df,
			final DatatypeFactory factory) {
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

	public ObjectRoleExpression getTopObjectProperty() {
		return em.objectRole(Vocabulary.TOP_OBJECT_PROPERTY);
	}

	public ObjectRoleExpression getBottomObjectProperty() {
		return em.objectRole(Vocabulary.BOTTOM_OBJECT_PROPERTY);
	}

	public DataRoleExpression getTopDataProperty() {
		return em.dataRole(Vocabulary.TOP_DATA_PROPERTY);
	}

	public DataRoleExpression getBottomDataProperty() {
		return em.dataRole(Vocabulary.BOTTOM_DATA_PROPERTY);
	}

	public void loadAxioms(final Collection<OWLAxiom> axioms) {
		for (OWLAxiom axiom : axioms) {
			//TODO check valid axioms, such as those involving topDataProperty
			if (!axiom2PtrMap.containsKey(axiom)) {
				final Axiom axiomPointer = axiom.accept(axiomTranslator);
				if (axiomPointer != null) {
					axiom2PtrMap.put(axiom, axiomPointer);
				}
			}
		}
	}

	public void retractAxiom(final OWLAxiom axiom) {
		final Axiom ptr = axiom2PtrMap.get(axiom);
		if (ptr != null) {
			kernel.retract(ptr);
			axiom2PtrMap.remove(axiom);
		}
	}

	protected ConceptExpression toClassPointer(final OWLClassExpression classExpression) {
		return classExpression.accept(classExpressionTranslator);
	}

	protected DataExpression toDataTypeExpressionPointer(final OWLDataRange dataRange) {
		return dataRange.accept(dataRangeTranslator);
	}

	protected ObjectRoleExpression toObjectPropertyPointer(
			final OWLObjectPropertyExpression propertyExpression) {
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
			final OWLDataPropertyExpression propertyExpression) {
		return dataPropertyTranslator.getPointerFromEntity(propertyExpression
				.asOWLDataProperty());
	}

	protected synchronized IndividualExpression toIndividualPointer(
			final OWLIndividual individual) {
		if (!individual.isAnonymous()) {
			return individualTranslator.getPointerFromEntity(individual
					.asOWLNamedIndividual());
		} else {
			return em.individual(individual.toStringID());
		}
	}

	protected synchronized Datatype<?> toDataTypePointer(final OWLDatatype datatype) {
		if (datatype == null) {
			throw new IllegalArgumentException("datatype cannot be null");
		}
		return datatypefactory.getKnownDatatype(datatype.getIRI().toString());
	}

	protected synchronized Literal<?> toDataValuePointer(final OWLLiteral literal) {
		String value = literal.getLiteral();
		if (literal.isRDFPlainLiteral()) {
			value = value + "@" + literal.getLang();
		}
		final String string = literal.getDatatype().getIRI().toString();
		final Datatype<?> knownDatatype = datatypefactory.getKnownDatatype(string);
		return knownDatatype.buildLiteral(value);
	}

	protected NodeSet<OWLNamedIndividual> translateIndividualPointersToNodeSet(
			final Iterable<IndividualExpression> pointers) {
		OWLNamedIndividualNodeSet ns = new OWLNamedIndividualNodeSet();
		for (IndividualExpression pointer : pointers) {
			if (pointer != null) {
				OWLNamedIndividual ind = individualTranslator
						.getEntityFromPointer(pointer);
				// XXX skipping anonymous individuals - counterintuitive but that's the specs for you
				if (ind != null) {
					ns.addEntity(ind);
				}
			}
		}
		return ns;
	}

	protected synchronized List<Expression> translateIndividualSet(
			final Set<OWLIndividual> inds) {
		List<Expression> l = new ArrayList<Expression>();
		for (OWLIndividual ind : inds) {
			l.add(toIndividualPointer(ind));
		}
		return l;
	}

	public final class EntailmentChecker implements OWLAxiomVisitorEx<Boolean> {
		public EntailmentChecker() {}

		public Boolean visit(final OWLSubClassOfAxiom axiom) {
			if (axiom.getSuperClass().equals(df.getOWLThing())
					|| axiom.getSubClass().equals(df.getOWLNothing())) {
				return true;
			}
			return kernel.isSubsumedBy(toClassPointer(axiom.getSubClass()),
					toClassPointer(axiom.getSuperClass()));
		}

		public Boolean visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
			return kernel.isAsymmetric(toObjectPropertyPointer(axiom.getProperty()));
		}

		public Boolean visit(final OWLReflexiveObjectPropertyAxiom axiom) {
			return kernel.isReflexive(toObjectPropertyPointer(axiom.getProperty()));
		}

		public Boolean visit(final OWLDisjointClassesAxiom axiom) {
			Set<OWLClassExpression> classExpressions = axiom.getClassExpressions();
			if (classExpressions.size() == 2) {
				Iterator<OWLClassExpression> it = classExpressions.iterator();
				return kernel.isDisjoint(toClassPointer(it.next()),
						toClassPointer(it.next()));
			} else {
				for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
					if (!ax.accept(this)) {
						return false;
					}
				}
				return true;
			}
		}

		public Boolean visit(final OWLDataPropertyDomainAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLObjectPropertyDomainAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
			for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
				if (!ax.accept(this)) {
					return false;
				}
			}
			return true;
		}

		public Boolean visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLDifferentIndividualsAxiom axiom) {
			for (OWLSubClassOfAxiom ax : axiom.asOWLSubClassOfAxioms()) {
				if (!ax.accept(this)) {
					return false;
				}
			}
			return true;
		}

		// TODO: this check is incomplete
		public Boolean visit(final OWLDisjointDataPropertiesAxiom axiom) {
			List<OWLDataPropertyExpression> l = new ArrayList<OWLDataPropertyExpression>(
					axiom.getProperties());
			for (int i = 0; i < l.size() - 1; i++) {
				for (int j = i + 1; j < l.size(); j++) {
					if (!kernel.isDisjointRoles(toDataPropertyPointer(l.get(i)),
							toDataPropertyPointer(l.get(i)))) {
						return false;
					}
				}
			}
			return true;
		}

		public Boolean visit(final OWLDisjointObjectPropertiesAxiom axiom) {
			List<OWLObjectPropertyExpression> l = new ArrayList<OWLObjectPropertyExpression>(
					axiom.getProperties());
			for (int i = 0; i < l.size() - 1; i++) {
				for (int j = i + 1; j < l.size(); j++) {
					if (!kernel.isDisjointRoles(toObjectPropertyPointer(l.get(i)),
							toObjectPropertyPointer(l.get(i)))) {
						return false;
					}
				}
			}
			return true;
		}

		public Boolean visit(final OWLObjectPropertyRangeAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLObjectPropertyAssertionAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLFunctionalObjectPropertyAxiom axiom) {
			return kernel.isFunctional(toObjectPropertyPointer(axiom.getProperty()));
		}

		public Boolean visit(final OWLSubObjectPropertyOfAxiom axiom) {
			return kernel.isSubRoles(toObjectPropertyPointer(axiom.getSubProperty()),
					toObjectPropertyPointer(axiom.getSuperProperty()));
		}

		public Boolean visit(final OWLDisjointUnionAxiom axiom) {
			return axiom.getOWLEquivalentClassesAxiom().accept(this)
					&& axiom.getOWLDisjointClassesAxiom().accept(this);
		}

		public Boolean visit(final OWLDeclarationAxiom axiom) {
			return false;
		}

		public Boolean visit(final OWLAnnotationAssertionAxiom axiom) {
			return false;
		}

		public Boolean visit(final OWLSymmetricObjectPropertyAxiom axiom) {
			return kernel.isSymmetric(toObjectPropertyPointer(axiom.getProperty()));
		}

		public Boolean visit(final OWLDataPropertyRangeAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLFunctionalDataPropertyAxiom axiom) {
			return kernel.isFunctional(toDataPropertyPointer(axiom.getProperty()));
		}

		public Boolean visit(final OWLEquivalentDataPropertiesAxiom axiom) {
			for (OWLAxiom ax : axiom.asSubDataPropertyOfAxioms()) {
				if (!ax.accept(this)) {
					return false;
				}
			}
			return true;
		}

		public Boolean visit(final OWLClassAssertionAxiom axiom) {
			return kernel.isInstance(toIndividualPointer(axiom.getIndividual()),
					toClassPointer(axiom.getClassExpression()));
		}

		public Boolean visit(final OWLEquivalentClassesAxiom axiom) {
			Set<OWLClassExpression> classExpressionSet = axiom.getClassExpressions();
			if (classExpressionSet.size() == 2) {
				Iterator<OWLClassExpression> it = classExpressionSet.iterator();
				return kernel.isEquivalent(toClassPointer(it.next()),
						toClassPointer(it.next()));
			} else {
				for (OWLAxiom ax : axiom.asOWLSubClassOfAxioms()) {
					if (!ax.accept(this)) {
						return false;
					}
				}
				return true;
			}
		}

		public Boolean visit(final OWLDataPropertyAssertionAxiom axiom) {
			return axiom.asOWLSubClassOfAxiom().accept(this);
		}

		public Boolean visit(final OWLTransitiveObjectPropertyAxiom axiom) {
			return kernel.isTransitive(toObjectPropertyPointer(axiom.getProperty()));
		}

		public Boolean visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
			return kernel.isIrreflexive(toObjectPropertyPointer(axiom.getProperty()));
		}

		// TODO: this is incomplete
		public Boolean visit(final OWLSubDataPropertyOfAxiom axiom) {
			return kernel.isSubRoles(toDataPropertyPointer(axiom.getSubProperty()),
					toDataPropertyPointer(axiom.getSuperProperty()));
		}

		public Boolean visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
			return kernel
					.isInverseFunctional(toObjectPropertyPointer(axiom.getProperty()));
		}

		public Boolean visit(final OWLSameIndividualAxiom axiom) {
			for (OWLSameIndividualAxiom ax : axiom.asPairwiseAxioms()) {
				Iterator<OWLIndividual> it = ax.getIndividuals().iterator();
				OWLIndividual indA = it.next();
				OWLIndividual indB = it.next();
				if (!kernel.isSameIndividuals(toIndividualPointer(indA),
						toIndividualPointer(indB))) {
					return false;
				}
			}
			return true;
		}

		public Boolean visit(final OWLSubPropertyChainOfAxiom axiom) {
			List<ObjectRoleExpression> l = new ArrayList<ObjectRoleExpression>();
			for (OWLObjectPropertyExpression p : axiom.getPropertyChain()) {
				l.add(toObjectPropertyPointer(p));
			}
			return kernel
					.isSubChain(toObjectPropertyPointer(axiom.getSuperProperty()), l);
		}

		public Boolean visit(final OWLInverseObjectPropertiesAxiom axiom) {
			for (OWLAxiom ax : axiom.asSubObjectPropertyOfAxioms()) {
				if (!ax.accept(this)) {
					return false;
				}
			}
			return true;
		}

		public Boolean visit(final OWLHasKeyAxiom axiom) {
			// FIXME!! unsupported by FaCT++ ATM
			//return null;
			throw new UnsupportedEntailmentTypeException(axiom);
		}

		public Boolean visit(final OWLDatatypeDefinitionAxiom axiom) {
			// FIXME!! unsupported by FaCT++ ATM
			//return null;
			throw new UnsupportedEntailmentTypeException(axiom);
		}

		public Boolean visit(final SWRLRule rule) {
			// FIXME!! unsupported by FaCT++ ATM
			//return null;
			throw new UnsupportedEntailmentTypeException(rule);
		}

		public Boolean visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
			return false;
		}

		public Boolean visit(final OWLAnnotationPropertyDomainAxiom axiom) {
			return false;
		}

		public Boolean visit(final OWLAnnotationPropertyRangeAxiom axiom) {
			return false;
		}
	}

	abstract class OWLEntityTranslator<E extends OWLObject, T extends Entity> {
		private final Map<E, T> entity2dlentity = new HashMap<E, T>();
		private final Map<T, E> dlentity2entity = new HashMap<T, E>();

		protected final void fillMaps(final E entity, final T dlentity) {
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

		protected T registerNewEntity(final E entity) {
			T pointer = this.createPointerForEntity(entity);
			this.fillMaps(entity, pointer);
			return pointer;
		}

		public final E getEntityFromPointer(final T pointer) {
			return this.dlentity2entity.get(pointer);
		}

		public final T getPointerFromEntity(final E entity) {
			T pointer = this.entity2dlentity.get(entity);
			if (pointer == null) {
				pointer = this.registerNewEntity(entity);
			}
			return pointer;
		}

		public Node<E> getNodeFromPointers(final Collection<T> pointers) {
			DefaultNode<E> node = this.createDefaultNode();
			for (T pointer : pointers) {
				node.add(this.getEntityFromPointer(pointer));
			}
			return node;
		}

		public NodeSet<E> getNodeSetFromPointers(final Collection<Collection<T>> pointers) {
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

	final class ObjectPropertyTranslator extends
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
			entity = entity.getInverseProperty().getSimplified();
			fillMaps(entity, createPointerForEntity(entity));
			return pointer;
		}

		@Override
		protected ObjectRoleExpression createPointerForEntity(
				final OWLObjectPropertyExpression entity) {
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

	final class ComplexObjectPropertyTranslator extends
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
			entity = entity.getInverseProperty().getSimplified();
			fillMaps(entity, createPointerForEntity(entity));
			return pointer;
		}

		@Override
		protected ObjectRoleComplexExpression createPointerForEntity(
				final OWLObjectPropertyExpression entity) {
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

	protected final class DeclarationVisitorEx implements OWLEntityVisitorEx<Axiom> {
		public Axiom visit(final OWLClass cls) {
			return kernel.declare(df.getOWLDeclarationAxiom(cls), toClassPointer(cls));
		}

		public Axiom visit(final OWLObjectProperty property) {
			return kernel.declare(df.getOWLDeclarationAxiom(property),
					toObjectPropertyPointer(property));
		}

		public Axiom visit(final OWLDataProperty property) {
			return kernel.declare(df.getOWLDeclarationAxiom(property),
					toDataPropertyPointer(property));
		}

		public Axiom visit(final OWLNamedIndividual individual) {
			return kernel.declare(df.getOWLDeclarationAxiom(individual),
					toIndividualPointer(individual));
		}

		public Axiom visit(final OWLDatatype datatype) {
			return kernel.declare(df.getOWLDeclarationAxiom(datatype),
					toDataTypePointer(datatype));
		}

		public Axiom visit(final OWLAnnotationProperty property) {
			return null;
		}
	}

	final class AxiomTranslator implements OWLAxiomVisitorEx<Axiom> {
		private final DeclarationVisitorEx v;

		public AxiomTranslator() {
			v = new DeclarationVisitorEx();
		}

		public Axiom visit(final OWLSubClassOfAxiom axiom) {
			return kernel.impliesConcepts(axiom, toClassPointer(axiom.getSubClass()),
					toClassPointer(axiom.getSuperClass()));
		}

		public Axiom visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
			return kernel.relatedToNot(axiom, toIndividualPointer(axiom.getSubject()),
					toObjectPropertyPointer(axiom.getProperty()),
					toIndividualPointer(axiom.getObject()));
		}

		public Axiom visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
			return kernel.setAsymmetric(axiom,
					toObjectPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLReflexiveObjectPropertyAxiom axiom) {
			return kernel.setReflexive(axiom,
					toObjectPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLDisjointClassesAxiom axiom) {
			return kernel.disjointConcepts(axiom,
					translateClassExpressionSet(axiom.getClassExpressions()));
		}

		private List<Expression> translateClassExpressionSet(
				final Set<OWLClassExpression> classExpressions) {
			List<Expression> l = new ArrayList<Expression>();
			for (OWLClassExpression ce : classExpressions) {
				l.add(toClassPointer(ce));
			}
			return l;
		}

		public Axiom visit(final OWLDataPropertyDomainAxiom axiom) {
			return kernel.setDDomain(axiom, toDataPropertyPointer(axiom.getProperty()),
					toClassPointer(axiom.getDomain()));
		}

		public Axiom visit(final OWLObjectPropertyDomainAxiom axiom) {
			return kernel.setODomain(axiom, toObjectPropertyPointer(axiom.getProperty()),
					toClassPointer(axiom.getDomain()));
		}

		public Axiom visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
			return kernel.equalORoles(axiom,
					translateObjectPropertySet(axiom.getProperties()));
		}

		private List<Expression> translateObjectPropertySet(
				final Collection<OWLObjectPropertyExpression> properties) {
			List<Expression> l = new ArrayList<Expression>();
			for (OWLObjectPropertyExpression property : properties) {
				l.add(toObjectPropertyPointer(property));
			}
			return l;
		}

		public Axiom visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
			return kernel.valueOfNot(axiom, toIndividualPointer(axiom.getSubject()),
					toDataPropertyPointer(axiom.getProperty()),
					toDataValuePointer(axiom.getObject()));
		}

		public Axiom visit(final OWLDifferentIndividualsAxiom axiom) {
			return kernel.processDifferent(axiom,
					translateIndividualSet(axiom.getIndividuals()));
		}

		public Axiom visit(final OWLDisjointDataPropertiesAxiom axiom) {
			return kernel.disjointDRoles(axiom,
					translateDataPropertySet(axiom.getProperties()));
		}

		private List<Expression> translateDataPropertySet(
				final Set<OWLDataPropertyExpression> properties) {
			List<Expression> l = new ArrayList<Expression>();
			for (OWLDataPropertyExpression property : properties) {
				l.add(toDataPropertyPointer(property));
			}
			return l;
		}

		public Axiom visit(final OWLDisjointObjectPropertiesAxiom axiom) {
			return kernel.disjointORoles(axiom,
					translateObjectPropertySet(axiom.getProperties()));
		}

		public Axiom visit(final OWLObjectPropertyRangeAxiom axiom) {
			return kernel.setORange(axiom, toObjectPropertyPointer(axiom.getProperty()),
					toClassPointer(axiom.getRange()));
		}

		public Axiom visit(final OWLObjectPropertyAssertionAxiom axiom) {
			return kernel.relatedTo(axiom, toIndividualPointer(axiom.getSubject()),
					toObjectPropertyPointer(axiom.getProperty()),
					toIndividualPointer(axiom.getObject()));
		}

		public Axiom visit(final OWLFunctionalObjectPropertyAxiom axiom) {
			return kernel.setOFunctional(axiom,
					toObjectPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLSubObjectPropertyOfAxiom axiom) {
			return kernel.impliesORoles(axiom,
					toObjectPropertyPointer(axiom.getSubProperty()),
					toObjectPropertyPointer(axiom.getSuperProperty()));
		}

		public Axiom visit(final OWLDisjointUnionAxiom axiom) {
			return kernel.disjointUnion(axiom, toClassPointer(axiom.getOWLClass()),
					translateClassExpressionSet(axiom.getClassExpressions()));
		}

		public Axiom visit(final OWLDeclarationAxiom axiom) {
			OWLEntity entity = axiom.getEntity();
			return entity.accept(v);
		}

		public Axiom visit(final OWLAnnotationAssertionAxiom axiom) {
			// Ignore
			return null;
		}

		public Axiom visit(final OWLSymmetricObjectPropertyAxiom axiom) {
			return kernel.setSymmetric(axiom,
					toObjectPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLDataPropertyRangeAxiom axiom) {
			return kernel.setDRange(axiom, toDataPropertyPointer(axiom.getProperty()),
					toDataTypeExpressionPointer(axiom.getRange()));
		}

		public Axiom visit(final OWLFunctionalDataPropertyAxiom axiom) {
			return kernel.setDFunctional(axiom,
					toDataPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLEquivalentDataPropertiesAxiom axiom) {
			return kernel.equalDRoles(axiom,
					translateDataPropertySet(axiom.getProperties()));
		}

		public Axiom visit(final OWLClassAssertionAxiom axiom) {
			return kernel.instanceOf(axiom, toIndividualPointer(axiom.getIndividual()),
					toClassPointer(axiom.getClassExpression()));
		}

		public Axiom visit(final OWLEquivalentClassesAxiom axiom) {
			return kernel.equalConcepts(axiom,
					translateClassExpressionSet(axiom.getClassExpressions()));
		}

		public Axiom visit(final OWLDataPropertyAssertionAxiom axiom) {
			return kernel.valueOf(axiom, toIndividualPointer(axiom.getSubject()),
					toDataPropertyPointer(axiom.getProperty()),
					toDataValuePointer(axiom.getObject()));
		}

		public Axiom visit(final OWLTransitiveObjectPropertyAxiom axiom) {
			return kernel.setTransitive(axiom,
					toObjectPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
			return kernel.setIrreflexive(axiom,
					toObjectPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLSubDataPropertyOfAxiom axiom) {
			return kernel.impliesDRoles(axiom,
					toDataPropertyPointer(axiom.getSubProperty()),
					toDataPropertyPointer(axiom.getSuperProperty()));
		}

		public Axiom visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
			return kernel.setInverseFunctional(axiom,
					toObjectPropertyPointer(axiom.getProperty()));
		}

		public Axiom visit(final OWLSameIndividualAxiom axiom) {
			return kernel.processSame(axiom,
					translateIndividualSet(axiom.getIndividuals()));
		}

		public Axiom visit(final OWLSubPropertyChainOfAxiom axiom) {
			return kernel.impliesORoles(axiom,
					em.compose(translateObjectPropertySet(axiom.getPropertyChain())),
					toObjectPropertyPointer(axiom.getSuperProperty()));
		}

		public Axiom visit(final OWLInverseObjectPropertiesAxiom axiom) {
			return kernel.setInverseRoles(axiom,
					toObjectPropertyPointer(axiom.getFirstProperty()),
					toObjectPropertyPointer(axiom.getSecondProperty()));
		}

		public Axiom visit(final OWLHasKeyAxiom axiom) {
			//			translateObjectPropertySet(axiom.getObjectPropertyExpressions());
			//			TDLObjectRoleExpression objectPropertyPointer = kernel
			//					.getObjectPropertyKey();
			//			translateDataPropertySet(axiom.getDataPropertyExpressions());
			throw new ReasonerInternalException(
					"JFact Kernel: unsupported operation 'getDataPropertyKey'");
			//			TDLDataRoleExpression dataPropertyPointer = kernel
			//					.getDataPropertyKey();
			//			return kernel.tellHasKey(
			//					toClassPointer(axiom.getClassExpression()),
			//					dataPropertyPointer, objectPropertyPointer);
		}

		public Axiom visit(final OWLDatatypeDefinitionAxiom axiom) {
			throw new ReasonerInternalException(
					"JFact Kernel: unsupported operation 'OWLDatatypeDefinitionAxiom'");
			//			kernel.getDataSubType(axiom.getDatatype().getIRI().toString(),
			//					toDataTypeExpressionPointer(axiom.getDataRange()));
		}

		public Axiom visit(final SWRLRule rule) {
			// Ignore
			return null;
		}

		public Axiom visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
			// Ignore
			return null;
		}

		public Axiom visit(final OWLAnnotationPropertyDomainAxiom axiom) {
			// Ignore
			return null;
		}

		public Axiom visit(final OWLAnnotationPropertyRangeAxiom axiom) {
			// Ignore
			return null;
		}
	}

	final class ClassExpressionTranslator extends
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
		protected ConceptExpression createPointerForEntity(final OWLClass entity) {
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

		public ConceptExpression visit(final OWLClass desc) {
			return getPointerFromEntity(desc);
		}

		public ConceptExpression visit(final OWLObjectIntersectionOf desc) {
			return em.and(translateClassExpressionSet(desc.getOperands()));
		}

		private List<Expression> translateClassExpressionSet(
				final Set<OWLClassExpression> classExpressions) {
			List<Expression> l = new ArrayList<Expression>();
			for (OWLClassExpression ce : classExpressions) {
				l.add(ce.accept(this));
			}
			return l;
		}

		public ConceptExpression visit(final OWLObjectUnionOf desc) {
			return em.or(translateClassExpressionSet(desc.getOperands()));
		}

		public ConceptExpression visit(final OWLObjectComplementOf desc) {
			return em.not(desc.getOperand().accept(this));
		}

		public ConceptExpression visit(final OWLObjectSomeValuesFrom desc) {
			return em.exists(toObjectPropertyPointer(desc.getProperty()), desc
					.getFiller().accept(this));
		}

		public ConceptExpression visit(final OWLObjectAllValuesFrom desc) {
			return em.forall(toObjectPropertyPointer(desc.getProperty()), desc
					.getFiller().accept(this));
		}

		public ConceptExpression visit(final OWLObjectHasValue desc) {
			return em.value(toObjectPropertyPointer(desc.getProperty()),
					toIndividualPointer(desc.getValue()));
		}

		public ConceptExpression visit(final OWLObjectMinCardinality desc) {
			return em.minCardinality(desc.getCardinality(),
					toObjectPropertyPointer(desc.getProperty()),
					desc.getFiller().accept(this));
		}

		public ConceptExpression visit(final OWLObjectExactCardinality desc) {
			return em.cardinality(desc.getCardinality(),
					toObjectPropertyPointer(desc.getProperty()),
					desc.getFiller().accept(this));
		}

		public ConceptExpression visit(final OWLObjectMaxCardinality desc) {
			return em.maxCardinality(desc.getCardinality(),
					toObjectPropertyPointer(desc.getProperty()),
					desc.getFiller().accept(this));
		}

		public ConceptExpression visit(final OWLObjectHasSelf desc) {
			return em.selfReference(toObjectPropertyPointer(desc.getProperty()));
		}

		public ConceptExpression visit(final OWLObjectOneOf desc) {
			return em.oneOf(translateIndividualSet(desc.getIndividuals()));
		}

		public ConceptExpression visit(final OWLDataSomeValuesFrom desc) {
			return em.exists(toDataPropertyPointer(desc.getProperty()),
					toDataTypeExpressionPointer(desc.getFiller()));
		}

		public ConceptExpression visit(final OWLDataAllValuesFrom desc) {
			return em.forall(toDataPropertyPointer(desc.getProperty()),
					toDataTypeExpressionPointer(desc.getFiller()));
		}

		public ConceptExpression visit(final OWLDataHasValue desc) {
			return em.value(toDataPropertyPointer(desc.getProperty()),
					toDataValuePointer(desc.getValue()));
		}

		public ConceptExpression visit(final OWLDataMinCardinality desc) {
			return em.minCardinality(desc.getCardinality(),
					toDataPropertyPointer(desc.getProperty()),
					toDataTypeExpressionPointer(desc.getFiller()));
		}

		public ConceptExpression visit(final OWLDataExactCardinality desc) {
			return em.cardinality(desc.getCardinality(),
					toDataPropertyPointer(desc.getProperty()),
					toDataTypeExpressionPointer(desc.getFiller()));
		}

		public ConceptExpression visit(final OWLDataMaxCardinality desc) {
			return em.maxCardinality(desc.getCardinality(),
					toDataPropertyPointer(desc.getProperty()),
					toDataTypeExpressionPointer(desc.getFiller()));
		}
	}

	final class DataPropertyTranslator extends
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
		protected DataRoleExpression createPointerForEntity(final OWLDataProperty entity) {
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

	final class DataRangeTranslator extends
			OWLEntityTranslator<OWLDatatype, DataExpression> implements
			OWLDataRangeVisitorEx<DataExpression> {
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
		protected DataExpression createPointerForEntity(final OWLDatatype entity) {
			return datatypefactory.getKnownDatatype(entity.getIRI().toString());
		}

		public Datatype<?> visit(final OWLDatatype node) {
			return datatypefactory.getKnownDatatype(node.getIRI().toString());
		}

		public DataExpression visit(final OWLDataOneOf node) {
			List<Expression> l = new ArrayList<Expression>();
			for (OWLLiteral literal : node.getValues()) {
				l.add(toDataValuePointer(literal));
			}
			return em.dataOneOf(l);
		}

		public DataExpression visit(final OWLDataComplementOf node) {
			return em.dataNot(node.getDataRange().accept(this));
		}

		public DataExpression visit(final OWLDataIntersectionOf node) {
			return em.dataAnd(translateDataRangeSet(node.getOperands()));
		}

		private List<Expression> translateDataRangeSet(final Set<OWLDataRange> dataRanges) {
			List<Expression> l = new ArrayList<Expression>();
			for (OWLDataRange op : dataRanges) {
				l.add(op.accept(this));
			}
			return l;
		}

		public DataExpression visit(final OWLDataUnionOf node) {
			return em.dataOr(translateDataRangeSet(node.getOperands()));
		}

		public DataExpression visit(final OWLDatatypeRestriction node) {
			DatatypeExpression<?> toReturn = null;
			Datatype<?> type = datatypefactory.getKnownDatatype(node.getDatatype()
					.getIRI().toString());
			final Set<OWLFacetRestriction> facetRestrictions = node
					.getFacetRestrictions();
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
				toReturn = toReturn.addFacet(Facets.parse(restriction.getFacet()), dv);
			}
			return toReturn;
		}
	}

	final class IndividualTranslator extends
			OWLEntityTranslator<OWLNamedIndividual, IndividualExpression> {
		public IndividualTranslator() {}

		@Override
		protected IndividualExpression getTopEntityPointer() {
			return null;
		}

		@Override
		protected IndividualExpression getBottomEntityPointer() {
			return null;
		}

		@Override
		protected IndividualExpression createPointerForEntity(
				final OWLNamedIndividual entity) {
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

	public ClassExpressionTranslator getClassExpressionTranslator() {
		return classExpressionTranslator;
	}

	public DataRangeTranslator getDataRangeTranslator() {
		return dataRangeTranslator;
	}

	public ObjectPropertyTranslator getObjectPropertyTranslator() {
		return objectPropertyTranslator;
	}

	public DataPropertyTranslator getDataPropertyTranslator() {
		return dataPropertyTranslator;
	}

	public IndividualTranslator getIndividualTranslator() {
		return individualTranslator;
	}

	public EntailmentChecker getEntailmentChecker() {
		return entailmentChecker;
	}

	public Set<OWLAxiom> translateTAxiomSet(final Collection<Axiom> trace) {
		Set<OWLAxiom> ret = new HashSet<OWLAxiom>();
		for (Axiom ap : trace) {
			ret.add(ptr2AxiomMap.get(ap));
		}
		return ret;
	}
}
