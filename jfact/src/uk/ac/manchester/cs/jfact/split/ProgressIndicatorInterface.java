package uk.ac.manchester.cs.jfact.split;

/**  interface of the progress indicator */
abstract class ProgressIndicatorInterface {
    /**  limit of the progress: indicate [0..uLimit] */
    long uLimit;
    /**  current value of an indicator */
    long uCurrent;

    /**  initial exposure method: can be overriden in derived classes */
    void initExposure() {}

    /**  indicate current value somehow */
    abstract void expose();

    /**  check whether the limit is reached */
    boolean checkMax() {
        if (uCurrent > uLimit) {
            uCurrent = uLimit;
            return true;
        } else {
            return false;
        }
    }

    /**  empty c'tor */
    ProgressIndicatorInterface() {
        uLimit = 0;
        uCurrent = 0;
    }

    /**  init c'tor */
    ProgressIndicatorInterface(long limit) {
        uCurrent = 0;
        setLimit(limit);
    }

    /**  set indicator to a given VALUE */
    void setIndicator(long value) {
        if (uCurrent != value) {
            uCurrent = value;
            checkMax();
            expose();
        }
    }

    /**  increment current value of an indicator to DELTA steps */
    void incIndicator(long delta) {
        setIndicator(uCurrent + delta);
    }

    void incIndicator() {
        setIndicator(uCurrent + 1);
    }

    /**  set indicator to 0 */
    void reset() {
        setIndicator(0);
    }

    /**  set the limit of an indicator to a given VALUE */
    protected void setLimit(long limit) {
        uLimit = limit;
        reset();
        initExposure();
    }
}
