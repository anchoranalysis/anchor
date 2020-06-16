package org.anchoranalysis.image.objmask.match;

import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;

public class ObjWithMatches {
	
	private ObjectMask objMask;
	private ObjectMaskCollection matches = new ObjectMaskCollection();
	
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

	public ObjectMaskCollection getMatches() {
		return matches;
	}
}
