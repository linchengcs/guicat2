package edu.umd.cs.guitar.replayer.monitor;

import edu.umd.cs.guitar.model.data.StepType;

/**
 * A wrapper of test step data Info about a testcase step event.
 * 
 * 
 * @deprecated Use a subclass of {@link GTestStepEventArgs} instead
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 * 
 */
@Deprecated
public class TestStepStartEventArgs extends GTestStepEventArgs {

	/**
	 * @param step
	 */
	public TestStepStartEventArgs(StepType step) {
		super(step);
	}

}
