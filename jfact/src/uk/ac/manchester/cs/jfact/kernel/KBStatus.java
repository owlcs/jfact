package uk.ac.manchester.cs.jfact.kernel;

/** KB status */
public enum KBStatus {
    /** no axioms loaded yet; not used in TBox */
    kbEmpty,
    /** axioms are added to the KB, no preprocessing done */
    kbLoading,
    /** KB is preprocessed and consistency checked */
    kbCChecked,
    /** KB is classified */
    kbClassified,
    /** KB is realised */
    kbRealised
}
