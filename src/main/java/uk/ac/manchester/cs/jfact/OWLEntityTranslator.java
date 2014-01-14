package uk.ac.manchester.cs.jfact;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;

import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Entity;

abstract class OWLEntityTranslator<E extends OWLObject, T extends Entity> implements
        Serializable {
    private static final long serialVersionUID = 11000L;
    private final Map<E, T> entity2dlentity = new HashMap<E, T>();
    private final Map<T, E> dlentity2entity = new HashMap<T, E>();
    protected final ExpressionManager em;
    protected final OWLDataFactory df;
    protected final TranslationMachinery tr;

    protected void fillMaps(E entity, T dlentity) {
        this.entity2dlentity.put(entity, dlentity);
        this.dlentity2entity.put(dlentity, entity);
    }

    protected OWLEntityTranslator(ExpressionManager em, OWLDataFactory df,
            TranslationMachinery tr) {
        this.em = em;
        this.df = df;
        this.tr = tr;
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

    public Node<E> node(Collection<T> pointers) {
        DefaultNode<E> node = this.createDefaultNode();
        for (T pointer : pointers) {
            node.add(this.getEntityFromPointer(pointer));
        }
        return node;
    }

    public NodeSet<E> nodeSet(Collection<Collection<T>> pointers) {
        DefaultNodeSet<E> nodeSet = this.createDefaultNodeSet();
        for (Collection<T> pointerArray : pointers) {
            nodeSet.addNode(this.node(pointerArray));
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
