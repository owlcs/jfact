package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.*;

/** JFact factory */
public class JFactFactory implements OWLReasonerFactory {
    @Override
    public String getReasonerName() {
        return "JFact";
    }

    @Override
    public OWLReasoner createReasoner(OWLOntology ontology) {
        JFactReasoner toReturn = new JFactReasoner(ontology, new SimpleConfiguration(),
                BufferingMode.BUFFERING);
        return verify(toReturn);
    }

    private OWLReasoner verify(JFactReasoner toReturn) {
        OWLOntologyManager m = toReturn.getRootOntology().getOWLOntologyManager();
        m.addOntologyChangeListener(toReturn);
        // toReturn.kernel.writeReasoningResult(new
        // LeveLogger.LogAdapterStream(), 0);
        return toReturn;
    }

    @Override
    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
        JFactReasoner toReturn = new JFactReasoner(ontology, new SimpleConfiguration(),
                BufferingMode.NON_BUFFERING);
        return verify(toReturn);
    }

    @Override
    public OWLReasoner createReasoner(OWLOntology ontology,
            OWLReasonerConfiguration config) throws IllegalConfigurationException {
        JFactReasoner toReturn = new JFactReasoner(ontology, config,
                BufferingMode.BUFFERING);
        return verify(toReturn);
    }

    @Override
    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology,
            OWLReasonerConfiguration config) throws IllegalConfigurationException {
        JFactReasoner toReturn = new JFactReasoner(ontology, config,
                BufferingMode.NON_BUFFERING);
        return verify(toReturn);
    }
}
