package edu.wmich.cs.spinach;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.CTEvents2Testcase;
import edu.wmich.cs.carot.util.Olog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin Cheng on 12/26/16.
 */
public class TestCaseEventSequenceHandler implements IEventSequenceHandler{
    private String dir;

    public TestCaseEventSequenceHandler(String dir) {
        this.dir = dir;
    }

    @Override
    public void handle(List<IEvent> events) {
        Olog.log.info(events.toString()  + " --valid test case");
        List<CTEvent> ctEvents = new ArrayList<>();
        for (IEvent event : events) {
            ctEvents.add((CTEvent) event.getEvent());
        }

        List<List<CTEvent>> eventss = new ArrayList<>();
        eventss.add(ctEvents);
        CTEvents2Testcase.writeTo(eventss, dir);
    }
}
