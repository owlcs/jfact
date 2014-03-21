package uk.ac.manchester.cs.jfact.helpers;

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
public interface LogAdapter {

    /**
     * @param t
     *        t
     * @param strings
     *        strings
     * @return the log adapter for chaining
     */
    LogAdapter printTemplate(Templates t, Object... strings);

    /**
     * @param i
     *        i
     * @return the log adapter for chaining
     */
    LogAdapter print(int i);

    /** @return the log adapter for chaining */
    LogAdapter println();

    /**
     * @param d
     *        d
     * @return the log adapter for chaining
     */
    LogAdapter print(double d);

    /**
     * @param f
     *        f
     * @return the log adapter for chaining
     */
    LogAdapter print(float f);

    /**
     * @param b
     *        b
     * @return the log adapter for chaining
     */
    LogAdapter print(boolean b);

    /**
     * @param b
     *        b
     * @return the log adapter for chaining
     */
    LogAdapter print(byte b);

    /**
     * @param c
     *        c
     * @return the log adapter for chaining
     */
    LogAdapter print(char c);

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    LogAdapter print(short s);

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    LogAdapter print(String s);

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    LogAdapter print(Object s);

    /**
     * @param s
     *        s
     * @return the log adapter for chaining
     */
    LogAdapter print(Object... s);

    /**
     * @param s1
     *        s1
     * @param s2
     *        s2
     * @return the log adapter for chaining
     */
    LogAdapter print(Object s1, Object s2);

    /**
     * @param s1
     *        s1
     * @param s2
     *        s2
     * @param s3
     *        s3
     * @return the log adapter for chaining
     */
    LogAdapter print(Object s1, Object s2, Object s3);

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
    LogAdapter print(Object s1, Object s2, Object s3, Object s4);

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
    LogAdapter print(Object s1, Object s2, Object s3, Object s4, Object s5);
}
