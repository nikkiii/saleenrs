package org.saleen.rs2.eventlistener.impl;

import java.util.Iterator;

import org.saleen.event.Event;
import org.saleen.event.EventConsumer;
import org.saleen.event.impl.PlayerLoginEvent;
import org.saleen.event.impl.PlayerLoginEvent.PlayerLogin;
import org.saleen.event.impl.PlayerLoginEvent.PlayerLogout;
import org.saleen.rs2.content.chat.FriendStatus;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;

/**
 * An <code>EventConsumer</code> which binds to the player login/logout events
 * to update friend statuses.
 * 
 * @author Nikki
 */
public class FriendLoginListener extends EventConsumer {

	@SuppressWarnings("unchecked")
	public FriendLoginListener() {
		bind(PlayerLogin.class, PlayerLogout.class);
	}

	@Override
	public void consume(Event event) {
		PlayerLoginEvent evt = (PlayerLoginEvent) event;
		if (evt.isPlayerLogin()) {
			// Player logged in, update friends!
			long name = evt.getPlayer().getNameAsLong();
			evt.getPlayer().getFriendsList().updateStatuses();
			Iterator<Player> it$ = World.getWorld().getPlayers().iterator();
			while (it$.hasNext()) {
				Player player2 = it$.next();
				if (player2.getFriendsList().contains(name)) {
					player2.getFriendsList().updateStatus(name);
				}
			}
		} else {
			// Player logged out, set status offline!
			long name = evt.getPlayer().getNameAsLong();
			Iterator<Player> it$ = World.getWorld().getPlayers().iterator();
			while (it$.hasNext()) {
				Player player2 = it$.next();
				if (player2.getFriendsList().contains(name)) {
					player2.getFriendsList().setStatus(name,
							FriendStatus.OFFLINE);
				}
			}
		}
	}
}
