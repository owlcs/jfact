package uk.ac.manchester.cs.jfact.datatypes.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.DatatypeFactory;

public class ConformantTest {
	List<Datatype<?>> list = DatatypeFactory.getValues();

	@Test
	public void testCompatibility() {
		for (Datatype<?> d : list) {
			assertTrue(d.equals(DatatypeFactory.LITERAL)
					|| d.getAncestors().contains(DatatypeFactory.LITERAL));
		}
		assertTrue(DatatypeFactory.LITERAL.getAncestors().size() == 0);
		assertTrue(DatatypeFactory.DATETIME.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.BOOLEAN.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.BASE64BINARY.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.HEXBINARY.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.ANYURI.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.STRING.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.REAL.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.XMLLITERAL.getAncestors().size() == 1);
		assertTrue(DatatypeFactory.DATETIMESTAMP.getAncestors().contains(
				DatatypeFactory.DATETIME));
		assertTrue(DatatypeFactory.DATETIMESTAMP.getAncestors().size() == 2);
		assertTrue(DatatypeFactory.DECIMAL.getAncestors().contains(DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.DECIMAL.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.DECIMAL.getAncestors().size() == 3);
		assertTrue(DatatypeFactory.RATIONAL.getAncestors().contains(DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.RATIONAL.getAncestors().size() == 2);
		assertTrue(DatatypeFactory.BYTE.getAncestors().contains(DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.BYTE.getAncestors().contains(DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.BYTE.getAncestors().contains(DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.BYTE.getAncestors().contains(DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.BYTE.getAncestors().contains(DatatypeFactory.LONG));
		assertTrue(DatatypeFactory.BYTE.getAncestors().contains(DatatypeFactory.INT));
		assertTrue(DatatypeFactory.BYTE.getAncestors().contains(DatatypeFactory.SHORT));
		assertTrue(DatatypeFactory.BYTE.getAncestors().size() == 8);
		assertTrue(DatatypeFactory.INT.getAncestors().contains(DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.INT.getAncestors().contains(DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.INT.getAncestors().contains(DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.INT.getAncestors().contains(DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.INT.getAncestors().contains(DatatypeFactory.LONG));
		assertTrue(DatatypeFactory.INT.getAncestors().size() == 6);
		assertTrue(DatatypeFactory.INTEGER.getAncestors().contains(DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.INTEGER.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.INTEGER.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.INTEGER.getAncestors().size() == 4);
		assertTrue(DatatypeFactory.LONG.getAncestors().contains(DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.LONG.getAncestors().contains(DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.LONG.getAncestors().contains(DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.LONG.getAncestors().contains(DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.LONG.getAncestors().size() == 5);
		assertTrue(DatatypeFactory.NEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.NEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.NEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.NEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.NEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.NONPOSITIVEINTEGER));
		assertTrue(DatatypeFactory.NEGATIVEINTEGER.getAncestors().size() == 6);
		assertTrue(DatatypeFactory.NONNEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.NONNEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.NONNEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.NONNEGATIVEINTEGER.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.NONNEGATIVEINTEGER.getAncestors().size() == 5);
		assertTrue(DatatypeFactory.NONPOSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.NONPOSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.NONPOSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.NONPOSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.NONPOSITIVEINTEGER.getAncestors().size() == 5);
		assertTrue(DatatypeFactory.POSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.POSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.POSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.POSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.POSITIVEINTEGER.getAncestors().contains(
				DatatypeFactory.NONNEGATIVEINTEGER));
		assertTrue(DatatypeFactory.POSITIVEINTEGER.getAncestors().size() == 6);
		assertTrue(DatatypeFactory.SHORT.getAncestors().contains(DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.SHORT.getAncestors()
				.contains(DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.SHORT.getAncestors().contains(DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.SHORT.getAncestors().contains(DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.SHORT.getAncestors().contains(DatatypeFactory.LONG));
		assertTrue(DatatypeFactory.SHORT.getAncestors().contains(DatatypeFactory.INT));
		assertTrue(DatatypeFactory.SHORT.getAncestors().size() == 7);
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.NONNEGATIVEINTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.UNSIGNEDLONG));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.UNSIGNEDINT));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().contains(
				DatatypeFactory.UNSIGNEDSHORT));
		assertTrue(DatatypeFactory.UNSIGNEDBYTE.getAncestors().size() == 9);
		assertTrue(DatatypeFactory.UNSIGNEDINT.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.UNSIGNEDINT.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.UNSIGNEDINT.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.UNSIGNEDINT.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDINT.getAncestors().contains(
				DatatypeFactory.NONNEGATIVEINTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDINT.getAncestors().contains(
				DatatypeFactory.UNSIGNEDLONG));
		assertTrue(DatatypeFactory.UNSIGNEDINT.getAncestors().size() == 7);
		assertTrue(DatatypeFactory.UNSIGNEDLONG.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.UNSIGNEDLONG.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.UNSIGNEDLONG.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.UNSIGNEDLONG.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDLONG.getAncestors().contains(
				DatatypeFactory.NONNEGATIVEINTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDLONG.getAncestors().size() == 6);
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().contains(
				DatatypeFactory.REAL));
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().contains(
				DatatypeFactory.RATIONAL));
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().contains(
				DatatypeFactory.DECIMAL));
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().contains(
				DatatypeFactory.INTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().contains(
				DatatypeFactory.NONNEGATIVEINTEGER));
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().contains(
				DatatypeFactory.UNSIGNEDLONG));
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().contains(
				DatatypeFactory.UNSIGNEDINT));
		assertTrue(DatatypeFactory.UNSIGNEDSHORT.getAncestors().size() == 8);
		assertTrue(DatatypeFactory.LANGUAGE.getAncestors().contains(
				DatatypeFactory.STRING));
		assertTrue(DatatypeFactory.LANGUAGE.getAncestors().contains(
				DatatypeFactory.NORMALIZEDSTRING));
		assertTrue(DatatypeFactory.LANGUAGE.getAncestors()
				.contains(DatatypeFactory.TOKEN));
		assertTrue(DatatypeFactory.LANGUAGE.getAncestors().size() == 4);
		assertTrue(DatatypeFactory.NAME.getAncestors().contains(DatatypeFactory.STRING));
		assertTrue(DatatypeFactory.NAME.getAncestors().contains(
				DatatypeFactory.NORMALIZEDSTRING));
		assertTrue(DatatypeFactory.NAME.getAncestors().contains(DatatypeFactory.TOKEN));
		assertTrue(DatatypeFactory.NAME.getAncestors().size() == 4);
		assertTrue(DatatypeFactory.NCNAME.getAncestors().contains(DatatypeFactory.STRING));
		assertTrue(DatatypeFactory.NCNAME.getAncestors().contains(
				DatatypeFactory.NORMALIZEDSTRING));
		assertTrue(DatatypeFactory.NCNAME.getAncestors().contains(DatatypeFactory.TOKEN));
		assertTrue(DatatypeFactory.NCNAME.getAncestors().contains(DatatypeFactory.NAME));
		assertTrue(DatatypeFactory.NCNAME.getAncestors().size() == 5);
		assertTrue(DatatypeFactory.NMTOKEN.getAncestors()
				.contains(DatatypeFactory.STRING));
		assertTrue(DatatypeFactory.NMTOKEN.getAncestors().contains(
				DatatypeFactory.NORMALIZEDSTRING));
		assertTrue(DatatypeFactory.NMTOKEN.getAncestors().contains(DatatypeFactory.TOKEN));
		assertTrue(DatatypeFactory.NMTOKEN.getAncestors().size() == 4);
		assertTrue(DatatypeFactory.NMTOKENS.getAncestors().contains(
				DatatypeFactory.STRING));
		assertTrue(DatatypeFactory.NMTOKENS.getAncestors().contains(
				DatatypeFactory.NORMALIZEDSTRING));
		assertTrue(DatatypeFactory.NMTOKENS.getAncestors()
				.contains(DatatypeFactory.TOKEN));
		assertTrue(DatatypeFactory.NMTOKENS.getAncestors().contains(
				DatatypeFactory.NMTOKEN));
		assertTrue(DatatypeFactory.NMTOKENS.getAncestors().size() == 5);
		assertTrue(DatatypeFactory.NORMALIZEDSTRING.getAncestors().contains(
				DatatypeFactory.STRING));
		assertTrue(DatatypeFactory.NORMALIZEDSTRING.getAncestors().size() == 2);
		assertTrue(DatatypeFactory.TOKEN.getAncestors().contains(DatatypeFactory.STRING));
		assertTrue(DatatypeFactory.TOKEN.getAncestors().contains(
				DatatypeFactory.NORMALIZEDSTRING));
		assertTrue(DatatypeFactory.NORMALIZEDSTRING.getAncestors().size() == 2);
		for (Datatype<?> p : list) {
			for (Datatype<?> t : p.getAncestors()) {
				assertTrue(p.getClass().getSimpleName(),
						p.getAncestors().containsAll(t.getAncestors()));
			}
		}
		//		for(int i=0;i<list.size();i++) {
		//			for(Datatype p:list.get(i).getAncestors()) {
		//				System.out.println("assertTrue("+list.get(i).getClass().getSimpleName()+".instance().getAncestors().contains("+p.getClass().getSimpleName()+".instance()));");
		//				}
		//			for(Datatype p:list.get(i).getDescendants()) {
		//				System.out.println("assertTrue("+list.get(i).getClass().getSimpleName()+".instance().getDescendants().contains("+p.getClass().getSimpleName()+".instance()));");
		//				}
		//			}
	}
}
