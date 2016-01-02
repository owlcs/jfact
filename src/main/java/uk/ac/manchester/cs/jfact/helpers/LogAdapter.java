package uk.ac.manchester.cs.jfact.helpers;

import java.io.Serializable;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/**
 * log
 * 
 * @author ignazio
 */
@SuppressWarnings("unused")
public interface LogAdapter extends Serializable {

    /**
     * @return true if enabled
     */
    default boolean isEnabled() {
        return false;
    }

    /**
     * @param t
     *        t
     * @param strings
     *        strings
     * @return the log adapter for chaining
     */
    default LogAdapter printTemplate(Templates t, Object... strings) {
        return this;
    }

    /**
     * @param t
     *        t
     * @param strings
     *        strings
     * @return the log adapter for chaining
     */
    default LogAdapter printTemplateInt(Templates t, int... strings) {
        return this;
    }

    /**
     * @param t
     *        t
     * @param s
     *        s
     * @param strings
     *        strings
     * @return the log adapter for chaining
     */
    default LogAdapter printTemplateMixInt(Templates t, Object s, int... strings) {
        return this;
    }

    /**
     * @param i
     *        i
     * @return the log adapter for chaining
     */
    default LogAdapter print(int i) {
        return this;
    }

    /** @return the log adapter for chaining */
    default LogAdapter println() {
        return this;
    }

    /**
     * @param d
     *        d
     * @return the log adapter for chaining
     */
    default LogAdapter print(double d) {
        return this;
    }

    /**
     * @param f
     *        f
     * @return the log adapter for chaining
     */
    default LogAdapter print(float f) {
        return this;
    }

    /**
     * @param b
     *        b
     * @return the log adapter for chaining
     */
    default LogAdapter print(boolean b) {
        return this;
    }

    /**
     * @param b
     *        b
     * @return the log adapter for chaining
     */
    default LogAdapter print(byte b) {
        return this;
    }

    /**
     * @param c
     *        c
     * @return the log adapter for chaining
     */
    default LogAdapter print(char c) {
        return this;
    }

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    default LogAdapter print(short s) {
        return this;
    }

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    default LogAdapter print(String s) {
        return this;
    }

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    default LogAdapter print(Object s) {
        return this;
    }

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    default LogAdapter print(Object... s) {
        return this;
    }

    /**
     * @param s1
     *        s1
     * @param s2
     *        s2
     * @return the log adapter for chaining
     */
    default LogAdapter print(Object s1, Object s2) {
        return this;
    }

    /**
     * @param s1
     *        s1
     * @param s2
     *        s2
     * @param s3
     *        s3
     * @return the log adapter for chaining
     */
    default LogAdapter print(Object s1, Object s2, Object s3) {
        return this;
    }

    /**
     * @param s1
     *        s1
     * @param s2
     *        s2
     * @param s3
     *        s3
     * @param s4
     *        s4
     * @return the log adapter for chaining
     */
    default LogAdapter print(Object s1, Object s2, Object s3, Object s4) {
        return this;
    }

    /**
     * @param s1
     *        s1
     * @param s2
     *        s2
     * @param s3
     *        s3
     * @param s4
     *        s4
     * @param s5
     *        s5
     * @return the log adapter for chaining
     */
    default LogAdapter print(Object s1, Object s2, Object s3, Object s4, Object s5) {
        return this;
    }
}
