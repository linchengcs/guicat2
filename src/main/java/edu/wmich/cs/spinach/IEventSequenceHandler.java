package edu.wmich.cs.spinach;

import java.util.List;

/**
 * Created by Lin Cheng on 12/26/16.
 */
interface IEventSequenceHandler {
    void handle(List<IEvent> events);
}
