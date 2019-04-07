package edu.umd.cs.guitar.replayer.monitor;

import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.StepType;

/**
 * A wrapper of test step data Info about a testcase step event.
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class StateMonitorAfterEventArg extends TestStepEndEventArgs {

	public ComponentType component;
	GUIType window;

	public ComponentType getComponent() {
		return component;
	}

	public GUIType getWindow() {
		return window;
	}

	/**
	 * @param step
	 * @param component
	 * @param window
	 */
	public StateMonitorAfterEventArg(StepType step, ComponentType component,
			GUIType window) {
		super(step, component, window);
	}
}
