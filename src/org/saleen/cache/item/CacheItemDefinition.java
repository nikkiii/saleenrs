package org.saleen.cache.item;

public class CacheItemDefinition {

	private static CacheItemDefinition[] defs;

	public static void init(int maxitems) {
		defs = new CacheItemDefinition[maxitems];
	}

	public static void addItem(CacheItemDefinition cacheItemDefinition) {
		if (cacheItemDefinition.getCertTemplateID() != -1) {
			CacheItemDefinition note = cacheItemDefinition;
			String s = "a";
			char c = cacheItemDefinition.getName().charAt(0);
			if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
				s = "an";
			note.setDescription("Swap this note at any bank for " + s + " "
					+ cacheItemDefinition.getName() + ".");
			note.setStackable(true);
		}
		defs[cacheItemDefinition.getId()] = cacheItemDefinition;
	}

	private int id;
	private String name;
	private String description;
	private boolean members;
	private boolean stackable;
	private String[] groundActions;
	private String[] actions;
	private int certID;
	private int[] stackIDs;
	private int[] stackAmounts;
	private int certTemplateID;

	public CacheItemDefinition(int id, String name, String desc,
			boolean members, boolean stackable, String[] groundActions,
			String[] actions, int certID, int certTemplateID, int[] stackIDs,
			int[] stackAmounts) {
		this.id = id;
		this.name = name;
		this.description = desc;
		this.members = members;
		this.stackable = stackable;
		this.groundActions = groundActions;
		this.actions = actions;
		this.certID = certID;
		this.certTemplateID = certTemplateID;
		this.stackIDs = stackIDs;
		this.stackAmounts = stackAmounts;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isMembers() {
		return members;
	}

	public boolean isStackable() {
		return stackable;
	}

	public String[] getGroundActions() {
		return groundActions;
	}

	public String[] getActions() {
		return actions;
	}

	public int getCertID() {
		return certID;
	}

	public int getCertTemplateID() {
		return certTemplateID;
	}

	public int[] getStackIDs() {
		return stackIDs;
	}

	public int[] getStackAmounts() {
		return stackAmounts;
	}

	private void setStackable(boolean b) {
		this.stackable = b;
	}

	private void setDescription(String string) {
		this.description = string;
	}
}
