package org.saleen.rs2.packet;

import org.saleen.rs2.content.skills.impl.BasicLevelRequirement;
import org.saleen.rs2.content.skills.impl.CompositeLevelRequirement;
import org.saleen.rs2.content.skills.impl.LevelRequirement;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Skills;
import org.saleen.rs2.model.container.Equipment;
import org.saleen.rs2.model.container.Equipment.EquipmentType;
import org.saleen.rs2.model.container.Inventory;
import org.saleen.rs2.model.definition.EquipmentDefinition;
import org.saleen.rs2.net.Packet;

/**
 * Handles the 'wield' option on items.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WieldPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		int slot = packet.getShortA() & 0xFFFF;
		int interfaceId = packet.getShortA() & 0xFFFF;

		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Item item = player.getInventory().get(slot);
				if (item != null && item.getId() == id) {
					EquipmentDefinition definition = item
							.getEquipmentDefinition();
					if (definition == null) {
						return;
					}
					if (definition.getRequirements() != null) {
						LevelRequirement requirement = definition
								.getRequirements();
						if (!requirement.hasRequirements(player)) {
							BasicLevelRequirement missing;
							if (requirement instanceof CompositeLevelRequirement) {
								missing = ((CompositeLevelRequirement) requirement)
										.getMissingRequirement(player);
								player.getActionSender().sendMessage(
										"You need level "
												+ missing.getRequiredLevel()
												+ " "
												+ Skills.SKILL_NAME[missing
														.getSkillId()]
												+ " to wield this!");
							} else {
								missing = (BasicLevelRequirement) requirement;
								player.getActionSender().sendMessage(
										"You need level "
												+ missing.getRequiredLevel()
												+ " "
												+ Skills.SKILL_NAME[missing
														.getSkillId()]
												+ " to wield this!");
							}
							return;
						}
					}
					EquipmentType type = Equipment.getType(item);
					Item oldEquip = null;
					boolean stackable = false;
					if (player.getEquipment().isSlotUsed(type.getSlot())
							&& !stackable) {
						oldEquip = player.getEquipment().get(type.getSlot());
						player.getEquipment().set(type.getSlot(), null);
					}
					player.getInventory().set(slot, null);
					if (oldEquip != null) {
						player.getInventory().add(oldEquip);
					}
					if (!stackable) {
						player.getEquipment().set(type.getSlot(), item);
					} else {
						player.getEquipment().add(item);
					}
				}
			}
			break;
		}
	}

}
