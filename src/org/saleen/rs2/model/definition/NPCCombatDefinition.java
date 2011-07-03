package org.saleen.rs2.model.definition;

public class NPCCombatDefinition {

	private static final int MAX_DEFS = 6391;

	private static NPCCombatDefinition[] defs = new NPCCombatDefinition[MAX_DEFS];

	public static NPCCombatDefinition forId(int id) {
		if (defs[id] == null) {
			return defs[id] = new NPCCombatDefinition();
		}
		return defs[id];
	}

	private int maxHealth = 1000;

	public NPCCombatDefinition() {
		// Nothing.
	}

	public int getMaxHealth() {
		return maxHealth;
	}
}
