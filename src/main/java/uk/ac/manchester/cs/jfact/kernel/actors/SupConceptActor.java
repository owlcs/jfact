package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;

/** class for exploring concept taxonomy to find super classes */
@PortedFrom(file = "Kernel.cpp", name = "SupConceptActor")
public class SupConceptActor implements Actor, Serializable {

    @PortedFrom(file = "Kernel.cpp", name = "pe") protected final ClassifiableEntry pe;

    /**
     * @param q
     *        q
     */
    public SupConceptActor(ClassifiableEntry q) {
        pe = q;
    }

    @PortedFrom(file = "Kernel.cpp", name = "entry")
    protected boolean entry(ClassifiableEntry q) {
        return !pe.equals(q);
    }

    @Override
    @PortedFrom(file = "Kernel.cpp", name = "apply")
    public boolean apply(TaxonomyVertex v) {
        if (!entry(v.getPrimer())) {
            return false;
        }
        return v.synonyms().allMatch(this::entry);
    }

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (entry(v.getPrimer())) {
            return true;
        }
        return v.synonyms().anyMatch(this::entry);
    }
}
