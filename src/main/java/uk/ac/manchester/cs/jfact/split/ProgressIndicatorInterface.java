package uk.ac.manchester.cs.jfact.split;

import java.io.Serializable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import conformance.PortedFrom;

/** interface of the progress indicator */
@PortedFrom(file = "ProgressIndicatorInterface.h", name = "ProgressIndicatorInterface")
abstract class ProgressIndicatorInterface implements Serializable {
    private static final long serialVersionUID = 11000L;
    /** limit of the progress: indicate [0..uLimit] */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "uLimit")
    private long uLimit;
    /** current value of an indicator */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "uCurrent")
    private long uCurrent;

    /** initial exposure method: can be overriden in derived classes */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "initExposure")
    private void initExposure() {}

    /** indicate current value somehow */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "expose")
    protected abstract void expose();

    /** check whether the limit is reached
     * 
     * @return true if limit reached */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "checkMax")
    private boolean checkMax() {
        if (uCurrent > uLimit) {
            uCurrent = uLimit;
            return true;
        } else {
            return false;
        }
    }

    /** empty c'tor */
    protected ProgressIndicatorInterface() {
        uLimit = 0;
        uCurrent = 0;
    }

    /** init c'tor
     * 
     * @param limit
     *            limit */
    protected ProgressIndicatorInterface(long limit) {
        uCurrent = 0;
        setLimit(limit);
    }

    /** set indicator to a given VALUE
     * 
     * @param value
     *            value */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "setIndicator")
    protected void setIndicator(long value) {
        if (uCurrent != value) {
            uCurrent = value;
            checkMax();
            expose();
        }
    }

    /** increment current value of an indicator to DELTA steps
     * 
     * @param delta
     *            delta */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "incIndicator")
    protected void incIndicator(long delta) {
        setIndicator(uCurrent + delta);
    }

    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "incIndicator")
    protected void incIndicator() {
        setIndicator(uCurrent + 1);
    }

    /** set indicator to 0 */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "reset")
    protected void reset() {
        setIndicator(0);
    }

    /** set the limit of an indicator to a given VALUE
     * 
     * @param limit
     *            limit */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "setLimit")
    protected void setLimit(long limit) {
        uLimit = limit;
        reset();
        initExposure();
    }
}
