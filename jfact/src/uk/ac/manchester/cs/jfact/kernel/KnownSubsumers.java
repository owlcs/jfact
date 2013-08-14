package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;
import java.util.List;

/** abstract class to represent the known subsumers of a concept */
public abstract class KnownSubsumers implements Serializable {
    private static final long serialVersionUID = 11000L;

    /** begin of the Sure subsumers interval */
    public abstract List<ClassifiableEntry> s_begin();

    /** begin of the Possible subsumers interval */
    public abstract List<ClassifiableEntry> p_begin();

    // flags
    /** whether there are no sure subsumers */
    public boolean s_empty() {
        return s_begin().isEmpty();
    }

    /** whether there are no possible subsumers */
    public boolean p_empty() {
        return p_begin().isEmpty();
    }

    /** @return true iff CE is the possible subsumer */
    public boolean isPossibleSub(@SuppressWarnings("unused") ClassifiableEntry ce) {
        return true;
    }
}
