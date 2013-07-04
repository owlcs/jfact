package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.vocab.OWLFacet;

@SuppressWarnings("javadoc")
public class Facets {
    private static abstract class AbstractFacet implements Facet {
        final String uri;
        final String fragment;

        public AbstractFacet(String u) {
            uri = DatatypeFactory.namespace + u;
            fragment = u;
        }

        @Override
        public String getURI() {
            return uri;
        }

        @Override
        public String toString() {
            return "facet[" + fragment + "]";
        }

        @Override
        public int hashCode() {
            return uri.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            }
            if (obj instanceof Facet) {
                return uri.equals(((Facet) obj).getURI());
            }
            return false;
        }

        @Override
        public Comparable parseNumber(Object value) {
            if (!isNumberFacet()) {
                throw new UnsupportedOperationException(
                        "Only number facets can parse numbers");
            }
            if (value == null) {
                throw new IllegalArgumentException("Cannot parse a null value");
            }
            if (value instanceof Comparable) {
                return (Comparable) value;
            }
            try {
                if (value instanceof String) {
                    return new BigDecimal((String) value);
                }
                if (value instanceof Literal) {
                    Object typedValue = ((Literal<?>) value).typedValue();
                    if (typedValue instanceof Number) {
                        return (Comparable) typedValue;
                    }
                    if (typedValue instanceof Calendar) {
                        return (Comparable) typedValue;
                    }
                    // if (typedValue instanceof Number) {
                    // return new BigDecimal(typedValue.toString());
                    // }
                    // else it's not parseable anyway...
                }
                // any other type, its string form must be parseable by
                // BigDecimal - or exceptions will be spat out
                return new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Cannot parse '" + value.toString()
                        + "' as a big decimal");
            }
        }

        @Override
        public Comparable parse(Object o) {
            return (Comparable) o;
        }
    }

    private static class LimitFacet extends AbstractFacet {
        public LimitFacet(String u) {
            super(u);
        }

        @Override
        public boolean isNumberFacet() {
            return true;
        }
    }

    public enum whitespace {
        // 4.3.6 whiteSpace
        /*
         * [Definition:] whiteSpace constrains the ·value space· of types
         * ·derived· from string such that the various behaviors specified in
         * Attribute Value Normalization in [XML 1.0 (Second Edition)] are
         * realized. The value of whiteSpace must be one of {preserve, replace,
         * collapse}.
         */
        // preserve
        /*
         * No normalization is done, the value is not changed (this is the
         * behavior required by [XML 1.0 (Second Edition)] for element content)
         */
        // replace
        /*
         * All occurrences of #x9 (tab), #xA (line feed) and #xD (carriage
         * return) are replaced with #x20 (space)
         */
        // collapse
        /*
         * After the processing implied by replace, contiguous sequences of
         * #x20's are collapsed to a single #x20, and leading and trailing
         * #x20's are removed.
         */
        /*
         * NOTE: The notation #xA used here (and elsewhere in this
         * specification) represents the Universal Character Set (UCS) code
         * point hexadecimal A (line feed), which is denoted by U+000A. This
         * notation is to be distinguished from &#xA;, which is the XML
         * character reference to that same UCS code point.
         */
        preserve {
            @Override
            public String normalize(String input) {
                return input;
            }
        },
        replace {
            @Override
            public String normalize(String input) {
                return input.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
            }
        },
        collapse {
            @Override
            public String normalize(String input) {
                StringBuilder b = new StringBuilder(replace.normalize(input));
                for (int i = 0; i < b.length(); i++) {
                    if (b.charAt(i) == ' ') {
                        while (i < b.length() - 1 && b.charAt(i + 1) == ' ') {
                            b.deleteCharAt(i + 1);
                        }
                    }
                }
                return b.toString();
            }
        };
        public abstract String normalize(String input);
    }

    public static Facet length = new LimitFacet("length");
    public static Facet minLength = new LimitFacet("minLength");
    public static Facet maxLength = new LimitFacet("maxLength");
    public static Facet totalDigits = new LimitFacet("totalDigits");
    public static Facet fractionDigits = new LimitFacet("fractionDigits");
    public static Facet whiteSpace = new AbstractFacet("whiteSpace") {
        @Override
        public boolean isNumberFacet() {
            return false;
        }

        @Override
        public whitespace parse(Object value) {
            if (value instanceof whitespace) {
                return (whitespace) value;
            }
            if (value instanceof String) {
                return whitespace.valueOf((String) value);
            }
            throw new ReasonerInternalException("Cannot parse " + value
                    + " as a whitespace enum value");
        }
    };
    public static Facet pattern = new AbstractFacet("pattern") {
        @Override
        public boolean isNumberFacet() {
            return false;
        }

        @Override
        public String parse(Object value) {
            return value.toString();
        }
    };
    public static Facet enumeration = new AbstractFacet("enumeration") {
        @Override
        public boolean isNumberFacet() {
            return false;
        }
    };
    public static Facet maxInclusive = new LimitFacet("maxInclusive") {
        @Override
        public String toString() {
            return "]";
        }
    };
    public static Facet maxExclusive = new LimitFacet("maxExclusive") {
        @Override
        public String toString() {
            return ")";
        }
    };
    public static Facet minInclusive = new LimitFacet("minInclusive") {
        @Override
        public String toString() {
            return "[";
        }
    };
    public static Facet minExclusive = new LimitFacet("minExclusive") {
        @Override
        public String toString() {
            return "(";
        }
    };

    public static List<Facet> values() {
        return new ArrayList<Facet>(values);
    }

    private static final List<Facet> values = Arrays.asList(enumeration, fractionDigits,
            length, maxExclusive, maxInclusive, minExclusive, minInclusive, maxLength,
            minLength, pattern, totalDigits, whiteSpace);

    public static Facet parse(OWLFacet f) {
        switch (f) {
            case LENGTH:
                return length;
            case MIN_LENGTH:
                return minLength;
            case MAX_LENGTH:
                return maxLength;
            case PATTERN:
                return pattern;
            case MIN_INCLUSIVE:
                return minInclusive;
            case MIN_EXCLUSIVE:
                return minExclusive;
            case MAX_INCLUSIVE:
                return maxInclusive;
            case MAX_EXCLUSIVE:
                return maxExclusive;
            case TOTAL_DIGITS:
                return totalDigits;
            case FRACTION_DIGITS:
                return fractionDigits;
            default:
                throw new OWLRuntimeException("Unsupported facet: " + f);
        }
    }

    public static Facet parse(String f) {
        f = f.substring(f.indexOf(':') + 1);
        for (Facet facet : values) {
            if (facet.getURI().endsWith("#" + f)) {
                return facet;
            }
        }
        throw new OWLRuntimeException("Unsupported facet: " + f);
    }
}
