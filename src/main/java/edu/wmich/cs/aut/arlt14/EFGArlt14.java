package edu.wmich.cs.aut.arlt14;

import edu.wmich.cs.radish.sequence.IEFG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lin Cheng on 1/2/17.
 */
public class EFGArlt14 implements IEFG<String> {

    @Override
    public List<String> availableAfter(String s) {
        List<String> ret = new ArrayList<>();
        ret.add("e1");
        ret.add("e2");
        ret.add("e3");
        ret.add("e4");
        ret.add("e5");
        return ret;
    }

    @Override
    public List<String> getInitials() {
        List<String> ret = new ArrayList<>();
        ret.add("e1");
        ret.add("e2");
        ret.add("e3");
        ret.add("e4");
        ret.add("e5");
        return ret;
    }

}
