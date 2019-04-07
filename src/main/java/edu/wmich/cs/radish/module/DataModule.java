package edu.wmich.cs.radish.module;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import java.util.Set;

/**
 * Created by rick on 10/6/16.
 */
public class DataModule extends  Module{

    public DataModule(Set<CTEvent> events){
        for (CTEvent event : events) {
            this.elements.add(event);
        }
    }


}
