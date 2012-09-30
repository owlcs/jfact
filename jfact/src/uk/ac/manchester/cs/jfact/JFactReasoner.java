package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;
import org.semanticweb.owlapi.util.Version;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.*;
import uk.ac.manchester.cs.jfact.kernel.actors.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.*;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Axiom;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;
import uk.ac.manchester.cs.jfact.split.ModuleType;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;

/** Synchronization policy: all methods for OWLReasoner are synchronized, except
 * the methods which do not touch the kernel or only affect threadsafe data
 * structures. inner private classes are not synchronized since methods from
 * those classes cannot be invoked from outsize synchronized methods. */
public class JFactReasoner implements OWLReasoner, OWLOntologyChangeListener,
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
    private final OWLOntology root;
    private final BufferingMode bufferingMode;
    private final List<OWLOntologyChange> rawChanges = new ArrayList<OWLOntologyChange>();
    private final List<OWLAxiom> reasonerAxioms = new ArrayList<OWLAxiom>();
    private final JFactReasonerConfiguration configuration;
    private final OWLDataFactory df;
    private TranslationMachinery translationMachinery;
    // holds the consistency status: true for consistent, false for
    // inconsistent, null for not verified (or changes received)
    private Boolean consistencyVerified = null;
    private final Set<OWLEntity> knownEntities = new HashSet<OWLEntity>();
    private final DatatypeFactory datatypeFactory;

    public JFactReasoner(OWLOntology o, OWLReasonerConfiguration c, BufferingMode b) {
        this(o, c instanceof JFactReasonerConfiguration ? (JFactReasonerConfiguration) c
                : new JFactReasonerConfiguration(c), b);
    }

    public JFactReasoner(OWLOntology rootOntology, JFactReasonerConfiguration config,
            BufferingMode bufferingMode) {
        configuration = config;
        root = rootOntology;
        df = root.getOWLOntologyManager().getOWLDataFactory();
        datatypeFactory = DatatypeFactory.getInstance();
        kernel = new ReasoningKernel(configuration, datatypeFactory);
        em = kernel.getExpressionManager();
        this.bufferingMode = bufferingMode;
        manager = root.getOWLOntologyManager();
        knownEntities.add(df.getOWLThing());
        knownEntities.add(df.getOWLNothing());
        for (OWLOntology ont : root.getImportsClosure()) {
            for (OWLAxiom ax : ont.getLogicalAxioms()) {
                OWLAxiom axiom = ax.getAxiomWithoutAnnotations();
                reasonerAxioms.add(axiom);
                knownEntities.addAll(axiom.getSignature());
            }
            for (OWLAxiom ax : ont.getAxioms(AxiomType.DECLARATION)) {
                OWLAxiom axiom = ax.getAxiomWithoutAnnotations();
                reasonerAxioms.add(axiom);
                knownEntities.addAll(axiom.getSignature());
            }
        }
        kernel.setTopBottomRoleNames(Vocabulary.TOP_OBJECT_PROPERTY,
                Vocabulary.BOTTOM_OBJECT_PROPERTY, Vocabulary.TOP_DATA_PROPERTY,
                Vocabulary.BOTTOM_DATA_PROPERTY);
        kernel.setInterruptedSwitch(interrupted);
        configuration.getProgressMonitor().reasonerTaskStarted(
                ReasonerProgressMonitor.LOADING);
        configuration.getProgressMonitor().reasonerTaskBusy();
        kernel.clearKB();
        translationMachinery = new TranslationMachinery(kernel, df, datatypeFactory);
        translationMachinery.loadAxioms(reasonerAxioms);
        configuration.getProgressMonitor().reasonerTaskStopped();
    }

    public synchronized Node<OWLClass> getEquivalentClasses(OWLClassExpression ce)
            throws InconsistentOntologyException, ClassExpressionNotInProfileException,
            ReasonerInterruptedException, TimeOutException {
        Collection<ConceptExpression> pointers;
        if (isFreshName(ce)) {
            pointers = Collections.emptyList();
        } else {
            checkConsistency();
            TaxonomyActor actor = new TaxonomyActor(em, new ClassPolicy());
            kernel.getEquivalentConcepts(translationMachinery.toClassPointer(ce), actor);
            pointers = actor.getClassSynonyms();
        }
        return translationMachinery.getClassExpressionTranslator().getNodeFromPointers(
                pointers);
    }

    public DatatypeFactory getDatatypeFactory() {
        return datatypeFactory;
    }

    private boolean isFreshName(OWLClassExpression ce) {
        if (ce.isAnonymous()) {
            return false;
        }
        return !knownEntities.contains(ce.asOWLClass());
    }

    public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
            throws OWLException {
        handleRawOntologyChanges(changes);
    }

    public BufferingMode getBufferingMode() {
        return bufferingMode;
    }

    public long getTimeOut() {
        return configuration.getTimeOut();
    }

    public OWLOntology getRootOntology() {
        return root;
    }

    /** Handles raw ontology changes. If the reasoner is a buffering reasoner
     * then the changes will be stored in a buffer. If the reasoner is a
     * non-buffering reasoner then the changes will be automatically flushed
     * through to the change filter and passed on to the reasoner.
     * 
     * @param changes
     *            The list of raw changes. */
    private synchronized void handleRawOntologyChanges(
            List<? extends OWLOntologyChange> changes) {
        rawChanges.addAll(changes);
        // We auto-flush the changes if the reasoner is non-buffering
        if (bufferingMode.equals(BufferingMode.NON_BUFFERING)) {
            flush();
        }
    }

    public synchronized List<OWLOntologyChange> getPendingChanges() {
        return new ArrayList<OWLOntologyChange>(rawChanges);
    }

    public synchronized Set<OWLAxiom> getPendingAxiomAdditions() {
        if (rawChanges.size() > 0) {
            Set<OWLAxiom> added = new HashSet<OWLAxiom>();
            computeDiff(added, new HashSet<OWLAxiom>());
            return added;
        }
        return Collections.emptySet();
    }

    public synchronized Set<OWLAxiom> getPendingAxiomRemovals() {
        if (rawChanges.size() > 0) {
            Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
            computeDiff(new HashSet<OWLAxiom>(), removed);
            return removed;
        }
        return Collections.emptySet();
    }

    /** Flushes the pending changes from the pending change list. The changes
     * will be analysed to dermine which axioms have actually been added and
     * removed from the imports closure of the root ontology and then the
     * reasoner will be asked to handle these changes via the
     * {@link #handleChanges(java.util.Set, java.util.Set)} method. */
    public synchronized void flush() {
        // Process the changes
        if (rawChanges.size() > 0) {
            Set<OWLAxiom> added = new HashSet<OWLAxiom>();
            Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
            computeDiff(added, removed);
            rawChanges.clear();
            if (!added.isEmpty() || !removed.isEmpty()) {
                reasonerAxioms.removeAll(removed);
                reasonerAxioms.addAll(added);
                knownEntities.clear();
                for (OWLAxiom ax : reasonerAxioms) {
                    knownEntities.addAll(ax.getSignature());
                }
                // set the consistency status to not verified
                consistencyVerified = null;
                handleChanges(added, removed);
            }
        }
    }

    /** Computes a diff of what axioms have been added and what axioms have been
     * removed from the list of pending changes. Note that even if the list of
     * pending changes is non-empty then there may be no changes for the
     * reasoner to deal with.
     * 
     * @param added
     *            The logical axioms that have been added to the imports closure
     *            of the reasoner root ontology
     * @param removed
     *            The logical axioms that have been removed from the imports
     *            closure of the reasoner root ontology */
    private synchronized void computeDiff(Set<OWLAxiom> added, Set<OWLAxiom> removed) {
        for (OWLOntology ont : root.getImportsClosure()) {
            for (OWLAxiom ax : ont.getLogicalAxioms()) {
                if (!reasonerAxioms.contains(ax.getAxiomWithoutAnnotations())) {
                    added.add(ax);
                }
            }
            for (OWLAxiom ax : ont.getAxioms(AxiomType.DECLARATION)) {
                if (!reasonerAxioms.contains(ax.getAxiomWithoutAnnotations())) {
                    added.add(ax);
                }
            }
        }
        for (OWLAxiom ax : reasonerAxioms) {
            if (!root.containsAxiomIgnoreAnnotations(ax, true)) {
                removed.add(ax);
            }
        }
    }

    public FreshEntityPolicy getFreshEntityPolicy() {
        return configuration.getFreshEntityPolicy();
    }

    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return configuration.getIndividualNodeSetPolicy();
    }

    /** Asks the reasoner implementation to handle axiom additions and removals
     * from the imports closure of the root ontology. The changes will not
     * include annotation axiom additions and removals.
     * 
     * @param addAxioms
     *            The axioms to be added to the reasoner.
     * @param removeAxioms
     *            The axioms to be removed from the reasoner */
    private synchronized void handleChanges(Set<OWLAxiom> addAxioms,
            Set<OWLAxiom> removeAxioms) {
        translationMachinery.loadAxioms(addAxioms);
        for (OWLAxiom ax_r : removeAxioms) {
            translationMachinery.retractAxiom(ax_r);
        }
    }

    public String getReasonerName() {
        return REASONER_NAME;
    }

    public Version getReasonerVersion() {
        return VERSION;
    }

    public void interrupt() {
        interrupted.set(true);
    }

    // precompute inferences
    public synchronized void precomputeInferences(InferenceType... inferenceTypes)
            throws ReasonerInterruptedException, TimeOutException,
            InconsistentOntologyException {
        for (InferenceType it : inferenceTypes) {
            if (supportedInferenceTypes.contains(it)) {
                if (!kernel.isKBRealised()) {
                    kernel.realiseKB();
                }
                return;
            }
        }
    }

    public boolean isPrecomputed(InferenceType inferenceType) {
        if (supportedInferenceTypes.contains(inferenceType)) {
            return kernel.isKBRealised();
        }
        return true;
    }

    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return supportedInferenceTypes;
    }

    // consistency
    public synchronized boolean isConsistent() throws ReasonerInterruptedException,
            TimeOutException {
        if (consistencyVerified == null) {
            consistencyVerified = kernel.isKBConsistent();
        }
        return consistencyVerified;
    }

    private void checkConsistency() {
        if (interrupted.get()) {
            throw new ReasonerInterruptedException();
        }
        if (!isConsistent()) {
            throw new InconsistentOntologyException();
        }
    }

    public synchronized boolean isSatisfiable(OWLClassExpression classExpression)
            throws ReasonerInterruptedException, TimeOutException,
            ClassExpressionNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        checkConsistency();
        return kernel.isSatisfiable(translationMachinery.toClassPointer(classExpression));
    }

    public Node<OWLClass> getUnsatisfiableClasses() throws ReasonerInterruptedException,
            TimeOutException, InconsistentOntologyException {
        return getBottomClassNode();
    }

    // entailments
    public synchronized boolean isEntailed(OWLAxiom axiom)
            throws ReasonerInterruptedException, UnsupportedEntailmentTypeException,
            TimeOutException, AxiomNotInProfileException, FreshEntitiesException,
            InconsistentOntologyException {
        checkConsistency();
        if (reasonerAxioms.contains(axiom.getAxiomWithoutAnnotations())) {
            return true;
        }
        try {
            boolean entailed = axiom.accept(translationMachinery.getEntailmentChecker());
            return entailed;
        } catch (ReasonerFreshEntityException e) {
            String iri = e.getIri();
            if (getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
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

    public synchronized boolean isEntailed(Set<? extends OWLAxiom> axioms)
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

    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
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
    public synchronized Set<OWLAxiom> getTrace(OWLAxiom axiom) {
        kernel.needTracing();
        if (this.isEntailed(axiom)) {
            return translationMachinery.translateTAxiomSet(kernel.getTrace());
        }
        return Collections.emptySet();
    }

    // classes
    public Node<OWLClass> getTopClassNode() {
        return getEquivalentClasses(df.getOWLThing());
    }

    public Node<OWLClass> getBottomClassNode() {
        return getEquivalentClasses(df.getOWLNothing());
    }

    public synchronized NodeSet<OWLClass> getSubClasses(OWLClassExpression ce,
            boolean direct) throws ReasonerInterruptedException, TimeOutException,
            FreshEntitiesException, InconsistentOntologyException {
        if (isFreshName(ce)) {
            if (configuration.getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
                throw new FreshEntitiesException(ce.getSignature());
            }
            return new OWLClassNodeSet(getBottomClassNode());
        }
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new ClassPolicy());
        kernel.getSubConcepts(translationMachinery.toClassPointer(ce), direct, actor);
        Collection<Collection<ConceptExpression>> pointers = actor.getClassElements();
        return translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(pointers);
    }

    public synchronized NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce,
            boolean direct) throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        if (isFreshName(ce)) {
            return new OWLClassNodeSet(getTopClassNode());
        }
        checkConsistency();
        return translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(
                        askSuperClasses(translationMachinery.toClassPointer(ce), direct));
    }

    public synchronized NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
        TaxonomyActor actor = new TaxonomyActor(em, new ClassPolicy());
        ConceptExpression p = translationMachinery.toClassPointer(ce);
        kernel.getDisjointConcepts(p, actor);
        return translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(actor.getClassElements());
    }

    // object properties
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return getEquivalentObjectProperties(df.getOWLTopObjectProperty());
    }

    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return getEquivalentObjectProperties(df.getOWLBottomObjectProperty());
    }

    public synchronized NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new ObjectPropertyPolicy());
        kernel.getSubRoles(translationMachinery.toObjectPropertyPointer(pe), direct,
                actor);
        return translationMachinery.getObjectPropertyTranslator().getNodeSetFromPointers(
                actor.getObjectPropertyElements());
    }

    public synchronized NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new ObjectPropertyPolicy());
        kernel.getSupRoles(translationMachinery.toObjectPropertyPointer(pe), direct,
                actor);
        return translationMachinery.getObjectPropertyTranslator().getNodeSetFromPointers(
                actor.getObjectPropertyElements());
    }

    public synchronized Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
            OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new ObjectPropertyPolicy());
        kernel.getEquivalentRoles(translationMachinery.toObjectPropertyPointer(pe), actor);
        return translationMachinery.getObjectPropertyTranslator().getNodeFromPointers(
                actor.getObjectPropertySynonyms());
    }

    public synchronized NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
            OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        // TODO: incomplete
        OWLObjectPropertyNodeSet toReturn = new OWLObjectPropertyNodeSet();
        toReturn.addNode(getBottomObjectPropertyNode());
        return toReturn;
    }

    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
            OWLObjectPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return getEquivalentObjectProperties(pe.getInverseProperty());
    }

    public synchronized NodeSet<OWLClass> getObjectPropertyDomains(
            OWLObjectPropertyExpression pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        ConceptExpression subClass = translationMachinery.toClassPointer(df
                .getOWLObjectSomeValuesFrom(pe, df.getOWLThing()));
        return translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(askSuperClasses(subClass, direct));
    }

    public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe,
            boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        return getSuperClasses(
                df.getOWLObjectSomeValuesFrom(pe.getInverseProperty(), df.getOWLThing()),
                direct);
    }

    // data properties
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        OWLDataPropertyNode toReturn = new OWLDataPropertyNode();
        toReturn.add(df.getOWLTopDataProperty());
        return toReturn;
    }

    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return getEquivalentDataProperties(df.getOWLBottomDataProperty());
    }

    public synchronized NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe,
            boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new DataPropertyPolicy());
        kernel.getSubRoles(translationMachinery.toDataPropertyPointer(pe), direct, actor);
        return translationMachinery.getDataPropertyTranslator().getNodeSetFromPointers(
                actor.getDataPropertyElements());
    }

    public synchronized NodeSet<OWLDataProperty> getSuperDataProperties(
            OWLDataProperty pe, boolean direct) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new DataPropertyPolicy());
        kernel.getSupRoles(translationMachinery.toDataPropertyPointer(pe), direct, actor);
        Collection<Collection<DataRoleExpression>> properties = actor
                .getDataPropertyElements();
        return translationMachinery.getDataPropertyTranslator().getNodeSetFromPointers(
                properties);
    }

    public synchronized Node<OWLDataProperty> getEquivalentDataProperties(
            OWLDataProperty pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        DataRoleExpression p = translationMachinery.toDataPropertyPointer(pe);
        TaxonomyActor actor = new TaxonomyActor(em, new DataPropertyPolicy());
        kernel.getEquivalentRoles(p, actor);
        Collection<DataRoleExpression> dataPropertySynonyms = actor
                .getDataPropertySynonyms();
        return translationMachinery.getDataPropertyTranslator().getNodeFromPointers(
                dataPropertySynonyms);
    }

    public synchronized NodeSet<OWLDataProperty> getDisjointDataProperties(
            OWLDataPropertyExpression pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        // TODO: incomplete
        OWLDataPropertyNodeSet toReturn = new OWLDataPropertyNodeSet();
        toReturn.addNode(getBottomDataPropertyNode());
        return toReturn;
    }

    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        return getSuperClasses(df.getOWLDataSomeValuesFrom(pe, df.getTopDatatype()),
                direct);
    }

    // individuals
    public synchronized NodeSet<OWLClass>
            getTypes(OWLNamedIndividual ind, boolean direct)
                    throws InconsistentOntologyException, ReasonerInterruptedException,
                    TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new ClassPolicy());
        kernel.getTypes(translationMachinery.toIndividualPointer(ind), direct, actor);
        Collection<Collection<ConceptExpression>> classElements = actor
                .getClassElements();
        return translationMachinery.getClassExpressionTranslator()
                .getNodeSetFromPointers(classElements);
    }

    public synchronized NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce,
            boolean direct) throws InconsistentOntologyException,
            ClassExpressionNotInProfileException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new IndividualPolicy(true));
        kernel.getInstances(translationMachinery.toClassPointer(ce), actor, direct);
        return translationMachinery.translateIndividualPointersToNodeSet(actor
                .getPlainIndividualElements());
    }

    public synchronized NodeSet<OWLNamedIndividual> getObjectPropertyValues(
            OWLNamedIndividual ind, OWLObjectPropertyExpression pe)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        checkConsistency();
        List<Individual> fillers = kernel.getRoleFillers(
                translationMachinery.toIndividualPointer(ind),
                translationMachinery.toObjectPropertyPointer(pe));
        List<IndividualExpression> acc = new ArrayList<IndividualExpression>();
        for (NamedEntry p : fillers) {
            acc.add(em.individual(p.getName()));
        }
        return translationMachinery.translateIndividualPointersToNodeSet(acc);
    }

    public synchronized Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind,
            OWLDataProperty pe) throws InconsistentOntologyException,
            ReasonerInterruptedException, TimeOutException {
        checkConsistency();
        // for(DataRoleExpression e:
        // askDataProperties(translationMachinery.toIndividualPointer(ind))) {
        //
        // }
        // return kernel.getDRM().
        // return translationMachinery
        // .translateIndividualPointersToNodeSet(askRelatedIndividuals(
        // translationMachinery.toIndividualPointer(ind),
        // translationMachinery.toDataPropertyPointer(pe)));
        // TODO:
        return Collections.emptySet();
    }

    public synchronized Node<OWLNamedIndividual>
            getSameIndividuals(OWLNamedIndividual ind)
                    throws InconsistentOntologyException, ReasonerInterruptedException,
                    TimeOutException {
        checkConsistency();
        TaxonomyActor actor = new TaxonomyActor(em, new IndividualPolicy(true));
        kernel.getSameAs(translationMachinery.toIndividualPointer(ind), actor);
        return translationMachinery.getIndividualTranslator().getNodeFromPointers(
                actor.getIndividualSynonyms());
    }

    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual ind)
            throws InconsistentOntologyException, ReasonerInterruptedException,
            TimeOutException {
        OWLClassExpression ce = df.getOWLObjectOneOf(ind).getObjectComplementOf();
        return getInstances(ce, false);
    }

    public synchronized void dispose() {
        manager.removeOntologyChangeListener(this);
        translationMachinery = null;
        kernel = null;
    }

    public void dumpClassHierarchy(LogAdapter pw, boolean includeBottomNode) {
        dumpSubClasses(getTopClassNode(), pw, 0, includeBottomNode);
    }

    private void dumpSubClasses(Node<OWLClass> node, LogAdapter pw, int depth,
            boolean includeBottomNode) {
        if (includeBottomNode || !node.isBottomNode()) {
            for (int i = 0; i < depth; i++) {
                pw.print("    ");
            }
            pw.print(node);
            pw.println();
            for (Node<OWLClass> sub : getSubClasses(node.getRepresentativeElement(), true)) {
                dumpSubClasses(sub, pw, depth + 1, includeBottomNode);
            }
        }
    }

    private Collection<Collection<ConceptExpression>> askSuperClasses(
            ConceptExpression arg, boolean direct) {
        TaxonomyActor actor = new TaxonomyActor(em, new ClassPolicy());
        kernel.getSupConcepts(arg, direct, actor);
        return actor.getClassElements();
    }

    public synchronized void writeReasoningResult(LogAdapter o, long time) {
        kernel.writeReasoningResult(o, time);
    }

    // owl knowledge exploration
    private class RootNodeImpl implements RootNode {
        private DlCompletionTree pointer;

        public RootNodeImpl(DlCompletionTree p) {
            pointer = p;
        }

        public <T> T getNode() {
            return (T) pointer;
        }
    }

    public RootNode getRoot(OWLClassExpression expression) {
        return new RootNodeImpl(kernel.buildCompletionTree(translationMachinery
                .toClassPointer(expression)));
    }

    public Node<? extends OWLObjectPropertyExpression> getObjectNeighbours(
            RootNode object, boolean deterministicOnly) {
        List<ObjectRoleExpression> ret = new ArrayList<ObjectRoleExpression>();
        for (RoleExpression p : kernel.getObjectRoles(
                (DlCompletionTree) object.getNode(), deterministicOnly, false)) {
            ret.add((ObjectRoleExpression) p);
        }
        return translationMachinery.getObjectPropertyTranslator()
                .getNodeFromPointers(ret);
    }

    public Node<OWLDataProperty> getDataNeighbours(RootNode object,
            boolean deterministicOnly) {
        List<DataRoleExpression> ret = new ArrayList<DataRoleExpression>();
        for (RoleExpression p : kernel.getDataRoles((DlCompletionTree) object.getNode(),
                deterministicOnly)) {
            ret.add((DataRoleExpression) p);
        }
        return translationMachinery.getDataPropertyTranslator().getNodeFromPointers(ret);
    }

    public Collection<RootNode>
            getObjectNeighbours(RootNode n, OWLObjectProperty property) {
        List<RootNode> toReturn = new ArrayList<RootNode>();
        for (DlCompletionTree t : kernel.getNeighbours((DlCompletionTree) n.getNode(),
                translationMachinery.toObjectPropertyPointer(property))) {
            toReturn.add(new RootNodeImpl(t));
        }
        return toReturn;
    }

    public Collection<RootNode> getDataNeighbours(RootNode n, OWLDataProperty property) {
        List<RootNode> toReturn = new ArrayList<RootNode>();
        for (DlCompletionTree t : kernel.getNeighbours((DlCompletionTree) n.getNode(),
                translationMachinery.toDataPropertyPointer(property))) {
            toReturn.add(new RootNodeImpl(t));
        }
        return toReturn;
    }

    public Node<? extends OWLClassExpression> getObjectLabel(RootNode object,
            boolean deterministicOnly) {
        return translationMachinery.getClassExpressionTranslator().getNodeFromPointers(
                kernel.getObjectLabel((DlCompletionTree) object.getNode(),
                        deterministicOnly));
    }

    public Node<? extends OWLDataRange> getDataLabel(RootNode object,
            boolean deterministicOnly) {
        return translationMachinery.getDataRangeTranslator().getNodeFromPointers(
                kernel.getDataLabel((DlCompletionTree) object.getNode(),
                        deterministicOnly));
    }

    public int getAtomicDecompositionSize(boolean useSemantics, ModuleType type) {
        return kernel.getAtomicDecompositionSize(useSemantics, type);
    }

    public Set<OWLAxiom> getTautologies() {
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (Axiom ax : kernel.getTautologies()) {
            if (ax.getOWLAxiom() != null) {
                toReturn.add(ax.getOWLAxiom());
            }
        }
        return toReturn;
    }

    /** get a set of axioms that corresponds to the atom with the id INDEX */
    public Set<OWLAxiom> getAtomAxioms(int index) {
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (Axiom ax : kernel.getAtomAxioms(index)) {
            if (ax.getOWLAxiom() != null) {
                toReturn.add(ax.getOWLAxiom());
            }
        }
        return toReturn;
    }

    /** get a set of axioms that corresponds to the module of the atom with the */
    // id INDEX
    public Set<Axiom> getAtomModule(int index) {
        return kernel.getAtomModule(index);
    }

    /** get a set of atoms on which atom with index INDEX depends */
    public Set<TOntologyAtom> getAtomDependents(int index) {
        return kernel.getAtomDependents(index);
    }

    private class EntityVisitorEx implements OWLEntityVisitorEx<Expression> {
        public Expression visit(OWLClass cls) {
            return translationMachinery.toClassPointer(cls);
        }

        public Expression visit(OWLObjectProperty property) {
            return translationMachinery.toObjectPropertyPointer(property);
        }

        public Expression visit(OWLDataProperty property) {
            return translationMachinery.toDataPropertyPointer(property);
        }

        public Expression visit(OWLNamedIndividual individual) {
            return translationMachinery.toIndividualPointer(individual);
        }

        public Expression visit(OWLDatatype datatype) {
            return null;
        }

        public Expression visit(OWLAnnotationProperty property) {
            return null;
        }
    }

    EntityVisitorEx entityTranslator = new EntityVisitorEx();

    public Set<OWLAxiom> getModule(Set<OWLEntity> signature, boolean useSemantic,
            ModuleType moduletype) {
        List<Expression> list = new ArrayList<Expression>();
        for (OWLEntity entity : signature) {
            Expression ex = entity.accept(entityTranslator);
            if (ex != null) {
                list.add(ex);
            }
        }
        // System.out.println("JFactReasoner.getModule() " + list);
        List<Axiom> axioms = kernel.getModule(list, useSemantic, moduletype);
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (Axiom ax : axioms) {
            if (ax.getOWLAxiom() != null) {
                toReturn.add(ax.getOWLAxiom());
                // } else {
                // System.out.println("JFactReasoner.getModule() " + ax);
            }
        }
        return toReturn;
    }

    public Set<OWLAxiom> getNonLocal(Set<OWLEntity> signature, boolean useSemantic,
            ModuleType moduletype) {
        List<Expression> list = new ArrayList<Expression>();
        for (OWLEntity entity : signature) {
            Expression ex = entity.accept(entityTranslator);
            if (ex != null) {
                list.add(ex);
            }
        }
        Set<Axiom> axioms = kernel.getNonLocal(list, useSemantic, moduletype);
        Set<OWLAxiom> toReturn = new HashSet<OWLAxiom>();
        for (Axiom ax : axioms) {
            if (ax.getOWLAxiom() != null) {
                toReturn.add(ax.getOWLAxiom());
            }
        }
        return toReturn;
    }
}
