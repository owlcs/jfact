package uk.ac.manchester.cs.jfact;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

/** JFact factory. */
public class JFactFactory implements OWLReasonerFactory, Serializable {

    @Override
    public String getReasonerName() {
        return "JFact";
    }

    @Override
    public OWLReasoner createReasoner(OWLOntology ontology) {
        JFactReasoner toReturn = new JFactReasoner(ontology, new SimpleConfiguration(), BufferingMode.BUFFERING);
        return verify(toReturn);
    }

    private static OWLReasoner verify(JFactReasoner toReturn) {
        OWLOntologyManager m = toReturn.getRootOntology().getOWLOntologyManager();
        m.addOntologyChangeListener(toReturn);
        return toReturn;
    }

    @Override
    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
        JFactReasoner toReturn = new JFactReasoner(ontology, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
        return verify(toReturn);
    }

    @Override
    public OWLReasoner createReasoner(OWLOntology ontology, OWLReasonerConfiguration config) {
        JFactReasoner toReturn = new JFactReasoner(ontology, config, BufferingMode.BUFFERING);
        return verify(toReturn);
    }

    @Override
    public OWLReasoner createNonBufferingReasoner(OWLOntology ontology, OWLReasonerConfiguration config) {
        JFactReasoner toReturn = new JFactReasoner(ontology, config, BufferingMode.NON_BUFFERING);
        return verify(toReturn);
    }
}
