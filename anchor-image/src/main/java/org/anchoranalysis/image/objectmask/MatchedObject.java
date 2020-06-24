package org.anchoranalysis.image.objectmask;

/**
 * An object with associated matches.
 * 
 * @author Owen Feehan
 *
 */
public final class MatchedObject {
	
	private final ObjectMask source;
	private final ObjectCollection matches;
	
	/**
	 * Constructor
	 * 
	 * @param source the source object (which is matched against others)
	 */
	public MatchedObject(ObjectMask source, ObjectCollection matches) {
		super();
		this.source = source;
		this.matches = matches;
	}
	
	public int numMatches() {
		return this.matches.size();
	}

	public ObjectMask getSourceObj() {
		return source;
	}

	public ObjectCollection getMatches() {
		return matches;
	}
}
