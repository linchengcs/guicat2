package edu.wmich.cs.spinach;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class State {
    private List<IEvent> backtrack;
    private List<IEvent> enabled;
    private List<IEvent> backtracked;
    private List<IEvent> sleep;

    //the event that leads to this state, the representor of the state along with its ancestors
    private IEvent from;

    /*
     * states are nodes both for a linked list and a stack, for different purpose
     * linkded list for reverse search
     * stack for dfs search
     */
    private State prev;

    public State(IEvent from) {
        this.backtrack = new ArrayList<>();
        this.enabled = new ArrayList<>();
        this.backtracked = new ArrayList<>();
        this.sleep = new ArrayList<>();
        this.from = from;
        this.prev = null;
    }

    public boolean allBacktracked() {
        return this.backtrack.isEmpty();
    }

    /*remove and return an event from events, use queque (linkedlist) poll */
    public IEvent getEvent() {
        if (backtrack.isEmpty())
            return null;
        return backtrack.remove(0);
    }

    // generate next state
    public State next(IEvent event) {
        State ret = new State(event);
        ret.prev = this;
        ret.setEnabled(event.availableAfter());
       // ret.addBacktrack(ret.getEnabled().get(0));  // for convinience
        backtracked.add(event);
        return ret;
    }

    public boolean isInit() {
        return from == null;
    }

    public Iterator<IEvent> enabledIterator() {
        return enabled.iterator();
    }


    public void addBacktrack(IEvent event) {
        if (!backtrack.contains(event) && !backtracked.contains(event)) {
            backtrack.add(event);
        }
    }

    public void addBacktrack(List<IEvent> events) {
        for (IEvent event : events)
            addBacktrack(event);
    }

    public void addSleep(IEvent event) {
        if (!sleep.contains(event)) {
            sleep.add(event);
        }
    }

    public void addSleep(List<IEvent> events) {
        for (IEvent event : events)
            addSleep(event);
    }

    public int index() {
        int ret = 0;
        State next = this;
        while (!next.isInit()) {
            ret++;
            next = next.getPrev();
        }
        return ret;
    }

    //  getters and setters
    public IEvent getFrom() {
        return from;
    }

    public void setFrom(IEvent from) {
        this.from = from;
    }

    public void setEnabled(List<IEvent> enabled) {
        this.enabled = enabled;
    }


    public List<IEvent> getBacktrack() {
        return backtrack;
    }

    public void setBacktrack(LinkedList<IEvent> backtrack) {
        this.backtrack = backtrack;
    }

    public List<IEvent> getEnabled() {
        return enabled;
    }

    public List<IEvent> getBacktracked() {
        return backtracked;
    }

    public void setBacktracked(List<IEvent> backtracked) {
        this.backtracked = backtracked;
    }

    public State getPrev() {
        return prev;
    }

    public void setPrev(State prev) {
        this.prev = prev;
    }

    public List<IEvent> getSleep() {
        return sleep;
    }

    public String toString() {
        return "from:" + (from == null ? "null" : from.toString()) + ", backtrack number: " + backtrack.size();
    }
}
