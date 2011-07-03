package org.saleen.rs2.packet;

import org.saleen.event.EventProducer;
import org.saleen.event.impl.RegionChangeEvent;
import org.saleen.rs2.model.GroundItem;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.net.Packet;

/**
 * An implementation of a <code>PacketHandler</code> which handles when regions
 * are loaded
 * 
 * @author Nikki
 * 
 */
public class RegionChangePacketHandler extends EventProducer implements
		PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		for (GroundItem item : player.getRegion().getGroundItems()) {
			if (item.isGlobal()
					|| (item.getDropper() != null && item.getDropper() == player)) {
				if (player.getLocation().isWithinDistance(item.getLocation())) {
					player.getActionSender().addGroundItem(item);
				}
			}
		}
		produce(new RegionChangeEvent(player));
	}
}
