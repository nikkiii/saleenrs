package org.saleen.rs2.model.definition;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

import org.saleen.rs2.content.skills.impl.BasicLevelRequirement;
import org.saleen.rs2.content.skills.impl.CompositeLevelRequirement;
import org.saleen.rs2.content.skills.impl.LevelRequirement;
import org.saleen.rs2.model.container.Equipment;

/**
 * Represents a piece of Equipment, such as a weapon, platebody, cape etc
 * 
 * @author Nikki
 * 
 */
public class EquipmentDefinition {

	/**
	 * The number of items in the 474 cache
	 */
	private static final int NUM_ITEMS = 11791;

	/**
	 * An array of all definitions
	 */
	private static EquipmentDefinition[] definitions = new EquipmentDefinition[NUM_ITEMS];

	/**
	 * Initialize the item definitions
	 */
	public static void init() throws IOException {
		RandomAccessFile raf = new RandomAccessFile(
				"data/equipmentDefinitions.bin", "r");
		try {
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0,
					raf.length());
			while (true) {
				int id = buffer.getShort();
				if (id == -1) {
					break;
				}
				int slot = buffer.get();
				Equipment.getTypes().put(id, Equipment.getTypeForInteger(slot));

				int attackSpeed = 5;
				int[] animations = new int[8];
				int[] bonuses = new int[12];

				boolean extra = buffer.get() == 1;
				if (extra) {
					attackSpeed = buffer.get();
					for (int il = 0; il < bonuses.length; il++) {
						bonuses[il] = buffer.getShort();
					}
				}
				boolean weapon = buffer.get() == 1;
				if (weapon) {
					for (int i = 0; i < animations.length; i++) {
						animations[i] = buffer.getShort();
					}
				}
				LevelRequirement requirement = null;
				boolean hasRequirements = buffer.get() == 1;
				List<Integer> skillIds = new ArrayList<Integer>();
				List<Integer> skillLevels = new ArrayList<Integer>();
				if (hasRequirements) {
					int size = buffer.get();
					for (int il = 0; il < size; il++) {
						skillIds.add((int) buffer.get());
						skillLevels.add((int) buffer.get());
					}
					if (size > 1) {
						requirement = new CompositeLevelRequirement(skillIds,
								skillLevels);
					} else if (size == 1) {
						requirement = new BasicLevelRequirement(
								skillIds.get(0), skillLevels.get(0));
					}
				}
				if (weapon)
					definitions[id] = new WeaponDefinition(id, bonuses,
							attackSpeed, animations, requirement);
				else
					definitions[id] = new EquipmentDefinition(id, bonuses,
							requirement);
			}
		} finally {
			raf.close();
		}
	}

	/**
	 * Get the equipment definition for a specific id
	 * 
	 * @param id
	 *            The id to get for
	 * @return The definition
	 */
	public static EquipmentDefinition forId(int id) {
		return definitions[id];
	}

	/**
	 * The ID of this piece of equipment
	 */
	private int id;

	/**
	 * The bonuses of this piece of equipment
	 */
	private int[] bonuses;

	/**
	 * The level requirement, can be a <code>BasicLevelRequirement</code> or
	 * <code>CompositeLevelRequirement</code>
	 */
	private LevelRequirement requirement;

	/**
	 * Create an EquipmenDefinition by the specified id and bonuses
	 * 
	 * @param id
	 *            The ID of the equipment
	 * @param bonuses
	 *            The Bonuses
	 */
	public EquipmentDefinition(int id, int[] bonuses,
			LevelRequirement requirement) {
		this.id = id;
		this.bonuses = bonuses;
		this.requirement = requirement;
	}

	/**
	 * Get the id of this equipment definition
	 * 
	 * @return The id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the bonuses of this definition
	 * 
	 * @return The bonuses
	 */
	public int[] getBonuses() {
		return bonuses;
	}

	/**
	 * Get the level requirement of this equipment piece
	 * 
	 * @return The level requirement
	 */
	public LevelRequirement getRequirements() {
		return requirement;
	}

	/**
	 * Get a specific bonus
	 * 
	 * @param i
	 *            The bonus index
	 * @return The bonus
	 */
	public int getBonus(int i) {
		return bonuses[i];
	}
}
