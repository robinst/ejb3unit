package org.hibernate.reflection.java;

/**
 * A pair of objects that can be used as a key in a Map.
 * 
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 */
abstract class Pair<T, U> {

	private final T o1;

	private final U o2;

	Pair( T o1, U o2 ) {
		this.o1 = o1;
		this.o2 = o2;
	}

	@Override
	public boolean equals(Object obj) {
		Pair other = (Pair) obj;
		return safeEquals( o1, other.o1 ) && safeEquals( o2, other.o2 );
	}

	@Override
	public int hashCode() {
		return safeHashCode( o1 ) ^ safeHashCode( o2 );
	}

	private int safeHashCode(Object o) {
		if( o == null )
			return 0;
		return o.hashCode();
	}

	private boolean safeEquals(Object obj1, Object obj2) {
		if ( obj1 == null )
			return obj2 == null;
		boolean result = obj1.equals( obj2 );
		return result;
	}
}
