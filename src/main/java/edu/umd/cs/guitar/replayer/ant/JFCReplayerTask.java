/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
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
package edu.umd.cs.guitar.replayer.ant;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;

import edu.umd.cs.guitar.replayer.JFCReplayerMain;

/**
 * 
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 * 
 */
public class JFCReplayerTask extends MatchingTask {

	/**
	 * GUITAR variables
	 */
	File gui;
	File efg;

	File stateDir;
	File logDir;

	String classname;
	String configuration;

	/**
	 * @return the configuration
	 */
	public String getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration
	 *            the configuration to set
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * @param classname
	 *            the classname to set
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}

	/**
	 * 
	 */
	private static final String STATE_EXT = "sta";
	/**
	 * 
	 */
	private static final String LOG_EXT = "log";
	protected File file = null;
	protected File dir = null;
	protected Vector filesets = new Vector();
	protected boolean usedMatchingTask = false;

	/**
	 * Set the name of a single file to be removed.
	 * 
	 * @param file
	 *            the file to be deleted
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Set the directory from which files are to be deleted
	 * 
	 * @param dir
	 *            the directory path.
	 */
	public void setDir(File dir) {
		this.dir = dir;
	}

	/**
	 * Adds a set of files to be deleted.
	 * 
	 * @param set
	 *            the set of files to be deleted
	 */
	public void addFileset(FileSet set) {
		filesets.addElement(set);
	}

	/**
	 * add a name entry on the include list
	 * 
	 * @return a NameEntry object to be configured
	 */
	public PatternSet.NameEntry createInclude() {
		usedMatchingTask = true;
		return super.createInclude();
	}

	/**
	 * add a name entry on the include files list
	 * 
	 * @return an NameEntry object to be configured
	 */
	public PatternSet.NameEntry createIncludesFile() {
		usedMatchingTask = true;
		return super.createIncludesFile();
	}

	/**
	 * add a name entry on the exclude list
	 * 
	 * @return an NameEntry object to be configured
	 */
	public PatternSet.NameEntry createExclude() {
		usedMatchingTask = true;
		return super.createExclude();
	}

	/**
	 * add a name entry on the include files list
	 * 
	 * @return an NameEntry object to be configured
	 */
	public PatternSet.NameEntry createExcludesFile() {
		usedMatchingTask = true;
		return super.createExcludesFile();
	}

	/**
	 * add a set of patterns
	 * 
	 * @return PatternSet object to be configured
	 */
	public PatternSet createPatternSet() {
		usedMatchingTask = true;
		return super.createPatternSet();
	}

	/**
	 * Sets the set of include patterns. Patterns may be separated by a comma or
	 * a space.
	 * 
	 * @param includes
	 *            the string containing the include patterns
	 */
	public void setIncludes(String includes) {
		usedMatchingTask = true;
		super.setIncludes(includes);
	}

	/**
	 * Sets the set of exclude patterns. Patterns may be separated by a comma or
	 * a space.
	 * 
	 * @param excludes
	 *            the string containing the exclude patterns
	 */
	public void setExcludes(String excludes) {
		usedMatchingTask = true;
		super.setExcludes(excludes);
	}

	/**
	 * Sets whether default exclusions should be used or not.
	 * 
	 * @param useDefaultExcludes
	 *            "true"|"on"|"yes" when default exclusions should be used,
	 *            "false"|"off"|"no" when they shouldn't be used.
	 */
	public void setDefaultexcludes(boolean useDefaultExcludes) {
		usedMatchingTask = true;
		super.setDefaultexcludes(useDefaultExcludes);
	}

	/**
	 * Sets the name of the file containing the includes patterns.
	 * 
	 * @param includesfile
	 *            A string containing the filename to fetch the include patterns
	 *            from.
	 */
	public void setIncludesfile(File includesfile) {
		usedMatchingTask = true;
		super.setIncludesfile(includesfile);
	}

	/**
	 * Sets the name of the file containing the includes patterns.
	 * 
	 * @param excludesfile
	 *            A string containing the filename to fetch the include patterns
	 *            from.
	 */
	public void setExcludesfile(File excludesfile) {
		usedMatchingTask = true;
		super.setExcludesfile(excludesfile);
	}

	/**
	 * Sets case sensitivity of the file system
	 * 
	 * @param isCaseSensitive
	 *            "true"|"on"|"yes" if file system is case sensitive,
	 *            "false"|"off"|"no" when not.
	 */
	public void setCaseSensitive(boolean isCaseSensitive) {
		usedMatchingTask = true;
		super.setCaseSensitive(isCaseSensitive);
	}

	/**
	 * Sets whether or not symbolic links should be followed.
	 * 
	 * @param followSymlinks
	 *            whether or not symbolic links should be followed
	 */
	public void setFollowSymlinks(boolean followSymlinks) {
		usedMatchingTask = true;
		super.setFollowSymlinks(followSymlinks);
	}

	/**
	 * add a "Select" selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addSelector(SelectSelector selector) {
		usedMatchingTask = true;
		super.addSelector(selector);
	}

	/**
	 * add an "And" selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addAnd(AndSelector selector) {
		usedMatchingTask = true;
		super.addAnd(selector);
	}

	/**
	 * add an "Or" selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addOr(OrSelector selector) {
		usedMatchingTask = true;
		super.addOr(selector);
	}

	/**
	 * add a "Not" selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addNot(NotSelector selector) {
		usedMatchingTask = true;
		super.addNot(selector);
	}

	/**
	 * add a "None" selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addNone(NoneSelector selector) {
		usedMatchingTask = true;
		super.addNone(selector);
	}

	/**
	 * add a majority selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addMajority(MajoritySelector selector) {
		usedMatchingTask = true;
		super.addMajority(selector);
	}

	/**
	 * add a selector date entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addDate(DateSelector selector) {
		usedMatchingTask = true;
		super.addDate(selector);
	}

	/**
	 * add a selector size entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addSize(SizeSelector selector) {
		usedMatchingTask = true;
		super.addSize(selector);
	}

	/**
	 * add a selector filename entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addFilename(FilenameSelector selector) {
		usedMatchingTask = true;
		super.addFilename(selector);
	}

	/**
	 * add an extended selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addCustom(ExtendSelector selector) {
		usedMatchingTask = true;
		super.addCustom(selector);
	}

	/**
	 * add a contains selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addContains(ContainsSelector selector) {
		usedMatchingTask = true;
		super.addContains(selector);
	}

	/**
	 * add a present selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addPresent(PresentSelector selector) {
		usedMatchingTask = true;
		super.addPresent(selector);
	}

	/**
	 * add a depth selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addDepth(DepthSelector selector) {
		usedMatchingTask = true;
		super.addDepth(selector);
	}

	/**
	 * add a depends selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addDepend(DependSelector selector) {
		usedMatchingTask = true;
		super.addDepend(selector);
	}

	/**
	 * add a regular expression selector entry on the selector list
	 * 
	 * @param selector
	 *            the selector to be added
	 */
	public void addContainsRegexp(ContainsRegexpSelector selector) {
		usedMatchingTask = true;
		super.addContainsRegexp(selector);
	}

	/**
	 * add the modified selector
	 * 
	 * @param selector
	 *            the selector to add
	 * @since ant 1.6
	 */
	public void addModified(ModifiedSelector selector) {
		usedMatchingTask = true;
		super.addModified(selector);
	}

	/**
	 * add an arbitrary selector
	 * 
	 * @param selector
	 *            the selector to be added
	 * @since Ant 1.6
	 */
	public void add(FileSelector selector) {
		usedMatchingTask = true;
		super.add(selector);
	}

	/**
	 * @return the efg
	 */
	public File getEfg() {
		return efg;
	}

	/**
	 * @param efg
	 *            the efg to set
	 */
	public void setEfg(File efg) {
		this.efg = efg;
	}

	/**
	 * @return the stateDir
	 */
	public File getStateDir() {
		return stateDir;
	}

	/**
	 * @param stateDir
	 *            the stateDir to set
	 */
	public void setStateDir(File stateDir) {
		this.stateDir = stateDir;
	}

	/**
	 * @return the logDir
	 */
	public File getLogDir() {
		return logDir;
	}

	/**
	 * @param logDir
	 *            the logDir to set
	 */
	public void setLogDir(File logDir) {
		this.logDir = logDir;
	}

	/**
	 * @return the gui
	 */
	public File getGui() {
		return gui;
	}

	/**
	 * @param gui
	 *            the gui to set
	 */
	public void setGui(File gui) {
		this.gui = gui;
	}

	public void execute() {
		if (efg == null) {
			throw new BuildException("efg attribute is required", getLocation());
		}

		prepare();

		// Replay a single test case
		if (file != null) {
			if (file.exists()) {
				replayTestCase(file);
			}
		}

		// replay the files in the filesets
		for (int i = 0; i < filesets.size(); i++) {
			FileSet fs = (FileSet) filesets.elementAt(i);
			try {
				DirectoryScanner ds = fs.getDirectoryScanner(getProject());
				String[] files = ds.getIncludedFiles();
				String[] dirs = ds.getIncludedDirectories();
				replayTestSuite(fs.getDir(getProject()), files, dirs);
			} catch (BuildException be) {
				// TODO: Handle exception
			}
		}

		printInfo();

	}

	/**
	 * preparation steps before running scripts
	 * 
	 */
	private void prepare() {
		mkdir(logDir);
		mkdir(stateDir);
	}

	/**
	 * @param dir
	 */
	private void mkdir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
			log("Created dir: " + dir.getAbsolutePath());
		}
	}

	/**
	 * 
	 */
	private void printInfo() {
		System.out.println("== SUMMARY == ");
		System.out.println("GUI file:" + gui.getAbsolutePath());
		System.out.println("EFG file:" + efg.getAbsolutePath());
		System.out.println("State dir:" + stateDir.getAbsolutePath());
		System.out.println("Log dir:" + logDir.getAbsolutePath());
	}

	/**
	 * @param d
	 * @param files
	 * @param dirs
	 */
	private void replayTestSuite(File d, String[] files, String[] dirs) {
		if (files.length > 0) {
			log("Replaying " + files.length + " files from "
					+ d.getAbsolutePath());
			for (int j = 0; j < files.length; j++) {
				File f = new File(d, files[j]);
				if (!replayTestCase(f)) {
					String message = "Unable to replay file "
							+ f.getAbsolutePath();
					log(message);
				}
			}
		}

		if (dirs.length > 0) {
			int dirCount = 0;
			for (int j = dirs.length - 1; j >= 0; j--) {
				File currDir = new File(d, dirs[j]);
				String[] dirFiles = currDir.list();
				if (dirFiles == null || dirFiles.length == 0) {
					if (!replayTestCase(currDir)) {
						String message = "Unable to delete directory "
								+ currDir.getAbsolutePath();
						// if (failonerror) {
						// throw new BuildException(message);
						// } else {
						// log(message, quiet ? Project.MSG_VERBOSE
						// : Project.MSG_WARN);
						// }
					} else {
						dirCount++;
					}
				}
			}

			if (dirCount > 0) {
				log("Deleted " + dirCount + " director"
						+ (dirCount == 1 ? "y" : "ies") + " from "
						+ d.getAbsolutePath());
			}
		}

	}

	/**
	 * @param testcase
	 * @return
	 */
	private boolean replayTestCase(File testcase) {
		String logFile = logDir + File.separator + testcase.getName() + "."
				+ LOG_EXT;
		String stateFile = stateDir + File.separator + testcase.getName() + "."
				+ STATE_EXT;

		CommandLineBuilder clb = new CommandLineBuilder();

		// clb.addArg("JFCReplayerMain");
		clb.addArg("-g", gui.getPath());
		clb.addArg("-e", efg.getPath());
		clb.addArg("-t", testcase.getPath());
		clb.addArg("-l", logFile);
		clb.addArg("-gs", stateFile);
		clb.addArg("-c", classname);
		clb.addArg("-cf", configuration);

		// JFCReplayerJAXBTest test = new JFCReplayerJAXBTest();
		// test.setConf(configuration);
		// test.execute();

		System.out.println("CONFIGURATION: " + configuration);
		System.out.println("TEST CASE: " + configuration);
		

		JFCReplayerMain.main(clb.getArgs());
		return true;
	}
}
