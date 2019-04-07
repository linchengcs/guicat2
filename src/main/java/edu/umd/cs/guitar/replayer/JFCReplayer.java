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

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kohsuke.args4j.CmdLineException;

import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.model.GIDGenerator;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.JFCConstants;
import edu.umd.cs.guitar.model.JFCDefaultIDGeneratorSimple;
import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.AttributesType;
import edu.umd.cs.guitar.model.data.ComponentListType;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.Configuration;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.data.TestCase;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.replayer.monitor.CoberturaCoverageMonitor;
import edu.umd.cs.guitar.replayer.monitor.GTestMonitor;
import edu.umd.cs.guitar.replayer.monitor.JFCTerminationMonitor;
import edu.umd.cs.guitar.replayer.monitor.PauseMonitor;
import edu.umd.cs.guitar.replayer.monitor.StateMonitor;
import edu.umd.cs.guitar.replayer.monitor.TimeMonitor;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>

 * Note on exception handling:
 *
 * GUITAR related exceptions MUST be derived from GExceptions.
 *  All non GException exceptions are to be considered as AUT
 *  exceptions (unless explicitly stated and handled, in an
 *  itemised manner).
 *
 * All "caught" exceptions MUST be propagated upwards.
 *
 */
public class JFCReplayer
{
   JFCReplayerConfiguration CONFIG;

   public void
   execute()
   throws Exception, GException, FileNotFoundException
   {
      checkArgs();
      setupEnv();

//      System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY,
//            JFCReplayerConfiguration.LOG_FILE);
      
      GUITARLog.addFileAppender(JFCReplayerConfiguration.LOG_FILE);
      TestCase tc = (TestCase) (IO.readObjFromFile(
            JFCReplayerConfiguration.TESTCASE, TestCase.class));

      Replayer replayer;

      try {
         if (tc == null) {
            GUITARLog.log.error("TestASMShouldInstrument case not found");
            throw new FileNotFoundException("Testcase file not found");
         }

         replayer = new Replayer(tc,
                                 JFCReplayerConfiguration.GUI_FILE,
                                 JFCReplayerConfiguration.EFG_FILE);
         JFCReplayerMonitor jMonitor =
            new JFCReplayerMonitor(JFCReplayerConfiguration.MAIN_CLASS);

         // Add a GUI state record monitor
         GTestMonitor stateMonitor = new StateMonitor(
               JFCReplayerConfiguration.GUI_STATE_FILE,
               JFCReplayerConfiguration.DELAY);

         // Modified to get a simpler ID generator
         GIDGenerator idGenerator = JFCDefaultIDGeneratorSimple.getInstance();

         ((StateMonitor) stateMonitor).setIdGenerator(idGenerator);

         replayer.addTestMonitor(stateMonitor);

         // Add a pause monitor and ignore time out monitor if needed
         if (JFCReplayerConfiguration.PAUSE) {
            GTestMonitor pauseMonitor = new PauseMonitor();
            replayer.addTestMonitor(pauseMonitor);
         } else {
            // Add a timeout monitor
            GTestMonitor timeoutMonitor = new TimeMonitor(
                  JFCReplayerConfiguration.TESTSTEP_TIMEOUT,
                  JFCReplayerConfiguration.TESTCASE_TIMEOUT);
            replayer.addTestMonitor(timeoutMonitor);
         }

         // Add a Cobertura code coverage collector
         boolean isMeasureCoverage =
            (JFCReplayerConfiguration.COVERAGE_DIR != null &&
             JFCReplayerConfiguration.COVERAGE_CLEAN_FILE != null);

         if (isMeasureCoverage) {
            GTestMonitor coverageMonitor = new CoberturaCoverageMonitor(
                  JFCReplayerConfiguration.COVERAGE_CLEAN_FILE,
                  JFCReplayerConfiguration.COVERAGE_DIR);
            replayer.addTestMonitor(coverageMonitor);
         }

         // Add Terminal monitor
         if (JFCReplayerConfiguration.TERMINAL_SEARCH) {
            GTestMonitor terminalMonitor = new JFCTerminationMonitor();
            Map<Integer, String> mTerminalLabels =
               getMTerminalLabels(JFCReplayerConfiguration.CONFIG_FILE);

            ((JFCTerminationMonitor) terminalMonitor)
                  .setmTerminalLabels(mTerminalLabels);
            ((JFCTerminationMonitor) terminalMonitor)
                  .setDelay(JFCReplayerConfiguration.DELAY);
            replayer.addTestMonitor(terminalMonitor);
         }

	      /**
	       * Set additional GUI artifact data path for reading and
			 * writing.
	       */
	      if (CONFIG.USE_IMAGE) {
				/*
				 * Set path for reading artifacts. Currently this is created
				 * only for the "-m" case. It can be used for other cases too.
				 */
   	      String sDataPath = JFCReplayerConfiguration.GUI_FILE
      	                        + "." + "data/";
         	replayer.setDataPath(sDataPath);

   	      File file = new File(sDataPath);
      	   if (!file.exists()) {
					throw new FileNotFoundException("Image directory not found" +
                                               " but -m specified");
				}

	         // Setup ripper to images if specified
         	replayer.setUseImage();

				/*
				 * Set path for logging artifacts. Currently this is created
				 * only for the "-m" case. It can be used for other cases too.
				 */
				String sLogPath = JFCReplayerConfiguration.LOG_FILE +
				                     "." + "data/";
				file = new File(sLogPath);
				file.mkdir();

				jMonitor.setLogPath(sLogPath);
      	}

         // Set up string comparator
         jMonitor.setUseReg(JFCReplayerConfiguration.USE_REG);

			// Set replayer monitors
         replayer.setMonitor(jMonitor);
         replayer.setTimeOut(JFCReplayerConfiguration.TESTCASE_TIMEOUT);


  	      replayer.execute();

      } catch (GException e) {
     	   throw e;

     	} catch (IOException e) {
        	GUITARLog.log.error("Unable to create GUI data path");
  	      throw e;

		} catch (Exception e) {
			throw e;
      }
   }

