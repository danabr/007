package rps.util;

/**
 * A tuple.
 * @author Daniel Abrahamsson
 */
public class Tuple<A,B> {
    public A a;
    public B b;

    /**
     * Initializes a tuple.
     * @param a First object
     * @param b Second object
     */
    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
