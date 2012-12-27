package uk.ac.manchester.cs.jfact.kernel;

/** possible flags of re-checking ALL-like expressions in new nodes */
enum Redo {
    redoForall(1), redoFunc(2), redoAtMost(4), redoIrr(8);
    private int value;

    Redo(int i) {
        value = i;
    }

    protected int getValue() {
        return value;
    }
}
