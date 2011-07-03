local Animation = luajava.bindClass("org.saleen.rs2.model.Animation")
local Location = luajava.bindClass("org.saleen.rs2.model.Location")
function helloworld(player)
	player:playAnimation(Animation.WAVE)
	player:getActionSender():sendMessage("Hello, World!")
end
function camera(player)
	player:getActionSender():sendWelcome()
end