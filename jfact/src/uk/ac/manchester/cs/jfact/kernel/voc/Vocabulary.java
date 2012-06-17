package uk.ac.manchester.cs.jfact.kernel.voc;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
public final class Vocabulary {
	public static final String OWL_NAMESPACE = "http://www.w3.org/2002/07/owl#";
	public static final String TOP_OBJECT_PROPERTY = OWL_NAMESPACE + "topObjectProperty";
	public static final String BOTTOM_OBJECT_PROPERTY = OWL_NAMESPACE
			+ "bottomObjectProperty";
	public static final String TOP_DATA_PROPERTY = OWL_NAMESPACE + "topDataProperty";
	public static final String BOTTOM_DATA_PROPERTY = OWL_NAMESPACE
			+ "bottomDataProperty";
	public static final String ANY_SIMPLE_TYPE = "http://www.w3.org/2000/01/rdf-schema#anySimpleType";
	public static final String ANY_TYPE = "http://www.w3.org/2001/XMLSchema#anyType";
	public static final String XML_ANY_SIMPLE_TYPE = "http://www.w3.org/2001/XMLSchema#anySimpleType";
	public static final String LITERAL = "http://www.w3.org/2000/01/rdf-schema#Literal";
	public static final String PLAIN_LITERAL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral";
	public static final String XMLLITERAL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral";
	public static final String STRING = "http://www.w3.org/2001/XMLSchema#string";
	public static final String ANY_URI = "http://www.w3.org/2001/XMLSchema#anyURI";
	public static final String INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
	public static final String INT = "http://www.w3.org/2001/XMLSchema#int";
	public static final String NON_NEGATIVE_INTEGER = "http://www.w3.org/2001/XMLSchema#nonNegativeInteger";
	public static final String POSITIVE_INTEGER = "http://www.w3.org/2001/XMLSchema#positiveInteger";
	public static final String NEGATIVE_INTEGER = "http://www.w3.org/2001/XMLSchema#negativeInteger";
	public static final String SHORT = "http://www.w3.org/2001/XMLSchema#short";
	public static final String BYTE = "http://www.w3.org/2001/XMLSchema#byte";
	public static final String FLOAT = "http://www.w3.org/2001/XMLSchema#float";
	public static final String DOUBLE = "http://www.w3.org/2001/XMLSchema#double";
	public static final String REAL = "http://www.w3.org/2002/07/owl#real";
	public static final String RATIONAL = "http://www.w3.org/2002/07/owl#rational";
	public static final String DECIMAL = "http://www.w3.org/2001/XMLSchema#decimal";
	public static final String BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
	public static final String UNSIGNEDBYTE = "http://www.w3.org/2001/XMLSchema#unsignedByte";
	public static final String UNSIGNEDSHORT = "http://www.w3.org/2001/XMLSchema#unsignedShort";
	public static final String UNSIGNEDLONG = "http://www.w3.org/2001/XMLSchema#unsignedLong";
	public static final String LONG = "http://www.w3.org/2001/XMLSchema#long";
	public static final String UNSIGNEDINT = "http://www.w3.org/2001/XMLSchema#unsignedInt";
	public static final String NONPOSINT = "http://www.w3.org/2001/XMLSchema#nonPositiveInteger";
	public static final String DATETIME = "http://www.w3.org/2001/XMLSchema#dateTime";
	public static final String DATE = "http://www.w3.org/2001/XMLSchema#date";
	//	/** get name of the default string datatype */
	//	public static final String getStrTypeName() {
	//		return "http://www.w3.org/2001/XMLSchema//#string";
	//	}
	//
	//	/** get name of the default integer datatype */
	//	public static final String getIntTypeName() {
	//		return "http://www.w3.org/2001/XMLSchema//#integer";
	//	}
	//
	//	/** get name of the default floating point datatype */
	//	public static final String getRealTypeName() {
	//		return "http://www.w3.org/2001/XMLSchema//#float";
	//	}
	//
	//	/** get name of the default boolean datatype */
	//	public static final String getBoolTypeName() {
	//		return "http://www.w3.org/2001/XMLSchema//#boolean";
	//	}
}
