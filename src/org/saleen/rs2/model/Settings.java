package org.saleen.rs2.model;

/**
 * Contains client-side settings.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Settings {

	/**
	 * Withdraw as notes flag.
	 */
	private boolean withdrawAsNotes = false;

	/**
	 * Swapping flag.
	 */
	private boolean swapping = true;

	/**
	 * The headicon
	 */
	private HeadIcon headIcon = HeadIcon.NONE;

	/**
	 * The public chat mode
	 */
	private ChatMode publicMode = ChatMode.ON;

	/**
	 * The private chat mode
	 */
	private ChatMode privateMode = ChatMode.ON;

	/**
	 * The trade chat mode
	 */
	private ChatMode tradeMode = ChatMode.ON;

	/**
	 * Set the player's headicon
	 * 
	 * @param headIcon
	 */
	public void setHeadIcon(HeadIcon headIcon) {
		this.headIcon = headIcon;
	}

	/**
	 * Sets the withdraw as notes flag.
	 * 
	 * @param withdrawAsNotes
	 *            The flag.
	 */
	public void setWithdrawAsNotes(boolean withdrawAsNotes) {
		this.withdrawAsNotes = withdrawAsNotes;
	}

	/**
	 * Sets the swapping flag.
	 * 
	 * @param swapping
	 *            The swapping flag.
	 */
	public void setSwapping(boolean swapping) {
		this.swapping = swapping;
	}

	/**
	 * Checks if the player is withdrawing as notes.
	 * 
	 * @return The withdrawing as notes flag.
	 */
	public boolean isWithdrawingAsNotes() {
		return withdrawAsNotes;
	}

	/**
	 * Checks if the player is swapping.
	 * 
	 * @return The swapping flag.
	 */
	public boolean isSwapping() {
		return swapping;
	}

	/**
	 * Get the current headicon
	 * 
	 * @return The player's headicon
	 */
	public HeadIcon getHeadIcon() {
		return headIcon;
	}

	/**
	 * Set the player chat modes
	 * 
	 * @param publicMode
	 *            The public chat mode
	 * @param privateMode
	 *            The private chat mode
	 * @param tradeMode
	 *            The trade mode
	 */
	public void setChatModes(int publicMode, int privateMode, int tradeMode) {
		this.publicMode = ChatMode.values()[publicMode];
		this.privateMode = ChatMode.values()[privateMode];
		this.tradeMode = ChatMode.values()[tradeMode];
	}

	/**
	 * Get the public chat mode
	 * 
	 * @return The chat mode
	 */
	public ChatMode getPublicChatMode() {
		return publicMode;
	}

	/**
	 * Get the private chat mode
	 * 
	 * @return The chat mode
	 */
	public ChatMode getPrivateChatMode() {
		return privateMode;
	}

	/**
	 * Get the trade mode
	 * 
	 * @return The trade mode
	 */
	public ChatMode getTradeMode() {
		return tradeMode;
	}

	/**
	 * An enum containing all chat modes
	 * 
	 * @author Nikki
	 */
	public enum ChatMode {
		ON, FRIENDS, OFF, HIDE
	}
}
