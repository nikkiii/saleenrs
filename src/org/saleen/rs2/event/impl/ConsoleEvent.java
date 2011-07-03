package org.saleen.rs2.event.impl;

import java.text.NumberFormat;

import org.saleen.rs2.event.Event;
import org.saleen.rs2.model.World;
import org.saleen.util.log.Console;

public class ConsoleEvent extends Event {

	private static NumberFormat format = NumberFormat.getInstance();

	public ConsoleEvent() {
		super(5000);
	}

	@Override
	public void execute() {
		long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
				.freeMemory());
		Console.setTitle("Saleen - Threads: " + Thread.activeCount() + " Mem: "
				+ format.format(used / 1024) + "K Players online: "
				+ World.getWorld().getPlayers().size());
	}
}
