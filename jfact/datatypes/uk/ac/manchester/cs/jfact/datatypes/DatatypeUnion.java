package uk.ac.manchester.cs.jfact.datatypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DatatypeUnion implements DatatypeCombination<DatatypeUnion, Datatype<?>> {
    private final Set<Datatype<?>> basics = new HashSet<Datatype<?>>();
    private final String uri;
    private final Datatype<?> host;

    public DatatypeUnion(Datatype<?> host) {
        uri = "union#a" + DatatypeFactory.getIndex();
        this.host = host;
    }

    public Datatype<?> getHost() {
        return host;
    }

    public DatatypeUnion(Datatype<?> host, Collection<Datatype<?>> list) {
        this(host);
        basics.addAll(list);
    }

    public Iterable<Datatype<?>> getList() {
        return basics;
    }

    public DatatypeUnion add(Datatype<?> d) {
        DatatypeUnion toReturn = new DatatypeUnion(host, basics);
        toReturn.basics.add(d);
        return toReturn;
    }

    public boolean isCompatible(Literal<?> l) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(l)) {
            return false;
        }
        for (Datatype<?> d : basics) {
            if (d.isCompatible(l)) {
                return true;
            }
        }
        return false;
    }

    public String getDatatypeURI() {
        return uri;
    }

    public boolean isCompatible(Datatype<?> type) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(type)) {
            return false;
        }
        for (Datatype<?> d : basics) {
            if (d.isCompatible(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean emptyValueSpace() {
        for (Datatype<?> d : basics) {
            if (!d.isExpression()) {
                return false;
            }
            if (!d.asExpression().emptyValueSpace()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return uri + "{" + basics + "}";
    }
}
