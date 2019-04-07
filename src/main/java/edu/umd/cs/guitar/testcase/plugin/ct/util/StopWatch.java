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

package edu.umd.cs.guitar.testcase.plugin.ct.util;

/**
 * @author arlt
 */
public class StopWatch {

	/**
	 * Start Time
	 */
	private long startTime;

	/**
	 * Stop Time
	 */
	private long stopTime;

	/**
	 * Enabled or Disabled
	 */
	private boolean enabled;

	/**
	 * Returns a started instance of a stop watch
	 * 
	 * @return Stop watch
	 */
	public static StopWatch getInstanceAndStart() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		return stopWatch;
	}

	/**
	 * Start stop watch
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		enabled = true;
	}

	/**
	 * Stops the stop watch
	 * 
	 * @return Time
	 */
	public long stop() {
		if (!isEnabled())
			return 0;

		stopTime = System.currentTimeMillis();
		enabled = false;

		return getTime();
	}

	/**
	 * Returns the time
	 * 
	 * @return Time
	 */
	public long getTime() {
		if (isEnabled())
			return System.currentTimeMillis() - startTime;

		return stopTime - startTime;
	}

	/**
	 * Checks whether the stop watch is enabled
	 * 
	 * @return true = stop watch is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

}
