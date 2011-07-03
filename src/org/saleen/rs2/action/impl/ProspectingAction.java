package org.saleen.rs2.action.impl;

import org.saleen.rs2.action.impl.MiningAction.Node;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.definition.ItemDefinition;

/**
 * A prospecting action, called when a user inspects a rock
 * 
 * @author Nikki
 */
public class ProspectingAction extends InspectAction {

	/**
	 * The node type.
	 */
	private Node node;

	/**
	 * The delay.
	 */
	private static final int DELAY = 3000;

	/**
	 * Constructor.
	 * 
	 * @param player
	 * @param location
	 * @param node
	 */
	public ProspectingAction(Player player, Location location, Node node) {
		super(player, location);
		this.node = node;
	}

	@Override
	public long getInspectDelay() {
		return DELAY;
	}

	@Override
	public void giveRewards(Player player) {
		if (node == Node.EMPTY) {
			player.getActionSender().sendMessage(
					"There is no ore currently available in this rock.");
		} else {
			player.getActionSender().sendMessage(
					"This rock contains "
							+ ItemDefinition.forId(node.getOreId()).getName()
									.toLowerCase().replaceAll("ore", "").trim()
							+ ".");
		}
	}

	@Override
	public void init() {
		final Player player = getPlayer();
		player.getActionSender()
				.sendMessage("You examine the rock for ores...");
	}

}