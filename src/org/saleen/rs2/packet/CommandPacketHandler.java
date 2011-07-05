package org.saleen.rs2.packet;

import java.util.Random;

import org.saleen.event.EventProducer;
import org.saleen.event.impl.CommandEvent;
import org.saleen.rs2.Constants;
import org.saleen.rs2.content.combat.CombatCalculations;
import org.saleen.rs2.model.Animation;
import org.saleen.rs2.model.Graphic;
import org.saleen.rs2.model.Item;
import org.saleen.rs2.model.Location;
import org.saleen.rs2.model.Palette;
import org.saleen.rs2.model.Palette.PaletteTile;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.Player.Rights;
import org.saleen.rs2.model.Skills;
import org.saleen.rs2.model.World;
import org.saleen.rs2.model.container.Bank;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.pf.AStarPathFinder;
import org.saleen.rs2.pf.Path;
import org.saleen.rs2.pf.PathFinder;
import org.saleen.rs2.pf.Point;
import org.saleen.rs2.pf.Tile;
import org.saleen.rs2.pf.TileMap;
import org.saleen.rs2.pf.TileMapBuilder;
import org.saleen.rs2.script.FileScriptManager;

/**
 * Handles player commands (the ::words).
 * 
 * @author Graham Edgecombe
 * 
 */
