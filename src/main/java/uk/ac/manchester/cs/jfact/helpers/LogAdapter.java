package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
@SuppressWarnings("javadoc")
public interface LogAdapter {
    LogAdapter printTemplate(Templates t, Object... strings);

    LogAdapter print(int i);

    LogAdapter println();

    LogAdapter print(double d);

    LogAdapter print(float f);

    LogAdapter print(boolean b);

    LogAdapter print(byte b);

    LogAdapter print(char c);

    LogAdapter print(short s);

    LogAdapter print(String s);

    LogAdapter print(Object s);

    LogAdapter print(Object... s);

    LogAdapter print(Object s1, Object s2);

    LogAdapter print(Object s1, Object s2, Object s3);

    LogAdapter print(Object s1, Object s2, Object s3, Object s4);

    LogAdapter print(Object s1, Object s2, Object s3, Object s4, Object s5);
}
