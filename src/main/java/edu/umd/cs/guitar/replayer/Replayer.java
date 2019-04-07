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
package edu.umd.cs.guitar.replayer;

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
 * AbstractMain replayer class, monitoring the replayer's behaviors
 * 
 * Note on exception handling:
 * 
 * GUITAR related exceptions MUST be derived from GExceptions. All non
 * GException exceptions are to be considered as AUT exceptions (unless
 * explicitly stated and handled, in an itemised manner).
 * 
 * All "caught" exceptions MUST be propagated upwards unless expplicitly
 * itemised.
 * 
 * <p>
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
public class Replayer {
	/**
	 * SECTION: DATA
	 * 
	 * This section contains member variables and accessor functions.
	 */

	/**
	 * TestASMShouldInstrument case data
	 */
	 TestCase tc;
	String sGUIFfile;
	String sEFGFfile;

	// TestASMShouldInstrument Monitor
	GReplayerMonitor monitor;
	List<GTestMonitor> lTestMonitor = new ArrayList<GTestMonitor>();

	// Log
	Logger log = GUITARLog.log;

	// Secondary input
	GUIStructureWrapper guiStructureAdapter;
	EFG efg;
	Document docGUI;

	/**
	 * @param tc
	 * @param sGUIFile
	 * @param sEFGFile
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public Replayer(TestCase tc, String sGUIFile, String sEFGFile)
			throws ParserConfigurationException, SAXException, IOException {
		super();
		this.tc = tc;
		this.sGUIFfile = sGUIFile;
		this.sEFGFfile = sEFGFile;

		// Initialize GUI object
		GUIStructure gui = (GUIStructure) IO.readObjFromFile(sGUIFile,
				GUIStructure.class);
		guiStructureAdapter = new GUIStructureWrapper(gui);

		// Initialize EFG object
		this.efg = (EFG) IO.readObjFromFile(sEFGFile, EFG.class);

		// Initialize EFG XML file
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		builder = domFactory.newDocumentBuilder();
		docGUI = builder.parse(sGUIFile);

		// Initialize to null / disabled
		sDataPath = null;
		useImage = false;
	}

	public Replayer(String testcaseFile, String GUIFile, String EFGFile)
			throws ParserConfigurationException, SAXException, IOException {
		this((TestCase) IO.readObjFromFile(testcaseFile, TestCase.class),
				GUIFile, EFGFile);
	}

	/**
	 * Time out for the replayer TODO: Move to a monitor
	 */
	private int TIME_OUT = 0;

	/**
	 * @param nTimeOut
	 *            The nTimeOut to set
	 */
	public void setTimeOut(int nTimeOut) {
		this.TIME_OUT = nTimeOut;
	}

	/**
	 * Path for storing artifacts
	 */
	String sDataPath;

	/**
	 * Set the path where the replayer can find artifacts.
	 * 
	 * @param sDataPath
	 *            Name of path where the replayer finds artifacts
	 */
	public void setDataPath(String sDataPath) {
		this.sDataPath = sDataPath;
	}

	/**
	 * Use image based identification when possible.
	 */
	boolean useImage = false;

	/**
	 * useImage accessor
	 */
	public void setUseImage() {
		this.useImage = true;
	}

	/**
	 * SECTION: LOGIC
	 * 
	 * This section contains the core logic for replaying a GUITAR testcase.
	 */

	/**
	 * Parse and run test case.
	 * 
	 * @throws GException
	 * 
	 */
	public void execute() throws GException, Exception {
		log.info("------ BEGIN TESTCASE -----");

		try {
			monitor.setUp();

			log.info("Connecting to application");
			monitor.connectToApplication();
			log.info("Application is connected.");

			// Monitor before the test case
			for (GTestMonitor testMonitor : lTestMonitor) {
				GUITARLog.log.info("TestASMShouldInstrument monitor: "
						+ testMonitor.getClass().getCanonicalName()
						+ " is initialized");
				testMonitor.init();
			}

			log.info("Testcase size " + tc.getStep().size() + " steps");

			List<StepType> lSteps = tc.getStep();
			int nStep = lSteps.size();

			for (int i = 0; i < nStep; i++) {
				log.info("------- BEGIN STEP --------");
				StepType step = lSteps.get(i);
				executeStep(step);
				log.info("-------- END STEP ---------");
			}
			// Monitor after the test case
			for (GTestMonitor testMonitor : lTestMonitor) {
				testMonitor.term();
			}
			monitor.cleanUp();

		} catch (GException e) {
			for (GTestMonitor testMonitor : lTestMonitor) {
				testMonitor.exceptionHandler(e);
			}

			// Propagate error upwards

			log.info("-------- END STEP ---------");
			log.info("------- END TESTCASE ------");
			throw e;

		} catch (Exception e) {
			// Propagate error upwards

			log.info("-------- END STEP ---------");
			log.info("------- END TESTCASE ------");
			throw e;

		}

		log.info("------- END TESTCASE ------");
	}

