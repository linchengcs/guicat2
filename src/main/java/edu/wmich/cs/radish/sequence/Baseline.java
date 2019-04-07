package edu.wmich.cs.radish.sequence;

import edu.umd.cs.guitar.model.data.EFG;
import edu.wmich.cs.carot.util.Olog;

/**
 * Created by rick on 12/18/16.
 */
public class Baseline<E> {
    IEFG<E> efg;
    int bound;
    IDFSVisitor visitor;
    int counter;

    public Baseline(IEFG<E> efg, int bound, IDFSVisitor visitor) {
        this.efg = efg;
        this.bound = bound;
        this.visitor = visitor;
        this.counter = 0;
    }

    public void run() {
        for (E e : efg.getInitials()) {
            dfs(e, visitor);
        }
        Olog.log.info("number of test cases: " + counter);
    }

    public void dfs(E e, IDFSVisitor visitor) {
        visitor.visit(e);
        if (visitor.reachBound()) {
            visitor.output();
            visitor.backtrack();
            counter++;
            return;
        }
        for (E child : efg.availableAfter(e)) {
            dfs(child, visitor);
        }
        visitor.backtrack();
        return;
    }


}
