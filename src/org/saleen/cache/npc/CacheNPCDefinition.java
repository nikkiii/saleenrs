package org.saleen.cache.npc;

public class CacheNPCDefinition {

	private static CacheNPCDefinition[] defs = new CacheNPCDefinition[6391];

	public static void addDefinition(CacheNPCDefinition def) {
		defs[def.getId()] = def;
	}

	public static CacheNPCDefinition forId(int id) {
		return defs[id];
	}

	private int id;
	private String name;
	private String desc;
	private int combatLevel;
	private int size;
	private boolean isAttackable;

	public CacheNPCDefinition(int id, String name, String desc,
			int combatLevel, int size, String[] actions) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.combatLevel = combatLevel;
		this.isAttackable = combatLevel == 0 ? false : true;
		this.size = size;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public boolean isAttackable() {
		return isAttackable;
	}

	public int getSize() {
		return size;
	}

}
