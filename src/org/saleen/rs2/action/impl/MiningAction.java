package org.saleen.rs2.action.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.saleen.rs2.model.Animation;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Skills;
import org.saleen.rs2.model.World;

/**
 * An action for cutting down trees.
 * 
 * @author Graham Edgecombe
 * 
 */
public class MiningAction extends HarvestingAction {

	/**
	 * Represents types of nodes.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static enum Node {

		/**
		 * Copper ore.
		 */
		COPPER(436, 1, 17.5, new int[] { 2090, 2091, 9708, 9709, 9710, 11936,
				11937, 11938, 11960, 11961, 11962, 31080, 31081, 31082 }),

		/**
		 * Tin ore.
		 */
		TIN(438, 1, 17.5, new int[] { 2094, 2095, 9714, 9715, 9716, 11933,
				11934, 11935, 11957, 11958, 11959, 31077, 31078, 31079 }),

		/**
		 * Blurite ore.
		 */
		BLURITE(668, 10, 17.5, new int[] { 2110 }),

		/**
		 * Iron ore.
		 */
		IRON(440, 15, 35, new int[] { 2092, 2093, 9717, 9718, 9719, 11954,
				11955, 11956, 14856, 14857, 14858, 31071, 31072, 31073, 37307,
				37308, 37309 }),

		/**
		 * Silver ore.
		 */
		SILVER(442, 20, 40, new int[] { 2100, 2101, 2311, 11948, 11949, 11950,
				37304, 37305, 37306 }),

		/**
		 * Gold ore.
		 */
		GOLD(444, 40, 65, new int[] { 2098, 2099, 9720, 9721, 9722, 31065,
				31066, 37310, 37311, 37312 }),

		/**
		 * Coal ore.
		 */
		COAL(453, 30, 50, new int[] { 2096, 2097, 11930, 11931, 11932, 11963,
				11964, 14850, 14851, 14852, 31068, 31069, 31070 }),

		/**
		 * Mithril ore.
		 */
		MITHRIL(447, 55, 80, new int[] { 2102, 2103, 11942, 11943, 11944,
				14853, 14854, 14855, 31086, 31087, 31088 }),

		/**
		 * Adamantite ore.
		 */
		ADAMANTITE(449, 70, 95, new int[] { 2104, 2105, 11939, 11940, 11941,
				14862, 14863, 14864, 31083, 31085 }),

		/**
		 * Rune ore.
		 */
		RUNITE(451, 85, 125, new int[] { 2106, 2107, 14859, 14860, 14861 }),

		/**
		 * Clay ore.
		 */
		CLAY(434, 1, 5, new int[] { 2108, 2109, 9711, 9712, 9713, 15503, 15504,
				15505 }),

		/**
		 * Empty rock
		 */
		EMPTY(-1, -1, -1, new int[] { 11552, 11553, 11554, 14832, 14833, 14834,
				31059, 31060, 31061 });
		/**
		 * A map of object ids to nodes.
		 */
		private static Map<Integer, Node> nodes = new HashMap<Integer, Node>();

		/**
		 * Populates the node map.
		 */
		static {
			for (Node node : Node.values()) {
				for (int object : node.objects) {
					nodes.put(object, node);
				}
			}
		}

		/**
		 * Gets a node by an object id.
		 * 
		 * @param object
		 *            The object id.
		 * @return The node, or <code>null</code> if the object is not a node.
		 */
		public static Node forId(int object) {
			return nodes.get(object);
		}

		/**
		 * The object ids of this node.
		 */
		private int[] objects;

		/**
		 * The minimum level to mine this node.
		 */
		private int level;

		/**
		 * The ore this node contains.
		 */
		private int ore;

		/**
		 * The experience.
		 */
		private double experience;

