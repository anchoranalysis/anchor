package org.anchoranalysis.core.error.friendly;


/**
 * A run-time exception to throw in situations which should never occur, instead of <pre>assert false</pre>
 * 
 * <p>If this is somehow thrown, it's an indication there is a logical error in the code.</p>
 * 
 * <p>This makes for more readable code and type-safety rather than <pre>assert false</pre> or other miscellaneous exceptions.
 * 
 * @author Owen Feehan
 *
 */
public class AnchorImpossibleSituationException extends AnchorFriendlyRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AnchorImpossibleSituationException() {
		super("This situation should never occur in properly functioning code, as it is should be logically impossible to reach.");
		
		// As a further warnig, and assert is triggered.
		assert(false);
	}
}
