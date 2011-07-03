package org.saleen.rs2.packet;

import org.saleen.rs2.model.Player;
import org.saleen.rs2.net.Packet;

/**
 * A packet handler that is called when an interface is closed.
 * 
 * @author Graham Edgecombe
 * 
 */
public class CloseInterfacePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		player.getInterfaceState().interfaceClosed();
	}
}
