package uk.ac.manchester.cs.jfact.kernel;

import org.semanticweb.owlapi.model.IRI;
/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.reasoner.OWLReasonerRuntimeException;

/** The Class ReasonerFreshEntityException. */
public class ReasonerFreshEntityException extends OWLReasonerRuntimeException {


    /** The iri. */
    private final IRI iri;

    /**
     * Instantiates a new reasoner fresh entity exception.
     * 
     * @param iri
     *        the iri
     */
    public ReasonerFreshEntityException(IRI iri) {
        this.iri = iri;
    }

    /**
     * Instantiates a new reasoner fresh entity exception.
     * 
     * @param s
     *        the s
     * @param iri
     *        the iri
     */
    public ReasonerFreshEntityException(String s, IRI iri) {
        super(s);
        this.iri = iri;
    }

    /**
     * Instantiates a new reasoner fresh entity exception.
     * 
     * @param s
     *        the s
     * @param t
     *        the t
     * @param iri
     *        the iri
     */
    public ReasonerFreshEntityException(String s, Throwable t, IRI iri) {
        super(s, t);
        this.iri = iri;
    }

    /**
     * Instantiates a new reasoner fresh entity exception.
     * 
     * @param t
     *        the t
     * @param iri
     *        the iri
     */
    public ReasonerFreshEntityException(Throwable t, IRI iri) {
        super(t);
        this.iri = iri;
    }

    /** @return the iri */
    public IRI getIri() {
        return iri;
    }
}
