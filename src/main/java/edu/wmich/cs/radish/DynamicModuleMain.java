package edu.wmich.cs.radish;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.radish.ddg.DependencyData;
import edu.wmich.cs.radish.ddg.DynamicDDG;
import edu.wmich.cs.radish.module.EventGroup;
import edu.wmich.cs.radish.module.Module;
import edu.wmich.cs.radish.module.ModuleDDG;
import edu.wmich.cs.radish.module.ModuleEFG;
import edu.wmich.cs.radish.sequence.DPOR;

/**
 * Created by rick on 12/12/16.
 */
public class DynamicModuleMain {

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
        Olog.log.info("data events" + eventGroup.getDataEvents().toString());
        Olog.log.info("login events" + eventGroup.getLogicEvents().toString());
        Olog.log.info("control events" + eventGroup.getControlEvents().toString());
        Olog.log.info("data event sets by container" + eventGroup.getDataEventSetsByContainer().toString());
        Olog.log.info("data event sets by listener" + eventGroup.getDataEventSetsByListener().toString());

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

        DPOR<Module> dpor = new DPOR<Module>(moduleEFG, moduleDDg, eventGroup.getModules());
        //  dpor.generateFirst();
        // dpor.generate(new ArrayList<Module>(), 0);
        dpor.generate(dpor.stacks, 0);
    }

}
