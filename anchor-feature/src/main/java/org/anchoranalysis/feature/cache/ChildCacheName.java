package org.anchoranalysis.feature.cache;

/** 
 * A unique identifier for a child-cache name, that uses a class and optionally additionally a part-name
 * <ul>
 * a class -> guaranteed to be unique for the class
 * an option part-name (string) -> a further division of the class into different caches
 * </ul>
 * 
 **/
public class ChildCacheName {

	private Class<?> cls;
	private String part;
	
	/**
	 * Uses only the class as an identifier - and a blank part-name
	 * 
	 * @param cls class
	 */
	public ChildCacheName(Class<?> cls) {
		this(cls,"");
	}
	
	/**
	 * Uses only the class as an identifier - and a integer part-name
	 * 
	 * @param cls class
	 */
	public ChildCacheName(Class<?> cls, int id) {
		this(
			cls,
			String.valueOf(id)
		);
	}
	
	/**
	 * Uses both the class and a part-name as an identifier
	 * 
	 * @param cls class
	 * @param part part-name
	 */
	public ChildCacheName(Class<?> cls, String part) {
		super();
		this.cls = cls;
		this.part = part;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cls == null) ? 0 : cls.getCanonicalName().hashCode());
		result = prime * result + ((part == null) ? 0 : part.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChildCacheName other = (ChildCacheName) obj;
		if (cls == null) {
			if (other.cls != null)
				return false;
		} else if (!cls.getCanonicalName().equals(other.cls.getCanonicalName()))
			return false;
		if (part == null) {
			if (other.part != null)
				return false;
		} else if (!part.equals(other.part))
			return false;
		return true;
	}
}