		/**
		 * Creates the node.
		 * 
		 * @param ore
		 *            The ore id.
		 * @param level
		 *            The required level.
		 * @param experience
		 *            The experience per ore.
		 * @param objects
		 *            The object ids.
		 */
		private Node(int ore, int level, double experience, int[] objects) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.ore = ore;
		}

		/**
		 * Gets the experience.
		 * 
		 * @return The experience.
		 */
		public double getExperience() {
			return experience;
		}

		/**
		 * Gets the object ids.
		 * 
		 * @return The object ids.
		 */
		public int[] getObjectIds() {
			return objects;
		}

		/**
		 * Gets the ore id.
		 * 
		 * @return The ore id.
		 */
		public int getOreId() {
			return ore;
		}

		/**
		 * Gets the required level.
		 * 
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}
	}

	/**
	 * Represents types of axes.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static enum Pickaxe {

		/**
		 * Inferno adze
		 */
		ADZE(13661, 41, 10228),

		/**
		 * Rune pickaxe.
		 */
		RUNE(1275, 41, 624),

		/**
		 * Adamant pickaxe.
		 */
		ADAMANT(1271, 31, 628),

		/**
		 * Mithril pickaxe.
		 */
		MITHRIL(1273, 21, 629),

		/**
		 * Steel pickaxe.
		 */
		STEEL(1269, 11, 627),

		/**
		 * Iron pickaxe.
		 */
		IRON(1267, 5, 626),

		/**
		 * Bronze pickaxe.
		 */
		BRONZE(1265, 1, 625);

		/**
		 * The id.
		 */
		private int id;

		/**
		 * The level.
		 */
		private int level;

		/**
		 * The animation.
		 */
		private int animation;

		/**
		 * A map of object ids to axes.
		 */
		private static Map<Integer, Pickaxe> pickaxes = new HashMap<Integer, Pickaxe>();

		/**
		 * Populates the tree map.
		 */
		static {
			for (Pickaxe pickaxe : Pickaxe.values()) {
				pickaxes.put(pickaxe.id, pickaxe);
			}
		}

		/**
		 * Gets a axe by an object id.
		 * 
		 * @param object
		 *            The object id.
		 * @return The axe, or <code>null</code> if the object is not a axe.
		 */
		public static Pickaxe forId(int object) {
			return pickaxes.get(object);
		}

		/**
		 * Creates the axe.
		 * 
		 * @param id
		 *            The id.
		 * @param level
		 *            The required level.
		 * @param animation
		 *            The animation id.
		 */
		private Pickaxe(int id, int level, int animation) {
			this.id = id;
			this.level = level;
			this.animation = animation;
		}

		/**
		 * Gets the animation id.
		 * 
		 * @return The animation id.
		 */
		public int getAnimation() {
			return animation;
		}

		/**
		 * Gets the id.
		 * 
		 * @return The id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the required level.
		 * 
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}
	}

	/**
	 * The delay.
	 */
	private int delay = 3000;

	/**
	 * The factor.
	 */
	private double factor = 0.5;

	/**
	 * Whether or not this action grants periodic rewards.
	 */
	private static final boolean PERIODIC = false;

	/**
	 * The axe type.
	 */
	private Pickaxe pickaxe;

	/**
	 * The cycle count.
	 */
	private int cycleCount = 0;

	/**
	 * The node type.
	 */
	private Node node;

	private int object;

	/**
	 * Creates the <code>WoodcuttingAction</code>.
	 * 
	 * @param player
	 *            The player performing the action.#
	 * @param tree
	 *            The tree.
	 */
	public MiningAction(Player player, Location location, Node node, int object) {
		super(player, location);
		this.node = node;
		this.object = object;
	}

	/**
	 * Attempts to calculate the number of cycles to mine the ore based on
	 * mining level, ore level and axe speed modifier. Needs heavy work. It's
	 * only an approximation.
	 */
	public int calculateCycles(Player player, Node node, Pickaxe pickaxe) {
		final int mining = player.getSkills().getLevel(Skills.MINING);
		final int difficulty = node.getRequiredLevel();
		final int modifier = pickaxe.getRequiredLevel();
		final int random = new Random().nextInt(3);
		double cycleCount = 1;
		cycleCount = Math.ceil((difficulty * 60 - mining * 20) / modifier
				* 0.25 - random * 4);
		if (cycleCount < 1) {
			cycleCount = 1;
		}
		// player.getActionSender().sendMessage("You must wait " + cycleCount +
		// " cycles to mine this ore.");
		return (int) cycleCount;
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(pickaxe.getAnimation());
	}

	@Override
	public int getCycles() {
		return cycleCount;
	}

	@Override
	public double getExperience() {
		return node.getExperience();
	}

	@Override
	public double getFactor() {
		return factor;
	}

	@Override
	public long getHarvestDelay() {
		return delay;
	}

	@Override
	public Item getHarvestedItem() {
		return new Item(node.getOreId(), 1);
	}

	@Override
	public boolean getPeriodicRewards() {
		return PERIODIC;
	}

	@Override
	public int getSkill() {
		return Skills.MINING;
	}

	@Override
	public void init() {
		final Player player = getPlayer();
		final int mining = player.getSkills().getLevel(Skills.MINING);
		for (Pickaxe pickaxe : Pickaxe.values()) {
			if ((player.getEquipment().contains(pickaxe.getId()) || player
					.getInventory().contains(pickaxe.getId()))
					&& mining >= pickaxe.getRequiredLevel()) {
				this.pickaxe = pickaxe;
				break;
			}
		}
		if (pickaxe == null) {
			player.getActionSender()
					.sendMessage(
							"You do not have a pickaxe for which you have the level to use.");
			stop();
			return;
		}
		if (mining < node.getRequiredLevel()) {
			player.getActionSender().sendMessage(
					"You do not have the required level to mine this rock.");
			stop();
			return;
		}
		player.getActionSender().sendMessage(
				"You swing your pick at the rock...");
		cycleCount = calculateCycles(player, node, pickaxe);

	}

	public Node getNode() {
		return node;
	}

	public static long getRespawnDelay(Node node2) {
		int count = World.getWorld().getPlayers().size();
		long delay = 0;
		if (count >= 0 && count < 200) {
			switch (node2) {
			case TIN:
			case COPPER:
				delay = 2000;
				break;
			case IRON:
				delay = 5000;
				break;
			case SILVER:
				delay = 60000;
				break;
			case COAL:
				delay = 30000;
				break;
			case GOLD:
				delay = 60000;
				break;
			case MITHRIL:
				delay = 120000;
				break;
			case ADAMANTITE:
				delay = 240000;
				break;
			case RUNITE:
				delay = 750000;
				break;
			}
		} else if (count >= 200) {
			switch (node2) {
			case TIN:
			case COPPER:
				delay = 1800;
				break;
			case IRON:
				delay = 4500;
				break;
			case SILVER:
				delay = 54000;
				break;
			case COAL:
				delay = 27000;
				break;
			case GOLD:
				delay = 54000;
				break;
			case MITHRIL:
				delay = 108000;
				break;
			case ADAMANTITE:
				delay = 210600;
				break;
			case RUNITE:
				delay = 660000;
				break;
			}
		}
		return delay;
	}

	public int getObject() {
		return object;
	}

}
