package org.saleen.util;

public enum Priority {
	HIGHEST(0), HIGH(1), NORMAL(2), LOW(3), LOWEST(4);

	private int intValue;

	private Priority(int intValue) {
		this.intValue = intValue;
	}

	public int toInteger() {
		return intValue;
	}
}