package edu.wmich.cs.carot.ddg;


import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.*;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.graph.DTree;
import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Node;

import java.util.*;

public class SliceDDG<E> {
    private DiGraph<E> graph;
    private static final int LEAF_DIST_LIMIT = 3;
    public static final int MAX_SEQUENCE_SHARE_NODE = 1; //only 1, make merge to a tree
    public List<List<Node<E>>> leafgroups;
    private List<Node<E>> roots;
    private List<Node<E>> leaves;
    private Set<List<E>> eventSequences;
    private EFG efg;
    private List<E> initEvents;

    public SliceDDG(DiGraph<E> graph) {
        this.graph = graph;
        leafgroups = new LinkedList<>();
        roots = new LinkedList<>();
        leaves = new LinkedList<>();
        eventSequences = new HashSet<>();
    }

    public SliceDDG(DiGraph<E> graph, EFG efg) {
        this(graph);
        this.efg = efg;
        initEvents = new ArrayList<>();
        findInitEvents();
    }

    public DiGraph<E> getGraph() {
        return graph;
    }

    public void setGraph(DiGraph<E> graph) {
        this.graph = graph;
    }

    public List<List<Node<E>>> getLeafgroups() {
        return leafgroups;
    }

    public void setLeafgroups(List<List<Node<E>>> leafgroups) {
        this.leafgroups = leafgroups;
    }

    public List<Node<E>> getRoots() {
        return roots;
    }

    public void setRoots(List<Node<E>> roots) {
        this.roots = roots;
    }

