package org.saleen.util;

/**
 * A class which can be used to filter out items, by calling filter.accept()
 * 
 * @author Nikki
 * 
 * @param <T>
 *            The class type this filter will check
 */
public interface Filter<T> {
	public boolean accept(T t);
}
