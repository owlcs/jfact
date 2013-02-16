package uk.ac.manchester.cs.jfact.split;

import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.dl.ConceptName;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.AxiomInterface;

@SuppressWarnings("javadoc")
public class SplitVarEntry {
    // entry name
    public ConceptName name;
    // internal name
    public Concept concept;
    public TSignature sig;
    public Set<AxiomInterface> Module;
}
