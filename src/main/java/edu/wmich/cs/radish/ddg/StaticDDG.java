package edu.wmich.cs.radish.ddg;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.*;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;
import edu.umd.cs.guitar.testcase.JimpleAnalysisConfiguration;
import edu.umd.cs.guitar.testcase.plugin.JimpleAnalysis;
import edu.umd.cs.guitar.testcase.plugin.ct.CTBodyTransformer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSlicer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSootRunner;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTDef;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTUse;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.EventUnitMap;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Label;
import edu.wmich.cs.graph.Node;
import edu.wmich.cs.radish.AutConf;
import edu.wmich.cs.radish.sequence.IDDG;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Lin Cheng on 29/09/16.
 * run the aut, record data
 * generate ddg
 */
public class StaticDDG implements IDDG<CTEvent> {
    private DiGraph<CTEvent> ddg;
    private String autMain;

    private List<CTEvent> events;
    private List<CTEvent> notDuplicateEvents;
    private GUIStructure gui;
    private EFG efg;

    public static Window newWindow = null;


    private String sootScope;
    private String sootPackage;
    private int sootLength;
    private CTSlicer slicer;




    public StaticDDG() {
        this.ddg = new DiGraph<>();
    }

    public StaticDDG(GUIStructure gui, EFG efg, String sootScope, int sootLength, String sootPackage)  {
        this.gui = gui;
        this.efg = efg;
        this.events = CTEfgEvent.eventType2ctEvent(efg.getEvents().getEvent());
        this.notDuplicateEvents = new ArrayList<>();
        this.sootScope = sootScope;
        this.sootLength = sootLength;
        this.sootPackage = sootPackage;
        this.ddg = new DiGraph<>();
    }


    public void runSoot() {
        XMLHandler xml = new XMLHandler();

        // setup analysis
        JimpleAnalysis analysis = new JimpleAnalysis();
        JimpleAnalysisConfiguration.SCOPE = sootScope;
        JimpleAnalysisConfiguration.LENGTH = sootLength;
        JimpleAnalysisConfiguration.PACKAGE = sootPackage;

        // get events
        List<EventType> eventTypes = efg.getEvents().getEvent();

        // execute Soot runner
        CTSootRunner sootRunner = new CTSootRunner();
        CTBodyTransformer bodyTransformer = sootRunner.run(
                JimpleAnalysisConfiguration.SCOPE,
                JimpleAnalysisConfiguration.CLASSPATH,
                JimpleAnalysisConfiguration.PACKAGE);

        // run slicer
        slicer = new CTSlicer(bodyTransformer, events);
        slicer.run();
    }

