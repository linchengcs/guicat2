package edu.wmich.cs.spinach;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.RowType;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.radish.ddg.StaticDDG;
import edu.wmich.cs.radish.module.EventGroup;
import edu.wmich.cs.radish.module.Module;
import edu.wmich.cs.radish.module.ModuleDDG;
import edu.wmich.cs.radish.module.ModuleEFG;
import edu.wmich.cs.radish.sequence.IDDG;
import edu.wmich.cs.radish.sequence.IEFG;

import java.util.Arrays;

/**
 * Created by Lin Cheng on 12/29/16.
 */
public class ModuleMain extends AbstractMain<Module> {

    public ModuleMain(){}

    public static void main(String[] args) {

        if (args.length == 0) {
            args = new String[] {
                    "log/barad/barad.GUI",
                    "log/barad/barad.EFG",
                    "./aut/barad-ticket.jar",
                    "sleepset+reduction",
                    "3",
                    "log/barad/testcase"};

//            args = new String[] {
//                    "log/addressbook/addressbook.GUI",
//                    "log/addressbook/addressbook.EFG",
//                    "./aut/addressbook.jar",
//                    "allseq",
//                    "1",
//                    "log/addressbook/testcase"};

//            args = new String[] {
//                    "./log/ce/ce.GUI",
//                    "./log/ce/ce.EFG",
//                    "./aut/ce.jar",
//                    "sleepset+idle",
//                    "2",
//                    "./log/ce/testcase"};


//            args = new String[] {
//                    "./log/crosswordsage/crosswordsage.GUI",
//                    "./log/crosswordsage/crosswordsage.EFG",
//                    "./aut/CrosswordSage.jar",
//                    "sleepset+reduction",
//                    "3",
//                    "./log/crosswordsage/testcase"};

//            args = new String[] {
//                    "./log/calc/calc.GUI",
//                    "./log/calc/calc.EFG",
//                    "./aut/Calc.jar",
//                    "sleepset+reduction",
//                    "3",
//                    "./log/calc/testcase"};

        }

        String guiFile = args[0];
        String efgFile = args[1];
        String sootScope = args[2];
        String algorithm = args[3];
        int len = Integer.parseInt(args[4]);
        String dir = args[5];
        String sootPackage = "";

        Olog.log.info("Your arguments for AUTMain are" + Arrays.asList(args).toString());




        XMLHandler xml = new XMLHandler();
        EFG efg = (EFG) xml.readObjFromFile(efgFile, EFG.class);
        IEFG iefg = new AutEFG(efg);

        GUIStructure gui = (GUIStructure) xml.readObjFromFile(guiFile, GUIStructure.class);

        StaticDDG sddg = new StaticDDG(gui, efg, sootScope, 1, sootPackage);
        sddg.populate();
    //    Olog.log.info(sddg.getDdg().toString());
        IDDG ddg = sddg;


        EventGroup eventGroup = new EventGroup(gui, efg, sddg, sddg.getEvents());
        eventGroup.setGuiFile(guiFile);
        eventGroup.populate();


//            Olog.log.info(eventGroup.getSubgroups().toString());
//        Olog.log.info("data events" + eventGroup.getDataEvents().toString());
//        Olog.log.info("login events" + eventGroup.getLogicEvents().toString());
//        Olog.log.info("control events" + eventGroup.getControlEvents().toString());
//        Olog.log.info("data event sets by container" + eventGroup.getDataEventSetsByContainer().toString());
//        Olog.log.info("data event sets by listener" + eventGroup.getDataEventSetsByListener().toString());

        Olog.log.info("===modules===");
        Olog.log.info("logic modules: " + eventGroup.getLogicModules().toString());
        Olog.log.info("control modules: " + eventGroup.getControlEvents().toString());
        Olog.log.info("data modules: " + eventGroup.getDataModules().toString());


        Olog.autInfo.info("parameters: " + Arrays.asList(args).toString());
        ModuleEFG moduleEFG = new ModuleEFG(eventGroup);
        moduleEFG.populate();

//        Olog.log.info("==this is moduleEFG==");
//        Olog.log.info(moduleEFG.toString());


        //need implement module idle
        ModuleDDG moduleDDg = new ModuleDDG(eventGroup, sddg, iefg);
        moduleDDg.populate();
        Olog.log.info("==this is moduleDDG==");
        Olog.log.info(moduleDDg.toString());
//        Olog.log.info("disconnected sub graphs: " + moduleDDg.getDisconnectedSubGraphs().toString());

        Olog.autInfo.info("number of events: " + moduleEFG.size());
        Olog.autInfo.info("number of edges: " + moduleEFG.edgeSize());


        Olog.log.info("moduleEFG\n " + moduleEFG.toString());
        Olog.log.info("EFG\n" + efg.toString() );



        int i = 0;
        int sum = 0;
        for (RowType rowType  :  efg.getEventGraph().getRow()) {
            int j = 0;
            for (Integer e : rowType.getE())
                if (e > 0)
                    j++;
            Olog.log.info("event: " + efg.getEvents().getEvent().get(i++).getEventId() + ": " + j);
            sum += j;
        }
        Olog.log.info("number of rows: " + i);
        Olog.log.info("number of edges: " + sum);


        int bound = len + 1;
       // IEventSequenceHandler eventSequenceHandler = new ModuleSequenceHandler(dir);
          IEventSequenceHandler eventSequenceHandler = new LogEventSequenceHandler();

        IBacktrackUpdator backtrackUpdator ;
        if ("allseq".equals(algorithm)) {
            backtrackUpdator = new BaselineBacktrackUpdator();
        } else if ("sleepset".equals(algorithm)) {
            backtrackUpdator = new SleepSetBacktrackUpdator();
        } else if ("sleepset+idle".equals(algorithm)) {
            backtrackUpdator = new SleepSetWithIdleBacktrackUpdator();
        } else if ("sleepset+coverWrite".equals(algorithm)) {
            backtrackUpdator = new SleepSetWithCoverWriteUpdator();
        } else if ("sleepset+generalizedCoverWrite".equals(algorithm)) {
            backtrackUpdator = new SleepSetWithGeneralizedCoverWriteUpdator();
        } else if ("sleepset+irrelevantTail".equals(algorithm)) {
            backtrackUpdator = new SleepSetWithIrrelevantTailUpdator();
        } else if ("sleepset+extraRoot".equals(algorithm)) {
            backtrackUpdator = new SleepSetWithExtraRootUpdator();
        } else if ("sleepset+reduction".equals(algorithm)) {
            backtrackUpdator = new SleepSetWithReductionUpdator();
        } else {
            Olog.log.info("invalid algorithm!!");
            return;
        }

        ModuleMain moduleMain = new ModuleMain();

        moduleMain.setEfg(moduleEFG);
        moduleMain.setDdg(moduleDDg);
        moduleMain.setBacktrackUpdator(backtrackUpdator);
        moduleMain.setDFS_MAX_LENGTH(bound);
        moduleMain.setEventSequenceHandler(eventSequenceHandler);
        moduleMain.run();

//        AUTMain testAUT= new AUTMain();
//        testAUT.setEfg(iefg);
//        testAUT.setDdg(ddg);
//        testAUT.setBacktrackUpdator(backtrackUpdator);
//        testAUT.setDFS_MAX_LENGTH(bound);
//        testAUT.setEventSequenceHandler(eventSequenceHandler);
//        testAUT.run();

    }
}
