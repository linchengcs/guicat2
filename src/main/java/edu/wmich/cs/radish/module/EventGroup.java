package edu.wmich.cs.radish.module;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.Carot;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.carot.util.StaticFunc;
import edu.wmich.cs.radish.AutConf;
import edu.wmich.cs.radish.ddg.DataMarker;
import edu.wmich.cs.radish.ddg.DependencyData;
import edu.wmich.cs.radish.ddg.DynamicDDG;
import edu.wmich.cs.radish.sequence.DPOR;
import edu.wmich.cs.radish.sequence.IDDG;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;

/**
 * Created by Lin Cheng on 02/10/16.
 */
public class EventGroup {
    private GUIStructure gui;
    private edu.wmich.cs.radish.sequence.IDDG<CTEvent> ddg;
    private List<CTEvent> events;
    private List<Set<Set<CTEvent>>> subgroups;
    private static String guiFile;
    private EFG efg;

    private Set<CTEvent> dataEvents;
    private Set<Set<CTEvent>> dataEventsByContainerAndListener;
    private Set<CTEvent> logicEvents;
    private Set<CTEvent> controlEvents;
    private Map<Integer, Set<CTEvent>> dataEventSetsByContainer;
    private Map<String, Set<CTEvent>> dataEventSetsByListener;
    private  Map<CTEvent, Set<String>> dataEventListenerMap ;
    private static Map<Integer, Set<String>> containerEventMap = null;

    private Set<Module> dataModules;    // certain types    priority middle
    private Set<Module> logicModules;   // with user listener  priority high
    private Set<Module> controlModules;  // others  priority low

    private Set<Module> modules;

    public EventGroup () {
        subgroups = new ArrayList<>();
        dataEvents = new HashSet<>();
        dataEventsByContainerAndListener = new HashSet<>();
        logicEvents = new HashSet<>();
        controlEvents = new HashSet<>();
        dataEventSetsByContainer = new HashMap<>();
        dataEventSetsByListener = new HashMap<>();
        dataEventListenerMap = new HashMap<>();
        dataModules = new HashSet<>();
        logicModules = new HashSet<>();
        controlModules = new HashSet<>();
        modules = new HashSet<>();
    }

    public EventGroup(GUIStructure gui, IDDG ddg, List<CTEvent> events) {
        this();
        this.gui = gui;
        this.ddg = ddg;
        this.events = events;
    }

    public EventGroup(GUIStructure gui, EFG efg, IDDG ddg, List<CTEvent> events) {
        this(gui, ddg, events);
        this.efg = efg;
    }

    public static Map<Integer, Set<String>> getContainerEventMap( ) {
        if (containerEventMap == null) {
            if (guiFile == null || guiFile.isEmpty()) {
               Olog.log.error("null guiFile when generating Container Event map");
            }
            containerEventMap = generateContainerEventMap(guiFile);
        }
        return containerEventMap;
    }

    public static Integer getContainer(String eventId  ) {
        for (Map.Entry entrySet : getContainerEventMap().entrySet()) {
            Integer key = (Integer) entrySet.getKey();
            Set<String> value = (Set<String>) entrySet.getValue();
            if (value.contains(eventId)) {
                return key;
            }
        }
        return null;
    }

    public void populate() {
        groupEvents();  // category: data logic
       // generateContainerEventMap(guiFile);
        groupDataEventsByContainer();
        groupDataEventsByLisntener();  //category of data: by c and l
        generateDataEventListenerMap();
        groupDataEvents();
        populateModules();
    }

    private void populateModules() {
        for (Set<CTEvent> events : dataEventsByContainerAndListener) {
                dataModules.add(new DataModule(events));
                modules.add(new DataModule(events));
        }
        for (CTEvent event : logicEvents) {
       //     if (ddg.getNotDuplicateEvents().contains(event)) {
                logicModules.add(new LogicModule(event));
                modules.add(new LogicModule(event));
       //     }
        }
        for (CTEvent event : controlEvents) {
                controlModules.add(new ControlModule(event));
                modules.add(new ControlModule(event));
        }
    }

    private void groupEvents() {
        for (CTEvent event : events) {
            if (isUserEvent((CTEfgEvent)event)) {
                logicEvents.add(event);
            } else if (AutConf.isSwingDataConponent(getEventJavaType(event))) { //not right
                dataEvents.add(event);
            } else {
                controlEvents.add(event);
            }
        }
    }




