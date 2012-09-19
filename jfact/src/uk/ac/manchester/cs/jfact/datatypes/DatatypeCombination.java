package uk.ac.manchester.cs.jfact.datatypes;

public interface DatatypeCombination<Type, Element> {
    Type add(Element d);

    boolean isCompatible(Literal<?> l);

    String getDatatypeURI();

    boolean isCompatible(Datatype<?> type);

    Iterable<Element> getList();

    boolean emptyValueSpace();

    Datatype<?> getHost();
}
