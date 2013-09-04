package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.List;

/** abstract class to represent the known subsumers of a concept */
public abstract class KnownSubsumers implements Serializable {
    private static final long serialVersionUID = 11000L;

    /** @return begin of the Sure subsumers interval */
    public abstract List<ClassifiableEntry> s_begin();

    /** @return begin of the Possible subsumers interval */
    public abstract List<ClassifiableEntry> p_begin();

    // flags
    /** @return whether there are no sure subsumers */
    public boolean s_empty() {
        return s_begin().isEmpty();
    }

    /** @return whether there are no possible subsumers */
    public boolean p_empty() {
        return p_begin().isEmpty();
    }

    /** @param ce
     *            class expression
     * @return true iff CE is the possible subsumer */
    public boolean isPossibleSub(@SuppressWarnings("unused") ClassifiableEntry ce) {
        return true;
    }
}
