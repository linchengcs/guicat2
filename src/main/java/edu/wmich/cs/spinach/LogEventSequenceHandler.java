package edu.wmich.cs.spinach;

import edu.wmich.cs.carot.util.Olog;

import java.util.List;

/**
 * Created by Lin Cheng on 12/26/16.
 */
public class LogEventSequenceHandler implements IEventSequenceHandler{
    @Override
    public void handle(List<IEvent> events) {
        Olog.log.info(events.toString() + " --valid test case");
    }
}
