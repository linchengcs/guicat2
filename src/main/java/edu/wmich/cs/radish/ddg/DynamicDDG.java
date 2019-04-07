package edu.wmich.cs.radish.ddg;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.*;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.graph.*;
import edu.wmich.cs.graph.Label;
import edu.wmich.cs.radish.AutConf;
import edu.wmich.cs.radish.sequence.IDDG;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Created by Lin Cheng on 29/09/16.
 * run the aut, record data
 * generate ddg
 */
public class DynamicDDG implements IDDG<CTEvent> {
    private DiGraph<CTEvent> ddg;
    private String autMain;

    private Map<CTEvent, Set<DataMarker>> eventDefs;
    private Map<CTEvent, Set<DataMarker>> eventUses;
    private Map<CTEvent, Set<DataMarker>> dataEventDefs; // data events (without use defined listeners)
    private List<CTEvent> events;
    private GUIStructure gui;
    private EFG efg;

    public static Window newWindow = null;

    public DynamicDDG() {
        this.ddg = new DiGraph<>();
        eventDefs = new HashMap<>();
        eventUses = new HashMap<>();
        dataEventDefs = new HashMap<>();
    }

    public void setAutMain(String autMain) {
        this.autMain = autMain;
    }

    public void populate() {
        Rip rip = new Rip(autMain);
        rip.run();
        addDataEventDefs();
        addActionEventDefUse();
        generateDdg();

    }



    public Set<CTEvent> dependantOn(CTEvent event) {
        return null;
    }

    public void generateDdg () {
        DiGraph<CTEvent> graph = new DiGraph<CTEvent>();
        graph.addNodesFromEvents(events);
        for (Node<CTEvent> row : graph.getNodes()) {
            for (Node<CTEvent> col : graph.getNodes()) {
                CTEvent rowe = (CTEvent) row.getElement();
                CTEvent cole = (CTEvent) col.getElement();
                if (hasDependency(rowe, cole) && rowe != cole) {
                    graph.addEdge(row, col, new Label<>());
                }
            }
        }
        ddg = graph;
    }

    private void addActionEventDefUse () {
        Set<CTEvent> added = eventDefs.keySet();
        for (CTEvent event : events) {
            if (added.contains(event))
                continue;;
            String fun = cTEvent2fun((CTEfgEvent) event);
            if (fun == null || fun.isEmpty())
                continue;
            eventDefs.put(event, DependencyData.eventDefMap.get(fun));
            eventUses.put(event, DependencyData.eventUseMap.get(fun));
        }
    }

    public boolean hasDependency(CTEvent first, CTEvent second) {
        return hasReadWriteDependency(first, second) || hasWriteReadDependency(first, second) || hasWriteWriteDependency(first, second);
    }

    @Override
    public Set<Set<CTEvent>> getDisconnectedSubGraphs() {
        return null;
    }

    @Override
    public Set<CTEvent> getEventUses(CTEvent event) {
        Set<CTEvent> ret = new HashSet<>();
        for (CTEvent candidate : events) {
            if (hasWriteReadDependency(candidate, event))
                ret.add(candidate);
        }
        return ret;
    }

    @Override
    public Set<CTEvent> getEventDefs(CTEvent event) {
        Set<CTEvent> ret = new HashSet<>();
        for (CTEvent candidate : events) {
            if (hasWriteReadDependency(event, candidate))
                ret.add(candidate);
        }
        return ret;
    }

    private String cTEvent2fun(CTEfgEvent e) {
        if (((EventType)e.getObject()).getListeners().isEmpty())
            return null;
        String listener = ((EventType)e.getObject()).getListeners().get(0);
       // String type = getEventPropertyByName(e, "Class");
        String name = "actionPerformed";
        return listener.replace(".", "/") + "." + name;
    }

    public boolean hasWriteWriteDependency(CTEvent first, CTEvent second) {
        Set<DataMarker> intersection = new HashSet<>();
        Set<DataMarker> sf = eventDefs.get(first);
        Set<DataMarker> ss = eventDefs.get(second);
        if (sf == null || ss ==null)
            return false;
        intersection.addAll(sf);
        intersection.retainAll(ss);
        return !intersection.isEmpty();
    }

    @Override
    public List<CTEvent> getNotDuplicateEvents() {
        return null;
    }

    public boolean hasWriteReadDependency(CTEvent first, CTEvent second) {
        Set<DataMarker> intersection = new HashSet<>();
        Set<DataMarker> sf = eventDefs.get(first);
        Set<DataMarker> ss = eventUses.get(second);
        if (sf == null || ss ==null)
            return false;
        intersection.addAll(sf);
        intersection.retainAll(ss);
        return !intersection.isEmpty();
    }

    public boolean hasReadWriteDependency(CTEvent first, CTEvent second) {
        Set<DataMarker> intersection = new HashSet<>();
        Set<DataMarker> sf = eventUses.get(first);
        Set<DataMarker> ss = eventDefs.get(second);
        if (sf == null || ss ==null)
            return false;
        intersection.addAll(sf);
        intersection.retainAll(ss);
        return !intersection.isEmpty();
    }