public class CommandPacketHandler extends EventProducer implements
		PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		String commandString = packet.getRS2String();
		String[] args = commandString.split(" ");
		String command = args[0].toLowerCase();
		try {
			if (command.equals("addxp")) {
				int xp = new Random().nextInt(Short.MAX_VALUE);
				player.getActionSender().sendMessage("Gained xp: " + xp);
				player.getActionSender().sendXPCounter(0, xp);
			} else if (command.equals("test")) {
				Palette palette = new Palette();
				PaletteTile tile = new PaletteTile(
						player.getLocation().getX() / 8, player.getLocation()
								.getY() / 8);
				palette.setTile(6, 6, 0, tile);
				player.setLocation(Location.create(9000, 9000, 0));
				player.getActionSender().sendConstructMapRegion(palette);
			} else if (command.equals("tele")) {
				if (args.length == 3 || args.length == 4) {
					int x = Integer.parseInt(args[1]);
					int y = Integer.parseInt(args[2]);
					int z = player.getLocation().getZ();
					if (args.length == 4) {
						z = Integer.parseInt(args[3]);
					}
					player.setTeleportTarget(Location.create(x, y, z));
				} else {
					player.getActionSender().sendMessage(
							"Syntax is ::tele [x] [y] [z].");
				}
			} else if (command.equals("pos")) {
				player.getActionSender().sendMessage(
						"You are at: " + player.getLocation() + ".");
			} else if (command.equals("item")) {
				if (args.length == 2 || args.length == 3) {
					int id = Integer.parseInt(args[1]);
					int count = 1;
					if (args.length == 3) {
						count = Integer.parseInt(args[2]);
					}
					Item item = new Item(id, count);
					player.getInventory().add(item);
					player.getActionSender().sendMessage(
							"Successfully spawned " + count + " x "
									+ item.getDefinition().getName());
				} else {
					player.getActionSender().sendMessage(
							"Syntax is ::item [id] [count].");
				}
			} else if (command.equals("anim")) {
				if (args.length == 2 || args.length == 3) {
					int id = Integer.parseInt(args[1]);
					int delay = 0;
					if (args.length == 3) {
						delay = Integer.parseInt(args[2]);
					}
					player.playAnimation(Animation.create(id, delay));
				}
			} else if (command.equals("gfx")) {
				if (args.length == 2 || args.length == 3) {
					int id = Integer.parseInt(args[1]);
					int delay = 0;
					if (args.length == 3) {
						delay = Integer.parseInt(args[2]);
					}
					player.playGraphics(Graphic.create(id, delay));
				}
			} else if (command.equals("bank")) {
				Bank.open(player);
			} else if (command.equals("max")) {
				for (int i = 0; i < Skills.SKILL_COUNT; i++) {
					player.getSkills().setLevel(i, 99);
					player.getSkills().setExperience(i, 13034431);
				}
			} else if (command.startsWith("empty")) {
				player.getInventory().clear();
				player.getActionSender().sendMessage(
						"Your inventory has been emptied.");
			} else if (command.startsWith("lvl")) {
				try {
					player.getSkills().setLevel(Integer.parseInt(args[1]),
							Integer.parseInt(args[2]));
					player.getSkills().setExperience(
							Integer.parseInt(args[1]),
							player.getSkills().getXPForLevel(
									Integer.parseInt(args[2])) + 1);
					player.getActionSender().sendMessage(
							Skills.SKILL_NAME[Integer.parseInt(args[1])]
									+ " level is now "
									+ Integer.parseInt(args[2]) + ".");
				} catch (Exception e) {
					e.printStackTrace();
					player.getActionSender().sendMessage(
							"Syntax is ::lvl [skill] [lvl].");
				}
			} else if (command.startsWith("skill")) {
				try {
					player.getSkills().setLevel(Integer.parseInt(args[1]),
							Integer.parseInt(args[2]));
					player.getActionSender().sendMessage(
							Skills.SKILL_NAME[Integer.parseInt(args[1])]
									+ " level is temporarily boosted to "
									+ Integer.parseInt(args[2]) + ".");
				} catch (Exception e) {
					e.printStackTrace();
					player.getActionSender().sendMessage(
							"Syntax is ::skill [skill] [lvl].");
				}
			} else if (command.startsWith("enablepvp")) {
				try {
					player.updatePlayerAttackOptions(true);
					player.getActionSender().sendMessage("PvP combat enabled.");
				} catch (Exception e) {

				}
			} else if (command.startsWith("setrank")) {
				player.setRights(Rights.valueOf(args[1].toUpperCase()));
				player.getActionSender().sendRights();
			} else if (command.equals("players")) {
				player.getActionSender().sendMessage(
						"There are " + World.getWorld().getPlayers().size()
								+ " players on " + Constants.SERVER_NAME + ".");
			} else if (command.startsWith("update")) {
				player.getActionSender().sendUpdateTime(
						Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			} else if (command.equals("invokescript")) {
				FileScriptManager.getScriptManager().invoke(args[1], player);
			} else if (command.startsWith("tmask")) {
				int radius = 0;
				TileMapBuilder bldr = new TileMapBuilder(player.getLocation(),
						radius);
				TileMap map = bldr.build();
				Tile t = map.getTile(0, 0);
				player.getActionSender().sendMessage(
						"N: " + t.isNorthernTraversalPermitted() + " E: "
								+ t.isEasternTraversalPermitted() + " S: "
								+ t.isSouthernTraversalPermitted() + " W: "
								+ t.isWesternTraversalPermitted());
			} else if (command.startsWith("goto")) {
				if (args.length == 3) {
					try {
						int radius = 16;

						int x = Integer.parseInt(args[1])
								- player.getLocation().getX() + radius;
						int y = Integer.parseInt(args[2])
								- player.getLocation().getY() + radius;

						TileMapBuilder bldr = new TileMapBuilder(
								player.getLocation(), radius);
						TileMap map = bldr.build();

						PathFinder pf = new AStarPathFinder();
						Path p = pf.findPath(player.getLocation(), radius, map,
								radius, radius, x, y);

						if (p == null)
							return;

						player.getWalkingQueue().reset();
						for (Point p2 : p.getPoints()) {
							player.getWalkingQueue().addStep(p2.getX(),
									p2.getY());
						}
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
			} else if (command.equals("calculatemax")) {
				player.getActionSender().sendMessage(
						"Strength bonus: " + player.getStrengthBonus());
				player.getActionSender().sendMessage(
						"Max hit: "
								+ CombatCalculations.calculateMaxMeleeHit(player));
			} else {
				produce(new CommandEvent(player, commandString));
			}
		} catch (Exception ex) {
			player.getActionSender().sendMessage(
					"Error while processing command.");
			ex.printStackTrace();
		}
	}

}
