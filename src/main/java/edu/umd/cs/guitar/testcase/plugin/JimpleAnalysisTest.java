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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.guitar.testcase.plugin.ct.AbtSlicer;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTAbtEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

/**
 * @author arlt
 */
public class JimpleAnalysisTest {

	/**
	 * AbstractMain method (for debugging purposes)
	 * 
	 * @param args
	 *            Command-line arguments
	 */
	public static void main(String[] args) {
		// (1) create an AbtSlicer object
		AbtSlicer abtSlicer = new AbtSlicer();

		// (2) set up parameters
		String scope = args[0]; // e.g. ".../testapp.jar"
		String classpath = "";
		String pakkage = "";

		// (3) run transformer
		abtSlicer.transform(scope, classpath, pakkage);

		// (4) create list of past events
		List<CTEvent> pastEvents = new LinkedList<CTEvent>();
		pastEvents
				.add(new CTAbtEvent(
						"<local.testapp.TestASMShouldInstrument$E1: void actionPerformed(java.awt.event.ActionEvent)>",
						"a"));
		
		// "a" is a friendly name for the event. You can still use:
		// new CTAbtEvent("<local.testapp.TestASMShouldInstrument$E1: void actionPerformed(java.awt.event.ActionEvent)>");
		
		pastEvents
				.add(new CTAbtEvent(
						"<local.testapp.TestASMShouldInstrument$E2: void actionPerformed(java.awt.event.ActionEvent)>",
						"b"));
		pastEvents
				.add(new CTAbtEvent(
						"<local.testapp.TestASMShouldInstrument$E3: void actionPerformed(java.awt.event.ActionEvent)>",
						"c"));

		// (5) create list of future events
		Set<CTEvent> futureEvents = new HashSet<CTEvent>();
		futureEvents
				.add(new CTAbtEvent(
						"<local.testapp.TestASMShouldInstrument$E4: void actionPerformed(java.awt.event.ActionEvent)>",
						"d"));
		futureEvents
				.add(new CTAbtEvent(
						"<local.testapp.TestASMShouldInstrument$E5: void actionPerformed(java.awt.event.ActionEvent)>",
						"e"));

		// (6) run slicer
		Map<List<CTEvent>, Integer> sequences = abtSlicer.slice(pastEvents,
				futureEvents, true, true);

		// (7) use 'sequences' in order to choose next event (here: just
		// logging)
		for (List<CTEvent> sequence : sequences.keySet()) {
			System.err.print(sequences.get(sequence) + ": ");
			for (CTEvent event : sequence) {
				System.err.print(event + ", ");
			}
			System.err.println();
		}
	}
}
