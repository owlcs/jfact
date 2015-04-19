package uk.ac.manchester.cs.jfact.kernel;

import org.roaringbitmap.RoaringBitmap;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.dep.DepSet;

@PortedFrom(file = "CWDArray.cpp", name = "UnMerge")
class UnMerge extends Restorer {

    private static final long serialVersionUID = 11000L;
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
