package uk.ac.manchester.cs.jfact.kernel.dl;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.kernel.NamedEntry;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.NamedEntity;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ObjectRoleExpression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorEx;

public final class ObjectRoleName implements ObjectRoleExpression, NamedEntity {
    /** name of the entity */
    protected final String Name;
    private NamedEntry entry;

    public NamedEntry getEntry() {
        return entry;
    }

    public void setEntry(final NamedEntry e) {
        entry = e;
    }

    public ObjectRoleName(final String name) {
        Name = name;
    }

    public void accept(final DLExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(final DLExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + Name + ")";
    }
}
