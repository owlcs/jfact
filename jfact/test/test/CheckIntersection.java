package test;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.datatypes.*;

public class CheckIntersection {
    @Test
    public void testIntersection() {
        DatatypeFactory f = DatatypeFactory.getInstance();
        DatatypeNumericEnumeration<BigInteger> d = new DatatypeNumericEnumeration<BigInteger>(
                (NumericDatatype<BigInteger>) DatatypeFactory.INTEGER,
                DatatypeFactory.INTEGER.buildLiteral("3"));
        DatatypeExpression<BigInteger> e = DatatypeFactory
                .getNumericDatatypeExpression((NumericDatatype<BigInteger>) DatatypeFactory.INTEGER);
        List<Datatype<?>> list = new ArrayList<Datatype<?>>();
        list.add(d);
        list.add(e.addFacet(Facets.minInclusive, "4"));
        DatatypeIntersection intersection = new DatatypeIntersection(
                DatatypeFactory.INTEGER, list);
        assertTrue(intersection.emptyValueSpace());
    }
}
