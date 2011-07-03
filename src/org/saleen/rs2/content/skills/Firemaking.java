package org.saleen.rs2.content.skills;

import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.pf.Tile;
import org.saleen.rs2.pf.TileMap;
import org.saleen.rs2.pf.TileMapBuilder;

public class Firemaking {

	private static final int RADIUS = 1;

	public enum MoveDirection {
		EAST, WEST, NONE
	}

	public static MoveDirection direction(Player player) {
		TileMapBuilder bldr = new TileMapBuilder(player.getLocation(), RADIUS);

		TileMap map = bldr.build();

		Tile tile = map.getTile(RADIUS, RADIUS);
		Tile westTile = map.getTile(RADIUS - 1, RADIUS);
		Tile eastTile = map.getTile(RADIUS + 1, RADIUS);

		if (tile.isWesternTraversalPermitted()
				&& westTile.isEasternTraversalPermitted()) {
			return MoveDirection.WEST;
		} else if (tile.isEasternTraversalPermitted()
				&& eastTile.isWesternTraversalPermitted()) {
			return MoveDirection.EAST;
		} else {
			return MoveDirection.NONE;
		}
	}

	public static void lightFire(Player player, int slot, int logs) {
		player.getInventory().remove(slot, new Item(logs, 1));

		MoveDirection direction = direction(player);
		if (direction == MoveDirection.WEST) {
			player.getWalkingQueue().addStep(player.getLocation().getX() - 1,
					player.getLocation().getY());
		} else if (direction == MoveDirection.EAST) {
			player.getWalkingQueue().addStep(player.getLocation().getX() + 1,
					player.getLocation().getY());
		} else {
			// Cannot move!
		}
	}
}
