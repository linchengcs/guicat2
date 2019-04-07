package edu.wmich.cs.radish.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rick on 10/12/16.
 */
public class Stacks<E> {
    //stack and eventSequence should have the same index, so the backtrack set can be associated to each event
    //stacks and event
    private List<BackTrackSet<E>>  backTrackSetList;

    private List<E> eventSequence;

    private int eventsCounter;

    public Stacks() {
        this.backTrackSetList = new ArrayList<>();
        this.eventSequence = new ArrayList<>();
        eventsCounter = 0;
    }

    public int size() {
        return this.eventSequence.size();
    }


    public boolean isEmpty() {
        return  this.eventSequence.isEmpty();
    }

    public void add(BackTrackSet<E> backTrackSet) {
        this.backTrackSetList.add(backTrackSet);
    }

    public BackTrackSet<E> get(int i) {
        return this.backTrackSetList.get(i);
    }

    public void addToSequence(E e) {
        this.eventSequence.add(e);
    }

    public void addToSequence(Transition<E> transition) {
        this.eventSequence.addAll(transition.getEvents());
        while (backTrackSetList.size() < eventSequence.size()) {
            backTrackSetList.add(new BackTrackSet<E>());
        }
    }

    public void addBackTrackSet(int index, BackTrackSet<E> backTrackSet) {
        while ( backTrackSetList.size() <= index) {
            backTrackSetList.add(new BackTrackSet<E>());
        }

        this.backTrackSetList.set(index, backTrackSet);
    }

    public void backTrackEvent() {
        this.eventSequence.remove(eventSequence.size()-1);
    }

    public void backTrackBacKTrackSet() {
        this.backTrackSetList.remove(backTrackSetList.size() -1);
    }

    public void backtrack() {
        this.eventSequence.remove(eventSequence.size()-1);
        this.backTrackSetList.remove(backTrackSetList.size() -1);
    }

    public E head() {
        return this.eventSequence.get(this.eventSequence.size() - 1);
    }
    public BackTrackSet<E> backTrackHead() {
        return this.backTrackSetList.get(backTrackSetList.size() - 1);
    }

    public void cleanAfter(int index) {
        while (eventSequence.size() > index) {
            eventSequence.remove(eventSequence.size() - 1);
        }
        while (backTrackSetList.size() > index + 1) {
            backTrackSetList.remove(backTrackSetList.size() - 1);
        }
    }

    public void initEventSequence() {
        while (eventSequence.size() > backTrackSetList.size() - 1)
            eventSequence.remove(eventSequence.size() - 1);
    }

    /*
    * getters and setters
     */

    public List<BackTrackSet<E>> getBackTrackSetList() {
        return backTrackSetList;
    }

    public void setBackTrackSetList(List<BackTrackSet<E>> backTrackSetList) {
        this.backTrackSetList = backTrackSetList;
    }

    public List<E> getEventSequence() {
        return eventSequence;
    }

    public void setEventSequence(List<E> eventSequence) {
        this.eventSequence = eventSequence;
    }

    public int getEventsCounter() {
        return eventsCounter;
    }

    public void setEventsCounter(int eventsCounter) {
        this.eventsCounter = eventsCounter;
    }

    @Override
    public String toString() {
        String ret = ""; // "===========Stacks toString=========";
        ret += "\n" + "EventSequence: " + this.eventSequence.toString() ;
        ret += "\n" + "BackTrackList:\n" ;
        for (int i = 0; i < backTrackSetList.size(); i++ ){
            BackTrackSet<E> bt = backTrackSetList.get(i);
            ret += "line " + i + ": ";
            if (bt != null)
                ret +=  bt.toString() + "\n";
        }
        return ret;
    }
}
