package org.saleen.event;

import org.saleen.util.Priority;

/**
 * A consumer of {@link Event} instances.
 * 
 * @author Blake Beaupain
 */
public abstract class EventConsumer {

	/**
	 * Binds the given <code>Event</code> types to this consumer.
	 * 
	 * @param eventTypes
	 *            The types that this consumer is interested in
	 */
	public void bind(Class<? extends Event>... eventTypes) {
		for (Class<? extends Event> eventType : eventTypes) {
			EventProcessor.getInstance().bind(eventType, this);
		}
	}
	
	/**
	 * Binds the given <code>Event</code> types to this consumer.
	 * 
	 * @param eventTypes
	 *            The types that this consumer is interested in
	 */
	public void bind(Class<? extends Event> eventType, Priority priority) {
		EventProcessor.getInstance().bind(eventType, this, priority);
	}

	/**
	 * Unbinds the given <code>Event</code> types from this consumer.
	 * 
	 * @param eventTypes
	 *            The types that this consumer is no longer interested in
	 */
	public void unbind(Class<? extends Event>... eventTypes) {
		for (Class<? extends Event> eventType : eventTypes) {
			EventProcessor.getInstance().unbind(eventType, this);
		}
	}

	/**
	 * Unbind all event types associated with this consumer
	 */
	public void unbindAll() {
		EventProcessor.getInstance().unbindTypes(this);
	}

	/**
	 * Consumes an <code>Event</code>.
	 * 
	 * @param event
	 *            The event to consume
	 */
	public abstract void consume(Event event);

}
