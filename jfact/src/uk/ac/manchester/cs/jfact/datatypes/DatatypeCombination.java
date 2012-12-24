package uk.ac.manchester.cs.jfact.datatypes;

/** @author ignazio
 * @param <Type>
 * @param <Element> */
public interface DatatypeCombination<Type, Element> {
    /** @param d
     * @return the type */
    Type add(Element d);

    /** @param l
     * @return true if the literal is compatible */
    boolean isCompatible(Literal<?> l);

    /** @return the datatype uri */
    String getDatatypeURI();

    /** @param type
     * @return true if the datatype is compatible */
    boolean isCompatible(Datatype<?> type);

    /** @return list of elements */
    Iterable<Element> getList();

    /** @return true if the value space is empty */
    boolean emptyValueSpace();

    /** @return the host datatype */
    Datatype<?> getHost();
}
