package edu.wmich.cs.radish.sequence;

import edu.wmich.cs.spinach.State;

import java.util.List;
import java.util.Set;


public interface IEFG<E> {
    List<E> availableAfter(E e);

    List<E> getInitials();

    default List<E> findShortestPath(E src, Set<E> tgts) {
        return null;
    }

    default boolean isControlEvent(E e) {
        return false;
    }

    default boolean isBranchEdge(E src, E tgt) {
        return false;
    }

}
