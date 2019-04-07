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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTDef;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.CTUse;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.util.Log;
import edu.umd.cs.guitar.testcase.plugin.ct.util.StopWatch;
import edu.wmich.cs.carot.util.CTEvents2Testcase;
import edu.wmich.cs.carot.util.Olog;

/**
 * @author arlt
 */
public class CTSequenceSelector {

	/**
	 * Index of first event
	 */
	final static int FIRST = 0;

	/**
	 * Index of second event
	 */
	final static int SECOND = 1;

	/**
	 * Index of third event
	 */
	final static int THIRD = 2;

	/**
	 * Slicer
	 */
	protected CTSlicer slicer;

	/**
	 * Events
	 */
	protected List<CTEvent> events;

	/**
	 * Stop Watch
	 */
	private StopWatch stopWatch = new StopWatch();

	/**
	 * List of chain sequences
	 */
	private List<List<CTEvent>> chains = new LinkedList<List<CTEvent>>();

	/**
	 * List of executable chain sequences
	 */
	private List<List<CTEvent>> execChains = new LinkedList<List<CTEvent>>();

	/**
	 * List of causal executable chain sequences
	 */
	private List<List<CTEvent>> causalExecChains = new LinkedList<List<CTEvent>>();

	/**
	 * List of bush sequences
	 */
	private List<List<CTEvent>> bushes = new LinkedList<List<CTEvent>>();

	/**
	 * List of executable bush sequences
	 */
	private List<List<CTEvent>> execBushes = new LinkedList<List<CTEvent>>();

	/**
	 * List of required executable bush sequences
	 */
	private List<List<CTEvent>> requiredExecBushes = new LinkedList<List<CTEvent>>();

	/**
	 * List of redundant executable bush sequences
	 */
	private List<List<CTEvent>> redundantExecBushes = new LinkedList<List<CTEvent>>();

	/**
	 * List of bushes which are also chains
	 */
	private List<List<CTEvent>> bushesInChains = new LinkedList<List<CTEvent>>();

	/**
	 * List of total sequences
	 */
	private List<List<CTEvent>> totalSequences = new LinkedList<List<CTEvent>>();


	public CTSequenceSelector(CTSlicer slicer) {
		this.slicer = slicer;
		this.events = slicer.getEvents();
	}

	public List<List<CTEvent>> getTotalSequences() {
		return totalSequences;
	}

	/**
	 * Runs the sequence selector
	 * 
	 * @param length
	 *            Length
	 * @param optimize
	 *            true = optimize set of sequences
	 */
	public void run(int length, boolean optimize) {
		stopWatch.start();

		// first select chains
		selectChains(length);
		if (optimize)
			optimizeChains();

		// then select bushes
		selectBushes(length);
		if (optimize)
			optimizeBushes();

		// create set of sequences
		totalSequences.addAll(causalExecChains);
		for (int i = 0; i < requiredExecBushes.size(); i++) {
		//	Log.info(String.format("Checking whether Bush Sequence %d of %d is also a Chain Sequence", i, requiredExecBushes.size()));
			List<CTEvent> sequence = requiredExecBushes.get(i);
			if (totalSequences.contains(sequence)) {
				bushesInChains.add(sequence);
			} else {
				totalSequences.add(sequence);
			}
		}

//		Olog.log.info("===============causalExecChains from CTsequenceSelector==========");
//		Olog.log.info(causalExecChains.toString());
//		Olog.log.info(execChains.toString());
//		Olog.log.info(totalSequences.toString());
//		Olog.log.info(requiredExecBushes.toString());
//		Olog.log.info(execBushes.toString());
//		Olog.log.info(redundantExecBushes.toString());
//
//		findOffsping();

		stopWatch.stop();
	}

	/**
	 * Selects chains of events
	 * 
	 * @param length
	 *            Length of sequences
	 */
	protected void selectChains(int length) {
		for (CTEvent event : events) {
		//	Log.info("Selecting Chain Sequences for Event "			+ event.getIdentifier());

			// create sequence and add event
			List<CTEvent> sequence = new LinkedList<CTEvent>();
			sequence.add(event);

			// select chain sequences
			selectChains(event, sequence, length - 1);
		}
	}

