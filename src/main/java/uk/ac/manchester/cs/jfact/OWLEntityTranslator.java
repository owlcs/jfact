package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.verifyNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;

import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Entity;

abstract class OWLEntityTranslator<E extends OWLObject, T extends Entity> implements Serializable {

    private final Map<E, T> entity2dlentity = new HashMap<>();
    private final Map<T, E> dlentity2entity = new HashMap<>();
    protected final ExpressionCache em;
    protected final OWLDataFactory df;
    protected final TranslationMachinery tr;

    protected OWLEntityTranslator(ExpressionCache em, OWLDataFactory df, TranslationMachinery tr) {
        this.em = em;
        this.df = df;
        this.tr = tr;
        fillTopAndBottomEntities();
    }

    protected void fillMaps(E entity, T dlentity) {
        this.entity2dlentity.put(entity, dlentity);
        this.dlentity2entity.put(dlentity, entity);
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

    protected T registerNewEntity(E entity) {
        T pointer = this.createPointerForEntity(entity);
        this.fillMaps(entity, pointer);
        return pointer;
    }

    public E getEntityFromPointer(T pointer) {
        return verifyNotNull(this.dlentity2entity.get(pointer), pointer+" does not have a correct reverse mapping");
    }

    public T getPointerFromEntity(E entity) {
        T pointer = this.entity2dlentity.get(entity);
        if (pointer == null) {
            pointer = this.registerNewEntity(entity);
        }
        return pointer;
    }

    public Node<E> node(Stream<T> pointers) {
        return createDefaultNode(pointers.map(this::getEntityFromPointer));
    }

    public NodeSet<E> nodeSet(Stream<Collection<T>> pointers) {
        return createDefaultNodeSet(pointers.map(p -> node(p.stream())));
    }

    protected abstract DefaultNode<E> createDefaultNode(Stream<E> stream);

    protected abstract DefaultNodeSet<E> createDefaultNodeSet(Stream<Node<E>> stream);

    @Nullable
    protected abstract T getTopEntityPointer();

    @Nullable
    protected abstract T getBottomEntityPointer();

    protected abstract T createPointerForEntity(E entity);

    @Nullable
    protected abstract E getTopEntity();

    @Nullable
    protected abstract E getBottomEntity();
}
