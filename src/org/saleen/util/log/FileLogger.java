package org.saleen.util.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class FileLogger extends java.util.logging.Handler {

	/**
	 * The buffered writer for this logger
	 */
	private BufferedWriter writer;

	/**
	 * A simple file logger!
	 */
	public FileLogger() {
		configure();
	}

	/**
	 * Configure the logger
	 */
	private void configure() {
		LogManager manager = LogManager.getLogManager();
		String name = getClass().getName();
		String file = manager.getProperty(name + ".file");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void publish(LogRecord record) {
		if (getFormatter() == null)
			setFormatter(new SimpleFormatter());
		if (!isLoggable(record)) {
			return;
		}
		try {
			writer.write(getFormatter().format(record));
		} catch (IOException e) {
			e.printStackTrace();
		}
		flush();
	}

	@Override
	public void flush() {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws SecurityException {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
