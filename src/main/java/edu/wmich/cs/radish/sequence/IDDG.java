package edu.wmich.cs.radish.sequence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rick on 10/7/16.
 */
public interface IDDG<E> {


    boolean hasDependency(E first, E next);


    default Set<E> getEventUses(E event) {
        return new HashSet<E>();
    }

    default Set<E> getEventDefs(E event) {
        return new HashSet<E>();
    }

    void populate();

    default Set<Set<E>> getDisconnectedSubGraphs() {
        return null;
    }

    default boolean isIdle(E e) {
        return false;
    }

    boolean hasReadWriteDependency(E first, E next);
    default boolean hasWriteReadDependency(E first, E next) {
        return  hasReadWriteDependency(next, first);
    }
    boolean hasWriteWriteDependency(E first, E next);

    default Set<String> eventRead(E event) {return new HashSet<>();}
    default Set<String> eventWrite(E event) {return new HashSet<>();}


      List<E> getNotDuplicateEvents();
}

