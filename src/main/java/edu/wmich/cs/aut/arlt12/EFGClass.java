package edu.wmich.cs.aut.arlt12;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.radish.sequence.IEFG;
import edu.wmich.cs.spinach.IEvent;
import edu.wmich.cs.spinach.SimpleEvent;
import edu.wmich.cs.spinach.State;

import java.util.*;

/**
 * Created by rick on 12/18/16.
 */
public class EFGClass implements IEFG<String> {

    @Override
    public List<String> availableAfter(String s) {
        List<String> ret = new ArrayList<>();
        if ("e1".equals(s) || "e2".equals(s) || "e4".equals(s) ) {
            ret.add("e1");
            ret.add("e2");
            ret.add("e3");
        }
        if ("e3".equals(s)) {
            ret.add("e4");
        }
        return ret;
    }

    @Override
    public List<String> getInitials() {
        List<String> ret = new ArrayList<>();
        ret.add("e1");
        ret.add("e2");
        ret.add("e3");
        return ret;
    }



}
