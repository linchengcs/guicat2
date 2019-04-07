/*	
 *  Copyright (c) 2011. The GREYBOX group at the University of Freiburg, Chair of Software Engineering.
 *  Names of owners of this group may be obtained by sending an e-mail to arlt@informatik.uni-freiburg.de
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */

package edu.umd.cs.guitar.testcase.plugin.ct;

import java.util.*;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTDef;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTMethod;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTUse;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.util.FileIO;
import edu.umd.cs.guitar.testcase.plugin.ct.util.Log;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Label;
import edu.wmich.cs.graph.Node;
import soot.SootField;
import soot.jimple.FieldRef;
import soot.toolkits.scalar.FlowSet;

/**
 * @author arlt
 */
public class CTSlicer {

    /**
     * Body Transformer
     */
    protected CTBodyTransformer bodyTransformer;

    /**
     * Events
     */
    protected List<CTEvent> events;

    /**
     * Event field defs
     */
    private Map<CTEvent, Set<CTDef>> eventFieldDefs = new HashMap<CTEvent, Set<CTDef>>();

    /**
     * Event field uses
     */
    private Map<CTEvent, Set<CTUse>> eventFieldUses = new HashMap<CTEvent, Set<CTUse>>();

    /**
     * Slice
     */
    private Map<CTDef, Set<CTUse>> slice = new HashMap<CTDef, Set<CTUse>>();

    /**
     * Visited Methods
     */
    private Set<String> slicedMethods = new HashSet<String>();

    private List<FlowSet> eventSlicerFlowset;


    /**
     * C-tor
     *
     * @param bodyTransformer
     *            Body Transformer
     * @param events
     *            Events
     */
    public CTSlicer(CTBodyTransformer bodyTransformer, List<CTEvent> events) {
        this.bodyTransformer = bodyTransformer;
        this.events = events;
    }


    /**
     * Runs the slicer
     */
    public void run() {
        // iterate events
        for (CTEvent event : events) {
            //		Log.info("Slicing event " + event.getIdentifier());

            Set<CTDef> eventDefs = new HashSet<CTDef>();
            Set<CTUse> eventUses = new HashSet<CTUse>();

            eventSlicerFlowset = new ArrayList<>();

            queryEventFieldDefs(event.getListener(), new HashSet<String>(),
                    eventDefs);
            queryEventFieldUses(event.getListener(), new HashSet<String>(),
                    eventUses);

            eventFieldDefs.put(event, eventDefs);
            eventFieldUses.put(event, eventUses);
            querySlice(event);
        }

        // addTextEventDefs();
        //		Olog.log.info("eventFieldDefs " + MyPrint.MapSet(eventFieldDefs));
        //		Olog.log.info("eventFieldUses " + MyPrint.MapSet(eventFieldUses));
        //		Olog.log.info("CTSlicer.slice: " + slice.toString());
        //		Olog.log.info("CTSlicer.slicedMethods " + slicedMethods.toString());
    }

    //need a bind between slice pkg and eventunitmap
    public void addLeafEventDefs(Map<String, String> eventUnitMap) {
        for (Map.Entry<String, String> entry : eventUnitMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            CTDef ctDef = new CTDef();
            ctDef.setValue(value);
            ctDef.setFieldValue(value);
            Olog.log.info(key + " " + value);
            CTEvent event = findCTEventById(key);
            eventFieldDefs.get(event).add(ctDef);
        }
    }

    private CTEvent findCTEventById(String id) {
        for (CTEvent event : events) {
            if (event.getIdentifier().equals(id))
                return event;
        }
        return null;
    }

    public  void addTextEventDefs() {
        if (bodyTransformer.getPackage().equals("ticket"))
            addTicketTextEventDefs();
        if (bodyTransformer.getPackage().equals("barad"))
            addBaradTextEventDefs();
        if (bodyTransformer.getPackage().equals("workout"))
            addWorkoutTextEventDefs();
    }

    public void addTicketTextEventDefs() {
        for (CTEvent event : events) {
            CTDef ctDef = new CTDef();
            String id = event.getIdentifier();
            String field = "";
            if ("e1269095122".equals(id)) {
                field = "<examples.ticket.Ticket: javax.swing.JTextField nameInput>";
            }
            if ("e3379743782".equals(id)) {
                field = "<examples.ticket.Ticket: javax.swing.JTextField ageInput>";
            }
            if (!field.isEmpty()) {
                ctDef.setFieldValue(field);
                ctDef.setValue(field);
                eventFieldDefs.get(event).add(ctDef);
            }
        }
    }

