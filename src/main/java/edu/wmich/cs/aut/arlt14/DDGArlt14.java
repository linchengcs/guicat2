package edu.wmich.cs.aut.arlt14;

import edu.wmich.cs.radish.sequence.IDDG;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lin Cheng on 1/2/17.
 */
public class DDGArlt14 implements IDDG<String> {
    @Override
    public boolean hasDependency(String first, String next) {
        if (first.equals("e5") || next.equals("e5"))
            return  false;
        if (first.equals(next))
            return true;

        if ("e1".equals(first) && "e4".equals(next))
            return true;
        if ("e1".equals(next) && "e4".equals(first))
            return true;

        if ("e2".equals(first) && "e3".equals(next))
            return true;
        if ("e2".equals(next) && "e3".equals(first))
            return true;

        if ("e2".equals(first) && "e4".equals(next))
            return true;
        if ("e2".equals(next) && "e4".equals(first))
            return true;

        return false;
    }

    @Override
    public void populate() {

    }

    @Override
    public boolean isIdle(String event) {
//        if ("e1".equals(event) || "e2".equals(event))
//            return true;
//        return false;

        return true;
    }

    @Override
    public boolean hasReadWriteDependency(String first, String next) {
        if ("e4".equals(first) && "e1".equals(next))
            return true;
        if ("e4".equals(first) && "e2".equals(next))
            return true;
        if ("e2".equals(next) && "e4".equals(first))
            return true;

        return false;
    }

    @Override
    public boolean hasWriteWriteDependency(String first, String next) {
        if (first.equals("e5") || next.equals("e5"))
            return  false;
        if (first.equals(next))
            return true;
        return false;
    }

    @Override
    public Set<String> eventRead(String event) {
        Set<String> ret = new HashSet<>();
        if ("e1".equals(event)) {
            ret.add("ModifyImageWindow.checkBox");
        }
        if ("e2".equals(event)) {
            ret.add("ModifyImageWindow.slider");
        }
        if ("e3".equals(event)) {
            ret.add("ModifyImageWindow.angle");
        }
        if ("e4".equals(event)) {
            ret.add("ModifyImageWindow.convert");
            ret.add("ModifyImageWindow.angle");
        }
        return ret;
    }

    @Override
    public Set<String> eventWrite(String event) {
        Set<String> ret = new HashSet<>();
        if ("e1".equals(event)) {
            ret.add("ModifyImageWindow.convert");
        }
        if ("e2".equals(event)) {
            ret.add("ModifyImageWindow.angle");
        }
        if ("e3".equals(event)) {
            ret.add("ModifyImageWindow.RotationAngle");
        }
        if ("e4".equals(event)) {
            ret.add("ModifyImageWindow.image");
        }
        return ret;
    }


    public List<String> getNotDuplicateEvents() {
        return Arrays.asList(new String[]{"e1", "e2", "e3", "e4", "e5"});
    }

}