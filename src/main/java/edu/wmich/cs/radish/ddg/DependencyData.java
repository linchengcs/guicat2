package edu.wmich.cs.radish.ddg;

import java.util.*;

/**
 * Created by oliver on 17/09/16.
 */
public class DependencyData {
    public static Map<String, Set<DataMarker>> funDefMap = new HashMap<>();
    public static Map<String, Set<DataMarker>> funUseMap = new HashMap<>();
    public static Map<String, Set<DataMarker>> eventDefMap = new HashMap<>();
    public static Map<String, Set<DataMarker>> eventUseMap = new HashMap<>();
    public static List<FunAct> funActs = new ArrayList<>();
    public static String currentFun = null;

    public static void addFunDef(String fun, DataMarker dataMarker) {
        if (!funDefMap.containsKey(fun)) {
            Set<DataMarker> dataSet = new HashSet<DataMarker>();
            funDefMap.put(fun, dataSet);
        }
        Set<DataMarker> oldDataSet = funDefMap.get(fun);
        oldDataSet.add(dataMarker);
        if (isRecording()) {
            addEventDef(fun, dataMarker);
        }
    }

    public static void addFunUse(String fun, DataMarker dataMarker) {
        if (!funUseMap.containsKey(fun)) {
            Set<DataMarker> dataSet = new HashSet<DataMarker>();
            funUseMap.put(fun, dataSet);
        }
        Set<DataMarker> oldDataSet = funUseMap.get(fun);
        oldDataSet.add(dataMarker);
        if (isRecording()) {
            addEventUse(fun, dataMarker);
        }
    }

    public static void markFunStart(String s){
        funActs.add(new FunAct(s, FunAct.ENTER));
    }

    public static void markFunEnd(String s){
        funActs.add(new FunAct(s, FunAct.EXIT));
    }

    public static void markEventStart(String s) {
        assert s != null && !s.isEmpty();
        currentFun = s;
    }

    public static void markEventEnd(String s) {
        currentFun = null;
    }

    public static void addEventDef(String fun, DataMarker dataMarker) {
        if (!eventDefMap.containsKey(fun)) {
            eventDefMap.put(fun, new HashSet<>());
        }
        eventDefMap.get(fun).add(dataMarker);
    }

    public static void addEventUse(String fun, DataMarker dataMarker) {
        if (!eventUseMap.containsKey(fun)) {
            eventUseMap.put(fun, new HashSet<>());
        }
        eventUseMap.get(fun).add(dataMarker);
    }

    public static boolean isRecording() {
        return currentFun != null;
    }

}
