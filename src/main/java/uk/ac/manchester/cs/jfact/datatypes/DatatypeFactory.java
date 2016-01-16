package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.COLLAPSE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conformance.Original;

/**
 * Factory for datatypes
 * 
 * @author ignazio
 */
@Original
public class DatatypeFactory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatatypeFactory.class);
    //@formatter:off
     static final String NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
    @SuppressWarnings("rawtypes")
    protected static final Comparable NUMBER_EXPRESSION = "[\\-+]?[0-9]+";
    @SuppressWarnings("rawtypes")
    @Nonnull protected static final Comparable WHITESPACE = COLLAPSE;
    @Nonnull protected static final Facet[] MINMAX = new Facet[] { maxInclusive, maxExclusive, minInclusive, minExclusive };
    @Nonnull protected static final Facet[] PEW = new Facet[] { pattern, enumeration, whiteSpace };
    @Nonnull protected static final Facet[] LEN = new Facet[] { length, minLength, maxLength };
    @Nonnull protected static final Facet[] DIGS = new Facet[] { totalDigits, fractionDigits };
    @Nonnull protected static final Set<Facet> STRINGFACETS = Utils.getFacets(PEW, LEN);
    @Nonnull protected static final Set<Facet> FACETS4 = Utils.getFacets(PEW, MINMAX);

    /**  LITERAL            */  @Nonnull  public static final Datatype<String>                LITERAL             = new LITERALDatatype();
    /**  ANYURI             */  @Nonnull  public static final Datatype<String>                ANYURI              = new ANYURIDatatype();
    /**  BASE64BINARY       */  @Nonnull  public static final Datatype<String>                BASE64BINARY        = new BASE64BINARYDatatype();
    /**  BOOLEAN            */  @Nonnull  public static final Datatype<Boolean>               BOOLEAN             = new BOOLEANDatatype();
    /**  DATETIME           */  @Nonnull  public static final Datatype<Date>                  DATETIME            = new DATETIMEDatatype();
    /**  HEXBINARY          */  @Nonnull  public static final Datatype<String>                HEXBINARY           = new HEXBINARYDatatype();
    /**  PLAINLITERAL       */  @Nonnull  public static final Datatype<String>                PLAINLITERAL        = new PLAINLITERALDatatype();
    /**  STRING             */  @Nonnull  public static final Datatype<String>                STRING              = new STRINGDatatype();
    /**  REAL               */  @Nonnull  public static final NumericDatatype<BigDecimal>     REAL                = new REALDatatype<>();
    /**  RATIONAL           */  @Nonnull  public static final NumericDatatype<BigDecimal>     RATIONAL            = new RATIONALDatatype<>();
    /**  DATETIMESTAMP      */  @Nonnull  public static final Datatype<Date>                  DATETIMESTAMP       = new DATETIMESTAMPDatatype();
    /**  DECIMAL            */  @Nonnull  public static final NumericDatatype<BigDecimal>     DECIMAL             = new DECIMALDatatype<>();
    /**  INTEGER            */  @Nonnull  public static final NumericDatatype<BigInteger>     INTEGER             = new INTEGERDatatype<>();
    /**  DOUBLE             */  @Nonnull  public static final NumericDatatype<Double>         DOUBLE              = new DOUBLEDatatype();
    /**  FLOAT              */  @Nonnull  public static final NumericDatatype<Float>          FLOAT               = new FLOATDatatype();
    /**  NONPOSITIVEINTEGER */  @Nonnull  public static final NumericDatatype<BigInteger>     NONPOSITIVEINTEGER  = new NONPOSITIVEINTEGERDatatype<>();
    /**  NEGATIVEINTEGER    */  @Nonnull  public static final NumericDatatype<BigInteger>     NEGATIVEINTEGER     = new NEGATIVEINTEGERDatatype<>();
    /**  NONNEGATIVEINTEGER */  @Nonnull  public static final NumericDatatype<BigInteger>     NONNEGATIVEINTEGER  = new NONNEGATIVEINTEGERDatatype<>();
    /**  POSITIVEINTEGER    */  @Nonnull  public static final NumericDatatype<BigInteger>     POSITIVEINTEGER     = new POSITIVEINTEGERDatatype<>();
    /**  LONG               */  @Nonnull  public static final NumericDatatype<Long>           LONG                = new LONGDatatype<>();
    /**  INT                */  @Nonnull  public static final NumericDatatype<Integer>        INT                 = new INTDatatype<>();
    /**  SHORT              */  @Nonnull  public static final NumericDatatype<Short>          SHORT               = new SHORTDatatype<>();
    /**  BYTE               */  @Nonnull  public static final NumericDatatype<Byte>           BYTE                = new BYTEDatatype();
    /**  UNSIGNEDLONG       */  @Nonnull  public static final NumericDatatype<BigInteger>     UNSIGNEDLONG        = new UNSIGNEDLONGDatatype<>();
    /**  UNSIGNEDINT        */  @Nonnull  public static final NumericDatatype<Long>           UNSIGNEDINT         = new UNSIGNEDINTDatatype<>();
    /**  UNSIGNEDSHORT      */  @Nonnull  public static final NumericDatatype<Integer>        UNSIGNEDSHORT       = new UNSIGNEDSHORTDatatype<>();
    /**  UNSIGNEDBYTE       */  @Nonnull  public static final NumericDatatype<Short>          UNSIGNEDBYTE        = new UnsignedByteForShort();
    /**  NORMALIZEDSTRING   */  @Nonnull  public static final Datatype<String>                NORMALIZEDSTRING    = new NORMALIZEDSTRINGDatatype();
    /**  TOKEN              */  @Nonnull  public static final Datatype<String>                TOKEN               = new TOKENDatatype();
    /**  LANGUAGE           */  @Nonnull  public static final Datatype<String>                LANGUAGE            = new LANGUAGEDatatype();
    /**  NAME               */  @Nonnull  public static final Datatype<String>                NAME                = new NAMEDatatype();
    /**  NCNAME             */  @Nonnull  public static final Datatype<String>                NCNAME              = new NCNAMEDatatype();
    /**  NMTOKEN            */  @Nonnull  public static final Datatype<String>                NMTOKEN             = new NMTOKENDatatype();
    /**  NMTOKENS           */  @Nonnull  public static final Datatype<String>                NMTOKENS            = new NMTOKENSDatatype();
    /**  XMLLITERAL         */  @Nonnull  public static final Datatype<String>                XMLLITERAL          = new XMLLITERALDatatype();
    //@formatter:on
    private static final List<Datatype<?>> values = getList();
    private final Map<IRI, Datatype<?>> knownDatatypes = new HashMap<>();
    private static int uriIndex = 0;

    private DatatypeFactory() {
        values.forEach(d -> knownDatatypes.put(d.getDatatypeIRI(), d));
        knownDatatypes.put(XSDVocabulary.DATE.getIRI(), DATETIME);
    }

    private static List<Datatype<?>> getList() {
        List<Datatype<?>> toReturn = new ArrayList<>();
        toReturn.add(ANYURI);
        toReturn.add(BASE64BINARY);
        toReturn.add(BOOLEAN);
        toReturn.add(DATETIME);
        toReturn.add(HEXBINARY);
        toReturn.add(LITERAL);
        toReturn.add(PLAINLITERAL);
        toReturn.add(REAL);
        toReturn.add(STRING);
        toReturn.add(DATETIMESTAMP);
        toReturn.add(DECIMAL);
        toReturn.add(DOUBLE);
        toReturn.add(FLOAT);
        toReturn.add(BYTE);
        toReturn.add(INT);
        toReturn.add(INTEGER);
        toReturn.add(LONG);
        toReturn.add(NEGATIVEINTEGER);
        toReturn.add(NONNEGATIVEINTEGER);
        toReturn.add(NONPOSITIVEINTEGER);
        toReturn.add(POSITIVEINTEGER);
        toReturn.add(SHORT);
        toReturn.add(UNSIGNEDBYTE);
        toReturn.add(UNSIGNEDINT);
        toReturn.add(UNSIGNEDLONG);
        toReturn.add(UNSIGNEDSHORT);
        toReturn.add(RATIONAL);
        toReturn.add(LANGUAGE);
        toReturn.add(NAME);
        toReturn.add(NCNAME);
        toReturn.add(NMTOKEN);
        toReturn.add(NMTOKENS);
        toReturn.add(NORMALIZEDSTRING);
        toReturn.add(TOKEN);
        toReturn.add(XMLLITERAL);
        return Collections.unmodifiableList(toReturn);
    }

    /**
     * @return the predefined datatypes, in an enumeration fashion - the list is
     *         unmodifiable.
     */
    public static List<Datatype<?>> getValues() {
        return values;
    }

    /**
     * @return the datatypes defined for this instance of DatatypeFactory; the
     *         returned list is modifiable but not backed by the
     *         DatatypeFactory, so changes will not be reflected back.
     */
    public Collection<Datatype<?>> getKnownDatatypes() {
        return new ArrayList<>(knownDatatypes.values());
    }

    static HasIRI getIndex(String prefix) {
        return () -> IRI.create(prefix + uriIndex++);
    }

    /**
     * @param key
     *        key
     * @param <R>
     *        type
     * @return datatype for key
     */
    public <R extends Comparable<R>> Datatype<R> getKnownDatatype(IRI key) {
        Datatype<?> datatype = knownDatatypes.get(key);
        if (datatype != null) {
            return (Datatype<R>) datatype;
        }
        // defend against incorrect datatype IRIs: some ontologies use
        // ^^<xsd:dateTime> instead of ^^xsd:dateTime, and this causes the
        // xsd: prefix not to be correctly replaced.
        String iriString = key.toString();
        if (iriString.startsWith("xsd:")) {
            String name = iriString.substring(4);
            for (XSDVocabulary v : XSDVocabulary.values()) {
                if (v.getShortForm().equals(name)) {
                    datatype = knownDatatypes.get(v.getIRI());
                    if (datatype == null) {
                        LOGGER.error(
                            "A known datatype for {} cannot be found; literal will be replaced with rdfs:Literal",
                            iriString);
                        knownDatatypes.put(key, LITERAL);
                        return (Datatype<R>) LITERAL;
                    }
                    return (Datatype<R>) datatype;
                }
            }
        }
        LOGGER.error("A known datatype for {} cannot be found; literal will be replaced with rdfs:Literal", iriString);
        knownDatatypes.put(key, LITERAL);
        return (Datatype<R>) LITERAL;
    }

    /**
     * @param key
     *        key
     * @return true if known datatype
     */
    public boolean isKnownDatatype(IRI key) {
        return knownDatatypes.containsKey(key);
    }

    /**
     * @return datatype factory instance
     */
    public static DatatypeFactory getInstance() {
        return new DatatypeFactory();
    }

    /**
     * @param min
     *        min
     * @param max
     *        max
     * @param excluded
     *        excluded
     * @return true if interval not empty
     */
    @SuppressWarnings("rawtypes")
    public static boolean intervalWithValues(@Nullable Comparable min, @Nullable Comparable max, int excluded) {
        if (min == null) {
            // unbound lower limit - value space cannot be empty
            // even if the actual type used to represent the literal is bounded,
            // the limit should explicitly be there.
            return true;
        }
        if (max == null) {
            // unbound upper limit - value space cannot be empty
            // even if the actual type used to represent the literal is bounded,
            // the limit should explicitly be there.
            return true;
        }
        // min and max are both not null
        @SuppressWarnings("unchecked") int comparison = min.compareTo(max);
        // comparison < 0: min is strictly smaller than max. Value space can
        // still be empty:
        // (1,2)^^integer has no values
        // if excluded is 0, comparison <=0 is enough to return true; there
        // would still be one element: [1,1]
        if (excluded == 0) {
            return comparison <= 0;
        }
        // if excluded is 1, then comparison <0 is required
        if (excluded == 1) {
            return comparison < 0;
        }
        // if excluded is 2, then min + 1 unit must be strictly smaller than
        // max; this becomes type dependent since it depends on the
        // representation
        if (excluded == 2) {
            Comparable increased = increase((Number) min);
            @SuppressWarnings("unchecked") int compareTo = increased.compareTo(max);
            return compareTo < 0;
        }
        return false;
    }

    /**
     * @param v
     *        v
     * @return increased number
     */
    @Nullable
    @SuppressWarnings("rawtypes")
    public static Comparable increase(Number v) {
        if (v instanceof Float) {
            return Float.valueOf(v.floatValue() + Float.MIN_NORMAL);
        }
        if (v instanceof BigDecimal) {
            return ((BigDecimal) v).add(((BigDecimal) v).ulp());
        }
        if (v instanceof BigInteger) {
            return ((BigInteger) v).add(BigInteger.ONE);
        }
        if (v instanceof Double) {
            return Double.valueOf(v.doubleValue() + Double.MIN_NORMAL);
        }
        if (v instanceof Byte) {
            int i = v.byteValue() + 1;
            return Byte.valueOf((byte) i);
        }
        if (v instanceof Integer) {
            return Integer.valueOf(v.intValue() + 1);
        }
        if (v instanceof Long) {
            return Long.valueOf(v.longValue() + 1);
        }
        if (v instanceof Short) {
            int i = v.shortValue() + 1;
            return Short.valueOf((short) i);
        }
        if (v instanceof AtomicInteger) {
            return Integer.valueOf(((AtomicInteger) v).get() + 1);
        }
        if (v instanceof AtomicLong) {
            return Long.valueOf(((AtomicLong) v).get() + 1);
        }
        return null;
    }

    /**
     * @return namespace
     */
    public static String getNamespace() {
        return NAMESPACE;
    }
}
