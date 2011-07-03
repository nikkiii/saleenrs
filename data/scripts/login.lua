local Constants = luajava.bindClass("org.saleen.rs2.Constants")
function dologin(player)
	player:getActionSender():sendMessage("Welcome to "..Constants.SERVER_NAME..", "..player:getName().."!")
end