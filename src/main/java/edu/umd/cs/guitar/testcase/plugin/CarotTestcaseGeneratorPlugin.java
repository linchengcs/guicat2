package edu.umd.cs.guitar.testcase.plugin;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.testcase.CarotTestcaseGeneratorConfiguration;
import edu.umd.cs.guitar.testcase.TestCaseGeneratorConfiguration;
import edu.umd.cs.guitar.testcase.plugin.ct.CTBodyTransformer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSlicer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSootRunner;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.Carot;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.carot.util.StaticFunc;

import java.util.List;

/**
 * Created by oliver on 08/09/16.
 */
public class CarotTestcaseGeneratorPlugin extends GTestCaseGeneratorPlugin {

    @Override
    public TestCaseGeneratorConfiguration getConfiguration() {
        return new CarotTestcaseGeneratorConfiguration();
    }

    @Override
    public boolean isValidArgs() {
        return true;
    }

    @Override
    public void generate(EFG efg, String outputDir, int nMaxNumber, boolean noDuplicateEvent, boolean treatTerminalEventSpecially) {
        CTSootRunner sootRunner = new CTSootRunner();
        CTBodyTransformer bodyTransformer = sootRunner.run(
                CarotTestcaseGeneratorConfiguration.SCOPE,
                CarotTestcaseGeneratorConfiguration.CLASSPATH,
                CarotTestcaseGeneratorConfiguration.PACKAGE);


        List<EventType> eventTypes = efg.getEvents().getEvent();
        List<CTEvent> efgEvents = CTEfgEvent.eventType2ctEvent(eventTypes);
        // run slicer
        CTSlicer slicer = new CTSlicer(bodyTransformer, efgEvents);
        slicer.run();

        XMLHandler xml = new XMLHandler();
        GUIStructure gui = (GUIStructure) xml.readObjFromFile(CarotTestcaseGeneratorConfiguration.GUI, GUIStructure.class);

        Carot carot = new Carot(gui, efg, slicer);
        carot.addLeaves();
        carot.populateDDG();

        Olog.log.info(carot.getIdClassMap().toString());
        Olog.log.info(carot.getLeaves().toString());
        Olog.log.info(carot.getDdg().getNodes().toString());

        Olog.log.info(StaticFunc.printEFG(efg));
        Olog.log.info("\n" + carot.slicer.generateDependencyMatrix(false));

        carot.groupLeaves();
        Olog.log.info("leaves: " + carot.getLeaves().size() + ", " + carot.getLeaves().toString());
        Olog.log.info("leaf groups: " + carot.getLeafGroups().size() + ", " + carot.getLeafGroups().toString());

        carot.populateModuleGraph();
        Olog.log.info("modules " + carot.getEventGroupGraph().getNodes().toString());

        carot.generateTestcases(CarotTestcaseGeneratorConfiguration.LENGTH, false, true);
        carot.writeTestcases(outputDir);
        Olog.log.info("testcase: " + carot.getEventSequences().size() + ", " + carot.getEventSequences().toString());
    }

    public static void main(String[] args) {
        String[] args_ticket = new String[] {
                "./aut/ticket.EFG",
                "./aut/ticket.jar"
        };

        String[] args_rachota = new String[] {
                "./aut/rachota.EFG",
                "./aut/Rachota.jar"
        };

        String[] args_terpword = new String[] {
                "./aut/terpword.EFG",
                "./aut/terpword.jar"
        };

        String[] args_barad = new String[] {
                "./aut/barad-ticket.EFG",
                "./aut/barad-ticket.jar"
        };


        //you need change **args** and **PACKAGE**
        args = args_barad;
        // read EFG
        XMLHandler xml = new XMLHandler();
        EFG efg = (EFG) xml.readObjFromFile(args[0], EFG.class);

        // setup analysis
        CarotTestcaseGeneratorPlugin blar = new CarotTestcaseGeneratorPlugin();
        CarotTestcaseGeneratorConfiguration.SCOPE = args[1];
        CarotTestcaseGeneratorConfiguration.LENGTH = 4;
        CarotTestcaseGeneratorConfiguration.PACKAGE = "barad";
        CarotTestcaseGeneratorConfiguration.GUI = "./aut/barad-ticket.GUI";  //**ticket** for Ticket, **barad** for BaradTicket

        // generate test cases
        blar.generate(efg, "./log/barad/carot/testcases", 0, false, false);
    }


}