	/**
	 * Selects chain event sequences
	 * 
	 * @param event
	 *            Event
	 * @param sequence
	 *            Sequence
	 * @param length
	 *            Length
	 */
	protected void selectChains(CTEvent event, List<CTEvent> sequence,
			int length) {
		// desired length reached?
		if (0 == length) {
			// add sequence
			chains.add(sequence);

			// make sequence executable
			if (null != makeExec(sequence))
				execChains.add(sequence);

			return;
		}

		// iterate events
		for (CTEvent event2 : events) {
			// check dependency
			if (!haveDependingFields(event, event2))
				continue;

			// add event to sequence
			List<CTEvent> newSequence = new LinkedList<CTEvent>(sequence);
			newSequence.add(event2);

			// select chain sequences
			selectChains(event2, newSequence, length - 1);
		}
	}

	public void findOffsping() {
		int flag = 0, rowi = 0, colj = 0;
		Olog.log.info("printing SliceDDG graph:");
		for (CTEvent row : events) {
			rowi++; colj = 0;
			Olog.log.info("printing row " + rowi + ", event: " + row.getIdentifier());
			Olog.log.info("event def: " + slicer.getEventFieldDefs(row));
			Olog.log.info("event uses: " + slicer.getEventFieldUses(row));
			for(CTEvent col : events) {
				colj++;
				flag = haveDependingFields(row, col) ? 1 : 0;
				Olog.log.info("col " + colj + ", flag = " + flag);
			}
		}
	}

	public String generateDependencyMatrix() {
	    Olog.log.info("printing dependency matrix:");
	    String mat = "";
        int flag = 0;
        for (CTEvent row : events) {
            String mrow = "";
            for(CTEvent col : events) {
                flag = haveDependingFields(row, col) ? 1 : 0;
                mrow += flag + ", ";
            }
            mrow = mrow.substring(0, mrow.length()-2);
            mat += mrow + "\r\n";
        }
        mat = mat.substring(0, mat.length()-2);
        Olog.log.info("\r\n" + mat +"\r\n");
        return mat;
    }

	/**
	 * Selects bushes of events
	 * 
	 * @param length
	 *            Length of sequences
	 */
	protected void selectBushes(int length) {
		for (CTEvent event : events) {
		//	Log.info("Selecting Bush Sequences for Event "			+ event.getIdentifier());

			// create sequence and add event
			List<CTEvent> sequence = new LinkedList<CTEvent>();
			sequence.add(event);

			// select bush sequences
			selectBushes(sequence, length);
		}
	}

	/**
	 * Selects bushes event sequences
	 * 
	 * @param sequence
	 *            Sequence
	 * @param length
	 *            Length
	 */
	protected void selectBushes(List<CTEvent> sequence, int length) {
		// desired length reached?
		if (sequence.size() == length) {
			// add sequence
			bushes.add(sequence);

			// make sequence executable
			if (null != makeExec(sequence))
				execBushes.add(sequence);

			return;
		}

		for (CTEvent event : events) {
			// create new list of events
			List<CTEvent> newSequence = new LinkedList<CTEvent>();
			newSequence.addAll(sequence);
			newSequence.add(newSequence.size() - 1, event);

			// check dependency
			boolean areDependent = true;
			for (int i = 0; i < newSequence.size() - 1; i++) {
				if (!haveDependingFields(newSequence.get(i),
						newSequence.get(newSequence.size() - 1))) {
					areDependent = false;
					break;
				}
			}

			// not dependent?
			if (!areDependent)
				continue;

			// try to select bush sequences from new list of events
			selectBushes(newSequence, length);
		}
	}

	/**
	 * Checks whether two events are dependent
	 * 
	 * @param event1
	 *            Event 1 (which may write fields read in Event 2)
	 * @param event2
	 *            Event 2 (which may read fields written in Event 1)
	 * @return true = two events have a dependency
	 */
	public boolean haveDependingFields(CTEvent event1, CTEvent event2) {
		Set<?> common = slicer.getCommonFields(event1, event2);
        Boolean bool = common.isEmpty();
//        if (!bool) {
//            Olog.log.info("++++++++++common fields______" + event1.toString() + "   " + event2.toString());
//            Olog.log.info(slicer.getEventFieldDefs(event1).toString());
//            Olog.log.info(slicer.getEventFieldUses(event2).toString());
//        }
        return !bool;
	}

