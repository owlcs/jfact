package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

/** The Class UnreachableSituationException. */
public class UnreachableSituationException extends ReasonerInternalException {



    /** Instantiates a new unreachable situation exception. */
    public UnreachableSituationException() {
        super("Unreachable situation!");
    }

    /**
     * Instantiates a new unreachable situation exception.
     * 
     * @param text
     *        the text
     */
    public UnreachableSituationException(String text) {
        super("Unreachable situation!\n" + text);
    }

    /**
     * Instantiates a new unreachable situation exception.
     * 
     * @param text
     *        the text
     * @param cause
     *        the cause
     */
    public UnreachableSituationException(String text, Throwable cause) {
        super("Unreachable situation!\n" + text, cause);
    }
}
