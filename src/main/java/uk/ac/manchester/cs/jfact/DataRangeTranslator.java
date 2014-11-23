package uk.ac.manchester.cs.jfact;

import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asList;
import static uk.ac.manchester.cs.jfact.kernel.ExpressionManager.*;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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
import uk.ac.manchester.cs.jfact.kernel.ExpressionCache;
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
    public DataRangeTranslator(ExpressionCache em, OWLDataFactory df,
            TranslationMachinery tr, DatatypeFactory f) {
        super(em, df, tr);
        this.f = f;
    }

    @Override
    protected DataExpression getTopEntityPointer() {
        return dataTop();
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
        return dataOneOf(asList(node.values().map(lit -> tr.pointer(lit))));
    }

    @Override
    public DataExpression visit(OWLDataComplementOf node) {
        return dataNot(node.getDataRange().accept(this));
    }

    @Override
    public DataExpression visit(OWLDataIntersectionOf node) {
        return dataAnd(translateDataRangeSet(node.operands()));
    }

    private List<DataExpression> translateDataRangeSet(
            Stream<? extends OWLDataRange> dataRanges) {
        return asList(dataRanges.map(op -> op.accept(this)));
    }

    @Override
    public DataExpression visit(OWLDataUnionOf node) {
        return dataOr(translateDataRangeSet(node.operands()));
    }

    @Override
    public DataExpression visit(OWLDatatypeRestriction node) {
        Datatype<?> type = f.getKnownDatatype(node.getDatatype().getIRI());
        Iterator<OWLFacetRestriction> facetRestrictions = node
                .facetRestrictions().iterator();
        if (!facetRestrictions.hasNext()) {
            return type;
        }
        DatatypeExpression<?> toReturn = null;
        if (type.isNumericDatatype()) {
            toReturn = type.wrapAsNumericExpression();
        } else if (type.isOrderedDatatype()) {
            toReturn = type.wrapAsOrderedExpression();
        } else {
            toReturn = type.wrapAsDatatypeExpression();
        }
        while (facetRestrictions.hasNext()) {
            OWLFacetRestriction restriction = facetRestrictions.next();
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
