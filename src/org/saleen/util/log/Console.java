package org.saleen.util.log;

import org.saleen.util.Misc;

/**
 * A simple console class which can change title :)
 * 
 * @author Nikki
 * 
 */
public class Console {

	static {
		if (Misc.isWindows()) {
			System.loadLibrary("console");
		}
	}

	/**
	 * Sets the console title via the 2 methods, 1 for windows, 1 for unix/mac
	 * 
	 * @param s
	 */
	public static void setTitle(String s) {
		if (Misc.isWindows()) {
			setTitleWindows(s);
		} else if (Misc.isUnix() || Misc.isMacOS()) {
			setTitleUnix(s);
		}
	}

	/**
	 * A native function defined by JNI
	 * 
	 * @param s
	 *            The string to set
	 */
	private static native void setTitleWindows(String s);

	/**
	 * A method which sets the UNIX title, ssh or terminal(?)
	 * 
	 * @param s
	 *            The string to set
	 */
	private static void setTitleUnix(String s) {
		System.out.printf("%c]0;%s%c", '\033', s, '\007');
	}
}
