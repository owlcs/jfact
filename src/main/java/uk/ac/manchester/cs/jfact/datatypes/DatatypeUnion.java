package uk.ac.manchester.cs.jfact.datatypes;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

/** datatype union */
public class DatatypeUnion implements DatatypeCombination<DatatypeUnion, Datatype<?>> {

    private final Set<Datatype<?>> basics = new HashSet<>();
    private final IRI uri;
    private final Datatype<?> host;

    /**
     * @param host
     *        host
     * @param list
     *        list
     */
    public DatatypeUnion(Datatype<?> host, Collection<Datatype<?>> list) {
        this(host);
        basics.addAll(list);
    }

    /**
     * @param host
     *        host
     */
    public DatatypeUnion(Datatype<?> host) {
        uri = DatatypeFactory.getIndex("urn:union#a").getIRI();
        this.host = host;
    }

    @Override
    public Datatype<?> getHost() {
        return host;
    }

    @Override
    public Iterable<Datatype<?>> getList() {
        return basics;
    }

    @Override
    public DatatypeUnion add(Datatype<?> d) {
        DatatypeUnion toReturn = new DatatypeUnion(host, basics);
        toReturn.basics.add(d);
        return toReturn;
    }

    @Override
    public boolean isCompatible(Literal<?> l) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(l)) {
            return false;
        }
        return basics.stream().anyMatch(d -> d.isCompatible(l));
    }

    @Override
    public IRI getDatatypeIRI() {
        return uri;
    }

    @Override
    public boolean isCompatible(Datatype<?> type) {
        // must be compatible with all basics
        // host is a shortcut to them
        if (!host.isCompatible(type)) {
            return false;
        }
        return basics.stream().anyMatch(d -> d.isCompatible(type));
    }

    @Override
    public boolean isContradictory(Datatype<?> type) {
        return !isCompatible(type);
    }

    @Override
    public boolean emptyValueSpace() {
        // value space is empty if all the basics are expressions with empty
        // value space
        return basics.stream().allMatch(d -> d.isExpression() && d.asExpression().emptyValueSpace());
    }

    @Override
    public String toString() {
        return uri + "{" + basics + '}';
    }
}
