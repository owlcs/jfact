package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.model.AxiomType.ANNOTATION_ASSERTION;
import static org.semanticweb.owlapi.model.AxiomType.ANNOTATION_PROPERTY_DOMAIN;
import static org.semanticweb.owlapi.model.AxiomType.ANNOTATION_PROPERTY_RANGE;
import static org.semanticweb.owlapi.model.AxiomType.ASYMMETRIC_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.CLASS_ASSERTION;
import static org.semanticweb.owlapi.model.AxiomType.DATATYPE_DEFINITION;
import static org.semanticweb.owlapi.model.AxiomType.DATA_PROPERTY_ASSERTION;
import static org.semanticweb.owlapi.model.AxiomType.DATA_PROPERTY_DOMAIN;
import static org.semanticweb.owlapi.model.AxiomType.DATA_PROPERTY_RANGE;
import static org.semanticweb.owlapi.model.AxiomType.DECLARATION;
import static org.semanticweb.owlapi.model.AxiomType.DIFFERENT_INDIVIDUALS;
import static org.semanticweb.owlapi.model.AxiomType.DISJOINT_CLASSES;
import static org.semanticweb.owlapi.model.AxiomType.DISJOINT_DATA_PROPERTIES;
import static org.semanticweb.owlapi.model.AxiomType.DISJOINT_OBJECT_PROPERTIES;
import static org.semanticweb.owlapi.model.AxiomType.DISJOINT_UNION;
import static org.semanticweb.owlapi.model.AxiomType.EQUIVALENT_CLASSES;
import static org.semanticweb.owlapi.model.AxiomType.EQUIVALENT_DATA_PROPERTIES;
import static org.semanticweb.owlapi.model.AxiomType.EQUIVALENT_OBJECT_PROPERTIES;
import static org.semanticweb.owlapi.model.AxiomType.FUNCTIONAL_DATA_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.FUNCTIONAL_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.HAS_KEY;
import static org.semanticweb.owlapi.model.AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.INVERSE_OBJECT_PROPERTIES;
import static org.semanticweb.owlapi.model.AxiomType.IRREFLEXIVE_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION;
import static org.semanticweb.owlapi.model.AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION;
import static org.semanticweb.owlapi.model.AxiomType.OBJECT_PROPERTY_ASSERTION;
import static org.semanticweb.owlapi.model.AxiomType.OBJECT_PROPERTY_DOMAIN;
import static org.semanticweb.owlapi.model.AxiomType.OBJECT_PROPERTY_RANGE;
import static org.semanticweb.owlapi.model.AxiomType.REFLEXIVE_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.SAME_INDIVIDUAL;
import static org.semanticweb.owlapi.model.AxiomType.SUBCLASS_OF;
import static org.semanticweb.owlapi.model.AxiomType.SUB_ANNOTATION_PROPERTY_OF;
import static org.semanticweb.owlapi.model.AxiomType.SUB_DATA_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.SUB_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.SUB_PROPERTY_CHAIN_OF;
import static org.semanticweb.owlapi.model.AxiomType.SWRL_RULE;
import static org.semanticweb.owlapi.model.AxiomType.SYMMETRIC_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.model.AxiomType.TRANSITIVE_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.add;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
import org.semanticweb.owlapi.model.parameters.AxiomAnnotations;
import org.semanticweb.owlapi.model.parameters.Imports;
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
import org.semanticweb.owlapitools.decomposition.AxiomWrapper;
import org.semanticweb.owlapitools.decomposition.OntologyAtom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conformance.Original;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.kernel.DlCompletionTree;
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.Individual;
import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.ReasonerFreshEntityException;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.actors.ClassPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.DataPropertyPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.IndividualPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.ObjectPropertyPolicy;
import uk.ac.manchester.cs.jfact.kernel.actors.TaxonomyActor;
import uk.ac.manchester.cs.jfact.kernel.dl.IndividualName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.IndividualExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/**
 * Synchronization policy: all methods for OWLReasoner are synchronized, except the methods which do
 * not touch the kernel or only affect threadsafe data structures. inner private classes are not
 * synchronized since methods from those classes cannot be invoked from outsize synchronized
 * methods.
 */
