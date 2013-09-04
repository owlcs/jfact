package uk.ac.manchester.cs.jfact.kernel.unused;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;

/** class to check whether there is a need to unsplit splitted var */
@SuppressWarnings("unused")
public class SingleSplit implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** signature of equivalent part of the split */
    private final Set<NamedEntity> eqSig;
    /** signature of subsumption part of the split */
    private final Set<NamedEntity> impSig;
    /** pointer to split vertex to activate */
    private final int bp;

    protected SingleSplit(Set<NamedEntity> es, Set<NamedEntity> is, int p) {
        eqSig = new HashSet<NamedEntity>(es);
        impSig = new HashSet<NamedEntity>(is);
        bp = p;
    }
}
