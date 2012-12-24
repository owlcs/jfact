package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace;

@SuppressWarnings("javadoc")
public class DatatypeFactory {
    static final String namespace = "http://www.w3.org/2001/XMLSchema#";
    static final Facet[] minmax = new Facet[] { maxInclusive, maxExclusive, minInclusive,
            minExclusive };
    static final Facet[] pew = new Facet[] { pattern, enumeration, whiteSpace };
    static final Facet[] len = new Facet[] { length, minLength, maxLength };
    static final Facet[] digs = new Facet[] { totalDigits, fractionDigits };
    static final Set<Facet> StringFacets = Utils.getFacets(pew, len);
    static final Set<Facet> FACETS4 = Utils.getFacets(pew, minmax);
    public static final Datatype<String> LITERAL = new LITERAL_DATATYPE();
    public static final Datatype<String> ANYURI = new ANYURI_DATATYPE();
    public static final Datatype<String> BASE64BINARY = new BASE64BINARY_DATATYPE();
    public static final Datatype<Boolean> BOOLEAN = new BOOLEAN_DATATYPE();
    public static final Datatype<Calendar> DATETIME = new DATETIME_DATATYPE();
    public static final Datatype<String> HEXBINARY = new HEXBINARY_DATATYPE();
    public static final Datatype<String> STRING = new STRING_DATATYPE();
    public static final Datatype<String> PLAINLITERAL = new PLAINLITERAL_DATATYPE();
    public static final Datatype<BigDecimal> REAL = new REAL_DATATYPE<BigDecimal>() {
        @Override
        public BigDecimal parseValue(String s) {
            return new BigDecimal(s);
        }
    };
    public static final Datatype<BigDecimal> RATIONAL = new RATIONAL_DATATYPE<BigDecimal>() {
        @Override
        public BigDecimal parseValue(String s) {
            return new BigDecimal(s);
        }
    };
    public static final Datatype<Calendar> DATETIMESTAMP = new DATETIMESTAMP_DATATYPE();
    public static final Datatype<BigDecimal> DECIMAL = new DECIMAL_DATATYPE<BigDecimal>() {
        @Override
        public BigDecimal parseValue(String s) {
            return new BigDecimal(s);
        }
    };
    public static final Datatype<BigInteger> INTEGER = new INTEGER_DATATYPE<BigInteger>() {
        @Override
        public BigInteger parseValue(String s) {
            return new BigInteger(s);
        }
    };
    public static final Datatype<Double> DOUBLE = new DOUBLE_DATATYPE();
    public static final Datatype<Float> FLOAT = new FLOAT_DATATYPE();
    public static final Datatype<BigInteger> NONPOSITIVEINTEGER = new NONPOSITIVEINTEGER_DATATYPE<BigInteger>() {
        @Override
        public BigInteger parseValue(String s) {
            BigInteger parse = new BigInteger(s);
            if (parse.compareTo(BigInteger.ZERO) > 0) {
                throw new ArithmeticException(
                        "Non positive integer required, but found: " + s);
            }
            return parse;
        }
    };
    public static final Datatype<BigInteger> NEGATIVEINTEGER = new NEGATIVEINTEGER_DATATYPE<BigInteger>() {
        @Override
        public BigInteger parseValue(String s) {
            BigInteger parse = new BigInteger(s);
            if (parse.compareTo(new BigInteger("-1")) > 0) {
                throw new ArithmeticException("Negative integer required, but found: "
                        + s);
            }
            return parse;
        }
    };
    public static final Datatype<BigInteger> NONNEGATIVEINTEGER = new NONNEGATIVEINTEGER_DATATYPE<BigInteger>() {
        @Override
        public BigInteger parseValue(String s) {
            BigInteger parseValue = new BigInteger(s);
            if (parseValue.compareTo(BigInteger.ZERO) < 0) {
                throw new ArithmeticException(
                        "Non negative integer required, but found: " + s);
            }
            return parseValue;
        }
    };
    public static final Datatype<BigInteger> POSITIVEINTEGER = new POSITIVEINTEGER_DATATYPE<BigInteger>() {
        @Override
        public BigInteger parseValue(String s) {
            BigInteger parseValue = new BigInteger(s);
            if (parseValue.compareTo(BigInteger.ZERO) <= 0) {
                throw new ArithmeticException("Positive integer required, but found: "
                        + s);
            }
            return parseValue;
        }
    };
    public static final Datatype<Long> LONG = new LONG_DATATYPE<Long>() {
        @Override
        public Long parseValue(String s) {
            return Long.parseLong(s);
        }
    };
    public static final Datatype<Integer> INT = new INT_DATATYPE<Integer>() {
        @Override
        public Integer parseValue(String s) {
            return Integer.parseInt(s);
        }
    };
    public static final Datatype<Short> SHORT = new SHORT_DATATYPE<Short>() {
        @Override
        public Short parseValue(String s) {
            return Short.parseShort(s);
        }
    };
    public static final Datatype<Byte> BYTE = new BYTE_DATATYPE<Byte>() {
        @Override
        public Byte parseValue(String s) {
            return Byte.parseByte(s);
        }
    };
    public static final Datatype<BigInteger> UNSIGNEDLONG = new UNSIGNEDLONG_DATATYPE<BigInteger>() {
        @Override
        public BigInteger parseValue(String s) {
            BigInteger b = new BigInteger(s);
            if (b.compareTo(BigInteger.ZERO) < 0) {
                throw new ArithmeticException("Unsigned long required, but found: " + s);
            }
            return b;
        }
    };
    public static final Datatype<Long> UNSIGNEDINT = new UNSIGNEDINT_DATATYPE<Long>() {
        @Override
        public Long parseValue(String s) {
            Long parseInt = Long.parseLong(s);
            if (parseInt < 0) {
                throw new ArithmeticException("Unsigned int required, but found: " + s);
            }
            return parseInt;
        }
    };
    public static final Datatype<Integer> UNSIGNEDSHORT = new UNSIGNEDSHORT_DATATYPE<Integer>() {
        @Override
        public Integer parseValue(String s) {
            Integer parseShort = Integer.parseInt(s);
            if (parseShort < 0) {
                throw new ArithmeticException("Unsigned short required, but found: " + s);
            }
            return parseShort;
        }
    };
    public static final Datatype<Short> UNSIGNEDBYTE = new UNSIGNEDBYTE_DATATYPE<Short>() {
        @Override
        public Short parseValue(String s) {
            Short parseByte = Short.parseShort(s);
            if (parseByte < 0) {
                throw new ArithmeticException("Unsigned short required, but found: " + s);
            }
            return parseByte;
        }
    };
    public static final Datatype<String> NORMALIZEDSTRING = new NORMALIZEDSTRING_DATATYPE();
    public static final Datatype<String> TOKEN = new TOKEN_DATATYPE();
    public static final Datatype<String> LANGUAGE = new LANGUAGE_DATATYPE();
    public static final Datatype<String> NAME = new NAME_DATATYPE();
    public static final Datatype<String> NCNAME = new NCNAME_DATATYPE();
    public static final Datatype<String> NMTOKEN = new NMTOKEN_DATATYPE();
    public static final Datatype<String> NMTOKENS = new NMTOKENS_DATATYPE();
    public static final Datatype<String> XMLLITERAL = new XMLLITERAL_DATATYPE();
    private static final List<Datatype<?>> values = getList();

