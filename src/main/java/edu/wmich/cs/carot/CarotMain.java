package edu.wmich.cs.carot;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.testcase.JimpleAnalysisConfiguration;
import edu.umd.cs.guitar.testcase.plugin.JimpleAnalysis;
import edu.umd.cs.guitar.testcase.plugin.ct.CTBodyTransformer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSlicer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSootRunner;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;

import java.util.List;

public class CarotMain {
    public static void main(String[] args) {
        String[] args_ticket = new String[] {
            "./aut/ticket.EFG",
            "./aut/ticket.jar"
        };

        String[] args_rachota = new String[] {
            "./log/rachota/rachota.EFG",
            "./aut/Rachota.jar",
                "./log/rachota/rachota.GUI",
        };

        String[] args_terpword = new String[] {
            "./aut/terpword.EFG",
            "./aut/terpword.jar"
        };

        String[] args_barad = new String[] {
            "./log/barad/barad.EFG",
            "./aut/barad-ticket.jar",
            "./log/barad/barad.GUI"
        };

        String[] args_jnotepad = new String[] {
            "./aut/jnotepad.EFG",
            "./aut/jnotepad.jar"
        };

        String[] args_terpspreadsheet = new String[] {
            "./aut/terpspreadsheet.EFG",
            "./aut/terpspreadsheet.jar"
        };

        String[] args_terppaint = new String[] {
            "./aut/terppaint.EFG",
            "./aut/TerpPaint3"
        };

        String[] args_workout = new String[] {
            "./aut/workout.EFG",
            "./aut/workout.jar"
        };

        //you need change **args** and **PACKAGE**
        args = args_barad;
        // read EFG
        XMLHandler xml = new XMLHandler();
        EFG efg = (EFG) xml.readObjFromFile(args[0], EFG.class);
        GUIStructure gui = (GUIStructure) xml.readObjFromFile(args[2], GUIStructure.class);

        // setup analysis
        JimpleAnalysis analysis = new JimpleAnalysis();
        JimpleAnalysisConfiguration.SCOPE = args[1];
        JimpleAnalysisConfiguration.LENGTH = 1;
        JimpleAnalysisConfiguration.PACKAGE = EventUnitMap.SOOT_PKG_BARAD;  //**ticket** for Ticket, **barad** for BaradTicket  **workout** for workout


        // get events
        List<EventType> eventTypes = efg.getEvents().getEvent();
        List<CTEvent> efgEvents = CTEfgEvent.eventType2ctEvent(eventTypes);

        // execute Soot runner
        CTSootRunner sootRunner = new CTSootRunner();
        CTBodyTransformer bodyTransformer = sootRunner.run(
                                                           JimpleAnalysisConfiguration.SCOPE,
                                                           JimpleAnalysisConfiguration.CLASSPATH,
                                                           JimpleAnalysisConfiguration.PACKAGE);

        // run slicer
        CTSlicer slicer = new CTSlicer(bodyTransformer, efgEvents);
        slicer.run();

        Carot carot = new Carot(gui, efg, slicer);
        carot.addLeaves();
        carot.populateDDG();

        Olog.log.info(carot.getIdClassMap().toString());
        Olog.log.info(carot.getLeaves().toString());
        Olog.log.info(carot.getDdg().getNodes().toString());


        Olog.log.info("\n" + carot.slicer.generateDependencyMatrix(false));

        carot.groupLeaves();
        Olog.log.info(carot.getLeafGroups().toString());

        carot.populateModuleGraph();
        Olog.log.info(carot.getEventGroupGraph().getNodes().toString());

   //     Olog.log.info(carot.getEventGroupGraph().generatePath(2, false, true).toString());
        carot.generateTestcases(2, false, true);
        Olog.log.info(carot.getEventSequences().toString());


    }
}
