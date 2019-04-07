package edu.wmich.cs.radish.sequence;

import java.util.*;

/**
 * Created by Lin Cheng on 11/10/16.
 * All E ment to be Transition<E>,
 * E must implements compareable
 */
public class BackTrackSet<E> implements Iterable {
    Queue<Transition<E>> backtrackSet;
    Set<Transition<E>> backtracked;

    public BackTrackSet() {
        this.backtrackSet = new LinkedList<>();
        this.backtracked = new HashSet<>();
    }

    public BackTrackSet(Transition<E> transition) {
        this();
        this.backtrackSet.add(transition);
    }

    public BackTrackSet(E e, boolean backTrack) {
        this(new Transition(e, backTrack));
    }



    public void addBackTrack(Transition<E> bn) {
        this.backtrackSet.offer(bn);
    }

    public void addBackTracked(Transition<E> bn) {
        this.backtrackSet.remove(bn);
        this.backtracked.add(bn);
    }

    public boolean contains(Transition<E> e) {
        return backtracked.contains(e) || backtrackSet.contains(e);
    }


    public boolean isBackedTracked(Transition<E> transition) {
        E ts = transition.start();
        for (Transition<E> t : backtracked) {
            if (t.start().equals(ts)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInBackTrackSet(Transition<E> transition) {
        E ts = transition.start();
        for (Transition<E> t : backtrackSet) {
            if (t.start().equals(ts))
                return true;
        }
        return false;
    }


    @Override
    public Iterator<Transition<E>> iterator() {
        return backtrackSet.iterator();
    }

    public Queue<Transition<E>> getBacktrackSet() {
        return backtrackSet;
    }

    public void setBacktrackSet(Queue<Transition<E>> backtrackSet) {
        this.backtrackSet = backtrackSet;
    }

    public Set<Transition<E>> getBacktracked() {
        return backtracked;
    }

    public void setBacktracked(Set<Transition<E>> backtracked) {
        this.backtracked = backtracked;
    }

    @Override
    public String toString() {
        String ret = "BackTrackSet: " + backtrackSet.toString() + "; "
                + "BackTracked" + backtracked.toString();
        return ret;
    }
}


