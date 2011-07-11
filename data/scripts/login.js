importClass(org.saleen.rs2.Constants);

function login(player) {
	player.getActionSender().sendMessage("Welcome to " + Constants.SERVER_NAME + ", " + player.getName() + "!");
}