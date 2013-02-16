package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.cardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;
import conformance.Original;
import conformance.PortedFrom;

/** helper class to set signature and locality class */
@PortedFrom(file = "LocalityChecker.h", name = "SigAccessor")
public class SigAccessor extends DLExpressionVisitorAdapter {
    /** signature of a module */
    @PortedFrom(file = "LocalityChecker.h", name = "sig")
    TSignature sig;

    /** @param s */
    @Original
    public void setSignature(TSignature s) {
        sig = s;
    }

    /** @return true iff EXPR is a top datatype */
    @Original
    static boolean isTopDT(Expression expr) {
        return expr instanceof DataTop;
    }

    /** @param expr
     * @return true iff EXPR is a top datatype or a built-in datatype; */
    @Original
    public boolean isTopOrBuiltInDataType(Expression expr) {
        return isTopDT(expr) || expr instanceof Datatype<?>;
    }

    /** @param expr
     * @return true iff EXPR is a top datatype or an infinite built-in datatype;
     *         FIXME add real/fraction later */
    @Original
    public boolean isTopOrBuiltInInfDataType(Expression expr) {
        if (isTopDT(expr)) {
            return true;
        }
        return expr instanceof Datatype<?>
                && ((Datatype<?>) expr).getCardinality().equals(
                        cardinality.COUNTABLYINFINITE);
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
