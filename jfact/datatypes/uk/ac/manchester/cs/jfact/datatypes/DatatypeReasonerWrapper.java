package uk.ac.manchester.cs.jfact.datatypes;

import java.util.Arrays;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.kernel.DagTag;
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.options.JFactReasonerConfiguration;

public class DatatypeReasonerWrapper {
    DataTypeReasoner delegate;

    public DatatypeReasonerWrapper(final JFactReasonerConfiguration o) {
        delegate = new DataTypeReasoner(o);
        System.out.println("\nDatatypeReasonerWrapper " + delegate.hashCode());
    }

    public boolean addDataEntry(boolean positive, DagTag type, NamedEntry dataEntry,
            DepSet dep) {
        boolean toReturn = delegate.addDataEntry(positive, type, dataEntry, dep);
        print(toReturn, "addDataEntry()", positive, type.toString(), dataEntry, dep);
        return toReturn;
    }

    private void print(Object toReturn, String methodName, Object... params) {
        System.out.print("\t" + methodName + "\t" + toReturn);
        if (params.length > 0) {
            System.out.println("\tparams:\t" + Arrays.toString(params));
        }
    }

    public DepSet getClashSet() {
        DepSet toReturn = delegate.getClashSet();
        print(toReturn, "getClashSet()");
        return toReturn;
    }

    public boolean checkClash() {
        boolean toReturn = delegate.checkClash();
        print(toReturn, "checkClash()");
        return toReturn;
    }
}
