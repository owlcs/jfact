package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.EnumSet;

/** different Concept Expression tags */
public enum DagTag {
    /** illegal entry */
    dtBad("bad-tag"),
    /** operations */
    dtTop("*TOP*"),
    /** operations */
    dtAnd("and"),
    /** operations */
    dtCollection("collection"),
    /** operations */
    dtForall("all"),
    /** operations */
    dtLE("at-most"),
    /** \neg\exists R.Self */
    dtIrr("irreflexive"),
    /** aux vertex with Projection FROM the current node */
    dtProj("projection"),
    /** NN-rule was applied */
    dtNN("NN-stopper"),
    // ID's
    /** primitive concept */
    dtPConcept("primconcept"),
    /** non-primitive concept */
    dtNConcept("concept"),
    /** primitive singleton */
    dtPSingleton("prim-singleton"),
    /** non-primitive singleton */
    dtNSingleton("singleton"),
    /** datatype */
    dtDataType("data-type"),
    /** data value */
    dtDataValue("data-value"),
    /** data expression */
    dtDataExpr("data-expr"),
    /** choose */
    dtChoose("choose"),
    /** split concept */
    dtSplitConcept("split-concept");
    private static EnumSet<DagTag> TRUE = EnumSet.of(dtDataType, dtDataValue, dtDataExpr,
            dtNN, dtBad, dtTop, dtChoose);
    private static EnumSet<DagTag> NotPos = EnumSet.of(dtPConcept, dtPSingleton,
            dtCollection, dtProj);

    /** @param pos
     * @return whether statistic's gathering should be omitted due to the type
     *         of a vertex */
    public boolean omitStat(boolean pos) {
        if (TRUE.contains(this)) {
            return true;
        }
        if (NotPos.contains(this)) {
            return !pos;
        }
        return false;
    }

    private String name;

    private DagTag(String s) {
        name = s;
    }

    /** @return name */
    public String getName() {
        return name;
    }

    // data type with restrictions
    /** @return check whether given DagTag is a primitive named concept-like
     *         entity */
    public boolean isPNameTag() {
        return this == DagTag.dtPConcept || this == DagTag.dtPSingleton;
    }

    /** @return check whether given DagTag is a non-primitive named concept-like
     *         entity */
    public boolean isNNameTag() {
        return this == DagTag.dtNConcept || this == DagTag.dtNSingleton;
    }

    /** @return check whether given DagTag is a named concept-like entity */
    public boolean isCNameTag() {
        return isPNameTag() || isNNameTag();
    }

    private static EnumSet<DagTag> complexConceptsEnumSet = EnumSet.of(DagTag.dtForall,
            DagTag.dtLE, DagTag.dtIrr, DagTag.dtNN, DagTag.dtChoose);

    /** @return true iff TAG represents complex concept */
    public boolean isComplexConcept() {
        return complexConceptsEnumSet.contains(this);
    }
}
