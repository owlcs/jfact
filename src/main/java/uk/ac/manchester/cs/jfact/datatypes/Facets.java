package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static org.semanticweb.owlapi.vocab.OWLFacet.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * facets
 * 
 * @author ignazio
 */
public class Facets implements Serializable {

    private static class AbstractFacet implements Facet, Serializable {

        protected final String uri;
        protected final String fragment;
        protected final boolean isNumber;
        protected final OWLFacet facet;

        public AbstractFacet(String u, boolean number, OWLFacet f) {
            uri = DatatypeFactory.getNamespace() + u;
            fragment = u;
            isNumber = number;
            facet = f;
        }

        @Override
        public OWLFacet facet() {
            return facet;
        }

        @Override
        public final boolean isNumberFacet() {
            return isNumber;
        }

        @Override
        public String getURI() {
            return uri;
        }

        @Override
        public String toString() {
            return "facet[" + fragment + ']';
        }

        @Override
        public int hashCode() {
            return uri.hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (super.equals(obj)) {
                return true;
            }
            if (obj instanceof Facet) {
                return uri.equals(((Facet) obj).getURI());
            }
            return false;
        }

        @Override
        public Comparable parseNumber(@Nullable Object value) {
            if (!isNumberFacet()) {
                throw new UnsupportedOperationException("Only number facets can parse numbers");
            }
            if (value == null) {
                throw new IllegalArgumentException("Cannot parse a null value");
            }
            try {
                if (value instanceof String) {
                    return new BigDecimal((String) value);
                }
                if (value instanceof Number) {
                    return new BigDecimal(value.toString());
                }
                if (value instanceof Literal) {
                    Object typedValue = ((Literal<?>) value).typedValue();
                    if (typedValue instanceof Number) {
                        return new BigDecimal(typedValue.toString());
                    }
                    if (typedValue instanceof Calendar) {
                        return (Comparable<?>) typedValue;
                    }
                }
                if (value instanceof Comparable) {
                    return (Comparable<?>) value;
                }
                // any other type, its string form must be parseable by
                // BigDecimal - or exceptions will be spat out
                return new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Cannot parse '" + value.toString() + "' as a big decimal: " + e
                    .getMessage());
            }
        }

        @Override
        public Comparable parse(Object o) {
            return (Comparable<?>) o;
        }
    }

    private static class LimitFacet extends AbstractFacet {

        private String toString;

        public LimitFacet(String u, String toString, OWLFacet f) {
            super(u, true, f);
            this.toString = toString;
        }

        public LimitFacet(String u, OWLFacet f) {
            super(u, true, f);
            toString = super.toString();
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    /**
     * whitespace enum
     * 
     * @author ignazio
     */
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
        /** preserve */
        PRESERVE {

            @Override
            public String normalize(@Nonnull String input) {
                return input;
            }
        },
        /** replace */
        REPLACE {

            @Override
            public String normalize(@Nonnull String input) {
                return input.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
            }
        },
        /** collapse */
        COLLAPSE {

            @Override
            public String normalize(@Nonnull String input) {
                StringBuilder b = new StringBuilder(REPLACE.normalize(input));
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

        /**
         * @param input
         *        input
         * @return normalized input
         */
        public abstract String normalize(String input);
    }

    /** length */
    public static final Facet length = new LimitFacet("length", LENGTH);
    /** minLength */
    public static final Facet minLength = new LimitFacet("minLength", MIN_LENGTH);
    /** maxLength */
    public static final Facet maxLength = new LimitFacet("maxLength", MAX_LENGTH);
    /** totalDigits */
    public static final Facet totalDigits = new LimitFacet("totalDigits", TOTAL_DIGITS);
    /** fractionDigits */
    public static final Facet fractionDigits = new LimitFacet("fractionDigits", FRACTION_DIGITS);
    /** whiteSpace */
    public static final Facet whiteSpace = new AbstractFacet("whiteSpace", false, null) {

        @Override
        public whitespace parse(Object value) {
            if (value instanceof whitespace) {
                return (whitespace) value;
            }
            if (value instanceof String) {
                return whitespace.valueOf((String) value);
            }
            throw new ReasonerInternalException("Cannot parse " + value + " as a whitespace enum value");
        }
    };
    /** pattern */
    public static final Facet pattern = new AbstractFacet("pattern", false, PATTERN) {

        @Override
        public String parse(Object value) {
            return value.toString();
        }
    };
    /** enumeration */
    public static final Facet enumeration = new AbstractFacet("enumeration", false, null);
    /** maxInclusive */
    public static final Facet maxInclusive = new LimitFacet("maxInclusive", "]", MAX_INCLUSIVE);
    /** maxExclusive */
    public static final Facet maxExclusive = new LimitFacet("maxExclusive", ")", MAX_EXCLUSIVE);
    /** minInclusive */
    public static final Facet minInclusive = new LimitFacet("minInclusive", "[", MIN_INCLUSIVE);
    /** minExclusive */
    public static final Facet minExclusive = new LimitFacet("minExclusive", "(", MIN_EXCLUSIVE);
    private static final List<Facet> values = Arrays.asList(enumeration, fractionDigits, length, maxExclusive,
        maxInclusive, minExclusive, minInclusive, maxLength, minLength, pattern, totalDigits, whiteSpace);

    /** @return all facets */
    public static Stream<Facet> values() {
        return values.stream();
    }

    private static EnumMap<OWLFacet, Facet> facets = facets();

    private static EnumMap<OWLFacet, Facet> facets() {
        EnumMap<OWLFacet, Facet> map = new EnumMap<>(OWLFacet.class);
        values.stream().filter(f -> f.facet() != null).forEach(f -> map.put(f.facet(), f));
        return map;
    }

    /**
     * @param f
     *        owl facet
     * @return facet
     */
    public static Facet parse(OWLFacet f) {
        Facet facet = facets.get(f);
        if (facet == null) {
                throw new OWLRuntimeException("Unsupported facet: " + f);
        }
        return facet;
    }

    /**
     * @param in
     *        string facet
     * @return facet
     */
    public static Facet parse(String in) {
        String f = '#' + in.substring(in.indexOf(':') + 1);
        for (Facet facet : values) {
            if (facet.getURI().endsWith(f)) {
                return facet;
            }
        }
        throw new OWLRuntimeException("Unsupported facet: " + f);
    }
}
