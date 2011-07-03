package org.saleen.rs2.content.object;

import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.container.Bank;

public class BankObjectAction implements ObjectAction {

	/**
	 * The player
	 */
	private Player player;

	public BankObjectAction(Player player) {
		this.player = player;
	}

	@Override
	public void useObject() {
		Bank.open(player);
	}
}
