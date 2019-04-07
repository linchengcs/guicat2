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

import java.io.File;

import edu.wmich.cs.carot.util.Olog;
import soot.PackManager;
import soot.Transform;

/**
 * @author arlt
 */
public class CTSootRunner {

	/**
	 * Body Transformer
	 */
	CTBodyTransformer bodyTransformer;

	/**
	 * Executes the Soot runner
	 * 
	 * 
	 * @param scope
	 *            Scope (usually a JAR file or a directory)
	 * @param classpath
	 *            Classpath
	 * @param pakkage
	 *            Package to be analyzed
	 * @return Body Transformer or null
	 */
	public CTBodyTransformer run(String scope, String classpath, String pakkage) {
		try {
			// create body transformer object
			bodyTransformer = new CTBodyTransformer();

			// set filter (that is, a package)
			bodyTransformer.setPackage(pakkage);

			// configure body transformer
			soot.G.reset();
			PackManager
					.v()
					.getPack("jtp")
					.add(new Transform("jtp.CTBodyTransformer", bodyTransformer));

			// run Soot (that is, the body transformer)
			soot.Main.main(new String[] { "-output-format",
					"J",
			//		"-i",
			//		"javax.swing.",
					"-allow-phantom-refs",
					"-pp",
					"-cp",
					buildClasspath(scope, classpath), "-process-dir", scope });

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

//		bodyTransformer.printStatistics();
//		bodyTransformer.printData();
		return bodyTransformer;
	}

	/**
	 * Builds the classpath for the Soot run
	 * 
	 * @param scope
	 *            Scope
	 * @param classpath
	 *            Classpath
	 * @return Classpath
	 */
	protected String buildClasspath(String scope, String classpath) {
		// add scope to classpath
		StringBuilder cp = new StringBuilder();
		cp.append(scope + File.pathSeparator);
		if (null != classpath)
			cp.append(classpath + File.pathSeparator);

		// add JRE libs to classpath
		cp.append(new File(new File(System.getProperty("java.home"), "lib"),
				"rt.jar").getPath() + File.pathSeparator);
		cp.append(new File(new File(System.getProperty("java.home"), "lib"),
				"jce.jar").getPath() + File.pathSeparator);

	//	Olog.log.info("static ct classpath: " + cp.toString());
		return cp.toString();
	}

}
