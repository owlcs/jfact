package uk.ac.manchester.cs.jfact.kernel;

import static uk.ac.manchester.cs.jfact.helpers.Helper.*;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.cs.jfact.helpers.DLTree;
import conformance.Original;
import conformance.PortedFrom;

/** class for simple rules like Ch :- Cb1, Cbi, CbN; all C are primitive named
 * concepts */
@PortedFrom(file = "dlTBox.h", name = "TSimpleRule")
class SimpleRule {
    /** body of the rule */
    @PortedFrom(file = "dlTBox.h", name = "Body")
    List<Concept> simpleRuleBody = new ArrayList<Concept>();
    /** head of the rule as a DLTree */
    @PortedFrom(file = "dlTBox.h", name = "tHead")
    protected DLTree tHead;
    /** head of the rule as a BP */
    @PortedFrom(file = "dlTBox.h", name = "bpHead")
    int bpHead;

    public SimpleRule(List<Concept> body, DLTree head) {
        simpleRuleBody.addAll(body);
        tHead = head;
        setBpHead(bpINVALID);
    }

    @PortedFrom(file = "dlTBox.h", name = "applicable")
    public boolean applicable(DlSatTester Reasoner) {
        return Reasoner.applicable(this);
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
