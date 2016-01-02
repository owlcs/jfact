package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.roaringbitmap.RoaringBitmap;

import uk.ac.manchester.cs.jfact.dep.DepSet;
import conformance.PortedFrom;

@PortedFrom(file = "CWDArray.cpp", name = "UnMerge")
class UnMerge extends Restorer {


    @PortedFrom(file = "CWDArray.cpp", name = "label")
    private final CWDArray label;
    @PortedFrom(file = "CWDArray.cpp", name = "offset")
    private final int offset;
    @PortedFrom(file = "CWDArray.cpp", name = "dep")
    private final RoaringBitmap dep;

    UnMerge(CWDArray lab, ConceptWDep p, int offset) {
        label = lab;
        this.offset = offset;
        dep = p.getDep().getDelegate();
    }

    @Override
    @PortedFrom(file = "CWDArray.h", name = "restore")
    public void restore() {
        int concept = label.getBase().get(offset).getConcept();
        ConceptWDep conceptWDep = new ConceptWDep(concept, DepSet.create(dep));
        label.getBase().set(offset, conceptWDep);
    }
}
