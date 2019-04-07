package edu.wmich.cs.radish.sequence;

/**
 * Created by rick on 12/18/16.
 */
public interface IDFSVisitor<E> {
    void visit(E e);
    boolean reachBound();
    void backtrack();
    void output();
}
