package org.anchoranalysis.image.seed;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.objectmask.ObjectMask;

// A seed provides an ObjMask of pixels that exclusively
//   belong to a particular object
public abstract class Seed {

	public abstract ObjectMask createMask();

	public abstract void scaleXY( double scale ) throws OperationFailedException;
	
	public abstract void flattenZ();
	
	public abstract void growToZ(int sz);
	
	public abstract Seed duplicate();
	
	public abstract boolean equalsDeep(Seed other);
}
