package uk.ac.manchester.cs.jfact.split;

import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.Ontology;
import uk.ac.manchester.cs.jfact.kernel.ReasoningKernel;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;
import conformance.PortedFrom;

@PortedFrom(file = "OntologyBasedModularizer.h", name = "OntologyBasedModularizer")
public class OntologyBasedModularizer {
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "Ontology")
    private Ontology ontology;
    private TModularizer Modularizer;

    @PortedFrom(file = "OntologyBasedModularizer.h", name = "OntologyBasedModularizer")
    public OntologyBasedModularizer(Ontology ontology, TModularizer mod) {
        this.ontology = ontology;
        Modularizer = mod;
    }

    public static TModularizer buildTModularizer(boolean useSemantic,
            ReasoningKernel kernel) {
        TModularizer Mod = null;
        if (useSemantic) {
            Mod = new TModularizer(kernel.getOptions(), new SemanticLocalityChecker(
                    kernel));
            Mod.preprocessOntology(kernel.getOntology().getAxioms());
        } else {
            Mod = new TModularizer(kernel.getOptions(), new SyntacticLocalityChecker());
            Mod.preprocessOntology(kernel.getOntology().getAxioms());
        }
        return Mod;
    }

    /** @return module */
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "getModule")
    public List<AxiomInterface> getModule(TSignature sig, ModuleType type) {
        Modularizer.extract(ontology.getAxioms(), sig, type);
        return Modularizer.getModule();
    }

    /** @return modularizer */
    @PortedFrom(file = "OntologyBasedModularizer.h", name = "getModularizer")
    public TModularizer getModularizer() {
        return Modularizer;
    }
}
