package edu.wmich.cs.aut.guess;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.radish.ddg.DataMarker;
import edu.wmich.cs.radish.module.Module;
import edu.wmich.cs.radish.sequence.IDDG;
import edu.wmich.cs.radish.sequence.IEFG;
import edu.wmich.cs.spinach.IEvent;
import edu.wmich.cs.spinach.SimpleEvent;
import edu.wmich.cs.spinach.State;

import java.util.*;

/**
 * Created by rick on 10/17/16.
 */
public class TestGraph {
    public Set<String> modules;
    public IEFG efg;
    public IDDG ddg;

    public TestGraph() {
        generateModules();
        this.efg = new GuessEFG();
        this.ddg = new GuessDDG();
    }

    private void generateModules () {
        this.modules = new HashSet<>();
        this.modules.add("Add");
        this.modules.add("Minus");
        this.modules.add("Start");
        this.modules.add("SetInit");
    }


}


class GuessEFG implements IEFG<String> {
    public List<String> getInitials() {
        List<String> ret = new ArrayList<>();
        ret.add("Add");
        ret.add("Minus");
        ret.add("Start");
        return ret;
    }

    public List<String> getEvents() {
        return null;
    }


    public List<String> availableAfter(String s) {
        List<String> ret = new ArrayList<>();
        if ("Add".equals(s) || "Minus".equals(s) || "SetInit".equals(s)) {
            ret.add("Add");
            ret.add("Minus");
            ret.add("Start");
        }
        if ("Start".equals(s)) {
            ret.add("SetInit");
        }
        return ret;
    }

}

class GuessDDG implements IDDG<String> {
    public void populate() {}
 
    public boolean hasDependency(String first, String next) {
        if ("Start".equals(first) || "Start".equals(next)) {
            return false;
        }
        return true;
    }

    @Override
    public Set<Set<String>> getDisconnectedSubGraphs() {
        return null;
    }

    @Override
    public boolean hasReadWriteDependency(String first, String next) {
        return false;
    }

    @Override
    public boolean hasWriteReadDependency(String first, String next) {
        return false;
    }

    @Override
    public boolean hasWriteWriteDependency(String first, String next) {
        return false;
    }

    @Override
    public List<String> getNotDuplicateEvents() {
        return null;
    }


    public CTEvent getDataEventByDef(DataMarker dataMarker) {
        return null;
    }

}
