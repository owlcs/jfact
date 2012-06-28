package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.datatypes.Facets.whitespace;

public final class DatatypeFactory {
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
        public BigDecimal parseValue(final String s) {
            return new BigDecimal(s);
        }
    };
    public static final Datatype<BigDecimal> RATIONAL = new RATIONAL_DATATYPE<BigDecimal>() {
        public BigDecimal parseValue(final String s) {
            return new BigDecimal(s);
        }
    };
    public static final Datatype<Calendar> DATETIMESTAMP = new DATETIMESTAMP_DATATYPE();
    public static final Datatype<BigDecimal> DECIMAL = new DECIMAL_DATATYPE<BigDecimal>() {
        public BigDecimal parseValue(final String s) {
            return new BigDecimal(s);
        }
    };
    public static final Datatype<BigInteger> INTEGER = new INTEGER_DATATYPE<BigInteger>() {
        public BigInteger parseValue(final String s) {
            return new BigInteger(s);
        }
    };
    public static final Datatype<Double> DOUBLE = new DOUBLE_DATATYPE();
    public static final Datatype<Float> FLOAT = new FLOAT_DATATYPE();
    public static final Datatype<BigInteger> NONPOSITIVEINTEGER = new NONPOSITIVEINTEGER_DATATYPE<BigInteger>() {
        public BigInteger parseValue(final String s) {
            final BigInteger parse = new BigInteger(s);
            if (parse.compareTo(BigInteger.ZERO) > 0) {
                throw new ArithmeticException(
                        "Non positive integer required, but found: " + s);
            }
            return parse;
        }
    };
    public static final Datatype<BigInteger> NEGATIVEINTEGER = new NEGATIVEINTEGER_DATATYPE<BigInteger>() {
        public BigInteger parseValue(final String s) {
            final BigInteger parse = new BigInteger(s);
            if (parse.compareTo(new BigInteger("-1")) > 0) {
                throw new ArithmeticException("Negative integer required, but found: "
                        + s);
            }
            return parse;
        }
    };
    public static final Datatype<BigInteger> NONNEGATIVEINTEGER = new NONNEGATIVEINTEGER_DATATYPE<BigInteger>() {
        public BigInteger parseValue(final String s) {
            final BigInteger parseValue = new BigInteger(s);
            if (parseValue.compareTo(BigInteger.ZERO) < 0) {
                throw new ArithmeticException(
                        "Non negative integer required, but found: " + s);
            }
            return parseValue;
        }
    };
    public static final Datatype<BigInteger> POSITIVEINTEGER = new POSITIVEINTEGER_DATATYPE<BigInteger>() {
        public BigInteger parseValue(final String s) {
            final BigInteger parseValue = new BigInteger(s);
            if (parseValue.compareTo(BigInteger.ZERO) <= 0) {
                throw new ArithmeticException("Positive integer required, but found: "
                        + s);
            }
            return parseValue;
        }
    };
    public static final Datatype<Long> LONG = new LONG_DATATYPE<Long>() {
        public Long parseValue(final String s) {
            return Long.parseLong(s);
        }
    };
    public static final Datatype<Integer> INT = new INT_DATATYPE<Integer>() {
        public Integer parseValue(final String s) {
            return Integer.parseInt(s);
        }
    };
    public static final Datatype<Short> SHORT = new SHORT_DATATYPE<Short>() {
        public Short parseValue(final String s) {
            return Short.parseShort(s);
        }
    };
    public static final Datatype<Byte> BYTE = new BYTE_DATATYPE<Byte>() {
        public Byte parseValue(final String s) {
            return Byte.parseByte(s);
        }
    };
    public static final Datatype<BigInteger> UNSIGNEDLONG = new UNSIGNEDLONG_DATATYPE<BigInteger>() {
        public BigInteger parseValue(final String s) {
            BigInteger b = new BigInteger(s);
            if (b.compareTo(BigInteger.ZERO) < 0) {
                throw new ArithmeticException("Unsigned long required, but found: " + s);
            }
            return b;
        }
    };
    public static final Datatype<Long> UNSIGNEDINT = new UNSIGNEDINT_DATATYPE<Long>() {
        public Long parseValue(final String s) {
            final Long parseInt = Long.parseLong(s);
            if (parseInt < 0) {
                throw new ArithmeticException("Unsigned int required, but found: " + s);
            }
            return parseInt;
        }
    };
    public static final Datatype<Integer> UNSIGNEDSHORT = new UNSIGNEDSHORT_DATATYPE<Integer>() {
        public Integer parseValue(final String s) {
            final Integer parseShort = Integer.parseInt(s);
            if (parseShort < 0) {
                throw new ArithmeticException("Unsigned short required, but found: " + s);
            }
            return parseShort;
        }
    };
    public static final Datatype<Short> UNSIGNEDBYTE = new UNSIGNEDBYTE_DATATYPE<Short>() {
        public Short parseValue(final String s) {
            final Short parseByte = Short.parseShort(s);
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
        return new ArrayList<Datatype<?>>(this.knownDatatypes.values());
    }

    private final Map<String, Datatype<?>> knownDatatypes = new HashMap<String, Datatype<?>>();
    private static int uri_index = 0;

    static final int getIndex() {
        return uri_index++;
    }

    private DatatypeFactory() {
        for (Datatype<?> d : values) {
            this.knownDatatypes.put(d.getDatatypeURI(), d);
        }
        // XXX handle dates as datetimes
        this.knownDatatypes.put("http://www.w3.org/2001/XMLSchema#date", DATETIME);
    }

    public Datatype<?> getKnownDatatype(final String key) {
        return this.knownDatatypes.get(key);
    }

    public boolean isKnownDatatype(final String key) {
        return this.knownDatatypes.containsKey(key);
    }

    public static DatatypeFactory getInstance() {
        return new DatatypeFactory();
    }

    //	public <O extends Comparable<O>> Datatype<O> defineNewDatatype(
    //			final Datatype<O> base, final String uri, final Collection<Facet> facets,
    //			final Collection<Datatype<?>> ancestors, final Map<Facet, Object> knownFacets) {
    //		return defineNewDatatype(base, uri, facets, ancestors, knownFacets,
    //				base.getOrdered(), base.getNumeric(), base.getCardinality(),
    //				base.getBounded());
    //	}
    //
    //	public <O extends Comparable<O>> Datatype<O> defineNewDatatype(
    //			final Datatype<O> base, final String uri) {
    //		return defineNewDatatype(base, uri, null, null, null, base.getOrdered(),
    //				base.getNumeric(), base.getCardinality(), base.getBounded());
    //	}
    //
    //	public Datatype<String> defineNewDatatype(final String uri) {
    //		return defineNewDatatype(LITERAL, uri, null, null, null, LITERAL.getOrdered(),
    //				LITERAL.getNumeric(), LITERAL.getCardinality(), LITERAL.getBounded());
    //	}
    //
    //	public <R extends Comparable<R>> DatatypeExpression<R> defineNewDatatypeIntersection(
    //			Collection<Datatype<R>> types) {
    //		return new DatatypeIntersection<R>(getIndex(), types);
    //	}
    //
    //	public <R extends Comparable<R>> DatatypeExpression<R> defineNewDatatypeUnion(
    //			Collection<Datatype<R>> types) {
    //		return new DatatypeUnion<R>(getIndex(), types);
    //	}
    //
    //	public <O extends Comparable<O>> Datatype<O> defineNewDatatype(
    //			final Datatype<O> base, final String uri, final Collection<Facet> facets,
    //			final Collection<Datatype<?>> ancestors,
    //			final Map<Facet, Object> knownFacets, final ordered ord,
    //			final boolean numeric, final cardinality card, final boolean bound) {
    //		if (knownDatatypes.containsKey(uri)) { throw new IllegalArgumentException(
    //				"datatype definitions cannot be overridden: " + uri
    //						+ " is already in the known types"); }
    //		final Set<Facet> f = new HashSet<Facet>(base.getFacets());
    //		if (facets != null) {
    //			f.addAll(facets);
    //		}
    //		final Set<Datatype<?>> a = new HashSet<Datatype<?>>(base.getAncestors());
    //		if (ancestors != null) {
    //			a.addAll(ancestors);
    //		}
    //		a.add(base);
    //		final Map<Facet, Object> known = new HashMap<Facet, Object>(
    //				base.getKnownFacetValues());
    //		if (knownFacets != null) {
    //			known.putAll(knownFacets);
    //		}
    //		Datatype<O> toReturn = new ABSTRACT_DATATYPE<O>(uri, f) {
    //			@Override
    //			public ordered getOrdered() {
    //				return ord;
    //			}
    //
    //			@Override
    //			public boolean getNumeric() {
    //				return numeric;
    //			}
    //
    //			@Override
    //			public cardinality getCardinality() {
    //				return card;
    //			}
    //
    //			@Override
    //			public boolean getBounded() {
    //				return bound;
    //			}
    //
    //			@Override
    //			public boolean isInValueSpace(O l) {
    //				return base.isInValueSpace(l);
    //			}
    //
    //			@Override
    //			public Collection<Literal<O>> listValues() {
    //				return base.listValues();
    //			}
    //
    //			public O parseValue(String s) {
    //				return base.parseValue(s);
    //			}
    //
    //			@Override
    //			public Literal<O> buildLiteral(String s) {
    //				return base.buildLiteral(s);
    //			}
    //		};
    //		knownDatatypes.put(uri, toReturn);
    //		return toReturn;
    //	}
    public static final boolean nonEmptyInterval(final BigDecimal min,
            final BigDecimal max, final int excluded) {
        if (min == null) {
            // unbound lower limit - value space cannot be empty
            // even if the actual type used to represent the literal is bounded, the limit should explicitly be there.
            return false;
        }
        if (max == null) {
            // unbound upper limit - value space cannot be empty
            // even if the actual type used to represent the literal is bounded, the limit should explicitly be there.
            return false;
        }
        // min and max are both not null
        int comparison = min.compareTo(max);
        // comparison < 0: min is strictly smaller than max. Value space can still be empty:
        // (1,2)^^integer has no values
        // if excluded is 0, comparison <=0 is enough to return true; there would still be one element: [1,1]
        if (excluded == 0) {
            return comparison <= 0;
        }
        // if excluded is 1, then comparison <0 is required
        if (excluded == 1) {
            return comparison < 0;
        }
        // if excluded is 2, then min + 1 unit must be strictly smaller than max; this becomes type dependent since it depends on the representation
        if (excluded == 2) {
            return min.add(min.ulp()).compareTo(max) < 0;
        }
        return false;
    }

    public static final <R extends Comparable<R>> DatatypeExpression<R> getDatatypeExpression(
            final Datatype<R> base) {
        return new DatatypeExpressionImpl<R>(base);
    }

    public static final <R extends Comparable<R>> DatatypeExpression<R> getNumericDatatypeExpression(
            final NumericDatatype<R> base) {
        return new DatatypeNumericExpressionImpl<R>(base);
    }

    public static final <R extends Comparable<R>> DatatypeExpression<R> getOrderedDatatypeExpression(
            final Datatype<R> base) {
        return new DatatypeOrderedExpressionImpl<R>(base);
    }

    static abstract class ABSTRACT_NUMERIC_DATATYPE<R extends Comparable<R>> extends
            ABSTRACT_DATATYPE<R> implements NumericDatatype<R> {
        public ABSTRACT_NUMERIC_DATATYPE(final String uri, final Set<Facet> f) {
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
        public boolean isInValueSpace(final R l) {
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
        public boolean isCompatible(final Datatype<?> type) {
            //			if (!super.isCompatible(type)) {
            //				return false;
            //			}
            if (type.equals(LITERAL)) {
                return true;
            }
            //			if(isSubType(type)||type.isSubType(this)) {
            //				return true;
            //			}
            if (type.getNumeric()) {
                // specific cases: float and double have overlapping value spaces with all numerics but are not compatible with any
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
                // if both have no max or both have no min -> there is an overlap
                // if one has no max, then min must be smaller than max of the other
                // if one has no min, the max must be larger than min of the other
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
                //disjoint if:
                // exclusives:
                // one minInclusive/exclusive is strictly larger than the other maxinclusive/exclusive
                return this.overlapping(this, wrapper) || this.overlapping(wrapper, this);
            } else {
                return false;
            }
        }

        <O extends Comparable<O>> NumericDatatype<O> wrap(final Datatype<O> d) {
            return new NumericDatatypeWrapper<O>(d);
        }

        public boolean hasMinExclusive() {
            return this.knownFacetValues.containsKey(minExclusive);
        }

        public boolean hasMinInclusive() {
            return this.knownFacetValues.containsKey(minInclusive);
        }

        public boolean hasMaxExclusive() {
            return this.knownFacetValues.containsKey(maxExclusive);
        }

        public boolean hasMaxInclusive() {
            return this.knownFacetValues.containsKey(maxInclusive);
        }

        public boolean hasMin() {
            return this.hasMinInclusive() || this.hasMinExclusive();
        }

        public boolean hasMax() {
            return this.hasMaxInclusive() || this.hasMaxExclusive();
        }

        public BigDecimal getMin() {
            if (this.hasMinExclusive()) {
                return (BigDecimal) minExclusive.parseNumber(this.knownFacetValues
                        .get(minExclusive));
            }
            if (this.hasMinInclusive()) {
                return (BigDecimal) minInclusive.parseNumber(this.knownFacetValues
                        .get(minInclusive));
            }
            return null;
        }

        public BigDecimal getMax() {
            if (this.hasMaxExclusive()) {
                return (BigDecimal) maxExclusive.parseNumber(this.knownFacetValues
                        .get(maxExclusive));
            }
            if (this.hasMaxInclusive()) {
                return (BigDecimal) maxInclusive.parseNumber(this.knownFacetValues
                        .get(maxInclusive));
            }
            return null;
        }
    }

    static class ANYURI_DATATYPE extends ABSTRACT_DATATYPE<String> {
        ANYURI_DATATYPE() {
            super(namespace + "anyURI", StringFacets);
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        public String parseValue(final String s) {
            return whitespace.collapse.normalize(s);
        }

        @Override
        public boolean isInValueSpace(final String l) {
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
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        public String parseValue(final String s) {
            return whitespace.collapse.normalize(s);
        }

        @Override
        public boolean isInValueSpace(final String s) {
            // all characters are letters, numbers, or +/=
            for (int i = 0; i < s.length(); i++) {
                final char c = s.charAt(i);
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
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        @Override
        public cardinality getCardinality() {
            return cardinality.FINITE;
        }

        @Override
        public Collection<Literal<Boolean>> listValues() {
            // if all datatypes are compatible, the intersection is the two booleans minu any restriction
            List<Literal<Boolean>> toReturn = new ArrayList<Literal<Boolean>>(2);
            toReturn.add(this.buildLiteral(Boolean.toString(true)));
            toReturn.add(this.buildLiteral(Boolean.toString(false)));
            return toReturn;
        }

        public Boolean parseValue(final String s) {
            whitespace facet = (whitespace) whiteSpace.parse(this.knownFacetValues
                    .get(whiteSpace));
            return Boolean.parseBoolean(facet.normalize(s));
        }
    }

    static class DATETIME_DATATYPE extends ABSTRACT_DATATYPE<Calendar> implements
            OrderedDatatype<Calendar> {
        DATETIME_DATATYPE() {
            this(namespace + "dateTime");
        }

        DATETIME_DATATYPE(final String u) {
            super(u, FACETS4);
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
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

        public Calendar parseValue(final String s) {
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
        public boolean isInValueSpace(final Calendar l) {
            if (this.hasMinExclusive()) {
                if (this.getMin().compareTo(l) <= 0) {
                    return false;
                }
            }
            if (this.hasMinInclusive()) {
                if (this.getMin().compareTo(l) < 0) {
                    return false;
                }
            }
            if (this.hasMaxExclusive()) {
                if (this.getMax().compareTo(l) >= 0) {
                    return false;
                }
            }
            if (this.hasMaxInclusive()) {
                if (this.getMax().compareTo(l) > 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isCompatible(final Datatype<?> type) {
            if (super.isCompatible(type)) {
                return true;
            }
            if (type.isSubType(this)) {
                // then its representation must be Calendars
                OrderedDatatype<Calendar> wrapper = (OrderedDatatype<Calendar>) type;
                // then both types are numeric
                // if both have no max or both have no min -> there is an overlap
                // if one has no max, then min must be smaller than max of the other
                // if one has no min, the max must be larger than min of the other
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
                //disjoint if:
                // exclusives:
                // one minInclusive/exclusive is strictly larger than the other maxinclusive/exclusive
                return this.overlapping(this, wrapper) || this.overlapping(wrapper, this);
            } else {
                return false;
            }
        }

        public boolean hasMinExclusive() {
            return this.knownFacetValues.containsKey(minExclusive);
        }

        public boolean hasMinInclusive() {
            return this.knownFacetValues.containsKey(minInclusive);
        }

        public boolean hasMaxExclusive() {
            return this.knownFacetValues.containsKey(maxExclusive);
        }

        public boolean hasMaxInclusive() {
            return this.knownFacetValues.containsKey(maxInclusive);
        }

        public boolean hasMin() {
            return this.hasMinInclusive() || this.hasMinExclusive();
        }

        public boolean hasMax() {
            return this.hasMaxInclusive() || this.hasMaxExclusive();
        }

        public Calendar getMin() {
            if (this.hasMinExclusive()) {
                return (Calendar) this.getFacetValue(minExclusive);
            }
            if (this.hasMinInclusive()) {
                return (Calendar) this.getFacetValue(minInclusive);
            }
            return null;
        }

        public Calendar getMax() {
            if (this.hasMaxExclusive()) {
                return (Calendar) this.getFacetValue(maxExclusive);
            }
            if (this.hasMaxInclusive()) {
                return (Calendar) this.getFacetValue(maxInclusive);
            }
            return null;
        }
    }

    static class HEXBINARY_DATATYPE extends ABSTRACT_DATATYPE<String> {
        HEXBINARY_DATATYPE() {
            super(namespace + "hexBinary", StringFacets);
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        public String parseValue(final String s) {
            return whitespace.collapse.normalize(s);
        }

        @Override
        public boolean isInValueSpace(final String s) {
            // all characters are numbers, or ABCDEF
            for (int i = 0; i < s.length(); i++) {
                final char c = s.charAt(i);
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
            this.ancestors = Collections.emptySet();
        }

        public String parseValue(final String s) {
            return s;
        }
    }

    static class PLAINLITERAL_DATATYPE extends ABSTRACT_DATATYPE<String> {
        PLAINLITERAL_DATATYPE() {
            super("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral", Utils
                    .getFacets(length, minLength, maxLength, pattern, enumeration));
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
        }

        public String parseValue(final String s) {
            return s;
        }
    }

    abstract static class REAL_DATATYPE<R extends Comparable<R>> extends
            ABSTRACT_NUMERIC_DATATYPE<R> {
        public REAL_DATATYPE() {
            this("http://www.w3.org/2002/07/owl#real");
        }

        REAL_DATATYPE(final String uri) {
            this(uri, Utils.getFacets(minmax));
        }

        REAL_DATATYPE(final String uri, final Set<Facet> f) {
            super(uri, f);
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
        }

        @Override
        public boolean isInValueSpace(final R l) {
            if (this.knownFacetValues.containsKey(minExclusive)) {
                BigDecimal v = this.getNumericFacetValue(minExclusive);
                BigDecimal input = (BigDecimal) minExclusive.parseNumber(l);
                if (input.compareTo(v) <= 0) {
                    return false;
                }
            }
            if (this.knownFacetValues.containsKey(minInclusive)) {
                BigDecimal v = this.getNumericFacetValue(minInclusive);
                BigDecimal input = (BigDecimal) minInclusive.parseNumber(l);
                if (input.compareTo(v) < 0) {
                    return false;
                }
            }
            if (this.knownFacetValues.containsKey(maxInclusive)) {
                BigDecimal v = this.getNumericFacetValue(maxInclusive);
                BigDecimal input = (BigDecimal) maxInclusive.parseNumber(l);
                if (input.compareTo(v) > 0) {
                    return false;
                }
            }
            if (this.knownFacetValues.containsKey(maxExclusive)) {
                BigDecimal v = this.getNumericFacetValue(maxExclusive);
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

        STRING_DATATYPE(final String uri) {
            super(uri, StringFacets);
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.preserve);
        }

        public String parseValue(final String s) {
            return s;
        }
    }

    static class DATETIMESTAMP_DATATYPE extends DATETIME_DATATYPE {
        DATETIMESTAMP_DATATYPE() {
            super(namespace + "dateTimeStamp");
            this.ancestors = Utils.generateAncestors(DATETIME);
            //TODO check what's required for this
        }
    }

    static abstract class DECIMAL_DATATYPE<R extends Comparable<R>> extends
            RATIONAL_DATATYPE<R> {
        DECIMAL_DATATYPE() {
            this(namespace + "decimal");
        }

        DECIMAL_DATATYPE(final String uri) {
            super(uri, Utils.getFacets(digs, pew, minmax));
            this.ancestors = Utils.generateAncestors(RATIONAL);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
        }

        @Override
        public ordered getOrdered() {
            return ordered.TOTAL;
        }
    }

    static class DOUBLE_DATATYPE extends ABSTRACT_NUMERIC_DATATYPE<Double> {
        DOUBLE_DATATYPE() {
            super(namespace + "double", Utils.getFacets(pew, minmax));
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
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

        public Double parseValue(final String s) {
            return Double.parseDouble(s);
        }

        @Override
        public boolean isCompatible(Datatype<?> type) {
            // implementation from ABSTRACT_DATATYPE
            if (type.isExpression()) {
                type = type.asExpression().getHostType();
            }
            return type.equals(this) || type.equals(DatatypeFactory.LITERAL)
                    || type.isSubType(this) || this.isSubType(type);
        }
    }

    static class FLOAT_DATATYPE extends ABSTRACT_NUMERIC_DATATYPE<Float> {
        FLOAT_DATATYPE() {
            super(namespace + "float", FACETS4);
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
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

        public Float parseValue(final String s) {
            final String trim = s.trim();
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
                    || type.isSubType(this) || this.isSubType(type);
        }
    }

    static abstract class BYTE_DATATYPE<R extends Comparable<R>> extends
            SHORT_DATATYPE<R> {
        BYTE_DATATYPE() {
            super(namespace + "byte");
            this.ancestors = Utils.generateAncestors(SHORT);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, Byte.MIN_VALUE);
            this.knownFacetValues.put(maxInclusive, Byte.MAX_VALUE);
        }
    }

    static abstract class INT_DATATYPE<R extends Comparable<R>> extends LONG_DATATYPE<R> {
        INT_DATATYPE() {
            this(namespace + "int");
        }

        INT_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(LONG);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, Integer.MIN_VALUE);
            this.knownFacetValues.put(maxInclusive, Integer.MAX_VALUE);
        }
    }

    static abstract class INTEGER_DATATYPE<R extends Comparable<R>> extends
            DECIMAL_DATATYPE<R> {
        INTEGER_DATATYPE() {
            this(namespace + "integer");
        }

        INTEGER_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(DECIMAL);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(fractionDigits, 0);
        }
    }

    static abstract class LONG_DATATYPE<R extends Comparable<R>> extends
            INTEGER_DATATYPE<R> {
        LONG_DATATYPE() {
            this(namespace + "long");
        }

        LONG_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(INTEGER);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, Long.MIN_VALUE);
            this.knownFacetValues.put(maxInclusive, Long.MAX_VALUE);
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
            this.ancestors = Utils.generateAncestors(NONPOSITIVEINTEGER);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(maxInclusive, -1);
        }
    }

    static abstract class NONNEGATIVEINTEGER_DATATYPE<R extends Comparable<R>> extends
            INTEGER_DATATYPE<R> {
        NONNEGATIVEINTEGER_DATATYPE() {
            this(namespace + "nonNegativeInteger");
        }

        NONNEGATIVEINTEGER_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(INTEGER);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, 0);
        }
    }

    static abstract class NONPOSITIVEINTEGER_DATATYPE<R extends Comparable<R>> extends
            INTEGER_DATATYPE<R> {
        NONPOSITIVEINTEGER_DATATYPE() {
            this(namespace + "nonPositiveInteger");
        }

        NONPOSITIVEINTEGER_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(INTEGER);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(maxInclusive, 0);
        }
    }

    static abstract class POSITIVEINTEGER_DATATYPE<R extends Comparable<R>> extends
            NONNEGATIVEINTEGER_DATATYPE<R> {
        POSITIVEINTEGER_DATATYPE() {
            super(namespace + "positiveInteger");
            this.ancestors = Utils.generateAncestors(NONNEGATIVEINTEGER);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, 1);
        }
    }

    static abstract class SHORT_DATATYPE<R extends Comparable<R>> extends INT_DATATYPE<R> {
        SHORT_DATATYPE() {
            this(namespace + "short");
        }

        SHORT_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(INT);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, Short.MIN_VALUE);
            this.knownFacetValues.put(maxInclusive, Short.MAX_VALUE);
        }
    }

    static abstract class UNSIGNEDBYTE_DATATYPE<R extends Comparable<R>> extends
            UNSIGNEDSHORT_DATATYPE<R> {
        UNSIGNEDBYTE_DATATYPE() {
            super(namespace + "unsignedByte");
            this.ancestors = Utils.generateAncestors(UNSIGNEDSHORT);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, 0);
            this.knownFacetValues.put(maxInclusive, 255);
        }
    }

    static abstract class UNSIGNEDINT_DATATYPE<R extends Comparable<R>> extends
            UNSIGNEDLONG_DATATYPE<R> {
        UNSIGNEDINT_DATATYPE() {
            this(namespace + "unsignedInt");
        }

        UNSIGNEDINT_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(UNSIGNEDLONG);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, 0);
            this.knownFacetValues.put(maxInclusive, 4294967295L);
        }
    }

    static abstract class UNSIGNEDLONG_DATATYPE<R extends Comparable<R>> extends
            NONNEGATIVEINTEGER_DATATYPE<R> {
        UNSIGNEDLONG_DATATYPE() {
            this(namespace + "unsignedLong");
        }

        UNSIGNEDLONG_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(NONNEGATIVEINTEGER);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, 0);
            this.knownFacetValues.put(maxInclusive,
                    new BigInteger("18446744073709551615"));
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

        UNSIGNEDSHORT_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(UNSIGNEDINT);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\-+]?[0-9]+");
            this.knownFacetValues.put(minInclusive, 0);
            this.knownFacetValues.put(maxInclusive, 65535);
        }
    }

    static abstract class RATIONAL_DATATYPE<R extends Comparable<R>> extends
            REAL_DATATYPE<R> {
        RATIONAL_DATATYPE(final String uri, final Set<Facet> f) {
            super(uri, f);
            this.ancestors = Utils.generateAncestors(REAL);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
        }

        public RATIONAL_DATATYPE() {
            this("http://www.w3.org/2002/07/owl#rational");
        }

        RATIONAL_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(REAL);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
        }
    }

    static class LANGUAGE_DATATYPE extends TOKEN_DATATYPE {
        LANGUAGE_DATATYPE() {
            super(namespace + "language");
            this.ancestors = Utils.generateAncestors(TOKEN);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*");
        }
    }

    static class NAME_DATATYPE extends TOKEN_DATATYPE {
        public NAME_DATATYPE() {
            this(namespace + "Name");
        }

        NAME_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(TOKEN);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "\\i\\c*");
        }
    }

    static class NCNAME_DATATYPE extends NAME_DATATYPE {
        NCNAME_DATATYPE() {
            super(namespace + "NCName");
            this.ancestors = Utils.generateAncestors(NAME);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "[\\i-[:]][\\c-[:]]*");
        }

        @Override
        public String getDatatypeURI() {
            return this.uri;
        }
    }

    static class NMTOKEN_DATATYPE extends TOKEN_DATATYPE {
        NMTOKEN_DATATYPE() {
            this(namespace + "NMTOKEN");
        }

        NMTOKEN_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(TOKEN);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(pattern, "\\c+");
        }

        @Override
        public String getDatatypeURI() {
            return this.uri;
        }
    }

    static class NMTOKENS_DATATYPE extends NMTOKEN_DATATYPE {
        NMTOKENS_DATATYPE() {
            super(namespace + "NMTOKENS");
            this.ancestors = Utils.generateAncestors(NMTOKEN);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
            this.knownFacetValues.put(minLength, 1);
        }
    }

    static class NORMALIZEDSTRING_DATATYPE extends STRING_DATATYPE {
        public NORMALIZEDSTRING_DATATYPE() {
            this(namespace + "normalizedString");
        }

        NORMALIZEDSTRING_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(STRING);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.replace);
        }
    }

    static class TOKEN_DATATYPE extends NORMALIZEDSTRING_DATATYPE {
        TOKEN_DATATYPE() {
            this(namespace + "token");
        }

        TOKEN_DATATYPE(final String uri) {
            super(uri);
            this.ancestors = Utils.generateAncestors(NORMALIZEDSTRING);
            this.knownFacetValues.putAll(super.getKnownFacetValues());
            this.knownFacetValues.put(whiteSpace, whitespace.collapse);
        }
    }

    static class XMLLITERAL_DATATYPE extends ABSTRACT_DATATYPE<String> {
        XMLLITERAL_DATATYPE() {
            super("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral", Collections
                    .<Facet> emptySet());
            this.ancestors = Utils.generateAncestors(LITERAL);
            this.knownFacetValues.putAll(LITERAL.getKnownFacetValues());
        }

        public String parseValue(final String s) {
            // XXX sort of arbitrary decision; the specs say it depends on the XML datatype whitespace normalization policy, but that's not clear. Some W3C tests assume that text elements are irrelevant
            return whitespace.collapse.normalize(s);
        }

        @Override
        public boolean isInValueSpace(final String l) {
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
