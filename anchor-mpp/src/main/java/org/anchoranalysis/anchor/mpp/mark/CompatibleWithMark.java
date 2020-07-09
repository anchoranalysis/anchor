package org.anchoranalysis.anchor.mpp.mark;

public interface CompatibleWithMark {
	
	// Tests if a kernel is compatible with a mark of a particular type
	boolean isCompatibleWith( Mark testMark );
}
