package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.model.parameters.Search;
import org.semanticweb.owlapi.reasoner.BufferingMode;
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
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDataPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLObjectPropertyNodeSet;
import org.semanticweb.owlapi.reasoner.knowledgeexploration.OWLKnowledgeExplorerReasoner;
import org.semanticweb.owlapi.util.Version;

import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.Individual;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.ReasonerFreshEntityException;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.actors.ClassPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.DataPropertyPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.IndividualPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.ObjectPropertyPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.TaxonomyActor;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.jfact.split.ModuleType;
import uk.ac.manchester.cs.jfact.split.TOntologyAtom;
import conformance.Original;

/**
 * Synchronization policy: all methods for OWLReasoner are synchronized, except
 * the methods which do not touch the kernel or only affect threadsafe data
 * structures. inner private classes are not synchronized since methods from
 * those classes cannot be invoked from outsize synchronized methods.
 */
public class JFactReasoner implements OWLReasoner, OWLOntologyChangeListener,
        OWLKnowledgeExplorerReasoner, Serializable {

    private static final long serialVersionUID = 10000L;
    protected final AtomicBoolean interrupted = new AtomicBoolean(false);
    private ReasoningKernel kernel;
    private final ExpressionCache em;
    @SuppressWarnings("null")
    @Nonnull
    private static final EnumSet<InferenceType> supportedInferenceTypes = EnumSet
            .of(InferenceType.CLASS_ASSERTIONS, InferenceType.CLASS_HIERARCHY,
                    InferenceType.DATA_PROPERTY_HIERARCHY,
                    InferenceType.OBJECT_PROPERTY_HIERARCHY,
                    InferenceType.SAME_INDIVIDUAL);
    @Nonnull
    private final OWLOntology root;
    @Nonnull
    private final BufferingMode bufferingMode;
    @Nonnull
    private final List<OWLOntologyChange> rawChanges = new ArrayList<>();
    @Nonnull
    private final Set<OWLAxiom> reasonerAxioms = new LinkedHashSet<>();
    @Original
    private final JFactReasonerConfiguration configuration;
    private final OWLDataFactory df;
    protected TranslationMachinery tr;
    // holds the consistency status: true for consistent, false for
    // inconsistent, null for not verified (or changes received)
    private Boolean consistencyVerified = null;
    private final Set<OWLEntity> knownEntities = new HashSet<>();
    private final DatatypeFactory datatypeFactory;

    /**
     * @param o
     *        o
     * @param c
     *        c
     * @param b
     *        b
     */
    public JFactReasoner(@Nonnull OWLOntology o,
            @Nonnull OWLReasonerConfiguration c, @Nonnull BufferingMode b) {
        this(
                o,
                c instanceof JFactReasonerConfiguration ? (JFactReasonerConfiguration) c
                        : new JFactReasonerConfiguration(c), b);
    }

    /**
     * @param rootOntology
     *        rootOntology
     * @param axioms
     *        axioms to actually use
     * @param config
     *        config
     * @param bufferingMode
     *        bufferingMode
     */
    public JFactReasoner(@Nonnull OWLOntology rootOntology,
            @Nonnull Collection<OWLAxiom> axioms,
            @Nonnull JFactReasonerConfiguration config,
            @Nonnull BufferingMode bufferingMode) {
        configuration = config;
        root = rootOntology;
        df = root.getOWLOntologyManager().getOWLDataFactory();
        datatypeFactory = DatatypeFactory.getInstance();
        kernel = new ReasoningKernel(configuration, datatypeFactory);
        em = kernel.getExpressionManager();
        this.bufferingMode = bufferingMode;
        knownEntities.add(df.getOWLThing());
        knownEntities.add(df.getOWLNothing());
        for (OWLOntology o : root.getImportsClosure()) {
            knownEntities.addAll(o.getSignature());
        }
        kernel.setInterruptedSwitch(interrupted);
        kernel.clearKB();
        configuration.getProgressMonitor().reasonerTaskStarted(
                ReasonerProgressMonitor.LOADING);
        configuration.getProgressMonitor().reasonerTaskBusy();
        tr = new TranslationMachinery(kernel, df, datatypeFactory);
        reasonerAxioms.addAll(axioms);
        tr.loadAxioms(reasonerAxioms);
        configuration.getProgressMonitor().reasonerTaskStopped();
    }

    /**
     * @param ont
     *        ontology
     * @return all axioms in the ontology and its import closure
     */
    @Nonnull
    public static List<OWLAxiom> importsIncluded(OWLOntology ont) {
        List<OWLAxiom> axioms = new ArrayList<>();
        for (OWLOntology o : ont.getImportsClosure()) {
            for (OWLAxiom ax : o.getLogicalAxioms()) {
                axioms.add(ax);
            }
            for (OWLAxiom ax : o.getAxioms(AxiomType.DECLARATION)) {
                axioms.add(ax);
            }
        }
        return axioms;
    }

    /**
     * @param rootOntology
     *        rootOntology
     * @param config
     *        config
     * @param bufferingMode
     *        bufferingMode
     */
    public JFactReasoner(@Nonnull OWLOntology rootOntology,
            @Nonnull JFactReasonerConfiguration config,
            @Nonnull BufferingMode bufferingMode) {
        this(rootOntology, importsIncluded(rootOntology), config, bufferingMode);
    }

    /**
     * @return the configuration for this reasoner
     */
    public JFactReasonerConfiguration getConfiguration() {
        return configuration;
    }

    /** @return ontology */
    public Ontology getOntology() {
        return kernel.getOntology();
    }

    @Override
    public synchronized Node<OWLClass> getEquivalentClasses(
            OWLClassExpression ce) {
        Collection<ConceptExpression> pointers = Collections.emptyList();
        if (!isFreshName(ce)) {
            checkConsistency();
            pointers = kernel.getEquivalentConcepts(tr.pointer(ce),
                    classActor()).getSynonyms();
        }
        return tr.getClassExpressionTranslator().node(pointers);
    }

    private boolean isFreshName(OWLClassExpression ce) {
        if (ce.isAnonymous()) {
            return false;
        }
        return !knownEntities.contains(ce.asOWLClass());
    }

    @Override
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
        rawChanges.addAll(changes);
        // We auto-flush the changes if the reasoner is non-buffering
        if (bufferingMode.equals(BufferingMode.NON_BUFFERING)) {
            flush();
        }
    }

    @Override
    public BufferingMode getBufferingMode() {
        return bufferingMode;
    }

    @Override
    public long getTimeOut() {
        return configuration.getTimeOut();
    }

    @Override
    public OWLOntology getRootOntology() {
        return root;
    }

    @Override
    public synchronized List<OWLOntologyChange> getPendingChanges() {
        return new ArrayList<>(rawChanges);
    }

    @SuppressWarnings("null")
    @Override
    public synchronized Set<OWLAxiom> getPendingAxiomAdditions() {
        if (!rawChanges.isEmpty()) {
            Set<OWLAxiom> added = new HashSet<>();
            computeDiff(added, new HashSet<OWLAxiom>());
            return added;
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("null")
    @Override
    public synchronized Set<OWLAxiom> getPendingAxiomRemovals() {
        if (!rawChanges.isEmpty()) {
            Set<OWLAxiom> removed = new HashSet<>();
            computeDiff(new HashSet<OWLAxiom>(), removed);
            return removed;
        }
        return Collections.emptySet();
    }

    @Override
    public synchronized void flush() {
        // Process the changes
        if (!rawChanges.isEmpty()) {
            Set<OWLAxiom> added = new HashSet<>();
            Set<OWLAxiom> removed = new HashSet<>();
            computeDiff(added, removed);
            rawChanges.clear();
            if (!added.isEmpty() || !removed.isEmpty()) {
                // for (OWLAxiom a : removed) {
                // breakCycles(a, false);
                // }
                // for (OWLAxiom a : added) {
                // breakCycles(a, true);
                // }
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

    /**
     * Computes a diff of what axioms have been added and what axioms have been
     * removed from the list of pending changes. Note that even if the list of
     * pending changes is non-empty then there may be no changes for the
     * reasoner to deal with.
     * 
     * @param added
     *        The logical axioms that have been added to the imports closure of
     *        the reasoner root ontology
     * @param removed
     *        The logical axioms that have been removed from the imports closure
     *        of the reasoner root ontology
     */
    private synchronized void computeDiff(Set<OWLAxiom> added,
            Set<OWLAxiom> removed) {
        for (OWLOntologyChange change : rawChanges) {
            OWLAxiom ax = change.getAxiom();
            if (change.isAddAxiom()) {
                if (!reasonerAxioms.contains(ax)
                        && !reasonerAxioms.contains(ax
                                .getAxiomWithoutAnnotations())) {
                    added.add(ax);
                }
            } else if (change.isRemoveAxiom()) {
                if (reasonerAxioms.contains(ax)
                        || reasonerAxioms.contains(ax
                                .getAxiomWithoutAnnotations())) {
                    removed.add(change.getAxiom());
                }
            }
        }
        added.removeAll(removed);
    }

    @Override
    public FreshEntityPolicy getFreshEntityPolicy() {
        return configuration.getFreshEntityPolicy();
    }

    @Override
    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return configuration.getIndividualNodeSetPolicy();
    }

    /**
     * Asks the reasoner implementation to handle axiom additions and removals
     * from the imports closure of the root ontology. The changes will not
     * include annotation axiom additions and removals.
     * 
     * @param addAxioms
     *        The axioms to be added to the reasoner.
     * @param removeAxioms
     *        The axioms to be removed from the reasoner
     */
    private synchronized void handleChanges(Set<OWLAxiom> addAxioms,
            Set<OWLAxiom> removeAxioms) {
        tr.loadAxioms(addAxioms);
        for (OWLAxiom ax_r : removeAxioms) {
            tr.retractAxiom(ax_r);
        }
    }

    @Override
    public String getReasonerName() {
        return "JFact";
    }

    @Override
    public Version getReasonerVersion() {
        return new Version(1, 2, 1, 0);
    }

    @Override
    public void interrupt() {
        interrupted.set(true);
    }

    // precompute inferences
    @Override
    public synchronized void precomputeInferences(
            InferenceType... inferenceTypes) {
        for (InferenceType it : inferenceTypes) {
            if (supportedInferenceTypes.contains(it)) {
                if (!kernel.isKBRealised()) {
                    kernel.realiseKB();
                }
                return;
            }
        }
    }

    @Override
    public boolean isPrecomputed(InferenceType inferenceType) {
        if (supportedInferenceTypes.contains(inferenceType)) {
            return kernel.isKBRealised();
        }
        return true;
    }

    @Override
    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return supportedInferenceTypes;
    }

    // consistency
    @Override
    public synchronized boolean isConsistent() {
        if (consistencyVerified == null) {
            try {
                consistencyVerified = kernel.isKBConsistent();
            } catch (InconsistentOntologyException e) {
                consistencyVerified = Boolean.FALSE;
            }
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

    @Override
    public synchronized boolean
            isSatisfiable(OWLClassExpression classExpression) {
        checkConsistency();
        return kernel.isSatisfiable(tr.pointer(classExpression));
    }

    @Override
    public Node<OWLClass> getUnsatisfiableClasses() {
        return getBottomClassNode();
    }

    // entailments
    @Override
    public synchronized boolean isEntailed(OWLAxiom axiom) {
        checkConsistency();
        if (root.containsAxiom(axiom, Imports.INCLUDED,
                Search.IGNORE_ANNOTATIONS)) {
            return true;
        }
        try {
            return axiom.accept(tr.getEntailmentChecker());
        } catch (ReasonerFreshEntityException e) {
            IRI iri = e.getIri();
            if (getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
                for (OWLEntity o : axiom.getSignature()) {
                    if (o.getIRI().equals(iri)) {
                        throw new FreshEntitiesException(o, e);
                    }
                }
                throw new FreshEntitiesException(axiom.getSignature(), e);
            }
            System.out
                    .println("JFactReasoner.isEntailed() WARNING: fresh entity exception in the reasoner for entity: "
                            + iri + "; defaulting to axiom not entailed");
            return false;
        }
    }

    @Override
    public synchronized boolean isEntailed(Set<? extends OWLAxiom> axioms) {
        for (OWLAxiom ax : axioms) {
            if (!this.isEntailed(checkNotNull(ax))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        if (axiomType.equals(AxiomType.SWRL_RULE)) {
            return false;
        }
        return true;
    }

    // tracing
    /**
     * @param axiom
     *        axiom
     * @return tracing set (set of axioms that were participate in achieving
     *         result) for a given entailment. Return empty set if the axiom is
     *         not entailed.
     */
    public synchronized Set<OWLAxiom> getTrace(@Nonnull OWLAxiom axiom) {
        kernel.needTracing();
        if (this.isEntailed(axiom)) {
            return tr.translateTAxiomSet(kernel.getTrace());
        }
        return Collections.emptySet();
    }

    // classes
    @Override
    public Node<OWLClass> getTopClassNode() {
        return getEquivalentClasses(df.getOWLThing());
    }

    @Override
    public Node<OWLClass> getBottomClassNode() {
        return getEquivalentClasses(df.getOWLNothing());
    }

    @Override
    public synchronized NodeSet<OWLClass> getSubClasses(OWLClassExpression ce,
            boolean direct) {
        if (isFreshName(ce)) {
            if (configuration.getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
                throw new FreshEntitiesException(ce.getSignature());
            }
            return new OWLClassNodeSet(getBottomClassNode());
        }
        checkConsistency();
        List<Collection<ConceptExpression>> pointers = kernel.getConcepts(
                tr.pointer(ce), direct, classActor(), false).getElements();
        if (pointers.isEmpty() || pointers.get(0).isEmpty()) {
            // XXX trick: empty sets imply the ony subclasses are the equivalent
            // to bottom.
            // should happen in the reasoner proper, but something stops that
            pointers.add(kernel.getEquivalentConcepts(
                    tr.pointer(df.getOWLNothing()), classActor()).getSynonyms());
        }
        return tr.getClassExpressionTranslator().nodeSet(pointers);
    }

    @Override
    public synchronized NodeSet<OWLClass> getSuperClasses(
            OWLClassExpression ce, boolean direct) {
        if (isFreshName(ce)) {
            return new OWLClassNodeSet(getTopClassNode());
        }
        checkConsistency();
        return tr.getClassExpressionTranslator().nodeSet(
                askSuperClasses(tr.pointer(ce), direct));
    }

    @Override
    public synchronized NodeSet<OWLClass> getDisjointClasses(
            OWLClassExpression ce) {
        ConceptExpression p = tr.pointer(ce);
        return tr.getClassExpressionTranslator().nodeSet(
                kernel.getDisjointConcepts(p, classActor()).getElements());
    }

    // object properties
    @Override
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return getEquivalentObjectProperties(df.getOWLTopObjectProperty());
    }

    @Override
    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return getEquivalentObjectProperties(df.getOWLBottomObjectProperty());
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression>
            getSubObjectProperties(OWLObjectPropertyExpression pe,
                    boolean direct) {
        checkConsistency();
        return tr.getObjectPropertyTranslator().nodeSet(
                kernel.getRoles(tr.pointer(pe), direct, objectActor(), false)
                        .getElements());
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression>
            getSuperObjectProperties(OWLObjectPropertyExpression pe,
                    boolean direct) {
        checkConsistency();
        List<Collection<ObjectRoleExpression>> elements = kernel.getRoles(
                tr.pointer(pe), direct, objectActor(), true).getElements();
        // XXX trick
        if (elements.isEmpty() || elements.get(0).isEmpty()) {
            elements.add(kernel.getEquivalentRoles(
                    tr.pointer(df.getOWLTopObjectProperty()), objectActor())
                    .getSynonyms());
        }
        return tr.getObjectPropertyTranslator().nodeSet(elements);
    }

    @Override
    public synchronized Node<OWLObjectPropertyExpression>
            getEquivalentObjectProperties(OWLObjectPropertyExpression pe) {
        checkConsistency();
        return tr.getObjectPropertyTranslator().node(
                kernel.getEquivalentRoles(tr.pointer(pe), objectActor())
                        .getSynonyms());
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression>
            getDisjointObjectProperties(OWLObjectPropertyExpression pe) {
        checkConsistency();
        // TODO: incomplete
        return new OWLObjectPropertyNodeSet(getBottomObjectPropertyNode());
    }

    @Override
    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
            OWLObjectPropertyExpression pe) {
        return getEquivalentObjectProperties(pe.getInverseProperty());
    }

    @Override
    public synchronized NodeSet<OWLClass> getObjectPropertyDomains(
            OWLObjectPropertyExpression pe, boolean direct) {
        checkConsistency();
        return tr.getClassExpressionTranslator().nodeSet(
                kernel.getORoleDomain(tr.pointer(pe), direct, classActor())
                        .getElements());
    }

    @Override
    public NodeSet<OWLClass> getObjectPropertyRanges(
            OWLObjectPropertyExpression pe, boolean direct) {
        return getObjectPropertyDomains(pe.getInverseProperty(), direct);
    }

    // data properties
    @Override
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        return new OWLDataPropertyNode(df.getOWLTopDataProperty());
    }

    @Override
    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        OWLDataPropertyNode toReturn = new OWLDataPropertyNode();
        toReturn.add(df.getOWLBottomDataProperty());
        return toReturn;
        // XXX both this and the previous method look like hacks
        // return getEquivalentDataProperties(df.getOWLBottomDataProperty());
    }

    @Override
    public synchronized NodeSet<OWLDataProperty> getSubDataProperties(
            OWLDataProperty pe, boolean direct) {
        checkConsistency();
        return tr.getDataPropertyTranslator().nodeSet(
                kernel.getRoles(tr.pointer(pe), direct, dataActor(), false)
                        .getElements());
    }

    @Override
    public synchronized NodeSet<OWLDataProperty> getSuperDataProperties(
            OWLDataProperty pe, boolean direct) {
        checkConsistency();
        List<Collection<DataRoleExpression>> elements = kernel.getRoles(
                tr.pointer(pe), direct, dataActor(), true).getElements();
        // XXX trick
        if (elements.isEmpty() || elements.get(0).isEmpty()) {
            elements.add(kernel.getEquivalentRoles(
                    tr.pointer(df.getOWLTopDataProperty()), dataActor())
                    .getSynonyms());
        }
        return tr.getDataPropertyTranslator().nodeSet(elements);
    }

    @Override
    public synchronized Node<OWLDataProperty> getEquivalentDataProperties(
            OWLDataProperty pe) {
        checkConsistency();
        DataRoleExpression p = tr.pointer(pe);
        Collection<DataRoleExpression> dataPropertySynonyms = kernel
                .getEquivalentRoles(p, dataActor()).getSynonyms();
        return tr.getDataPropertyTranslator().node(dataPropertySynonyms);
    }

    @Override
    public synchronized NodeSet<OWLDataProperty> getDisjointDataProperties(
            OWLDataPropertyExpression pe) {
        checkConsistency();
        // TODO: incomplete
        return new OWLDataPropertyNodeSet(getBottomDataPropertyNode());
    }

    @Override
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe,
            boolean direct) {
        return tr.getClassExpressionTranslator().nodeSet(
                kernel.getDRoleDomain(tr.pointer(pe), direct, classActor())
                        .getElements());
    }

    // individuals
    @Override
    public synchronized NodeSet<OWLClass> getTypes(OWLNamedIndividual ind,
            boolean direct) {
        checkConsistency();
        Collection<Collection<ConceptExpression>> classElements = kernel
                .getTypes(tr.pointer(ind), direct, classActor()).getElements();
        return tr.getClassExpressionTranslator().nodeSet(classElements);
    }

    /** @return class actor */
    private TaxonomyActor<ConceptExpression> classActor() {
        return new TaxonomyActor<>(em, new ClassPolicy());
    }

    /** @return object property actor */
    private TaxonomyActor<ObjectRoleExpression> objectActor() {
        return new TaxonomyActor<>(em, new ObjectPropertyPolicy());
    }

    /** @return data property actor */
    private TaxonomyActor<DataRoleExpression> dataActor() {
        return new TaxonomyActor<>(em, new DataPropertyPolicy());
    }

    /**
     * @param t
     *        type
     * @param <T>
     *        type
     * @return individual property actor
     */
    private <T extends Expression> TaxonomyActor<T> individualActor(
            @SuppressWarnings("unused") Class<T> t) {
        return new TaxonomyActor<>(em, new IndividualPolicy(true));
    }

    @Override
    public synchronized NodeSet<OWLNamedIndividual> getInstances(
            OWLClassExpression ce, boolean direct) {
        checkConsistency();
        TaxonomyActor<IndividualExpression> actor = kernel.getInstances(
                tr.pointer(ce), individualActor(IndividualExpression.class),
                direct);
        return tr.translateNodeSet(actor.getElements().iterator().next());
    }

    @Override
    public synchronized NodeSet<OWLNamedIndividual> getObjectPropertyValues(
            OWLNamedIndividual ind, OWLObjectPropertyExpression pe) {
        checkConsistency();
        List<Individual> fillers = kernel.getRoleFillers(tr.pointer(ind),
                tr.pointer(pe));
        List<IndividualExpression> acc = new ArrayList<>();
        for (NamedEntry p : fillers) {
            acc.add(em.individual(p.getName()));
        }
        return tr.translateNodeSet(acc);
    }

    @SuppressWarnings("null")
    @Override
    public synchronized Set<OWLLiteral> getDataPropertyValues(
            OWLNamedIndividual ind, OWLDataProperty pe) {
        checkConsistency();
        // for(DataRoleExpression e:
        // askDataProperties(translationMachinery.toIndividualPointer(ind))) {
        //
        // }
        // return kernel.getDRM().
        // return translationMachinery
        // .translateIndividualPointersToNodeSet(askRelatedIndividuals(
        // translationMachinery.toIndividualPointer(ind),
        // translationMachinery.pointer(pe)));
        // TODO:
        return Collections.emptySet();
    }

    @Override
    public synchronized Node<OWLNamedIndividual> getSameIndividuals(
            OWLNamedIndividual ind) {
        checkConsistency();
        return tr.getIndividualTranslator().node(
                kernel.getSameAs(tr.pointer(ind),
                        individualActor(IndividualName.class)).getSynonyms());
    }

    @Override
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
            OWLNamedIndividual ind) {
        OWLClassExpression ce = df.getOWLObjectOneOf(ind)
                .getObjectComplementOf();
        return getInstances(ce, false);
    }

    @Override
    public synchronized void dispose() {
        root.getOWLOntologyManager().removeOntologyChangeListener(this);
        tr = null;
        kernel = null;
    }

    /**
     * @param pw
     *        pw
     * @param includeBottomNode
     *        includeBottomNode
     */
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
            for (Node<OWLClass> sub : getSubClasses(
                    node.getRepresentativeElement(), true)) {
                dumpSubClasses(sub, pw, depth + 1, includeBottomNode);
            }
        }
    }

    private Collection<Collection<ConceptExpression>> askSuperClasses(
            ConceptExpression arg, boolean direct) {
        return kernel.getConcepts(arg, direct, classActor(), true)
                .getElements();
    }

    /**
     * @param time
     *        time
     */
    public synchronized void writeReasoningResult(long time) {
        kernel.writeReasoningResult(time);
    }

    // owl knowledge exploration
    private class RootNodeImpl implements RootNode, Serializable {

        private static final long serialVersionUID = 11000L;
        @Nonnull
        private final DlCompletionTree pointer;

        public RootNodeImpl(@Nonnull DlCompletionTree p) {
            pointer = p;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getNode() {
            return (T) pointer;
        }
    }

    @Override
    public RootNode getRoot(OWLClassExpression expression) {
        return new RootNodeImpl(kernel.buildCompletionTree(tr
                .pointer(expression)));
    }

    @Override
    public Node<? extends OWLObjectPropertyExpression> getObjectNeighbours(
            RootNode object, boolean deterministicOnly) {
        return tr.getObjectPropertyTranslator().node(
                kernel.getObjectRoles((DlCompletionTree) object.getNode(),
                        deterministicOnly, false));
    }

    @Override
    public Node<OWLDataProperty> getDataNeighbours(RootNode object,
            boolean deterministicOnly) {
        return tr.getDataPropertyTranslator().node(
                kernel.getDataRoles((DlCompletionTree) object.getNode(),
                        deterministicOnly));
    }

    @Override
    public Collection<RootNode> getObjectNeighbours(RootNode n,
            OWLObjectProperty property) {
        List<RootNode> toReturn = new ArrayList<>();
        for (DlCompletionTree t : kernel.getNeighbours(
                (DlCompletionTree) n.getNode(), tr.pointer(property))) {
            toReturn.add(new RootNodeImpl(checkNotNull(t)));
        }
        return toReturn;
    }

    @Override
    public Collection<RootNode> getDataNeighbours(RootNode n,
            OWLDataProperty property) {
        List<RootNode> toReturn = new ArrayList<>();
        for (DlCompletionTree t : kernel.getNeighbours(
                (DlCompletionTree) n.getNode(), tr.pointer(property))) {
            toReturn.add(new RootNodeImpl(checkNotNull(t)));
        }
        return toReturn;
    }

    @Override
    public Node<? extends OWLClassExpression> getObjectLabel(RootNode object,
            boolean deterministicOnly) {
        Node<OWLClass> nodeFromPointers = tr
                .getClassExpressionTranslator()
                .node(kernel.getObjectLabel(
                        (DlCompletionTree) object.getNode(), deterministicOnly));
        return nodeFromPointers;
    }

    @Override
    public Node<? extends OWLDataRange> getDataLabel(RootNode object,
            boolean deterministicOnly) {
        return tr.getDataRangeTranslator().node(
                kernel.getDataLabel((DlCompletionTree) object.getNode(),
                        deterministicOnly));
    }

    @Override
    public RootNode getBlocker(RootNode object) {
        return new RootNodeImpl(kernel.getBlocker((DlCompletionTree) object
                .getNode()));
    }

    /**
     * @param useSemantics
     *        useSemantics
     * @param type
     *        type
     * @return number of atoms
     */
    public int
            getAtomicDecompositionSize(boolean useSemantics, ModuleType type) {
        return kernel.getAtomicDecompositionSize(useSemantics, type);
    }

    /** @return set of tautologies */
    public Set<OWLAxiom> getTautologies() {
        return axiomsToSet(kernel.getTautologies());
    }

    /**
     * @param index
     *        index
     * @return set of axioms that corresponds to the atom with the id INDEX
     */
    public Set<OWLAxiom> getAtomAxioms(int index) {
        return axiomsToSet(kernel.getAtomAxioms(index));
    }

    private static Set<OWLAxiom> axiomsToSet(Collection<AxiomInterface> index) {
        Set<OWLAxiom> toReturn = new HashSet<>();
        for (AxiomInterface ax : index) {
            OWLAxiom owlAxiom = ax.getOWLAxiom();
            if (owlAxiom != null) {
                toReturn.add(owlAxiom);
            }
        }
        return toReturn;
    }

    /**
     * @param index
     *        index
     * @return set of axioms that corresponds to the module of the atom with the
     *         id INDEX
     */
    public Set<OWLAxiom> getAtomModule(int index) {
        return axiomsToSet(kernel.getAtomModule(index));
    }

    /** @return number of locality checks performed for Ad creation */
    public long getLocCheckNumber() {
        return kernel.getLocCheckNumber();
    }

    /**
     * @param index
     *        index
     * @return set of atoms on which atom with index INDEX depends
     */
    public Set<TOntologyAtom> getAtomDependents(int index) {
        return kernel.getAtomDependents(index);
    }

    /**
     * @param signature
     *        signature
     * @param useSemantic
     *        useSemantic
     * @param moduletype
     *        moduletype
     * @return set of axioms in the module
     */
    public Set<OWLAxiom> getModule(Set<OWLEntity> signature,
            boolean useSemantic, ModuleType moduletype) {
        List<Expression> list = tr.translateExpressions(signature);
        List<AxiomInterface> axioms = kernel.getModule(list, useSemantic,
                moduletype);
        return axiomsToSet(axioms);
    }

    /**
     * @param signature
     *        signature
     * @param useSemantic
     *        useSemantic
     * @param moduletype
     *        moduletype
     * @return set of non local axioms
     */
    public Set<OWLAxiom> getNonLocal(Set<OWLEntity> signature,
            boolean useSemantic, ModuleType moduletype) {
        List<Expression> list = tr.translateExpressions(signature);
        Set<AxiomInterface> axioms = kernel.getNonLocal(list, useSemantic,
                moduletype);
        return axiomsToSet(axioms);
    }

    /**
     * get all individuals from the set individuals that has r-successor and
     * s-successor and those are related via OP: r op s.
     * 
     * @param individuals
     *        set of individuals to choose from
     * @param r
     *        first operand of the comparison
     * @param s
     *        second operand of the comparison
     * @param op
     *        comparison operation: 0 means "equal", 1 means "different", 2
     *        means "lesser", 3 means "lesser than or equal", 4 means
     *        "greater than", 5 means "greater than or equal"
     * @return data related individuals
     */
    public synchronized Node<OWLNamedIndividual> getDataRelatedIndividuals(
            Set<OWLIndividual> individuals, OWLDataProperty r,
            OWLDataProperty s, int op) {
        checkConsistency();
        // load all the individuals as parameters
        return tr.getIndividualTranslator().node(
                kernel.getDataRelatedIndividuals(tr.pointer(r), tr.pointer(s),
                        op, tr.translate(individuals)));
    }
}
