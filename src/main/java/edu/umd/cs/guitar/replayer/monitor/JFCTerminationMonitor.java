package edu.umd.cs.guitar.replayer.monitor;

import java.awt.Frame;
import java.awt.Window;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.netbeans.jemmy.QueueTool;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.JFCActionHandler;
import edu.umd.cs.guitar.exception.GException;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.JFCXWindow;
import edu.umd.cs.guitar.util.GUITARLog;

public class JFCTerminationMonitor extends GTestMonitor {

	int delay;
	int attempts = 4;

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	Map<Integer, String> mTerminalLabels = new TreeMap<Integer, String>();

	public void setmTerminalLabels(Map<Integer, String> mTerminalLabels) {
		this.mTerminalLabels = mTerminalLabels;
	}

	public void afterStep(GTestStepEventArgs eStep) {

	}

	@Override
	public void beforeStep(GTestStepEventArgs step) {

	}

	@Override
	public void exceptionHandler(GException e) {

	}

	@Override
	public void init() {

	}

	@Override
	public void term() {
		GUITARLog.log.info("Looking for terminal widget....");

		GComponent terminal = null;

		GEvent click = new JFCActionHandler();
		int count = 0;
		do {
			terminal = getNextTerminalComponent();

			if (terminal != null) {
				click.perform(terminal, null);
				GUITARLog.log.info("Clicking on terminal: *"
						+ terminal.getTitle() + "*");
				new QueueTool().waitEmpty(delay);
			}
			count++;
		} while (terminal != null && count <= attempts);

	}

	LinkedList<GWindow> lVisibleWindow;

	private GComponent getNextTerminalComponent() {
		GComponent terminal = null;
		lVisibleWindow = new LinkedList<GWindow>();
		traversalGUI();

		for (GWindow window : lVisibleWindow) {
			terminal = getTerminal(window);
			if (terminal != null)
				return terminal;

		}
		return null;
	}

	private void traversalGUI() {
		Frame[] windows = Frame.getFrames();

		for (Window window : windows) {
			if (!window.isEnabled() || !window.isActive()
					|| !window.isVisible()) {
				continue;
			}
			GWindow gWindow = new JFCXWindow(window);
			lVisibleWindow.addFirst(gWindow);
		}
		for (Window aWindow : windows) {
			traversal(aWindow);
		}
	}

	private void traversal(Window parent) {
		Window[] lOwnedWins = parent.getOwnedWindows();
		for (Window window : lOwnedWins) {
			if (!window.isEnabled() || !window.isActive()
					|| !window.isVisible()) {
				continue;
			}
			lVisibleWindow.addFirst(new JFCXWindow(window));
		}

		for (Window aOwnedWin : lOwnedWins) {
			traversal(aOwnedWin);
		}

	}

	private GComponent getTerminal(GWindow window) {
		GComponent container = window.getContainer();
		return getTerminal(container);
	}

	private GComponent getTerminal(GComponent container) {

		if (isTerminal(container))
			return container;
		GComponent terminal = null;
		List<GComponent> children = container.getChildren();

		for (GComponent child : children) {
			terminal = getTerminal(child);
			if (terminal != null)
				return terminal;
		}
		return terminal;
	}

	private boolean isTerminal(GComponent container) {
		if (container == null)
			return false;

		if (!container.isEnable())
			return false;

		String title = container.getTitle();

		for (Integer index : mTerminalLabels.keySet()) {
			String sTerminalTitle = mTerminalLabels.get(index);
			if (sTerminalTitle.equalsIgnoreCase(title))
				return true;
		}
		return false;
	}

}
