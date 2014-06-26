package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** class to represent the TS's */
public class ToldSubsumers extends KnownSubsumers {

    private static final long serialVersionUID = 11000L;
    /** two iterators for the TS of a concept */
    private final List<ClassifiableEntry> beg;

    /**
     * @param b
     *        b
     */
    public ToldSubsumers(Collection<ClassifiableEntry> b) {
        beg = new ArrayList<>(b);
    }

    /** begin of the Sure subsumers interval */
    @Override
    public List<ClassifiableEntry> s_begin() {
        return beg;
    }

    /** end of the Sure subsumers interval */
    /** begin of the Possible subsumers interval */
    @Override
    public List<ClassifiableEntry> p_begin() {
        return Collections.emptyList();
    }
}