    /* first step, steps are ordered: groupDataEvents -> group by container -> group by listener
     * generate all data events and logic events
     * dynamic ddg only
     */
    private void groupDataEvents() {
        for (Map.Entry entry : dataEventSetsByContainer.entrySet()) {
            Integer container = (Integer)entry.getKey();
            Set<CTEvent> containerEvents = (Set<CTEvent>) entry.getValue();
            Set<Set<CTEvent>> subsets = new HashSet<>();
            for (CTEvent event : containerEvents) {
                boolean added = false;
                Set<String> eventListeners = dataEventListenerMap.get(event);
                if (eventListeners == null || eventListeners.isEmpty())
                    continue;
                for (Set<CTEvent> subset : subsets) {
                    CTEvent event1 = subset.iterator().next();
                    Set<String> eventListeners1 = dataEventListenerMap.get(event1);
                    if (eventListeners.equals(eventListeners1)) {
                        subset.add(event);
                        added = true;
                    }
                }
                if (!added) {
                    Set<CTEvent> newSet = new HashSet<>();
                    newSet.add(event);
                    subsets.add(newSet);
                }
            }
            dataEventsByContainerAndListener.addAll(subsets);
        }
    }

    /*
     * this function pupulates dataEventSetsByContainer
     */
    private void groupDataEventsByContainer() {
        for (Map.Entry entry : getContainerEventMap().entrySet()) {
            Integer container =  (Integer)entry.getKey();
            Set<String> group = (Set<String>) entry.getValue();
            Set<CTEvent> sub = new HashSet<>();
            for (String eventId : group) {
                CTEvent event = getEventById(eventId);
                if (event != null && dataEvents.contains(event)) {
                    sub.add(event);
                }
            }
            if (!sub.isEmpty()) {
                dataEventSetsByContainer.put(container,sub);
            }
        }
    }

    /*
     * this function populates dataEventSetsByContainerAndListener
     */
    private void groupDataEventsByLisntener() {
        for (CTEvent event : events) {
            if (isUserEvent((CTEfgEvent) event)) {
                String fun = event2DdgFun((CTEfgEvent) event);


                Set<CTEvent> sub = ddg.getEventUses(event);
                if (!sub.isEmpty())
                    dataEventSetsByListener.put(fun,sub);


/*
                Set<CTEvent> sub = new HashSet<>();
                Set<DataMarker> uses = DependencyData.funUseMap.get(fun);
                if (uses != null) {
                    for (DataMarker use : uses) {
                        CTEvent euse = ddg.getDataEventByDef(use);
                        if (euse != null) {
                            sub.add(euse);
                        }
                    }
                    if (!sub.isEmpty()) {
                        dataEventSetsByListener.put(fun, sub);
                    }
                }
                */

            }
        }
    }

    private void generateDataEventListenerMap() {
        for (Map.Entry entry : dataEventSetsByListener.entrySet()) {
            String listener = (String) entry.getKey();
            Set<CTEvent> group = (Set<CTEvent>) entry.getValue();

            for (CTEvent event : group) {
                if (dataEventListenerMap.get(event) == null ) {
                    dataEventListenerMap.put(event, new HashSet<>());
                }
                dataEventListenerMap.get(event).add(listener);
            }

        }
    }

    private String getEventJavaType(CTEvent event) {
        Carot carot = new Carot(gui, null, null);
        return carot.getEventJavaType(event);
    }


    /* first version
     * thought to find a common subsets
     * not working because I want to get rid of output widgets, like infoField
     */
    public void populateBySets() {
        groupBySameContainer();
        groupByCustomListener();
        groupBySameListener();
    }

