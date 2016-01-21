package sypan.utility;

import java.util.ArrayList;

/**
 * A list of <b>unique</b> elements of the specified type {@code T}. A given element cannot be added twice.
 * 
 * @author Carl Linley
 **/
public class Set<T> extends ArrayList<T> {

	private static final long serialVersionUID = -9088097747767487518L;

	public Set(int initialCapacity) {
		super(initialCapacity);
	}

	public Set() {
		super();
	}

	@Override
	public boolean add(T element) {
		if (!contains(element)) {
			return super.add(element);
		}
		return false;
	}

	/**
	 * Removes and returns the element at the head of list.
	 **/
	public T poll() {
		T e = get(0);
		remove(0);
		return e;
	}
}