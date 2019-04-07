package edu.wmich.cs.radish.module;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

import java.util.ArrayList;

/**
 * Created by rick on 11/1/16.
 */
public class ControlModule extends Module{
    public ControlModule () {
        this.elements = new ArrayList<>(1);
    }

    public ControlModule (CTEvent event) {
        this();
        this.elements.add(event);
    }
}
