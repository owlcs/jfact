package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** datatype clashes */
public enum DatatypeClashes {
    /** DT_TT */
    DT_TT(" DT-TT"),
    /** DT_Empty_interval */
    DT_EMPTY_INTERVAL(" DT-Empty_interval"),
    /** DT_C_MM */
    DT_C_MM(" DT-C-MM"),
    /** DT_TNT */
    DT_TNT(" DT-TNT"),
    /** DT_C_IT */
    DT_C_IT(" DT-C-IT");

    private String label;

    DatatypeClashes(String s) {
        label = s;
    }

    @Override
    public String toString() {
        return label;
    }
}
