package org.anchoranalysis.anchor.mpp.proposer;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * An exception thrown when something goes wrong in a Proposer
 * 
 * @author Owen Feehan
 *
 */
public class ProposerException extends AnchorFriendlyCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProposerException(String message) {
		super(message);
	}
	
	public ProposerException(Throwable cause) {
		super(cause);
	}
}
