package org.saleen.rs2.content.object;

import org.saleen.rs2.action.impl.WoodcuttingAction;
import org.saleen.rs2.action.impl.WoodcuttingAction.Tree;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;

public class TreeObjectAction implements ObjectAction {

	private Player player;
	private Location location;
	private Tree tree;

	public TreeObjectAction(Player player, Location location, Tree tree) {
		this.player = player;
		this.location = location;
		this.tree = tree;
	}

	@Override
	public void useObject() {
		player.getActionQueue().addAction(
				new WoodcuttingAction(player, location, tree));
	}

}
