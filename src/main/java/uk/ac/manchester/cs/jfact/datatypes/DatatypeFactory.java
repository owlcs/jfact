package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace.*;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.XSDVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conformance.Original;
import uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace;

/**
 * Factory for datatypes
 * 
 * @author ignazio
 */
@Original
public class DatatypeFactory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatatypeFactory.class);
    //@formatter:off
    private static final String NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
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
        // XXX handle dates as datetimes
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

    static int getIndex() {
        return uriIndex++;
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

    abstract static class AbstractNumericDatatype<R extends Comparable<R>> extends AbstractDatatype<R>
        implements NumericDatatype<R> {

        public AbstractNumericDatatype(IRI uri, Set<Facet> f, Set<Datatype<?>> ancestors) {
            super(uri, f, ancestors);
        }

        @Override
        public boolean getNumeric() {
            return true;
        }

        @Override
        public boolean isNumericDatatype() {
            return true;
        }

        @Override
        public NumericDatatype<R> asNumericDatatype() {
            return this;
        }

        @Override
        public boolean isOrderedDatatype() {
            return true;
        }

        @Override
        public OrderedDatatype<R> asOrderedDatatype() {
            return this;
        }

        @Override
        public ordered getOrdered() {
            return ordered.PARTIAL;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isInValueSpace(R lit) {
            if (this.hasMinExclusive()) {
                // to be in value space, ex min must be smaller than l
                Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
                if (l.compareTo(this.getMin()) <= 0) {
                    return false;
                }
            }
            if (this.hasMinInclusive()) {
                Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
                // to be in value space, min must be smaller or equal to l
                if (l.compareTo(this.getMin()) < 0) {
                    return false;
                }
            }
            if (this.hasMaxExclusive()) {
                Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
                // to be in value space, ex max must be bigger than l
                if (l.compareTo(this.getMax()) >= 0) {
                    return false;
                }
            }
            if (this.hasMaxInclusive()) {
                Comparable<R> l = (Comparable<R>) minExclusive.parseNumber(lit);
                // to be in value space, ex min must be smaller than l
                if (l.compareTo(this.getMax()) > 0) {
                    return false;
                }
            }
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isCompatible(Datatype<?> type) {
            if (type.equals(LITERAL)) {
                return true;
            }
            if (type.getNumeric()) {
                // specific cases: float and double have overlapping value
                // spaces with all numerics but are not compatible with any
                if (type.equals(FLOAT) || type.equals(DOUBLE)) {
                    return super.isCompatible(type);
                }
                NumericDatatype<R> wrapper;
                if (type instanceof NumericDatatype) {
                    wrapper = (NumericDatatype<R>) type;
                } else {
                    wrapper = AbstractNumericDatatype.wrap((Datatype<R>) type);
                }
                // then both types are numeric
                // if both have no max or both have no min -> there is an
                // overlap
                // if one has no max, then min must be smaller than max of the
                // other
                // if one has no min, the max must be larger than min of the
                // other
                // if one has neither max nor min, they are compatible
                if (!this.hasMax() && !this.hasMin()) {
                    return true;
                }
                if (!wrapper.hasMax() && !wrapper.hasMin()) {
                    return true;
                }
                if (!this.hasMax() && !wrapper.hasMax()) {
                    return true;
                }
                if (!this.hasMin() && !wrapper.hasMin()) {
                    return true;
                }
                if (!this.hasMin()) {
                    return overlapping(this, wrapper);
                }
                if (!this.hasMax()) {
                    return overlapping(wrapper, this);
                }
                if (!wrapper.hasMin()) {
                    return overlapping(wrapper, this);
                }
                if (!wrapper.hasMax()) {
                    return overlapping(this, wrapper);
                }
                // compare their range facets:
                // disjoint if:
                // exclusives:
                // one minInclusive/exclusive is strictly larger than the other
                // maxinclusive/exclusive
                return overlapping(this, wrapper) || overlapping(wrapper, this);
            } else {
                return false;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean emptyValueSpace() {
            if (!hasMin() || !hasMax()) {
                return false;
            }
            if (hasMaxExclusive() && hasMinExclusive()) {
                return getMax().compareTo((R) increase((Number) getMin())) < 0;
                // return getMin().equals(getMax());
            }
            return getMax().compareTo(getMin()) < 0;
        }

        private static <O extends Comparable<O>> NumericDatatype<O> wrap(Datatype<O> d) {
            return new NumericDatatypeWrapper<>(d);
        }

        @Override
        public boolean hasMinExclusive() {
            return knownNumericFacetValues.containsKey(minExclusive);
        }

        @Override
        public boolean hasMinInclusive() {
            return knownNumericFacetValues.containsKey(minInclusive);
        }

        @Override
        public boolean hasMaxExclusive() {
            return knownNumericFacetValues.containsKey(maxExclusive);
        }

        @Override
        public boolean hasMaxInclusive() {
            return knownNumericFacetValues.containsKey(maxInclusive);
        }

        @Override
        public boolean hasMin() {
            return this.hasMinInclusive() || this.hasMinExclusive();
        }

        @Override
        public boolean hasMax() {
            return this.hasMaxInclusive() || this.hasMaxExclusive();
        }

        @Nullable
        @Override
        public R getMin() {
            if (this.hasMinExclusive()) {
                return (R) knownNumericFacetValues.get(minExclusive);
            }
            if (this.hasMinInclusive()) {
                return (R) knownNumericFacetValues.get(minInclusive);
            }
            return null;
        }

        @Nullable
        @Override
        public R getMax() {
            if (this.hasMaxExclusive()) {
                return (R) knownNumericFacetValues.get(maxExclusive);
            }
            if (this.hasMaxInclusive()) {
                return (R) knownNumericFacetValues.get(maxInclusive);
            }
            return null;
        }
    }

    static class ANYURIDatatype extends AbstractDatatype<String> {

        ANYURIDatatype() {
            super(XSDVocabulary.ANY_URI.getIRI(), STRINGFACETS, Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }

        @Override
        public String parseValue(String s) {
            return COLLAPSE.normalize(s);
        }

        @Override
        public boolean isInValueSpace(String l) {
            try {
                URI.create(l);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    static class BASE64BINARYDatatype extends AbstractDatatype<String> {

        BASE64BINARYDatatype() {
            super(XSDVocabulary.BASE_64_BINARY.getIRI(), STRINGFACETS, Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, COLLAPSE);
        }

        @Override
        public String parseValue(String s) {
            return COLLAPSE.normalize(s);
        }

        @Override
        public boolean isInValueSpace(String s) {
            // all characters are letters, numbers, or +/=
            return s.chars().allMatch(c -> Character.isLetterOrDigit((char) c) || "+/=".indexOf(c) > -1);
        }
    }

    static class BOOLEANDatatype extends AbstractDatatype<Boolean> {

        BOOLEANDatatype() {
            super(XSDVocabulary.BOOLEAN.getIRI(), Utils.getFacets(pattern, whiteSpace),
                Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }

        @Override
        public cardinality getCardinality() {
            return cardinality.FINITE;
        }

        @Override
        public Collection<Literal<Boolean>> listValues() {
            // if all datatypes are compatible, the intersection is the two
            // booleans minu any restriction
            List<Literal<Boolean>> toReturn = new ArrayList<>(2);
            toReturn.add(buildLiteral(Boolean.toString(true)));
            toReturn.add(buildLiteral(Boolean.toString(false)));
            return toReturn;
        }

        @Override
        public Boolean parseValue(String s) {
            whitespace facet = (whitespace) whiteSpace.parse(knownNonNumericFacetValues.get(whiteSpace));
            return Boolean.valueOf(facet.normalize(s));
        }
    }

    static class DATETIMEDatatype extends AbstractDatatype<Date> implements OrderedDatatype<Date> {

        DATETIMEDatatype() {
            this(XSDVocabulary.DATE_TIME.getIRI());
        }

        DATETIMEDatatype(IRI u, Set<Datatype<?>> ancestors) {
            super(u, FACETS4, ancestors);
        }

        DATETIMEDatatype(IRI u) {
            super(u, FACETS4, Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }

        @Override
        public ordered getOrdered() {
            return ordered.PARTIAL;
        }

        @Override
        public boolean isOrderedDatatype() {
            return true;
        }

        @Override
        public OrderedDatatype<Date> asOrderedDatatype() {
            return this;
        }

        @Override
        public Date parseValue(String s) {
            try {
                XMLGregorianCalendar cal = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(s);
                return cal.normalize().toGregorianCalendar().getTime();
            } catch (DatatypeConfigurationException e) {
                throw new ReasonerInternalException(e);
            }
        }

        @Override
        public boolean isInValueSpace(Date l) {
            if (hasMinExclusive() && getMin().compareTo(l) <= 0) {
                return false;
            }
            if (hasMinInclusive() && getMin().compareTo(l) < 0) {
                return false;
            }
            if (hasMaxExclusive() && getMax().compareTo(l) >= 0) {
                return false;
            }
            if (hasMaxInclusive() && getMax().compareTo(l) > 0) {
                return false;
            }
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isCompatible(Datatype<?> type) {
            if (super.isCompatible(type)) {
                return true;
            }
            if (type.isSubType(this)) {
                // then its representation must be Calendars
                OrderedDatatype<Date> wrapper = (OrderedDatatype<Date>) type;
                // then both types are numeric
                // if both have no max or both have no min -> there is an
                // overlap
                // if one has no max, then min must be smaller than max of the
                // other
                // if one has no min, the max must be larger than min of the
                // other
                // if one has neither max nor min, they are compatible
                if (!hasMax() && !hasMin()) {
                    return true;
                }
                if (!wrapper.hasMax() && !wrapper.hasMin()) {
                    return true;
                }
                if (!hasMax() && !wrapper.hasMax()) {
                    return true;
                }
                if (!hasMin() && !wrapper.hasMin()) {
                    return true;
                }
                if (!hasMin()) {
                    return this.overlapping(this, wrapper);
                }
                if (!hasMax()) {
                    return this.overlapping(wrapper, this);
                }
                if (!wrapper.hasMin()) {
                    return this.overlapping(wrapper, this);
                }
                if (!wrapper.hasMax()) {
                    return this.overlapping(this, wrapper);
                }
                // compare their range facets:
                // disjoint if:
                // exclusives:
                // one minInclusive/exclusive is strictly larger than the other
                // maxinclusive/exclusive
                return this.overlapping(this, wrapper) || this.overlapping(wrapper, this);
            } else {
                return false;
            }
        }

        @Override
        public boolean hasMinExclusive() {
            return knownNumericFacetValues.containsKey(minExclusive);
        }

        @Override
        public boolean hasMinInclusive() {
            return knownNumericFacetValues.containsKey(minInclusive);
        }

        @Override
        public boolean hasMaxExclusive() {
            return knownNumericFacetValues.containsKey(maxExclusive);
        }

        @Override
        public boolean hasMaxInclusive() {
            return knownNumericFacetValues.containsKey(maxInclusive);
        }

        @Override
        public boolean hasMin() {
            return hasMinInclusive() || hasMinExclusive();
        }

        @Override
        public boolean hasMax() {
            return hasMaxInclusive() || hasMaxExclusive();
        }

        @Nullable
        @Override
        public Date getMin() {
            if (hasMinExclusive()) {
                return (Date) getFacetValue(minExclusive);
            }
            if (hasMinInclusive()) {
                return (Date) getFacetValue(minInclusive);
            }
            return null;
        }

        @Nullable
        @Override
        public Date getMax() {
            if (hasMaxExclusive()) {
                return (Date) getFacetValue(maxExclusive);
            }
            if (hasMaxInclusive()) {
                return (Date) getFacetValue(maxInclusive);
            }
            return null;
        }
    }

    static class HEXBINARYDatatype extends AbstractDatatype<String> {

        HEXBINARYDatatype() {
            super(XSDVocabulary.HEX_BINARY.getIRI(), STRINGFACETS, Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }

        @Override
        public String parseValue(String s) {
            return COLLAPSE.normalize(s);
        }

        @Override
        public boolean isInValueSpace(String s) {
            // all characters are numbers, or ABCDEF
            return s.chars().allMatch(c -> Character.isDigit((char) c) || "ABCDEF".indexOf(c) > -1);
        }
    }

    static class LITERALDatatype extends AbstractDatatype<String> {

        LITERALDatatype() {
            super(OWLRDFVocabulary.RDFS_LITERAL.getIRI(), Collections.<Facet> emptySet(),
                Collections.<Datatype<?>> emptySet());
        }

        @Override
        public String parseValue(String s) {
            return s;
        }
    }

    static class PLAINLITERALDatatype extends AbstractDatatype<String> {

        PLAINLITERALDatatype() {
            super(OWLRDFVocabulary.RDF_PLAIN_LITERAL.getIRI(),
                Utils.getFacets(length, minLength, maxLength, pattern, enumeration), Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        }

        @Override
        public String parseValue(String s) {
            return s;
        }
    }

    static class REALDatatype<R extends Comparable<R>> extends AbstractNumericDatatype<R> {

        public REALDatatype() {
            this(IRI.create("http://www.w3.org/2002/07/owl#", "real"));
        }

        REALDatatype(IRI uri) {
            this(uri, Utils.getFacets(MINMAX), Utils.generateAncestors(LITERAL));
        }

        REALDatatype(IRI uri, Set<Facet> f, Set<Datatype<?>> ancestors) {
            super(uri, f, ancestors);
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        }

        @SuppressWarnings("unchecked")
        @Override
        public R parseValue(String s) {
            return (R) new BigDecimal(s);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public boolean isInValueSpace(R l) {
            if (knownNumericFacetValues.containsKey(minExclusive)) {
                Comparable v = getNumericFacetValue(minExclusive);
                Comparable input = minExclusive.parseNumber(l);
                if (input.compareTo(v) <= 0) {
                    return false;
                }
            }
            if (knownNumericFacetValues.containsKey(minInclusive)) {
                Comparable v = getNumericFacetValue(minInclusive);
                Comparable input = minInclusive.parseNumber(l);
                if (input.compareTo(v) < 0) {
                    return false;
                }
            }
            if (knownNumericFacetValues.containsKey(maxInclusive)) {
                Comparable v = getNumericFacetValue(maxInclusive);
                Comparable input = maxInclusive.parseNumber(l);
                if (input.compareTo(v) > 0) {
                    return false;
                }
            }
            if (knownNumericFacetValues.containsKey(maxExclusive)) {
                Comparable v = getNumericFacetValue(maxExclusive);
                Comparable input = maxExclusive.parseNumber(l);
                if (input.compareTo(v) >= 0) {
                    return false;
                }
            }
            return true;
        }
    }

    static class STRINGDatatype extends AbstractDatatype<String> {

        public STRINGDatatype() {
            this(XSDVocabulary.STRING.getIRI(), Utils.generateAncestors(PLAINLITERAL));
        }

        STRINGDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, STRINGFACETS, ancestors);
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, PRESERVE);
        }

        @Override
        public String parseValue(String s) {
            return s;
        }
    }

    static class DATETIMESTAMPDatatype extends DATETIMEDatatype {

        DATETIMESTAMPDatatype() {
            super(XSDVocabulary.DATE_TIME_STAMP.getIRI(), Utils.generateAncestors(DATETIME));
        }
    }

    static class DECIMALDatatype<R extends Comparable<R>> extends RATIONALDatatype<R> {

        DECIMALDatatype() {
            this(XSDVocabulary.DECIMAL.getIRI(), Utils.generateAncestors(RATIONAL));
        }

        DECIMALDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, Utils.getFacets(DIGS, PEW, MINMAX), ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            return (R) new BigDecimal(s);
        }

        @Override
        public ordered getOrdered() {
            return ordered.TOTAL;
        }
    }

    static class DOUBLEDatatype extends AbstractNumericDatatype<Double> {

        DOUBLEDatatype() {
            super(XSDVocabulary.DOUBLE.getIRI(), Utils.getFacets(PEW, MINMAX), Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }

        @Override
        public boolean getBounded() {
            return true;
        }

        @Override
        public cardinality getCardinality() {
            return cardinality.FINITE;
        }

        @Override
        public boolean getNumeric() {
            return true;
        }

        @Override
        public Double parseValue(String s) {
            return Double.valueOf(s);
        }

        @Override
        public boolean isCompatible(Datatype<?> type) {
            // implementation from ABSTRACTDatatype
            if (type.isExpression()) {
                type = type.asExpression().getHostType();
            }
            return type.equals(this) || type.equals(DatatypeFactory.LITERAL) || type.isSubType(this) || isSubType(type);
        }
    }

    static class FLOATDatatype extends AbstractNumericDatatype<Float> {

        protected FLOATDatatype() {
            super(XSDVocabulary.FLOAT.getIRI(), FACETS4, Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }

        @Override
        public boolean getBounded() {
            return true;
        }

        @Override
        public cardinality getCardinality() {
            return cardinality.FINITE;
        }

        @Override
        public boolean getNumeric() {
            return true;
        }

        @Override
        public Float parseValue(String s) {
            String trim = s.trim();
            if ("-INF".equals(trim)) {
                return Float.valueOf(Float.NEGATIVE_INFINITY);
            }
            if ("INF".equals(trim)) {
                return Float.valueOf(Float.POSITIVE_INFINITY);
            }
            return Float.valueOf(s);
        }

        @Override
        public boolean isCompatible(Datatype<?> type) {
            // implementation from ABSTRACTDatatype
            if (type.isExpression()) {
                type = type.asExpression().getHostType();
            }
            return type.equals(this) || type.equals(DatatypeFactory.LITERAL) || type.isSubType(this) || isSubType(type);
        }

        @Override
        public boolean emptyValueSpace() {
            if (!hasMin() || !hasMax()) {
                return false;
            }
            if (hasMaxExclusive() && hasMinExclusive()) {
                if (getMin().compareTo(getMax()) == 0) {
                    // interval empty, no values admitted
                    return true;
                }
                // if diff is larger than 0, check
                return getMax().compareTo((Float) increase(getMin())) < 0;
            }
            return getMax().compareTo(getMin()) < 0;
        }
    }

    static class BYTEDatatype extends SHORTDatatype<Byte> {

        protected BYTEDatatype() {
            super(XSDVocabulary.BYTE.getIRI(), Utils.generateAncestors(SHORT));
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal(Byte.MIN_VALUE));
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(Byte.MAX_VALUE));
        }

        @Override
        public Byte parseValue(String s) {
            return Byte.valueOf(s);
        }
    }

    static class INTDatatype<R extends Comparable<R>> extends LONGDatatype<R> {

        protected INTDatatype() {
            this(XSDVocabulary.INT.getIRI(), Utils.generateAncestors(LONG));
        }

        protected INTDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal(Integer.MIN_VALUE));
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(Integer.MAX_VALUE));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            return (R) Integer.valueOf(s);
        }
    }

    static class INTEGERDatatype<R extends Comparable<R>> extends DECIMALDatatype<R> {

        protected INTEGERDatatype() {
            this(XSDVocabulary.INTEGER.getIRI(), Utils.generateAncestors(DECIMAL));
        }

        protected INTEGERDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNonNumericFacetValues.put(fractionDigits, Integer.valueOf(0));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            return (R) new BigInteger(s);
        }
    }

    static class LONGDatatype<R extends Comparable<R>> extends INTEGERDatatype<R> {

        protected LONGDatatype() {
            this(XSDVocabulary.LONG.getIRI(), Utils.generateAncestors(INTEGER));
        }

        protected LONGDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal(Long.MIN_VALUE));
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(Long.MAX_VALUE));
        }

        @Override
        @SuppressWarnings({ "unchecked" })
        public R parseValue(String s) {
            return (R) Long.valueOf(s);
        }

        @Override
        public boolean getBounded() {
            return true;
        }

        @Override
        public cardinality getCardinality() {
            return cardinality.FINITE;
        }
    }

    static class NEGATIVEINTEGERDatatype<R extends Comparable<R>> extends NONPOSITIVEINTEGERDatatype<R> {

        protected NEGATIVEINTEGERDatatype() {
            super(XSDVocabulary.NEGATIVE_INTEGER.getIRI(), Utils.generateAncestors(NONPOSITIVEINTEGER));
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(-1L));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            BigInteger parse = new BigInteger(s);
            if (parse.compareTo(new BigInteger("-1")) > 0) {
                throw new ArithmeticException("Negative integer required, but found: " + s);
            }
            return (R) parse;
        }
    }

    static class NONNEGATIVEINTEGERDatatype<R extends Comparable<R>> extends INTEGERDatatype<R> {

        protected NONNEGATIVEINTEGERDatatype() {
            this(XSDVocabulary.NON_NEGATIVE_INTEGER.getIRI(), Utils.generateAncestors(INTEGER));
        }

        protected NONNEGATIVEINTEGERDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal(0L));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            BigInteger parseValue = new BigInteger(s);
            if (parseValue.compareTo(BigInteger.ZERO) < 0) {
                throw new ArithmeticException("Non negative integer required, but found: " + s);
            }
            return (R) parseValue;
        }
    }

    static class NONPOSITIVEINTEGERDatatype<R extends Comparable<R>> extends INTEGERDatatype<R> {

        protected NONPOSITIVEINTEGERDatatype() {
            this(XSDVocabulary.NON_POSITIVE_INTEGER.getIRI(), Utils.generateAncestors(INTEGER));
        }

        protected NONPOSITIVEINTEGERDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(0L));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            BigInteger parse = new BigInteger(s);
            if (parse.compareTo(BigInteger.ZERO) > 0) {
                throw new ArithmeticException("Non positive integer required, but found: " + s);
            }
            return (R) parse;
        }
    }

    static class POSITIVEINTEGERDatatype<R extends Comparable<R>> extends NONNEGATIVEINTEGERDatatype<R> {

        protected POSITIVEINTEGERDatatype() {
            super(XSDVocabulary.POSITIVE_INTEGER.getIRI(), Utils.generateAncestors(NONNEGATIVEINTEGER));
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal(1L));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            BigInteger parseValue = new BigInteger(s);
            if (parseValue.compareTo(BigInteger.ZERO) <= 0) {
                throw new ArithmeticException("Positive integer required, but found: " + s);
            }
            return (R) parseValue;
        }
    }

    static class SHORTDatatype<R extends Comparable<R>> extends INTDatatype<R> {

        protected SHORTDatatype() {
            this(XSDVocabulary.SHORT.getIRI(), Utils.generateAncestors(INT));
        }

        protected SHORTDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal(Short.MIN_VALUE));
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(Short.MAX_VALUE));
        }

        @Override
        @SuppressWarnings({ "unchecked" })
        public R parseValue(String s) {
            return (R) Short.valueOf(s);
        }
    }

    abstract static class UNSIGNEDBYTEDatatype<R extends Comparable<R>> extends UNSIGNEDSHORTDatatype<R> {

        protected UNSIGNEDBYTEDatatype() {
            super(XSDVocabulary.UNSIGNED_BYTE.getIRI(), Utils.generateAncestors(UNSIGNEDSHORT));
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal((short) 0));
            knownNumericFacetValues.put(maxInclusive, new BigDecimal((short) 255));
        }
    }

    static class UNSIGNEDINTDatatype<R extends Comparable<R>> extends UNSIGNEDLONGDatatype<R> {

        protected UNSIGNEDINTDatatype() {
            this(XSDVocabulary.UNSIGNED_INT.getIRI(), Utils.generateAncestors(UNSIGNEDLONG));
        }

        protected UNSIGNEDINTDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, new BigDecimal(0L));
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(4294967295L));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            Long parseInt = Long.valueOf(s);
            if (parseInt.longValue() < 0) {
                throw new ArithmeticException("Unsigned int required, but found: " + s);
            }
            return (R) parseInt;
        }
    }

    static class UNSIGNEDLONGDatatype<R extends Comparable<R>> extends NONNEGATIVEINTEGERDatatype<R> {

        protected UNSIGNEDLONGDatatype() {
            this(XSDVocabulary.UNSIGNED_LONG.getIRI(), Utils.generateAncestors(NONNEGATIVEINTEGER));
        }

        protected UNSIGNEDLONGDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, BigDecimal.ZERO);
            knownNumericFacetValues.put(maxInclusive, new BigDecimal("18446744073709551615"));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            BigInteger b = new BigInteger(s);
            if (b.compareTo(BigInteger.ZERO) < 0) {
                throw new ArithmeticException("Unsigned long required, but found: " + s);
            }
            return (R) b;
        }

        @Override
        public boolean getBounded() {
            return true;
        }

        @Override
        public cardinality getCardinality() {
            return cardinality.FINITE;
        }
    }

    static class UNSIGNEDSHORTDatatype<R extends Comparable<R>> extends UNSIGNEDINTDatatype<R> {

        protected UNSIGNEDSHORTDatatype() {
            this(XSDVocabulary.UNSIGNED_SHORT.getIRI(), Utils.generateAncestors(UNSIGNEDINT));
        }

        protected UNSIGNEDSHORTDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, NUMBER_EXPRESSION);
            knownNumericFacetValues.put(minInclusive, BigDecimal.ZERO);
            knownNumericFacetValues.put(maxInclusive, new BigDecimal(65535));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            Integer parseShort = Integer.valueOf(s);
            if (parseShort.intValue() < 0) {
                throw new ArithmeticException("Unsigned short required, but found: " + s);
            }
            return (R) parseShort;
        }
    }

    private static class UnsignedByteForShort extends UNSIGNEDBYTEDatatype<Short> {

        public UnsignedByteForShort() {}

        @Override
        public Short parseValue(String s) {
            short parseByte = Short.parseShort(s);
            if (parseByte < 0) {
                throw new ArithmeticException("Unsigned short required, but found: " + s);
            }
            return Short.valueOf(parseByte);
        }
    }

    static class RATIONALDatatype<R extends Comparable<R>> extends REALDatatype<R> {

        protected RATIONALDatatype(IRI uri, Set<Facet> f, Set<Datatype<?>> ancestors) {
            super(uri, f, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
        }

        protected RATIONALDatatype() {
            this(IRI.create("http://www.w3.org/2002/07/owl#", "rational"));
        }

        protected RATIONALDatatype(IRI uri) {
            this(uri, Collections.<Facet> emptySet(), Utils.generateAncestors(REAL));
        }

        @Override
        @SuppressWarnings("unchecked")
        public R parseValue(String s) {
            return (R) new BigDecimal(s);
        }
    }

    static class LANGUAGEDatatype extends TOKENDatatype {

        protected LANGUAGEDatatype() {
            super(XSDVocabulary.LANGUAGE.getIRI(), Utils.generateAncestors(TOKEN));
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
        }
    }

    static class NAMEDatatype extends TOKENDatatype {

        public NAMEDatatype() {
            this(XSDVocabulary.NAME.getIRI(), Utils.generateAncestors(TOKEN));
        }

        protected NAMEDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, "\\i\\c*");
        }
    }

    static class NCNAMEDatatype extends NAMEDatatype {

        protected NCNAMEDatatype() {
            super(XSDVocabulary.NCNAME.getIRI(), Utils.generateAncestors(NAME));
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, "[\\i-[:]][\\c-[:]]*");
        }

        @Override
        public IRI getDatatypeIRI() {
            return uri;
        }
    }

    static class NMTOKENDatatype extends TOKENDatatype {

        protected NMTOKENDatatype() {
            this(XSDVocabulary.NMTOKEN.getIRI(), Utils.generateAncestors(TOKEN));
        }

        protected NMTOKENDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(pattern, "\\c+");
        }

        @Override
        public IRI getDatatypeIRI() {
            return uri;
        }
    }

    static class NMTOKENSDatatype extends NMTOKENDatatype {

        protected NMTOKENSDatatype() {
            super(IRI.create(NAMESPACE, "NMTOKENS"), Utils.generateAncestors(NMTOKEN));
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
            knownNonNumericFacetValues.put(minLength, Integer.valueOf(1));
        }
    }

    static class NORMALIZEDSTRINGDatatype extends STRINGDatatype {

        protected NORMALIZEDSTRINGDatatype() {
            this(XSDVocabulary.NORMALIZED_STRING.getIRI(), Utils.generateAncestors(STRING));
        }

        protected NORMALIZEDSTRINGDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, REPLACE);
        }
    }

    static class TOKENDatatype extends NORMALIZEDSTRINGDatatype {

        protected TOKENDatatype() {
            this(XSDVocabulary.TOKEN.getIRI(), Utils.generateAncestors(NORMALIZEDSTRING));
        }

        protected TOKENDatatype(IRI uri, Set<Datatype<?>> ancestors) {
            super(uri, ancestors);
            knownNonNumericFacetValues.putAll(super.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(super.getKnownNumericFacetValues());
            knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
        }
    }

    static class XMLLITERALDatatype extends AbstractDatatype<String> {

        protected XMLLITERALDatatype() {
            super(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "XMLLiteral"),
                Collections.<Facet> emptySet(), Utils.generateAncestors(LITERAL));
            knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
            knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        }

        @Override
        public String parseValue(String s) {
            // XXX sort of arbitrary decision; the specs say it depends on the
            // XML datatype whitespace normalization policy, but that's not
            // clear. Some W3C tests assume that text elements are irrelevant
            return COLLAPSE.normalize(s);
        }

        @Override
        public boolean isInValueSpace(String l) {
            try {
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(l.getBytes()));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    /**
     * @return namespace
     */
    public static String getNamespace() {
        return NAMESPACE;
    }
}
