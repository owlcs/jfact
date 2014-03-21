package uk.ac.manchester.cs.jfact.kernel.voc;

import java.io.Serializable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** vocabulary iris */
public final class Vocabulary implements Serializable {

    private static final long serialVersionUID = 11000L;
    //@formatter:off
    /** OWL_NAMESPACE          */ public static final String OWL_NAMESPACE            = "http://www.w3.org/2002/07/owl#";
    /** RDF_NAMESPACE          */ public static final String RDF_NAMESPACE            = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    /** RDFS_NAMESPACE         */ public static final String RDFS_NAMESPACE           = "http://www.w3.org/2000/01/rdf-schema#";
    /** XSD_NAMESPACE          */ public static final String XSD_NAMESPACE            = "http://www.w3.org/2001/XMLSchema#";

    /** TOP_OBJECT_PROPERTY    */ public static final String TOP_OBJECT_PROPERTY      = OWL_NAMESPACE + "topObjectProperty";
    /** BOTTOM_OBJECT_PROPERTY */ public static final String BOTTOM_OBJECT_PROPERTY   = OWL_NAMESPACE + "bottomObjectProperty";
    /** TOP_DATA_PROPERTY      */ public static final String TOP_DATA_PROPERTY        = OWL_NAMESPACE + "topDataProperty";
    /** BOTTOM_DATA_PROPERTY   */ public static final String BOTTOM_DATA_PROPERTY     = OWL_NAMESPACE + "bottomDataProperty";
    /** REAL                   */ public static final String REAL                     = OWL_NAMESPACE + "real";
    /** RATIONAL               */ public static final String RATIONAL                 = OWL_NAMESPACE + "rational";

    /** ANY_SIMPLE_TYPE        */ public static final String ANY_SIMPLE_TYPE          = RDFS_NAMESPACE + "anySimpleType";
    /** LITERAL                */ public static final String LITERAL                  = RDFS_NAMESPACE + "Literal";

    /** PLAIN_LITERAL          */ public static final String PLAIN_LITERAL            = RDF_NAMESPACE + "PlainLiteral";
    /** XMLLITERAL             */ public static final String XMLLITERAL               = RDF_NAMESPACE + "XMLLiteral";

    /** ANY_TYPE               */ public static final String ANY_TYPE                 = XSD_NAMESPACE + "anyType";
    /** XML_ANY_SIMPLE_TYPE    */ public static final String XML_ANY_SIMPLE_TYPE      = XSD_NAMESPACE + "anySimpleType";
    /** STRING                 */ public static final String STRING                   = XSD_NAMESPACE + "string";
    /** ANY_URI                */ public static final String ANY_URI                  = XSD_NAMESPACE + "anyURI";
    /** INTEGER                */ public static final String INTEGER                  = XSD_NAMESPACE + "integer";
    /** INT                    */ public static final String INT                      = XSD_NAMESPACE + "int";
    /** NON_NEGATIVE_INTEGER   */ public static final String NON_NEGATIVE_INTEGER     = XSD_NAMESPACE + "nonNegativeInteger";
    /** POSITIVE_INTEGER       */ public static final String POSITIVE_INTEGER         = XSD_NAMESPACE + "positiveInteger";
    /** NEGATIVE_INTEGER       */ public static final String NEGATIVE_INTEGER         = XSD_NAMESPACE + "negativeInteger";
    /** SHORT                  */ public static final String SHORT                    = XSD_NAMESPACE + "short";
    /** BYTE                   */ public static final String BYTE                     = XSD_NAMESPACE + "byte";
    /** FLOAT                  */ public static final String FLOAT                    = XSD_NAMESPACE + "float";
    /** DOUBLE                 */ public static final String DOUBLE                   = XSD_NAMESPACE + "double";
    /** DECIMAL                */ public static final String DECIMAL                  = XSD_NAMESPACE + "decimal";
    /** BOOLEAN                */ public static final String BOOLEAN                  = XSD_NAMESPACE + "boolean";
    /** UNSIGNEDBYTE           */ public static final String UNSIGNEDBYTE             = XSD_NAMESPACE + "unsignedByte";
    /** UNSIGNEDSHORT          */ public static final String UNSIGNEDSHORT            = XSD_NAMESPACE + "unsignedShort";
    /** UNSIGNEDLONG           */ public static final String UNSIGNEDLONG             = XSD_NAMESPACE + "unsignedLong";
    /** LONG                   */ public static final String LONG                     = XSD_NAMESPACE + "long";
    /** UNSIGNEDINT            */ public static final String UNSIGNEDINT              = XSD_NAMESPACE + "unsignedInt";
    /** NONPOSINT              */ public static final String NONPOSINT                = XSD_NAMESPACE + "nonPositiveInteger";
    /** DATETIME               */ public static final String DATETIME                 = XSD_NAMESPACE + "dateTime";
    /** DATE                   */ public static final String DATE                     = XSD_NAMESPACE + "date";

    //@formatter:on
}
