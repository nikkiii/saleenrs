package org.saleen.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A priority based list which can have 5 different priority levels.
 * @author Nikki
 *
 * @param <T>
 */
public class PriorityList<T> implements Iterable<T> {

	/**
	 * The backing sorted set
	 */
	private SortedSet<PriorityEntry> objects;

	/**
	 * The comparator of this list
	 */
	private Comparator<PriorityEntry> comparator = new PriorityComparator<PriorityEntry>();

	/**
	 * Create a new list
	 */
	public PriorityList() {
		objects = new TreeSet<PriorityEntry>(comparator);
	}

	/**
	 * Add an object with normal priority
	 * @param t
	 * 			The object
	 * @return
	 * 			If added, true
	 */
	public boolean add(T t) {
		return objects.add(new PriorityEntry(t, Priority.NORMAL));
	}

	/**
	 * Add an object with a specified priority
	 * @param t
	 * 			The object
	 * @param priority
	 * 			The priority
	 * @return
	 */
	public boolean add(T t, Priority priority) {
		return objects.add(new PriorityEntry(t, priority));
	}

	/**
	 * Remove an object, we have to iterate through the list and check object vs object..
	 * @param t
	 * 			The object..
	 * @return
	 * 			True if removed
	 */
	public boolean remove(T t) {
		Iterator<PriorityEntry> it = objects.iterator();
		while (it.hasNext()) {
			PriorityEntry next = it.next();
			if (next.object == t) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * A basic entry
	 * 
	 * @author Nikki
	 *
	 */
	public class PriorityEntry {
		private T object;
		private Priority priority;

		public PriorityEntry(T object, Priority priority) {
			this.object = object;
			this.priority = priority;
		}

		public Priority getPriority() {
			return priority;
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new PriorityIterator<T>(objects);
	}

	/**
	 * Check if this list contains an object
	 * @param object
	 * 			The object
	 * @return
	 * 			True, if found
	 */
	public boolean contains(T object) {
		Iterator<PriorityEntry> it = objects.iterator();
		while (it.hasNext()) {
			PriorityEntry next = it.next();
			if(object == next.object) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove all matching objects from this list
	 * @param coll
	 * 			The collection of objects to remove
	 */
	public void removeAll(Collection<T> coll) {
		Iterator<PriorityEntry> it = objects.iterator();
		while (it.hasNext()) {
			PriorityEntry next = it.next();
			if (coll.contains(next.object)) {
				it.remove();
			}
		}
	}

	/**
	 * An iterator which grabs the PriorityEntry objects from this list, and gets only the real objects
	 * @author Nikki
	 *
	 * @param <E>
	 * 			The class type
	 */
	public class PriorityIterator<E> implements Iterator<T> {

		/**
		 * The base iterator
		 */
		private Iterator<T> iterator;

		/**
		 * Create a new iterator
		 * @param objects
		 * 			The objects
		 */
		public PriorityIterator(SortedSet<PriorityEntry> objects) {
			List<T> list = new LinkedList<T>();
			for (PriorityEntry entry : objects) {
				list.add(entry.object);
			}
			iterator = list.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() {
			return iterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}