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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

/**
 * @author arlt
 */
public class FileIO {

	/**
	 * Reads text from a file
	 * 
	 * @param filename
	 *            File name
	 * @return null = failure
	 */
	public static String fromFile(String filename) {
		String s = null;
		try {
			StringBuffer sb = new StringBuffer();
			FileReader fr = new FileReader(new File(filename));
			BufferedReader br = new BufferedReader(fr);

			while (null != (s = br.readLine())) {
				sb.append(s + "\r\n");
			}

			s = sb.toString();
			fr.close();

		} catch (IOException e) {
			e.printStackTrace();
			s = null;
		}
		return s;
	}

	/**
	 * Writes text to a file
	 * 
	 * @param text
	 *            Text
	 * @param filename
	 *            File name
	 * @return false = failure
	 */
	public static boolean toFile(String text, String filename) {
		boolean success = false;
		try {
			FileWriter fw = new FileWriter(new File(filename));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
			fw.close();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * Checks if the directory exists
	 * 
	 * @param dirname
	 *            directory name
	 * @return true = directory does exist
	 */
	public static boolean doesDirectoryExist(String dirname) {
		if (null == dirname) {
			return false;
		}

		File file = new File(dirname);
		return file.exists() && file.isDirectory();
	}

	/**
	 * Checks if the file exists
	 * 
	 * @param filename
	 *            File name
	 * @return true = file does exist
	 */
	public static boolean doesFileExist(String filename) {
		if (null == filename) {
			return false;
		}

		File file = new File(filename);
		return file.exists() && file.isFile();
	}

	/**
	 * Checks if the file's path exists
	 * 
	 * @param filename
	 *            File name
	 * @return true = file's path does exist
	 */
	public static boolean doesDirectoryOfFileExist(String filename) {
		if (null == filename) {
			return false;
		}

		File file = new File(filename);
		File fileParent = file.getParentFile();

		if (null == fileParent) {
			return false;
		}

		return fileParent.exists();
	}

	/**
	 * Generates a temporary filename
	 * 
	 * @param ext
	 *            Extension of the filename
	 * @return Temporary filename
	 */
	public static String generateTempFileName(String ext) {
		return String.format("%s%s.%s", System.getProperty("java.io.tmpdir"),
				UUID.randomUUID().toString(), ext);
	}

	/**
	 * Returns a string resource
	 * 
	 * @param clazz
	 *            Class
	 * @param name
	 *            Name
	 * @return String resource
	 */
	public static String getResource(Class<?> clazz, String name) {
		URL url = clazz.getResource(name);
		return FileIO.fromFile(url.getFile());
	}

}
