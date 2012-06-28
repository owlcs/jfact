package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;
import org.semanticweb.owlapi.util.Version;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.Individual;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.ReasonerFreshEntityException;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.actors.ClassPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.DataPropertyPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.IndividualPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.ObjectPropertyPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.TaxonomyActor;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.RoleExpression;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;
import uk.ac.manchester.cs.jfact.split.ModuleType;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;

/**
 * Synchronization policy: all methods for OWLReasoner are synchronized, except
 * the methods which do not touch the kernel or only affect threadsafe data
 * structures. inner private classes are not synchronized since methods from
 * those classes cannot be invoked from outsize synchronized methods.
 * 
 */
public final class JFactReasoner implements OWLReasoner, OWLOntologyChangeListener,
        OWLKnowledgeExplorerReasoner {
    private static final String REASONER_NAME = "JFact";
    private static final Version VERSION = new Version(0, 0, 0, 0);
    protected final AtomicBoolean interrupted = new AtomicBoolean(false);
    ReasoningKernel kernel;
    private final ExpressionManager em;
    private static final EnumSet<InferenceType> supportedInferenceTypes = EnumSet.of(
            InferenceType.CLASS_ASSERTIONS, InferenceType.CLASS_HIERARCHY,
            InferenceType.DATA_PROPERTY_HIERARCHY,
            InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.SAME_INDIVIDUAL);
    private final OWLOntologyManager manager;
    private final OWLOntology rootOntology;
    private final BufferingMode bufferingMode;
    private final List<OWLOntologyChange> rawChanges = new ArrayList<OWLOntologyChange>();
    private final List<OWLAxiom> reasonerAxioms = new ArrayList<OWLAxiom>();
    private final JFactReasonerConfiguration configuration;
    private final OWLDataFactory df;
    private TranslationMachinery translationMachinery;
    //holds the consistency status: true for consistent, false for inconsistent, null for not verified (or changes received)
    private Boolean consistencyVerified = null;
    private final Set<OWLEntity> knownEntities = new HashSet<OWLEntity>();
    private final DatatypeFactory datatypeFactory;

    public JFactReasoner(final OWLOntology o, final OWLReasonerConfiguration c,
            final BufferingMode b) {
        this(o, c instanceof JFactReasonerConfiguration ? (JFactReasonerConfiguration) c
                : new JFactReasonerConfiguration(c), b);
    }

    public JFactReasoner(final OWLOntology rootOntology,
            final JFactReasonerConfiguration config, final BufferingMode bufferingMode) {
        this.configuration = config;
        this.rootOntology = rootOntology;
        this.df = this.rootOntology.getOWLOntologyManager().getOWLDataFactory();
        this.datatypeFactory = DatatypeFactory.getInstance();
        this.kernel = new ReasoningKernel(this.configuration, this.datatypeFactory);
        this.em = this.kernel.getExpressionManager();
        this.bufferingMode = bufferingMode;
        this.manager = rootOntology.getOWLOntologyManager();
        this.knownEntities.add(this.df.getOWLThing());
        this.knownEntities.add(this.df.getOWLNothing());
        for (OWLOntology ont : rootOntology.getImportsClosure()) {
            for (OWLAxiom ax : ont.getLogicalAxioms()) {
                OWLAxiom axiom = ax.getAxiomWithoutAnnotations();
                this.reasonerAxioms.add(axiom);
                this.knownEntities.addAll(axiom.getSignature());
            }
            for (OWLAxiom ax : ont.getAxioms(AxiomType.DECLARATION)) {
                OWLAxiom axiom = ax.getAxiomWithoutAnnotations();
                this.reasonerAxioms.add(axiom);
                this.knownEntities.addAll(axiom.getSignature());
            }
        }
        this.kernel.setTopBottomRoleNames(Vocabulary.TOP_OBJECT_PROPERTY,
                Vocabulary.BOTTOM_OBJECT_PROPERTY, Vocabulary.TOP_DATA_PROPERTY,
                Vocabulary.BOTTOM_DATA_PROPERTY);
        this.kernel.setInterruptedSwitch(this.interrupted);
        this.configuration.getProgressMonitor().reasonerTaskStarted(
                ReasonerProgressMonitor.LOADING);
        this.configuration.getProgressMonitor().reasonerTaskBusy();
        this.kernel.clearKB();
        this.translationMachinery = new TranslationMachinery(this.kernel, this.df,
                this.datatypeFactory);
        this.translationMachinery.loadAxioms(this.reasonerAxioms);
        this.configuration.getProgressMonitor().reasonerTaskStopped();
    }

    public synchronized Node<OWLClass> getEquivalentClasses(final OWLClassExpression ce)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException,
            ReasonerInterruptedException, TimeOutException {
        Collection<ConceptExpression> pointers;
        if (this.isFreshName(ce)) {
            pointers = Collections.emptyList();
        } else {
            this.checkConsistency();
            TaxonomyActor actor = new TaxonomyActor(this.em, new ClassPolicy());
            this.kernel.getEquivalentConcepts(
                    this.translationMachinery.toClassPointer(ce), actor);
            pointers = actor.getClassSynonyms();
        }
        return this.translationMachinery.getClassExpressionTranslator()
                .getNodeFromPointers(pointers);
    }

    public DatatypeFactory getDatatypeFactory() {
        return this.datatypeFactory;
    }

    private boolean isFreshName(final OWLClassExpression ce) {
        if (ce.isAnonymous()) {
            return false;
        }
        return !this.knownEntities.contains(ce.asOWLClass());
    }

    public void ontologiesChanged(final List<? extends OWLOntologyChange> changes)
            throws OWLException {
        this.handleRawOntologyChanges(changes);
    }

    public BufferingMode getBufferingMode() {
        return this.bufferingMode;
    }

    public long getTimeOut() {
        return this.configuration.getTimeOut();
    }

    public OWLOntology getRootOntology() {
        return this.rootOntology;
    }

    /**
     * Handles raw ontology changes. If the reasoner is a buffering reasoner
     * then the changes will be stored in a buffer. If the reasoner is a
     * non-buffering reasoner then the changes will be automatically flushed
     * through to the change filter and passed on to the reasoner.
     * 
     * @param changes
     *            The list of raw changes.
     */
    private synchronized void handleRawOntologyChanges(
            final List<? extends OWLOntologyChange> changes) {
        this.rawChanges.addAll(changes);
        // We auto-flush the changes if the reasoner is non-buffering
        if (this.bufferingMode.equals(BufferingMode.NON_BUFFERING)) {
            this.flush();
        }
    }

    public synchronized List<OWLOntologyChange> getPendingChanges() {
        return new ArrayList<OWLOntologyChange>(this.rawChanges);
    }

    public synchronized Set<OWLAxiom> getPendingAxiomAdditions() {
        if (this.rawChanges.size() > 0) {
            Set<OWLAxiom> added = new HashSet<OWLAxiom>();
            this.computeDiff(added, new HashSet<OWLAxiom>());
            return added;
        }
        return Collections.emptySet();
    }

    public synchronized Set<OWLAxiom> getPendingAxiomRemovals() {
        if (this.rawChanges.size() > 0) {
            Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
            this.computeDiff(new HashSet<OWLAxiom>(), removed);
            return removed;
        }
        return Collections.emptySet();
    }

    /**
     * Flushes the pending changes from the pending change list. The changes
     * will be analysed to dermine which axioms have actually been added and
     * removed from the imports closure of the root ontology and then the
     * reasoner will be asked to handle these changes via the
     * {@link #handleChanges(java.util.Set, java.util.Set)} method.
     */
    public synchronized void flush() {
        // Process the changes
        if (this.rawChanges.size() > 0) {
            final Set<OWLAxiom> added = new HashSet<OWLAxiom>();
            final Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
            this.computeDiff(added, removed);
            this.rawChanges.clear();
            if (!added.isEmpty() || !removed.isEmpty()) {
                this.reasonerAxioms.removeAll(removed);
                this.reasonerAxioms.addAll(added);
                this.knownEntities.clear();
                for (OWLAxiom ax : this.reasonerAxioms) {
                    this.knownEntities.addAll(ax.getSignature());
                }
                // set the consistency status to not verified
                this.consistencyVerified = null;
                this.handleChanges(added, removed);
            }
        }
    }

    /**
     * Computes a diff of what axioms have been added and what axioms have been
     * removed from the list of pending changes. Note that even if the list of
     * pending changes is non-empty then there may be no changes for the
     * reasoner to deal with.
     * 
     * @param added
     *            The logical axioms that have been added to the imports closure
     *            of the reasoner root ontology
     * @param removed
     *            The logical axioms that have been removed from the imports
     *            closure of the reasoner root ontology
     */
    private synchronized void computeDiff(final Set<OWLAxiom> added,
            final Set<OWLAxiom> removed) {
        for (OWLOntology ont : this.rootOntology.getImportsClosure()) {
            for (OWLAxiom ax : ont.getLogicalAxioms()) {
                if (!this.reasonerAxioms.contains(ax.getAxiomWithoutAnnotations())) {
                    added.add(ax);
                }
            }
            for (OWLAxiom ax : ont.getAxioms(AxiomType.DECLARATION)) {
                if (!this.reasonerAxioms.contains(ax.getAxiomWithoutAnnotations())) {
                    added.add(ax);
                }
            }
        }
        for (OWLAxiom ax : this.reasonerAxioms) {
            if (!this.rootOntology.containsAxiomIgnoreAnnotations(ax, true)) {
                removed.add(ax);
            }
        }
    }

    public FreshEntityPolicy getFreshEntityPolicy() {
        return this.configuration.getFreshEntityPolicy();
    }

    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return this.configuration.getIndividualNodeSetPolicy();
    }

    /**
     * Asks the reasoner implementation to handle axiom additions and removals
     * from the imports closure of the root ontology. The changes will not
     * include annotation axiom additions and removals.
     * 
     * @param addAxioms
     *            The axioms to be added to the reasoner.
     * @param removeAxioms
     *            The axioms to be removed from the reasoner
     */
    private synchronized void handleChanges(final Set<OWLAxiom> addAxioms,
            final Set<OWLAxiom> removeAxioms) {
        this.translationMachinery.loadAxioms(addAxioms);
        for (OWLAxiom ax_r : removeAxioms) {
            this.translationMachinery.retractAxiom(ax_r);
        }
    }

    public String getReasonerName() {
        return REASONER_NAME;
    }

    public Version getReasonerVersion() {
        return VERSION;
    }

    public void interrupt() {
        this.interrupted.set(true);
    }

    // precompute inferences
    public synchronized void precomputeInferences(final InferenceType... inferenceTypes)
            throws ReasonerInterruptedException, TimeOutException,
            InconsistentOntologyException {
        for (InferenceType it : inferenceTypes) {
            if (supportedInferenceTypes.contains(it)) {
                if (!this.kernel.isKBRealised()) {
                    this.kernel.realiseKB();
                }
                return;
            }
        }
    }

    public boolean isPrecomputed(final InferenceType inferenceType) {
        if (supportedInferenceTypes.contains(inferenceType)) {
            return this.kernel.isKBRealised();
        }
        return true;
    }

    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return supportedInferenceTypes;
    }

    // consistency
    public synchronized boolean isConsistent() throws ReasonerInterruptedException,
            TimeOutException {
        if (this.consistencyVerified == null) {
            this.consistencyVerified = this.kernel.isKBConsistent();
        }
        return this.consistencyVerified;
    }

    private void checkConsistency() {
        if (this.interrupted.get()) {
            throw new ReasonerInterruptedException();
        }
        if (!this.isConsistent()) {
            throw new InconsistentOntologyException();
        }
    }

    public synchronized boolean isSatisfiable(final OWLClassExpression classExpression)
            throws ReasonerInterruptedException, TimeOutException,
            ClassExpressionNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        this.checkConsistency();
        return this.kernel.isSatisfiable(this.translationMachinery
                .toClassPointer(classExpression));
    }

    public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException,
            TimeOutException, InconsistentOntologyException {
        return this.getBottomClassNode();
    }

    // entailments
    public synchronized boolean isEntailed(final OWLAxiom axiom)
            throws ReasonerInterruptedException, UnsupportedEntailmentTypeException,
            TimeOutException, AxiomNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        this.checkConsistency();
        if (this.reasonerAxioms.contains(axiom.getAxiomWithoutAnnotations())) {
            return true;
        }
        try {
            boolean entailed = axiom.accept(this.translationMachinery
                    .getEntailmentChecker());
            return entailed;
        } catch (ReasonerFreshEntityException e) {
            String iri = e.getIri();
            if (this.getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
                for (OWLEntity o : axiom.getSignature()) {
                    if (o.getIRI().toString().equals(iri)) {
                        throw new FreshEntitiesException(o);
                    }
                }
                throw new FreshEntitiesException(axiom.getSignature());
            }
            System.out
                    .println("JFactReasoner.isEntailed() WARNING: fresh entity exception in the reasoner for entity: "
                            + iri + "; defaulting to axiom not entailed");
            return false;
        }
    }

    public synchronized boolean isEntailed(final Set<? extends OWLAxiom> axioms)
            throws ReasonerInterruptedException, UnsupportedEntailmentTypeException,
            TimeOutException, AxiomNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        for (OWLAxiom ax : axioms) {
            if (!this.isEntailed(ax)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEntailmentCheckingSupported(final AxiomType<?> axiomType) {
        if (axiomType.equals(AxiomType.SWRL_RULE)) {
            return false;
        }
        return true;
    }

    // tracing
    /*
     * Return tracing set (set of axioms that were participate in achieving
     * result) for a given entailment. Return empty set if the axiom is not
     * entailed.
     */
    public synchronized Set<OWLAxiom> getTrace(final OWLAxiom axiom) {
        this.kernel.needTracing();
        if (this.isEntailed(axiom)) {
            return this.translationMachinery.translateTAxiomSet(this.kernel.getTrace());
        }
        return Collections.emptySet();
    }

    // classes
    public Node<OWLClass> getTopClassNode() {
        return this.getEquivalentClasses(this.df.getOWLThing());
    }

    public Node<OWLClass> getBottomClassNode() {
        return this.getEquivalentClasses(this.df.getOWLNothing());
    }

    public synchronized NodeSet<OWLClass> getSubClasses(final OWLClassExpression ce,
            final boolean direct) throws ReasonerInterruptedException, TimeOutException,
            FreshEntitiesException, InconsistentOntologyException {
        if (this.isFreshName(ce)) {
            if (this.configuration.getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
                throw new FreshEntitiesException(ce.getSignature());
            }
            return new OWLClassNodeSet(this.getBottomClassNode());
        }
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new ClassPolicy());
        this.kernel.getSubConcepts(this.translationMachinery.toClassPointer(ce), direct,
                actor);
        Collection<Collection<ConceptExpression>> pointers = actor.getClassElements();
        return this.translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(pointers);
    }

    public synchronized NodeSet<OWLClass> getSuperClasses(final OWLClassExpression ce,
            final boolean direct) throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        if (this.isFreshName(ce)) {
            return new OWLClassNodeSet(this.getTopClassNode());
        }
        this.checkConsistency();
        return this.translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(
                        this.askSuperClasses(
                                this.translationMachinery.toClassPointer(ce), direct));
    }

    public synchronized NodeSet<OWLClass> getDisjointClasses(final OWLClassExpression ce) {
        TaxonomyActor actor = new TaxonomyActor(this.em, new ClassPolicy());
        ConceptExpression p = this.translationMachinery.toClassPointer(ce);
        this.kernel.getDisjointConcepts(p, actor);
        return this.translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(actor.getClassElements());
    }

    // object properties
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return this.getEquivalentObjectProperties(this.df.getOWLTopObjectProperty());
    }

    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return this.getEquivalentObjectProperties(this.df.getOWLBottomObjectProperty());
    }

    public synchronized NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
            final OWLObjectPropertyExpression pe, final boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new ObjectPropertyPolicy());
        this.kernel.getSubRoles(this.translationMachinery.toObjectPropertyPointer(pe),
                direct, actor);
        return this.translationMachinery.getObjectPropertyTranslator()
                .getNodeSetFromPointers(actor.getObjectPropertyElements());
    }

    public synchronized NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
            final OWLObjectPropertyExpression pe, final boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new ObjectPropertyPolicy());
        this.kernel.getSupRoles(this.translationMachinery.toObjectPropertyPointer(pe),
                direct, actor);
        return this.translationMachinery.getObjectPropertyTranslator()
                .getNodeSetFromPointers(actor.getObjectPropertyElements());
    }

    public synchronized Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
            final OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new ObjectPropertyPolicy());
        this.kernel.getEquivalentRoles(
                this.translationMachinery.toObjectPropertyPointer(pe), actor);
        return this.translationMachinery.getObjectPropertyTranslator()
                .getNodeFromPointers(actor.getObjectPropertySynonyms());
    }

    public synchronized NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
            final OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        this.checkConsistency();
        // TODO: incomplete
        OWLObjectPropertyNodeSet toReturn = new OWLObjectPropertyNodeSet();
        toReturn.addNode(this.getBottomObjectPropertyNode());
        return toReturn;
    }

    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
            final OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return this.getEquivalentObjectProperties(pe.getInverseProperty());
    }

    public synchronized NodeSet<OWLClass> getObjectPropertyDomains(
            final OWLObjectPropertyExpression pe, final boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        this.checkConsistency();
        ConceptExpression subClass = this.translationMachinery.toClassPointer(this.df
                .getOWLObjectSomeValuesFrom(pe, this.df.getOWLThing()));
        return this.translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(this.askSuperClasses(subClass, direct));
    }

    public NodeSet<OWLClass> getObjectPropertyRanges(
            final OWLObjectPropertyExpression pe, final boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return this.getSuperClasses(
                this.df.getOWLObjectSomeValuesFrom(pe.getInverseProperty(),
                        this.df.getOWLThing()), direct);
    }

    // data properties
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        OWLDataPropertyNode toReturn = new OWLDataPropertyNode();
        toReturn.add(this.df.getOWLTopDataProperty());
        return toReturn;
    }

    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return this.getEquivalentDataProperties(this.df.getOWLBottomDataProperty());
    }

    public synchronized NodeSet<OWLDataProperty> getSubDataProperties(
            final OWLDataProperty pe, final boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new DataPropertyPolicy());
        this.kernel.getSubRoles(this.translationMachinery.toDataPropertyPointer(pe),
                direct, actor);
        return this.translationMachinery.getDataPropertyTranslator()
                .getNodeSetFromPointers(actor.getDataPropertyElements());
    }

    public synchronized NodeSet<OWLDataProperty> getSuperDataProperties(
            final OWLDataProperty pe, final boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new DataPropertyPolicy());
        this.kernel.getSupRoles(this.translationMachinery.toDataPropertyPointer(pe),
                direct, actor);
        Collection<Collection<DataRoleExpression>> properties = actor
                .getDataPropertyElements();
        return this.translationMachinery.getDataPropertyTranslator()
                .getNodeSetFromPointers(properties);
    }

    public synchronized Node<OWLDataProperty> getEquivalentDataProperties(
            final OWLDataProperty pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        this.checkConsistency();
        DataRoleExpression p = this.translationMachinery.toDataPropertyPointer(pe);
        TaxonomyActor actor = new TaxonomyActor(this.em, new DataPropertyPolicy());
        this.kernel.getEquivalentRoles(p, actor);
        Collection<DataRoleExpression> dataPropertySynonyms = actor
                .getDataPropertySynonyms();
        return this.translationMachinery.getDataPropertyTranslator().getNodeFromPointers(
                dataPropertySynonyms);
    }

    public synchronized NodeSet<OWLDataProperty> getDisjointDataProperties(
            final OWLDataPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        this.checkConsistency();
        // TODO: incomplete
        OWLDataPropertyNodeSet toReturn = new OWLDataPropertyNodeSet();
        toReturn.addNode(this.getBottomDataPropertyNode());
        return toReturn;
    }

    public NodeSet<OWLClass> getDataPropertyDomains(final OWLDataProperty pe,
            final boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return this.getSuperClasses(
                this.df.getOWLDataSomeValuesFrom(pe, this.df.getTopDatatype()), direct);
    }

    // individuals
    public synchronized NodeSet<OWLClass> getTypes(final OWLNamedIndividual ind,
            final boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new ClassPolicy());
        this.kernel.getTypes(this.translationMachinery.toIndividualPointer(ind), direct,
                actor);
        Collection<Collection<ConceptExpression>> classElements = actor
                .getClassElements();
        return this.translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(classElements);
    }

    public synchronized NodeSet<OWLNamedIndividual> getInstances(
            final OWLClassExpression ce, final boolean direct)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException,
            ReasonerInterruptedException, TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new IndividualPolicy(true));
        this.kernel.getInstances(this.translationMachinery.toClassPointer(ce), actor,
                direct);
        return this.translationMachinery.translateIndividualPointersToNodeSet(actor
                .getPlainIndividualElements());
    }

    public synchronized NodeSet<OWLNamedIndividual> getObjectPropertyValues(
            final OWLNamedIndividual ind, final OWLObjectPropertyExpression pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        this.checkConsistency();
        List<Individual> fillers = this.kernel.getRoleFillers(
                this.translationMachinery.toIndividualPointer(ind),
                this.translationMachinery.toObjectPropertyPointer(pe));
        List<IndividualExpression> acc = new ArrayList<IndividualExpression>();
        for (NamedEntry p : fillers) {
            acc.add(this.em.individual(p.getName()));
        }
        return this.translationMachinery.translateIndividualPointersToNodeSet(acc);
    }

    public synchronized Set<OWLLiteral> getDataPropertyValues(
            final OWLNamedIndividual ind, final OWLDataProperty pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        this.checkConsistency();
        //		for(DataRoleExpression e:
        //		askDataProperties(translationMachinery.toIndividualPointer(ind))) {
        //
        //		}
        //		return kernel.getDRM().
        //		return translationMachinery
        //				.translateIndividualPointersToNodeSet(askRelatedIndividuals(
        //						translationMachinery.toIndividualPointer(ind),
        //						translationMachinery.toDataPropertyPointer(pe)));
        // TODO:
        return Collections.emptySet();
    }

    public synchronized Node<OWLNamedIndividual> getSameIndividuals(
            final OWLNamedIndividual ind) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        this.checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(this.em, new IndividualPolicy(true));
        this.kernel.getSameAs(this.translationMachinery.toIndividualPointer(ind), actor);
        return this.translationMachinery.getIndividualTranslator().getNodeFromPointers(
                actor.getIndividualSynonyms());
    }

    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
            final OWLNamedIndividual ind) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        OWLClassExpression ce = this.df.getOWLObjectOneOf(ind).getObjectComplementOf();
        return this.getInstances(ce, false);
    }

    public synchronized void dispose() {
        this.manager.removeOntologyChangeListener(this);
        this.translationMachinery = null;
        this.kernel = null;
    }

    public void dumpClassHierarchy(final LogAdapter pw, final boolean includeBottomNode) {
        this.dumpSubClasses(this.getTopClassNode(), pw, 0, includeBottomNode);
    }

    private void dumpSubClasses(final Node<OWLClass> node, final LogAdapter pw,
            final int depth, final boolean includeBottomNode) {
        if (includeBottomNode || !node.isBottomNode()) {
            for (int i = 0; i < depth; i++) {
                pw.print("    ");
            }
            pw.print(node);
            pw.println();
            for (Node<OWLClass> sub : this.getSubClasses(node.getRepresentativeElement(),
                    true)) {
                this.dumpSubClasses(sub, pw, depth + 1, includeBottomNode);
            }
        }
    }

    private Collection<Collection<ConceptExpression>> askSuperClasses(
            final ConceptExpression arg, final boolean direct) {
        TaxonomyActor actor = new TaxonomyActor(this.em, new ClassPolicy());
        this.kernel.getSupConcepts(arg, direct, actor);
        return actor.getClassElements();
    }

    public synchronized void writeReasoningResult(final LogAdapter o, final long time) {
        this.kernel.writeReasoningResult(o, time);
    }

    // owl knowledge exploration
    private class RootNodeImpl implements RootNode {
        private final DlCompletionTree pointer;

        public RootNodeImpl(final DlCompletionTree p) {
            this.pointer = p;
        }

        public <T> T getNode() {
            return (T) this.pointer;
        }
    }

    public RootNode getRoot(final OWLClassExpression expression) {
        return new RootNodeImpl(this.kernel.buildCompletionTree(this.translationMachinery
                .toClassPointer(expression)));
    }

    public Node<? extends OWLObjectPropertyExpression> getObjectNeighbours(
            final RootNode object, final boolean deterministicOnly) {
        List<ObjectRoleExpression> ret = new ArrayList<ObjectRoleExpression>();
        for (RoleExpression p : this.kernel.getObjectRoles(
                (DlCompletionTree) object.getNode(), deterministicOnly, false)) {
            ret.add((ObjectRoleExpression) p);
        }
        return this.translationMachinery.getObjectPropertyTranslator()
                .getNodeFromPointers(ret);
    }

    public Node<OWLDataProperty> getDataNeighbours(final RootNode object,
            final boolean deterministicOnly) {
        List<DataRoleExpression> ret = new ArrayList<DataRoleExpression>();
        for (RoleExpression p : this.kernel.getDataRoles(
                (DlCompletionTree) object.getNode(), deterministicOnly)) {
            ret.add((DataRoleExpression) p);
        }
        return this.translationMachinery.getDataPropertyTranslator().getNodeFromPointers(
                ret);
    }

    public Collection<RootNode> getObjectNeighbours(final RootNode n,
            final OWLObjectProperty property) {
        List<RootNode> toReturn = new ArrayList<RootNode>();
        for (DlCompletionTree t : this.kernel.getNeighbours(
                (DlCompletionTree) n.getNode(),
                this.translationMachinery.toObjectPropertyPointer(property))) {
            toReturn.add(new RootNodeImpl(t));
        }
        return toReturn;
    }

    public Collection<RootNode> getDataNeighbours(final RootNode n,
            final OWLDataProperty property) {
        List<RootNode> toReturn = new ArrayList<RootNode>();
        for (DlCompletionTree t : this.kernel.getNeighbours(
                (DlCompletionTree) n.getNode(),
                this.translationMachinery.toDataPropertyPointer(property))) {
            toReturn.add(new RootNodeImpl(t));
        }
        return toReturn;
    }

    public Node<? extends OWLClassExpression> getObjectLabel(final RootNode object,
            final boolean deterministicOnly) {
        return this.translationMachinery.getClassExpressionTranslator()
                .getNodeFromPointers(
                        this.kernel.getObjectLabel((DlCompletionTree) object.getNode(),
                                deterministicOnly));
    }

    public Node<? extends OWLDataRange> getDataLabel(final RootNode object,
            final boolean deterministicOnly) {
        return this.translationMachinery.getDataRangeTranslator().getNodeFromPointers(
                this.kernel.getDataLabel((DlCompletionTree) object.getNode(),
                        deterministicOnly));
    }

    public int getAtomicDecompositionSize(final boolean useSemantics,
            final ModuleType type) {
        return this.kernel.getAtomicDecompositionSize(useSemantics, type);
    }

    /// get a set of axioms that corresponds to the atom with the id INDEX
    public Set<OWLAxiom> getAtomAxioms(final int index) {
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (Axiom ax : this.kernel.getAtomAxioms(index)) {
            if (ax.getOWLAxiom() != null) {
                toReturn.add(ax.getOWLAxiom());
            }
        }
        return toReturn;
    }

    /// get a set of axioms that corresponds to the module of the atom with the id INDEX
    public Set<Axiom> getAtomModule(final int index) {
        return this.kernel.getAtomModule(index);
    }

    /// get a set of atoms on which atom with index INDEX depends
    public Set<TOntologyAtom> getAtomDependents(final int index) {
        return this.kernel.getAtomDependents(index);
    }

    private class EntityVisitorEx implements OWLEntityVisitorEx<Expression> {
        public Expression visit(final OWLClass cls) {
            return JFactReasoner.this.translationMachinery.toClassPointer(cls);
        }

        public Expression visit(final OWLObjectProperty property) {
            return JFactReasoner.this.translationMachinery
                    .toObjectPropertyPointer(property);
        }

        public Expression visit(final OWLDataProperty property) {
            return JFactReasoner.this.translationMachinery
                    .toDataPropertyPointer(property);
        }

        public Expression visit(final OWLNamedIndividual individual) {
            return JFactReasoner.this.translationMachinery
                    .toIndividualPointer(individual);
        }

        public Expression visit(final OWLDatatype datatype) {
            return null;
        }

        public Expression visit(final OWLAnnotationProperty property) {
            return null;
        }
    }

    final EntityVisitorEx entityTranslator = new EntityVisitorEx();

    public Set<OWLAxiom> getModule(final Set<OWLEntity> signature,
            final boolean useSemantic, final ModuleType moduletype) {
        List<Expression> list = new ArrayList<Expression>();
        for (OWLEntity entity : signature) {
            Expression ex = entity.accept(this.entityTranslator);
            if (ex != null) {
                list.add(ex);
            }
        }
        List<Axiom> axioms = this.kernel.getModule(list, useSemantic, moduletype);
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (Axiom ax : axioms) {
            if (ax.getOWLAxiom() != null) {
                toReturn.add(ax.getOWLAxiom());
            }
        }
        return toReturn;
    }

    public Set<OWLAxiom> getNonLocal(final Set<OWLEntity> signature,
            final boolean useSemantic, final ModuleType moduletype) {
        List<Expression> list = new ArrayList<Expression>();
        for (OWLEntity entity : signature) {
            Expression ex = entity.accept(this.entityTranslator);
            if (ex != null) {
                list.add(ex);
            }
        }
        Set<Axiom> axioms = this.kernel.getNonLocal(list, useSemantic, moduletype);
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (Axiom ax : axioms) {
            if (ax.getOWLAxiom() != null) {
                toReturn.add(ax.getOWLAxiom());
            }
        }
        return toReturn;
    }
}
