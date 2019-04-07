package edu.wmich.cs.carot;

import edu.umd.cs.guitar.model.data.*;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSlicer;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.group.EventGroupGraph;
import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Edge;
import edu.wmich.cs.graph.Node;
import edu.wmich.cs.carot.group.EventGroup;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.carot.util.StaticFunc;

import java.util.*;

public class Carot implements ICarot {
    DiGraph<CTEvent> ddg;
    EFG efg;
    GUIStructure gui;
    public CTSlicer slicer;
    List<CTEvent> leaves;
    List<List<CTEvent>> leafGroups;

    EventGroupGraph eventGroupGraph;
    Set<List<CTEvent>> eventSequences;


    private static final int LEAF_DIST_LIMIT = 2;
    private Map<String, String> idClassMap = null;


    public static final String[] LEAF_JAVA_TYPE = {
            "javax.swing.JTextField",
            "javax.swing.JTextArea",
            "javax.swing.JComboBox",
            "javax.swing.JRadioButton",
            "javax.swing.JCheckBox",
            "javax.swing.JFormattedTextField"
    };

    public Carot(GUIStructure gui, EFG efg, CTSlicer slicer) {
        this.gui = gui;
        this.efg = efg;
        this.slicer = slicer;
        this.leaves = new ArrayList<>();
        this.leafGroups = new ArrayList<>();
        this.eventGroupGraph = new EventGroupGraph();
        this.eventSequences = new HashSet<>();
    }


    public void addLeaves() {
        findLeaves();
        addLeavesToDdg();
    }

    public void populateDDG() {
        ddg = slicer.generateDiGraph(false);
    }

    public void groupLeaves() {
        findLeafGroups();
    }


    public void populateModuleGraph() {
        for (Node<CTEvent> node : ddg.getNodes()) {
            CTEvent event = node.getElement();
            if (!leaves.contains(event)) {
                EventGroup eventGroup = new EventGroup(event, EventGroup.ModuleType.ACTION);
                Node<EventGroup> mnode = new Node<EventGroup>(eventGroup, eventGroup.toString());
                eventGroupGraph.addNode(mnode);
            }
        }
        for (List<CTEvent> group : leafGroups) {
            EventGroup eventGroup = new EventGroup(group, EventGroup.ModuleType.DATA);
            Node<EventGroup> mnode = new Node<EventGroup>(eventGroup, eventGroup.toString());
            eventGroupGraph.addNode(mnode);
        }

        for (Node<EventGroup> node : eventGroupGraph.getNodes()) {
            Set<CTEvent> allInEvents = new HashSet<>();
            Set<CTEvent> allOutEvents = new HashSet<>();
            Set<Node<EventGroup>> allInNodes = new HashSet<>();
            Set<Node<EventGroup>> allOutNodes = new HashSet<>();

            Set<CTEvent> moduleEvents = node.getElement().getEvents();
            for (CTEvent event : moduleEvents) {
                Node<CTEvent> eventNode = ddg.getNodeByElement(event);
                for (Edge<CTEvent> inEdge: eventNode.getInEdges()) {
                    allInEvents.add(inEdge.getFrom().getElement());
                }
                for (Edge<CTEvent> outEdge: eventNode.getOutEdges()) {
                    allOutEvents.add(outEdge.getTo().getElement());
                }
            }
            for (CTEvent inEvent : allInEvents) {
                allInNodes.add(eventGroupGraph.findNodeModuleByEvent(inEvent));
            }
            for (CTEvent outEvent : allOutEvents) {
                allOutNodes.add(eventGroupGraph.findNodeModuleByEvent(outEvent));
            }
            for (Node<EventGroup> inNode : allInNodes) {
                eventGroupGraph.addEdge(inNode, node);
            }
            for (Node<EventGroup> outNode : allOutNodes) {
                eventGroupGraph.addEdge(node, outNode);
            }
        }
    }

    public void generateTestcases(int len, boolean cyclic, boolean includeShort) {
        eventSequences = eventGroupGraph.generateEventSequence(len, cyclic, includeShort);
    }

    public void writeTestcases(String dir) {
        for (List<CTEvent> es : eventSequences) {
            StaticFunc.writeTestCase(es, dir);
        }
    }


    // getters and setters
    public DiGraph<CTEvent> getDdg() {
        return ddg;
    }

    public void setDdg(DiGraph<CTEvent> ddg) {
        this.ddg = ddg;
    }

    public EFG getEfg() {
        return efg;
    }

    public void setEfg(EFG efg) {
        this.efg = efg;
    }

    public GUIStructure getGui() {
        return gui;
    }

    public void setGui(GUIStructure gui) {
        this.gui = gui;
    }

    public CTSlicer getSlicer() {
        return slicer;
    }

    public void setSlicer(CTSlicer slicer) {
        this.slicer = slicer;
    }

    public void setLeaves(List<CTEvent> leaves) {
        this.leaves = leaves;
    }

    public List<List<CTEvent>> getLeafGroups() {
        return leafGroups;
    }

