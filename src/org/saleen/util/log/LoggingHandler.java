package org.saleen.util.log;

import java.io.FileInputStream;
import java.util.logging.LogManager;

/**
 * A logging implementation loader...
 * 
 * @author Nikki
 * 
 */
public class LoggingHandler {

	/**
	 * Read the configuration from the logging property file
	 */
	public static void setup() {
		try {
			LogManager.getLogManager().readConfiguration(
					new FileInputStream("conf/logging.conf"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
