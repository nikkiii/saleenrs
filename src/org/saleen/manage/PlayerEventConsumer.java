package org.saleen.manage;

import org.json.JSONException;
import org.json.JSONObject;
import org.saleen.event.Event;
import org.saleen.event.EventConsumer;
import org.saleen.event.impl.PlayerLoginEvent;
import org.saleen.rs2.model.Player;
import org.saleen.util.Filter;

public class PlayerEventConsumer extends EventConsumer {

	/**
	 * The management console instance
	 */
	private ManagementConsole console;

	public PlayerEventConsumer(ManagementConsole console) {
		this.console = console;
	}

	@Override
	public void consume(Event event) {
		if (event instanceof PlayerLoginEvent) {
			processLoginEvent((PlayerLoginEvent) event);
		}
	}

	private void processLoginEvent(PlayerLoginEvent event) {
		try {
			JSONObject object = new JSONObject();
			Player player = event.getPlayer();
			object.put("player", player.getName());
			object.put("rights", player.getRights().toInteger());
			object.put("event", event.isPlayerLogin() ? "login" : "logout");
			console.write(ManagementChannels.construct(
					ManagementChannels.PLAYERS, object),
					new Filter<ManagementSession>() {
						@Override
						public boolean accept(ManagementSession t) {
							return t.isSubscribedTo("players");
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
