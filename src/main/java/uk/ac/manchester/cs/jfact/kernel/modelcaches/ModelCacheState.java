package uk.ac.manchester.cs.jfact.kernel.modelcaches;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** status of model cache or merge operation */
public enum ModelCacheState {
    //@formatter:off
    /** clash in model/merging fails because of direct contradiction; */        INVALID,
    /** valid model/success in merging; */                                      VALID,
    /** incorrect model/merging fails because of incompleteness of procedure; */FAILED,
    /** untested model cache. */                                                UNKNOWN;
    //@formatter:on

    /** @return is this invalid? */
    public boolean usageByState() {
        return this == INVALID;
    }
}
