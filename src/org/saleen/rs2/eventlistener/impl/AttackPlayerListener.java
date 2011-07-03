package org.saleen.rs2.eventlistener.impl;

import org.saleen.event.Event;
import org.saleen.event.EventConsumer;
import org.saleen.event.impl.ClickOption;
import org.saleen.event.impl.PlayerOptionEvent;
import org.saleen.event.impl.PlayerOptionEvent.PlayerOption1;
import org.saleen.rs2.action.impl.AttackAction;
import org.saleen.rs2.content.area.Area;
import org.saleen.rs2.content.area.Areas;
import org.saleen.rs2.model.Player;

/**
 * An event consumer which will handle the player attack option
 * 
 * @author Nikki
 * 
 */
public class AttackPlayerListener extends EventConsumer {

	@SuppressWarnings("unchecked")
	public AttackPlayerListener() {
		bind(PlayerOption1.class);
	}

	@Override
	public void consume(Event event) {
		PlayerOptionEvent evt = (PlayerOptionEvent) event;
		if (evt.isOption(ClickOption.CLICK_1)) {
			Player player = evt.getPlayer();
			Player victim = evt.getInteractingPlayer();

			Area area = Areas.get("wilderness");
			if (area.contains(player.getLocation())
					&& area.contains(victim.getLocation())) {
				player.getActionQueue().addAction(
						new AttackAction(player, victim));
			} else {
				player.getActionSender()
						.sendMessage(
								"You cannot attack other players unless you are in the wilderness!");
			}
		}
	}
}
