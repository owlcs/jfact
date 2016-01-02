package uk.ac.manchester.cs.jfact.helpers;

import java.io.Serializable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** Timer */
public class Timer implements Serializable {


    /** save the starting time of the timer */
    private long startTime;
    /** calculated time between Start() and Stop() calls */
    private long resultTime;
    /** flag to show timer is started */
    private boolean started;

    /** Default constructor. */
    public Timer() {
        startTime = 0;
        resultTime = 0;
        started = false;
    }

    /** reset */
    public void reset() {
        started = false;
        resultTime = 0;
    }

    /** @return delta */
    public long calcDelta() {
        long finishTime = System.currentTimeMillis();
        // calculate difference between current time and start time
        return finishTime - startTime;
    }

    /** start if not started */
    public void start() {
        if (!started) {
            startTime = System.currentTimeMillis();
            started = true;
        }
    }

    /** stop if not stopped */
    public void stop() {
        if (started) {
            started = false;
            resultTime += calcDelta();
        }
    }

    /** @return time */
    public long getResultTime() {
        return resultTime;
    }

    @Override
    public String toString() {
        return "Elapsed: " + resultTime;
    }
}
