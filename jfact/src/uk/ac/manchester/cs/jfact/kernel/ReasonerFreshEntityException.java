package uk.ac.manchester.cs.jfact.kernel;

import org.semanticweb.owlapi.reasoner.OWLReasonerRuntimeException;

@SuppressWarnings("javadoc")
public class ReasonerFreshEntityException extends OWLReasonerRuntimeException {
    private String iri;

    public ReasonerFreshEntityException(String iri) {
        this.iri = iri;
    }

    public ReasonerFreshEntityException(String s, String iri) {
        super(s);
        this.iri = iri;
    }

    public ReasonerFreshEntityException(String s, Throwable t, String iri) {
        super(s, t);
        this.iri = iri;
    }

    public ReasonerFreshEntityException(Throwable t, String iri) {
        super(t);
        this.iri = iri;
    }

    public String getIri() {
        return iri;
    }
}
