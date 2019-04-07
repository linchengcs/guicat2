/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland.
 * Names of owners of this group may be obtained by sending an e-mail to
 * atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.umd.cs.guitar.replayer2;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.umd.cs.guitar.model.data.WidgetMapElementType;

import edu.umd.cs.guitar.model.wrapper.GUIMapWrapper;

import edu.umd.cs.guitar.model.GApplication;

import edu.umd.cs.guitar.model.data.GUIMap;

import edu.umd.cs.guitar.replayer.GReplayerMonitor;

import edu.umd.cs.guitar.replayer.Replayer;

import edu.umd.cs.guitar.exception.ReplayerConstructionException;

import edu.umd.cs.guitar.model.data.GUIType;

import edu.umd.cs.guitar.model.data.ComponentType;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.exception.ComponentDisabled;
import edu.umd.cs.guitar.exception.ComponentNotFound;
import edu.umd.cs.guitar.exception.ReplayerStateException;
import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.data.StepType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;
import edu.umd.cs.guitar.model.wrapper.PropertyTypeWrapper;
import edu.umd.cs.guitar.replayer.monitor.GTestMonitor;
import edu.umd.cs.guitar.replayer.monitor.GTestStepEventArgs;
import edu.umd.cs.guitar.replayer.monitor.TestStepEndEventArgs;
import edu.umd.cs.guitar.replayer.monitor.TestStepStartEventArgs;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 *
 *
 * <p>
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class Replayer2 {

  GReplayerMonitor2 monitor;
  GApplication application;

  Logger LOGGER = Logger.getLogger(Replayer2.class);

  /**
   * @param monitor
   * @param application
   */
  public Replayer2(GReplayerMonitor2 monitor, GApplication application) {
    super();
    this.monitor = monitor;
    this.application = application;
  }


  /**
   * Execute a single step in the test case
   *
   * <p>
   *
   * @param step
   * @param map
   * @throws ComponentNotFound
   * @throws ReplayerStateException
   */
  void executeStep(StepType step, GUIMap map) throws ComponentNotFound, ReplayerStateException {
    // Compatibility check

    // Events
    String eventID = step.getEventId();
    LOGGER.info("Executing Step EventID = " + eventID);
    if (map == null) {
      GUITARLog.log.error("Lookup table not found.");
      throw new ReplayerStateException();
    }

    GUIMapWrapper mapWrapper = new GUIMapWrapper(map);

    // Get widget ID and actions

    EventType event = mapWrapper.getEventByID(eventID);
    String widgetId = event.getWidgetId();

    GUITARLog.log.info("Searching for widget:");
    GUITARLog.log.info(" + Widget ID:  " + widgetId);

    WidgetMapElementType widgetElement = mapWrapper.getWidgetByID(widgetId);
    if (widgetElement == null) {
      GUITARLog.log.error("Component not found in the lookup table.");
      throw new ComponentNotFound();
    }

    ComponentType component = widgetElement.getComponent();
    ComponentType window = widgetElement.getWindow();

    GComponent gComponent = monitor.getComponent(application, window, component);
    

    // // Matching widget was not found
    if (gComponent == null) {
      // Bail out with exception
      LOGGER.error("Component NOT found on the GUI.");
      throw new ComponentNotFound();
    }

    // Matching widget was found
    LOGGER.info("FOUND widget");
    LOGGER.info(" + Widget Title = " + gComponent.getTitle());
    if (!gComponent.isEnable()) {
      LOGGER.error(
          gComponent.getTitle() + " is disabled.");
      throw new ComponentDisabled();
    }
    
    // Execute action on matchd widget
    String action = event.getAction();    
    if(action == null){
      LOGGER.info("Error in getting action named ");
      throw new ComponentNotFound();
    }
    GEvent gEvent = getAction(action);
    List<String> parameters = step.getParameter();
    LOGGER.info(" + Action: " + action);
    

    // Optional data
    AttributesType optional = component.getOptional();
    Hashtable<String, List<String>> optionalValues = new Hashtable<String, List<String>>();;

    if (optional != null) {
      for (PropertyType property : optional.getProperty()) {
        optionalValues.put(property.getName(), property.getValue());
      }
    }

    if (parameters == null || parameters.size() == 0) {
      gEvent.perform(gComponent, optionalValues);
    } else {
      gEvent.perform(gComponent, parameters, optionalValues);
    }

    // // -----------------------
    // // Monitor after step
    // if (!lTestMonitor.isEmpty()) {
    // try {
    // GTestStepEventArgs stepEndArgs = new TestStepEndEventArgs(step, null,
    // null);
    // for (GTestMonitor aTestMonitor : lTestMonitor) {
    // aTestMonitor.afterStep(stepEndArgs);
    // }
    // } catch (Exception e) {
    // log.error("Failed to collect post-event state", e);
    // }
    // }

  }


  /**
   * Create an action (event) using its class name
   *
   * @param actionName
   * @return
   */
  private GEvent getAction(String actionName) {
    GEvent retAction = null;

    try {
      Class<?> c = Class.forName(actionName);
      Object action = c.newInstance();

      retAction = (GEvent) action;

    } catch (Exception e) {
      GUITARLog.log.error("Error in getting action named " + actionName, e);
    }

    return retAction;
  }

  /**
   * SECTION: LOGIC
   *
   *  This section contains the core logic for replaying a GUITAR testcase.
   * Parse and run test case.
   *
   * @throws GException
   *
   */
  public void execute(TestCase testcase, GUIMap map) throws GException, Exception {
    LOGGER.info("------ BEGIN TESTCASE -----");

    try {
      LOGGER.info("Connecting to application");
      application.connect();
      LOGGER.info("Application is connected.");

      // Monitor before the test case
      // for (GTestMonitor monitor : lTestMonitor) {
      // GUITARLog.log.info("TestASMShouldInstrument monitor: "
      // + this.monitor.getClass().getCanonicalName()
      // + " is initialized");
      // monitor.init();
      // }

      LOGGER.info("Testcase size " + testcase.getStep().size() + " steps");

      List<StepType> lSteps = testcase.getStep();
      int nStep = lSteps.size();

      for (int i = 0; i < nStep; i++) {
        LOGGER.info("------- BEGIN STEP --------");
        StepType step = lSteps.get(i);
        executeStep(step, map);
        LOGGER.info("-------- END STEP ---------");
      }
      // Monitor after the test case
      // for (GTestMonitor monitor : lTestMonitor) {
      // monitor.term();
      // }
      // monitor.cleanUp();

    } catch (GException e) {
      // for (GTestMonitor monitor : lTestMonitor) {
      // monitor.exceptionHandler(e);
      // }

      // Propagate error upwards

      LOGGER.info("-------- END STEP ---------");
      LOGGER.info("------- END TESTCASE ------");
      throw e;

    } catch (Exception e) {
      // Propagate error upwards

      LOGGER.info("-------- END STEP ---------");
      LOGGER.info("------- END TESTCASE ------");
      throw e;

    }

    LOGGER.info("------- END TESTCASE ------");
  }

} // End of class