	/**
	 * Execute a single step in the test case
	 * 
	 * <p>
	 * 
	 * TODO: Rewrite the test monitor
	 * 
	 * @param step
	 * @throws ComponentNotFound
	 * @throws ReplayerStateException
	 */
	void
	executeStep(StepType step)
	throws ComponentNotFound,
			 ReplayerStateException, Exception
	{
		GTestStepEventArgs stepStartArgs = new TestStepStartEventArgs(step);

		// -----------------------
		// Monitor before step
		for (GTestMonitor aTestMonitor : lTestMonitor) {
			aTestMonitor.beforeStep(stepStartArgs);
		}

		// Events
		String sEventID = step.getEventId();
		GUITARLog.log.info("Executing Step EventID = " + sEventID);

		// Get widget ID and actions
		String sWidgetID = null;
		String sAction = null;

		List<EventType> lEvents = efg.getEvents().getEvent();

		for (EventType event : lEvents) {
			String eventID = event.getEventId();
			if (sEventID.equals(eventID)) {
				sWidgetID = event.getWidgetId();
				sAction = event.getAction();
			}
		}

		// Locate step event in EFG
		if (sWidgetID == null) {
		   // Skip event, if not found. Helps skip pseudo widgets.
			GUITARLog.log.error("Step Event ID = " + sEventID
					+ ". Not found in EFG. Skipping event");
         return;
		} else if (sAction == null) {
			GUITARLog.log.error("Step Event ID = " + sEventID
					+ ". Action not found in EFG.");
			GUITARLog.log.error("Action not found");
			throw new ReplayerStateException();
		}

		// Lookup window for widget/event
		String sWindowTitle = getWindowName(sWidgetID);
		if (sWindowTitle == null) {
			GUITARLog.log.error("Step Event ID = " + sEventID
					+ ". Unable to locate window for widget");
			throw new ReplayerStateException();
		}

		// Wait for window to appear
		GUITARLog.log.info("Waiting for window:");
		GUITARLog.log.info(" + Window Title = " + sWindowTitle);
		GUITARLog.log.info(" + Widget ID    = " + sWidgetID);

		// Wait for expected window to appear
		GWindow gWindow = replayerWaitForWindow(sWindowTitle);

		if (gWindow == null) {
			GUITARLog.log.error("Expected window did not appear");
			throw new ComponentNotFound();
		}

		GUITARLog.log.info("FOUND window");
		GUITARLog.log.info(" + Window Title = " + gWindow.getTitle());
		GUITARLog.log.info("");

		ComponentTypeWrapper comp = guiStructureAdapter
				.getComponentFromID(sWidgetID);

		if (comp == null) {
			GUITARLog.log
					.error("Component not found in GUI state.");
			throw new ReplayerStateException();
		}

		GUITARLog.log.info("Searching for widget:");
		GUITARLog.log.info(" + Widget ID = " + sWidgetID);

		/*
		 * Once the window has been identified, search the window (gWindow)
		 * for the widget to click (sWidgetID).
		 */
		GComponent gComponent =
		   replayerSearchComponent(gWindow, sWidgetID, comp);

		// Matching widget was not found
		if (gComponent == null) {
		   // Skip event, if not found. Helps skip pseudo widgets.
			GUITARLog.log.error("gComponent == null. "
					+ "Skipping event " + sWidgetID);
         return;
		}

		// Matching widget was found
		GUITARLog.log.info("FOUND widget");
		GUITARLog.log.info(" + Widget Title = " + gComponent.getTitle());
		if (!gComponent.isEnable()) {
			GUITARLog.log.error(gComponent.getTitle() + " is disabled. "
					+ "ComponentDisabled exception.");
			throw new ComponentDisabled();
		}

		// Execute action on matchd widget
		GEvent gEvent = monitor.getAction(sAction);
		List<String> parameters = step.getParameter();

		GUITARLog.log.info(" + Action: *" + sAction);
		GUITARLog.log.info("");

		// Optional data
		AttributesType optional = comp.getDComponentType().getOptional();
		Hashtable<String, List<String>> optionalValues = null;

		if (optional != null) {

			optionalValues = new Hashtable<String, List<String>>();
			for (PropertyType property : optional.getProperty()) {
				optionalValues.put(property.getName(), property.getValue());
			}
		}

		if (parameters == null) {
			gEvent.perform(gComponent, optionalValues);
		} else if (parameters.size() == 0) {
			gEvent.perform(gComponent, optionalValues);
		} else {
			gEvent.perform(gComponent, parameters, optionalValues);
		}

		// -----------------------
		// Monitor after step
		if (!lTestMonitor.isEmpty()) {
			try {
				GTestStepEventArgs stepEndArgs = new TestStepEndEventArgs(step,
						gComponent.extractProperties(),
						gWindow.extractGUIProperties());
				for (GTestMonitor aTestMonitor : lTestMonitor) {
					aTestMonitor.afterStep(stepEndArgs);
				}
			} catch (Exception e) {
				log.error("Failed to collect post-event state", e);

				// Propagate error upwards
				throw e;
			}
		}
	}

