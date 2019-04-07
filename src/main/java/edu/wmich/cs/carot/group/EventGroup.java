package edu.wmich.cs.carot.group;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by oliver on 07/09/16.
 */
public class EventGroup {
    private Set<CTEvent> events;
    private ModuleType moduleType;

    public enum ModuleType {DATA, ACTION}

    public EventGroup() {
        this.events = new HashSet<>();
    }

    public EventGroup(CTEvent event) {
        this();
        events.add(event);
    }

    public EventGroup(CTEvent event, ModuleType moduleType) {
        this(event);
        this.moduleType = moduleType;
    }

    public EventGroup(List<CTEvent> list) {
        this();
        events.addAll(list);
    }

    public EventGroup(List<CTEvent> list, ModuleType moduleType) {
        this(list);
        this.moduleType = moduleType;
    }


    public Set<CTEvent> getEvents() {
        return events;
    }

    public void setEvents(Set<CTEvent> events) {
        this.events = events;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    @Override
    public String toString() {
        return events.toString();
    }
}