    public void addBaradTextEventDefs () {
        for (CTEvent event : events) {
            CTDef ctDef = new CTDef();
            String id = event.getIdentifier();
            String field = "";
            if ("e1887239368".equals(id)) {
                field = "<examples.ticket.BaradTicket: javax.swing.JTextField nameInput>";

            }
            if ("e1877567236".equals(id)) {
                field = "<examples.ticket.BaradTicket: javax.swing.JTextField idInput>";
            }
            if ("e1886351932".equals(id)) {
                field = "<examples.ticket.BaradTicket: javax.swing.JTextField fromInput>";
            }
            if ("e1877569020".equals(id)) {
                field = "<examples.ticket.BaradTicket: javax.swing.JTextField toInput>";
            }
            if (!field.isEmpty()) {
                ctDef.setFieldValue(field);
                ctDef.setValue(field);
                eventFieldDefs.get(event).add(ctDef);
            }
        }
    }

    public void addWorkoutTextEventDefs () {
        for (CTEvent event : events) {
            CTDef ctDef = new CTDef();
            String id = event.getIdentifier();
            String field = "";
            if ("e1867326300".equals(id)) {
                field = "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JComboBox metabolismCombo>";

            }
            if ("e1533213504".equals(id)) {
                field = "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JComboBox genderCombo>";

            }
            if ("e647517916".equals(id)) {
                field = "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JComboBox experienceCombo>";

            }
            if ("e2736112220".equals(id)) {
                field = "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JTextField ageTextField>";

            }
            if ("e2777134200".equals(id)) {
                field = "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JTextField heightTextField>";

            }
            if ("e199916088".equals(id)) {
                field = "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JTextField weightTextField>";

            }
            if (!field.isEmpty()) {
                ctDef.setFieldValue(field);
                ctDef.setValue(field);
                eventFieldDefs.get(event).add(ctDef);
            }
        }
    }


    /**
     * Queries all field defs in the event
     *
     * @param methodName
     *            Method name
     * @param methods
     *            Methods
     * @param eventDefs
     *            Event defs
     */
    protected void queryEventFieldDefs(String methodName, Set<String> methods,
                                       Set<CTDef> eventDefs) {
        if (methods.contains(methodName))
            return; // method was already analyzed
        else
            methods.add(methodName);



        CTMethod method = bodyTransformer.getMethod(methodName);
        if (null == method)
            return; // method was not analyzed

        if (methodName.contains("getText")) {
            //	Olog.log.info("++++++querying getText: " + method.getSignature());
        }

        Set<CTDef> methodDefs = bodyTransformer.getMethodFieldDefs(method);
        if (null != methodDefs) {
            // add method defs
            for (CTDef methodDef : methodDefs) {
                eventDefs.add(methodDef);
            }
        }

        // analyze invokes
        for (CTUse invoke : method.getInvokes()) {
            queryEventFieldDefs(invoke.getMethodSignature(), methods, eventDefs);
        }
    }

    /**
     * Queries all field uses in the event
     *
     * @param methodName
     *            Method name
     * @param methods
     *            Methods
     * @param eventUses
     *            Event uses
     */
    protected void queryEventFieldUses(String methodName, Set<String> methods,
                                       Set<CTUse> eventUses) {
        if (methods.contains(methodName))
            return; // method was already analyzed
        else
            methods.add(methodName);

        CTMethod method = bodyTransformer.getMethod(methodName);
        if (null == method)
            return; // method was not analyzed

        Set<CTUse> methodUses = bodyTransformer.getMethodFieldUses(method);
        if (null != methodUses) {
            // add method uses
            for (CTUse methodUse : methodUses) {
                if (!isDefined(methodUse))
                     eventUses.add(methodUse);
            }
        }

        if (bodyTransformer.getMethodFlowSet().get(method) != null)
            eventSlicerFlowset.add(bodyTransformer.getMethodFlowSet().get(method));

        // analyze invokes
        for (CTUse invoke : method.getInvokes()) {
            queryEventFieldUses(invoke.getMethodSignature(), methods, eventUses);
        }
    }

