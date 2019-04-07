package edu.wmich.cs.radish.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rick on 10/17/16.
 */
public class Transition<E> {
    private List<E> events;
    private boolean isBackTrack;

    public Transition() {
        this.events = new ArrayList<E>();
        this.isBackTrack = false;
    }

    public  Transition(E e, boolean isBackTrack) {
        this();
        this.events.add(e);
        this.isBackTrack = isBackTrack;
    }

    public Transition(List<E> list, boolean isBackTrack) {
        this.events = list;
        this.isBackTrack = isBackTrack;
    }

    @Deprecated
    public E head() {
        return events.get(events.size() - 1);
    }

    public E start() {
        return events.get(0);
    }

    public E end() {
        return events.get(events.size() - 1);
    }

    public void append(Transition<E> next) {
        this.events.addAll(next.getEvents());
    }

    public int size() {
        return this.events.size();
    }

    public Transition<E> subTransition(int from, int to) {
        return new Transition<E>(events.subList(from, to), isBackTrack);
    }

    public Transition<E> subTransition(int from) {
        return this.subTransition(from, this.size());
    }

    // getters and setters
    public List<E> getEvents() {
        return events;
    }

    public void setEvents(List<E> transition) {
        this.events = transition;
    }

    public boolean isBackTrack() {
        return isBackTrack;
    }

    public void setBackTrack(boolean backTrack) {
        isBackTrack = backTrack;
    }

    @Override
    public String toString() {
        return this.events.toString();
    }

    // used to check whether contained by backTrackSet.backTracked.contains()
    @Override
    public boolean equals(Object object) {
        return this.toString().equals(object.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
