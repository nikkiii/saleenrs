package org.saleen.rs2.content.combat;

import java.util.Random;

import org.saleen.rs2.content.skills.Prayers;
import org.saleen.rs2.content.skills.Prayers.Prayer;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Skills;
import org.saleen.rs2.model.container.Equipment;

public class CombatCalculations {
	private static final Random random = new Random();

	public static int calculateMeleeHit(Player player) {
		return random.nextInt(calculateMaxMeleeHit(player));
	}

	public static int calculateMaxMeleeHit(Player player) {
		double prayerBonus = 1.0;
		Prayer prayer = player.getPrayer().get(Prayers.STRENGTH_PRAYER);
		if (prayer != null) {
			switch (prayer) {
			case BURST_OF_STRENGTH:
				prayerBonus = 1.05;
				break;
			case SUPERHUMAN_STRENGTH:
				prayerBonus = 1.1;
				break;
			case ULTIMATE_STRENGTH:
				prayerBonus = 1.15;
				break;
			case CHIVALRY:
				prayerBonus = 1.18;
				break;
			case PIETY:
				prayerBonus = 1.23;
				break;
			}
		}
		int effective = 8 + (int) (player.getSkills().getLevel(Skills.STRENGTH) * prayerBonus);

		int max = (int) (5 + effective * (1 + (player.getStrengthBonus()) / 64));

		double specialBonus = 1.0;

		boolean special = false;
		if (special) {
			Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
			if (weapon != null) {
				switch (weapon.getId()) {
				case 11694: // Armadyl godsword
					specialBonus = 1.35;
					break;
				case 11696: // Bandos godsword
					specialBonus = 1.2;
					break;
				case 11698:
					specialBonus = 1.1;
					break;
				}
			}
		}

		double otherBonus = 1.0;
		// Slayer masks
		Item helmet = player.getEquipment().get(Equipment.SLOT_HELM);
		if (helmet != null) {
			if (helmet.getId() >= 8901 && helmet.getId() <= 8921) {
				otherBonus = 7 / 6;
			}
		}
		// Salve amulets
		Item amulet = player.getEquipment().get(Equipment.SLOT_AMULET);
		if (amulet != null) {
			if (amulet.getId() == 4081 || amulet.getId() == 10588) {
				otherBonus = 7 / 6;
			}
		}
		return ((int) (max * specialBonus * otherBonus));
	}
}
