package org.saleen.rs2.action.impl;

import org.saleen.rs2.action.Action;
import org.saleen.rs2.event.impl.ObjectRestoreEvent;
import org.saleen.rs2.model.Animation;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.model.definition.ItemDefinition;
import org.saleen.rs2.model.region.Region;

/**
 * <p>
 * A harvesting action is a resource-gathering action, which includes, but is
 * not limited to, woodcutting and mining.
 * </p>
 * 
 * <p>
 * This class implements code related to all harvesting-type skills, such as
 * dealing with the action itself, looping, expiring the object (i.e. changing
 * rocks to the gray rock and trees to the stump), checking requirements and
 * giving out the harvested resources.
 * </p>
 * 
 * <p>
 * The individual woodcutting and mining classes implement things specific to
 * these individual skills such as random events.
 * </p>
 * 
 * @author Graham Edgecombe
 * 
 */
public abstract class HarvestingAction extends Action {
	/**
	 * The location.
	 */
	private Location location;

	/**
	 * The total number of cycles.
	 */
	private int totalCycles;

	/**
	 * The number of remaining cycles.
	 */
	private int cycles;

	/**
	 * The region containing this action
	 */
	private Region region;

	/**
	 * Creates the harvesting action for the specified player.
	 * 
	 * @param player
	 *            The player to create the action for.
	 */
	public HarvestingAction(Player player, Location location) {
		super(player, 0);
		this.location = location;
		this.region = World.getWorld().getRegionManager()
				.getRegionByLocation(location);
	}

	@Override
	public void execute() {
		final Player player = getPlayer();
		if (!player.getWalkingQueue().isEmpty()) {
			return;
		}
		if (this.getDelay() == 0) {
			this.setDelay(getHarvestDelay());
			init();
			if (this.isRunning()) {
				player.playAnimation(getAnimation());
				player.face(location);
			}
			this.cycles = getCycles();
			this.totalCycles = cycles;
		} else {
			cycles--;
			Item item = getHarvestedItem();
			if (player.getInventory().hasRoomFor(item)) {
				if (totalCycles == 1 || Math.random() > getFactor()) {
					if (getPeriodicRewards()) {
						giveRewards(player, item);
					}
				}
			} else {
				stop();
				player.getActionSender().sendMessage(
						"There is not enough space in your inventory.");
				return;
			}
			if (cycles == 0) {
				if (!getPeriodicRewards()) {
					giveRewards(player, item);
				}
				if (this instanceof MiningAction) {
					long delay = MiningAction
							.getRespawnDelay(((MiningAction) this).getNode());
					registerRestore(delay);
				} else if (this instanceof WoodcuttingAction) {
					// Tree tree = ((WoodcuttingAction)this).getTree();
					long delay = 0;
					registerRestore(delay);
				}
				for (Region region : World.getWorld().getRegionManager()
						.getSurroundingRegions(this.location)) {
					for (Player pl : region.getPlayers()) {
						if (pl.getLocation().isWithinDistance(this.location)) {
							if (pl.getActionQueue().getCurrentAction() != null) {
								if (pl.getActionQueue().getCurrentAction() instanceof HarvestingAction) {
									HarvestingAction action = (HarvestingAction) pl
											.getActionQueue()
											.getCurrentAction();
									if (action.location.equals(this.location)) {
										action.stop();
									}
								}
							}
						}
					}
				}
				player.playAnimation(Animation.RESET);
				stop();
			} else {
				player.playAnimation(getAnimation());
				player.face(location);
			}
		}
	}

	private void registerRestore(long delay) {
		GameObject object = region.getObjectAt(location);
		if (object != null) {
			for (Region region : World.getWorld().getRegionManager()
					.getSurroundingRegions(location)) {
				for (Player pl : region.getPlayers()) {
					if (pl.getLocation().isWithinDistance(this.location))
						// TODO REPLACEMENT!
						pl.getActionSender().sendCreateObject(location, 0,
								object.getRotation(), object.getType());
				}
			}
			World.getWorld().submit(new ObjectRestoreEvent(object, delay));
		} else {
			// Somehow they mined it or used it...
		}
	}

	/**
	 * Gets the animation.
	 * 
	 * @return The animation.
	 */
	public abstract Animation getAnimation();

	/**
	 * Gets the number of cycles.
	 * 
	 * @return The number of cycles.
	 */
	public abstract int getCycles();

	/**
	 * Gets the experience.
	 * 
	 * @return The experience.
	 */
	public abstract double getExperience();

	/**
	 * Gets the success factor.
	 * 
	 * @return The success factor.
	 */
	public abstract double getFactor();

	/**
	 * Gets the harvest delay.
	 * 
	 * @return The delay between consecutive harvests.
	 */
	public abstract long getHarvestDelay();

	/**
	 * Gets the harvested item.
	 * 
	 * @return The harvested item.
	 */
	public abstract Item getHarvestedItem();

	/**
	 * Gets reward type.
	 * 
	 * @return <code>true/false</code> Whether items are rewarded periodically
	 *         during the action.
	 */
	public abstract boolean getPeriodicRewards();

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.ALWAYS;
	}

	/**
	 * Gets the skill.
	 * 
	 * @return The skill.
	 */
	public abstract int getSkill();

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	/**
	 * Grants the player his or her reward.
	 * 
	 * @param player
	 *            The player object.
	 * @param reward
	 *            The item reward object.
	 */
	private void giveRewards(Player player, Item reward) {
		player.getInventory().add(reward);
		ItemDefinition def = reward.getDefinition();
		player.getActionSender().sendMessage(
				"You get some " + def.getName() + ".");
		player.getSkills().addExperience(getSkill(), getExperience());
	}

	/**
	 * Called when the action is initialized.
	 */
	public abstract void init();
}
