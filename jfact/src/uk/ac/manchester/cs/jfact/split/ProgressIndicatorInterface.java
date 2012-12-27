package uk.ac.manchester.cs.jfact.split;

import conformance.PortedFrom;

/** interface of the progress indicator */
@PortedFrom(file = "ProgressIndicatorInterface.h", name = "ProgressIndicatorInterface")
abstract class ProgressIndicatorInterface {
    /** limit of the progress: indicate [0..uLimit] */
    long uLimit;
    /** current value of an indicator */
    long uCurrent;

    /** initial exposure method: can be overriden in derived classes */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "initExposure")
    void initExposure() {}

    /** indicate current value somehow */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "expose")
    abstract void expose();

    /** check whether the limit is reached */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "checkMax")
    boolean checkMax() {
        if (uCurrent > uLimit) {
            uCurrent = uLimit;
            return true;
        } else {
            return false;
        }
    }

    /** empty c'tor */
    ProgressIndicatorInterface() {
        uLimit = 0;
        uCurrent = 0;
    }

    /** init c'tor */
    ProgressIndicatorInterface(long limit) {
        uCurrent = 0;
        setLimit(limit);
    }

    /** set indicator to a given VALUE */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "setIndicator")
    void setIndicator(long value) {
        if (uCurrent != value) {
            uCurrent = value;
            checkMax();
            expose();
        }
    }

    /** increment current value of an indicator to DELTA steps */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "incIndicator")
    void incIndicator(long delta) {
        setIndicator(uCurrent + delta);
    }

    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "incIndicator")
    void incIndicator() {
        setIndicator(uCurrent + 1);
    }

    /** set indicator to 0 */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "reset")
    void reset() {
        setIndicator(0);
    }

    /** set the limit of an indicator to a given VALUE */
    @PortedFrom(file = "ProgressIndicatorInterface.h", name = "setLimit")
    protected void setLimit(long limit) {
        uLimit = limit;
        reset();
        initExposure();
    }
}
