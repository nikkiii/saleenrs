package org.saleen.rs2.packet;

import org.saleen.event.ConsumerInterruptor;
import org.saleen.event.EventProducer;
import org.saleen.event.impl.ActionButtonEvent;
import org.saleen.rs2.content.skills.Prayers.Prayer;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.container.Bank;
import org.saleen.rs2.net.Packet;

/**
 * Handles clicking on most buttons in the interface.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ActionButtonPacketHandler extends EventProducer implements
		PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		final int button = packet.getShort();

		// First send to the consumers, see if they want the event
		try {
			produce(new ActionButtonEvent(player, button));
		} catch (ConsumerInterruptor e) {
			// If they do, they will interrupt the chain
			return;
		}
		// If not, we continue on!
		Prayer prayer = Prayer.forButton(button);
		if (prayer != null) {
			player.getPrayer().togglePrayer(prayer);
			return;
		}
		switch (button) {
		case 19158:
			player.getWalkingQueue().setRunningToggled(
					player.getWalkingQueue().isRunningToggled() ? false : true);
			break;
		case 2458:
			player.getActionSender().sendLogout();
			break;
		case 5387:
			player.getSettings().setWithdrawAsNotes(
					!player.getSettings().isWithdrawingAsNotes());
			break;
		case 5386:
			Bank.depositAll(player);
			break;
		case 8130:
			player.getSettings().setSwapping(true);
			break;
		case 8131:
			player.getSettings().setSwapping(false);
			break;
		case 18132: // Leave clan
			break;
		default:
			break;
		}
	}
}
