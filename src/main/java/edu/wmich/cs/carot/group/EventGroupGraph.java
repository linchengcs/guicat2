package edu.wmich.cs.carot.group;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lin Cheng on 07/09/16.
 */
public class EventGroupGraph extends DiGraph<EventGroup> {

    public EventGroup findModuleByEvent(CTEvent key) {
        for (Node<EventGroup> moduleNode : getNodes()) {
            EventGroup eventGroup = moduleNode.getElement();
            for (CTEvent event : eventGroup.getEvents()) {
                if (event == key) {
                    return eventGroup;
                }
            }
        }
        return null;
    }

    public Node<EventGroup> findNodeModuleByEvent(CTEvent key) {
        for (Node<EventGroup> moduleNode : getNodes()) {
            EventGroup eventGroup = moduleNode.getElement();
            for (CTEvent event : eventGroup.getEvents()) {
                if (event == key) {
                    return moduleNode;
                }
            }
        }
        return null;
    }

    public Set<List<EventGroup>> generatePath(int len, boolean cyclic, boolean includeShort) {
        Set<List<EventGroup>> ans = super.generatePath(len, cyclic, includeShort);

        for (List<EventGroup> list : ans) {
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i).getModuleType() == EventGroup.ModuleType.DATA)
                    ans.remove(list);
            }
        }
        return ans;
    }

    public Set<List<CTEvent>> moduleSeq2EventSeq(Set<List<EventGroup>> ms) {
        Set<List<CTEvent>> ans = new HashSet<>();
        for (List<EventGroup> eventGroups : ms) {
            List<CTEvent> events = new ArrayList<>();
            for (EventGroup eventGroup : eventGroups) {
                for (CTEvent event : eventGroup.getEvents())
                    events.add(event);
            }
            ans.add(events);
        }
        return ans;
    }

    public Set<List<CTEvent>> generateEventSequence(int len, boolean cyclic, boolean includeShort) {
        Set<List<EventGroup>> modulesSet = generatePath(len, cyclic, includeShort);
        return moduleSeq2EventSeq(modulesSet);
    }


}
