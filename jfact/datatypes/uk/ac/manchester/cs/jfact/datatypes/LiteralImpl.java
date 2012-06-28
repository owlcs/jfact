package uk.ac.manchester.cs.jfact.datatypes;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

class LiteralImpl<T extends Comparable<T>> implements Literal<T> {
    private final Datatype<T> type;
    private final String value;

    //private final T typedValue;
    public LiteralImpl(final Datatype<T> type, final String value) {
        //,  T  typedValue) {
        this.type = type;
        this.value = value;
        //this.typedValue = typedValue;
    }

    public Datatype<T> getDatatypeExpression() {
        return this.type;
    }

    public String value() {
        return this.value;
    }

    public T typedValue() {
        return this.type.parseValue(this.value);
    }

    public void accept(final DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(final DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public int compareTo(final Literal<T> arg0) {
        return this.type.parseValue(this.value).compareTo(arg0.typedValue());
    }

    @Override
    public String toString() {
        return "\"" + this.value + "\"^^" + this.type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof Literal) {
            return this.type.equals(((Literal) obj).getDatatypeExpression())
                    && this.typedValue().equals(((Literal) obj).typedValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode() + this.typedValue().hashCode();
    }
}
