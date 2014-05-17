package uk.ac.manchester.cs.jfact.split;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.Original;
import conformance.PortedFrom;

/** helper class to set signature and locality class */
@PortedFrom(file = "LocalityChecker.h", name = "SigAccessor")
public class SigAccessor extends DLExpressionVisitorAdapter {

    private static final long serialVersionUID = 11000L;
    /** signature of a module */
    @PortedFrom(file = "LocalityChecker.h", name = "sig")
    protected TSignature sig;

    /**
     * @param s
     *        s
     */
    @Original
    public void setSignature(TSignature s) {
        sig = s;
    }

    /** empty sig */
    public SigAccessor() {}

    /**
     * @param s
     *        s
     */
    public SigAccessor(TSignature s) {
        setSignature(s);
    }

    /**
     * @param expr
     *        expr
     * @return true iff EXPR is a top datatype
     */
    @Original
    private static boolean isTopDT(Expression expr) {
        return expr instanceof DataTop;
    }

    /**
     * @param expr
     *        expr
     * @return true iff EXPR is a top datatype or a built-in datatype;
     */
    @Original
    public boolean isTopOrBuiltInDataType(Expression expr) {
        return isTopDT(expr) || expr instanceof Datatype<?>;
    }

    /** @return true iff concepts are treated as TOPs */
    @PortedFrom(file = "LocalityChecker.h", name = "topCLocal")
    public boolean topCLocal() {
        return sig.topCLocal();
    }

    /** @return true iff roles are treated as TOPs */
    @PortedFrom(file = "LocalityChecker.h", name = "topRLocal")
    public boolean topRLocal() {
        return sig.topRLocal();
    }
}