    private boolean isDefined(CTUse use) {
        String fuse = use.getFieldValue();
        boolean ret = false;
        for (FlowSet flowSet : eventSlicerFlowset) {
            Iterator fsit = flowSet.iterator();
            while (fsit.hasNext()) {
                SootField sf = (SootField) fsit.next();
                if (sf.toString().equals(fuse))
                    ret = true;
            }
        }


        return  false;
    }

    /**
     * Slices the field defs in the listener
     *
     * @param event
     *            Event
     */
    protected void querySlice(CTEvent event) {
        Set<CTDef> eventDefs = eventFieldDefs.get(event);
        if (null == eventDefs)
            return;

        // query uses for defs
        for (CTDef eventDef : eventDefs) {
            Set<CTUse> eventDefSlice;
            if (slice.containsKey(eventDef)) {
                eventDefSlice = slice.get(eventDef);
            } else {
                eventDefSlice = new HashSet<CTUse>();
                slice.put(eventDef, eventDefSlice);
            }
            queryUses(eventDef, eventDefSlice, event);
            slicedMethods.clear();
        }
    }

    /**
     * Queries for uses
     *
     * @param def
     *            Def
     * @param eventDefSlice
     *            EventDefSlice
     * @param event
     *            Event
     */
    protected void queryUses(CTDef def, Set<CTUse> eventDefSlice, CTEvent event) {
        Set<CTUse> uses = bodyTransformer.getUnitUses(def.getUnit());
        if (null == uses)
            return;

        for (CTUse use : uses) {
            // ignore use if it is already analyzed
            if (eventDefSlice.contains(use))
                continue;

            // does use call a method?
            if (null != use.getMethodSignature()) {
                String methodSignature = use.getMethodSignature();
                if (slicedMethods.contains(methodSignature))
                    continue;

                slicedMethods.add(methodSignature);
                CTMethod method = bodyTransformer.getMethod(methodSignature);
                if (null != method) {
                    Set<CTUse> returnUses = bodyTransformer
                            .getMethodUses(method);
                    if (null != returnUses) {
                        for (CTUse returnUse : returnUses) {
                            if (!returnUse.isReturnStmt())
                                continue;

                            // query def for return use
                            queryDefs(returnUse, returnUse.getUnit()
                                    .getMethod(), eventDefSlice, event);
                        }
                    }
                }
            }

            // query def for use
            if (!queryDefs(use, use.getUnit().getMethod(), eventDefSlice, event)) {
                if (!use.isFieldValue())
                    continue;

                // analyze invoked methods
                for (CTMethod method : getInvokes(use)) {
                    queryDefs(use, method, eventDefSlice, event);
                }

                // analyze invokedBy methods
                for (CTMethod method : getInvokedBys(use, event)) {
                    queryDefs(use, method, eventDefSlice, event);
                }
            }
        }
    }

    /**
     * Queries for defs
     *
     * @param use
     *            Use
     * @param method
     *            Method
     * @param eventDefSlice
     *            EventDefSlice
     * @param event
     *            Event
     */
    protected boolean queryDefs(CTUse use, CTMethod method,
                                Set<CTUse> eventDefSlice, CTEvent event) {
        // add use to slice
        eventDefSlice.add(use);

        // get defs in unit
        Set<CTDef> defs = bodyTransformer.getMethodDefs(method);
        if (null == defs)
            return false;

        boolean success = false;
        for (CTDef def : defs) {
            // ignore def if it is declared after the current use
            // (works only within the same method)
            if (def.getUnit().getMethod() == use.getUnit().getMethod())
                if (def.getUnit().getID() > use.getUnit().getID())
                    continue;

            String defValue = def.getValue();
            String useValue = use.getValue();

            // compare field values?
            if (def.isFieldValue() && use.isFieldValue()) {
                defValue = def.getFieldValue();
                useValue = use.getFieldValue();
            }

            // ignore def if it does not equal use
            if (!defValue.equals(useValue))
                continue;

            // query use(s) for def
            success = true;
            queryUses(def, eventDefSlice, event);
        }

        return success;
    }

