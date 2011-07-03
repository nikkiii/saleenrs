package org.saleen.util.log;

import java.util.logging.LogRecord;

import org.json.JSONException;
import org.json.JSONObject;
import org.saleen.manage.ManagementConsole;

/**
 * An implementation of Java's <code>Handler</code> which logs messages to the
 * IoAcceptor sessions with data being a JSON Object
 * 
 * @author Nikki
 * 
 */
public class ManagementLoggerHandler extends java.util.logging.Handler {

	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		try {
			JSONObject logObject = new JSONObject();
			logObject.put("loggername", record.getLoggerName());
			logObject.put("sourceclass", record.getSourceClassName());
			logObject.put("sourcemethod", record.getSourceMethodName());
			logObject.put("level", record.getLevel().getLocalizedName());
			logObject.put("message", record.getMessage());
			logObject.put("time", record.getMillis());
			ManagementConsole.getInstance().logRecord(logObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void flush() {
		// Nothing!
	}

	@Override
	public void close() throws SecurityException {
		// Nothing!
	}
}