   /**
    * Check for command-line arguments
    * 
    * @throws CmdLineException
    * 
    */
   private void checkArgs()
   throws CmdLineException
	{
      // Check argument
      if (GReplayerConfiguration.HELP) {
         throw new CmdLineException("");
      }

      boolean isPrintUsage = false;

      if (JFCReplayerConfiguration.MAIN_CLASS == null) {
         System.err.println("missing '-c' argument");
         isPrintUsage = true;
      }

      if (JFCReplayerConfiguration.GUI_FILE == null) {
         System.err.println("missing '-g' argument");
         isPrintUsage = true;
      }

      if (JFCReplayerConfiguration.EFG_FILE == null) {
         System.err.println("missing '-e' argument");
         isPrintUsage = true;
      }

      if (JFCReplayerConfiguration.TESTCASE == null) {
         System.err.println("missing '-t' argument");
         isPrintUsage = true;
      }

      boolean isNotMeasureCoverage =
         (JFCReplayerConfiguration.COVERAGE_DIR == null)
          && (JFCReplayerConfiguration.COVERAGE_CLEAN_FILE == null);

      boolean isMeasureCoverage =
         (JFCReplayerConfiguration.COVERAGE_DIR != null)
          && (JFCReplayerConfiguration.COVERAGE_CLEAN_FILE != null);

      if (!isMeasureCoverage && !isNotMeasureCoverage) {
         System.err
               .println("'-cd,-cc' should be either all set or all unset");
         isPrintUsage = true;
      }

      if (isPrintUsage) {
         throw new CmdLineException("");
      }
   }

   /**
    * @param configuration
    */
   public
   JFCReplayer(JFCReplayerConfiguration configuration) {
      super();
      this.CONFIG = configuration;
   }

	/**
    * 
    */
   private void
   setupEnv()
   {
      // --------------------------
      // Terminal list
      // Try to find absolute path first then relative path

      Configuration conf;
      
      XMLHandler xmlHandler = new XMLHandler();

      conf = (Configuration) IO.readObjFromFile(
            JFCReplayerConfiguration.CONFIG_FILE, Configuration.class);

      if (conf == null) {
         InputStream in = getClass().getClassLoader().getResourceAsStream(
               JFCReplayerConfiguration.CONFIG_FILE);
         conf = (Configuration) IO.readObjFromFile(in, Configuration.class);
      }

      List<FullComponentType> cTerminalList = conf.getTerminalComponents()
            .getFullComponent();

      for (FullComponentType cTermWidget : cTerminalList) {
         ComponentType component = cTermWidget.getComponent();
         AttributesType attributes = component.getAttributes();

         if (attributes != null) {
            JFCConstants.sTerminalWidgetSignature
                  .add(new AttributesTypeWrapper(component
                        .getAttributes()));
        }
      }

      List<FullComponentType> lIgnoredComps =
         new ArrayList<FullComponentType>();
      List<String> ignoredWindow = new ArrayList<String>();

      ComponentListType ignoredAll = conf.getIgnoredComponents();

      if (ignoredAll != null) {
         for (FullComponentType fullComp : ignoredAll.getFullComponent()) {
            ComponentType comp = fullComp.getComponent();

            // TODO: Shortcut here
            if (comp == null) {
               ComponentType win = fullComp.getWindow();
               ComponentTypeWrapper winAdapter = new ComponentTypeWrapper(
                     win);
               String ID = winAdapter
                     .getFirstValueByName(GUITARConstants.ID_TAG_NAME);

               if (ID != null) {
                  JFCConstants.sIgnoredWins.add(ID);
               }

            } else {
               lIgnoredComps.add(fullComp);
            }
         }
		}

   }
	

   private static Map<Integer, String>
   getMTerminalLabels(String configFile)
   {
      Configuration conf;

      conf = (Configuration) IO.readObjFromFile(configFile,
            Configuration.class);

      List<FullComponentType> cTerminalList = conf.getTerminalComponents()
            .getFullComponent();

      Map<Integer, String> mTerminalLabels = new TreeMap<Integer, String>();
      int i = 0;

      for (FullComponentType cTermWidget : cTerminalList) {
         ComponentType component = cTermWidget.getComponent();
         ComponentTypeWrapper wComp = new ComponentTypeWrapper(component);
         mTerminalLabels.put(i, wComp
               .getFirstValueByName(GUITARConstants.TITLE_TAG_NAME));
         i++;

      }

      return mTerminalLabels;
   }

} // End of class
