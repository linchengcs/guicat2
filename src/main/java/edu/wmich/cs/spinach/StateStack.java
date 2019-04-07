package edu.wmich.cs.spinach;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * pretty standard stack
 */
public class StateStack {
    List<State> states;

    public StateStack() {
        this.states = new ArrayList<>();
    }

    public State top() {
        return states.get(size() - 1);
    }

    public State pop() {
        State ret = top();
        states.remove(size() - 1);
        return ret;
    }

    public void push(State state) {
        states.add(state);
    }

    public int size() {
        return states.size();
    }

    public boolean hasBacktrackPoints() {
        for (State state : states) {
            if (!state.allBacktracked())
                return true;
        }
        return false;
    }

    public String output() {
        String ret = "[";
        for (int i = 0; i < states.size(); i++) {
            IEvent event = states.get(i).getFrom();
            if (event != null) {
                ret += event.getEvent().toString() + ",";
            }
        }
        ret += "]";
        return ret;
    }

    public List<IEvent> getEvents() {
        List<IEvent> ret = new ArrayList<>();
        for (State state : states) {
            IEvent event = state.getFrom();
            if (event != null)
                ret.add(state.getFrom());
        }
        return ret;
    }

    public String toString() {
        return states.toString();
    }
}
