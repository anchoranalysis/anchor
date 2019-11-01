package ch.ethz.biol.cell.mpp.proposer;

import org.anchoranalysis.anchor.mpp.mark.Mark;

public interface ICompatibleWith {
	
	// Tests if a kernel is compatible with a mark of a particular type
	boolean isCompatibleWith( Mark testMark );
}
