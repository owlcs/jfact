package test;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.datatypes.*;

public class CheckNegation {
    @Test
    public void testNegations() {
        DatatypeNumericEnumeration<BigInteger> four = new DatatypeNumericEnumeration<BigInteger>(
                (NumericDatatype<BigInteger>) DatatypeFactory.INTEGER,
                DatatypeFactory.INTEGER.buildLiteral("4"));
        Datatype<?> d1 = new DatatypeNegation<BigInteger>(four);
        DatatypeNumericEnumeration<BigInteger> five = new DatatypeNumericEnumeration<BigInteger>(
                (NumericDatatype<BigInteger>) DatatypeFactory.INTEGER,
                DatatypeFactory.INTEGER.buildLiteral("5"));
        Datatype<?> d2 = new DatatypeNegation<BigInteger>(five);
        assertTrue("not 4 compatible with not 5: broken", d1.isCompatible(d2));
        assertTrue("not 5 compatible with not 4: broken", d2.isCompatible(d1));
        assertFalse(four.isCompatible(d1));
        assertFalse(d1.isCompatible(four));
        assertTrue(d2.isCompatible(four));
        assertTrue(four.isCompatible(d2));
        assertFalse(five.isCompatible(d2));
        assertFalse(d2.isCompatible(five));
        assertTrue(d1.isCompatible(five));
        assertTrue(five.isCompatible(d1));
    }
}