public class JFactReasoner
    implements OWLReasoner, OWLOntologyChangeListener, OWLKnowledgeExplorerReasoner, Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JFactReasoner.class);
    protected final AtomicBoolean interrupted = new AtomicBoolean(false);
    private ReasoningKernel kernel;
    private final ExpressionCache em;
    @Nonnull
    private static final EnumSet<InferenceType> supportedInferenceTypes =
        EnumSet.of(InferenceType.CLASS_ASSERTIONS, InferenceType.CLASS_HIERARCHY,
            InferenceType.DATA_PROPERTY_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY,
            InferenceType.SAME_INDIVIDUAL);
    private static final Collection<AxiomType<?>> types = Arrays.asList(DECLARATION,
        OBJECT_PROPERTY_RANGE, FUNCTIONAL_OBJECT_PROPERTY, DIFFERENT_INDIVIDUALS,
        EQUIVALENT_CLASSES, SYMMETRIC_OBJECT_PROPERTY, DATA_PROPERTY_DOMAIN, DISJOINT_CLASSES,
        SUBCLASS_OF, DATA_PROPERTY_RANGE, TRANSITIVE_OBJECT_PROPERTY, INVERSE_OBJECT_PROPERTIES,
        OBJECT_PROPERTY_DOMAIN, SUB_OBJECT_PROPERTY, DATA_PROPERTY_ASSERTION
        // all wine axioms
        , DISJOINT_OBJECT_PROPERTIES, EQUIVALENT_OBJECT_PROPERTIES, SUB_PROPERTY_CHAIN_OF,
        INVERSE_FUNCTIONAL_OBJECT_PROPERTY, ASYMMETRIC_OBJECT_PROPERTY, REFLEXIVE_OBJECT_PROPERTY,
        IRREFLEXIVE_OBJECT_PROPERTY, DISJOINT_DATA_PROPERTIES, SUB_DATA_PROPERTY,
        EQUIVALENT_DATA_PROPERTIES, FUNCTIONAL_DATA_PROPERTY, DATATYPE_DEFINITION, DISJOINT_UNION,
        SAME_INDIVIDUAL, HAS_KEY, NEGATIVE_OBJECT_PROPERTY_ASSERTION,
        NEGATIVE_DATA_PROPERTY_ASSERTION, SUB_ANNOTATION_PROPERTY_OF, ANNOTATION_PROPERTY_DOMAIN,
        ANNOTATION_ASSERTION, ANNOTATION_PROPERTY_RANGE, SWRL_RULE, CLASS_ASSERTION,
        OBJECT_PROPERTY_ASSERTION);
    @Nonnull
    private final OWLOntology root;
    @Nonnull
    private final BufferingMode bufferingMode;
    @Nonnull
    private final List<OWLOntologyChange> rawChanges = new ArrayList<>();
    @Nonnull
    private final List<OWLAxiom> axioms = new ArrayList<>();
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
     * @param o o
     * @param c c
     * @param b b
     */
    public JFactReasoner(OWLOntology o, OWLReasonerConfiguration c, BufferingMode b) {
        this(o, c instanceof JFactReasonerConfiguration ? (JFactReasonerConfiguration) c
            : new JFactReasonerConfiguration(c), b);
    }

    /**
     * @param rootOntology rootOntology
     * @param axioms axioms to actually use
     * @param config config
     * @param bufferingMode bufferingMode
     */
    public JFactReasoner(OWLOntology rootOntology, Collection<OWLAxiom> axioms,
        JFactReasonerConfiguration config, BufferingMode bufferingMode) {
        configuration = config;
        root = rootOntology;
        df = root.getOWLOntologyManager().getOWLDataFactory();
        datatypeFactory = DatatypeFactory.getInstance();
        kernel = new ReasoningKernel(configuration, datatypeFactory, df);
        em = kernel.getExpressionManager();
        this.bufferingMode = bufferingMode;
        knownEntities.add(df.getOWLThing());
        knownEntities.add(df.getOWLNothing());
        add(knownEntities, root.importsClosure().flatMap(o -> o.signature()));
        kernel.setInterruptedSwitch(interrupted);
        kernel.clearKB();
        configuration.getProgressMonitor().reasonerTaskStarted(ReasonerProgressMonitor.LOADING);
        configuration.getProgressMonitor().reasonerTaskBusy();
        tr = new TranslationMachinery(kernel, df, datatypeFactory);
        this.axioms.addAll(axioms);
        tr.loadAxioms(axioms.stream());
        configuration.getProgressMonitor().reasonerTaskStopped();
    }

    /**
     * @param rootOntology rootOntology
     * @param config config
     * @param bufferingMode bufferingMode
     */
    public JFactReasoner(OWLOntology rootOntology, JFactReasonerConfiguration config,
        BufferingMode bufferingMode) {
        this(rootOntology, importsIncluded(rootOntology), config, bufferingMode);
    }

    /**
     * @param ont ontology
     * @return all axioms in the ontology and its import closure
     */
    public static List<OWLAxiom> importsIncluded(OWLOntology ont) {
        // for each imported ontology,
        // take all axioms in the sequence defined in types
        List<OWLAxiom> list = new ArrayList<>();
        ont.importsClosure().forEach(o -> types.forEach(t -> add(list, o.axioms(t))));
        return list;
    }

    /**
     * @return the configuration for this reasoner
     */
    public JFactReasonerConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @return ontology
     */
    public Ontology getOntology() {
        return kernel.getOntology();
    }

    @Override
    public synchronized Node<OWLClass> getEquivalentClasses(OWLClassExpression ce) {
        if (isFreshName(ce)) {
            return tr.getClassExpressionTranslator().node(Stream.empty());
        }
        checkConsistency();
        Stream<ConceptExpression> stream =
            kernel.getEquivalentConcepts(tr.pointer(ce), classActor()).getSynonyms().stream();
        return tr.getClassExpressionTranslator().node(stream);
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

    @Override
    public synchronized Set<OWLAxiom> getPendingAxiomAdditions() {
        if (!rawChanges.isEmpty()) {
            return asSet(
                rawChanges.stream().filter(OWLOntologyChange::isAddAxiom).map(c -> c.getAxiom()));
        }
        return Collections.emptySet();
    }

    @Override
    public synchronized Set<OWLAxiom> getPendingAxiomRemovals() {
        if (!rawChanges.isEmpty()) {
            return asSet(rawChanges.stream().filter(OWLOntologyChange::isRemoveAxiom)
                .map(c -> c.getAxiom()));
        }
        return Collections.emptySet();
    }

    @Override
    public synchronized void flush() {
        // Process the changes
        if (!rawChanges.isEmpty()) {
            Set<OWLAxiom> added = new HashSet<>();
            Set<OWLAxiom> removed = new HashSet<>();
            Set<OWLAxiom> reasonerAxioms = new HashSet<>(axioms);
            for (OWLOntologyChange change : rawChanges) {
                OWLAxiom ax = change.getAxiom();
                if (change.isAddAxiom()) {
                    if (!reasonerAxioms.contains(ax)
                        && !reasonerAxioms.contains(ax.getAxiomWithoutAnnotations())) {
                        added.add(ax);
                    }
                } else if (change.isRemoveAxiom()) {
                    if (reasonerAxioms.contains(ax)
                        || reasonerAxioms.contains(ax.getAxiomWithoutAnnotations())) {
                        removed.add(change.getAxiom());
                    }
                }
            }
            added.removeAll(removed);
            rawChanges.clear();
            if (!added.isEmpty() || !removed.isEmpty()) {
                reasonerAxioms.removeAll(removed);
                reasonerAxioms.addAll(added);
                axioms.clear();
                axioms.addAll(reasonerAxioms);
                knownEntities.clear();
                axioms.forEach(ax -> add(knownEntities, ax.signature()));
                // set the consistency status to not verified
                consistencyVerified = null;
                handleChanges(added.stream(), removed.stream());
            }
        }
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
     * Asks the reasoner implementation to handle axiom additions and removals from the imports
     * closure of the root ontology. The changes will not include annotation axiom additions and
     * removals.
     * 
     * @param addAxioms The axioms to be added to the reasoner.
     * @param removeAxioms The axioms to be removed from the reasoner
     */
    private synchronized void handleChanges(Stream<OWLAxiom> addAxioms,
        Stream<OWLAxiom> removeAxioms) {
        tr.loadAxioms(addAxioms);
        removeAxioms.forEach(tr::retractAxiom);
    }

    @Override
    public String getReasonerName() {
        return "JFact";
    }

    @Override
    public Version getReasonerVersion() {
        return new Version(5, 0, 3, 0);
    }

    @Override
    public void interrupt() {
        interrupted.set(true);
    }

    // precompute inferences
    @Override
    public synchronized void precomputeInferences(InferenceType... inferenceTypes) {
        if (!kernel.isKBRealised()
            && Stream.of(inferenceTypes).anyMatch(supportedInferenceTypes::contains)) {
            kernel.realiseKB();
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
                consistencyVerified = Boolean.valueOf(kernel.isKBConsistent());
            } catch (InconsistentOntologyException e) {
                consistencyVerified = Boolean.FALSE;
            }
        }
        return consistencyVerified.booleanValue();
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
    public synchronized boolean isSatisfiable(OWLClassExpression classExpression) {
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
            AxiomAnnotations.IGNORE_AXIOM_ANNOTATIONS)) {
            return true;
        }
        try {
            return axiom.accept(tr.getEntailmentChecker()).booleanValue();
        } catch (ReasonerFreshEntityException e) {
            IRI iri = e.getIri();
            if (getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
                Optional<OWLEntity> fresh =
                    axiom.signature().filter(ent -> ent.getIRI().equals(iri)).findAny();
                if (fresh.isPresent()) {
                    throw new FreshEntitiesException(fresh.get(), e);
                }
                throw new FreshEntitiesException(asList(axiom.signature()), e);
            }
            LOGGER.warn(
                "Fresh entity exception in the reasoner for entity: '{}'; defaulting to axiom not entailed",
                iri);
            return false;
        }
    }

    @Override
    public synchronized boolean isEntailed(Set<? extends OWLAxiom> axioms) {
        return axioms.stream().allMatch(ax -> isEntailed(checkNotNull(ax)));
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
     * @param axiom axiom
     * @return tracing set (set of axioms that were participate in achieving result) for a given
     *         entailment. Return empty set if the axiom is not entailed.
     */
    public synchronized Set<OWLAxiom> getTrace(OWLAxiom axiom) {
        kernel.needTracing();
        if (this.isEntailed(axiom)) {
            return asSet(kernel.getTrace().map(AxiomWrapper::getAxiom).filter(ax -> ax != null));
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
    public synchronized NodeSet<OWLClass> getSubClasses(OWLClassExpression ce, boolean direct) {
        if (isFreshName(ce)) {
            if (configuration.getFreshEntityPolicy() == FreshEntityPolicy.DISALLOW) {
                throw new FreshEntitiesException(asList(ce.signature()));
            }
            return new OWLClassNodeSet(getBottomClassNode());
        }
        checkConsistency();
        List<Collection<ConceptExpression>> pointers =
            kernel.getConcepts(tr.pointer(ce), direct, classActor(), false).getElements();
        // XXX trick: empty sets imply the ony subclasses are the equivalent
        // to bottom.
        // should happen in the reasoner proper, but something stops that
        Optional<Collection<ConceptExpression>> empty =
            pointers.stream().filter(c -> c.isEmpty()).findAny();
        if (empty.isPresent()) {
            empty.get().addAll(bottomNode());
        } else {
            if (pointers.isEmpty()) {
                pointers.add(bottomNode());
            }
        }
        return tr.getClassExpressionTranslator().nodeSet(pointers.stream());
    }

    private Collection<ConceptExpression> bottomNode() {
        return kernel.getEquivalentConcepts(tr.pointer(df.getOWLNothing()), classActor())
            .getSynonyms();
    }

    @Override
    public synchronized NodeSet<OWLClass> getSuperClasses(OWLClassExpression ce, boolean direct) {
        if (isFreshName(ce)) {
            return new OWLClassNodeSet(getTopClassNode());
        }
        checkConsistency();
        Stream<Collection<ConceptExpression>> stream =
            askSuperClasses(tr.pointer(ce), direct).stream();
        return tr.getClassExpressionTranslator().nodeSet(stream);
    }

    @Override
    public synchronized NodeSet<OWLClass> getDisjointClasses(OWLClassExpression ce) {
        ConceptExpression p = tr.pointer(ce);
        Stream<Collection<ConceptExpression>> stream =
            kernel.getDisjointConcepts(p, classActor()).getElements().stream();
        return tr.getClassExpressionTranslator().nodeSet(stream);
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
    public synchronized NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
        OWLObjectPropertyExpression pe, boolean direct) {
        checkConsistency();
        Stream<Collection<ObjectRoleExpression>> stream =
            kernel.getRoles(tr.pointer(pe), direct, objectActor(), false).getElements().stream();
        return tr.getObjectPropertyTranslator().nodeSet(stream);
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
        OWLObjectPropertyExpression pe, boolean direct) {
        checkConsistency();
        List<Collection<ObjectRoleExpression>> elements =
            kernel.getRoles(tr.pointer(pe), direct, objectActor(), true).getElements();
        // XXX trick
        if (elements.isEmpty() || elements.get(0).isEmpty()) {
            elements.add(
                kernel.getEquivalentRoles(tr.pointer(df.getOWLTopObjectProperty()), objectActor())
                    .getSynonyms());
        }
        return tr.getObjectPropertyTranslator().nodeSet(elements.stream());
    }

    @Override
    public synchronized Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
        OWLObjectPropertyExpression pe) {
        checkConsistency();
        Stream<ObjectRoleExpression> stream =
            kernel.getEquivalentRoles(tr.pointer(pe), objectActor()).getSynonyms().stream();
        return tr.getObjectPropertyTranslator().node(stream);
    }

    @Override
    public synchronized NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
        OWLObjectPropertyExpression pe) {
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
    public synchronized NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression pe,
        boolean direct) {
        checkConsistency();
        Stream<Collection<ConceptExpression>> stream =
            kernel.getORoleDomain(tr.pointer(pe), direct, classActor()).getElements().stream();
        return tr.getClassExpressionTranslator().nodeSet(stream);
    }

    @Override
    public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression pe,
        boolean direct) {
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
    public synchronized NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty pe,
        boolean direct) {
        checkConsistency();
        Stream<Collection<DataRoleExpression>> stream =
            kernel.getRoles(tr.pointer(pe), direct, dataActor(), false).getElements().stream();
        return tr.getDataPropertyTranslator().nodeSet(stream);
    }

    @Override
    public synchronized NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe,
        boolean direct) {
        checkConsistency();
        List<Collection<DataRoleExpression>> elements =
            kernel.getRoles(tr.pointer(pe), direct, dataActor(), true).getElements();
        // XXX trick
        if (elements.isEmpty() || elements.get(0).isEmpty()) {
            elements
                .add(kernel.getEquivalentRoles(tr.pointer(df.getOWLTopDataProperty()), dataActor())
                    .getSynonyms());
        }
        return tr.getDataPropertyTranslator().nodeSet(elements.stream());
    }

    @Override
    public synchronized Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe) {
        checkConsistency();
        DataRoleExpression p = tr.pointer(pe);
        Stream<DataRoleExpression> dataPropertySynonyms =
            kernel.getEquivalentRoles(p, dataActor()).getSynonyms().stream();
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
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe, boolean direct) {
        Stream<Collection<ConceptExpression>> stream =
            kernel.getDRoleDomain(tr.pointer(pe), direct, classActor()).getElements().stream();
        return tr.getClassExpressionTranslator().nodeSet(stream);
    }

    // individuals
    @Override
    public synchronized NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct) {
        checkConsistency();
        Stream<Collection<ConceptExpression>> classElements =
            kernel.getTypes(tr.pointer(ind), direct, classActor()).getElements().stream();
        return tr.getClassExpressionTranslator().nodeSet(classElements);
    }

    /**
     * @return class actor
     */
    private TaxonomyActor<ConceptExpression> classActor() {
        return new TaxonomyActor<>(em, new ClassPolicy());
    }

    /**
     * @return object property actor
     */
    private TaxonomyActor<ObjectRoleExpression> objectActor() {
        return new TaxonomyActor<>(em, new ObjectPropertyPolicy());
    }

    /**
     * @return data property actor
     */
    private TaxonomyActor<DataRoleExpression> dataActor() {
        return new TaxonomyActor<>(em, new DataPropertyPolicy());
    }

    /**
     * @param t type
     * @param <T> type
     * @return individual property actor
     */
    private <T extends Expression> TaxonomyActor<T> individualActor(
        @SuppressWarnings("unused") Class<T> t) {
        return new TaxonomyActor<>(em, new IndividualPolicy(true));
    }

    @Override
    public synchronized NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce,
        boolean direct) {
        checkConsistency();
        TaxonomyActor<IndividualExpression> actor = kernel.getInstances(tr.pointer(ce),
            individualActor(IndividualExpression.class), direct);
        return tr.translateNodeSet(actor.getElements().iterator().next().stream());
    }

    @Override
    public synchronized NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual ind,
        OWLObjectPropertyExpression pe) {
        checkConsistency();
        List<Individual> fillers = kernel.getRoleFillers(tr.pointer(ind), tr.pointer(pe));
        return tr
            .translateNodeSet(fillers.stream().map(p -> em.individual(p.getEntity().getEntity())));
    }

    @Override
    public synchronized Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind,
        OWLDataProperty pe) {
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
    public synchronized Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind) {
        checkConsistency();
        Stream<IndividualName> stream =
            kernel.getSameAs(tr.pointer(ind), individualActor(IndividualName.class)).getSynonyms()
                .stream();
        return tr.getIndividualTranslator().node(stream);
    }

    @Override
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual ind) {
        OWLClassExpression ce = df.getOWLObjectOneOf(ind).getObjectComplementOf();
        return getInstances(ce, false);
    }

    @Override
    public synchronized void dispose() {
        root.getOWLOntologyManager().removeOntologyChangeListener(this);
        tr = null;
        kernel = null;
    }

    /**
     * @param pw pw
     * @param includeBottomNode includeBottomNode
     */
    public void dumpClassHierarchy(LogAdapter pw, boolean includeBottomNode) {
        dumpSubClasses(getTopClassNode(), pw, 0, includeBottomNode);
    }

    private void dumpSubClasses(Node<OWLClass> node, LogAdapter pw, int depth,
        boolean includeBottomNode) {
        if (includeBottomNode || !node.isBottomNode()) {
            IntStream.range(0, depth).forEach(n -> pw.print("    "));
            pw.print(node).println();
            getSubClasses(node.getRepresentativeElement(), true)
                .forEach(sub -> dumpSubClasses(sub, pw, depth + 1, includeBottomNode));
        }
    }

    private Collection<Collection<ConceptExpression>> askSuperClasses(ConceptExpression arg,
        boolean direct) {
        return kernel.getConcepts(arg, direct, classActor(), true).getElements();
    }

    /**
     * @param time time
     */
    public synchronized void writeReasoningResult(long time) {
        kernel.writeReasoningResult(time);
    }

    // owl knowledge exploration
    private class RootNodeImpl implements RootNode, Serializable {

        @Nonnull
        private final DlCompletionTree pointer;

        public RootNodeImpl(DlCompletionTree p) {
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
        return new RootNodeImpl(kernel.buildCompletionTree(tr.pointer(expression)));
    }

    @Override
    public Node<? extends OWLObjectPropertyExpression> getObjectNeighbours(RootNode object,
        boolean deterministicOnly) {
        Stream<ObjectRoleExpression> stream =
            kernel.getObjectRoles(tree(object), deterministicOnly, false).stream();
        return tr.getObjectPropertyTranslator().node(stream);
    }

    @Override
    public Node<OWLDataProperty> getDataNeighbours(RootNode object, boolean deterministicOnly) {
        Stream<DataRoleExpression> stream =
            kernel.getDataRoles(tree(object), deterministicOnly).stream();
        return tr.getDataPropertyTranslator().node(stream);
    }

    @Override
    public Collection<RootNode> getObjectNeighbours(RootNode n, OWLObjectProperty property) {
        Stream<DlCompletionTree> stream =
            kernel.getNeighbours(tree(n), tr.pointer(property)).stream();
        return asList(stream.map(t -> new RootNodeImpl(checkNotNull(t))));
    }

    @Override
    public Collection<RootNode> getDataNeighbours(RootNode n, OWLDataProperty property) {
        return asList(kernel.getNeighbours(tree(n), tr.pointer(property)).stream()
            .map(t -> new RootNodeImpl(checkNotNull(t))));
    }

    protected DlCompletionTree tree(RootNode n) {
        return (DlCompletionTree) n.getNode();
    }

    @Override
    public Node<? extends OWLClassExpression> getObjectLabel(RootNode object,
        boolean deterministicOnly) {
        Stream<ConceptExpression> stream =
            kernel.getObjectLabel(tree(object), deterministicOnly).stream();
        return tr.getClassExpressionTranslator().node(stream);
    }

    @Override
    public Node<? extends OWLDataRange> getDataLabel(RootNode object, boolean deterministicOnly) {
        Stream<DataExpression> stream =
            kernel.getDataLabel(tree(object), deterministicOnly).stream();
        return tr.getDataRangeTranslator().node(stream);
    }

    @Override
    public RootNode getBlocker(RootNode object) {
        return new RootNodeImpl(kernel.getBlocker(tree(object)));
    }

    /**
     * @param useSemantics useSemantics
     * @param type type
     * @return number of atoms
     */
    public int getAtomicDecompositionSize(boolean useSemantics, ModuleType type) {
        return kernel.getAtomicDecompositionSize(root, useSemantics, type);
    }

    /**
     * @return set of tautologies
     */
    public Set<OWLAxiom> getTautologies() {
        return asSet(kernel.getTautologies().stream());
    }

    /**
     * @param index index
     * @return set of axioms that corresponds to the atom with the id INDEX
     */
    public Set<OWLAxiom> getAtomAxioms(int index) {
        return axiomsToSet(kernel.getAtomAxioms(index).stream());
    }

    private static Set<OWLAxiom> axiomsToSet(Stream<AxiomWrapper> index) {
        return asSet(index.map(ax -> ax.getAxiom()));
    }

    /**
     * @param index index
     * @return set of axioms that corresponds to the module of the atom with the id INDEX
     */
    public Set<OWLAxiom> getAtomModule(int index) {
        return axiomsToSet(kernel.getAtomModule(index).stream());
    }

    /**
     * @param index index
     * @return set of atoms on which atom with index INDEX depends
     */
    public Set<OntologyAtom> getAtomDependents(int index) {
        return kernel.getAtomDependents(index);
    }

    /**
     * @param signature signature
     * @param useSemantic useSemantic
     * @param moduletype moduletype
     * @return set of axioms in the module
     */
    public Set<OWLAxiom> getModule(Set<OWLEntity> signature, boolean useSemantic,
        ModuleType moduletype) {
        List<Expression> list = tr.translateExpressions(signature.stream());
        Collection<AxiomWrapper> axioms = kernel.getModule(list, useSemantic, moduletype, this);
        return axiomsToSet(axioms.stream());
    }

    /**
     * @param signature signature
     * @param useSemantic useSemantic
     * @param moduletype moduletype
     * @return set of non local axioms
     */
    public Set<OWLAxiom> getNonLocal(Set<OWLEntity> signature, boolean useSemantic,
        ModuleType moduletype) {
        List<Expression> list = tr.translateExpressions(signature.stream());
        Set<AxiomWrapper> axioms = kernel.getNonLocal(list, useSemantic, moduletype, this);
        return axiomsToSet(axioms.stream());
    }

    /**
     * get all individuals from the set individuals that has r-successor and s-successor and those
     * are related via OP: r op s.
     * 
     * @param individuals set of individuals to choose from
     * @param r first operand of the comparison
     * @param s second operand of the comparison
     * @param op comparison operation: 0 means "equal", 1 means "different", 2 means "lesser", 3
     *        means "lesser than or equal", 4 means "greater than", 5 means "greater than or equal"
     * @return data related individuals
     */
    public synchronized Node<OWLNamedIndividual> getDataRelatedIndividuals(
        Stream<OWLIndividual> individuals, OWLDataProperty r, OWLDataProperty s, int op) {
        checkConsistency();
        // load all the individuals as parameters
        Stream<IndividualName> stream = kernel
            .getDataRelatedIndividuals(tr.pointer(r), tr.pointer(s), op, tr.translate(individuals))
            .stream();
        return tr.getIndividualTranslator().node(stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        root.getOWLOntologyManager().addOntologyChangeListener(this);
    }
}
