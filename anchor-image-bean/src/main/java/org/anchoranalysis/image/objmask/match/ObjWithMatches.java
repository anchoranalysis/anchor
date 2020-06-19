package org.anchoranalysis.image.objmask.match;

import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;
import org.anchoranalysis.image.objectmask.ObjectCollectionFactory;

/**
 * An object with associated matches.
 * 
 * @author Owen Feehan
 *
 */
public final class ObjWithMatches {
	
	private final ObjectMask delegate;
	private final ObjectCollection matches = ObjectCollectionFactory.empty();
	
	public ObjWithMatches(ObjectMask obj) {
		super();
		this.delegate = obj;
	}
	
	public void addSeed( ObjectMask seed ) {
		this.matches.add(seed);
	}
	
	public int numMatches() {
		return this.matches.size();
	}

	public ObjectMask getSourceObj() {
		return delegate;
	}

	public ObjectCollection getMatches() {
		return matches;
	}
}
