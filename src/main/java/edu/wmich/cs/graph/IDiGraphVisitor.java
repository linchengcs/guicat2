package edu.wmich.cs.graph;

/**
 * Created by rick on 11/2/16.
 */
public interface IDiGraphVisitor<E> {
    void visit(Node<E> node);
}
