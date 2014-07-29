package uk.ac.manchester.cs.jfact;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        E topEntity = this.getTopEntity();
        this.fillMaps(topEntity, this.getTopEntityPointer());
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
    public Node<E> node(Collection<T> pointers) {
        DefaultNode<E> node = this.createDefaultNode();
        for (T pointer : pointers) {
            node.add(this.getEntityFromPointer(pointer));
        }
        return node;
    }

    @Nonnull
    public NodeSet<E> nodeSet(Collection<Collection<T>> pointers) {
        DefaultNodeSet<E> nodeSet = this.createDefaultNodeSet();
        for (Collection<T> pointerArray : pointers) {
            nodeSet.addNode(this.node(pointerArray));
        }
        return nodeSet;
    }

    @Nonnull
    protected abstract DefaultNode<E> createDefaultNode();

    @Nonnull
    protected abstract DefaultNodeSet<E> createDefaultNodeSet();

    @Nonnull
    protected abstract T getTopEntityPointer();

    @Nonnull
    protected abstract T getBottomEntityPointer();

    @Nonnull
    protected abstract T createPointerForEntity(E entity);

    @Nonnull
    protected abstract E getTopEntity();

    protected abstract E getBottomEntity();
}
