package uk.ac.manchester.cs.jfact.kernel.actors;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry;
import uk.ac.manchester.cs.jfact.kernel.Concept;
import uk.ac.manchester.cs.jfact.kernel.Individual;
import uk.ac.manchester.cs.jfact.kernel.TaxonomyVertex;
import conformance.PortedFrom;

/** RI actor */
@PortedFrom(file = "Kernel.cpp", name = "RIActor")
public class RIActor implements Actor, Serializable {

    private static final long serialVersionUID = 11000L;
    private final List<Individual> acc = new ArrayList<>();

    /**
     * process single entry in a vertex label
     * 
     * @param p
     *        p
     * @return true if try successful
     */
    protected boolean tryEntry(ClassifiableEntry p) {
        // check the applicability
        if (p.isSystem() || !((Concept) p).isSingleton()) {
            return false;
        }
        // print the concept
        acc.add((Individual) p);
        return true;
    }

    @Override
    public boolean apply(TaxonomyVertex v) {
        boolean ret = tryEntry(v.getPrimer());
        for (ClassifiableEntry p : v.begin_syn()) {
            ret |= tryEntry(p);
        }
        return ret;
    }

    @Override
    public boolean applicable(TaxonomyVertex v) {
        if (test(v.getPrimer())) {
            return true;
        }
        for (ClassifiableEntry p : v.begin_syn()) {
            if (test(p)) {
                return true;
            }
        }
        return false;
    }

    private static boolean test(ClassifiableEntry p) {
        return !(p.isSystem() || !((Concept) p).isSingleton());
    }

    @Override
    public void clear() {
        acc.clear();
    }

    /** @return accumulator */
    public List<Individual> getAcc() {
        return acc;
    }
}
