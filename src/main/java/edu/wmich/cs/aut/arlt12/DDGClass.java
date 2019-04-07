package edu.wmich.cs.aut.arlt12;

import edu.wmich.cs.radish.sequence.IDDG;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lin Cheng on 12/22/16.
 */
public class DDGClass implements IDDG<String> {
    @Override
    public boolean hasDependency(String first, String next) {
//        if (first.equals(next))
//            return true;
//        if ("e1".equals(first)  || "e2".equals(first) ) {
//            if("e1".equals(next) || "e2".equals(next) )  {
//                return false;
//            }
//        }

        if ("e1".equals(first)  || "e2".equals(first) || "e4".equals(first)) {
            if("e1".equals(next) || "e2".equals(next) || "e4".equals(next))  {
                return true;
            }
        }


        return false;
    }

    @Override
    public void populate() {
    }

    @Override
    public boolean isIdle(String event) {
        if ("e1".equals(event) || "e2".equals(event))
            return true;
        return false;
    }

    @Override
    public boolean hasReadWriteDependency(String first, String next) {
        if ("e4".equals(first))
            if ("e1".equals(next) || "e2".equals(next))
                return true;
        return false;
    }


    @Override
    public boolean hasWriteWriteDependency(String first, String next) {
        if ("e1".equals(first)  || "e2".equals(first) || "e4".equals(first)) {
            if("e1".equals(next) || "e2".equals(next) || "e4".equals(next))  {
                return true;
            }
        }
        return false;
    }

    public Set<String> eventRead(String event) {
        Set<String> ret = new HashSet<>();
        if ("e3".equals(event)) {
            ret.add("MainWindow.d");
        }
        if ("e4".equals(event)) {
            ret.add("MainWindow.d");
            ret.add("MainWindow.text");
        }
        return ret;
    }

    public Set<String> eventWrite(String event) {
        Set<String> ret = new HashSet<>();
        if ("e1".equals(event)) {
            ret.add("MainWindow.text");
        }
        if ("e2".equals(event)) {
            ret.add("MainWindow.text");
        }
        if ("e3".equals(event)) {
            ret.add("MainWindow.d");
        }
        if ("e4".equals(event)) {
            ret.add("MainWindow.text");
            ret.add("MainWindow.d");
        }
        return ret;
    }

    public List<String> getNotDuplicateEvents() {
        return Arrays.asList(new String[]{"e1", "e2", "e3", "e4"});
    }

}