	/**
	 * Makes a sequence executable
	 * 
	 * @param sequence
	 *            Sequence
	 * @return Executable Sequence
	 */
	public List<CTEvent> makeExec(List<CTEvent> sequence) {
		return sequence; // do nothing
	}

	/**
	 * Optimizes the set of chain sequences
	 */
	protected void optimizeChains() {
		for (int i = 0; i < execChains.size(); i++) {
//			Log.info(String.format(
//					"Performing Causal Analysis on Chain Sequence %d of %d",
//					i + 1, execChains.size()));

			// optimization only works for sequence lengths >= 3
			List<CTEvent> sequence = execChains.get(i);
			if (sequence.size() < 3)
				continue;

			boolean isCausal = true;
			for (int j = 0; j < sequence.size() - 2; j++) {
				// drop sequence if a sub-sequence (a triple) is not causal
				List<CTEvent> subSequence = sequence.subList(j, j + 3);
				if (!isCausal(subSequence)) {
					isCausal = false;
					break;
				}
			}

			if (isCausal) {
				causalExecChains.add(sequence);
			}
		}
	}

	/**
	 * Checks whether three events are causal
	 * 
	 * @param sequence
	 *            Event Sequence
	 * @return true = the three events are causal
	 */
	public boolean isCausal(List<CTEvent> sequence) {
		if (null == sequence || 3 != sequence.size())
			return false;

		// compute intersections
		Set<String> int12 = slicer.getCommonFields(sequence.get(FIRST),
				sequence.get(SECOND));
		Set<String> int23 = slicer.getCommonFields(sequence.get(SECOND),
				sequence.get(THIRD));

		// iterate common field values
		for (String fieldValue : int23) {
			Map<CTDef, Set<CTUse>> slice = slicer.getSubSlice(
					sequence.get(SECOND), fieldValue);
			for (CTDef def : slice.keySet()) {
				Set<CTUse> uses = slice.get(def);
				for (CTUse use : uses) {
					if (int12.contains(use.getFieldValue()))
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks whether events in a sequence are causal
	 * 
	 * @param sequence
	 *            Event Sequence
	 * @return true = the events in the sequence are causal
	 */
	public boolean isCausalEx(List<CTEvent> sequence) {
		// null or only 1 event in sequence?
		if (null == sequence || 1 == sequence.size())
			return false;
		
		// only 2 events in sequence?
		if ( 2 == sequence.size() )
			return haveDependingFields(sequence.get(0), sequence.get(1));

		// add common fields to list
		List<Set<String>> commonFieldList = new LinkedList<Set<String>>();
		for (int i = 0; i < sequence.size() - 1; i++) {
			Set<String> commonFields = slicer.getCommonFields(sequence.get(i), sequence.get(i + 1));
			commonFieldList.add(commonFields);
		}

		// iterate common fields
		for (int i = commonFieldList.size() - 1; i > 0; i--) {
			boolean isCausal = false;
			Set<String> commonFields23 = commonFieldList.get(i);
			for (String fieldValue : commonFields23) {
				Map<CTDef, Set<CTUse>> slice = slicer.getSubSlice(sequence.get(i), fieldValue);
				for (CTDef def : slice.keySet()) {
					Set<CTUse> uses = slice.get(def);
					for (CTUse use : uses) {
						Set<String> commonFields12 = commonFieldList.get(i - 1);
						if (commonFields12.contains(use.getFieldValue())) {
							isCausal = true;
						}
					}
				}
			}
			if (!isCausal)
				return false;
		}
		return true;
	}

	/**
	 * Optimizes the set of bush sequences
	 */
	protected void optimizeBushes() {
		for (int i = 0; i < execBushes.size(); i++) {
//			Log.info(String.format(
//					"Performing POR Analysis on Bush Sequence %d of %d", i + 1,
//					execBushes.size()));

			// optimization only works for sequence lengths >= 3
			List<CTEvent> sequence = execBushes.get(i);
			if (sequence.size() < 3)
				continue;

			// is bush already redundant?
			if (redundantExecBushes.contains(sequence)) {
				continue;
			}

			// is bush PO-reducible?
			if (isPOReducible(sequence)) {
				// create redundant sequence (for later checking)
				List<CTEvent> redundantSequence = new LinkedList<CTEvent>();
				redundantSequence.add(sequence.get(1));
				redundantSequence.add(sequence.get(0));
				redundantSequence.add(sequence.get(2));
				redundantExecBushes.add(redundantSequence);
			}

			requiredExecBushes.add(sequence);
		}
	}

	/**
	 * Checks whether the first two events are PO-reducible
	 * 
	 * @param sequence
	 *            Event Sequence
	 * @return true = the first two events are PO-reducible
	 */
	public boolean isPOReducible(List<CTEvent> sequence) {
		if (null == sequence || 3 != sequence.size())
			return false;

		// compute intersections
		Set<String> int13 = slicer.getCommonFields(sequence.get(FIRST),
				sequence.get(THIRD));
		Set<String> int23 = slicer.getCommonFields(sequence.get(SECOND),
				sequence.get(THIRD));

		// iterate common field values
		for (String fieldValue : int13) {
			Map<CTDef, Set<CTUse>> slice = slicer.getSubSlice(
					sequence.get(FIRST), fieldValue);
			for (CTDef def : slice.keySet()) {
				Set<CTUse> uses = slice.get(def);
				for (CTUse use : uses) {
					if (int23.contains(use.getFieldValue()))
						return false;
				}
			}
		}

		// iterate common field values
		for (String fieldValue : int23) {
			Map<CTDef, Set<CTUse>> slice = slicer.getSubSlice(
					sequence.get(SECOND), fieldValue);
			for (CTDef def : slice.keySet()) {
				Set<CTUse> uses = slice.get(def);
				for (CTUse use : uses) {
					if (int13.contains(use.getFieldValue()))
						return false;
				}
			}
		}

		return true;
	}

	/**
	 * Prints the given sequence
	 * 
	 * @param sequence
	 *            Sequence
	 */
	public void logSequence(List<CTEvent> sequence) {
		StringBuilder sb = new StringBuilder();
		for (CTEvent event : sequence) {
			sb.append(event.getIdentifier() + ", ");
		}
		Log.info(sb.toString());
	}

	/**
	 * Prints statistics of the sequence selector
	 */
	public void printStatistics() {
		Log.info("*** Statistics of Sequence Selector ***");

		// chains
		Log.info(String.format("Chains: %d", chains.size()));
		Log.info(String.format("Executable Chains: %d", execChains.size()));
		Log.info(String.format("Causal Executable Chains: %d",
				causalExecChains.size()));

		// bushes
		Log.info(String.format("Bushes: %d", bushes.size()));
		Log.info(String.format("Executable Bushes: %d", execBushes.size()));
		Log.info(String.format("Required Bushes: %d", requiredExecBushes.size()));
		Log.info(String.format("Redundant Bushes: %d",
				redundantExecBushes.size()));

		// total
		Log.info(String.format("Bushes in Chains: %d", bushesInChains.size()));
		Log.info(String.format("Total Sequences: %d", totalSequences.size()));

		Log.info("bushInChains: " + bushesInChains.toString());
		Log.info("totalSequence: " + totalSequences.toString());

		// time
		Log.info(String.format("Total Time: %d ms", stopWatch.getTime()));
		Log.info(String.format("Time per Event: %d ms", stopWatch.getTime()
				/ events.size()));
		Log.info("\n" +  totalSequences.size());
	}

	/**
	 * Prints the frequency of sequence lengths
	 */
	public void printFrequencyOfSequenceLengths() {
		Map<Integer, Integer> freq = new HashMap<Integer, Integer>();
		for (List<CTEvent> sequence : totalSequences) {
			List<CTEvent> execSequence = makeExec(sequence);
			if (null == execSequence)
				continue;

			// analyze size (length of sequence)
			int length = execSequence.size();
			if (!freq.containsKey(length)) {
				freq.put(length, 1);
			} else {
				int count = freq.get(length);
				freq.put(length, count + 1);
			}
		}

		// print info
		for (int length : freq.keySet()) {
			Log.info(String.format("Length-%d = %d", length, freq.get(length)));
		}
	}

}