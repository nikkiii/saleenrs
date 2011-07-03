package org.saleen.rs2.event.impl;

import org.saleen.rs2.content.combat.Damage.Hit;
import org.saleen.rs2.model.Entity;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;

public class CannonHitEvent extends DelayedHitEvent {

	private Location location;

	public CannonHitEvent(Location location, long delay, Entity entity, Hit hit) {
		super(delay, entity, hit);
		this.location = location;
	}

	@Override
	public void execute() {
		if (getDelay() == 200) {
			for (Player player : entity.getRegion().getPlayers())
				player.getActionSender().sendProjectile(location,
						entity.getLocation(), 30, 53, 50, 38, 38, 40, entity);
			this.setDelay(500);
		} else {
			super.execute();
		}
	}

}
