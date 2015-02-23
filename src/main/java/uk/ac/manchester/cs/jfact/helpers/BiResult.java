package uk.ac.manchester.cs.jfact.helpers;

public class BiResult<T, Q> {

    T t;
    Q q;

    public BiResult(T t, Q q) {
        this.t = t;
        this.q = q;
    }

    public T getKey() {
        return t;
    }

    public Q getValue() {
        return q;
    }
}
