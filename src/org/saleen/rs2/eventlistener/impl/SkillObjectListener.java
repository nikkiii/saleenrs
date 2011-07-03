package org.saleen.rs2.eventlistener.impl;

import org.saleen.event.Event;
import org.saleen.event.EventConsumer;
import org.saleen.event.impl.ClickOption;
import org.saleen.event.impl.ObjectOptionEvent;
import org.saleen.event.impl.ObjectOptionEvent.ObjectOption1;
import org.saleen.event.impl.ObjectOptionEvent.ObjectOption2;
import org.saleen.rs2.action.impl.MiningAction;
import org.saleen.rs2.action.impl.MiningAction.Node;
import org.saleen.rs2.action.impl.ObjectUseAction;
import org.saleen.rs2.action.impl.ProspectingAction;
import org.saleen.rs2.action.impl.WoodcuttingAction.Tree;
import org.saleen.rs2.content.object.TreeObjectAction;
import org.saleen.rs2.model.GameObject;
import org.saleen.rs2.model.Player;

/**
 * An implementation of a <code>ObjectEventListener</code> which handles mining
 * and skill object events
 * 
 * @author Nikki
 * 
 */
public class SkillObjectListener extends EventConsumer {

	@SuppressWarnings("unchecked")
	public SkillObjectListener() {
		bind(ObjectOption1.class, ObjectOption2.class);
	}

	public void objectOption1(Player player, GameObject object) {
		Tree tree = Tree.forId(object.getId());
		if (tree != null
				&& player.getLocation().isWithinInteractionDistance(
						object.getLocation())) {
			player.getActionQueue().addAction(
					new ObjectUseAction(player, object, new TreeObjectAction(
							player, object.getLocation(), tree)));
		}
		// mining
		Node node = Node.forId(object.getId());
		if (node != null
				&& player.getLocation().isWithinDistance(object.getLocation())) {
			if (node == Node.EMPTY) {
				player.face(object.getLocation());
				player.getActionSender().sendMessage(
						"There is no ore currently available in this rock.");
			} else {
				player.getActionQueue().addAction(
						new MiningAction(player, object.getLocation(), node,
								object.getId()));
			}
			return;
		}
	}

	public void objectOption2(Player player, GameObject object) {
		Node node = Node.forId(object.getId());
		if (node != null
				&& player.getLocation().isWithinInteractionDistance(
						object.getLocation())) {
			player.getActionQueue().addAction(
					new ProspectingAction(player, object.getLocation(), node));
			return;
		}
	}

	@Override
	public void consume(Event event) {
		ObjectOptionEvent evt = (ObjectOptionEvent) event;
		if (evt.isOption(ClickOption.CLICK_1)) {
			objectOption1(evt.getPlayer(), evt.getObject());
		} else if (evt.isOption(ClickOption.CLICK_2)) {
			objectOption2(evt.getPlayer(), evt.getObject());
		}
	}

}
