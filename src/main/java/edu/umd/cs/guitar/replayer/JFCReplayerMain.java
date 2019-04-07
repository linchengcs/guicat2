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

import java.awt.*;
import java.io.FileNotFoundException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Entry class for JFCReplayer
 *
 * Note on exception handling:
 *
 * GUITAR related exceptions MUST be derived from GExceptions.
 *  All non GException exceptions are to be considered as AUT
 *  exceptions (unless explicitly stated and handled, in an
 *  itemised manner).
 *
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 */
public class JFCReplayerMain {
   public static final int SUCCESS_EXIT = 0;

   public static final int FILE_NOT_FOUND_EXCEPTION_EXIT = -3;
   public static final int GUITAR_EXCEPTION_EXIT         = -2;
   public static final int UNCAUCHT_EXCEPTION_EXIT       = -1;

   static long nStartTime;
   static long nEndTime;

   /**
    * @param args
    */
   public static void
   main(String[] args)
   {
      GUITARLog.log.info("------- BEGIN REPLAY -------");

      int retVal = SUCCESS_EXIT;

      nStartTime = System.currentTimeMillis();

      /**
       * Note: We don't actually use this configuration object. We only use
       * this object to initialize the static fields of
       * JFCReplayerConfiguration
       */
      JFCReplayerConfiguration configuration = new JFCReplayerConfiguration();
      CmdLineParser parser = new CmdLineParser(configuration);
      JFCReplayer jfcReplayer = new JFCReplayer(configuration);

      try {
         parser.parseArgument(args);
         jfcReplayer.execute();

         retVal = SUCCESS_EXIT;
      } catch (CmdLineException e) {
         System.err.println(e.getMessage());
         System.err.println();
         System.err
               .println("Usage: java [JVM options] "
                     + JFCReplayerMain.class.getName()
                     + " [Relayer options] \n");

         System.err.println("where [Replayer options] include:");
         System.err.println();

         parser.printUsage(System.err);
         retVal = GUITAR_EXCEPTION_EXIT;

      } catch (FileNotFoundException e) {

         GUITARLog.log.error("File not found exception");
         retVal = FILE_NOT_FOUND_EXCEPTION_EXIT;

      } catch (GException e) {

         GUITARLog.log.error("GUITAR exception");
         retVal = GUITAR_EXCEPTION_EXIT;

      } catch (Exception e) {

         GUITARLog.log.error("General exception. " +
                             "Likely AUT exception. " +
                             "If not file GUITAR bug.");
         retVal = UNCAUCHT_EXCEPTION_EXIT;

         e.printStackTrace();
      }

      printInfo(retVal);

      GUITARLog.log.info("-------- END REPLAY --------");

      Arrays.stream(Frame.getFrames()).forEach(f -> f.dispose());
      // Exit with error status
      System.exit(retVal);
   }

   /**
     * 
     */
   static private void
   printInfo(int retVal)
   {
      // ------------------
      // Elapsed time:
      long nEndTime = System.currentTimeMillis();
      long nDuration = nEndTime - nStartTime;
      DateFormat df = new SimpleDateFormat("HH : mm : ss: SS");

      df.setTimeZone(TimeZone.getTimeZone("GMT"));

      GUITARLog.log.info("Pass status    : " + retVal);
      GUITARLog.log.info("Time Elapsed   : " + df.format(nDuration));
      GUITARLog.log.info("Testcase       : " + JFCReplayerConfiguration.TESTCASE);
      GUITARLog.log.info("Log file       : " + JFCReplayerConfiguration.LOG_FILE);
      GUITARLog.log.info("GUI state file : "
            + JFCReplayerConfiguration.GUI_STATE_FILE);
   }

} // End of class
