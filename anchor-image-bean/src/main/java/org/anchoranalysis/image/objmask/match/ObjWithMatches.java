package org.anchoranalysis.image.objmask.match;

import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;

public final class ObjWithMatches {
	
	private final ObjectMask objMask;
	private final ObjectCollection matches = new ObjectCollection();
	
	public ObjWithMatches(ObjectMask objMask) {
		super();
		this.objMask = objMask;
	}
	
	public void addSeed( ObjectMask seed ) {
		this.matches.add(seed);
	}
	
	public int numMatches() {
		return this.matches.size();
	}

	public ObjectMask getSourceObj() {
		return objMask;
	}

	public ObjectCollection getMatches() {
		return matches;
	}
}