    @Override
    public void populate() {
        runSoot();
        addDataEventDefs();
        generateDdg();
        generateNotDuplicateEvents();
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

    @Override
    public Set<String> eventRead(CTEvent event){
        Set<String> ret = new HashSet<>();
        Set<CTUse> ctUses = slicer.getEventFieldUses().get(event);
        try {
            for (CTUse ctUse : ctUses)
                ret.add(ctUse.getValue());
        }
        catch (Exception e) {
            int a = 1;
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public Set<String> eventWrite(CTEvent event){
        Set<String> ret = new HashSet<>();
        Set<CTDef> ctDefs = slicer.getEventFieldDefs().get(event);
        for (CTDef ctDef : ctDefs)
            ret.add(ctDef.getValue());
        return ret;
    }

    @Override
    public Set<Set<CTEvent>> getDisconnectedSubGraphs() {
        return null;
    }

    public void setAutMain(String autMain) {
        this.autMain = autMain;
    }

    @Override
    public boolean isIdle(CTEvent event) {
        Set<String> uses = slicer.getEventFieldUses(event);
        if (uses == null || uses.isEmpty())
            return true; //for data widgets
        return  false;
    }

    @Override
    public boolean hasDependency(CTEvent first, CTEvent second) {
        return hasReadWriteDependency(first, second) || hasWriteReadDependency(first, second) || hasWriteWriteDependency(first, second);
   //     return hasWriteReadDependency(first, second);
    }

    @Override
    public List<CTEvent> getNotDuplicateEvents() {
        return notDuplicateEvents;
    }

    private void generateNotDuplicateEvents() {
         for (CTEvent event : events) {
            boolean duplicate = false;
            for (CTEvent added : notDuplicateEvents) {
                if (added.getIdentifier().equals("e1788275568")) {
                    if (event.getIdentifier().equals("e3636878594")) {
                    Olog.log.info("pause");
                }

                   }

                if ( !eventRead(event).isEmpty() || !eventWrite(event).isEmpty() ) {
                    if (eventRead(event).equals(eventRead(added))
                            && eventWrite(event).equals(eventWrite(added)) ) {
                        duplicate = true;
                    //    Olog.log.info("----------find duplicate events: " + event.toString() + ", and " + added.toString());
                        break;
                    }
                }
            }
            if (!duplicate)
                notDuplicateEvents.add(event);
        }
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

    private boolean checkExistsCommandField(Set<String> sf, Set<String> ss) {
        for (String firstValue : sf ) {
            for (String secondValue : ss) {
                String[] tmpFirst = firstValue.split("<|:\\s|\\s|>");
                String[] tmpSecond = secondValue.split("<|:\\s|\\s|>");
                if (tmpFirst[1].equals(tmpSecond[1]) && tmpFirst[2].equals(tmpSecond[2]) && tmpFirst[3].equals(tmpSecond[3]))
                    return true;
            }
        }

        return false;
    }


    public boolean hasWriteWriteDependency(CTEvent first, CTEvent second) {
        Set<String> intersection = new HashSet<>();
        Set<String> sf = slicer.getEventFieldDefs(first);
        Set<String> ss = slicer.getEventFieldDefs(second);
        if (sf == null || ss ==null)
            return false;
        return checkExistsCommandField(sf, ss);
    }

    public boolean hasWriteReadDependency(CTEvent first, CTEvent second) {
        Set<String> intersection = new HashSet<>();
        Set<String> sf = slicer.getEventFieldDefs(first);
        Set<String> ss = slicer.getEventFieldUses(second);
        if (sf == null || ss ==null)
            return false;
        return checkExistsCommandField(sf, ss);
    }

    public boolean hasReadWriteDependency(CTEvent first, CTEvent second) {
        Set<String> intersection = new HashSet<>();
        Set<String> sf = slicer.getEventFieldUses(first);
        Set<String> ss = slicer.getEventFieldDefs(second);
        if (sf == null || ss ==null)
            return false;
        return checkExistsCommandField(sf, ss);
    }

    private void addDataEventDefs() {
        outerloop:
        for (CTEvent event1 : events) {
            if (event1.getListener() != null )
                continue outerloop;
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

            //Set<DataMarker> def = new HashSet<>();
            //DataMarker dm = new DataMarker(title);

      //      Olog.log.info(title.toString());
            if (checkGuiTitleIsInstrumentFormat(title)) {
                Set<CTDef> def = new HashSet<>();
                String[] tmp = title.split("\\.|:");
                String className = tmp[0].replace("/", ".");
                String fieldName = tmp[1];
                String fieldType = tmp[2].substring(1, tmp[2].length() - 1).replace("/", ".");
                String field = "data.<" + className + ": " + fieldType + " " + fieldName + ">";
                CTDef ctdef = new CTDef();
                ctdef.setFieldValue(field);
                ctdef.setValue(field);
                def.add(ctdef);
                slicer.getEventFieldDefs().put(event1, def);
            }
        }
    }

    private boolean checkGuiTitleIsInstrumentFormat(String title) {
        String reg = ".*\\..*:.*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(title);
        return matcher.matches();
    }
    public CTEvent getEventById(String eventId) {
        for (CTEvent event : events)
            if (event.getIdentifier().equals(eventId))
                return event;
        return null;
    }



    /* getters and setters */

    public DiGraph<CTEvent> getDdg() {
        return ddg;
    }

    public void setDdg(DiGraph<CTEvent> ddg) {
        this.ddg = ddg;
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

    public CTSlicer getSlicer() {
        return slicer;
    }

    public static void main(String[] args) {
        String autMain = "edu.wmich.cs.aut.barad.BaradTicket";
         //  dddg.runAut();
        String guiFile = "./log/barad/barad.GUI";
        String efgFile = "./log/barad/barad.EFG";
        XMLHandler xml = new XMLHandler();
        GUIStructure gui = (GUIStructure) xml.readObjFromFile(guiFile, GUIStructure.class);
        EFG efg = (EFG) xml.readObjFromFile(efgFile, EFG.class);
        StaticDDG dddg = new StaticDDG(gui, efg, "./aut/barad-ticket.jar", 1, "barad");
    //    dddg.setAutMain(autMain);
        dddg.populate();
        Olog.log.info(DependencyData.funDefMap.toString());
        Olog.log.info(DependencyData.funUseMap.toString());
        Olog.log.info(DependencyData.funActs.toString());
        Olog.log.info(DependencyData.eventDefMap.toString());
        Olog.log.info(DependencyData.eventUseMap.toString());
        Olog.log.info(dddg.getDdg().toString());
    }


}