    private static final List<Datatype<?>> getList() {
        List<Datatype<?>> toReturn = new ArrayList<Datatype<?>>();
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

    /** @return the predefined datatypes, in an enumeration fashion - the list is
     *         unmodifiable. */
    public static List<Datatype<?>> getValues() {
        return values;
    }

    /** @return the datatypes defined for this instance of DatatypeFactory; the
     *         returned list is modifiable but not backed by the
     *         DatatypeFactory, so changes will not be reflected back. */
    public Collection<Datatype<?>> getKnownDatatypes() {
        return new ArrayList<Datatype<?>>(knownDatatypes.values());
    }

    private final Map<String, Datatype<?>> knownDatatypes = new HashMap<String, Datatype<?>>();
    private static int uri_index = 0;

    static int getIndex() {
        return uri_index++;
    }

    private DatatypeFactory() {
        for (Datatype<?> d : values) {
            knownDatatypes.put(d.getDatatypeURI(), d);
        }
        // XXX handle dates as datetimes
        knownDatatypes.put("http://www.w3.org/2001/XMLSchema#date", DATETIME);
    }

    public Datatype<?> getKnownDatatype(String key) {
        return knownDatatypes.get(key);
    }

    public boolean isKnownDatatype(String key) {
        return knownDatatypes.containsKey(key);
    }

    public static DatatypeFactory getInstance() {
        return new DatatypeFactory();
    }

    // public <O extends Comparable<O>> Datatype<O> defineNewDatatype(
    // Datatype<O> base, String uri, Collection<Facet> facets,
    // Collection<Datatype<?>> ancestors, Map<Facet, Object> knownFacets) {
    // return defineNewDatatype(base, uri, facets, ancestors, knownFacets,
    // base.getOrdered(), base.getNumeric(), base.getCardinality(),
    // base.getBounded());
    // }
    //
    // public <O extends Comparable<O>> Datatype<O> defineNewDatatype(
    // Datatype<O> base, String uri) {
    // return defineNewDatatype(base, uri, null, null, null, base.getOrdered(),
    // base.getNumeric(), base.getCardinality(), base.getBounded());
    // }
    //
    // public Datatype<String> defineNewDatatype( String uri) {
    // return defineNewDatatype(LITERAL, uri, null, null, null,
    // LITERAL.getOrdered(),
    // LITERAL.getNumeric(), LITERAL.getCardinality(), LITERAL.getBounded());
    // }
    //
    // public <R extends Comparable<R>> DatatypeExpression<R>
    // defineNewDatatypeIntersection(
    // Collection<Datatype<R>> types) {
    // return new DatatypeIntersection<R>(getIndex(), types);
    // }
    //
    // public <R extends Comparable<R>> DatatypeExpression<R>
    // defineNewDatatypeUnion(
    // Collection<Datatype<R>> types) {
    // return new DatatypeUnion<R>(getIndex(), types);
    // }
    //
    // public <O extends Comparable<O>> Datatype<O> defineNewDatatype(
    // Datatype<O> base, String uri, Collection<Facet> facets,
    // Collection<Datatype<?>> ancestors,
    // Map<Facet, Object> knownFacets, ordered ord,
    // boolean numeric, cardinality card, boolean bound) {
    // if (knownDatatypes.containsKey(uri)) { throw new
    // IllegalArgumentException(
    // "datatype definitions cannot be overridden: " + uri
    // + " is already in the known types"); }
    // Set<Facet> f = new HashSet<Facet>(base.getFacets());
    // if (facets != null) {
    // f.addAll(facets);
    // }
    // Set<Datatype<?>> a = new HashSet<Datatype<?>>(base.getAncestors());
    // if (ancestors != null) {
    // a.addAll(ancestors);
    // }
    // a.add(base);
    // Map<Facet, Object> known = new HashMap<Facet, Object>(
    // base.getKnownFacetValues());
    // if (knownFacets != null) {
    // known.putAll(knownFacets);
    // }
    // Datatype<O> toReturn = new ABSTRACT_DATATYPE<O>(uri, f) {
    // @Override
    // public ordered getOrdered() {
    // return ord;
    // }
    //
    // @Override
    // public boolean getNumeric() {
    // return numeric;
    // }
    //
    // @Override
    // public cardinality getCardinality() {
    // return card;
    // }
    //
    // @Override
    // public boolean getBounded() {
    // return bound;
    // }
    //
    // @Override
    // public boolean isInValueSpace(O l) {
    // return base.isInValueSpace(l);
    // }
    //
    // @Override
    // public Collection<Literal<O>> listValues() {
    // return base.listValues();
    // }
    //
    // public O parseValue(String s) {
    // return base.parseValue(s);
    // }
    //
    // @Override
    // public Literal<O> buildLiteral(String s) {
    // return base.buildLiteral(s);
    // }
    // };
    // knownDatatypes.put(uri, toReturn);
    // return toReturn;
    // }
    public static boolean nonEmptyInterval(BigDecimal min, BigDecimal max, int excluded) {
        if (min == null) {
            // unbound lower limit - value space cannot be empty
            // even if the actual type used to represent the literal is bounded,
            // the limit should explicitly be there.
            return false;
        }
        if (max == null) {
            // unbound upper limit - value space cannot be empty
            // even if the actual type used to represent the literal is bounded,
            // the limit should explicitly be there.
            return false;
        }
        // min and max are both not null
        int comparison = min.compareTo(max);
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
            return min.add(min.ulp()).compareTo(max) < 0;
        }
        return false;
    }