    public List<Node<E>> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<Node<E>> leaves) {
        this.leaves = leaves;
    }

    public Set<List<E>> getEventSequences() {
        return eventSequences;
    }

    public void findLeaves(){
        for (Node<E> node : graph.getNodes()) {
            if (node.getInEdges().size() == 0 && !node.isOrphan())
                leaves.add(node);
        }
    }


    private int findLeafDistance(Node<E> leaf1, Node<E> leaf2) {
        return 0;
    }

    public void findLeafGroups() {
        List<Node<E>> workingLeaves = leaves;
        int flag = 0;
        List<Node<E>> currentGroup;
        while (!workingLeaves.isEmpty()) {
            Node<E> node = workingLeaves.get(0);
            currentGroup = new ArrayList<Node<E>>();
            populateLeafGroup(node, workingLeaves, currentGroup);
            leafgroups.add(currentGroup);
            workingLeaves.removeAll(currentGroup);
        }
    }



    private List<Node<E>> populateLeafGroup(Node<E> seed, List<Node<E>> workingLeaves, List<Node<E>> ret) {
        if (!ret.contains(seed))
            ret.add(seed);
        List<Node<E>> newWorkingLeaves = new ArrayList<Node<E>>();
        for (Node<E> node : workingLeaves)
            if (!ret.contains(node))
                newWorkingLeaves.add(node);
        for (Node<E> node : newWorkingLeaves) {
            int dist = graph.findDistBetweenLeaves(node, seed);
            if (dist >= 0 && dist <= LEAF_DIST_LIMIT) {
                populateLeafGroup(node, workingLeaves, ret);
            }
        }
        return ret;
    }

    public void generateTestCase () {
        for (Node<E> root : roots) {
            for (List<Node<E>> list : leafgroups) {
                generateTestCase(root, list);
            }
        }
    }

    /* generate test cases given
     * @root,
     * @leaves, which belongs to same group
     */
    public void generateTestCase(Node<E> root, List<Node<E>> leaves) {
        List<List<List<E>>> pathsSet = graph.generatePathsSetBetweenRootAndLeaves(leaves, root);
     //   Olog.log.info(pathsSet.toString());
        List<List<E>> res = new ArrayList<>();
    //    Olog.log.info(System.currentTimeMillis()+"");
        combinationAmongSets(pathsSet, res);
   //     Olog.log.info(System.currentTimeMillis()+"");
    }

    static int ii = 0;
    public void combinationAmongSets(List<List<List<E>>> sets, List<List<E>> res) {
        if (sets.isEmpty()) {
            //        Olog.log.info(res.toString());
            DTree<E> dTree = new DTree<>();
            dTree = dTree.merge(res);
            if (dTree != null) {
                List<E> tmpSeq = dTree.toSequence();
                eventSequences.add(makeExecutable(tmpSeq));
                eventSequences.add(tmpSeq);
            }
        }
        for (List<List<E>> set : sets) {
            List<List<List<E>>> remain = getCopyWithout(sets, set);
            for (List<E> se : set) {
                res.add(se);
                combinationAmongSets(remain, res);
                res.remove(res.size()-1);
                //                Olog.log.info(ii++ + " " + res.size());
            };
        }
    }

    public static <T> List<T> getCopyWithout(List<T> list, T without) {
        List<T> ret = new ArrayList<T>();
        for (T v : list) {
            if (v != without)
                ret.add(v);
        }
        return ret;
    }

    static int i = 0;
    public void writeTestCase(String folder) {
        for (List<E> es : eventSequences) {
            TestCase tc = new TestCase();
            List<StepType> steps = new ArrayList<>();
            String name = "t";
            for (int i = 1; i <= es.size(); i++) {
                E e = es.get(es.size() - i);
                CTEvent event = (CTEvent) e;
                StepType step = new StepType();
                step.setEventId(event.getIdentifier());
                //step.setReachingStep(true);
                steps.add(step);
                name += "_" + event.getIdentifier();
            }
            name += ".tst";
            tc.setStep(steps);
            //      Olog.log.info(tc.toString());
            IO.writeObjToFile(tc, folder+"/" + name);
        }
    }

    public List<E> makeExecutable(List<E> es) {
        List<E> ans = new ArrayList<>();

        E first = es.get(0);
        if (!initEvents.contains(first)) {
            es.add(0, initEvents.get(0));  //start with init event
        }
        ans.add(es.get(0));
        for (int i = 0; i < es.size() - 1; i++) {
            E src = es.get(i);
            E tgt = es.get(i+1);
            if ((initEvents.contains(src) && initEvents.contains(tgt) ) ||
                isExecutable(src, tgt)) {
                ans.add(tgt);
            } else {
                List<E> ex = shortestEFGPathBetween(src, tgt);
                if (ex != null && !ex.isEmpty()) {
                    ex.remove(0);
                    ans.addAll(ex);
                } else {
                    return new ArrayList<>();  //return empty when unreachable
                }
            }
        }
        Olog.log.info("original es: " + es.toString());
        Olog.log.info("executable es: " + ans.toString());
        return ans;
    }

    public boolean isExecutable(E src, E tgt) {
        return getNextEFGEvents(src).contains(tgt);
    }

    //E is a CTEvent
    public List<E> shortestEFGPathBetween(E src, E tgt) {
        Set<E> unsettled = new HashSet<>();
        Set<E> settled = new HashSet<>();
        Map<E, Integer> distance = new HashMap<>();
        Map<E, E> preDecessor = new HashMap<>();
        List<E> ret = new ArrayList<>();
        for (Node<E> node : graph.getNodes()) {
            distance.put(node.getElement(), Integer.MAX_VALUE);
            unsettled.add(node.getElement());
        }
        distance.put(src, 0);
        preDecessor.put(src, null);

        while (!unsettled.isEmpty()) {
            E next = findMinValue(distance, unsettled);
            int dist = distance.get(next);
            if (dist == Integer.MAX_VALUE)
                break;

            settled.add(next);
            unsettled.remove(next);
            for (E child : getNextEFGEvents(next)) {
                int tmp = dist + 1;
                if (tmp < distance.get(child)) {
                    distance.put(child, tmp);
                    preDecessor.put(child, next);
                }
            }
        }

        ret.add(0, tgt);
        while (true ) {
            if (tgt == src) {
                break;
            }
            tgt = preDecessor.get(tgt) ;
            if (tgt == null)
                return null;
            ret.add(0, tgt);
        }
        return ret;
    }

    private E findMinValue(Map<E, Integer> distance, Set<E> unsettled) {
        E ret = null;
        int min = -1;
        for (E node : unsettled) {
            int tmp  = distance.get(node);
            if (ret == null) {
                ret = node;
                min = tmp;
            } else if (tmp < min) {
                ret = node;
                min = tmp;
            }
        }
        return ret;
    }

    //E should be CTEvent
    public List<E> getNextEFGEvents(E event) {
        CTEvent ctevent = (CTEvent) event;
        EventType et = getEventTypeFromCTEvent(ctevent);
        int row = efg.getEvents().getEvent().indexOf(et);
        int size = efg.getEvents().getEvent().size();
        List<E> ret = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            int relation = efg.getEventGraph().getRow().get(row).getE().get(j);
            if (relation > 0) {
                EventType tmp = efg.getEvents().getEvent().get(j);
                ret.add(getCTEventFromEventType(tmp));
            }
        }
        return ret;
    }

    private EventType getEventTypeFromCTEvent(CTEvent e) {
        for (EventType et : efg.getEvents().getEvent()) {
            if (et.getEventId().equals(e.getIdentifier()))
                return et;
        }
        return null;
    }

    private E getCTEventFromEventType(EventType et) {
        String id = et.getEventId();
        for (Node<E> node : graph.getNodes()) {
            E value = node.getElement();
            CTEvent cte = (CTEvent) value;
            if (cte.getIdentifier().equals(id)) {
                return value ; //problem? should new a obj?
            }
        }
        return null;
    }

    public String printEFG(){
        String ret = "\n,";
        for (EventType et : efg.getEvents().getEvent()) {
            ret += et.getEventId() + ",";
        }
        List<RowType> eg = efg.getEventGraph().getRow();
        for (int i = 0; i < eg.size(); i++) {
            ret += "\n" + efg.getEvents().getEvent().get(i).getEventId() + ",";
            RowType row = eg.get(i);
            List<Integer> re = row.getE();
            for (int j = 0; j < re.size(); j++) {
                ret += re.get(j) +",";
            }
        }
        return ret;
    }

    private void findInitEvents() {
        List<EventType> initialEvents = new ArrayList<EventType>();
        List<EventType> eventList = efg.getEvents().getEvent();
        for (EventType event : eventList) {
            if (event.isInitial()) {
                E cte = getCTEventFromEventType(event);
                initEvents.add(cte);
            }

        }
    }
}