    // this fumction populates containerEventMap
    private static Map<Integer, Set<String>>  generateContainerEventMap(String guiFile) {
        Map<Integer, Set<String>> containerEventMap = new HashMap<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = dbBuilder.parse(guiFile);
            doc.getDocumentElement().normalize();

            NodeList containers = doc.getElementsByTagName("Contents");
            //  Map<Integer, Set<String>> widgetGroupMap = new HashMap<>(); //map container (Contents) to component event id. the events are not repetitive
            for (int i = 0; i < containers.getLength(); i++) {
                Node container = containers.item(i);   // contents or container
                if (container != null && container.getNodeType() == Node.ELEMENT_NODE) {
                    //     Olog.log.info("----" + container.getNodeType() + container.getChildNodes().getLength() + container.getNodeName());
                    for (int j = 0; j < container.getChildNodes().getLength(); j++) {
                        Node widget = container.getChildNodes().item(j);    // widget
                        if (widget != null && widget.getNodeType() == Node.ELEMENT_NODE) {
                            //  Olog.log.info("--------" + widget.getNodeType() + widget.getNodeName()  );
                            for (int k = 0; k < widget.getChildNodes().getLength(); k++) {
                                Node attributes = widget.getChildNodes().item(k); // Attributes
                                if (attributes != null && attributes.getNodeType() == Node.ELEMENT_NODE) {
                                    //   Olog.log.info("------------" + attributes.getNodeType() + attributes.getNodeName());
                                    for (int s = 0; s < attributes.getChildNodes().getLength(); s++) {
                                        Node property = attributes.getChildNodes().item(s);
                                        if (property != null && property.getNodeType() == Node.ELEMENT_NODE) {
                                            //   Olog.log.info("-----------------" + property.getNodeName());
                                            String name = null;
                                            String value = null;
                                            for (int t = 0; t < property.getChildNodes().getLength(); t++) {
                                                Node leaf = property.getChildNodes().item(t);
                                                if (leaf != null && leaf.getNodeType() == Node.ELEMENT_NODE) {
                                                    //  Olog.log.info("----------------------" + leaf.getNodeName() + ":" + leaf.getTextContent());
                                                    if (leaf.getTextContent().equals("ID") && leaf.getNodeName().equals("Name")) {
                                                        name = "ID";
                                                    }
                                                    if ("ID".equals(name) && leaf.getNodeName().equals("Value")) {
                                                        value = leaf.getTextContent();
                                                        name = null;
                                                        if (containerEventMap.get(container.hashCode()) == null) {
                                                            containerEventMap.put(container.hashCode(), new HashSet<String>());
                                                        }
                                                        containerEventMap.get(container.hashCode()).add(value.replace('w', 'e'));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return containerEventMap;
    }

    private void groupBySameContainer() {
        Set<Set<CTEvent>> subgroup = new HashSet<>();
        for (Set<String> group : getContainerEventMap().values()) {
            Set<CTEvent> sub = new HashSet<>();
            for (String eventId : group) {
                CTEvent event = getEventById(eventId);
                if (event != null) {
                    sub.add(event);
                }
            }
            if (!sub.isEmpty()) {
                subgroup.add(sub);
            }
        }
        subgroups.add(subgroup);
    }


    /* all sys events are grouped by listener read
     * use events are already in unit group
     * listener write is ignored
     * only consider those in FunAct,
     * DynamicDDG only
     */
    private void groupBySameListener() {
        Set<Set<CTEvent>> subgroup = new HashSet<>();
        for (CTEvent event : events) {
            if (isUserEvent((CTEfgEvent) event)) {
                Set<CTEvent> sub = new HashSet<>();
                String fun = event2DdgFun((CTEfgEvent) event);
                Set<DataMarker> uses = DependencyData.funUseMap.get(fun);
                Olog.log.info(fun);
                if (uses != null) {
                    Olog.log.info(uses.toString());
                    for (DataMarker use : uses) {
                        CTEvent euse = ((DynamicDDG)ddg).getDataEventByDef(use);
                        if (euse != null) {
                            sub.add(euse);
                        }
                    }
                    if (!sub.isEmpty()) {
                        subgroup.add(sub);
                    }
                }
            }
        }
        subgroups.add(subgroup);
    }

    /* listeners defined by use are in different groups, each group has a user defined listener
     * all events without user defined listeners, are in the same group.
     * all information can be found in EFG
     */
    private void groupByCustomListener() {
        Set<Set<CTEvent>> subgroup = new HashSet<>();
        Set<CTEvent> sysgroup = new HashSet<>();
        subgroup.add(sysgroup);   // no need to add becuase those will group by container
        for (CTEvent event : events) {
            if (isUserEvent((CTEfgEvent)event)) {
                Set<CTEvent> sub = new HashSet<>();
                sub.add(event);
                subgroup.add(sub);
            } else {
                sysgroup.add(event);
            }
        }
        subgroups.add(subgroup);
    }

    private boolean isUserEvent(CTEfgEvent event) {
        List<String> listeners = ((EventType)event.getObject()).getListeners();
        if (listeners == null || listeners.isEmpty())
            return false;
        return true;
    }

    private String event2DdgFun(CTEfgEvent event) {
        return ((EventType) event.getObject()).getListeners().get(0).replace(".", "/") + ".actionPerformed";
    }

    public CTEvent getEventById(String eventId) {
        for (CTEvent event : events)
            if (event.getIdentifier().equals(eventId))
                return event;
        return null;
    }


    private void groupBySameContainer1() {
        GUIStructureWrapper gw = new GUIStructureWrapper(gui);
        Map<ComponentTypeWrapper, Set<CTEvent>> parentChildrenMap = new HashMap<>();
        for (CTEvent event : events) {
            String widgetId = StaticFunc.getEventWidgetId(event);
            ComponentTypeWrapper gtw = gw.getComponentFromID(widgetId).getParent();
            if (parentChildrenMap.get(gtw) == null) {
                parentChildrenMap.put(gtw, new HashSet<>());
            }
            parentChildrenMap.get(gtw).add(event);
        }
        subgroups.add(new HashSet<Set<CTEvent>>(parentChildrenMap.values()));
    }


    // getters and setters
    public GUIStructure getGui() {
        return gui;
    }

    public void setGui(GUIStructure gui) {
        this.gui = gui;
    }

    public IDDG getDdg() {
        return ddg;
    }

    public void setDdg(edu.wmich.cs.radish.sequence.IDDG ddg) {
        this.ddg = ddg;
    }

    public List<CTEvent> getEvents() {
        return events;
    }

    public void setEvents(List<CTEvent> events) {
        this.events = events;
    }

    public List<Set<Set<CTEvent>>> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(List<Set<Set<CTEvent>>> subgroups) {
        this.subgroups = subgroups;
    }

    public String getGuiFile() {
        return guiFile;
    }

    public void setGuiFile(String guiFile) {
        this.guiFile = guiFile;
    }

    public EFG getEfg() {
        return efg;
    }

    public void setEfg(EFG efg) {
        this.efg = efg;
    }

    public Set<CTEvent> getDataEvents() {
        return dataEvents;
    }

    public void setDataEvents(Set<CTEvent> dataEvents) {
        this.dataEvents = dataEvents;
    }

    public Set<CTEvent> getLogicEvents() {
        return logicEvents;
    }

    public void setLogicEvents(Set<CTEvent> logicEvents) {
        this.logicEvents = logicEvents;
    }

    public Set<Module> getDataModules() {
        return dataModules;
    }

    public void setDataModules(Set<Module> dataModules) {
        this.dataModules = dataModules;
    }

    public Set<Module> getLogicModules() {
        return logicModules;
    }

    public void setLogicModules(Set<Module> logicModules) {
        this.logicModules = logicModules;
    }

    public Set<Set<CTEvent>> getDataEventsByContainerAndListener() {
        return dataEventsByContainerAndListener;
    }

    public void setDataEventsByContainerAndListener(Set<Set<CTEvent>> dataEventsByContainerAndListener) {
        this.dataEventsByContainerAndListener = dataEventsByContainerAndListener;
    }

    public Set<CTEvent> getControlEvents() {
        return controlEvents;
    }

    public void setControlEvents(Set<CTEvent> controlEvents) {
        this.controlEvents = controlEvents;
    }

    public Map<Integer, Set<CTEvent>> getDataEventSetsByContainer() {
        return dataEventSetsByContainer;
    }

    public void setDataEventSetsByContainer(Map<Integer, Set<CTEvent>> dataEventSetsByContainer) {
        this.dataEventSetsByContainer = dataEventSetsByContainer;
    }

    public Map<String, Set<CTEvent>> getDataEventSetsByListener() {
        return dataEventSetsByListener;
    }

    public void setDataEventSetsByListener(Map<String, Set<CTEvent>> dataEventSetsByListener) {
        this.dataEventSetsByListener = dataEventSetsByListener;
    }

    public Map<CTEvent, Set<String>> getDataEventListenerMap() {
        return dataEventListenerMap;
    }

    public void setDataEventListenerMap(Map<CTEvent, Set<String>> dataEventListenerMap) {
        this.dataEventListenerMap = dataEventListenerMap;
    }

    public Set<Module> getControlModules() {
        return controlModules;
    }

    public void setControlModules(Set<Module> controlModules) {
        this.controlModules = controlModules;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    public static void main(String[] args) {
        String autMain = "edu.wmich.cs.aut.barad.BaradTicket";
        String guiFile = "./log/barad/barad.GUI";
        String efgFile = "./log/barad/barad.EFG";

//        String autMain = "edu.wmich.cs.aut.workout.AbstractMain";
//        String guiFile = "./log/workout/workout.GUI";
//        String efgFile = "./log/workout/workout.EFG";

//        String autMain = "edu.wmich.cs.aut.addressbook.AddressBook";
//        String guiFile = "./log/addressbook/addressbook.GUI";
//        String efgFile = "./log/addressbook/addressbook.EFG";

//        String autMain = "edu.wmich.cs.aut.payment.PaymentForm";
//        String guiFile = "./log/payment/payment.GUI";
//        String efgFile = "./log/payment/payment.EFG";


//        String autMain = "TerpSpreadSheet";
//        String guiFile = "./log/terpspreadsheet/terpspreadsheet.GUI";
//        String efgFile = "./log/terpspreadsheet/terpspreadsheet.EFG";

//        String autMain = "org.cesilko.rachota.gui.MainWindow";
//        String guiFile = "./log/rachota/rachota.GUI";
//        String efgFile = "./log/rachota/rachota.EFG";

//        String autMain = "jgp.gui.JGP";
//        String guiFile = "./log/jgp/jgp.GUI";
//        String efgFile = "./log/jgp/jgp.EFG";

//        String autMain = "examples.jnotepad.Notepad";
//        String guiFile = "./log/jnotepad/jnotepad.GUI";
//        String efgFile = "./log/jnotepad/jnotepad.EFG";

//        String autMain = "calc.Copyright";
//        String guiFile = "./log/calc/calc.GUI";
//        String efgFile = "./log/calc/calc.EFG";


//        String autMain = "gui.ClassEditor";
//        String guiFile = "./log/ce/ce.GUI";
//        String efgFile = "./log/ce/ce.EFG";

//        String autMain = "regextester.RegexTester";
//        String guiFile = "./log/regextester/regextester.GUI";
//        String efgFile = "./log/regextester/regextester.EFG";

//        String autMain = "de.beimax.janag.NamegenGUI";
//        String guiFile = "./log/janag/janag.GUI";
//        String efgFile = "./log/janag/janag.EFG";


//        String autMain = "hashvcalc.HashVcalc";
//        String guiFile = "./log/hashvcalc/hashvcalc.GUI";
//        String efgFile = "./log/hashvcalc/hashvcalc.EFG";

//        String autMain = "crosswordsage.MainScreen";
//        String guiFile = "./log/crosswordsage/crosswordsage.GUI";
//        String efgFile = "./log/crosswordsage/crosswordsage.EFG";



        DynamicDDG dddg = new DynamicDDG();
        dddg.setAutMain(autMain);
        //  dddg.runAut();
        XMLHandler xml = new XMLHandler();
        GUIStructure gui = (GUIStructure) xml.readObjFromFile(guiFile, GUIStructure.class);
        EFG efg = (EFG) xml.readObjFromFile(efgFile, EFG.class);
        dddg.setGui(gui);
        dddg.setEfg(efg);
        dddg.populate();
        Olog.log.info("funDefMap: " + DependencyData.funDefMap.toString());
        Olog.log.info("funUseMap: " + DependencyData.funUseMap.toString());
//        Olog.log.info(DependencyData.funActs.toString());
        Olog.log.info("eventDefMap: " + DependencyData.eventDefMap.toString());
        Olog.log.info("eventUseMap: " + DependencyData.eventUseMap.toString());
        Olog.log.info("event defs" + dddg.getEventDefs().toString());
        Olog.log.info("event uses" + dddg.getEventUses().toString());
        Olog.log.info(dddg.getDdg().toString());

        EventGroup eventGroup = new EventGroup(gui, efg, dddg, dddg.getEvents());
        eventGroup.setGuiFile(guiFile);
        eventGroup.populate();


        Olog.log.info(eventGroup.getSubgroups().toString());
        Olog.log.info("data events" + eventGroup.dataEvents.toString());
        Olog.log.info("login events" + eventGroup.logicEvents.toString());
        Olog.log.info("control events" + eventGroup.controlEvents.toString());
        Olog.log.info("data event sets by container" + eventGroup.dataEventSetsByContainer.toString());
        Olog.log.info("data event sets by listener" + eventGroup.dataEventSetsByListener.toString());

        Olog.log.info("===modules===");
        Olog.log.info(eventGroup.getLogicModules().toString());
        Olog.log.info(eventGroup.getDataModules().toString());

        ModuleEFG moduleEFG = new ModuleEFG(eventGroup);
        moduleEFG.populate();

        Olog.log.info("==this is moduleEFG==");
        Olog.log.info(moduleEFG.toString());

        ModuleDDG moduleDDg = new ModuleDDG(eventGroup, dddg);
        moduleDDg.populate();
        Olog.log.info("==this is moduleDDG==");
        Olog.log.info(moduleDDg.toString());
        Olog.log.info("disconnected sub graphs: " + moduleDDg.getDisconnectedSubGraphs().toString());

        DPOR<Module> dpor = new DPOR<Module>(moduleEFG, moduleDDg, eventGroup.modules);
      //  dpor.generateFirst();
       // dpor.generate(new ArrayList<Module>(), 0);
        dpor.generate(dpor.stacks, 0);
    }

}
