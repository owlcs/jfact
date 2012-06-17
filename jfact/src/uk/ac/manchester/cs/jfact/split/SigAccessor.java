package uk.ac.manchester.cs.jfact.split;

import uk.ac.manchester.cs.jfact.datatypes.Datatype;
import uk.ac.manchester.cs.jfact.datatypes.cardinality;
import uk.ac.manchester.cs.jfact.kernel.dl.DataTop;
import uk.ac.manchester.cs.jfact.kernel.dl.interfaces.Expression;
import uk.ac.manchester.cs.jfact.visitors.DLExpressionVisitorAdapter;

/// helper class to set signature and locality class
public class SigAccessor extends DLExpressionVisitorAdapter {
	/// signature of a module
	TSignature sig;

	/// init c'tor
	SigAccessor(final TSignature s) {
		sig = new TSignature(s);
	}

	/// @return true iff EXPR is a top datatype
	static boolean isTopDT(final Expression expr) {
		return expr instanceof DataTop;
	}

	//	/// @return true iff EXPR is a top datatype or a built-in datatype; FIXME for now -- just top
	//	static boolean isTopOrBuiltInDT(final Expression expr) {
	//		return isTopDT(expr);
	//	}
	//
	//	/// @return true iff EXPR is a top datatype or an infinite built-in datatype; FIXME for now -- just top
	//	static boolean isTopOrBuiltInInfDT(final Expression expr) {
	//		return isTopDT(expr);
	//	}
	/// @return true iff EXPR is a top datatype or a built-in datatype;
	public boolean isTopOrBuiltInDataType(final Expression expr) {
		return isTopDT(expr) || expr instanceof Datatype<?>;
	}

	/// @return true iff EXPR is a top datatype or an infinite built-in datatype; FIXME add real/fraction later
	public boolean isTopOrBuiltInInfDataType(final Expression expr) {
		if (isTopDT(expr)) {
			return true;
		}
		return expr instanceof Datatype<?>
				&& ((Datatype<?>) expr).getCardinality().equals(
						cardinality.COUNTABLYINFINITE);
	}

	/// @return true iff concepts are treated as TOPs
	public boolean topCLocal() {
		return sig.topCLocal();
	}

	/// @return true iff roles are treated as TOPs
	public boolean topRLocal() {
		return sig.topRLocal();
	}
}
