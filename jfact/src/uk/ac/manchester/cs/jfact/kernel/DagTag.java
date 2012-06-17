package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.EnumSet;

/** different Concept Expression tags */
public enum DagTag {
	// illegal entry
	dtBad("bad-tag"),
	// operations
	dtTop("*TOP*"), dtAnd("and"), dtCollection("collection"), dtForall("all"), dtLE(
			"at-most"),
	//dtUAll("all U"), // \dall U.C
	dtIrr("irreflexive"), // \neg\exists R.Self
	dtProj("projection"), // aux vertex with Projection FROM the current node
	dtNN("NN-stopper"), // NN-rule was applied
	// ID's
	dtPConcept("primconcept"), // primitive concept
	dtNConcept("concept"), // non-primitive concept
	dtPSingleton("prim-singleton"), dtNSingleton("singleton"), dtDataType("data-type"), dtDataValue(
			"data-value"), dtDataExpr("data-expr"), dtChoose("choose"), dtSplitConcept(
			"split-concept");
	private static final EnumSet<DagTag> TRUE = EnumSet.of(dtDataType, dtDataValue,
			dtDataExpr, dtNN, dtBad, dtTop, dtChoose);
	private static final EnumSet<DagTag> NotPos = EnumSet.of(dtPConcept, dtPSingleton,
			dtCollection, dtProj);

	/**
	 * whether statistic's gathering should be omitted due to the type of a
	 * vertex
	 */
	public boolean omitStat(final boolean pos) {
		if (TRUE.contains(this)) {
			return true;
		}
		if (NotPos.contains(this)) {
			return !pos;
		}
		return false;
	}

	private String name;

	private DagTag(final String s) {
		name = s;
	}

	public String getName() {
		return name;
	}

	// data type with restrictions
	/** check whether given DagTag is a primitive named concept-like entity */
	public boolean isPNameTag() {
		return this == DagTag.dtPConcept || this == DagTag.dtPSingleton;
	}

	/** check whether given DagTag is a non-primitive named concept-like entity */
	public boolean isNNameTag() {
		return this == DagTag.dtNConcept || this == DagTag.dtNSingleton;
	}

	/** check whether given DagTag is a named concept-like entity */
	public boolean isCNameTag() {
		return isPNameTag() || isNNameTag();
	}

	private static final EnumSet<DagTag> complexConceptsEnumSet = EnumSet.of(
			DagTag.dtForall, DagTag.dtLE, DagTag.dtIrr, DagTag.dtNN, DagTag.dtChoose);

	/** @return true iff TAG represents complex concept */
	public boolean isComplexConcept() {
		return complexConceptsEnumSet.contains(this);
	}
}
