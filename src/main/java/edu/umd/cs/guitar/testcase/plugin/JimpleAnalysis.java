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

package edu.umd.cs.guitar.testcase.plugin;

import java.util.List;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.JimpleAnalysisConfiguration;
import edu.umd.cs.guitar.testcase.TestCaseGeneratorConfiguration;
import edu.umd.cs.guitar.testcase.plugin.ct.CTBodyTransformer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTEfgSequenceSelector;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSlicer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSootRunner;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.wmich.cs.carot.util.CTEvents2Testcase;

/**
 * @author arlt
 */
public class JimpleAnalysis extends GTestCaseGeneratorPlugin {

	@Override
	public TestCaseGeneratorConfiguration getConfiguration() {
		return new JimpleAnalysisConfiguration();
	}

	@Override
	public boolean isValidArgs() {
		if (JimpleAnalysisConfiguration.SCOPE == null)
			return false;
		return true;
	}

	@Override
	public void generate(EFG efg, String outputDir, int nMaxNumber,
			boolean noDuplicateEvent, boolean treatTerminalEventSpecially) {
		try {
			// init
			this.efg = efg;
			initialize();

			// get events
			List<EventType> eventTypes = efg.getEvents().getEvent();
			List<CTEvent> efgEvents = CTEfgEvent.eventType2ctEvent(eventTypes);

			// execute Soot runner
			CTSootRunner sootRunner = new CTSootRunner();
			CTBodyTransformer bodyTransformer = sootRunner.run(
					JimpleAnalysisConfiguration.SCOPE,
					JimpleAnalysisConfiguration.CLASSPATH,
					JimpleAnalysisConfiguration.PACKAGE);

			// run slicer
			CTSlicer slicer = new CTSlicer(bodyTransformer, efgEvents);
			slicer.run();

			// run sequence selector
			CTEfgSequenceSelector selector = new CTEfgSequenceSelector(slicer,
					efg, succs);
		//	selector.run(JimpleAnalysisConfiguration.LENGTH, true);
			selector.run(JimpleAnalysisConfiguration.LENGTH, true);  // false for no optimize

			CTEvents2Testcase.writeTo(selector.getTotalSequences(), outputDir);
			selector.generateDependencyMatrix();
			// print statistics
			// bodyTransformer.printStatistics();
			// slicer.printStatistics();
			selector.printStatistics();
			// selector.printFrequencyOfSequenceLengths();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * AbstractMain method (for debugging purposes)
	 * 
	 * @param args
	 *            Command-line arguments
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			String[] args_ticket = new String[]{
					"./aut/ticket.EFG",
					"./aut/ticket.jar"
			};

			String[] args_rachota = new String[]{
					"./aut/rachota.EFG",
					"./aut/Rachota.jar"
			};

			String[] args_terpword = new String[]{
					"./aut/terpword.EFG",
					"./aut/terpword.jar"
			};

			String[] args_barad = new String[]{
			        "./log/barad/barad.GUI",
					"./log/barad/barad.EFG",
					"./aut/barad-ticket.jar",
                    "slice",
                    "2",
                    "log/testcase"
			};

			String[] args_arlt12 = new String[]{
					"./log/arlt/arlt.GUI",
					"./log/arlt/arlt.EFG",
					"./aut/arlt.jar",
					"slice",
					"3",
					"log/testcase"
			};
			//you need change **args** and **PACKAGE**
			args = args_arlt12;
		}



        String guiFile = args[0];
        String efgFile = args[1];
        String sootScope = args[2];
        String algorithm = args[3];
        int len = Integer.parseInt(args[4]);
        String dir = args[5];
        String sootPackage = "";

		// read EFG
		XMLHandler xml = new XMLHandler();
		EFG efg = (EFG) xml.readObjFromFile(efgFile, EFG.class);



		// setup analysis
		JimpleAnalysis analysis = new JimpleAnalysis();
		JimpleAnalysisConfiguration.SCOPE = sootScope;
		JimpleAnalysisConfiguration.LENGTH = len;
		JimpleAnalysisConfiguration.PACKAGE = sootPackage;  //**ticket** for Ticket, **barad** for BaradTicket

		// generate test cases
		analysis.generate(efg, dir, 0, false, false);
	}

}
