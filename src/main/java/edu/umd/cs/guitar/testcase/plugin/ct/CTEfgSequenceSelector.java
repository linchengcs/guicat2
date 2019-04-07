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

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.umd.cs.guitar.util.GraphUtil;

/**
 * @author arlt
 */
public class CTEfgSequenceSelector extends CTSequenceSelector {

	/**
	 * Successors
	 */
	private Hashtable<EventType, Vector<EventType>> succs;

	/**
	 * Graph Utilities
	 */
	private GraphUtil graphUtil;


	public CTEfgSequenceSelector(CTSlicer slicer, EFG efg,
			Hashtable<EventType, Vector<EventType>> succs) {
		super(slicer);
		this.succs = succs;
		this.graphUtil = new GraphUtil(efg);
	}

	/**
	 * Makes a sequence executable
	 * 
	 * @param sequence
	 *            Sequence
	 * @return Executable Sequence
	 */
	public List<CTEvent> makeExec(List<CTEvent> sequence) {
		if (null == sequence || sequence.isEmpty())
			return null;

		List<EventType> path = new LinkedList<EventType>();
		for (int i = 0; i < sequence.size(); i++) {
			LinkedList<EventType> subPath;
			if (0 == i) {
				EventType event = (EventType) sequence.get(0).getObject();
				subPath = graphUtil.pathToRoot(event);
			} else {
				EventType event1 = (EventType) sequence.get(i - 1).getObject();
				EventType event2 = (EventType) sequence.get(i).getObject();
				subPath = bfsEvent2Event(event1, event2);
			}

			if (null == subPath) {
				return null;
			} else {
				path.addAll(subPath);
			}
		}

		return CTEfgEvent.eventType2ctEvent(path, events);
	}

	/**
	 * Breadth-first-search from event to event in the EFG
	 * 
	 * @param start
	 *            Start event
	 * @param goal
	 *            Goal event to reach
	 * @return Event sequence between start and goal
	 */
	public LinkedList<EventType> bfsEvent2Event(EventType start, EventType goal) {
		LinkedList<EventType> retPath = null;
		EventType found = null;

		HashMap<EventType, EventType> visitedBy = new HashMap<EventType, EventType>();
		LinkedList<EventType> queue = new LinkedList<EventType>();
		queue.add(start);

		while (found == null && !queue.isEmpty()) {
			EventType e = queue.remove();
			for (EventType succ : succs.get(e)) {
				if (!visitedBy.containsKey(succ)) {
					visitedBy.put(succ, e);
					if (succ == goal) {
						found = succ;
						break;
					} else {
						queue.add(succ);
					}
				}
			}
		}

		if (found != null) {
			retPath = new LinkedList<EventType>();
			EventType cursor = visitedBy.get(found);
			while (cursor != start) {
				retPath.addFirst(cursor);
				cursor = visitedBy.get(cursor);
			}
		}

		return retPath;
	}

}
