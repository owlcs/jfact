package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.helpers.Helper.BP_INVALID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import conformance.Original;
import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.helpers.DLTree;

/**
 * class for simple rules like Ch :- Cb1, Cbi, CbN; all C are primitive named
 * concepts
 */
@PortedFrom(file = "dlTBox.h", name = "TSimpleRule")
class SimpleRule implements Serializable {

    /** body of the rule */
    @PortedFrom(file = "dlTBox.h", name = "Body") protected final List<Concept> simpleRuleBody = new ArrayList<>();
    /** head of the rule as a DLTree */
    @PortedFrom(file = "dlTBox.h", name = "tHead") protected final DLTree tHead;
    /** head of the rule as a BP */
    @PortedFrom(file = "dlTBox.h", name = "bpHead") protected int bpHead;

    public SimpleRule(List<Concept> body, DLTree head) {
        simpleRuleBody.addAll(body);
        tHead = head;
        setBpHead(BP_INVALID);
    }

    @Original
    public List<Concept> getBody() {
        return simpleRuleBody;
    }

    @Original
    public void setBpHead(int bpHead) {
        this.bpHead = bpHead;
    }

    @Original
    public int getBpHead() {
        return bpHead;
    }
}
