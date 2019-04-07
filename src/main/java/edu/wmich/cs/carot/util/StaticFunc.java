package edu.wmich.cs.carot.util;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.*;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

import java.util.*;

/**
 * Created by oliver on 09/08/16.
 */
public class StaticFunc {
    public static <K1, K2> String MapSet (Map<K1, Set<K2>> map) {
        String ret = "\r\n";
        for (Map.Entry<K1, Set<K2>> entry : map.entrySet()) {
            ret += "key :\r\n";
            //    ret += "    " + entry.getKey().toString() + "\r\n" + "value: " + entry.getValue().size() + "\r\n";
            ret += "    " + ((CTEvent)entry.getKey()).getIdentifier() + "\r\n" + "value: " + entry.getValue().size() + "\r\n";
            for (K2 value : entry.getValue()) {
                ret +=  "    " + value.toString() + "\r\n";
            }
            ret += "\r\n";
        }
        return ret;
    }

    public static void writeTestCase(List<CTEvent> testcase, String dir) {
        TestCase tc = new TestCase();
        List<StepType> steps = new ArrayList<>();
        String name = "t";
        for (int i = 0; i < testcase.size(); i++) {
            CTEvent event = testcase.get(i);
            StepType step = new StepType();
            step.setEventId(event.getIdentifier());
            //step.setReachingStep(true);
            steps.add(step);
            name += "_" + event.getIdentifier();
        }
        name += ".tst";
        tc.setStep(steps);
        //      Olog.log.info(tc.toString());
        IO.writeObjToFile(tc, dir+"/" + name);
    }

    public static String printEFG(EFG efg){
        String ret = "\n,";
        for (EventType et : efg.getEvents().getEvent()) {
            ret += et.getEventId() + ",";
        }
        List<RowType> eg = efg.getEventGraph().getRow();
        for (int i = 0; i < eg.size(); i++) {
            ret += "\n" + efg.getEvents().getEvent().get(i).getEventId() + ",";
            RowType row = eg.get(i);
            List<Integer> re = row.getE();
            for (int j = 0; j < re.size(); j++) {
                ret += re.get(j) +",";
            }
        }
        return ret;
    }

    public static String getEventWidgetId(CTEvent event) {
        CTEfgEvent event1 = (CTEfgEvent) event;
        String widgetId = ((EventType) event1.getObject()).getWidgetId();
        return widgetId;
    }

    public static <E> E randFromList(List<E> list) {
        Random randomizer = new Random();
        return list.get(randomizer.nextInt(list.size()));
      //  return list.get(0);
      //  return list.get(list.size()-1);
    }

    public static <E> Set<E> intersection(Set<E> s1, Set<E> s2) {
        Set<E> intersection = new HashSet<E>(s1);
        intersection.retainAll(s2);
        return intersection;
    }

    public static boolean checkExistsCommonField(Set<String> sf, Set<String> ss) {
        for (String firstValue : sf ) {
            for (String secondValue : ss) {
                String[] tmpFirst = firstValue.split("<|:\\s|\\s|>");
                String[] tmpSecond = secondValue.split("<|:\\s|\\s|>");
                if (tmpFirst[1].equals(tmpSecond[1]) && tmpFirst[2].equals(tmpSecond[2]) && tmpFirst[3].equals(tmpSecond[3]))
                    return true;
            }
        }

        return false;
    }
}
