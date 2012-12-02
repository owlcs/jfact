package uk.ac.manchester.cs.jfact.datatypes;

import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

class LiteralImpl<T extends Comparable<T>> implements Literal<T> {
    private final Datatype<T> type;
    private final String value;

    public LiteralImpl(Datatype<T> type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public Datatype<T> getDatatypeExpression() {
        return this.type;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public T typedValue() {
        return this.type.parseValue(this.value);
    }

    @Override
    public void accept(DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int compareTo(Literal<T> arg0) {
        return this.type.parseValue(this.value).compareTo(arg0.typedValue());
    }

    @Override
    public String toString() {
        return "\"" + this.value + "\"^^" + this.type;
    }

    @Override
    public boolean equals(Object obj) {
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
