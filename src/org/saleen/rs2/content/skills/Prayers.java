package org.saleen.rs2.content.skills;

import java.util.HashMap;

import org.saleen.rs2.model.HeadIcon;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Skills;
import org.saleen.rs2.util.TextUtils;

/**
 * A prayer skill managing class, containing prayer info and setting headicons.
 * 
 * @author Nikki
 * 
 */
public class Prayers {

	/**
	 * Prayer statuses
	 */
	private boolean[] prayerstatus = new boolean[Prayer.values().length];

	/**
	 * The Player this manager belongs to
	 */
	private Player player;

	/**
	 * Create a prayermanager instance for a player
	 * 
	 * @param player
	 *            The player to create for
	 */
	public Prayers(Player player) {
		this.player = player;
	}

	/**
	 * Prayer masks
	 */
	public static final int OVERHEAD_PRAYER = 1;
	public static final int ATTACK_PRAYER = 2;
	public static final int STRENGTH_PRAYER = 4;
	public static final int RANGE_PRAYER = 8;
	public static final int MAGIC_PRAYER = 16;
	public static final int DEFENCE_PRAYER = 32;

	public enum Prayer {
		/**
		 * Low level prayers
		 */
		THICK_SKIN(1, 83, 5609, DEFENCE_PRAYER), BURST_OF_STRENGTH(4, 84, 5610,
				STRENGTH_PRAYER), CLARITY_OF_THOUGHT(7, 85, 5611, ATTACK_PRAYER), SHARP_EYE(
				8, 101, 19812, RANGE_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER), MYSTIC_WILL(
				9, 701, 19814, MAGIC_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER),

		/**
		 * Medium level prayers
		 */
		ROCK_SKIN(10, 86, 5612, DEFENCE_PRAYER), SUPERHUMAN_STRENGTH(13, 87,
				5613, STRENGTH_PRAYER), IMPROVED_REFLEXES(16, 88, 5614,
				ATTACK_PRAYER),

		/**
		 * Misc prayers like protect item
		 */
		RAPID_RESTORE(17, 89, 5615), RAPID_HEAL(22, 90, 5616), PROTECT_ITEM(25,
				91, 5617),

		/**
		 * Medium level prayers cont
		 */
		HAWK_EYE(26, 702, 19816, RANGE_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER), MYSTIC_LORE(
				27, 703, 19818, MAGIC_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER),

		/**
		 * High level prayers
		 */
		STEEL_SKIN(28, 92, 5618, DEFENCE_PRAYER), ULTIMATE_STRENGTH(31, 93,
				5619, STRENGTH_PRAYER), INCREDIBLE_REFLEXES(34, 94, 5620,
				ATTACK_PRAYER),

		/**
		 * Protect prayers
		 */
		PROTECT_MAGIC(37, 95, 5621, OVERHEAD_PRAYER, HeadIcon.PROTECT_MAGIC), PROTECT_RANGE(
				40, 96, 5622, OVERHEAD_PRAYER, HeadIcon.PROTECT_MISSLES), PROTECT_MELEE(
				43, 97, 5623, OVERHEAD_PRAYER, HeadIcon.PROTECT_MELEE),

		/**
		 * More high level prayers cont
		 */
		EAGLE_EYE(44, 704, 19821, RANGE_PRAYER | ATTACK_PRAYER
				| STRENGTH_PRAYER), MYSTIC_MIGHT(45, 705, 19823, MAGIC_PRAYER
				| ATTACK_PRAYER | STRENGTH_PRAYER),

		/**
		 * Damage dealing/stat recovering/prayer "stealing" prayers
		 */
		RETRIBUTION(46, 98, 683, OVERHEAD_PRAYER, HeadIcon.RETRIBUTION), REDEMPTION(
				49, 99, 684, OVERHEAD_PRAYER, HeadIcon.REDEMPTION), SMITE(52,
				100, 685, OVERHEAD_PRAYER, HeadIcon.SMITE),

		/**
		 * Highest level prayers available
		 */
		CHIVALRY(60, 706, 19825, ATTACK_PRAYER | STRENGTH_PRAYER
				| DEFENCE_PRAYER), PIETY(70, 707, 19827, ATTACK_PRAYER
				| STRENGTH_PRAYER | DEFENCE_PRAYER);

		/**
		 * A map of Buttonid -> prayer
		 */
		private static HashMap<Integer, Prayer> prayers = new HashMap<Integer, Prayer>();

		static {
			for (Prayer prayer : Prayer.values()) {
				prayers.put(prayer.getButtonId(), prayer);
			}
		}

		private int levelreq;
		private int configId;
		private int buttonId;
		private int prayMask;
		private HeadIcon headIcon;

		private Prayer(int praylevelreq, int configId, int buttonId) {
			this.levelreq = praylevelreq;
			this.configId = configId;
			this.buttonId = buttonId;
		}

		private Prayer(int praylevelreq, int configId, int buttonId,
				int prayMask) {
			this.levelreq = praylevelreq;
			this.configId = configId;
			this.buttonId = buttonId;
			this.prayMask = prayMask;
		}

