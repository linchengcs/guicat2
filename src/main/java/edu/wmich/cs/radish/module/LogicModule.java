package edu.wmich.cs.radish.module;


import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

import java.util.ArrayList;

/**
 * Created by rick on 10/6/16.
 */
public class LogicModule  extends Module{
    public LogicModule () {
        this.elements = new ArrayList<>(1);
    }

    public LogicModule (CTEvent event) {
        this();
        this.elements.add(event);
    }
}
