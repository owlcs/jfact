package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.ClassifiableEntry.*;

import java.io.Serializable;

import conformance.Original;
import conformance.PortedFrom;

/** relation betweenindividuals */
@PortedFrom(file = "tRelated.h", name = "TRelated")
public class Related implements Serializable {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "tRelated.h", name = "a")
    private Individual a;
    @PortedFrom(file = "tRelated.h", name = "b")
    private Individual b;
    @PortedFrom(file = "tRelated.h", name = "R")
    private Role R;

    /** @param a_
     * @param b_
     * @param R_ */
    public Related(Individual a_, Individual b_, Role R_) {
        a = a_;
        b = b_;
        R = R_;
    }

    /** simplify structure wrt synonyms */
    @PortedFrom(file = "tRelated.h", name = "simplify")
    public void simplify() {
        R = resolveSynonym(R);
        a = resolveSynonym(a);
        b = resolveSynonym(b);
        a.addRelated(this);
    }

    /** @return role wrt the FROM direction */
    @PortedFrom(file = "tRelated.h", name = "getRole")
    public Role getRole() {
        return R;
    }

    /** @return individual a */
    @Original
    public Individual getA() {
        return a;
    }

    /** @return individual b */
    @Original
    public Individual getB() {
        return b;
    }
}
