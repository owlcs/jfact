package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.PortedFrom;

/** modularizer */
@PortedFrom(file = "OntologyBasedModularizer.h", name = "OntologyBasedModularizer")
public class OntologyBasedModularizer implements Serializable {

    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "Ontology")
    private final Ontology ontology;
    private final TModularizer Modularizer;

    /**
     * @param ontology
     *        ontology
     * @param mod
     *        mod
     */
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "OntologyBasedModularizer")
    public OntologyBasedModularizer(Ontology ontology, TModularizer mod) {
        this.ontology = ontology;
        Modularizer = mod;
    }

    /**
     * @param useSemantic
     *        useSemantic
     * @param kernel
     *        kernel
     * @return modularizer
     */
    public static TModularizer buildTModularizer(boolean useSemantic,
            ReasoningKernel kernel) {
        TModularizer Mod = null;
        if (useSemantic) {
            Mod = new TModularizer(kernel.getOptions(),
                    new SemanticLocalityChecker(kernel));
            Mod.preprocessOntology(kernel.getOntology().getAxioms());
        } else {
            Mod = new TModularizer(kernel.getOptions(),
                    new SyntacticLocalityChecker());
            Mod.preprocessOntology(kernel.getOntology().getAxioms());
        }
        return Mod;
    }

    /**
     * @param sig
     *        sig
     * @param type
     *        type
     * @return module
     */
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "getModule")
    public List<AxiomInterface> getModule(TSignature sig, ModuleType type) {
        return getModule(ontology.getAxioms(), sig, type);
    }

    /**
     * @param axioms
     *        axioms
     * @param sig
     *        sig
     * @param type
     *        type
     * @return module
     */
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "getModule")
    public List<AxiomInterface> getModule(List<AxiomInterface> axioms,
            TSignature sig, ModuleType type) {
        Modularizer.extract(axioms, sig, type);
        return Modularizer.getModule();
    }

    /** @return modularizer */
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "getModularizer")
    public TModularizer getModularizer() {
        return Modularizer;
    }
}
