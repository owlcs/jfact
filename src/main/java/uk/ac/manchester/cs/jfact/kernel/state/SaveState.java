package uk.ac.manchester.cs.jfact.kernel.state;

import java.io.Serializable;

import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** class for save/restore */
@PortedFrom(file = "CGLabel.h", name = "SaveState")
public class SaveState implements Serializable {


    /** states for simple-, complex- and extra labels */
    private int sc;
    private int cc;

    /** Default constructor. */
    public SaveState() {
        sc = Integer.MAX_VALUE;
        cc = Integer.MAX_VALUE;
    }

    /** @return sc */
    public int getSc() {
        return sc;
    }

    /** @return cc */
    public int getCc() {
        return cc;
    }

    /**
     * @param sc
     *        sc
     */
    public void setSc(int sc) {
        this.sc = sc;
    }

    /**
     * @param cc
     *        cc
     */
    public void setCc(int cc) {
        this.cc = cc;
    }
}
