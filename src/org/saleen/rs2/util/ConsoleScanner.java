package org.saleen.rs2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.saleen.rs2.model.World;

/**
 * A simple class to read data from the commandline console
 * 
 * @author Nikki
 * 
 */
public class ConsoleScanner implements Runnable {

	public ConsoleScanner() {

	}

	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			try {
				String line = reader.readLine();
				if (line == null) {
					continue;
				}
				World.getWorld().consoleCommand(line);
				if (line.equalsIgnoreCase("exit")) {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
