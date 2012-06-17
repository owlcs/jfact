package uk.ac.manchester.cs.jfact.kernel.state;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** class for S/R local state */
public final class DLCompletionGraphSaveState {
	/** number of valid nodes */
	private int nNodes;
	/** end pointer of saved nodes */
	private int sNodes;
	/** number of used edges */
	private int nEdges;

	public DLCompletionGraphSaveState() {
		nNodes = 0;
		sNodes = 0;
		nEdges = 0;
	}

	public int getnNodes() {
		return nNodes;
	}

	public void setnNodes(final int nNodes) {
		this.nNodes = nNodes;
	}

	public int getsNodes() {
		return sNodes;
	}

	public void setsNodes(final int sNodes) {
		this.sNodes = sNodes;
	}

	public int getnEdges() {
		return nEdges;
	}

	public void setnEdges(final int nEdges) {
		this.nEdges = nEdges;
	}

	@Override
	public String toString() {
		return "CGSaveState (" + nNodes + "," + nEdges + "," + sNodes + ")";
	}
}
