package uk.ac.manchester.cs.jfact.kernel.state;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** class for save/restore */
public final class SaveState {
	/** states for simple-, complex- and extra labels */
	private int sc;
	private int cc;

	public SaveState() {
		sc = Integer.MAX_VALUE;
		cc = Integer.MAX_VALUE;
	}

	/** copy c'tor */
	//	public SaveState(SaveState ss) {
	//		sc = ss.sc;
	//		cc = ss.cc;
	//	}
	public int getSc() {
		return sc;
	}

	public int getCc() {
		return cc;
	}

	public void setSc(final int sc) {
		this.sc = sc;
	}

	public void setCc(final int cc) {
		this.cc = cc;
	}
}
