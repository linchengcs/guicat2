package edu.wmich.cs.spinach;


import edu.wmich.cs.radish.sequence.IDDG;
import edu.wmich.cs.radish.sequence.IEFG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SimpleEvent<E> implements IEvent<E> {
    IEFG<E>  efg;
    IDDG<E> ddg;
    E event;

    public SimpleEvent(E event, IEFG<E> efg, IDDG<E> ddg) {
        this.event = event;
        this.efg = efg;
        this.ddg = ddg;
    }

    @Override
    public List<IEvent> availableAfter() {
        List<E> events = efg.availableAfter(event);
        List<IEvent> ret = new ArrayList<>();
        for (E event: events) {
            ret.add(new SimpleEvent(event, efg, ddg));
        }
        return ret;
    }

    @Override
    public boolean conflictsWith(IEvent<E> tgt) {
        return ddg.hasDependency(event, tgt.getEvent());
    }

    public E getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return event.toString();
    }

    @Override
    public boolean equals(Object tgt) {
        return event.toString().equals(tgt.toString());
    }

    @Override
    public int hashCode() {
        return event.toString().hashCode();
    }

    @Override
    public boolean idle() {
        return ddg.isIdle(event);
    }



    public Set<String> read() {
        return ddg.eventRead(event);
    }
    public Set<String> write() {
        return ddg.eventWrite(event);
    }

}