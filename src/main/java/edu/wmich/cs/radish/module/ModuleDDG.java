package edu.wmich.cs.radish.module;


import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Node;
import edu.wmich.cs.radish.ddg.DataMarker;
import edu.wmich.cs.radish.sequence.IDDG;
import edu.wmich.cs.graph.IDiGraphVisitor;
import edu.wmich.cs.radish.sequence.IEFG;
import edu.wmich.cs.spinach.IEvent;
import edu.wmich.cs.spinach.SimpleEvent;

import java.util.*;

/**
 * Created by rick on 10/6/16.
 */
public class ModuleDDG extends DiGraph<Module> implements IDDG<Module> {
    private EventGroup eventGroup;
    private IDDG ddg;
    private IEFG efg;
    private Set<Set<Module>> disconnectedSubGraphs;


    private Map<Module, Set<DataMarker>> moduleDefs;
    private Map<Module, Set<DataMarker>> moduleUses;

    public ModuleDDG (EventGroup eventGroup, IDDG ddg) {
        super();
        this.eventGroup = eventGroup;
        this.ddg = ddg;
        this.moduleUses = new HashMap<>();
        this.moduleDefs = new HashMap<>();
        this.disconnectedSubGraphs = new HashSet<>();
    }

    public ModuleDDG(EventGroup eventGroup, IDDG ddg, IEFG efg) {
        this.eventGroup = eventGroup;
        this.ddg = ddg;
        this.efg = efg;
    }

    public void populate() {
        populateNodes();
     //   populateModulesDefsUses();
        populateEdges();
  //      findDisconnectedSubGraphs();
    }


    public List<Module> dependantOn(Module module) {
        return null;
    }

    private void populateNodes() {
        for (Module module : eventGroup.getModules()) {
             this.nodes.add(new Node<>(module));
        }
    }

    private void populateEdges() {
        for (Node<Module> from : nodes) {
            for (Node<Module> to : nodes) {
                if (from.getElement() != to.getElement() && hasDependency(from.getElement(), to.getElement())) {
                    addEdge(from, to);
                }
            }
        }
    }

    @Override
    public boolean hasDependency(Module first, Module second) {
     //   return hasReadWriteDependency(first, second) || hasWriteReadDependency(first, second) || hasWriteWriteDependency(first, second);
        for (CTEvent ef : first.getElements()) {
            for (CTEvent es : second.getElements()) {
                if (ddg.hasDependency(ef, es))
                    return true;
            }
        }
        return false;
    }

    public boolean isIdle(Module module) {
        boolean ret = true;
        for (CTEvent event : module.getElements())
            if (!ddg.isIdle(event))
                ret = false;
        return ret;
    }

    public Set<String> eventRead(Module module) {
        Set ret = new HashSet<>();
        for (CTEvent event : module.getElements()) {
            IEvent ievent = new SimpleEvent(event, efg, ddg);
            ret.addAll(ievent.read());
        }
        return ret;
    }

    public Set<String> eventWrite(Module module) {
        Set ret = new HashSet();
        for (CTEvent event : module.getElements()) {
            IEvent ievent = new SimpleEvent(event, efg, ddg);
            ret.addAll(ievent.write());
        }
        return ret;
    }

    @Override
    public List<Module> getNotDuplicateEvents() {
        return  new ArrayList<>(eventGroup.getModules());
    }


    public boolean hasWriteWriteDependency(Module first, Module second) {
        Set<DataMarker> intersection = new HashSet<>();
        Set<DataMarker> sf = moduleDefs.get(first);
        Set<DataMarker> ss = moduleDefs.get(second);
        if (sf == null || ss ==null)
            return false;
        intersection.addAll(sf);
        intersection.retainAll(ss);
        return !intersection.isEmpty();
    }


    public boolean hasReadWriteDependency(Module first, Module second) {
        Set<DataMarker> intersection = new HashSet<>();
        Set<DataMarker> sf = moduleUses.get(first);
        Set<DataMarker> ss = moduleDefs.get(second);
        if (sf == null || ss ==null)
            return false;
        intersection.addAll(sf);
        intersection.retainAll(ss);
        return !intersection.isEmpty();
    }

    public boolean hasWriteReadDependency(Module first, Module second) {
        Set<DataMarker> intersection = new HashSet<>();
        Set<DataMarker> sf = moduleDefs.get(first);
        Set<DataMarker> ss = moduleUses.get(second);
        if (sf == null || ss ==null)
            return false;
        intersection.addAll(sf);
        intersection.retainAll(ss);
        return !intersection.isEmpty();
    }

    private void findDisconnectedSubGraphs() {
        Map<Integer, Set<Module>> disconnectedSubGraphsMap = new HashMap<>();
        int mark = 1;
        for (Node<Module> node : nodes) {
            if (node.getMark() == 0 && !node.isOrphan()) {
                MarkDisconnectedSubGraphVisitor visitor = new MarkDisconnectedSubGraphVisitor(mark++);
                dfs(node, visitor);
            }
        }

        for (Node<Module> node : nodes) {
            Integer index = Integer.valueOf(node.getMark());
            if (index == 0)
                continue;  // orphan
            if (disconnectedSubGraphsMap.get(index) == null) {
                disconnectedSubGraphsMap.put(index, new HashSet<>());
            }
            disconnectedSubGraphsMap.get(index).add(node.getElement());
        }
        disconnectedSubGraphs.addAll(disconnectedSubGraphsMap.values());
    }

    class MarkDisconnectedSubGraphVisitor implements IDiGraphVisitor<Module> {
        private int mark;

        public MarkDisconnectedSubGraphVisitor(int mark) {
            this.mark = mark;
        }

        @Override
        public void visit(Node<Module> node) {
            node.setMark(this.mark);
        }
    }

    public Set<Set<Module>> getDisconnectedSubGraphs() {
        return disconnectedSubGraphs;
    }


    public String toString() {
      //  return super.toString() + "\n disconnectedSubgraphs:" + disconnectedSubGraphs.size();
        return super.toString();
    }


}

