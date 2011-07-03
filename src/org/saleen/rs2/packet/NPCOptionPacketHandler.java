package org.saleen.rs2.packet;

import org.saleen.event.EventProducer;
import org.saleen.event.impl.NPCOptionEvent.NPCOption1;
import org.saleen.event.impl.NPCOptionEvent.NPCOption2;
import org.saleen.rs2.action.impl.AttackAction;
import org.saleen.rs2.model.NPC;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.Packet;

public class NPCOptionPacketHandler extends EventProducer implements
		PacketHandler {

	private static final int ATTACK = 72, OPTION_1 = 155, OPTION_2 = 17;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case ATTACK:
			handleAttack(player, packet);
			break;
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		}
	}

	private void handleAttack(Player player, Packet packet) {
		int index = packet.getShortA();
		NPC npc = World.getWorld().getNPC(index);
		npc.setInteractingEntity(player);
		player.getActionQueue().addAction(new AttackAction(player, npc));
	}

	private void handleOption1(Player player, Packet packet) {
		int index = packet.getLEShort();
		NPC npc = World.getWorld().getNPC(index);
		// Produce an event for the option
		produce(new NPCOption1(player, npc));
	}

	private void handleOption2(Player player, Packet packet) {
		int index = packet.getLEShortA();
		NPC npc = World.getWorld().getNPC(index);
		// Produce an event for the option
		produce(new NPCOption2(player, npc));
	}
}
