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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTMethod;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.util.CollectionHelper;

/**
 * @author arlt
 */
public class AbtSlicer {

	/**
	 * Soot Runner
	 */
	private CTSootRunner sootRunner;

	/**
	 * Body Transformer
	 */
	private CTBodyTransformer bodyTransformer;

	/**
	 * Slicer
	 */
	private CTSlicer slicer;
	
	/**
	 * Sequence Selector
	 */
	private CTSequenceSelector selector;

	/**
	 * C-tor
	 */
	public AbtSlicer() {

	}

	/**
	 * Transforms a Java program into a Jimple representation
	 * 
	 * @param scope
	 *            Scope (usually a JAR file or a directory)
	 * @param classpath
	 *            Classpath
	 * @param pakkage
	 *            Package to be analyzed
	 * @return true = success
	 */
	public boolean transform(String scope, String classpath, String pakkage) {
		// run Soot and get body transformer
		sootRunner = new CTSootRunner();
		bodyTransformer = sootRunner.run(scope, classpath, pakkage);
		return (null != bodyTransformer);
	}

	/**
	 * Slices the Jimple representation of the Java program
	 * 
	 * @param pastEvents
	 *            past events
	 * @param futureEvents
	 *            future events
	 * @return Possible event sequences
	 */
	public Map<List<CTEvent>, Integer> slice(
			List<CTEvent> pastEvents,
			Set<CTEvent> futureEvents,
			boolean startInAnyPastEvent,
			boolean reportZeroLengths) {
		
		// expect at least one past and one future event
		if ( pastEvents.isEmpty() || futureEvents.isEmpty() ) {
			return null;
		}
		
		// first of all, slice all available events
		List<CTEvent> events = new LinkedList<CTEvent>();
		events.addAll(pastEvents);
		events.addAll(futureEvents);
		slicer = new CTSlicer(bodyTransformer, events);
		slicer.run();
		
		// then, build possible event sequences	
		Set<List<CTEvent>> possibleSequences = new HashSet<List<CTEvent>>();
		for ( CTEvent futureEvent : futureEvents ) {
			if ( startInAnyPastEvent ) {
				for ( int i = 0; i < pastEvents.size(); i++ ) {
					List<CTEvent> possibleSequence = new LinkedList<CTEvent>();
					List<CTEvent> subPastEvents = pastEvents.subList(i, pastEvents.size());					
					possibleSequence.addAll(subPastEvents);
					possibleSequence.add(futureEvent);
					possibleSequences.add(possibleSequence);
				}
			} else {
				List<CTEvent> possibleSequence = new LinkedList<CTEvent>();
				possibleSequence.addAll(pastEvents);
				possibleSequence.add(futureEvent);
				possibleSequences.add(possibleSequence);
			}
		}
		
		int length; // length of def-use chain
		selector = new CTSequenceSelector(slicer);
		Map<List<CTEvent>, Integer> sequences = new HashMap<List<CTEvent>, Integer>(); 

		// then, rate possible event sequences
		for ( List<CTEvent> possibleSequence : possibleSequences ) {
			// reset length
			length = 0;
			
			// sequence length = 2
			if ( 2 == possibleSequence.size() ) {
				if ( selector.haveDependingFields(possibleSequence.get(0), possibleSequence.get(1)) ) {
					length = 1;
				}
			}
			// sequence length > 2
			else {
				// compute possible sub-sequences
				List<CTEvent> fullSubSeq = possibleSequence.subList(1, possibleSequence.size() - 1);
				List<List<CTEvent>> subSeqs = CollectionHelper.powerset(fullSubSeq);
				
				// rate possible sub-sequences
				for ( List<CTEvent> subSeq : subSeqs) {
					// ignore the empty set
					if ( subSeq.isEmpty() )
						continue;
					
					List<CTEvent> seq = new LinkedList<CTEvent>();
					seq.add(possibleSequence.get(0));
					seq.addAll(subSeq);
					seq.add(possibleSequence.get(possibleSequence.size() - 1));
					
					if ( selector.isCausalEx(seq) ) {
						if ( seq.size() > length ) {
							length = seq.size();
						}
					}
				}
			}
			
			// report possible sequence
			if ( length > 0 || reportZeroLengths ) {
				sequences.put(possibleSequence, length);				
			}
		}
		
		return sequences;
	}


	/**
	 * Guesses the set of action listeners
	 * 
	 * @return Set of action listeners
	 */
	public Set<String> guessEvents() {
		Set<String> events = new HashSet<String>();
		Collection<CTMethod> methods = getCurrentBodyTransformer().getMethods();
		for (CTMethod method : methods) {
			String signature = method.getSignature();
			if (signature.contains("actionPerformed")) {
				events.add(signature);
			}
		}
		return events;
	}

	/**
	 * Returns the current soot runner
	 * 
	 * @return Soot runner
	 */
	public CTSootRunner getCurrentSootRunner() {
		return sootRunner;
	}

	/**
	 * Returns the current body transformer
	 * 
	 * @return Body transformer
	 */
	public CTBodyTransformer getCurrentBodyTransformer() {
		return bodyTransformer;
	}

	/**
	 * Returns the current slicer
	 * 
	 * @return Slicer
	 */
	public CTSlicer getCurrentSlicer() {
		return slicer;
	}

}
