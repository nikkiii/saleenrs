from org.saleen.rs2 import Constants
def login(player):
	player.getActionSender().sendMessage("Welcome to " + Constants.SERVER_NAME + ", " + player.getName() + "!")