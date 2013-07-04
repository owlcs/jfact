package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
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
    public String getName() {
        return toString();
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
