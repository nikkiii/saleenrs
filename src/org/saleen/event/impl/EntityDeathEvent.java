package org.saleen.event.impl;

import org.saleen.event.Event;
import org.saleen.rs2.model.Entity;

/**
 * Represents a player or npc death
 * 
 * @author Nikki
 *
 */
public class EntityDeathEvent implements Event {
	
	/**
	 * The entity
	 */
	private Entity entity;
	
	/**
	 * Create a new event..
	 * @param entity
	 * 			The entity
	 */
	public EntityDeathEvent(Entity entity) {
		this.entity = entity;
	}
	
	/**
	 * Get the entity
	 * @return
	 */
	public Entity getEntity() {
		return entity;
	}
}
