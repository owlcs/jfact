package uk.ac.manchester.cs.jfact;

import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;

/** declaration translator */
public class DeclarationVisitorEx implements OWLEntityVisitorEx<AxiomInterface>,
        Serializable {
    private static final long serialVersionUID = 11000L;
    private Ontology o;
    private OWLDataFactory df;
    private TranslationMachinery tr;

    /** @param o
     *            o
     * @param df
     *            df
     * @param tr
     *            tr */
    public DeclarationVisitorEx(Ontology o, OWLDataFactory df, TranslationMachinery tr) {
        this.o = o;
        this.df = df;
        this.tr = tr;
    }

    private OWLDeclarationAxiom d(OWLEntity e) {
        return df.getOWLDeclarationAxiom(e);
    }

    @Override
    public AxiomInterface visit(OWLClass cls) {
        return o.add(new AxiomDeclaration(d(cls), tr.pointer(cls)));
    }

    @Override
    public AxiomInterface visit(OWLObjectProperty property) {
        return o.add(new AxiomDeclaration(d(property), tr.pointer(property)));
    }

    @Override
    public AxiomInterface visit(OWLDataProperty property) {
        return o.add(new AxiomDeclaration(d(property), tr.pointer(property)));
    }

    @Override
    public AxiomInterface visit(OWLNamedIndividual individual) {
        return o.add(new AxiomDeclaration(d(individual), tr.pointer(individual)));
    }

    @Override
    public AxiomInterface visit(OWLDatatype datatype) {
        return o.add(new AxiomDeclaration(d(datatype), tr.pointer(datatype)));
    }

    @Override
    public AxiomInterface visit(OWLAnnotationProperty property) {
        return null;
    }
}
