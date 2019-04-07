package edu.wmich.cs.radish.module;

import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.wrapper.EFGWrapper;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Edge;
import edu.wmich.cs.graph.Node;
import edu.wmich.cs.radish.sequence.IEFG;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rick on 10/6/16.
 */
public class ModuleEFG extends DiGraph<Module> implements IEFG<Module> {
    private EventGroup eventGroup;
    private EFGWrapper efgWrapper;
    private List<Module> initialModules;

    public ModuleEFG (EventGroup eventGroup) {
        super();
        this.eventGroup = eventGroup;
        this.efgWrapper = new EFGWrapper(eventGroup.getEfg());
        this.initialModules = new ArrayList<>();
    }

    /* populate nodes, edges
     * nodes: logic module + data module
     */


    // big problem of guitar terminate event!!!!!
    public void populate() {
        populateNodes();
        populateEdges();
        populateInitNodes();
        generateBranchMarker();
    }

    private void populateInitNodes() {
        for (Node<Module> node : nodes) {
            Module module = node.getElement();
            for (CTEvent event : module.getElements()) {
                EventType eventType = (EventType) event.getObject();
                if (eventType.isInitial()) {
                    initialModules.add(module);
                    break;
                }
            }
        }
    }

    private void populateNodes() {
        for (Module module : eventGroup.getModules()) {
            this.nodes.add(new Node<>(module));
        }
    }

    private void populateEdges() {
        for (Node<Module> node : nodes) {
          //  assert checkModuleHasSameEFGStatus(node.getElement());
        }

        for (Node<Module> from : nodes) {
            if (from.toString().contains("9128")) {
                Olog.log.info("pause");
            }
            for (Node<Module> to : nodes) {
                if (hasEdge(from.getElement(), to.getElement())) {
                    addEdge(from, to);
                }
            }
        }
    }

    private boolean hasEdge(Module from, Module to) {
        for (CTEvent efrom : from.getElements()) {
            for (CTEvent eto : to.getElements()) {
                Integer efromContainer = EventGroup.getContainer(efrom.getIdentifier());
                Integer etoContainer = EventGroup.getContainer(eto.getIdentifier());
//                if(efromContainer != null && etoContainer != null && efromContainer == etoContainer) {
//                    return true;
//                }

                if (efgWrapper.getEdge(efrom.getIdentifier(), eto.getIdentifier()) <= 0) { //edges should be either full or none, return at first time
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    private boolean checkModuleHasSameEFGStatus(Module module) {
        Set<CTEvent> set1 = new HashSet<>();
        Set<CTEvent> set2 = new HashSet<>();
        CTEvent event1 = eventGroup.getEvents().get(0);
        for (CTEvent aei : eventGroup.getEvents()) {
            if (efgWrapper.getEdge(event1.getIdentifier(), aei.getIdentifier()) > 0){
                set1.add(aei);
            }
        }
        for (int i = 1; i < eventGroup.getEvents().size(); i++) {
            CTEvent event = eventGroup.getEvents().get(i);
            for (CTEvent aei : eventGroup.getEvents()) {
                if (efgWrapper.getEdge(event.getIdentifier(), aei.getIdentifier()) > 0){
                    set2.add(aei);
                }
            }
            if (!set1.equals(set2))
                return false;
        }
        return true;
    }

    @Override
    public List<Module> availableAfter(Module module) {
        if (module == null)
            return initialModules;

        Node<Module> node = this.getNodeByElement(module);
        List<Module> ret = new ArrayList<>();
        for (Edge<Module> edge : node.getOutEdges()) {
            Node<Module> next = edge.getTo();
            ret.add(next.getElement());
        }
        return ret;
    }

    @Override
    public List<Module> getInitials() {
        return new ArrayList<>(initialModules);
    }


    @Override
    public boolean isControlEvent(Module module) {
        for (Module m : eventGroup.getControlModules()) {
            if (m.equals(module)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBranchEdge(Module src, Module tgt) {
        Edge<Module> edge = getEdge(getNodeByElement(src), getNodeByElement(tgt));
        return edge == null ? false : edge.isBranchMarker();
    }


    public EventGroup getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(EventGroup eventGroup) {
        this.eventGroup = eventGroup;
    }

    public List<Module> getInitialModules() {
        return initialModules;
    }

    public void setInitialModules(List<Module> initialModules) {
        this.initialModules = initialModules;
    }

    @Override
    public String toString() {
        String ans = super.toString();
        ans += "\nInitials: " + initialModules.toString();
        return ans;
    }
}
