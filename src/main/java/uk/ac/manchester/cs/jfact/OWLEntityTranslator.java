package uk.ac.manchester.cs.jfact;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;

import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Entity;

abstract class OWLEntityTranslator<E extends OWLObject, T extends Entity>
        implements Serializable {

    private static final long serialVersionUID = 11000L;
    private final Map<E, T> entity2dlentity = new HashMap<>();
    private final Map<T, E> dlentity2entity = new HashMap<>();
    protected final ExpressionCache em;
    protected final OWLDataFactory df;
    protected final TranslationMachinery tr;

    protected void fillMaps(E entity, T dlentity) {
        this.entity2dlentity.put(entity, dlentity);
        this.dlentity2entity.put(dlentity, entity);
    }

    protected OWLEntityTranslator(ExpressionCache em, OWLDataFactory df,
            TranslationMachinery tr) {
        this.em = em;
        this.df = df;
        this.tr = tr;
        fillTopAndBottomEntities();
    }

    protected void fillTopAndBottomEntities() {
        E topEntity = this.getTopEntity();
        if (topEntity != null) {
            this.fillMaps(topEntity, this.getTopEntityPointer());
        }
        E bottomEntity = this.getBottomEntity();
        if (bottomEntity != null) {
            this.fillMaps(bottomEntity, this.getBottomEntityPointer());
        }
    }

    @Nonnull
    protected T registerNewEntity(E entity) {
        T pointer = this.createPointerForEntity(entity);
        this.fillMaps(entity, pointer);
        return pointer;
    }

    public E getEntityFromPointer(T pointer) {
        return this.dlentity2entity.get(pointer);
    }

    @Nonnull
    public T getPointerFromEntity(E entity) {
        T pointer = this.entity2dlentity.get(entity);
        if (pointer == null) {
            pointer = this.registerNewEntity(entity);
        }
        return pointer;
    }

    @Nonnull
    public Node<E> node(Stream<T> pointers) {
        return createDefaultNode(pointers.map(p -> getEntityFromPointer(p)));
    }

    @Nonnull
    public NodeSet<E> nodeSet(Stream<Collection<T>> pointers) {
        return createDefaultNodeSet(pointers.map(p -> node(p.stream())));
    }

    @Nonnull
    protected abstract DefaultNode<E> createDefaultNode(
            @Nonnull Stream<E> stream);

    @Nonnull
    protected abstract DefaultNodeSet<E> createDefaultNodeSet(
            @Nonnull Stream<Node<E>> stream);

    protected abstract T getTopEntityPointer();

    protected abstract T getBottomEntityPointer();

    @Nonnull
    protected abstract T createPointerForEntity(E entity);

    protected abstract E getTopEntity();

    protected abstract E getBottomEntity();
}
