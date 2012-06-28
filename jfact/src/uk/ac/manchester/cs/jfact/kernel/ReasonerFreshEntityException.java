package uk.ac.manchester.cs.jfact.kernel;

import org.semanticweb.owlapi.reasoner.OWLReasonerRuntimeException;

public class ReasonerFreshEntityException extends OWLReasonerRuntimeException {
    private final String iri;

    public ReasonerFreshEntityException(final String iri) {
        this.iri = iri;
    }

    public ReasonerFreshEntityException(final String s, final String iri) {
        super(s);
        this.iri = iri;
    }

    public ReasonerFreshEntityException(final String s, final Throwable t,
            final String iri) {
        super(s, t);
        this.iri = iri;
    }

    public ReasonerFreshEntityException(final Throwable t, final String iri) {
        super(t);
        this.iri = iri;
    }

    public String getIri() {
        return iri;
    }
}
