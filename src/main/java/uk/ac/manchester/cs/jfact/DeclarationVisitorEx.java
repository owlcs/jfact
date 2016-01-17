package uk.ac.manchester.cs.jfact;

import java.io.Serializable;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapitools.decomposition.AxiomWrapper;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.AxiomDeclaration;
import uk.ac.manchester.cs.jfact.kernel.dl.axioms.Axioms;

/** declaration translator */
public class DeclarationVisitorEx implements OWLEntityVisitorEx<AxiomWrapper>, Serializable {

    private Ontology o;
    private OWLDataFactory df;
    private TranslationMachinery tr;

    /**
     * @param o
     *        o
     * @param df
     *        df
     * @param tr
     *        tr
     */
    public DeclarationVisitorEx(Ontology o, OWLDataFactory df, TranslationMachinery tr) {
        this.o = o;
        this.df = df;
        this.tr = tr;
    }

    private OWLDeclarationAxiom d(OWLEntity e) {
        return df.getOWLDeclarationAxiom(e);
    }

    @Override
    public AxiomWrapper visit(OWLClass cls) {
        return o.add(new AxiomDeclaration(d(cls), tr.pointer(cls)));
    }

    @Override
    public AxiomWrapper visit(OWLObjectProperty property) {
        return o.add(new AxiomDeclaration(d(property), tr.pointer(property)));
    }

    @Override
    public AxiomWrapper visit(OWLDataProperty property) {
        return o.add(new AxiomDeclaration(d(property), tr.pointer(property)));
    }

    @Override
    public AxiomWrapper visit(OWLNamedIndividual individual) {
        return o.add(new AxiomDeclaration(d(individual), tr.pointer(individual)));
    }

    @Override
    public AxiomWrapper visit(OWLDatatype datatype) {
        return o.add(new AxiomDeclaration(d(datatype), tr.pointer(datatype)));
    }

    @Override
    public AxiomWrapper visit(OWLAnnotationProperty property) {
        return Axioms.dummy();
    }
}
