package org.saleen.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core event processing engine. Handles the binding, unbinding, and
 * broadcasting of global events to their respective consumers. When an
 * {@link Event} is bound to any number of {@link EventConsumer} instances, all
 * calls of {@link EventProcessor#broadcast(Event)} will result in the
 * {@link EventConsumer#consume(Event)} method being called for the given
 * <code>EventConsumer</code> instances.
 * 
 * @author Blake Beaupain
 */
public class EventProcessor {

	/**
	 * The instance
	 */
	private static EventProcessor instance;

	/**
	 * The logger
	 */
	private Logger logger = Logger.getAnonymousLogger();

	/**
	 * The map of Events -> Event Consumers
	 */
	private Map<Class<? extends Event>, LinkedList<EventConsumer>> map = new HashMap<Class<? extends Event>, LinkedList<EventConsumer>>();

	private EventProcessor() {
		// Access via singleton only.
	}

	/**
	 * Broadcasts an event to all bound {@link EventConsumer}s. This is a
	 * <code>protected</code> method, all broadcasting of <code>Event</code>
	 * instances must be done through an {@link EventProducer}.
	 * 
	 * @param event
	 *            The event to broadcast
	 */
	protected void broadcast(Event event) {
		for (EventConsumer consumer : getConsumersOf(event.getClass())) {
			try {
				consumer.consume(event);
			} catch (ConsumerInterruptor i) {
				break;
			} catch (Exception ex) {
				logger.log(Level.WARNING,
						"Exception thrown while firing event.", ex);
			}
		}
	}

	/**
	 * Binds an {@link Event} type with a set {@link EventConsumer} instances.
	 * All calls to {@link EventProcessor#broadcast(Event)} with an
	 * <code>Event</code> of the given type as an argument will result in a
	 * {@link EventConsumer#consume(Event)} call on the given consumers.
	 * 
	 * @param eventType
	 *            The type of <code>Event</code> to bind the consumers to
	 * @param consumers
	 *            The <code>EventConsumer</code> instances to consume the given
	 *            event when broadcasted to this event processor
	 */
	public void bind(Class<? extends Event> eventType,
			EventConsumer... consumers) {
		LinkedList<EventConsumer> list = getConsumersOf(eventType);
		for (EventConsumer consumer : consumers) {
			list.addFirst(consumer);
		}
	}

	/**
	 * Unbinds an {@link Event} type with a set of {@link EventConsumer}
	 * instances. All argued <code>EventConsumer</code> instances will not
	 * receive any broadcasts of the given <code>Event</code> type.
	 * 
	 * @param eventType
	 * @param consumers
	 */
	public void unbind(Class<? extends Event> eventType,
			EventConsumer... consumers) {
		getConsumersOf(eventType).removeAll(Arrays.asList(consumers));
	}

	/**
	 * Gets a <code>List</code> containing the <code>EventConsumer</code>
	 * instances interested in receiving <code>Event</code>s of the given type.
	 * Performs lazily initialization of non-existent lists.
	 * 
	 * @param eventType
	 *            The type of <code>Event</code>
	 * @return A list of <code>EventConsumer</code> instances interested in the
	 *         given event.
	 */
	private LinkedList<EventConsumer> getConsumersOf(
			Class<? extends Event> eventType) {
		LinkedList<EventConsumer> list = map.get(eventType);
		if (list == null) {
			map.put(eventType, list = new LinkedList<EventConsumer>());
		}
		return list;
	}

	/**
	 * Unbind all types from a specific consumer
	 * 
	 * @param eventConsumer
	 *            The consumer
	 * @return
	 */
	public void unbindTypes(EventConsumer eventConsumer) {
		for (Entry<Class<? extends Event>, LinkedList<EventConsumer>> entry : map
				.entrySet()) {
			if (entry.getValue().contains(eventConsumer)) {
				unbind(entry.getKey(), eventConsumer);
			}
		}
	}

	/**
	 * Get the instance
	 * 
	 * @return The instance
	 */
	protected static EventProcessor getInstance() {
		return instance == null ? instance = new EventProcessor() : instance;
	}
}