    /**
     * Returns the set of methods invoked by the method of the given use
     *
     * @param use
     *            Use
     * @return Methods
     */
    public List<CTMethod> getInvokes(CTUse use) {
        List<CTMethod> methods = new LinkedList<CTMethod>();
        getInvokes(use, methods);
        return methods;
    }

    /**
     * Returns the set of methods invoked by the method of the given use
     *
     * @param use
     *            Use
     * @param methods
     *            Methods
     */
    protected void getInvokes(CTUse use, List<CTMethod> methods) {
        List<CTUse> invokes = use.getUnit().getMethod().getInvokes();
        for (CTUse invoke : invokes) {
            CTMethod invokedMethod = bodyTransformer.getMethod(invoke
                    .getMethodSignature());
            if (methods.contains(invokedMethod))
                continue;

            methods.add(invokedMethod);
            getInvokes(invoke, methods);
        }
    }

    /**
     * Returns the set of methods which invoke the method of the given use
     *
     * @param use
     *            Use
     * @param event
     *            Event
     * @return Methods
     */
    public List<CTMethod> getInvokedBys(CTUse use, CTEvent event) {
        List<CTMethod> methods = new LinkedList<CTMethod>();
        getInvokedBys(use, event, new LinkedList<CTMethod>(), methods);
        return methods;
    }

    /**
     * Returns the set of methods which invoke the method of the given use
     *
     * @param use
     *            Use
     * @param event
     *            Event
     * @param tmpMethods
     *            Temporary methods
     * @param methods
     *            Methods
     * @return Methods
     */
    protected void getInvokedBys(CTUse use, CTEvent event,
                                 List<CTMethod> tmpMethods, List<CTMethod> methods) {
        // does method match with event signature?
        CTMethod method = use.getUnit().getMethod();
        if (method.getSignature().equals(event.getListener())) {
            for (CTMethod tmpMethod : tmpMethods) {
                if (!methods.contains(tmpMethod))
                    methods.add(tmpMethod);
            }
            return;
        }

        Set<CTUse> invokedBys = method.getInvokedBy();
        for (CTUse invokedBy : invokedBys) {
            // method already visited?
            CTMethod invokedByMethod = invokedBy.getUnit().getMethod();
            if (tmpMethods.contains(invokedByMethod))
                continue;

            // create new list of temporary methods
            List<CTMethod> newMethods = new LinkedList<CTMethod>(tmpMethods);
            newMethods.add(invokedByMethod);
            getInvokedBys(invokedBy, event, newMethods, methods);
        }
    }

    /**
     * Returns the field defs for an event
     *
     * @param event
     *            Event
     * @return Field defs
     */
    public Set<String> getEventFieldDefs(CTEvent event) {
        Set<CTDef> defs = eventFieldDefs.containsKey(event) ? eventFieldDefs
                .get(event) : null;
        if (null == defs)
            return null;

        // iterate defs
        Set<String> uniqueDefs = new HashSet<String>();
        for (CTDef def : defs) {
            uniqueDefs.add(def.getFieldValue());
        }
        return uniqueDefs;
    }

    /**
     * Returns the field uses for an event
     *
     * @param event
     *            Event
     * @return Field uses
     */
    public Set<String> getEventFieldUses(CTEvent event) {
        Set<CTUse> uses = eventFieldUses.containsKey(event) ? eventFieldUses
                .get(event) : null;
        if (null == uses)
            return null;

        // iterate uses
        Set<String> uniqueUses = new HashSet<String>();
        for (CTUse use : uses) {
            uniqueUses.add(use.getFieldValue());
        }
        return uniqueUses;
    }

    /**
     * Returns the set of common fields of event1 and event2
     *
     * @param event1
     *            Event1
     * @param event2
     *            Event2
     * @return Set of common fields
     */
    public Set<String> getCommonFields(CTEvent event1, CTEvent event2) {
        Set<String> intersection = new HashSet<String>();
        Set<String> defs = getEventFieldDefs(event1);
        Set<String> uses = getEventFieldUses(event2);

        if (null == defs || null == uses)
            return intersection;

        // compute intersection of defs and uses
        intersection.addAll(defs);
        intersection.retainAll(uses);
        return intersection;
    }

