package org.saleen.rs2.content.dialogue;

/**
 * Represents an NPC or other dialogue When showing dialogues, it will auto
 * select the correct one for the amount of lines
 * 
 * @author Nikki
 */
public class Dialogue {

	/**
	 * The identifying name of this dialogue
	 */
	private String name;

	/**
	 * The text of this dialogue
	 */
	private String[] text;

	/**
	 * This id will be -1 unless it's used as an npc dialogue, if -1 it will
	 * show the player head
	 */
	private int npcId = -1;

	/**
	 * The dialogue type, option or normal
	 */
	private DialogueType type;

	/**
	 * The next dialogue, by default it will go to the same dialogue package
	 */
	private String next = "null";

	/**
	 * Create a dialogue from the data read
	 * 
	 * @param typeIndex
	 *            The type of dialogue
	 * @param lines
	 *            The lines
	 * @param npcId
	 *            The NPC id, or -1 if player
	 * @param next
	 *            The next dialogue name
	 */
	public Dialogue(String name, int typeIndex, String[] lines, int npcId,
			String next) {
		this.name = name;
		this.type = DialogueType.values()[typeIndex];
		this.text = lines;
		this.npcId = npcId;
		this.next = next;
	}

	/**
	 * Get the text of this dialogue
	 * 
	 * @return An array of lines this dialogue has
	 */
	public String[] getText() {
		return text;
	}

	/**
	 * Get the dialogue type
	 * 
	 * @return The type, either Normal or Option
	 */
	public DialogueType getType() {
		return type;
	}

	/**
	 * Get the npc id associated
	 * 
	 * @return The NPC id of this dialogue, or -1 if player
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Get the next dialogue
	 * 
	 * @return The next dialogue id
	 */
	public String getNext() {
		return next;
	}

	/**
	 * Check whether the dialogue belongs to an npc or player
	 * 
	 * @return true, if the dialogue belongs to an npc, or false if player
	 */
	public boolean isNPC() {
		return npcId != -1;
	}

	/**
	 * Get the dialogue's identifying name
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Represents the 2 different dialogue types
	 * 
	 * @author Nikki
	 */
	public enum DialogueType {
		NORMAL, OPTION
	}
}
