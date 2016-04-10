package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;

import javax.annotation.Nullable;

import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** label that can be merged */
@PortedFrom(file = "mergableLabel.h", name = "mergableLabel")
public class MergableLabel implements Serializable {

    /** sample for all equivalent labels */
    @PortedFrom(file = "mergableLabel.h", name = "pSample") private MergableLabel pSample;

    /** Default constructor. */
    public MergableLabel() {
        pSample = this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof MergableLabel) {
            MergableLabel p = (MergableLabel) obj;
            return pSample == p.pSample;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (pSample != null) {
            return pSample.hashCode();
        }
        return super.hashCode();
    }

    /**
     * make 2 labels equal
     * 
     * @param p
     *        p
     */
    @PortedFrom(file = "mergableLabel.h", name = "merge")
    public void merge(MergableLabel p) {
        MergableLabel sample = p.resolve();
        resolve();
        if (pSample != sample) {
            pSample.pSample = sample;
        }
    }

    /**
     * make label's depth lesser than or equal 2;
     * 
     * @return sample of the label
     */
    @PortedFrom(file = "mergableLabel.h", name = "resolve")
    public MergableLabel resolve() {
        if (isSample()) {
            return this;
        }
        MergableLabel test = this;
        MergableLabel rootSample = pSample;
        while (!test.isSample()) {
            test = test.pSample;
        }
        pSample = test;
        // all elements from rootSample to the sample resolve topSample
        while (rootSample != pSample) {
            test = rootSample;
            rootSample = rootSample.pSample;
            test.pSample = pSample;
        }
        return pSample;
    }

    /** @return is given lable a sample label */
    @PortedFrom(file = "mergableLabel.h", name = "isSample")
    public boolean isSample() {
        return pSample == this;
    }
}
