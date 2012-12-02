package uk.ac.manchester.cs.jfact.helpers;

/* This file is part of the JFact DL reasoner
 Copyright 2011 by Ignazio Palmisano, Dmitry Tsarkov, University of Manchester
 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA*/
import static uk.ac.manchester.cs.jfact.kernel.Token.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.semanticweb.owlapi.reasoner.ReasonerInternalException;

import uk.ac.manchester.cs.jfact.kernel.Lexeme;
import uk.ac.manchester.cs.jfact.kernel.Token;

public abstract class DLTree {
    private static final CloningVisitor cloner = new CloningVisitor();
    /** element in the tree node */
    protected Lexeme elem;
    /** children collection */
    protected List<DLTree> children;
    protected DLTree ancestor;

    public DLTree(Lexeme Init) {
        elem = Init;
    }

    public Token token() {
        return elem.getToken();
    }

    public boolean isTOP() {
        return elem.getToken() == TOP;
    }

    public boolean isNOT() {
        return elem.getToken() == NOT;
    }

    public boolean isBOTTOM() {
        return elem.getToken() == BOTTOM;
    }

    public boolean isAND() {
        return elem.getToken() == AND;
    }

    public Lexeme elem() {
        return elem;
    }

    public abstract DLTree getChild();

    public abstract DLTree getLeft();

    public abstract DLTree getRight();

    public DLTree getAncestor() {
        return ancestor;
    }

    public void setAncestor(DLTree r) {
        ancestor = r;
    }

    public void addChild(DLTree d) {
        if (d != null) {
            children.add(d);
            d.ancestor = this;
        }
    }

    public void addFirstChild(DLTree d) {
        if (d != null) {
            children.add(0, d);
            d.ancestor = this;
        }
    }

    public void addFirstChildren(Collection<DLTree> d) {
        if (d != null) {
            children.addAll(0, d);
            for (DLTree t : d) {
                t.ancestor = this;
            }
        }
    }

    public DLTree(Token tok) {
        this(new Lexeme(tok));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof DLTree) {
            DLTree t2 = (DLTree) obj;
            return equalTrees(this, t2);
        }
        return false;
    }

    @Override
    public String toString() {
        if (getChildren().size() > 0) {
            StringBuilder b = new StringBuilder();
            b.append("(");
            b.append(elem);
            for (DLTree d : getChildren()) {
                b.append(' ');
                b.append(d);
            }
            b.append(")");
            return b.toString();
        } else {
            return elem.toString();
        }
    }

    @Override
    public int hashCode() {
        return elem.hashCode() + (children == null ? 0 : children.hashCode());
    }

    public abstract void accept(DLTreeVisitor v);

    public abstract <O> O accept(DLTreeVisitorEx<O> v);

    public abstract void replace(DLTree toReplace, DLTree replacement);

    public List<DLTree> getChildren() {
        return children;
    }

    public static boolean equalTrees(DLTree t1, DLTree t2) {
        if (t1 == null && t2 == null) {
            return true;
        }
        if (t1 == null || t2 == null) {
            return false;
        }
        if (t1.elem.equals(t2.elem)) {
            if (t1 instanceof LEAFDLTree) {
                return true;
            }
            if (t1 instanceof ONEDLTree) {
                return t1.getChild().equals(t2.getChild());
            }
            Collection<DLTree> c1 = t1.getChildren();
            Collection<DLTree> c2 = t2.getChildren();
            return c1.size() == c2.size() && c1.containsAll(c2) && c2.containsAll(c1);
        }
        return false;
    }

    public DLTree copy() {
        return this.accept(cloner);
    }

    /** check if DL tree is a concept-like name */
    public boolean isCN() {
        return isConst() || isName();
    }

    // check if DL tree is a concept constant
    public boolean isConst() {
        if (isTOP() || isBOTTOM()) {
            return true;
        }
        return false;
    }

    // check if DL tree is a concept/individual name
    public boolean isName() {
        return token() == CNAME || token() == INAME;
    }
}

interface DLTreeVisitor {
    void visit(LEAFDLTree t);

    void visit(ONEDLTree t);

    void visit(TWODLTree t);

    void visit(NDLTree t);
}

interface DLTreeVisitorEx<O> {
    O visit(LEAFDLTree t);

    O visit(ONEDLTree t);

    O visit(TWODLTree t);

    O visit(NDLTree t);
}

class CloningVisitor implements DLTreeVisitorEx<DLTree> {
    @Override
    public DLTree visit(LEAFDLTree t) {
        return new LEAFDLTree(new Lexeme(t.elem));
    }

    @Override
    public DLTree visit(ONEDLTree t) {
        return new ONEDLTree(new Lexeme(t.elem), t.getChild().accept(this));
    }

    @Override
    public DLTree visit(TWODLTree t) {
        return new TWODLTree(new Lexeme(t.elem), t.getLeft().accept(this), t.getRight()
                .accept(this));
    }

