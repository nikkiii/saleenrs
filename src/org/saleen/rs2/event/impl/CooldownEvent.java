package org.saleen.rs2.event.impl;

import org.saleen.rs2.event.Event;
import org.saleen.rs2.model.Entity;
import org.saleen.rs2.model.EntityCooldowns.CooldownFlags;

/**
 * This event handles the expiry of a cooldown.
 * 
 * @author Brett Russell
 * 
 */
public class CooldownEvent extends Event {

	private Entity entity;

	private CooldownFlags cooldown;

	/**
	 * Creates a cooldown event for a single CooldownFlag.
	 * 
	 * @param entity
	 *            The entity for whom we are expiring a cooldown.
	 * @param duration
	 *            The length of the cooldown.
	 */
	public CooldownEvent(Entity entity, CooldownFlags cooldown, int duration) {
		super(duration);
		this.entity = entity;
		this.cooldown = cooldown;
	}

	@Override
	public void execute() {
		entity.getEntityCooldowns().set(cooldown, false);
		this.stop();
	}

}
