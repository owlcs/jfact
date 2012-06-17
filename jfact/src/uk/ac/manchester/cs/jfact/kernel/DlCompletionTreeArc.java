package uk.ac.manchester.cs.jfact.kernel;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import uk.ac.manchester.cs.jfact.dep.DepSet;
import uk.ac.manchester.cs.jfact.helpers.LogAdapter;
import uk.ac.manchester.cs.jfact.helpers.Templates;

public final class DlCompletionTreeArc {
	/** pointer to "to" node */
	private final DlCompletionTree node;
	/** role, labelling given arc */
	protected Role role;
	/** dep-set of the arc */
	protected DepSet depSet;
	/** pointer to reverse arc */
	protected DlCompletionTreeArc reverse;
	/** true if the edge going from a predecessor to a successor */
	private boolean succEdge = true;

	/**
	 * init an arc with R as a label and NODE on given LEVEL; use it inside
	 * MAKEARCS only
	 */
	/** class for restoring edge */
	final static class EdgeRestorer extends Restorer {
		private final DlCompletionTreeArc arc;
		private final Role role;

		public EdgeRestorer(final DlCompletionTreeArc q) {
			arc = q;
			role = q.role;
		}

		@Override
		public void restore() {
			arc.role = role;
			arc.reverse.role = role.inverse();
		}
	}

	/** class for restoring dep-set */
	final static class EdgeDepRestorer extends Restorer {
		private final DlCompletionTreeArc arc;
		private final DepSet dep;

		public EdgeDepRestorer(final DlCompletionTreeArc q) {
			arc = q;
			dep = DepSet.create(q.getDep());
		}

		@Override
		public void restore() {
			arc.depSet = DepSet.create(dep);
		}
	}

	/** set given arc as a reverse of current */
	public void setReverse(final DlCompletionTreeArc v) {
		reverse = v;
		v.reverse = this;
	}

	public DlCompletionTreeArc(final Role r, final DepSet dep, final DlCompletionTree n) {
		role = r;
		depSet = DepSet.create(dep);
		node = n;
		reverse = null;
	}

	/** get label of the edge */
	public Role getRole() {
		return role;
	}

	/** get dep-set of the edge */
	public DepSet getDep() {
		return depSet;
	}

	/** set the successor field */
	public void setSuccEdge(final boolean val) {
		succEdge = val;
	}

	/** @return true if the edge is the successor one */
	public boolean isSuccEdge() {
		return succEdge;
	}

	/** @return true if the edge is the predecessor one */
	public boolean isPredEdge() {
		return !succEdge;
	}

	/** get (RW) access to the end of arc */
	public DlCompletionTree getArcEnd() {
		return node;
	}

	/** get access to reverse arc */
	public DlCompletionTreeArc getReverse() {
		return reverse;
	}

	/** check if arc is labelled by a super-role of PROLE */
	public boolean isNeighbour(final Role pRole) {
		return role != null && role.lesserequal(pRole);
	}

	/** same as above; fills DEP with current DEPSET if so */
	public boolean isNeighbour(final Role pRole, final DepSet dep) {
		if (isNeighbour(pRole)) {
			dep.clear();
			dep.add(depSet);
			return true;
		}
		return false;
	}

	/** is arc merged to another */
	public boolean isIBlocked() {
		return role == null;
	}

	/** check whether the edge is reflexive */
	public boolean isReflexiveEdge() {
		return node.equals(reverse.node);
	}

	/** save and invalidate arc (together with reverse arc) */
	public Restorer save() {
		if (role == null) {
			throw new IllegalArgumentException();
		}
		Restorer ret = new EdgeRestorer(this);
		role = null;
		reverse.role = null;
		return ret;
	}

	/** add dep-set to an edge; return restorer */
	public Restorer addDep(final DepSet dep) {
		if (dep.isEmpty()) {
			throw new IllegalArgumentException();
		}
		Restorer ret = new EdgeDepRestorer(this);
		depSet.add(dep);
		return ret;
	}

	/** print current arc */
	public void print(final LogAdapter o) {
		o.printTemplate(Templates.DLCOMPLETIONTREEARC,
				isIBlocked() ? "-" : role.getName(), depSet);
	}
}
