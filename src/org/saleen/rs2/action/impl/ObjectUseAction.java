package org.saleen.rs2.action.impl;

import org.saleen.rs2.action.Action;
import org.saleen.rs2.content.area.Area;
import org.saleen.rs2.content.area.BasicArea;
import org.saleen.rs2.content.object.ObjectAction;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;

/**
 * An implementation of an <code>Action</code> which is used for Objects,
 * checking location to surrounding object locations
 * 
 * @author Nikki
 * 
 */
public class ObjectUseAction extends Action {

	/**
	 * The object this action is checking
	 */
	private GameObject object;

	/**
	 * The array of locations, used when checking
	 */
	private Area bounds;

	/**
	 * The action to call when the player is close enough
	 */
	private ObjectAction action;

	/**
	 * Create the action
	 * 
	 * @param player
	 *            The player to create for
	 * @param object
	 *            The object to use
	 * @param action
	 *            The action to call after finished
	 */
	public ObjectUseAction(Player player, GameObject object, ObjectAction action) {
		super(player, 600);
		this.object = object;
		this.action = action;
	}

	/**
	 * Initialize the object locations
	 */
	public void init() {
		int posX = object.getLocation().getX();
		int posY = object.getLocation().getY();

		int sizeX = object.getDefinition().getSizeX();
		int sizeY = object.getDefinition().getSizeY();
		if (sizeX != 1 || sizeY != 1) {
			// finalRotation - 0 = west, 1 = north, 2 = east, 3 =
			// south
			switch (object.getRotation()) {
			case 0:
				sizeX = -sizeX;
				break;
			case 3:
				sizeY = -sizeY;
				break;
			}
		}

		Location origLoc = Location.create(posX - 1, posY - 1);
		Location otherLoc = Location.create(posX + sizeX, posY + sizeY);

		boolean reversed = otherLoc.getX() < posX && otherLoc.getY() < posY;

		bounds = new BasicArea(reversed ? otherLoc : origLoc,
				reversed ? origLoc : otherLoc);
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.WALKABLE;
	}

	@Override
	public void execute() {
		if (bounds == null) {
			init();
		}
		if (bounds.contains(player.getLocation())
				&& player.getWalkingQueue().isEmpty()) {
			player.face(object.getLocation());
			action.useObject();
			this.stop();
		}
	}
}
