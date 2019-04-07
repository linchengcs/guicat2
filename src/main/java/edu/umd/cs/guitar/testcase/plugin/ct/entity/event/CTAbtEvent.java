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

/**
 * @author arlt
 */
public class CTAbtEvent implements CTEvent {

	/**
	 * Event
	 */
	private String listener;
	
	/**
	 * A friendly name of the event
	 */
	private String friendlyName;

	/**
	 * C-tor
	 * 
	 * @param listener
	 *            Listener
	 */
	public CTAbtEvent(String listener) {
		this.listener = listener;
	}
	
	/**
	 * C-tor
	 * 
	 * @param listener
	 *            Listener
	 * @param friendlyName
	 * 			  A friendly name of the event
	 */
	public CTAbtEvent(String listener, String friendlyName) {
		this.listener = listener;
		this.friendlyName = friendlyName;
	}

	@Override
	public String getIdentifier() {
		return listener;
	}

	@Override
	public String getListener() {
		return listener;
	}

	@Override
	public Object getObject() {
		return listener;
	}
	
	@Override
	public String toString() {
		if ( null != friendlyName )
			return friendlyName;
		return super.toString();
	}

}
