package edu.wmich.cs.carot.util;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oliver on 04/09/16.
 */
public class CTEvents2Testcase {
    public static void writeTo(List<List<CTEvent>> eventSequences, String folder) {
        for (List<CTEvent> es : eventSequences) {
            TestCase tc = new TestCase();
            List<StepType> steps = new ArrayList<>();
            String name = "t";
            for (int i = 0; i < es.size(); i++) {
                CTEvent event = es.get(i);
                StepType step = new StepType();
                step.setEventId(event.getIdentifier());
                //step.setReachingStep(true);
                steps.add(step);
                name += "_" + event.getIdentifier();
            }
            name += ".tst";
            tc.setStep(steps);
            //      Olog.log.info(tc.toString());
//            File dir = new File(folder);
//            assert dir.isDirectory();
//            dir.mkdir();  //maintain dir in shell scripts
            new XMLHandler().writeObjToFile(tc, folder+"/" + name);
        }
    }
}
