package edu.wmich.cs.graph;


import java.util.HashSet;
import java.util.Set;

public class Label<E> {
    private Set<E> label;

    public Label() {
        label = new HashSet<E>();
    }

    public Label(Set<E> set) {
        this.label = set;
    }

    @Override
    public String toString() {
        return label.toString();
    }
}
