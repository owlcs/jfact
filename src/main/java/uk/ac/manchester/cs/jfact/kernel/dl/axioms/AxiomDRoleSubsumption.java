package uk.ac.manchester.cs.jfact.kernel.dl.axioms;

import java.util.stream.Stream;

import javax.annotation.Nullable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.model.OWLAxiom;

import conformance.PortedFrom;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.DataRoleExpression;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitor;
import uk.ac.manchester.cs.jfact.visitors.DLAxiomVisitorEx;

/** data role subsumption */
@PortedFrom(file = "tDLAxiom.h", name = "TDLAxiomDRoleSubsumption")
public class AxiomDRoleSubsumption extends AxiomSingleDRole {

    @PortedFrom(file = "tDLAxiom.h", name = "SubRole") private final DataRoleExpression subRole;

    /**
     * @param ax
     *        ax
     * @param subRole
     *        subRole
     * @param supRole
     *        supRole
     */
    @PortedFrom(file = "tDLAxiom.h", name = "SubRole")
    public AxiomDRoleSubsumption(OWLAxiom ax, DataRoleExpression subRole, DataRoleExpression supRole) {
        super(ax, supRole);
        this.subRole = subRole;
    }

    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    public void accept(DLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    @Nullable
    @Override
    @PortedFrom(file = "tDLAxiom.h", name = "accept")
    public <O> O accept(DLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    /** @return role */
    @PortedFrom(file = "tDLAxiom.h", name = "getSubRole")
    public DataRoleExpression getSubRole() {
        return subRole;
    }

    @Override
    public Stream<Expression> namedEntitySignature() {
        return Stream.of(role, subRole);
    }
}