    public static <R extends Comparable<R>> DatatypeExpression<R> getDatatypeExpression(
            Datatype<R> base) {
        return new DatatypeExpressionImpl<R>(base);
    }

    public static <R extends Comparable<R>> DatatypeExpression<R>
            getNumericDatatypeExpression(NumericDatatype<R> base) {
        return new DatatypeNumericExpressionImpl<R>(base);
    }

    public static <R extends Comparable<R>> DatatypeExpression<R>
            getOrderedDatatypeExpression(Datatype<R> base) {
        return new DatatypeOrderedExpressionImpl<R>(base);
    }

    static abstract class ABSTRACT_NUMERIC_DATATYPE<R extends Comparable<R>> extends
            ABSTRACT_DATATYPE<R> implements NumericDatatype<R> {
        public ABSTRACT_NUMERIC_DATATYPE(String uri, Set<Facet> f) {
            super(uri, f);
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
        public OrderedDatatype<BigDecimal> asOrderedDatatype() {
            return this;
        }

        @Override
        public ordered getOrdered() {
            return ordered.PARTIAL;
        }

        @Override
        public boolean isInValueSpace(R l) {
            if (this.hasMinExclusive()) {
                if (this.getMin().compareTo((BigDecimal) minExclusive.parseNumber(l)) <= 0) {
                    return false;
                }
            }
            if (this.hasMinInclusive()) {
                if (this.getMin().compareTo((BigDecimal) minInclusive.parseNumber(l)) < 0) {
                    return false;
                }
            }
            if (this.hasMaxExclusive()) {
                if (this.getMax().compareTo((BigDecimal) maxExclusive.parseNumber(l)) >= 0) {
                    return false;
                }
            }
            if (this.hasMaxInclusive()) {
                if (this.getMax().compareTo((BigDecimal) maxInclusive.parseNumber(l)) > 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isCompatible(Datatype<?> type) {
            // if (!super.isCompatible(type)) {
            // return false;
            // }
            if (type.equals(LITERAL)) {
                return true;
            }
            // if(isSubType(type)||type.isSubType(this)) {
            // return true;
            // }
            if (type.getNumeric()) {
                // specific cases: float and double have overlapping value
                // spaces with all numerics but are not compatible with any
                if (type.equals(FLOAT) || type.equals(DOUBLE)) {
                    return super.isCompatible(type);
                }
                NumericDatatype<?> wrapper;
                if (type instanceof NumericDatatype) {
                    wrapper = (NumericDatatype<?>) type;
                } else {
                    wrapper = this.wrap(type);
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
                    return this.overlapping(this, wrapper);
                }
                if (!this.hasMax()) {
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

        <O extends Comparable<O>> NumericDatatype<O> wrap(Datatype<O> d) {
            return new NumericDatatypeWrapper<O>(d);
        }

        @Override
        public boolean hasMinExclusive() {
            return knownFacetValues.containsKey(minExclusive);
        }

        @Override
        public boolean hasMinInclusive() {
            return knownFacetValues.containsKey(minInclusive);
        }

        @Override
        public boolean hasMaxExclusive() {
            return knownFacetValues.containsKey(maxExclusive);
        }

        @Override
        public boolean hasMaxInclusive() {
            return knownFacetValues.containsKey(maxInclusive);
        }

        @Override
        public boolean hasMin() {
            return this.hasMinInclusive() || this.hasMinExclusive();
        }

        @Override
        public boolean hasMax() {
            return this.hasMaxInclusive() || this.hasMaxExclusive();
        }

        @Override
        public BigDecimal getMin() {
            if (this.hasMinExclusive()) {
                return (BigDecimal) minExclusive.parseNumber(knownFacetValues
                        .get(minExclusive));
            }
            if (this.hasMinInclusive()) {
                return (BigDecimal) minInclusive.parseNumber(knownFacetValues
                        .get(minInclusive));
            }
            return null;
        }

        @Override
        public BigDecimal getMax() {
            if (this.hasMaxExclusive()) {
                return (BigDecimal) maxExclusive.parseNumber(knownFacetValues
                        .get(maxExclusive));
            }
            if (this.hasMaxInclusive()) {
                return (BigDecimal) maxInclusive.parseNumber(knownFacetValues
                        .get(maxInclusive));
            }
            return null;
        }
    }

    static class ANYURI_DATATYPE extends ABSTRACT_DATATYPE<String> {
        ANYURI_DATATYPE() {
            super(namespace + "anyURI", StringFacets);
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        @Override
        public String parseValue(String s) {
            return whitespace.collapse.normalize(s);
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

    static class BASE64BINARY_DATATYPE extends ABSTRACT_DATATYPE<String> {
        BASE64BINARY_DATATYPE() {
            super(namespace + "base64Binary", StringFacets);
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        @Override
        public String parseValue(String s) {
            return whitespace.collapse.normalize(s);
        }

        @Override
        public boolean isInValueSpace(String s) {
            // all characters are letters, numbers, or +/=
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!Character.isLetter(c) && !Character.isDigit(c)
                        && "+/=".indexOf(c) == -1) {
                    return false;
                }
            }
            return true;
        }
    }

    static class BOOLEAN_DATATYPE extends ABSTRACT_DATATYPE<Boolean> {
        BOOLEAN_DATATYPE() {
            super(namespace + "boolean", Utils.getFacets(pattern, whiteSpace));
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        @Override
        public cardinality getCardinality() {
            return cardinality.FINITE;
        }

        @Override
        public Collection<Literal<Boolean>> listValues() {
            // if all datatypes are compatible, the intersection is the two
            // booleans minu any restriction
            List<Literal<Boolean>> toReturn = new ArrayList<Literal<Boolean>>(2);
            toReturn.add(buildLiteral(Boolean.toString(true)));
            toReturn.add(buildLiteral(Boolean.toString(false)));
            return toReturn;
        }

        @Override
        public Boolean parseValue(String s) {
            whitespace facet = (whitespace) whiteSpace.parse(knownFacetValues
                    .get(whiteSpace));
            return Boolean.parseBoolean(facet.normalize(s));
        }
    }

    static class DATETIME_DATATYPE extends ABSTRACT_DATATYPE<Calendar> implements
            OrderedDatatype<Calendar> {
        DATETIME_DATATYPE() {
            this(namespace + "dateTime");
        }

        DATETIME_DATATYPE(String u) {
            super(u, FACETS4);
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
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
        public OrderedDatatype<Calendar> asOrderedDatatype() {
            return this;
        }

        @Override
        public Calendar parseValue(String s) {
            XMLGregorianCalendar cal;
            try {
                cal = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(s);
                return cal.normalize().toGregorianCalendar();
            } catch (DatatypeConfigurationException e) {
                throw new ReasonerInternalException(e);
            }
        }

        @Override
        public boolean isInValueSpace(Calendar l) {
            if (hasMinExclusive()) {
                if (getMin().compareTo(l) <= 0) {
                    return false;
                }
            }
            if (hasMinInclusive()) {
                if (getMin().compareTo(l) < 0) {
                    return false;
                }
            }
            if (hasMaxExclusive()) {
                if (getMax().compareTo(l) >= 0) {
                    return false;
                }
            }
            if (hasMaxInclusive()) {
                if (getMax().compareTo(l) > 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isCompatible(Datatype<?> type) {
            if (super.isCompatible(type)) {
                return true;
            }
            if (type.isSubType(this)) {
                // then its representation must be Calendars
                OrderedDatatype<Calendar> wrapper = (OrderedDatatype<Calendar>) type;
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
            return knownFacetValues.containsKey(minExclusive);
        }

        @Override
        public boolean hasMinInclusive() {
            return knownFacetValues.containsKey(minInclusive);
        }

        @Override
        public boolean hasMaxExclusive() {
            return knownFacetValues.containsKey(maxExclusive);
        }

        @Override
        public boolean hasMaxInclusive() {
            return knownFacetValues.containsKey(maxInclusive);
        }

        @Override
        public boolean hasMin() {
            return hasMinInclusive() || hasMinExclusive();
        }

        @Override
        public boolean hasMax() {
            return hasMaxInclusive() || hasMaxExclusive();
        }

        @Override
        public Calendar getMin() {
            if (hasMinExclusive()) {
                return (Calendar) getFacetValue(minExclusive);
            }
            if (hasMinInclusive()) {
                return (Calendar) getFacetValue(minInclusive);
            }
            return null;
        }

        @Override
        public Calendar getMax() {
            if (hasMaxExclusive()) {
                return (Calendar) getFacetValue(maxExclusive);
            }
            if (hasMaxInclusive()) {
                return (Calendar) getFacetValue(maxInclusive);
            }
            return null;
        }
    }

    static class HEXBINARY_DATATYPE extends ABSTRACT_DATATYPE<String> {
        HEXBINARY_DATATYPE() {
            super(namespace + "hexBinary", StringFacets);
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        @Override
        public String parseValue(String s) {
            return whitespace.collapse.normalize(s);
        }

        @Override
        public boolean isInValueSpace(String s) {
            // all characters are numbers, or ABCDEF
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!Character.isDigit(c) && "ABCDEF".indexOf(c) == -1) {
                    return false;
                }
            }
            return true;
        }
    }

    static class LITERAL_DATATYPE extends ABSTRACT_DATATYPE<String> {
        LITERAL_DATATYPE() {
            super("http://www.w3.org/2000/01/rdf-schema#Literal", Collections
                    .<Facet> emptySet());
            ancestors = Collections.emptySet();
        }

        @Override
        public String parseValue(String s) {
            return s;
        }
    }

    static class PLAINLITERAL_DATATYPE extends ABSTRACT_DATATYPE<String> {
        PLAINLITERAL_DATATYPE() {
            super("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral", Utils
                    .getFacets(length, minLength, maxLength, pattern, enumeration));
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
        }

        @Override
        public String parseValue(String s) {
            return s;
        }
    }

    abstract static class REAL_DATATYPE<R extends Comparable<R>> extends
            ABSTRACT_NUMERIC_DATATYPE<R> {
        public REAL_DATATYPE() {
            this("http://www.w3.org/2002/07/owl#real");
        }

        REAL_DATATYPE(String uri) {
            this(uri, Utils.getFacets(minmax));
        }

        REAL_DATATYPE(String uri, Set<Facet> f) {
            super(uri, f);
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
        }

        @Override
        public boolean isInValueSpace(R l) {
            if (knownFacetValues.containsKey(minExclusive)) {
                BigDecimal v = getNumericFacetValue(minExclusive);
                BigDecimal input = (BigDecimal) minExclusive.parseNumber(l);
                if (input.compareTo(v) <= 0) {
                    return false;
                }
            }
            if (knownFacetValues.containsKey(minInclusive)) {
                BigDecimal v = getNumericFacetValue(minInclusive);
                BigDecimal input = (BigDecimal) minInclusive.parseNumber(l);
                if (input.compareTo(v) < 0) {
                    return false;
                }
            }
            if (knownFacetValues.containsKey(maxInclusive)) {
                BigDecimal v = getNumericFacetValue(maxInclusive);
                BigDecimal input = (BigDecimal) maxInclusive.parseNumber(l);
                if (input.compareTo(v) > 0) {
                    return false;
                }
            }
            if (knownFacetValues.containsKey(maxExclusive)) {
                BigDecimal v = getNumericFacetValue(maxExclusive);
                BigDecimal input = (BigDecimal) maxExclusive.parseNumber(l);
                if (input.compareTo(v) >= 0) {
                    return false;
                }
            }
            return true;
        }
    }

    static class STRING_DATATYPE extends ABSTRACT_DATATYPE<String> {
        public STRING_DATATYPE() {
            this(namespace + "string");
        }

        STRING_DATATYPE(String uri) {
            super(uri, StringFacets);
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.preserve);
        }

        @Override
        public String parseValue(String s) {
            return s;
        }
    }

    static class DATETIMESTAMP_DATATYPE extends DATETIME_DATATYPE {
        DATETIMESTAMP_DATATYPE() {
            super(namespace + "dateTimeStamp");
            ancestors = Utils.generateAncestors(DATETIME);
            // TODO check what's required for this
        }
    }

    static abstract class DECIMAL_DATATYPE<R extends Comparable<R>> extends
            RATIONAL_DATATYPE<R> {
        DECIMAL_DATATYPE() {
            this(namespace + "decimal");
        }

        DECIMAL_DATATYPE(String uri) {
            super(uri, Utils.getFacets(digs, pew, minmax));
            ancestors = Utils.generateAncestors(RATIONAL);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        @Override
        public ordered getOrdered() {
            return ordered.TOTAL;
        }
    }

    static class DOUBLE_DATATYPE extends ABSTRACT_NUMERIC_DATATYPE<Double> {
        DOUBLE_DATATYPE() {
            super(namespace + "double", Utils.getFacets(pew, minmax));
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
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
            return Double.parseDouble(s);
        }

        @Override
        public boolean isCompatible(Datatype<?> type) {
            // implementation from ABSTRACT_DATATYPE
            if (type.isExpression()) {
                type = type.asExpression().getHostType();
            }
            return type.equals(this) || type.equals(DatatypeFactory.LITERAL)
                    || type.isSubType(this) || isSubType(type);
        }
    }

    static class FLOAT_DATATYPE extends ABSTRACT_NUMERIC_DATATYPE<Float> {
        FLOAT_DATATYPE() {
            super(namespace + "float", FACETS4);
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
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
            if (trim.equals("-INF")) {
                return Float.NEGATIVE_INFINITY;
            }
            if (trim.equals("INF")) {
                return Float.POSITIVE_INFINITY;
            }
            return Float.parseFloat(s);
        }

        @Override
        public boolean isCompatible(Datatype<?> type) {
            // implementation from ABSTRACT_DATATYPE
            if (type.isExpression()) {
                type = type.asExpression().getHostType();
            }
            return type.equals(this) || type.equals(DatatypeFactory.LITERAL)
                    || type.isSubType(this) || isSubType(type);
        }
    }

    static abstract class BYTE_DATATYPE<R extends Comparable<R>> extends
            SHORT_DATATYPE<R> {
        BYTE_DATATYPE() {
            super(namespace + "byte");
            ancestors = Utils.generateAncestors(SHORT);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, Byte.MIN_VALUE);
            knownFacetValues.put(maxInclusive, Byte.MAX_VALUE);
        }
    }

    static abstract class INT_DATATYPE<R extends Comparable<R>> extends LONG_DATATYPE<R> {
        INT_DATATYPE() {
            this(namespace + "int");
        }

        INT_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(LONG);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, Integer.MIN_VALUE);
            knownFacetValues.put(maxInclusive, Integer.MAX_VALUE);
        }
    }

    static abstract class INTEGER_DATATYPE<R extends Comparable<R>> extends
            DECIMAL_DATATYPE<R> {
        INTEGER_DATATYPE() {
            this(namespace + "integer");
        }

        INTEGER_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(DECIMAL);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(fractionDigits, 0);
        }
    }

    static abstract class LONG_DATATYPE<R extends Comparable<R>> extends
            INTEGER_DATATYPE<R> {
        LONG_DATATYPE() {
            this(namespace + "long");
        }

        LONG_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(INTEGER);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, Long.MIN_VALUE);
            knownFacetValues.put(maxInclusive, Long.MAX_VALUE);
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

    static abstract class NEGATIVEINTEGER_DATATYPE<R extends Comparable<R>> extends
            NONPOSITIVEINTEGER_DATATYPE<R> {
        NEGATIVEINTEGER_DATATYPE() {
            super(namespace + "negativeInteger");
            ancestors = Utils.generateAncestors(NONPOSITIVEINTEGER);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(maxInclusive, -1);
        }
    }

    static abstract class NONNEGATIVEINTEGER_DATATYPE<R extends Comparable<R>> extends
            INTEGER_DATATYPE<R> {
        NONNEGATIVEINTEGER_DATATYPE() {
            this(namespace + "nonNegativeInteger");
        }

        NONNEGATIVEINTEGER_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(INTEGER);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, 0);
        }
    }

    static abstract class NONPOSITIVEINTEGER_DATATYPE<R extends Comparable<R>> extends
            INTEGER_DATATYPE<R> {
        NONPOSITIVEINTEGER_DATATYPE() {
            this(namespace + "nonPositiveInteger");
        }

        NONPOSITIVEINTEGER_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(INTEGER);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(maxInclusive, 0);
        }
    }

    static abstract class POSITIVEINTEGER_DATATYPE<R extends Comparable<R>> extends
            NONNEGATIVEINTEGER_DATATYPE<R> {
        POSITIVEINTEGER_DATATYPE() {
            super(namespace + "positiveInteger");
            ancestors = Utils.generateAncestors(NONNEGATIVEINTEGER);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, 1);
        }
    }

    static abstract class SHORT_DATATYPE<R extends Comparable<R>> extends INT_DATATYPE<R> {
        SHORT_DATATYPE() {
            this(namespace + "short");
        }

        SHORT_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(INT);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, Short.MIN_VALUE);
            knownFacetValues.put(maxInclusive, Short.MAX_VALUE);
        }
    }

    static abstract class UNSIGNEDBYTE_DATATYPE<R extends Comparable<R>> extends
            UNSIGNEDSHORT_DATATYPE<R> {
        UNSIGNEDBYTE_DATATYPE() {
            super(namespace + "unsignedByte");
            ancestors = Utils.generateAncestors(UNSIGNEDSHORT);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, 0);
            knownFacetValues.put(maxInclusive, 255);
        }
    }

    static abstract class UNSIGNEDINT_DATATYPE<R extends Comparable<R>> extends
            UNSIGNEDLONG_DATATYPE<R> {
        UNSIGNEDINT_DATATYPE() {
            this(namespace + "unsignedInt");
        }

        UNSIGNEDINT_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(UNSIGNEDLONG);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, 0);
            knownFacetValues.put(maxInclusive, 4294967295L);
        }
    }

    static abstract class UNSIGNEDLONG_DATATYPE<R extends Comparable<R>> extends
            NONNEGATIVEINTEGER_DATATYPE<R> {
        UNSIGNEDLONG_DATATYPE() {
            this(namespace + "unsignedLong");
        }

        UNSIGNEDLONG_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(NONNEGATIVEINTEGER);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, 0);
            knownFacetValues.put(maxInclusive, new BigInteger("18446744073709551615"));
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

    static abstract class UNSIGNEDSHORT_DATATYPE<R extends Comparable<R>> extends
            UNSIGNEDINT_DATATYPE<R> {
        UNSIGNEDSHORT_DATATYPE() {
            this(namespace + "unsignedShort");
        }

        UNSIGNEDSHORT_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(UNSIGNEDINT);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            knownFacetValues.put(minInclusive, 0);
            knownFacetValues.put(maxInclusive, 65535);
        }
    }

