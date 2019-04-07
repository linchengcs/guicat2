package edu.wmich.cs.radish.sequence;

import edu.umd.cs.guitar.model.data.*;
import edu.umd.cs.guitar.replayer.JFCReplayerMain;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.radish.ddg.*;

import java.util.Set;

/**
 * Created by oliver on 16/09/16.
 */
public class Runner {
    public EFG efg;
    public GUIStructure gui;
    public String autMain;
    public Set<Set<EventType>> eventsSet;

    public Runner(EFG efg, GUIStructure gui, String autMain) {
        this.efg = efg;
        this.gui = gui;
        this.autMain = autMain;
    }

    public static void main(String[] args) {


        String[] args_barad = new String[] {
                "./aut/barad-ticket.EFG",
                "./aut/barad-ticket.jar",
                "./aut/barad-ticket.GUI",
                "edu.wmich.cs.aut.barad.BaradTicket"
        };

  //      args = args_barad;

//        XMLHandler xml = new XMLHandler();
//        EFG efg = (EFG) xml.readObjFromFile(args[0], EFG.class);
//        GUIStructure gui = (GUIStructure) xml.readObjFromFile(args[2], GUIStructure.class);
//
//        Runner runner = new Runner(efg, gui, args[3]);

//        JFCReplayerMain.main(new String[] {
//                "-c" + " edu.wmich.cs.aut.barad.BaradTicket" +
//                " -g" + " log/barad/barad.GUI" +
//                " -e" + " log/barad/barad.EFG" +
//                " -t" + " log/barad/guitar/testcases/t_e1078110046_e1886351932.tst" +
//                " -i" + " 100" +
//                " -d" + " 50" +
//                " -l" + " log/barad/guitar/logs/t_e1078110046_e1886351932.log" +
//                " -gs" + " log/barad/guitar/states/t_e1078110046_e1886351932.sta" +
//                " -cf" + " ./conf/barad/configuration.xml" +
//                " -ts"});

        edu.wmich.cs.radish.ddg.DependencyAgent.logger.info("what the fuck");

        JFCReplayerMain.main(args);

        Olog.log.info(DependencyData.funDefMap.toString());
        Olog.log.info(DependencyData.funUseMap.toString());
        Olog.log.info("olog");
    }
}