		private Prayer(int praylevelreq, int configId, int buttonId,
				int prayMask, HeadIcon headIcon) {
			this.levelreq = praylevelreq;
			this.configId = configId;
			this.buttonId = buttonId;
			this.prayMask = prayMask;
			this.headIcon = headIcon;
		}

		public int getPrayerLevel() {
			return levelreq;
		}

		public int getConfigId() {
			return configId;
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getMask() {
			return prayMask;
		}

		public HeadIcon getHeadIcon() {
			return headIcon;
		}

		public static Prayer forButton(int button) {
			return prayers.get(button);
		}
	}

	/**
	 * Set all configs on
	 */
	public void turnAllOn() {
		for (Prayer prayer : Prayer.values()) {
			player.getActionSender().sendConfig(prayer.getConfigId(), 1);
		}
	}

	/**
	 * Set all configs off
	 */
	public void turnAllOff() {
		for (Prayer prayer : Prayer.values()) {
			player.getActionSender().sendConfig(prayer.getConfigId(), 0);
		}
	}

	/**
	 * Toggle a prayer, setting the headicon and checking level if turning on
	 * 
	 * @param prayer
	 *            The prayer to toggle
	 */
	public void togglePrayer(Prayer prayer) {
		if (isPrayerOn(prayer)) {
			player.getActionSender().sendConfig(prayer.getConfigId(), 0);
			set(prayer, false);
			if (prayer.getHeadIcon() != null) {
				player.setHeadIcon(HeadIcon.NONE);
			}
		} else {
			if (player.getSkills().getLevel(Skills.PRAYER) < prayer
					.getPrayerLevel()) {
				player.getActionSender().sendMessage(
						"You need a Prayer level of at least "
								+ prayer.getPrayerLevel() + " to use "
								+ TextUtils.formatEnum(prayer));
				player.getActionSender().sendConfig(prayer.getConfigId(), 0);
				return;
			}
			set(prayer, true);
			checkExtraPrayers(prayer);
			if (prayer.getHeadIcon() != null) {
				player.setHeadIcon(prayer.getHeadIcon());
			}
		}
		player.getActionSender().sendMessage("Prayer toggled: " + prayer);
	}

	/**
	 * Set a prayer on/off
	 * 
	 * @param prayer
	 *            The prayer to set
	 * @param on
	 *            true if on, false if off
	 */
	public void set(Prayer prayer, boolean on) {
		prayerstatus[prayer.ordinal()] = on;
	}

	/**
	 * Clear prayers/curses
	 */
	public void reset() {
		for (int i = 0; i < prayerstatus.length; i++) {
			prayerstatus[i] = false;
		}
	}

	/**
	 * Check if a prayer is on
	 * 
	 * @param prayer
	 *            The prayer to check
	 * @return If the prayer is on, true
	 */
	public boolean isPrayerOn(Prayer prayer) {
		return prayerstatus[prayer.ordinal()];
	}

	/**
	 * Get the prayer for the specified mask
	 * 
	 * @param mask
	 *            The mask
	 * @return The prayer or null
	 */
	public Prayer get(int mask) {
		for (Prayer prayer : Prayer.values()) {
			if (isPrayerOn(prayer)) {
				if ((prayer.getMask() & mask) != 0) {
					return prayer;
				}
			}
		}
		return null;
	}

	/**
	 * Check for the extra prayers on, such as turning on Piety turns off all
	 * other strength boosting
	 * 
	 * @param prayer
	 *            The prayer toggled
	 */
	public void checkExtraPrayers(Prayer prayer) {
		if (prayer.getMask() == -1) {
			return;
		}
		boolean overheadPrayer = (prayer.getMask() & OVERHEAD_PRAYER) != 0;
		boolean attackPrayer = (prayer.getMask() & ATTACK_PRAYER) != 0;
		boolean strengthPrayer = (prayer.getMask() & STRENGTH_PRAYER) != 0;
		boolean defencePrayer = (prayer.getMask() & DEFENCE_PRAYER) != 0;
		boolean rangePrayer = (prayer.getMask() & RANGE_PRAYER) != 0;
		boolean magicPrayer = (prayer.getMask() & MAGIC_PRAYER) != 0;
		for (Prayer p : Prayer.values()) {
			if (!isPrayerOn(p) || p == prayer) {
				continue;
			}

			if (p.getMask() == -1)
				continue;

			if ((p.getMask() & OVERHEAD_PRAYER) != 0 && overheadPrayer
					|| (p.getMask() & ATTACK_PRAYER) != 0 && attackPrayer
					|| (p.getMask() & STRENGTH_PRAYER) != 0 && strengthPrayer
					|| (p.getMask() & DEFENCE_PRAYER) != 0 && defencePrayer
					|| (p.getMask() & RANGE_PRAYER) != 0 && rangePrayer
					|| (p.getMask() & MAGIC_PRAYER) != 0 && magicPrayer) {
				togglePrayer(p);
			}
		}
	}
}
