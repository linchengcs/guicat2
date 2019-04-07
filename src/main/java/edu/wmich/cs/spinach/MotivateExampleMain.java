package edu.wmich.cs.spinach;

import edu.wmich.cs.aut.arlt12.DDGClass;
import edu.wmich.cs.aut.arlt12.EFGClass;
import edu.wmich.cs.aut.arlt14.DDGArlt14;
import edu.wmich.cs.aut.arlt14.EFGArlt14;
import edu.wmich.cs.radish.sequence.IDDG;
import edu.wmich.cs.radish.sequence.IEFG;


/**
 * Created by Lin Cheng on 12/22/16.
 */
public class MotivateExampleMain extends AbstractMain{
    public MotivateExampleMain() {
        super();
    }


    public static void main(String[] args) {
        MotivateExampleMain motivateExampleMain = new MotivateExampleMain();

//        IEFG<String> efg = new EFGClass();
//        IDDG<String> ddg = new DDGClass();

        IEFG<String> efg = new EFGArlt14();
        IDDG<String> ddg = new DDGArlt14();


        //  IBacktrackUpdator backtrackUpdator= new BaselineBacktrackUpdator();
     //  IBacktrackUpdator backtrackUpdator= new DPORBacktrackUpdator();
     //   IBacktrackUpdator backtrackUpdator = new SleepSetBacktrackUpdator();
    //   IBacktrackUpdator backtrackUpdator = new SleepSetWithIdleBacktrackUpdator();
        IBacktrackUpdator backtrackUpdator = new SleepSetWithReductionUpdator();
        int bound = 3;

        IEventSequenceHandler eventSequenceHandler = new LogEventSequenceHandler();
        motivateExampleMain.setEfg(efg);
        motivateExampleMain.setDdg(ddg);
        motivateExampleMain.setBacktrackUpdator(backtrackUpdator);
        motivateExampleMain.setDFS_MAX_LENGTH(bound);
        motivateExampleMain.setEventSequenceHandler(eventSequenceHandler);
        motivateExampleMain.run();
    }
}