    /**
     * Computes a sub-slice for the given event and field value
     *
     * @param event
     *            Event
     * @param fieldValue
     *            Field value
     * @return Sub-Slice
     */
    public Map<CTDef, Set<CTUse>> getSubSlice(CTEvent event, String fieldValue) {
        Map<CTDef, Set<CTUse>> subSlice = new HashMap<CTDef, Set<CTUse>>();

        Set<CTDef> defs = eventFieldDefs.get(event);
        for (CTDef def : defs) {
            if (!def.getFieldValue().equals(fieldValue))
                continue;

            Set<CTUse> uses = slice.get(def);
            subSlice.put(def, uses);
        }
        return subSlice;
    }

    /**
     * Returns the events
     *
     * @return Events
     */
    public List<CTEvent> getEvents() {
        return events;
    }

    /**
     * Prints the slice
     */
    public void printSlice() {
        StringBuilder sb = new StringBuilder();
        for (CTDef def : slice.keySet()) {
            sb.append(String.format("# field: %s%n", def.getFieldValue()));
            for (CTUse use : slice.get(def)) {
                sb.append(String.format("# -->: %s%n", use.getValue()));
            }
        }
        Log.info(sb.toString());
    }

    /**
     * Prints statistics of the slicer
     */
    public void printStatistics() {
        Log.info("*** Statistics of Slicer ***");
        StringBuilder sb = new StringBuilder();
        for (CTDef def : slice.keySet()) {
            Set<CTUse> uses = slice.get(def);
            sb.append(String.format("%d%n", uses.size()));
        }
        FileIO.toFile(sb.toString(), "/tmp/slice.txt");
    }

    public boolean haveDependingFields(CTEvent event1, CTEvent event2) {
        Set<?> common = getCommonFields(event1, event2);
        Boolean bool = common.isEmpty();
        if (!bool) {
            //if (event2.getIdentifier().equals("e1385016382") && event1.getIdentifier().equals("e3956994952")) {
            Olog.log.info("++++++++++ common fields ++++++++++" + event1.getIdentifier() + " and " + event2.getIdentifier());
            //	Olog.log.info(getEventFieldDefs(event1).toString());
            //	Olog.log.info(getEventFieldUses(event2).toString());
            Olog.log.info(common.toString());
            //	}
        }
        return !bool;
    }

    public String generateDependencyMatrix() {
        return generateDependencyMatrix(true);
    }

    public String generateDependencyMatrix(boolean removeAll ) {
        //		Olog.log.info("printing dependency matrix:");
        String mat = "";
        int flag = 0;
        for (CTEvent row : events) {
            String mrow = "";
            for(CTEvent col : events) {
                Set<String> common = getCommonFields(row, col);
                Set<String> reverse = getCommonFields(col, row);
                if (removeAll) {
                    common.removeAll(reverse);
                }

                flag = common.isEmpty() ? 0 : 1;
                mrow += flag + ", ";
            }
            mrow = mrow.substring(0, mrow.length()-2);
            mat += mrow + "\r\n";
        }
        mat = mat.substring(0, mat.length()-2);
        //		Olog.log.info("\r\n" + mat +"\r\n");
        return mat;
    }

    /*
    @removeAll true use the set difference; false not change the original ddg
     */
    public DiGraph generateDiGraph(boolean removeAll) {
        DiGraph<CTEvent> graph = new DiGraph<CTEvent>();
        graph.addNodesFromEvents(events);
        for (Node<CTEvent> row : graph.getNodes()) {
            for (Node<CTEvent> col : graph.getNodes()) {
                CTEvent rowe = (CTEvent) row.getElement();
                CTEvent cole = (CTEvent) col.getElement();
                Set<String> common = getCommonFields(rowe, cole);
                Set<String> reverse = getCommonFields(cole, rowe);
                if (removeAll)
                    common.removeAll(reverse);
                if (!common.isEmpty()) {
                    Label label = new Label<String>(common);
                    //  Edge edge = new Edge(row, col, new Label<String>(common));
                    graph.addEdge(row, col, label);
                }
            }
        }
        return graph;
    }

    public DiGraph generateDiGraph() {
        return generateDiGraph(true);
    }

    public String getPackage() {
        return bodyTransformer.getPackage();
    }

    public Map<CTEvent, Set<CTDef>> getEventFieldDefs() {
        return eventFieldDefs;
    }

    public Map<CTEvent, Set<CTUse>> getEventFieldUses() {
        return eventFieldUses;
    }
}
