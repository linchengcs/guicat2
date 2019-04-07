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

package edu.umd.cs.guitar.testcase.plugin.ct.entity.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.umd.cs.guitar.model.data.EventType;

/**
 * @author arlt
 */
public class CTEfgEvent implements CTEvent {

	/**
	 * Event Type
	 */
	private EventType eventType;

	/**
	 * C-tor
	 * 
	 * @param eventType
	 *            Event Type
	 */
	public CTEfgEvent(EventType eventType) {
		this.eventType = eventType;
	}

	@Override
	public String getIdentifier() {
		return eventType.getEventId();
	}

	@Override
	public String getListener() {
		return getEventSignature();
	}

	@Override
	public Object getObject() {
		return eventType;
	}


	protected String getEventSignature() {
		List<String> listeners = eventType.getListeners();
		if (null == listeners || listeners.isEmpty())
			return null;

		String signature = String.format(
				"<%s: void actionPerformed(java.awt.event.ActionEvent)>",
				listeners.get(0));
		return signature;
	}

	/**
	 * Creates a list of ct events from a list of event types
	 * 
	 * @param eventTypes
	 *            List of event types
	 * @return List of ct events
	 */
	public static List<CTEvent> eventType2ctEvent(List<EventType> eventTypes) {
		// convert event types into new ct events
		List<CTEvent> newEvents = new LinkedList<CTEvent>();
		for (EventType eventType : eventTypes) {
			CTEfgEvent efgEvent = new CTEfgEvent(eventType);
			newEvents.add(efgEvent);
		}
		return newEvents;
	}

	/**
	 * Creates a list of ct events from a list of event types
	 * 
	 * @param eventTypes
	 *            List of event types
	 * @param ctEvents
	 *            ctEvents
	 * @return List of ct events
	 */
	public static List<CTEvent> eventType2ctEvent(List<EventType> eventTypes,
			List<CTEvent> ctEvents) {
		// copy existing ct events in helper map
		Map<EventType, CTEvent> helperMap = new HashMap<EventType, CTEvent>();
		for (CTEvent ctEvent : ctEvents) {
			helperMap.put((EventType) ctEvent.getObject(), ctEvent);
		}

		// convert event types into existing ct events
		List<CTEvent> newEvents = new LinkedList<CTEvent>();
		for (EventType eventType : eventTypes) {
			CTEvent ctEvent = helperMap.get(eventType);
			newEvents.add(ctEvent);
		}
		return newEvents;
	}

	@Override
	public String toString() {
		return getIdentifier();
	}

	@Override
	public boolean equals(Object o) {
		return this.toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

}
