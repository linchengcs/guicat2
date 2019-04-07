package edu.wmich.cs.carot.util;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.graph.DTree;
import  edu.wmich.cs.graph.Matrix;

import java.util.List;

/**
 * Created by oliver on 15/08/16.
 */
public class Graph2Gephi {
    public static <E> String graph2Gephi(List<E> events, Matrix edges) {
        String ret = "";
        for (E event : events) {
            ret += ";" + ((CTEvent)event).getIdentifier();
        }

        int i = 0;
        assert events.size() == edges.nrow();

        for (List<Integer> row: edges.getData() ) {
            ret += "\r\n" + ((CTEvent)(events.get(i++))).getIdentifier() ;
            for (int ele : row) {
                ret += ";" + ele ;
            }
        }
        return ret;
    }

    public static <E> String dTree2Gephi(DTree<E> dTree, String ret) {
        String from = ((CTEvent)dTree.getValue()).getIdentifier();
        for (DTree<E> child : dTree.getChildren()) {
            String to = ((CTEvent)child.getValue()).getIdentifier();
            ret += from + ";" + to + "\r\n";
            ret = dTree2Gephi(child, ret);
        }
        return ret;
    }

    public static <E> String dTree2Gephi(DTree<E> dTree) {
        return dTree2Gephi(dTree, "");
    }
}