	/**
	 * Get container window corresponding to a given widget ID.
	 * This function looks up the GUI structure to extract the
	 * window title of the window containing the widget.
	 *
	 * @param  sWidgetID Widget ID for which container window is required
	 *
	 * @return String    Window title of window containing sWidgetID
	 */
	String
	getWindowName(String sWidgetID)
	{
		String sWindowName = null;

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr;
		Object result;
		NodeList nodes;

		try {
			String xpathExpression = "/GUIStructure/GUI[Container//Property[Name=\""
					+ GUITARConstants.ID_TAG_NAME
					+ "\" and Value=\""
					+ sWidgetID
					+ "\"]]/Window/Attributes/Property[Name=\""
					+ GUITARConstants.TITLE_TAG_NAME + "\"]/Value/text()";
			expr = xpath.compile(xpathExpression);
			result = expr.evaluate(docGUI, XPathConstants.NODESET);
			nodes = (NodeList) result;

			if (nodes.getLength() > 0) {
				sWindowName = nodes.item(0).getNodeValue();
			}

		} catch (XPathExpressionException e) {
			/*
			 * Not propagating. Return value is set to NULL instead. Caller must
			 * check.
			 */
			GUITARLog.log.error(e);
		}

		return sWindowName;
	}

	/**
	 * Get the replayer monitor
	 * 
	 * @return the replayer monitor
	 */
	public GReplayerMonitor getMonitor() {
		return monitor;
	}

