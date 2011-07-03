package org.saleen;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.saleen.fileserver.FileServer;
import org.saleen.manage.ManagementConsole;
import org.saleen.rs2.Constants;
import org.saleen.rs2.RS2Server;
import org.saleen.rs2.model.World;
import org.saleen.util.log.Console;
import org.saleen.util.log.LoggingHandler;

/**
 * A class to start both the file and game servers.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Server {

	/**
	 * The protocol version.
	 */
	public static final int VERSION = 317;

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Server.class
			.getName());

	/**
	 * The entry point of the application.
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String[] args) {
		Console.setTitle("Saleen - Starting up");
		printLogo();
		LoggingHandler.setup();
		logger.info("Starting " + Constants.SERVER_NAME + "...");
		World.getWorld(); // this starts off background loading
		ManagementConsole.setup();
		try {
			new FileServer().bind().start();
			RS2Server.getInstance().init().bind(RS2Server.PORT).start();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error starting " + Constants.SERVER_NAME
					+ ".", ex);
			System.exit(1);
		}
	}

	public static void printLogo() {
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(
					"conf/logo.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			reader.close();
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
