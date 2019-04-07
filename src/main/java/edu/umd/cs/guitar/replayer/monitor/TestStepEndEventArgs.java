package edu.umd.cs.guitar.replayer.monitor;

import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.StepType;

/**
 * A wrapper of test step data Info about a testcase step event.
 *
 * @deprecated Use a subclass of {@link GTestStepEventArgs} instead
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
@Deprecated
public class TestStepEndEventArgs extends GTestStepEventArgs {

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
  public TestStepEndEventArgs(StepType step, ComponentType component, GUIType window) {
    super(step);
    this.component = component;
    this.window = window;
  }

}
