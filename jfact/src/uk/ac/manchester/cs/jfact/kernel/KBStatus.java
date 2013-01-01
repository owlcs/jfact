package uk.ac.manchester.cs.jfact.kernel;

public enum KBStatus {
    kbEmpty, // no axioms loaded yet; not used in TBox
    kbLoading, // axioms are added to the KB, no preprocessing done
    kbCChecked, // KB is preprocessed and consistency checked
    kbClassified, // KB is classified
    kbRealised
    // KB is realised
}