    public void setLeafGroups(List<List<CTEvent>> leafGroups) {
        this.leafGroups = leafGroups;
    }

    public EventGroupGraph getEventGroupGraph() {
        return eventGroupGraph;
    }

    public void setEventGroupGraph(EventGroupGraph eventGroupGraph) {
        this.eventGroupGraph = eventGroupGraph;
    }

    public List<CTEvent> getLeaves() {
        return leaves;
    }

    public Map<String, String> getIdClassMap() {
        if (idClassMap == null) {
            idClassMap = new HashMap<>();
            computeIdClassMap();
        }
        return idClassMap;
    }

    public Set<List<CTEvent>> getEventSequences() {
        return eventSequences;
    }

    public void setEventSequences(Set<List<CTEvent>> eventSequences) {
        this.eventSequences = eventSequences;
    }

    //private functions
    private void findLeaves() {
        for (CTEvent ctEvent : slicer.getEvents()) {
            String eventJavaType = getEventJavaType(ctEvent);
            if (isLeaf(eventJavaType)) {
                leaves.add(ctEvent);
            }
        }
    }

    private void addLeavesToDdg() {
        String sootPackageName = slicer.getPackage();
        Olog.log.info("adding leaves to ddg...");
        Olog.log.info("package name is " + sootPackageName);
        Map<String, String> eventUnitMap = EventUnitMap.getEventUnitMap(sootPackageName);
        Olog.log.info("leaves are " + eventUnitMap.toString());
        slicer.addLeafEventDefs(eventUnitMap);
    }

    public String getEventJavaType(CTEvent event) {
        String eventId = event.getIdentifier();
        String widgetId = "w" + eventId.substring(1, eventId.length());
        return this.getIdClassMap().get(widgetId);
    }

    private void computeIdClassMap() {
        for (GUIType guiType : gui.getGUI()) {
            for (ComponentType component : guiType.getContainer().getContents().getWidgetOrContainer()) {
                dfs(component);
            }
        }
    }

    private void dfs(ComponentType component) {
        //        Olog.log.info(" level = " + level ++ + component.getClass().toString() );
        if (component instanceof ComponentType) {
            //            Olog.log.info("this is widget");
            getEventJavaTypeFromComponent(component);
        }
        if (component instanceof ContainerType) {
            //  Olog.log.info("this is container");
            ContainerType container = (ContainerType) component;
            for (ComponentType child : container.getContents().getWidgetOrContainer()) {
                dfs(child);
            }
        }
    }

    private void getEventJavaTypeFromComponent(ComponentType component) {
        String tmpClassName = "";
        String tmpWidgetId = "";

        for (PropertyType property : component.getAttributes().getProperty()) {
            String name = property.getName();
            List<String> value = property.getValue();
            if ("Class".equals(name)) {
                assert value.size() == 1;
                tmpClassName = value.get(0);
            }
            if ("ID".equals(name)) {
                tmpWidgetId = value.get(0);
            }
        }
        idClassMap.put(tmpWidgetId, tmpClassName);
    }

    private boolean isLeaf(String eventJavaType) {
        for (int i = 0; i < LEAF_JAVA_TYPE.length; i++) {
            if (eventJavaType.equals(LEAF_JAVA_TYPE[i])) {
                return true;
            }
        }
        return false;
    }

    private void findLeafGroups() {
        List<List<Node<CTEvent>>> leafNodeGroups = new ArrayList<>();
        List<Node<CTEvent>> workingLeaves = new ArrayList<>();
        for (Node<CTEvent> node : ddg.getNodes()) {
            if (leaves.contains(node.getElement()))
                workingLeaves.add(node);
        }
        List<Node<CTEvent>> currentGroup;
        while (!workingLeaves.isEmpty()) {
            Node<CTEvent> node = workingLeaves.get(0);
            currentGroup = new ArrayList<Node<CTEvent>>();
            populateLeafGroup(node, workingLeaves, currentGroup);
            leafNodeGroups.add(currentGroup);
            workingLeaves.removeAll(currentGroup);
        }

        for (List<Node<CTEvent>> group : leafNodeGroups) {
            ArrayList<CTEvent> leafGroup = new ArrayList<>();
            for (Node<CTEvent> node : group) {
                leafGroup.add(node.getElement());
            }
            leafGroups.add(leafGroup);
        }
    }

    private List<Node<CTEvent>> populateLeafGroup(Node<CTEvent> seed, List<Node<CTEvent>> workingLeaves, List<Node<CTEvent>> ret) {
        if (!ret.contains(seed))
            ret.add(seed);
        List<Node<CTEvent>> newWorkingLeaves = new ArrayList<Node<CTEvent>>();
        for (Node<CTEvent> node : workingLeaves)
            if (!ret.contains(node))
                newWorkingLeaves.add(node);
        for (Node<CTEvent> node : newWorkingLeaves) {
            int dist = ddg.findDistBetweenLeaves(node, seed);
            if (dist >= 0 && dist <= LEAF_DIST_LIMIT) {
                populateLeafGroup(node, workingLeaves, ret);
            }
        }
        return ret;
    }



}
