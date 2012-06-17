package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version. 
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
public final class Timer {
	/** save the starting time of the timer */
	private long startTime;
	/** calculated time between Start() and Stop() calls */
	private long resultTime;
	/** flag to show timer is started */
	private boolean started;

	public Timer() {
		startTime = 0;
		resultTime = 0;
		started = false;
	}

	public void reset() {
		started = false;
		resultTime = 0;
	}

	public long calcDelta() {
		long finishTime = System.currentTimeMillis();
		// calculate difference between cuttent time and start time
		return finishTime - startTime;
	}

	public void start() {
		if (!started) {
			startTime = System.currentTimeMillis();
			started = true;
		}
	}

	public void stop() {
		if (started) {
			started = false;
			resultTime += calcDelta();
		}
	}

	public long getResultTime() {
		return resultTime;
	}

	@Override
	public String toString() {
		return "Elapsed: " + resultTime;
	}
}
