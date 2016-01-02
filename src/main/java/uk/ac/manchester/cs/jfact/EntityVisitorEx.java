package uk.ac.manchester.cs.jfact;

import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.jfact.kernel.dl.axioms.Axioms;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;

class EntityVisitorEx implements OWLEntityVisitorEx<Expression>, Serializable {


    private TranslationMachinery tr;

    public EntityVisitorEx(TranslationMachinery em) {
        tr = em;
    }

    @Override
    public Expression visit(OWLClass cls) {
        return tr.pointer(cls);
    }

    @Override
    public Expression visit(OWLObjectProperty property) {
        return tr.pointer(property);
    }

    @Override
    public Expression visit(OWLDataProperty property) {
        return tr.pointer(property);
    }

    @Override
    public Expression visit(OWLNamedIndividual individual) {
        return tr.pointer(individual);
    }

    @Override
    public Expression visit(OWLDatatype datatype) {
        return Axioms.dummyExpression();
    }

    @Override
    public Expression visit(OWLAnnotationProperty property) {
        return Axioms.dummyExpression();
    }
}
