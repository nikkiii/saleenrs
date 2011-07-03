package org.saleen.util;

import java.util.Comparator;

import org.saleen.util.PriorityList.PriorityEntry;

@SuppressWarnings("rawtypes")
public class PriorityComparator<T extends PriorityEntry> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		Priority p1 = o1.getPriority();
		Priority p2 = o2.getPriority();
		//The lower the number, higher the priority
		if(p1.toInteger() > p2.toInteger()) {
			return 1;
		} else if(p1.toInteger() < p2.toInteger()) {
			return -1;
		}
		return 0;
	}
}
