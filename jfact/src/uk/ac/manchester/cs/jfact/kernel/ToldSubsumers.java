package uk.ac.manchester.cs.jfact.kernel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** class to represent the TS's */
public class ToldSubsumers extends KnownSubsumers {
    private static final long serialVersionUID = 11000L;
    /** two iterators for the TS of a concept */
    private final List<ClassifiableEntry> beg;

    public ToldSubsumers(Collection<ClassifiableEntry> b) {
        beg = new ArrayList<ClassifiableEntry>(b);
    }

    /** begin of the Sure subsumers interval */
    @Override
    public List<ClassifiableEntry> s_begin() {
        return beg;
    }

    /** end of the Sure subsumers interval */
    /** begin of the Possible subsumers interval */
    @Override
    public List<ClassifiableEntry> p_begin() {
        return Collections.emptyList();
    }
}
