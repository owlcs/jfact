package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;

import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** restorer */
@PortedFrom(file = "tRestorer.h", name = "TRestorer")
public abstract class Restorer implements Serializable {


    @PortedFrom(file = "tRestorer.h", name = "lev")
    private int raresavestackLevel;

    /** restore an object based on saved information */
    @PortedFrom(file = "tRestorer.h", name = "restore")
    public abstract void restore();

    /** @return for accessing the level on TRareSaveStack */
    @PortedFrom(file = "tRestorer.h", name = "getLevel")
    public int getRaresavestackLevel() {
        return raresavestackLevel;
    }

    /**
     * for accessing the level on TRareSaveStack
     * 
     * @param raresavestackLevel
     *        raresavestackLevel
     */
    @PortedFrom(file = "tRestorer.h", name = "setLevel")
    public void setRaresavestackLevel(int raresavestackLevel) {
        this.raresavestackLevel = raresavestackLevel;
    }
}
