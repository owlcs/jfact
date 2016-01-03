package uk.ac.manchester.cs.jfact.datatypes;

class UnsignedByteForShort extends UNSIGNEDBYTEDatatype<Short> {

    @Override
    public Short parseValue(String s) {
        short parseByte = Short.parseShort(s);
        if (parseByte < 0) {
            throw new ArithmeticException("Unsigned short required, but found: " + s);
        }
        return Short.valueOf(parseByte);
    }
}
