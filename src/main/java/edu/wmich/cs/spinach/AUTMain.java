package edu.wmich.cs.spinach;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.FileAppender;
import com.mysql.jdbc.log.Log;
import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.radish.ddg.StaticDDG;
import edu.wmich.cs.radish.sequence.IDDG;
import edu.wmich.cs.radish.sequence.IEFG;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Lin Cheng on 12/21/16.
 */
public class AUTMain extends AbstractMain<CTEvent>{

    IEFG<CTEvent> efg;

    public AUTMain(){}




    public static void main(String[] args) {
//        String autMain = "edu.wmich.cs.aut.barad.BaradTicket";
//        String guiFile = "./log/barad/barad.GUI";
//        String efgFile = "./log/barad/barad.EFG";
//        String sootScope = "./aut/barad-ticket.jar";
//        String sootPackage = "barad";
//        int dporMaxLength = 3;

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

        if (args.length == 0) {
//            args = new String[] {
//                    "./log/barad/barad.GUI",
//                    "./log/barad/barad.EFG",
//                    "./aut/barad-ticket.jar",
//                    "sleepset+reduction",
//                    "9",
//                    "./log/barad/testcase"};

//            args = new String[] {
//                    "./log/crosswordsage/crosswordsage.GUI",
//                    "./log/crosswordsage/crosswordsage.EFG",
//                    "./aut/CrosswordSage.jar",
//                    "sleepset+reduction",
//                    "3",
//                    "./log/crosswordsage/testcase"};


//            args = new String[] {
//                    "./log/arlt/arlt.GUI",
//                    "./log/arlt/arlt.EFG",
//                    "./aut/arlt.jar",
//                    "sleepset+reduction",
//                    "2",
//                    "./log/arlt/testcase"};

            args = new String[] {
                    "./log/arlt14/arlt14.GUI",
                    "./log/arlt14/arlt14.EFG",
                    "./aut/arlt14.jar",
                    "sleepset+reduction",
                    "3",
                    "./log/arlt14/testcase"};

//            args = new String[] {
//                    "./log/freemind/freemind.GUI",
//                    "./log/freemind/freemind.EFG",
//                    "./aut/freemind.jar",
//                    "sleepset+reduction",
//                    "2",
//                    "./log/freemind/testcase"};

//            args = new String[] {
//                    "./log/jgp/jgp.GUI",
//                    "./log/jgp/jgp.EFG",
//                    "./aut/jgp.jar",
//                    "sleepset",
//                    "3",
//                    "./log/jgp/testcase"};

//            args = new String[] {
//                    "./log/ce/ce.GUI",
//                    "./log/ce/ce.EFG",
//                    "./aut/ce.jar",
//                    "sleepset+reduction",
//                    "2",
//                    "./log/ce/testcase"};

//            args = new String[] {
//                    "./log/payment/payment.GUI",
//                    "./log/payment/payment.EFG",
//                    "./aut/payment.jar",
//                    "sleepset+reduction",
//                    "4",
//                    "./log/payment/testcase"};

//                        args = new String[] {
//                    "./log/workout/workout.GUI",
//                    "./log/workout/workout.EFG",
//                    "./aut/workout.jar",
//                    "sleepset+reduction",
//                    "3",
//                    "./log/workout/testcase"};


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

        IBacktrackUpdator backtrackUpdator;


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

        XMLHandler xml = new XMLHandler();
        EFG efg = (EFG) xml.readObjFromFile(efgFile, EFG.class);
        IEFG iefg = new AutEFG(efg);

        GUIStructure gui = (GUIStructure) xml.readObjFromFile(guiFile, GUIStructure.class);

        StaticDDG sddg = new StaticDDG(gui, efg, sootScope, 1, sootPackage);
        //    dddg.setAutMain(autMain);
        sddg.populate();
    Olog.log.info(sddg.getDdg().toString());
        Olog.log.info(sddg.getSlicer().getEventFieldDefs().toString());
        Olog.log.info(sddg.getSlicer().getEventFieldUses().toString());
        IDDG ddg = sddg;

//        Set<String> r = sddg.eventRead(sddg.getEventById("e3873108694"));    //e220300476 e3873108694
//        Set<String> w = sddg.eventWrite(sddg.getEventById("e3873108694")); //e3873108694  e3873108632
//        Olog.log.info(r.toString());
//        Olog.log.info(w.toString());
//        r.retainAll(w);
//        Olog.log.info(r.toString());




        int bound = len + 1;
        IEventSequenceHandler eventSequenceHandler = new TestCaseEventSequenceHandler(dir);
      //  IEventSequenceHandler eventSequenceHandler = new LogEventSequenceHandler();
        AUTMain testAUT= new AUTMain();
        testAUT.setEfg(iefg);
        testAUT.setDdg(ddg);
        testAUT.setBacktrackUpdator(backtrackUpdator);
        testAUT.setDFS_MAX_LENGTH(bound);
        testAUT.setEventSequenceHandler(eventSequenceHandler);
        testAUT.run();

    }
}
