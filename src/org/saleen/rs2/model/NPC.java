package org.saleen.rs2.model;

import org.saleen.cache.npc.CacheNPCDefinition;
import org.saleen.rs2.content.combat.Damage.Hit;
import org.saleen.rs2.event.impl.DeathEvent;
import org.saleen.rs2.model.UpdateFlags.UpdateFlag;
import org.saleen.rs2.model.definition.NPCCombatDefinition;
import org.saleen.rs2.model.definition.NPCDefinition;
import org.saleen.rs2.model.region.Region;

/**
 * <p>
 * Represents a non-player character in the in-game world.
 * </p>
 * 
 * @author Graham Edgecombe
 * 
 */
public class NPC extends Entity {

	/**
	 * The definition.
	 */
	private final NPCDefinition definition;

	/**
	 * The definition from the cache
	 */
	private final CacheNPCDefinition gamedef;

	/**
	 * The combat definition of this npc
	 */
	private final NPCCombatDefinition combatdef;

	/**
	 * The current health of this npc
	 */
	private int health;

	/**
	 * The current chat string
	 */
	private String forceChat = "";

	/**
	 * The NPC id to transform to
	 */
	private int transformTo;

	/**
	 * The direction this npc is facing
	 */
	private Face faceDirection;

	/**
	 * The bottom left coordinate of this range
	 */
	private Location rangeBottomLeft;

	/**
	 * The bottom left coordinate of this range
	 */
	private Location rangeTopRight;

	/**
	 * Creates the NPC with the specified definition.
	 * 
	 * @param definition
	 *            The definition.
	 */
	public NPC(NPCDefinition definition) {
		super();
		this.definition = definition;
		this.gamedef = CacheNPCDefinition.forId(definition.getId());
		this.combatdef = NPCCombatDefinition.forId(definition.getId());
		this.health = combatdef.getMaxHealth();
	}

	/**
	 * Gets the NPC definition.
	 * 
	 * @return The NPC definition.
	 */
	public NPCDefinition getDefinition() {
		return definition;
	}

	/**
	 * Gets the Cache definition
	 * 
	 * @return The NPC Cache definition
	 */
	public CacheNPCDefinition getGameDef() {
		return gamedef;
	}

	public NPCCombatDefinition getCombatDefinition() {
		return combatdef;
	}

	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}

	@Override
	public void inflict(Hit hit) {
		if (!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(hit);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if (!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(hit);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		health -= hit.getDamage();
		if (health <= 0) {
			if (!this.isDead()) {
				World.getWorld().submit(new DeathEvent(this));
			}
			this.setDead(true);
		}
	}

	public String getForceChat() {
		return forceChat;
	}

	public void requestForceChat(String text) {
		this.forceChat = text;
		this.getUpdateFlags().flag(UpdateFlag.FORCED_CHAT);
	}

	/**
	 * Get the npc to transform to
	 * 
	 * @return The npc id
	 */
	public int getTransformTo() {
		return transformTo;
	}

	/**
	 * Transform this npc into a new npc
	 * 
	 * @param npcid
	 *            The npc id
	 */
	public void transform(int npcid) {
		this.transformTo = npcid;
		this.getUpdateFlags().flag(UpdateFlag.TRANSFORM);
	}

	/**
	 * Get this NPC's health
	 * 
	 * @return The current health remaining
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Set the health of this npc back to max
	 */
	public void resetHealth() {
		this.health = this.getCombatDefinition().getMaxHealth();
	}

	/**
	 * Set the face direction with the index of the direction
	 * 
	 * @param face
	 */
	public void setFace(int face) {
		this.faceDirection = Face.values()[face];
	}

	/**
	 * Send the mask for this npc to face a location
	 */
	public void updateFaceDir() {
		if (faceDirection != null)
			face(location.transform(faceDirection.getDiffX(),
					faceDirection.getDiffY(), 0));
	}

	public enum Face {
		NORTH(0, 1), NORTHEAST(1, 1), EAST(1, 0), SOUTHEAST(1, -1), SOUTH(0, -1), SOUTHWEST(
				-1, -1), WEST(-1, 0), NORTHWEST(1, -1);

		private int diffX;
		private int diffY;

		/**
		 * Create a face direction
		 * 
		 * @param diffX
		 *            The x difference
		 * @param diffY
		 *            The y difference
		 */
		private Face(int diffX, int diffY) {
			this.diffX = diffX;
			this.diffY = diffY;
		}

		/**
		 * The difference in the x coordinate
		 * 
		 * @return The difference
		 */
		public int getDiffX() {
			return diffX;
		}

		/**
		 * The difference in the y coordinate
		 * 
		 * @return The difference
		 */
		public int getDiffY() {
			return diffY;
		}
	}

	/**
	 * Check if the npc can walk
	 * 
	 * @return True, if it can walk
	 */
	public boolean canWalk() {
		return rangeBottomLeft != null && !rangeBottomLeft.equals(location)
				&& rangeTopRight != null && !rangeTopRight.equals(location);
	}

	/**
	 * Set the southwest range of this npc
	 * 
	 * @param location
	 *            The southwest location
	 */
	public void setRangeBottomLeft(Location location) {
		this.rangeBottomLeft = location;
	}

	/**
	 * Set the northeast range of this npc
	 * 
	 * @param location
	 *            The northeast location
	 */
	public void setRangeTopRight(Location location) {
		this.rangeTopRight = location;
	}

	/**
	 * Get the southwest range
	 * 
	 * @return The southwest location
	 */
	public Location getRangeBottomLeft() {
		return rangeBottomLeft;
	}

	/**
	 * Get the northeast range
	 * 
	 * @return The northeast location
	 */
	public Location getRangeTopRight() {
		return rangeTopRight;
	}
}
