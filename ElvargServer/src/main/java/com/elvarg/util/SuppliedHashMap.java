package com.elvarg.util;

import java.util.*;
import java.util.function.Supplier;

/**
 * Extended Hash map implementation that adds a method {@link #getAndCreate(Object)}
 * <p>
 * This is a get method that will return a new instance of type <V> if no key is
 * present. 
 * <br>
 * @author Advocatus | https://www.rune-server.ee/members/119929-advocatus/
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class SuppliedHashMap<K, V> extends HashMap<K, V> {

	@java.io.Serial
	public static final long serialVersionUID = 1L;

	private Supplier<V> supplier;

	/**
	 * 
	 * @param supplier This is a functional interface whose functional method is 
	 * {@link Supplier#get()}.
	 */
	public SuppliedHashMap(Supplier<V> supplier) {
		super();
		this.supplier = supplier;

	}

	/**
	 * Returns the value to which the specified key is mapped, or a new instance of
	 * type <V> using the implementation of the {@link #createValue()}
	 * 
	 * @param key
	 * @return The value.
	 */
	public V getAndCreate(K key) {
		V v = this.get(key);
		if (v == null) {
			v = supplier.get();
			put(key, v);
		}
		return v;
	}

}
