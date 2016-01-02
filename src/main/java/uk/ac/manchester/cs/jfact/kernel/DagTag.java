package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.EnumSet;

import javax.annotation.Nonnull;

/** different Concept Expression tags */
public enum DagTag {
    //@formatter:off
    /** illegal entry */                                    BAD           ("bad-tag"),
    /** operations */                                       TOP           ("*TOP*"),
    /** operations */                                       AND           ("and"),
    /** operations */                                       COLLECTION    ("collection"),
    /** operations */                                       FORALL        ("all"),
    /** operations */                                       LE            ("at-most"),
    /** \neg\exists R.Self */                               IRR           ("irreflexive"),
    /** aux vertex with Projection FROM the current node */ PROJ          ("projection"),
    /** NN-rule was applied */                              NN            ("NN-stopper"),
    // ID's
    /** primitive concept */                                PCONCEPT      ("primconcept"),
    /** non-primitive concept */                            NCONCEPT      ("concept"),
    /** primitive singleton */                              PSINGLETON    ("prim-singleton"),
    /** non-primitive singleton */                          NSINGLETON    ("singleton"),
    /** datatype */                                         DATATYPE      ("data-type"),
    /** data value */                                       DATAVALUE     ("data-value"),
    /** data expression */                                  DATAEXPR      ("data-expr"),
    /** choose */                                           CHOOSE        ("choose");
    //@formatter:on
    private static final EnumSet<DagTag> TRUE = EnumSet.of(DATATYPE, DATAVALUE, DATAEXPR, NN, BAD, TOP, CHOOSE);
    private static final EnumSet<DagTag> NotPos = EnumSet.of(PCONCEPT, PSINGLETON, COLLECTION, PROJ);
    @Nonnull private final String name;
    private static final EnumSet<DagTag> complexConceptsEnumSet = EnumSet.of(FORALL, LE, IRR, NN, CHOOSE);

    private DagTag(String s) {
        name = s;
    }

    /**
     * @param pos
     *        pos
     * @return whether statistic's gathering should be omitted due to the type
     *         of a vertex
     */
    public boolean omitStat(boolean pos) {
        if (TRUE.contains(this)) {
            return true;
        }
        if (NotPos.contains(this)) {
            return !pos;
        }
        return false;
    }

    /** @return name */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    // data type with restrictions
    /**
     * @return check whether given DagTag is a primitive named concept-like
     *         entity
     */
    public boolean isPNameTag() {
        return this == PCONCEPT || this == PSINGLETON;
    }

    /**
     * @return check whether given DagTag is a non-primitive named concept-like
     *         entity
     */
    public boolean isNNameTag() {
        return this == NCONCEPT || this == NSINGLETON;
    }

    /** @return check whether given DagTag is a named concept-like entity */
    public boolean isCNameTag() {
        return isPNameTag() || isNNameTag();
    }

    /** @return true iff TAG represents complex concept */
    public boolean isComplexConcept() {
        return complexConceptsEnumSet.contains(this);
    }
}
