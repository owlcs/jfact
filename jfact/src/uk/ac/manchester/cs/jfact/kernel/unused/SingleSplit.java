package uk.ac.manchester.cs.jfact.kernel.unused;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

/** class to check whether there is a need to unsplit splitted var */
@SuppressWarnings("unused")
public class SingleSplit implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** signature of equivalent part of the split */
    private final Set<NamedEntity> eqSig;
    /** signature of subsumption part of the split */
    private final Set<NamedEntity> impSig;
    /** pointer to split vertex to activate */
    private final int bp;

    protected SingleSplit(Set<NamedEntity> es, Set<NamedEntity> is, int p) {
        eqSig = new HashSet<NamedEntity>(es);
        impSig = new HashSet<NamedEntity>(is);
        bp = p;
    }
}