	/**
	 * @param monitor
	 *            the replayer monitor to set
	 */
	public void setMonitor(GReplayerMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * 
	 * Add a test monitor
	 * 
	 * <p>
	 * 
	 * @param aTestMonitor
	 */
	public void addTestMonitor(GTestMonitor aTestMonitor) {
		aTestMonitor.setReplayer(this);
		this.lTestMonitor.add(aTestMonitor);
	}

	/**
	 * Remove a test monitor
	 * 
	 * <p>
	 * 
	 * @param mTestMonitor
	 */
	public void removeTestMonitor(GTestMonitor mTestMonitor) {
		this.lTestMonitor.remove(mTestMonitor);
	}

	/**
	 * Wait for a window to appear. This is a blocking call.
	 *
	 * Two methods of waiting for the new window are used.
	 *    *  useImage - use image based identification 
	 *    * !useImage - use "window title" based identification
	 * 
	 * @param sWindowTitle Window title of window to wait for
	 */
	protected GWindow
	replayerWaitForWindow(String sWindowTitle)
	throws IOException
	{
		/*
		 * This is a blocking call. Waits until window appears. Uses a regex
		 * based match if specified in the command line.
		 */
		GWindow gWindowByImage = null;
		GWindow gWindowByTitle = null;

		if (useImage) {
		   String strUUID = null;

			// Find GUI Window image for comparing
			GUITypeWrapper guiTypeWrapperParent = guiStructureAdapter
					.getGUIByTitle(sWindowTitle);
			PropertyType propertyType =
			   (guiTypeWrapperParent != null) ? guiTypeWrapperParent
					.getWindowProperty(GUITARConstants.UUID_TAG_NAME) : null;
			if (guiTypeWrapperParent != null) {
				strUUID = propertyType.getValue().get(0);

			   gWindowByImage = monitor.waitForWindow(sWindowTitle,
			                   sDataPath + "/"
				 			    	 + strUUID + ".png");
			}

			// Write ripped and replay image pair to log
			if (gWindowByImage != null) {
				monitor.writeMatchedComponents(gWindowByImage.getContainer(),
			   	                            sDataPath + File.separatorChar + 
			      	                         strUUID + ".png");
			}
		}

		// Always determine using the title based method
		gWindowByTitle = monitor.getWindow(sWindowTitle);

		// Determine if the two methods differed
		if (gWindowByTitle != null &&
			 gWindowByImage != null) {
			if (gWindowByTitle.hashCode() != gWindowByImage.hashCode()) {
				GUITARLog.log.info("Window identification mismatch");
				GUITARLog.log.info("Image based " + gWindowByImage.getTitle() +
				                   "Title based " + gWindowByTitle.getTitle());
			}
		}
		if (useImage && gWindowByTitle != null && gWindowByImage == null) {
			GUITARLog.log.info("Window identification mismatch");
			GUITARLog.log.info("Image based is null. " +
			                   "Title based " + gWindowByTitle.getTitle());
		}
		if (useImage && gWindowByTitle == null && gWindowByImage != null) {
			GUITARLog.log.info("Window identification mismatch");
			GUITARLog.log.info("Image based " + gWindowByImage.getTitle() +
			                   "Title based is null");
		}

		return useImage ? gWindowByImage : gWindowByTitle;
	}

	/**
	 * Locate the component corresponding to sWidgetID inside gwindow.
	 *
	 * @param gWindow   Window in which to look for component
	 * @param sWidgetID Widget ID of widget to look for
	 * @param componentTypeWrapper Component corresponding to sWidgetID
	 *
	 * @returns GComponent of located window 
	 */
	GComponent
	replayerSearchComponent(GWindow gWindow,
									String sWidgetID,
									ComponentTypeWrapper componentTypeWrapper)
	throws IOException
	{
		GComponent container = gWindow.getContainer();

		GComponent gComponentByImage = null;
		GComponent gComponentByProp  = null;

		if (useImage) {
		   String strUUID = null;

			// Find GUI Window image for comparing
			ComponentTypeWrapper compTypeWrapper = guiStructureAdapter
					.getComponentFromID(sWidgetID);
			if (compTypeWrapper != null) {
				strUUID =
				   (compTypeWrapper != null) ? compTypeWrapper
					.getFirstValueByName(GUITARConstants.UUID_TAG_NAME) : null;
					
					// baonn says: Because we decided not to put the image feature directly in
					// the core, I commented it out so the Replayer is still compatible with other
					// components 
//
//				try {
// 					gComponentByImage =
//					   container.searchComponentByImage(sDataPath +
//						                                 "/" + strUUID + ".png");
//				} catch (IOException e) {
//					throw new ReplayerStateException();
//				}
			}

			// Write ripped and replay image pair to log
			if (gComponentByImage != null) {
				monitor.writeMatchedComponents(gComponentByImage,
			   	                            sDataPath + File.separatorChar + 
			      	                         strUUID + ".png");
			}
		}

		// Always perform property based identification
		List<PropertyType> ID =
		   monitor.selectIDProperties(componentTypeWrapper
			.getDComponentType());
		List<PropertyTypeWrapper> IDAdapter =
			new ArrayList<PropertyTypeWrapper>();
		for (PropertyType p : ID) {
			IDAdapter.add(new PropertyTypeWrapper(p));
		}
		gComponentByProp = container.getFirstChild(IDAdapter);

		// Determine if the two methods were differed
		if (gComponentByProp != null &&
			 gComponentByImage != null) {
			if (gComponentByProp.hashCode() != gComponentByImage.hashCode()) {
				GUITARLog.log.info("Component identification mismatch");
				GUITARLog.log.info("Image based " + gComponentByImage.getTitle() +
				                   " Prop based " + gComponentByProp.getTitle());
			}
		}
		if (useImage && gComponentByProp != null && gComponentByImage == null) {
			GUITARLog.log.info("Component identification mismatch");
			GUITARLog.log.info("Image based is null. " +
			                   " Prop based " + gComponentByProp.getTitle());
		}
		if (useImage && gComponentByProp == null && gComponentByImage != null) {
			GUITARLog.log.info("Component identification mismatch");
			GUITARLog.log.info("Image based " + gComponentByImage.getTitle() +
			                   " Prop based is null");
		}

		return useImage ? gComponentByImage : gComponentByProp;
	}

} // End of class
