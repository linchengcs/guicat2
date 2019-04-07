/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.testcase.plugin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventGraphType;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.testcase.TestCaseGeneratorConfiguration;
import edu.wmich.cs.carot.util.Olog;

/**
 * 
 * Common interface for all Test case generator plugins
 * 
 * <p>
 * 
 * @author Bao Nguyen
 * 
 */
public abstract class GTestCaseGeneratorPlugin {

   /**
    * 
    */
   final String TEST_NAME_PREFIX = "t";

   /**
    * 
    */
   final String TEST_NAME_SUFIX = ".tst";

   ObjectFactory factory = new ObjectFactory();
   List<EventType> initialEvents = null;

   /**
    * Map of <event, all events used to reveal it> Note that we don't store al
    * predecessor events here
    */
   Hashtable<EventType, Vector<EventType>> preds;

   /**
    * Map of <event, all successor events>
    */
   Hashtable<EventType, Vector<EventType>> succs;
   EFG efg;
   boolean noDuplicateEvent;
   boolean treatTerminalEventSpecially;
   LinkedList<EventType> terminalEvents;

   public EFG efgx;

   /**
    * Overridable function for retrieving additional plugin-specific arguments.
    * <p>
    * 
    * @return TestCaseGeneratorConfiguration object with required arguments
    */
   public TestCaseGeneratorConfiguration getConfiguration() {
      TestCaseGeneratorConfiguration configuration = new TestCaseGeneratorConfiguration();

      return configuration;
   }

   /**
    * Check arguments for each plugin
    * 
    * <p>
    * 
    * @return
    */
   abstract public boolean isValidArgs();

   /**
    * Generate test cases
    * 
    * <p>
    * 
    * @param efg
    * @param outputDir
    * @param nMaxNumber
    * @param noDuplicateEvent
    * @param treatTerminalEventSpecially
    */
   abstract public void generate(EFG efg,
                                 String outputDir,
                                 int nMaxNumber,
                                 boolean noDuplicateEvent,
                                 boolean treatTerminalEventSpecially);

   /**
    * Preparing for test case generation
    */
   void initialize() {
      Olog.log.info("Parsing follow relations");
      parseFollowRelations();

      Olog.log.info("Getting initial events");
      parseInitialEvents();
   }

   /**
    * Get initial events
    */
   private void
   parseInitialEvents()
   {
      initialEvents = new ArrayList<EventType>();
      List<EventType> eventList = efg.getEvents().getEvent();

      for (EventType event : eventList) {
         /*
          * if (event.isInitial() && preds.get(event) == null) {
          * initialEvents.add(event); }
          */
         if (event.isInitial()) {
            initialEvents.add(event);
         }

      }
      Olog.log.info("Initial event count: " + initialEvents.size());
   }

   /**
    * Get follow relations
    */
   private void
   parseFollowRelations()
   {
      int added = 0;
      List<EventType> eventList = efg.getEvents().getEvent();
      int eventGraphSize = eventList.size();
      EventGraphType eventGraph = efg.getEventGraph();

      succs = new Hashtable<EventType, Vector<EventType>>();
      preds = new Hashtable<EventType, Vector<EventType>>();

      for (int row = 0; row < eventGraphSize; row++) {
         EventType currentEvent = eventList.get(row);
         Vector<EventType> s = new Vector<EventType>();

         for (int col = 0; col < eventGraphSize; col++) {
            int relation = eventGraph.getRow().get(row).getE().get(col);

            // Other is followed by current event: current -> other
            if (relation != GUITARConstants.NO_EDGE) {
               EventType otherEvent = eventList.get(col);

               s.add(otherEvent);

               if (relation == GUITARConstants.REACHING_EDGE
                     && !otherEvent.getEventId().equals(
                           currentEvent.getEventId())) {

                  // Create preds list
                  Vector<EventType> p = preds.get(otherEvent);
                  if (p == null) {
                     p = new Vector<EventType>();
                  }

                  p.add(currentEvent);
                  preds.put(otherEvent, p);
                                                added++;
               } // if
            } // if

            succs.put(currentEvent, s);
         } // for
      } // for

      Olog.log.info("Added " + added + " edges");
   }

   Map<EventType, List<EventType>> visitedEdges;


   /**
    * 
    * Write the event sequence to file
    * 
    * <p>
    * 
    * @param tCName
    * @param path
    */
   void writeToFile(String tCName, LinkedList<EventType> path)
   {
      TestCase tc = factory.createTestCase();
      List<StepType> lStep = new ArrayList<StepType>();

      for (EventType e : path) {
         StepType step = factory.createStepType();
         step.setEventId(e.getEventId());
         lStep.add(step);
      }

      tc.setStep(lStep);

      Olog.log.info("Writting to " + tCName);
      IO.writeObjToFile(tc, tCName);
   }

   /**
    * 
    * Check if given event already appears in an event list.
    * 
    * <p>
    * 
    * @param event
    * @param eventList
    */
   boolean isDuplicateEvent(EventType event,
                            LinkedList<EventType> eventList)
   {
      for (EventType existingEvent : eventList) {
         if (event.getEventId().equals(existingEvent.getEventId()))
            return true;
      }

      return false;
   }

   /**
    * 
    * Check if given event is TERMINAL event.
    * 
    * <p>
    * 
    * @param event
    */
   boolean isTerminalEvent(EventType event)
   {
      String type = event.getType();

      if (type.equals(GUITARConstants.TERMINAL)) {
         return true;
      }

      return false;
   }

} // End of class
