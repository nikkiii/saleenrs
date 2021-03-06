package org.saleen.rs2.task.impl;

import org.saleen.rs2.GameEngine;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.task.Task;

/**
 * A task that is executed when a player has logged in.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ChannelLoginTask implements Task {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates the session login task.
	 * 
	 * @param player
	 *            The player that logged in.
	 */
	public ChannelLoginTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		World.getWorld().register(player);
	}

}
