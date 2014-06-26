package uk.ac.manchester.cs.jfact;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLDatatypeNode;
import org.semanticweb.owlapi.reasoner.impl.OWLDatatypeNodeSet;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeExpression;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;
import uk.ac.manchester.cs.jfact.datatypes.Facet;
import uk.ac.manchester.cs.jfact.datatypes.Facets;
import uk.ac.manchester.cs.jfact.datatypes.Literal;
import uk.ac.manchester.cs.jfact.kernel.ExpressionManager;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataExpression;

/** datarange translator */
public class DataRangeTranslator extends
        OWLEntityTranslator<OWLDatatype, DataExpression> implements
        OWLDataRangeVisitorEx<DataExpression> {

    private static final long serialVersionUID = 11000L;
    private final DatatypeFactory f;

    /**
     * @param em
     *        em
     * @param df
     *        df
     * @param tr
     *        tr
     * @param f
     *        f
     */
    public DataRangeTranslator(ExpressionManager em, OWLDataFactory df,
            TranslationMachinery tr, DatatypeFactory f) {
        super(em, df, tr);
        this.f = f;
    }

    @Override
    protected DataExpression getTopEntityPointer() {
        return em.dataTop();
    }

    @Override
    protected DataExpression getBottomEntityPointer() {
        return null;
    }

    @Override
    protected DefaultNode<OWLDatatype> createDefaultNode() {
        return new OWLDatatypeNode();
    }

    @Override
    protected OWLDatatype getTopEntity() {
        return df.getTopDatatype();
    }

    @Override
    protected OWLDatatype getBottomEntity() {
        return null;
    }

    @Override
    protected DefaultNodeSet<OWLDatatype> createDefaultNodeSet() {
        return new OWLDatatypeNodeSet();
    }

    @Override
    protected DataExpression createPointerForEntity(OWLDatatype entity) {
        return f.getKnownDatatype(entity.getIRI());
    }

    @Override
    public Datatype<?> visit(OWLDatatype node) {
        return f.getKnownDatatype(node.getIRI());
    }

    @Override
    public DataExpression visit(OWLDataOneOf node) {
        List<Literal<?>> l = new ArrayList<>();
        for (OWLLiteral literal : node.getValues()) {
            l.add(tr.pointer(literal));
        }
        return em.dataOneOf(l);
    }

    @Override
    public DataExpression visit(OWLDataComplementOf node) {
        return em.dataNot(node.getDataRange().accept(this));
    }

    @Override
    public DataExpression visit(OWLDataIntersectionOf node) {
        return em.dataAnd(translateDataRangeSet(node.getOperands()));
    }

    private List<DataExpression> translateDataRangeSet(
            Set<OWLDataRange> dataRanges) {
        List<DataExpression> l = new ArrayList<>();
        for (OWLDataRange op : dataRanges) {
            l.add(op.accept(this));
        }
        return l;
    }

    @Override
    public DataExpression visit(OWLDataUnionOf node) {
        return em.dataOr(translateDataRangeSet(node.getOperands()));
    }

    @Override
    public DataExpression visit(OWLDatatypeRestriction node) {
        Datatype<?> type = f.getKnownDatatype(node.getDatatype().getIRI());
        Set<OWLFacetRestriction> facetRestrictions = node
                .getFacetRestrictions();
        if (facetRestrictions.isEmpty()) {
            return type;
        }
        DatatypeExpression<?> toReturn = null;
        if (type.isNumericDatatype()) {
            toReturn = DatatypeFactory.getNumericDatatypeExpression(type
                    .asNumericDatatype());
        } else if (type.isOrderedDatatype()) {
            toReturn = DatatypeFactory.getOrderedDatatypeExpression(type);
        } else {
            toReturn = DatatypeFactory.getDatatypeExpression(type);
        }
        for (OWLFacetRestriction restriction : facetRestrictions) {
            Literal<?> dv = tr.pointer(restriction.getFacetValue());
            Facet facet = Facets.parse(restriction.getFacet());
            if (facet.isNumberFacet()) {
                toReturn = toReturn.addNumericFacet(facet, dv.typedValue());
            } else {
                toReturn = toReturn.addNonNumericFacet(facet, dv.typedValue());
            }
        }
        return toReturn;
    }
}
