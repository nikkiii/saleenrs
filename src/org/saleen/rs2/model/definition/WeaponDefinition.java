package org.saleen.rs2.model.definition;

import org.saleen.rs2.content.skills.impl.LevelRequirement;

/**
 * A sub-class of <code>EquipmentDefinition</code> which represents a weapon
 * 
 * @author Nikki
 * 
 */
public class WeaponDefinition extends EquipmentDefinition {

	/**
	 * Attack speed of this weapon
	 */
	private int attackSpeed;

	/**
	 * Attack animation of this weapon
	 */
	private int attackAnim;

	/**
	 * Stand animation of this weapon
	 */
	private int standAnim;

	/**
	 * Stand turning animation of this weapon
	 */
	private int standTurn;

	/**
	 * Walk animation of this weapon
	 */
	private int walkAnim;

	/**
	 * Turn 180 animation of this weapon
	 */
	private int turn180;

	/**
	 * Turn 90 cw animation of this weapon
	 */
	private int turn90cw;

	/**
	 * Turn 90 ccw animation of this weapon
	 */
	private int turn90ccw;

	/**
	 * Run animation of this weapon
	 */
	private int runAnim;

	public WeaponDefinition(int id, int[] bonuses, int attackSpeed,
			int[] animations, LevelRequirement requirement) {
		super(id, bonuses, requirement);
		// Attack speed
		this.attackSpeed = attackSpeed;
		// Normal anims
		this.attackAnim = animations[0];
		this.standAnim = animations[1];
		this.walkAnim = animations[2];
		this.runAnim = animations[3];
		// Turn anims
		this.standTurn = animations[4];
		this.turn180 = animations[5];
		this.turn90cw = animations[6];
		this.turn90ccw = animations[7];
	}

	/**
	 * Get the attack speed of this weapon
	 * 
	 * @return The attack speed
	 */
	public int getAttackSpeed() {
		return attackSpeed;
	}

	/**
	 * Get the attack animation of this weapon
	 * 
	 * @return The attack animation
	 */
	public int getAttackAnim() {
		return attackAnim;
	}

	/**
	 * Get the stand animation of this weapon
	 * 
	 * @return The stand animation
	 */
	public int getStandAnim() {
		return standAnim;
	}

	/**
	 * Get the stand turn animation of this weapon
	 * 
	 * @return The stand turn animation
	 */
	public int getStandTurn() {
		return standTurn;
	}

	/**
	 * Get the walk animation of this weapon
	 * 
	 * @return The walk animation
	 */
	public int getWalkAnim() {
		return walkAnim;
	}

	/**
	 * Get the turn 180 animation of this weapon
	 * 
	 * @return The turn 180 animation
	 */
	public int getTurn180() {
		return turn180;
	}

	/**
	 * Get the turn 90 cw animation of this weapon
	 * 
	 * @return The animation
	 */
	public int getTurn90cw() {
		return turn90cw;
	}

	/**
	 * Get the turn 90 ccw animation
	 * 
	 * @return The animation
	 */
	public int getTurn90ccw() {
		return turn90ccw;
	}

	/**
	 * Get the run animation
	 * 
	 * @return The run animation
	 */
	public int getRunAnim() {
		return runAnim;
	}
}
