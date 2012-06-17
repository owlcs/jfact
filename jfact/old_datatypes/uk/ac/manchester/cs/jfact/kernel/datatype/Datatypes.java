package uk.ac.manchester.cs.jfact.kernel.datatype;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.kernel.voc.Vocabulary;

@SuppressWarnings("unchecked")
public enum Datatypes {
	INT {
		@Override
		public DatatypeRepresentation<Integer> parse(String s) {
			return new IntRep(Integer.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Integer> build(Object s) {
			return new IntRep((Integer) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d)
					|| EnumSet.of(NEGINT, POSINT, NONNEGINT, NONPOSINT).contains(d);
		}
	},
	DECIMAL {
		@Override
		public DatatypeRepresentation<BigDecimal> parse(String s) {
			return new DecimalRep(new BigDecimal(s));
		}

		@Override
		public DatatypeRepresentation<BigDecimal> build(Object s) {
			return new DecimalRep((BigDecimal) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d)
					|| EnumSet.of(INT, NEGINT, POSINT, NONNEGINT, NONPOSINT).contains(d);
		}
	},
	SHORT {

		@Override
		public DatatypeRepresentation<Short> parse(String s) {
			return new ShortRep(Short.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Short> build(Object s) {
			return new ShortRep((Short) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d) || d == UNSIGNEDSHORT;
		}
	},
	UNSIGNEDSHORT {
		@Override
		public DatatypeRepresentation<Short> parse(String s) {
			return new UnsignedShortRep(Short.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Short> build(Object s) {
			return new UnsignedShortRep((Short) s);
		}
	},
	BYTE {
		@Override
		public DatatypeRepresentation<Byte> parse(String s) {
			return new ByteRep(Byte.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Byte> build(Object s) {
			return new ByteRep((Byte) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d) || d == SHORT || d == UNSIGNEDBYTE
					|| d == UNSIGNEDSHORT;
		}
	},
	UNSIGNEDBYTE {
		@Override
		public DatatypeRepresentation<Byte> parse(String s) {
			return new UnsignedByteRep(Byte.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Byte> build(Object s) {
			return new UnsignedByteRep((Byte) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d) || d == UNSIGNEDSHORT;
		}
	},
	BOOLEAN {
		@Override
		public DatatypeRepresentation<Boolean> parse(String s) {
			return new BoolRep(Boolean.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Boolean> build(Object s) {
			return new BoolRep((Boolean) s);
		}
	},
	DOUBLE {
		@Override
		public DatatypeRepresentation<Double> parse(String s) {
			return new DoubleRep(parseDouble(s));
		}

		@Override
		public DatatypeRepresentation<Double> build(Object s) {
			return new DoubleRep((Double) s);
		}
	},
	FLOAT {
		@Override
		public DatatypeRepresentation<Float> parse(String s) {
			return new FloatRep(Float.valueOf(s.replace("inf", "Infinity").replace("INF",
					"Infinity")));
		}

		@Override
		public DatatypeRepresentation<Float> build(Object s) {
			return new FloatRep((Float) s);
		}
	},
	STRING {
		@Override
		public DatatypeRepresentation<String> parse(String s) {
			return new StringRep(s);
		}

		@Override
		public DatatypeRepresentation<String> build(Object s) {
			return new StringRep((String) s);
		}
	},
	LITERAL {
		@Override
		public DatatypeRepresentation<Object> parse(String s) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DatatypeRepresentation<Object> build(Object s) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean compatible(Datatypes d) {
			return true;
		}
	},
	DATETIME {
		@Override
		public DatatypeRepresentation<XMLGregorianCalendar> parse(String s) {
			XMLGregorianCalendar cal;
			try {
				cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(s);
			} catch (DatatypeConfigurationException e) {
				throw new ReasonerInternalException(e);
			} catch (IllegalArgumentException e) {
				throw new ReasonerInternalException("Error parsing " + s, e);
			}
			return new DateTimeRep(cal);
		}

		@Override
		public DatatypeRepresentation<XMLGregorianCalendar> build(Object s) {
			return new DateTimeRep((XMLGregorianCalendar) s);
		}
	},
	POSINT {
		@Override
		public DatatypeRepresentation<Integer> parse(String s) {
			return new PosIntRep(Integer.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Integer> build(Object s) {
			return new PosIntRep((Integer) s);
		}
	},
	NEGINT {
		@Override
		public DatatypeRepresentation<Integer> parse(String s) {
			return new NegIntRep(Integer.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Integer> build(Object s) {
			return new NegIntRep((Integer) s);
		}
	},
	NONPOSINT {
		@Override
		public DatatypeRepresentation<Integer> parse(String s) {
			return new NonPosIntRep(Integer.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Integer> build(Object s) {
			return new NonPosIntRep((Integer) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d) || d == NEGINT;
		}

		@Override
		public boolean compatible(Datatypes d, Object o) {
			if (d == NONNEGINT) {
				if (o == null) {
					// undefined value, but there is one possible valid one
					return true;
				}
				if (o instanceof Integer) {
					return ((Integer) o).intValue() == 0;
				}
			}
			return super.compatible(d, o);
		}
	},
	NONNEGINT {
		@Override
		public DatatypeRepresentation<Integer> parse(String s) {
			return new NonNegIntRep(Integer.valueOf(s));
		}

		@Override
		public DatatypeRepresentation<Integer> build(Object s) {
			return new NonNegIntRep((Integer) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d) || d == POSINT;
		}

		@Override
		public boolean compatible(Datatypes d, Object o) {
			if (d == NONPOSINT) {
				if (o == null) {
					// undefined value, but there is one possible valid one
					return true;
				}
				if (o instanceof Integer) {
					return ((Integer) o).intValue() == 0;
				}
			}
			return super.compatible(d, o);
		}
	},
	REAL {
		@Override
		public DatatypeRepresentation<BigDecimal> parse(String s) {
			return new RealRep(new BigDecimal(s));
		}

		@Override
		public DatatypeRepresentation<BigDecimal> build(Object s) {
			return new RealRep((BigDecimal) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return super.compatible(d)
					|| EnumSet.complementOf(EnumSet.of(STRING, DATETIME, DOUBLE, FLOAT))
							.contains(d);
		}
	},
	RATIONAL {
		@Override
		public DatatypeRepresentation<BigDecimal> parse(String s) {
			return new RationalRep(new BigDecimal(parseDouble(s)));
		}

		@Override
		public DatatypeRepresentation<BigDecimal> build(Object s) {
			return new RationalRep((BigDecimal) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return d != DECIMAL && REAL.compatible(d);
		}
	},
	INTEGER {
		@Override
		public DatatypeRepresentation<BigInteger> parse(String s) {
			return new IntegerRep(new BigInteger(s));
		}

		@Override
		public DatatypeRepresentation<BigInteger> build(Object s) {
			return new IntegerRep((BigInteger) s);
		}

		@Override
		public boolean compatible(Datatypes d) {
			return d != DECIMAL && REAL.compatible(d);
		}
	},
	FRESH {
		@Override
		public boolean compatible(Datatypes d) {
			return true;
		}

		@Override
		public DatatypeRepresentation<Object> build(Object s) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DatatypeRepresentation<Object> parse(String s) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	public abstract <O> DatatypeRepresentation<O> parse(String s);

	public abstract <O> DatatypeRepresentation<O> build(O s);

	public boolean compatible(Datatypes d) {
		return d == this;// || d == LITERAL;
	}

	public boolean compatible(Datatypes d, Object o) {
		return compatible(d);
	}

	static final String pattern = "\\-?[0-9]+(\\.[0-9]+)?([eE]\\-?[0-9]+)?";
	static final String fractionPattern = "(\\-?)([0-9]+)/([0-9]+)";
	static final Pattern regularFloat = Pattern.compile(pattern);
	static final Pattern fraction = Pattern.compile(fractionPattern);

	public final static Float parseFloat(String s) {
		Matcher m = regularFloat.matcher(s);
		if (m.matches()) {
			return Float.valueOf(s);
		} else {
			if (s.contains("inf") || s.contains("INF") || s.contains("Inf")) {
				return Float.valueOf(s.replace("inf", "Infinity").replace("INF",
						"Infinity"));
			}
			Pattern p = Pattern.compile(fractionPattern);
			m = p.matcher(s);
			if (m.matches()) {
				return (m.group(1).length() == 0 ? +1 : -1)
						* Float.parseFloat(m.group(2)) / Float.parseFloat(m.group(3));
			}
			throw new NumberFormatException("Unparsable float: " + s);
		}
	}

	public final static Double parseDouble(String s) {
		Matcher m = regularFloat.matcher(s);
		if (m.matches()) {
			return Double.valueOf(s);
		} else {
			if (s.contains("inf") || s.contains("INF") || s.contains("Inf")) {
				return Double.valueOf(s.replace("inf", "Infinity").replace("INF",
						"Infinity"));
			}
			Pattern p = Pattern.compile(fractionPattern);
			m = p.matcher(s);
			if (m.matches()) {
				return (m.group(1).length() == 0 ? +1 : -1)
						* Double.parseDouble(m.group(2)) / Double.parseDouble(m.group(3));
			}
			throw new NumberFormatException("Unparsable double: " + s);
		}
	}

	private final static Map<String, Datatypes> datatypeMap = buildDatatypeMap();

	public static Datatypes getBuiltInDataType(String DTName) {
		if (datatypeMap.containsKey(DTName)) {
			return datatypeMap.get(DTName);
		}
		// FIXME!! Data Top
		// FIXME extensions
		throw new ReasonerInternalException("Unsupported datatype " + DTName);
	}

	private static Map<String, Datatypes> buildDatatypeMap() {
		Map<String, Datatypes> toReturn = new HashMap<String, Datatypes>();
		toReturn.put(Vocabulary.LITERAL, LITERAL);
		toReturn.put(Vocabulary.ANY_SIMPLE_TYPE, LITERAL);
		toReturn.put(Vocabulary.ANY_TYPE, LITERAL);
		toReturn.put(Vocabulary.XML_ANY_SIMPLE_TYPE, LITERAL);
		toReturn.put(Vocabulary.PLAIN_LITERAL, STRING);
		toReturn.put(Vocabulary.XMLLITERAL, STRING);
		toReturn.put(Vocabulary.STRING, STRING);
		toReturn.put(Vocabulary.ANY_URI, STRING);
		toReturn.put(Vocabulary.INTEGER, INTEGER);
		toReturn.put(Vocabulary.INT, INT);
		toReturn.put(Vocabulary.NON_NEGATIVE_INTEGER, NONNEGINT);
		toReturn.put(Vocabulary.POSITIVE_INTEGER, POSINT);
		toReturn.put(Vocabulary.NEGATIVE_INTEGER, NEGINT);
		toReturn.put(Vocabulary.SHORT, SHORT);
		toReturn.put(Vocabulary.BYTE, BYTE);
		toReturn.put(Vocabulary.UNSIGNEDBYTE, UNSIGNEDBYTE);
		toReturn.put(Vocabulary.UNSIGNEDINT, INT);
		toReturn.put(Vocabulary.UNSIGNEDSHORT, UNSIGNEDSHORT);
		toReturn.put(Vocabulary.NONPOSINT, NONPOSINT);
		toReturn.put(Vocabulary.FLOAT, FLOAT);
		toReturn.put(Vocabulary.DOUBLE, DOUBLE);
		toReturn.put(Vocabulary.REAL, REAL);
		toReturn.put(Vocabulary.RATIONAL, RATIONAL);
		toReturn.put(Vocabulary.DECIMAL, DECIMAL);
		toReturn.put(Vocabulary.BOOLEAN, BOOLEAN);
		toReturn.put(Vocabulary.DATETIME, DATETIME);
		//XXX this is wrong
		toReturn.put(Vocabulary.DATE, DATETIME);
		toReturn.put(Vocabulary.UNSIGNEDLONG, INT);
		toReturn.put(Vocabulary.LONG, INT);
		return toReturn;
	}
}

class IntRep implements DatatypeRepresentation<Integer> {
	protected Integer value;

	public Datatypes getDatatype() {
		return Datatypes.INT;
	}

	public IntRep(Integer v) {
		value = v;
	}

	public int compareTo(DatatypeRepresentation<Integer> o) {
		return value.compareTo(o.getValue());
	}

	public Integer getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value++;
			return false;
		}
		return excl;
	}

	public boolean correctMax(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value--;
			return false;
		}
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Integer> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass().equals(IntRep.class)) {
			return value.equals(((IntRep) obj).getValue());
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return value.hashCode();
	}
}

class DecimalRep implements DatatypeRepresentation<BigDecimal> {
	protected BigDecimal value;

	public Datatypes getDatatype() {
		return Datatypes.DECIMAL;
	}

	public DecimalRep(BigDecimal v) {
		value = v;
	}

	public int compareTo(DatatypeRepresentation<BigDecimal> o) {
		return value.compareTo(o.getValue());
	}

	public BigDecimal getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value = value.add(BigDecimal.ONE);
			return false;
		}
		return excl;
	}

	public boolean correctMax(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value = value.subtract(BigDecimal.ONE);
			return false;
		}
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<BigDecimal> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass().equals(DecimalRep.class)) {
			return value.equals(((DecimalRep) obj).getValue());
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return value.hashCode();
	}
}

class PosIntRep extends IntRep {
	@Override
	public Datatypes getDatatype() {
		return Datatypes.POSINT;
	}

	public PosIntRep(Integer v) {
		super(v);
		if (v.intValue() < 1) {
			throw new IllegalArgumentException("out of range: " + v);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass().equals(PosIntRep.class)) {
			return value.equals(((PosIntRep) obj).getValue());
		}
		return false;
	}
}

class NegIntRep extends IntRep {
	@Override
	public Datatypes getDatatype() {
		return Datatypes.NEGINT;
	}

	public NegIntRep(Integer v) {
		super(v);
		if (v.intValue() > -1) {
			throw new IllegalArgumentException("out of range: " + v);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass().equals(NegIntRep.class)) {
			return value.equals(((NegIntRep) obj).getValue());
		}
		return false;
	}
}

class NonPosIntRep extends IntRep {
	@Override
	public Datatypes getDatatype() {
		return Datatypes.NONPOSINT;
	}

	public NonPosIntRep(Integer v) {
		super(v);
		if (v.intValue() > 0) {
			throw new IllegalArgumentException("out of range: " + v);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass().equals(NonPosIntRep.class)) {
			return value.equals(((NonPosIntRep) obj).getValue());
		}
		return false;
	}
}

class NonNegIntRep extends IntRep {
	@Override
	public Datatypes getDatatype() {
		return Datatypes.NONNEGINT;
	}

	public NonNegIntRep(Integer v) {
		super(v);
		if (v.intValue() < 0) {
			throw new IllegalArgumentException("out of range: " + v);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass().equals(NonNegIntRep.class)) {
			return value.equals(((NonNegIntRep) obj).getValue());
		}
		return false;
	}
}

class DateTimeRep implements DatatypeRepresentation<XMLGregorianCalendar> {
	protected XMLGregorianCalendar value;

	public Datatypes getDatatype() {
		return Datatypes.DATETIME;
	}

	public DateTimeRep(XMLGregorianCalendar v) {
		value = v;
	}

	public int compareTo(DatatypeRepresentation<XMLGregorianCalendar> o) {
		return value.compare(o.getValue());
	}

	public XMLGregorianCalendar getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<XMLGregorianCalendar> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof DateTimeRep) {
			return value.equals(((DateTimeRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

class ShortRep implements DatatypeRepresentation<Short> {
	protected Short value;

	public Datatypes getDatatype() {
		return Datatypes.SHORT;
	}

	public ShortRep(Short v) {
		value = v;
	}

	public int compareTo(DatatypeRepresentation<Short> o) {
		return value.compareTo(o.getValue());
	}

	public Short getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value++;
			return false;
		}
		return excl;
	}

	public boolean correctMax(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value--;
			return false;
		}
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Short> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof ShortRep) {
			return value.equals(((ShortRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

class UnsignedShortRep implements DatatypeRepresentation<Short> {
	protected Short value;

	public Datatypes getDatatype() {
		return Datatypes.SHORT;
	}

	public UnsignedShortRep(Short v) {
		if (v.shortValue() < 0) {
			throw new IllegalArgumentException("v cannot be negative! v: "
					+ v.shortValue());
		}
		value = v;
	}

	public int compareTo(DatatypeRepresentation<Short> o) {
		return value.compareTo(o.getValue());
	}

	public Short getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value++;
			return false;
		}
		return excl;
	}

	public boolean correctMax(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value--;
			return false;
		}
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Short> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof UnsignedShortRep) {
			return value.equals(((UnsignedShortRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

class ByteRep implements DatatypeRepresentation<Byte> {
	protected Byte value;

	public Datatypes getDatatype() {
		return Datatypes.BYTE;
	}

	public ByteRep(Byte v) {
		value = v;
	}

	public int compareTo(DatatypeRepresentation<Byte> o) {
		return value.compareTo(o.getValue());
	}

	public Byte getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value++;
			return false;
		}
		return excl;
	}

	public boolean correctMax(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value--;
			return false;
		}
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Byte> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof ByteRep) {
			return value.equals(((ByteRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

class UnsignedByteRep implements DatatypeRepresentation<Byte> {
	protected Byte value;

	public Datatypes getDatatype() {
		return Datatypes.UNSIGNEDBYTE;
	}

	public UnsignedByteRep(Byte v) {
		if (v.byteValue() < 0) {
			throw new IllegalArgumentException("v cannot be negative! v: "
					+ v.byteValue());
		}
		value = v;
	}

	public int compareTo(DatatypeRepresentation<Byte> o) {
		return value.compareTo(o.getValue());
	}

	public Byte getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value++;
			return false;
		}
		return excl;
	}

	public boolean correctMax(boolean excl) {
		if (excl) {
			// transform (n,} into [n+1,}
			value--;
			return false;
		}
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Byte> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof UnsignedByteRep) {
			return value.equals(((UnsignedByteRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

/// representation of a boolean value
class BoolRep implements DatatypeRepresentation<Boolean> {
	/// value of a bool: 0/1
	protected Boolean value;

	public Datatypes getDatatype() {
		return Datatypes.BOOLEAN;
	}

	/// main comparison method; @returns -1 if this < val, 0 if this == val, 1 if this > val
	public int compareTo(DatatypeRepresentation<Boolean> val) {
		return value.compareTo(val.getValue());
	}

	public BoolRep(Boolean b) {
		value = b;
	}

	public Boolean getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Boolean> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof BoolRep) {
			return value.equals(((BoolRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

/// representation of a string value
class StringRep implements DatatypeRepresentation<String> {
	/// string itself
	protected String value;

	public Datatypes getDatatype() {
		return Datatypes.STRING;
	}

	public int compareTo(DatatypeRepresentation<String> val) {
		return value.compareTo(((StringRep) val).value);
	}

	public StringRep(String name) {
		value = name;
	}

	public String getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<String> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof StringRep) {
			return value.equals(((StringRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

/// representation of a float value
class FloatRep implements DatatypeRepresentation<Float> {
	/// float value of a string
	protected Float value;

	public Datatypes getDatatype() {
		return Datatypes.FLOAT;
	}

	public int compareTo(DatatypeRepresentation<Float> val) {
		return value.compareTo(val.getValue());
	}

	public FloatRep(Float f) {
		value = f;
	}

	public Float getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Float> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof FloatRep) {
			return value.equals(((FloatRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

/// representation of a double value
class DoubleRep implements DatatypeRepresentation<Double> {
	/// double value of a string
	protected Double value;

	public Datatypes getDatatype() {
		return Datatypes.DOUBLE;
	}

	public int compareTo(DatatypeRepresentation<Double> val) {
		return value.compareTo(val.getValue());
	}

	public DoubleRep(Double d) {
		value = d;
	}

	public Double getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<Double> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof DoubleRep) {
			return value.equals(((DoubleRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

class RealRep implements DatatypeRepresentation<BigDecimal> {
	/// double value of a string
	protected BigDecimal value;

	public Datatypes getDatatype() {
		return Datatypes.REAL;
	}

	public int compareTo(DatatypeRepresentation<BigDecimal> val) {
		return value.compareTo(val.getValue());
	}

	public RealRep(BigDecimal d) {
		value = d;
	}

	public BigDecimal getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<BigDecimal> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof RealRep) {
			return value.equals(((RealRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

class RationalRep implements DatatypeRepresentation<BigDecimal> {
	/// double value of a string
	protected BigDecimal value;

	public Datatypes getDatatype() {
		return Datatypes.RATIONAL;
	}

	public int compareTo(DatatypeRepresentation<BigDecimal> val) {
		return value.compareTo(val.getValue());
	}

	public RationalRep(BigDecimal d) {
		value = d;
	}

	public RationalRep(String d) {
		this(parse(d));
	}

	private static BigDecimal parse(String s) {
		int i = s.indexOf('/');
		if (i == -1) {
			throw new IllegalArgumentException(
					"invalid string used: no '/' character separating longs: " + s);
		}
		double n = Long.parseLong(s.substring(0, i));
		double d = Long.parseLong(s.substring(i + 1));
		BigDecimal b = new BigDecimal(n / d);
		return b;
	}

	public BigDecimal getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<BigDecimal> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof RationalRep) {
			return value.equals(((RationalRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

class IntegerRep implements DatatypeRepresentation<BigInteger> {
	/// double value of a string
	protected BigInteger value;

	public Datatypes getDatatype() {
		return Datatypes.INTEGER;
	}

	public int compareTo(DatatypeRepresentation<BigInteger> val) {
		return value.compareTo(val.getValue());
	}

	public IntegerRep(BigInteger d) {
		value = d;
	}

	public IntegerRep(String d) {
		this(parse(d));
	}

	private static BigInteger parse(String s) {
		int i = s.indexOf('/');
		if (i == -1) {
			throw new IllegalArgumentException(
					"invalid string used: no '/' character separating longs: " + s);
		}
		return new BigInteger(s);
	}

	public BigInteger getValue() {
		return value;
	}

	public boolean correctMin(boolean excl) {
		return excl;
	}

	public boolean correctMax(boolean excl) {
		return excl;
	}

	public boolean lesser(DatatypeRepresentation<BigInteger> other) {
		return compareTo(other) < 0;
	}

	@Override
	public String toString() {
		return " " + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof IntegerRep) {
			return value.equals(((IntegerRep) obj).getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
