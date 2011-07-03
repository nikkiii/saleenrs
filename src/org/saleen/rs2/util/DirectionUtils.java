package org.saleen.rs2.util;

/**
 * A utility class for direction-related methods.
 * 
 * @author Graham Edgecombe
 * 
 */
public class DirectionUtils {

	/**
	 * Finds a direction.
	 * 
	 * @param dx
	 *            X difference.
	 * @param dy
	 *            Y difference.
	 * @return The direction.
	 */
	public static int direction(int dx, int dy) {
		if (dx < 0) {
			if (dy < 0) {
				return 5;
			} else if (dy > 0) {
				return 0;
			} else {
				return 3;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				return 7;
			} else if (dy > 0) {
				return 2;
			} else {
				return 4;
			}
		} else {
			if (dy < 0) {
				return 6;
			} else if (dy > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	/**
	 * 0 = southeast 1 = south 2 = southwest 3 = east 4 = west 5 = northeast 6 =
	 * north 7 = northwest
	 */

	public static int getCannonDir(int anim) {
		switch (anim) {
		case 514: // west
			return 4;
		case 515: // northwest
			return 7;
		case 516: // north
			return 6;
		case 517: // northeast
			return 5;
		case 518: // east
			return 3;
		case 519: // southeast
			return 0;
		case 520: // south
			return 1;
		case 521: // southwest
			return 2;
		}
		return -1;
	}

}
