package edu.wmich.cs.radish.sequence;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.radish.ddg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by oliver on 17/09/16.
 */
public class LogTest {
    static Logger log = LoggerFactory.getLogger(edu.wmich.cs.radish.ddg.DependencyAgent.class);
    public int variable;


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

}
