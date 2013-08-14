package uk.ac.manchester.cs.jfact.kernel.unused;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.KnownSubsumers;

/** all the derived subsumers of a class (came from the model) */
public class DerivedSubsumers extends KnownSubsumers {
    private static final long serialVersionUID = 11000L;
    /** set of sure- and possible subsumers */
    protected final List<ClassifiableEntry> Sure, Possible;

    /** c'tor: copy given sets
     * 
     * @param sure
     * @param possible */
    public DerivedSubsumers(List<ClassifiableEntry> sure, List<ClassifiableEntry> possible) {
        Sure = new ArrayList<ClassifiableEntry>(sure);
        Possible = new ArrayList<ClassifiableEntry>(possible);
    }

    /** begin of the Sure subsumers interval */
    @Override
    public List<ClassifiableEntry> s_begin() {
        return Sure;
    }

    /** begin of the Possible subsumers interval */
    @Override
    public List<ClassifiableEntry> p_begin() {
        return Possible;
    }
}