    @Override
    public DLTree visit(NDLTree t) {
        List<DLTree> l = new ArrayList<DLTree>();
        for (DLTree tree : t.children) {
            l.add(tree.accept(this));
        }
        return new NDLTree(new Lexeme(t.elem), l);
    }
}

class ReverseCloningVisitor implements DLTreeVisitorEx<DLTree> {
    @Override
    public DLTree visit(LEAFDLTree t) {
        return DLTreeFactory.inverseComposition(t);
    }

    @Override
    public DLTree visit(ONEDLTree t) {
        return new ONEDLTree(new Lexeme(t.elem), t.getChild().accept(this));
    }

    @Override
    public DLTree visit(TWODLTree t) {
        return new TWODLTree(new Lexeme(t.elem), t.getRight().accept(this), t.getLeft()
                .accept(this));
    }

    @Override
    public DLTree visit(NDLTree t) {
        List<DLTree> l = new ArrayList<DLTree>(t.children);
        List<DLTree> actual = new ArrayList<DLTree>();
        Collections.reverse(l);
        for (DLTree tree : l) {
            actual.add(tree.accept(this));
        }
        return new NDLTree(new Lexeme(t.elem), actual);
    }
}

/** things that have no children */
class LEAFDLTree extends DLTree {
    LEAFDLTree(Lexeme l) {
        super(l);
    }

    @Override
    public DLTree getChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DLTree getLeft() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DLTree getRight() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<DLTree> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void accept(DLTreeVisitor v) {
        v.visit(this);
    }

    @Override
    public <O> O accept(DLTreeVisitorEx<O> v) {
        return v.visit(this);
    }

    @Override
    public void replace(DLTree toReplace, DLTree replacement) {
        throw new UnsupportedOperationException();
    }
}

/** covers trees with only one child, i.e., inverse, not */
class ONEDLTree extends DLTree {
    DLTree child;

    ONEDLTree(Lexeme l, DLTree t) {
        super(l);
        child = t;
        if (t != null) {
            t.ancestor = this;
        }
    }

    @Override
    public DLTree getLeft() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DLTree getChild() {
        return child;
    }

    @Override
    public DLTree getRight() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<DLTree> getChildren() {
        return Collections.singletonList(child);
    }

    @Override
    public void accept(DLTreeVisitor v) {
        v.visit(this);
    }

    @Override
    public <O> O accept(DLTreeVisitorEx<O> v) {
        return v.visit(this);
    }

    @Override
    public void replace(DLTree toReplace, DLTree replacement) {
        if (child == toReplace) {
            child = replacement;
            if (replacement != null) {
                replacement.ancestor = this;
            }
        }
    }
}

/** covers trees with two and only two children */
class TWODLTree extends DLTree {
    TWODLTree(Lexeme l, DLTree t1, DLTree t2) {
        super(l);
        children = new ArrayList<DLTree>(2);
        addChild(t1);
        addChild(t2);
    }

    @Override
    public DLTree getChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DLTree getLeft() {
        return children.get(0);
    }

    @Override
    public DLTree getRight() {
        return children.get(1);
    }

    @Override
    public void accept(DLTreeVisitor v) {
        v.visit(this);
    }

    @Override
    public <O> O accept(DLTreeVisitorEx<O> v) {
        return v.visit(this);
    }

    @Override
    public void replace(DLTree toReplace, DLTree replacement) {
        int p = children.indexOf(toReplace);
        if (p > -1) {
            children.set(p, replacement);
            if (replacement != null) {
                replacement.ancestor = this;
            }
        }
    }
}

class NDLTree extends DLTree {
    public NDLTree(Lexeme l, Collection<DLTree> trees) {
        super(l);
        children = new ArrayList<DLTree>();
        if (trees.size() < 2) {
            throw new ReasonerInternalException(
                    "not enough elements in the n-ary element");
        }
        for (DLTree d : trees) {
            addChild(d);
        }
    }

    public NDLTree(Lexeme l, DLTree C, DLTree D) {
        super(l);
        children = new ArrayList<DLTree>();
        if (C == null || D == null) {
            throw new ReasonerInternalException(
                    "not enough elements in the n-ary element");
        }
        addChild(C);
        addChild(D);
    }

    @Override
    public DLTree getChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DLTree getLeft() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DLTree getRight() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void accept(DLTreeVisitor v) {
        v.visit(this);
    }

    @Override
    public <O> O accept(DLTreeVisitorEx<O> v) {
        return v.visit(this);
    }

    @Override
    public void replace(DLTree toReplace, DLTree replacement) {
        if (children.contains(toReplace)) {
            children.remove(toReplace);
            if (replacement != null) {
                children.add(replacement);
                replacement.ancestor = this;
            }
        }
    }
}
