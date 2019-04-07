package edu.wmich.cs.graph;

import java.util.List;

public class Sequence<E> {
    private List<Node<E>> sequence;

    public Sequence(List<Node<E>> sequence) {
        this.sequence = sequence;
    }

    public List<Node<E>> getSequence() {
        return sequence;
    }

    public void setSequence(List<Node<E>> sequence) {
        this.sequence = sequence;
    }
}
