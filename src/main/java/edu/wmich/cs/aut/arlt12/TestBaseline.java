package edu.wmich.cs.aut.arlt12;

import edu.wmich.cs.radish.sequence.Baseline;
import edu.wmich.cs.radish.sequence.BaselineVisitor;
import edu.wmich.cs.radish.sequence.IDFSVisitor;


public class TestBaseline {
    public static void main(String[] args) {
        EFGClass efgClass = new EFGClass();
        int bound = 3;
        IDFSVisitor<String> idfsVisitor = new BaselineVisitor<>(bound);
        Baseline<String> baseline = new Baseline<>(efgClass, bound, idfsVisitor);

        baseline.run();
    }
}
