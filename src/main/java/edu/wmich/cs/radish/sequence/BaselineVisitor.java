package edu.wmich.cs.radish.sequence;

import edu.wmich.cs.carot.util.Olog;

import java.util.ArrayList;
import java.util.List;

public class BaselineVisitor<E> implements IDFSVisitor<E>{
    List<E> sequence;
    int bound;

    public BaselineVisitor() {
        this.sequence = new ArrayList<E>();
    }

    public BaselineVisitor(int bound) {
        this();
        this.bound = bound;
    }

    @Override
    public void visit(E e) {
        sequence.add(e);
    }

    public boolean reachBound() {
        return sequence.size() == bound;
    }

    public void backtrack() {
        this.sequence.remove(this.sequence.size()-1);
    }

    public void output() {
        Olog.log.info(this.sequence.toString());
    }
}