    private void addDataEventDefs() {
        outerloop:
        for (CTEvent event1 : events) {
            CTEfgEvent event = (CTEfgEvent) event1;
            String widgetId = ((EventType)event.getObject()).getWidgetId();
            GUIStructureWrapper guiw = new GUIStructureWrapper(gui);
            ComponentTypeWrapper cw = guiw.getComponentFromID(widgetId);
            ComponentType ct = cw.getDComponentType();
            AttributesType at = ct.getAttributes();

            String title = null;
            String type = null;
            for (PropertyType pt: at.getProperty()) {
                if (pt.getName().equals("Title")) {
                    title = pt.getValue().get(0);
                }
                if (pt.getName().equals("Class")) {
                    type = pt.getValue().get(0);
                }
                if (type != null && title != null) {
                    if (!AutConf.addAccessibleName(type)) {
                        continue outerloop;
                    }
                    break;
                }
            }
//            assert title != null && !title.isEmpty() : "no title!!!" ;

            Set<DataMarker> def = new HashSet<>();
            DataMarker dm = new DataMarker(title);
            def.add(dm);
            eventDefs.put(event1, def);
            dataEventDefs.put(event1, def);
        }
    }

    private String getEventPropertyByName(CTEvent event, String name) {
        CTEfgEvent event1 = (CTEfgEvent) event;
        String widgetId = ((EventType) event1.getObject()).getWidgetId();
        GUIStructureWrapper guiw = new GUIStructureWrapper(gui);
        ComponentTypeWrapper cw = guiw.getComponentFromID(widgetId);
        ComponentType ct = cw.getDComponentType();
        AttributesType at = ct.getAttributes();
        String value = null;
        for (PropertyType pt : at.getProperty()) {
            if (pt.getName().equals(name)) {
                value = pt.getValue().get(0);
                break;
            }
        }
        return value;
    }

    public CTEvent getDataEventByDef(DataMarker dataMarker) {
//        Olog.log.info(dataMarker.toString());
//        Olog.log.info(dataEventDefs.toString());
        assert !dataEventDefs.isEmpty();
        for (Map.Entry<CTEvent, Set<DataMarker>>  entry : dataEventDefs.entrySet()) {
            CTEvent event = entry.getKey();
            Set<DataMarker> sdm = entry.getValue();
            for (DataMarker dm : sdm) {
                if (dm.equals(dataMarker)) {
                    return event;
                }
            }
        }
        return null;
    }



    /* getters and setters */

    public DiGraph<CTEvent> getDdg() {
        return ddg;
    }

    public void setDdg(DiGraph<CTEvent> ddg) {
        this.ddg = ddg;
    }

    public String getAutMain() {
        return autMain;
    }

    public Map<CTEvent, Set<DataMarker>> getEventDefs() {
        return eventDefs;
    }

    public void setEventDefs(Map<CTEvent, Set<DataMarker>> eventDefs) {
        this.eventDefs = eventDefs;
    }

    public Map<CTEvent, Set<DataMarker>> getEventUses() {
        return eventUses;
    }

    public void setEventUses(Map<CTEvent, Set<DataMarker>> eventUses) {
        this.eventUses = eventUses;
    }

    public List<CTEvent> getEvents() {
        return events;
    }

    public void setEvents(List<CTEvent> events) {
        this.events = events;
    }

    public GUIStructure getGui() {
        return gui;
    }

    public void setGui(GUIStructure gui) {
        this.gui = gui;
    }

    public EFG getEfg() {
        return efg;
    }

    public void setEfg(EFG efg) {
        this.efg = efg;
        this.events = CTEfgEvent.eventType2ctEvent(efg.getEvents().getEvent());
    }

    public Map<CTEvent, Set<DataMarker>> getDataEventDefs() {
        return dataEventDefs;
    }

    public void setDataEventDefs(Map<CTEvent, Set<DataMarker>> dataEventDefs) {
        this.dataEventDefs = dataEventDefs;
    }

    public static void main(String[] args) {
        String autMain = "edu.wmich.cs.aut.barad.BaradTicket";
        DynamicDDG dddg = new DynamicDDG();
        dddg.setAutMain(autMain);
        //  dddg.runAut();
        String guiFile = "./log/barad/barad.GUI";
        String efgFile = "./log/barad/barad.EFG";
        XMLHandler xml = new XMLHandler();
        GUIStructure gui = (GUIStructure) xml.readObjFromFile(guiFile, GUIStructure.class);
        EFG efg = (EFG) xml.readObjFromFile(efgFile, EFG.class);
        dddg.setGui(gui);
        dddg.setEfg(efg);
        dddg.populate();
        Olog.log.info(DependencyData.funDefMap.toString());
        Olog.log.info(DependencyData.funUseMap.toString());
        Olog.log.info(DependencyData.funActs.toString());
        Olog.log.info(DependencyData.eventDefMap.toString());
        Olog.log.info(DependencyData.eventUseMap.toString());
        Olog.log.info(dddg.getEventDefs().toString());
        Olog.log.info(dddg.getEventUses().toString());
        Olog.log.info(dddg.getDdg().toString());
    }

    public List<CTEvent> findShortestPath(CTEvent src, Set<CTEvent> tgts) {
        return null;
    }

}