    static abstract class RATIONAL_DATATYPE<R extends Comparable<R>> extends
            REAL_DATATYPE<R> {
        RATIONAL_DATATYPE(String uri, Set<Facet> f) {
            super(uri, f);
            ancestors = Utils.generateAncestors(REAL);
            knownFacetValues.putAll(super.getKnownFacetValues());
        }

        public RATIONAL_DATATYPE() {
            this("http://www.w3.org/2002/07/owl#rational");
        }

        RATIONAL_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(REAL);
            knownFacetValues.putAll(super.getKnownFacetValues());
        }
    }

    static class LANGUAGE_DATATYPE extends TOKEN_DATATYPE {
        LANGUAGE_DATATYPE() {
            super(namespace + "language");
            ancestors = Utils.generateAncestors(TOKEN);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
        }
    }

    static class NAME_DATATYPE extends TOKEN_DATATYPE {
        public NAME_DATATYPE() {
            this(namespace + "Name");
        }

        NAME_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(TOKEN);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "\\i\\c*");
        }
    }

    static class NCNAME_DATATYPE extends NAME_DATATYPE {
        NCNAME_DATATYPE() {
            super(namespace + "NCName");
            ancestors = Utils.generateAncestors(NAME);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "[\\i-[:]][\\c-[:]]*");
        }

        @Override
        public String getDatatypeURI() {
            return uri;
        }
    }

    static class NMTOKEN_DATATYPE extends TOKEN_DATATYPE {
        NMTOKEN_DATATYPE() {
            this(namespace + "NMTOKEN");
        }

        NMTOKEN_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(TOKEN);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(pattern, "\\c+");
        }

        @Override
        public String getDatatypeURI() {
            return uri;
        }
    }

    static class NMTOKENS_DATATYPE extends NMTOKEN_DATATYPE {
        NMTOKENS_DATATYPE() {
            super(namespace + "NMTOKENS");
            ancestors = Utils.generateAncestors(NMTOKEN);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
            knownFacetValues.put(minLength, 1);
        }
    }

    static class NORMALIZEDSTRING_DATATYPE extends STRING_DATATYPE {
        public NORMALIZEDSTRING_DATATYPE() {
            this(namespace + "normalizedString");
        }

        NORMALIZEDSTRING_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(STRING);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.replace);
        }
    }

    static class TOKEN_DATATYPE extends NORMALIZEDSTRING_DATATYPE {
        TOKEN_DATATYPE() {
            this(namespace + "token");
        }

        TOKEN_DATATYPE(String uri) {
            super(uri);
            ancestors = Utils.generateAncestors(NORMALIZEDSTRING);
            knownFacetValues.putAll(super.getKnownFacetValues());
            knownFacetValues.put(whiteSpace, whitespace.collapse);
        }
    }

    static class XMLLITERAL_DATATYPE extends ABSTRACT_DATATYPE<String> {
        XMLLITERAL_DATATYPE() {
            super("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral", Collections
                    .<Facet> emptySet());
            ancestors = Utils.generateAncestors(LITERAL);
            knownFacetValues.putAll(LITERAL.getKnownFacetValues());
        }

        public String parseValue(String s) {
            // XXX sort of arbitrary decision; the specs say it depends on the
            // XML datatype whitespace normalization policy, but that's not
            // clear. Some W3C tests assume that text elements are irrelevant
            return whitespace.collapse.normalize(s);
        }

        @Override
        public boolean isInValueSpace(String l) {
            try {
                DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new ByteArrayInputStream(l.getBytes()));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
}
