package uk.ac.manchester.cs.jfact.kernel;

import java.io.Serializable;

import javax.annotation.Nullable;

import conformance.PortedFrom;

/* This file is part of the JFact DL reasoner
 Copyright 2011-2013 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
/** lexeme */
@PortedFrom(file = "tLexeme.h", name = "TLexeme")
public class Lexeme implements Serializable {

    /** Lexeme's Token */
    @PortedFrom(file = "tLexeme.h", name = "token") private final Token token;
    /** pointer to information (for names) */
    @PortedFrom(file = "tLexeme.h", name = "pNE") private final NamedEntry pName;
    @PortedFrom(file = "tLexeme.h", name = "data") private final int data;

    private Lexeme(Token tok, @Nullable NamedEntry e, int i) {
        token = tok;
        pName = e;
        data = i;
    }

    /**
     * default c'tor for pointers
     * 
     * @param tok
     *        tok
     * @param p
     *        p
     */
    public Lexeme(Token tok, NamedEntry p) {
        this(tok, p, 0);
    }

    /**
     * default c'tor for pointers
     * 
     * @param tok
     *        tok
     */
    public Lexeme(Token tok) {
        this(tok, null, 0);
    }

    /**
     * default c'tor for numbers
     * 
     * @param tok
     *        tok
     * @param val
     *        val
     */
    public Lexeme(Token tok, int val) {
        this(tok, null, val);
    }

    /**
     * @param t
     *        t
     */
    public Lexeme(Lexeme t) {
        this(t.token, t.pName, t.data);
    }

    /** @return Token of given Lexeme */
    @PortedFrom(file = "tLexeme.h", name = "getToken")
    public Token getToken() {
        return token;
    }

    /** @return name pointer of given lexeme */
    @PortedFrom(file = "tLexeme.h", name = "getNE")
    public NamedEntry getNE() {
        return pName;
    }

    /** @return data value of given lexeme */
    @PortedFrom(file = "tLexeme.h", name = "getData")
    public int getData() {
        return data;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Lexeme) {
            Lexeme lex = (Lexeme) obj;
            if (!token.equals(lex.token)) {
                return false;
            }
            if (pName == null && lex.pName == null) {
                return data == lex.data;
            }
            if (pName == null) {
                return false;
            }
            return pName.equals(lex.pName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return token.hashCode() + data;
    }

    @Override
    public String toString() {
        if (pName == null) {
            if (token == Token.GE || token == Token.LE) {
                return token.getName() + " " + data;
            }
            return token.getName();
        }
        if (token == Token.INAME) {
            return '(' + pName.toString() + ')';
        } else {
            return pName.toString();
        }
    }
}
