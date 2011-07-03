package org.saleen.rs2.event.impl;

import org.saleen.rs2.event.Event;
import org.saleen.rs2.model.Animation;
import org.saleen.rs2.model.Entity;
import org.saleen.rs2.model.NPC;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Skills;

/**
 * The death event handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 * 
 * @author Graham
 * 
 */
public class DeathEvent extends Event {

	private Entity entity;

	/**
	 * Creates the death event for the specified entity.
	 * 
	 * @param entity
	 *            The player or npc whose death has just happened.
	 */
	public DeathEvent(Entity entity) {
		super(3500);
		this.entity = entity;
		entity.playAnimation(Animation.create(836));
	}

	@Override
	public void execute() {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			p.getSkills().setLevel(Skills.HITPOINTS,
					p.getSkills().getLevelForExperience(Skills.HITPOINTS));
			entity.setDead(false);
			entity.setTeleportTarget(Entity.DEFAULT_LOCATION);
			p.getActionSender().sendMessage("Oh dear, you are dead!");
			this.stop();
		} else if (entity instanceof NPC) {
			NPC npc = (NPC) entity;
			entity.setDead(false);
			npc.resetHealth();
			this.stop();
		}
	}

}