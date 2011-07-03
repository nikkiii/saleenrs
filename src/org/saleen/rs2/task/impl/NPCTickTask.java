package org.saleen.rs2.task.impl;

import java.util.Random;

import org.saleen.rs2.GameEngine;
import org.saleen.rs2.model.NPC;
import org.saleen.rs2.pf.DumbPathFinder;
import org.saleen.rs2.pf.Path;
import org.saleen.rs2.pf.PathFinder;
import org.saleen.rs2.pf.Point;
import org.saleen.rs2.pf.TileMap;
import org.saleen.rs2.pf.TileMapBuilder;
import org.saleen.rs2.task.Task;

/**
 * A task which performs pre-update tasks for an NPC.
 * 
 * @author Graham Edgecombe
 * 
 */
public class NPCTickTask implements Task {

	/**
	 * The npc who we are performing pre-update tasks for.
	 */
	private NPC npc;

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * Creates the tick task.
	 * 
	 * @param npc
	 *            The npc.
	 */
	public NPCTickTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute(GameEngine context) {
		/*
		 * If the map region changed set the last known region.
		 */
		if (npc.isMapRegionChanging()) {
			npc.setLastKnownRegion(npc.getLocation());
		}

		if (npc.canWalk()) {
			if (!npc.isInteracting() && random.nextInt(3) == 1) {
				int gotoX = npc.getLocation().getX() + random.nextInt(2)
						- random.nextInt(2);
				int gotoY = npc.getLocation().getY() + random.nextInt(2)
						- random.nextInt(2);

				boolean canWalk = true;

				int radius = 2;

				int x2 = gotoX - npc.getLocation().getX() + radius;
				int y2 = gotoY - npc.getLocation().getY() + radius;

				TileMapBuilder bldr = new TileMapBuilder(npc.getLocation(),
						radius);
				TileMap map = bldr.build();

				PathFinder pf = new DumbPathFinder();
				Path p = pf.findPath(npc.getLocation(), radius, map, radius,
						radius, x2, y2);

				if (p == null)
					canWalk = false;

				if (canWalk) {
					npc.getWalkingQueue().reset();
					for (Point p2 : p.getPoints()) {
						npc.getWalkingQueue().addStep(p2.getX(), p2.getY());
					}
					npc.getWalkingQueue().finish();
				}
			}
		}

		/*
		 * Process the next movement in the NPC's walking queue.
		 */
		npc.getWalkingQueue().processNextMovement();
	}

}
