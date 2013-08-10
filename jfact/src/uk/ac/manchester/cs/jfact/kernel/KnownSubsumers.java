package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.List;

/** abstract class to represent the known subsumers of a concept */
public abstract class KnownSubsumers implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** begin of the Sure subsumers interval */
    abstract List<ClassifiableEntry> s_begin();

    /** begin of the Possible subsumers interval */
    abstract List<ClassifiableEntry> p_begin();

    // flags
    /** whether there are no sure subsumers */
    boolean s_empty() {
        return s_begin().isEmpty();
    }

    /** whether there are no possible subsumers */
    boolean p_empty() {
        return p_begin().isEmpty();
    }

    /** @return true iff CE is the possible subsumer */
    boolean isPossibleSub(ClassifiableEntry ce) {
        return true;
    }
}
