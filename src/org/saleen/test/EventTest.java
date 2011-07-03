package org.saleen.test;

import org.saleen.event.Event;
import org.saleen.event.EventConsumer;
import org.saleen.event.EventProducer;
import org.saleen.util.Priority;

public class EventTest {
	private EventProducer producer = new EventProducer();
	
	public EventTest() {
		TestConsumer cons3 = new TestConsumer(Priority.NORMAL);
		TestConsumer cons1 = new TestConsumer(Priority.HIGHEST);
		TestConsumer cons5 = new TestConsumer(Priority.LOWEST);
		TestConsumer cons4 = new TestConsumer(Priority.LOW);
		TestConsumer cons2 = new TestConsumer(Priority.HIGH);
		producer.produce(new TestEvent());
	}
	
	public class TestEvent implements Event {
	}
	
	public class TestConsumer extends EventConsumer {
		private Priority priority;
		public TestConsumer(Priority priority) {
			this.priority = priority;
			bind(TestEvent.class, priority);
		}
		@Override
		public void consume(Event event) {
			System.out.println("Priority "+priority+" called");
		}
	}
	
	public static void main(String[] args) {
		new EventTest();
	}
}
