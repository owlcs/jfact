package uk.ac.manchester.cs.jfact.kernel.datatype;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.bpINVALID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.kernel.NamedEntry;

public class DataEntry<O> extends NamedEntry implements Datatyped {
	/** corresponding type (Type has null in the field) */
	private Datatypes type;
	/** DAG index of the entry */
	private int pName;
	/** ComparableDT, used only for values */
	private DatatypeRepresentation<O> comp;
	/** restriction to the entry */
	private final DataInterval<O> constraints = new DataInterval<O>();

	/** create data entry with given name */
	public DataEntry(final String name) {
		super(name);
		type = null;
		pName = bpINVALID;
	}

	/** check if data entry represents basic data type */
	public boolean isBasicDataType() {
		return type == null && constraints.isEmpty();
	}

	/** check if data entry represents data value */
	public boolean isDataValue() {
		return type != null && constraints.isEmpty();
	}

	/** set host data type for the data value */
	public void setHostType(Datatypes t) {
		type = t;
		//XXX crappy implementation still here
		if (getName().equals("expr") && type != Datatypes.STRING) {
			if (type == Datatypes.DATETIME) {
				// then a neutral date of some sort
				try {
					comp = (DatatypeRepresentation<O>) type.build(DatatypeFactory
							.newInstance().newXMLGregorianCalendar());
				} catch (DatatypeConfigurationException e) {
					throw new ReasonerInternalException(e);
				}
			} else {
				// then init with 0
				initComp("0");
			}
		} else {
			initComp();
		}
	}

	public void initComp() {
		if (type != null) {
			comp = type.parse(getName());
		}
	}

	public void initComp(String s) {
		if (type != null) {
			comp = type.parse(s);
		}
	}

	public DatatypeRepresentation<O> getComp() {
		return comp;
	}

	/** get host type */
	public Datatypes getType() {
		return type;
	}

	public Datatypes getDatatype() {
		return type;
	}

	// facet part
	/** get RW access to constraints of the DE */
	public DataInterval<O> getFacet() {
		return constraints;
	}

	/** get pointer to DAG entry corresponding to the data entry */
	public int getBP() {
		return pName;
	}

	/** set DAG index of the data entry */
	public void setBP(int p) {
		pName = p;
	}

	public String toString() {
		return constraints.toString();
	}
}