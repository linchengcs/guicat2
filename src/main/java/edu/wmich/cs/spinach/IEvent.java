package edu.wmich.cs.spinach;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IEvent<E> {
    List<IEvent> availableAfter();
    boolean conflictsWith(IEvent<E> e);
    E getEvent();
    default boolean idle() {return  false;}

    default Set<String> read() {return new HashSet<>();}
    default Set<String> write() {return new HashSet<>();}


}
