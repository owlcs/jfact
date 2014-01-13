package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.concurrent.atomic.AtomicLong;

import org.semanticweb.owlapi.model.IRI;

import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.ConceptExpression;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRQuery;
import uk.ac.manchester.cs.jfact.kernel.queryobjects.QRVariable;
import conformance.PortedFrom;

/** term assigner */
@PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")
public class TermAssigner extends BuildELIOConcept {
    private static final long serialVersionUID = 11000L;
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "Factory")
    private final AtomicLong Factory = new AtomicLong();

    /** @param conjunctiveQueryFolding
     *            conjunctiveQueryFolding
     * @param query
     *            query */
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "TermAssigner")
    public TermAssigner(ConjunctiveQueryFolding conjunctiveQueryFolding, QRQuery query) {
        super(conjunctiveQueryFolding, query);
    }

    @Override
    @PortedFrom(file = "ConjunctiveQueryFolding.cpp", name = "createConceptByVar")
    protected ConceptExpression createConceptByVar(QRVariable v) {
        if (Query.isFreeVar(v)) {
            String string = conjunctiveQueryFolding.getNewVarMap().get(v).getName() + ":"
                    + Factory.incrementAndGet();
            ConceptExpression concept = conjunctiveQueryFolding.getpEM().concept(
                    IRI.create(string));
            conjunctiveQueryFolding.addNominal(concept);
            return concept;
        }
        return conjunctiveQueryFolding.getpEM().top();
    }
}
