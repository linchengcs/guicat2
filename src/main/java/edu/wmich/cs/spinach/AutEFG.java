package edu.wmich.cs.spinach;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.radish.sequence.IEFG;

import java.util.*;

/**
 * Created by Lin Cheng on 12/21/16.
 * The element should be CTEvent
 * The class should implements two functions: getInitials() and availabeAfter()
 */
public class AutEFG implements IEFG<CTEvent> {
    private EFG efg;
    private EFGWrapper efgWrapper;
    private List<EventType> eventTypes;
    private List<CTEvent> events;
    private List<CTEvent> initEvents;
    private Map<CTEvent, List<CTEvent>> edgeMap;
    private int numOfEdges =  0;

    public AutEFG(EFG efg) {
        this.efg = efg;
        this.efgWrapper = new EFGWrapper(efg);
        this.eventTypes = efg.getEvents().getEvent();
        this.events = CTEfgEvent.eventType2ctEvent(this.eventTypes);
        this.initEvents = new ArrayList<>();
        for (CTEvent event : events) {
            EventType eventType = (EventType)event.getObject();
            if (eventType.isInitial()) {
                initEvents.add(event);
            }
        }
        this.edgeMap = new HashMap<>();
        for (CTEvent key : events) {
            List<CTEvent> value = new ArrayList<>();
            for (CTEvent event : events) {
                if (efgWrapper.getEdge(key.getIdentifier(), event.getIdentifier()) > 0) { //edges should be either full or none, return at first time
                    value.add(event);
                    numOfEdges ++;
                }
            }
            edgeMap.put(key, value);
        }


    }

    public List<CTEvent> getEvents() {
        return null;
    }

    @Override
    public List<CTEvent> availableAfter(CTEvent ctEvent) {
        return edgeMap.get(ctEvent);
    }

    @Override
    public List<CTEvent> getInitials() {
        return initEvents;
    }

    public int getNumOfEdges() {
        return numOfEdges;
    }
}
