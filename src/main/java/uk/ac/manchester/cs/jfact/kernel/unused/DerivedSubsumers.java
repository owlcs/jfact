package uk.ac.manchester.cs.jfact.kernel.unused;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.KnownSubsumers;

/** all the derived subsumers of a class (came from the model) */
public class DerivedSubsumers extends KnownSubsumers {
    private static final long serialVersionUID = 11000L;
    /** set of sure- and possible subsumers */
    protected final List<ClassifiableEntry> Sure, Possible;

    /** c'tor: copy given sets
     * 
     * @param sure
     * @param possible */
    public DerivedSubsumers(List<ClassifiableEntry> sure, List<ClassifiableEntry> possible) {
        Sure = new ArrayList<ClassifiableEntry>(sure);
        Possible = new ArrayList<ClassifiableEntry>(possible);
    }

    /** begin of the Sure subsumers interval */
    @Override
    public List<ClassifiableEntry> s_begin() {
        return Sure;
    }

    /** begin of the Possible subsumers interval */
    @Override
    public List<ClassifiableEntry> p_begin() {
        return Possible;
    }
}
