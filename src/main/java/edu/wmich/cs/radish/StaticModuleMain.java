package edu.wmich.cs.radish;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.radish.ddg.DependencyData;
import edu.wmich.cs.radish.ddg.DynamicDDG;
import edu.wmich.cs.radish.ddg.StaticDDG;
import edu.wmich.cs.radish.module.EventGroup;
import edu.wmich.cs.radish.module.Module;
import edu.wmich.cs.radish.module.ModuleDDG;
import edu.wmich.cs.radish.module.ModuleEFG;
import edu.wmich.cs.radish.sequence.DPOR;

/**
 * Created by rick on 12/12/16.
 */
public class StaticModuleMain {
    public static void main(String[] args) {
//        String autMain = "edu.wmich.cs.aut.barad.BaradTicket";
//        String guiFile = "./log/barad/barad.GUI";
//        String efgFile = "./log/barad/barad.EFG";
//        String sootScope="./aut/barad-ticket.jar";
//        String sootPackage = "barad";
//        int dporMaxLength = 3;

//        String autMain = "edu.wmich.cs.aut.workout.AbstractMain";
//        String guiFile = "./log/workout/workout.GUI";
//        String efgFile = "./log/workout/workout.EFG";
//        String sootScope = "./aut/workout.jar";
//        String sootPackage = "workout";
//        int dporMaxLength = 3;


//        String autMain = "edu.wmich.cs.aut.addressbook.AddressBook";
//        String guiFile = "./log/addressbook/addressbook.GUI";
//        String efgFile = "./log/addressbook/addressbook.EFG";
//        String sootScope = "./aut/addressbook.jar";
//        String sootPackage = "adderssbook";
//        int dporMaxLength = 10;

//        String autMain = "edu.wmich.cs.aut.payment.PaymentForm";
//        String guiFile = "./log/payment/payment.GUI";
//        String efgFile = "./log/payment/payment.EFG";
//        String sootScope = "./aut/payment.jar";
//        String sootPackage = "payment";
//        int dporMaxLength = 5;


        //
//        String autMain = "TerpSpreadSheet";
//        String guiFile = "./log/terpspreadsheet/terpspreadsheet.GUI";
//        String efgFile = "./log/terpspreadsheet/terpspreadsheet.EFG";
//        String sootScope = "./aut/terpspreadsheet.jar";
//        String sootPackage = "terpspreadsheet";
//        int dporMaxLength = 1;

        //no modules
//        String autMain = "org.cesilko.rachota.gui.MainWindow";
//        String guiFile = "./log/rachota/rachota.GUI";
//        String efgFile = "./log/rachota/rachota.EFG";
//        String sootScope = "./aut/rachota.jar";
//        String sootPackage = "rachota";
//        int dporMaxLength = 1;

        //runs well , but no module
//        String autMain = "jgp.gui.JGP";
//        String guiFile = "./log/jgp/jgp.GUI";
//        String efgFile = "./log/jgp/jgp.EFG";
//        String sootScope = "./aut/jgp.jar";
//        String sootPackage = "jgp";
//        int dporMaxLength = 1;

        //runs well, no modules, size samll
//        String autMain = "examples.jnotepad.Notepad";
//        String guiFile = "./log/jnotepad/jnotepad.GUI";
//        String efgFile = "./log/jnotepad/jnotepad.EFG";
//        String sootScope = "./aut/jnotepad.jar";
//        String sootPackage = "jnotepad";
//        int dporMaxLength = 1;

        //no connected nodes
//        String autMain = "calc.Copyright";
//        String guiFile = "./log/calc/calc.GUI";
//        String efgFile = "./log/calc/calc.EFG";
//        String sootScope = "./aut/calc.jar";
//        String sootPackage = "calc";
//        int dporMaxLength = 1;

//big  no modules
//        String autMain = "gui.ClassEditor";
//        String guiFile = "./log/ce/ce.GUI";
//        String efgFile = "./log/ce/ce.EFG";
//        String sootScope = "./aut/ce.jar";
//        String sootPackage = "ce";
//        int dporMaxLength = 10;

        //working
//        String autMain = "regextester.RegexTester";
//        String guiFile = "./log/regextester/regextester.GUI";
//        String efgFile = "./log/regextester/regextester.EFG";
//        String sootScope = "./aut/RegexTester.jar";
//        String sootPackage = "regextester";
//        int dporMaxLength = 4;

        //not working
//        String autMain = "de.beimax.janag.NamegenGUI";
//        String guiFile = "./log/janag/janag.GUI";
//        String efgFile = "./log/janag/janag.EFG";
//        String sootScope = "./aut/janag.jar";
//        String sootPackage = "janag";
//        int dporMaxLength = 1;


//        String autMain = "hashvcalc.HashVcalc";
//        String guiFile = "./log/hashvcalc/hashvcalc.GUI";
//        String efgFile = "./log/hashvcalc/hashvcalc.EFG";
//        String sootScope = "./aut/HashVcalc.jar";
//        String sootPackage = "hashvcalc";
//        int dporMaxLength = 3;

        String autMain = "crosswordsage.MainScreen";
        String guiFile = "./log/crosswordsage/crosswordsage.GUI";
        String efgFile = "./log/crosswordsage/crosswordsage.EFG";
        String sootScope = "./aut/CrosswordSage.jar";
        String sootPackage = "crosswordsage";
        int dporMaxLength = 15;


        XMLHandler xml = new XMLHandler();
        GUIStructure gui = (GUIStructure) xml.readObjFromFile(guiFile, GUIStructure.class);
        EFG efg = (EFG) xml.readObjFromFile(efgFile, EFG.class);
        StaticDDG sddg = new StaticDDG(gui, efg, sootScope, 1, sootPackage);
        //    dddg.setAutMain(autMain);
        sddg.populate();
        Olog.log.info(DependencyData.funDefMap.toString());
        Olog.log.info(DependencyData.funUseMap.toString());
        Olog.log.info(DependencyData.funActs.toString());
        Olog.log.info(DependencyData.eventDefMap.toString());
        Olog.log.info(DependencyData.eventUseMap.toString());
        Olog.log.info(sddg.getDdg().toString());

        EventGroup eventGroup = new EventGroup(gui, efg, sddg, sddg.getEvents());
        eventGroup.setGuiFile(guiFile);
        eventGroup.populate();


        Olog.log.info(eventGroup.getSubgroups().toString());
        Olog.log.info("data events" + eventGroup.getDataEvents().toString());
        Olog.log.info("login events" + eventGroup.getLogicEvents().toString());
        Olog.log.info("control events" + eventGroup.getControlEvents().toString());
        Olog.log.info("data event sets by container" + eventGroup.getDataEventSetsByContainer().toString());
        Olog.log.info("data event sets by listener" + eventGroup.getDataEventSetsByListener().toString());

        Olog.log.info("===modules===");
        Olog.log.info("logic modules: " + eventGroup.getLogicModules().toString());
        Olog.log.info("data modules: " + eventGroup.getDataModules().toString());

        ModuleEFG moduleEFG = new ModuleEFG(eventGroup);
        moduleEFG.populate();

        Olog.log.info("==this is moduleEFG==");
        Olog.log.info(moduleEFG.toString());


        ModuleDDG moduleDDg = new ModuleDDG(eventGroup, sddg);
        moduleDDg.populate();
        Olog.log.info("==this is moduleDDG==");
        Olog.log.info(moduleDDg.toString());
        Olog.log.info("disconnected sub graphs: " + moduleDDg.getDisconnectedSubGraphs().toString());

        DPOR<Module> dpor = new DPOR<Module>(moduleEFG, moduleDDg, eventGroup.getModules());
        dpor.MAX_LENGTH = dporMaxLength;
        //  dpor.generateFirst();
        // dpor.generate(new ArrayList<Module>(), 0);
        dpor.generate(dpor.stacks, 0);
        Olog.log.info(dpor.results.toString());
        Olog.log.info("generated test cases size: " + dpor.results.size());
    }
}
