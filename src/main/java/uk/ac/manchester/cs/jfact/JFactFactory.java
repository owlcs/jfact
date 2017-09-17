package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

/** JFact factory. */
public class JFactFactory implements OWLReasonerFactory, Serializable {

    private static final long serialVersionUID = 11000L;

    @Override
    public String getReasonerName() {
        return "JFact";
    }

    @Override
    public JFactReasoner createReasoner(OWLOntology ontology) {
        JFactReasoner toReturn =
            new JFactReasoner(ontology, new JFactReasonerConfiguration(), BufferingMode.BUFFERING);
        return verify(toReturn);
    }

    @Nonnull
    private static JFactReasoner verify(JFactReasoner toReturn) {
        OWLOntologyManager m = toReturn.getRootOntology().getOWLOntologyManager();
        m.addOntologyChangeListener(toReturn);
        // toReturn.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        // toReturn.kernel.writeReasoningResult(0);
        return toReturn;
    }

    @Override
    public JFactReasoner createNonBufferingReasoner(OWLOntology ontology) {
        JFactReasoner toReturn = new JFactReasoner(ontology, new JFactReasonerConfiguration(),
            BufferingMode.NON_BUFFERING);
        return verify(toReturn);
    }

    @Override
    public JFactReasoner createReasoner(OWLOntology ontology, OWLReasonerConfiguration config)
        throws IllegalConfigurationException {
        JFactReasoner toReturn = new JFactReasoner(ontology, config, BufferingMode.BUFFERING);
        return verify(toReturn);
    }

    @Override
    public JFactReasoner createNonBufferingReasoner(OWLOntology ontology,
        OWLReasonerConfiguration config) throws IllegalConfigurationException {
        JFactReasoner toReturn = new JFactReasoner(ontology, config, BufferingMode.NON_BUFFERING);
        return verify(toReturn);
    }
}
