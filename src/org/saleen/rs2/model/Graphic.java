package org.saleen.rs2.model;

/**
 * Represents a single graphic request.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Graphic {

	public static final Graphic ZOMBIE_HAND = Graphic.create(1244);
	public static final Graphic AIR_GUITAR = Graphic.create(1537);

	/**
	 * Creates an graphic with no delay.
	 * 
	 * @param id
	 *            The id.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id) {
		return create(id, 0);
	}

	/**
	 * Creates a graphic.
	 * 
	 * @param id
	 *            The id.
	 * @param delay
	 *            The delay.
	 * @return The new graphic object.
	 */
	public static Graphic create(int id, int delay) {
		return new Graphic(id, delay);
	}

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The delay.
	 */
	private int delay;

	/**
	 * Creates a graphic.
	 * 
	 * @param id
	 *            The id.
	 * @param delay
	 *            The delay.
	 */
	private Graphic(int id, int delay) {
		this.id = id;
		this.delay = delay;
	}

	/**
	 * Gets the id.
	 * 
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the delay.
	 * 
	 * @return The delay.
	 */
	public int getDelay() {
		return delay;
	}

}
