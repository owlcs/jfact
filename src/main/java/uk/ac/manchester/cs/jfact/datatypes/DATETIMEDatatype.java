package uk.ac.manchester.cs.jfact.datatypes;

import static uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory.*;
import static uk.ac.manchester.cs.jfact.datatypes.Facets.*;

import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

class DATETIMEDatatype extends AbstractDatatype<Date> implements OrderedDatatype<Date> {

    DATETIMEDatatype() {
        this(XSDVocabulary.DATE_TIME);
    }

    DATETIMEDatatype(HasIRI u, Set<Datatype<?>> ancestors) {
        super(u, FACETS4, ancestors);
    }

    DATETIMEDatatype(HasIRI u) {
        super(u, FACETS4, Utils.generateAncestors(LITERAL));
        knownNonNumericFacetValues.putAll(LITERAL.getKnownNonNumericFacetValues());
        knownNumericFacetValues.putAll(LITERAL.getKnownNumericFacetValues());
        knownNonNumericFacetValues.put(whiteSpace, WHITESPACE);
    }

    @Override
    public ordered getOrdered() {
        return ordered.PARTIAL;
    }

    @Override
    public boolean isOrderedDatatype() {
        return true;
    }

    @Override
    public OrderedDatatype<Date> asOrderedDatatype() {
        return this;
    }

    @Override
    public Date parseValue(String s) {
        try {
            XMLGregorianCalendar cal = javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(s);
            return cal.normalize().toGregorianCalendar().getTime();
        } catch (DatatypeConfigurationException e) {
            throw new ReasonerInternalException(e);
        }
    }

    @Override
    public boolean isInValueSpace(Date l) {
        if (hasMinExclusive() && getMin().compareTo(l) <= 0) {
            return false;
        }
        if (hasMinInclusive() && getMin().compareTo(l) < 0) {
            return false;
        }
        if (hasMaxExclusive() && getMax().compareTo(l) >= 0) {
            return false;
        }
        if (hasMaxInclusive() && getMax().compareTo(l) > 0) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isCompatible(Datatype<?> type) {
        if (super.isCompatible(type)) {
            return true;
        }
        if (type.isSubType(this)) {
            // then its representation must be Calendars
            OrderedDatatype<Date> wrapper = (OrderedDatatype<Date>) type;
            // then both types are numeric
            // if both have no max or both have no min -> there is an
            // overlap
            // if one has no max, then min must be smaller than max of the
            // other
            // if one has no min, the max must be larger than min of the
            // other
            // if one has neither max nor min, they are compatible
            if (!hasMax() && !hasMin()) {
                return true;
            }
            if (!wrapper.hasMax() && !wrapper.hasMin()) {
                return true;
            }
            if (!hasMax() && !wrapper.hasMax()) {
                return true;
            }
            if (!hasMin() && !wrapper.hasMin()) {
                return true;
            }
            if (!hasMin()) {
                return this.overlapping(this, wrapper);
            }
            if (!hasMax()) {
                return this.overlapping(wrapper, this);
            }
            if (!wrapper.hasMin()) {
                return this.overlapping(wrapper, this);
            }
            if (!wrapper.hasMax()) {
                return this.overlapping(this, wrapper);
            }
            // compare their range facets:
            // disjoint if:
            // exclusives:
            // one minInclusive/exclusive is strictly larger than the other
            // maxinclusive/exclusive
            return this.overlapping(this, wrapper) || this.overlapping(wrapper, this);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasMinExclusive() {
        return knownNumericFacetValues.containsKey(minExclusive);
    }

    @Override
    public boolean hasMinInclusive() {
        return knownNumericFacetValues.containsKey(minInclusive);
    }

    @Override
    public boolean hasMaxExclusive() {
        return knownNumericFacetValues.containsKey(maxExclusive);
    }

    @Override
    public boolean hasMaxInclusive() {
        return knownNumericFacetValues.containsKey(maxInclusive);
    }

    @Override
    public boolean hasMin() {
        return hasMinInclusive() || hasMinExclusive();
    }

    @Override
    public boolean hasMax() {
        return hasMaxInclusive() || hasMaxExclusive();
    }

    @Nullable
    @Override
    public Date getMin() {
        if (hasMinExclusive()) {
            return (Date) getFacetValue(minExclusive);
        }
        if (hasMinInclusive()) {
            return (Date) getFacetValue(minInclusive);
        }
        return null;
    }

    @Nullable
    @Override
    public Date getMax() {
        if (hasMaxExclusive()) {
            return (Date) getFacetValue(maxExclusive);
        }
        if (hasMaxInclusive()) {
            return (Date) getFacetValue(maxInclusive);
        }
        return null;
    }
}
