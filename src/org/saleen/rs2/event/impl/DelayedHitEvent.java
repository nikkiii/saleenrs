package org.saleen.rs2.event.impl;

import org.saleen.rs2.content.combat.Damage.Hit;
import org.saleen.rs2.event.Event;
import org.saleen.rs2.model.Entity;

/**
 * An event which hits an entity at a delayed time
 * 
 * @author Nikki
 * 
 */
public class DelayedHitEvent extends Event {

	/**
	 * The entity to hit
	 */
	protected Entity entity;

	/**
	 * The hit to inflict
	 */
	private Hit hit;

	public DelayedHitEvent(long delay, Entity entity, Hit hit) {
		super(delay);
		this.entity = entity;
		this.hit = hit;
	}

	@Override
	public void execute() {
		entity.inflict(hit);
		this.stop();
	}
}
