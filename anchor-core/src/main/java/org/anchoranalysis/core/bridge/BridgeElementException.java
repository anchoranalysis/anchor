package org.anchoranalysis.core.bridge;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * An exception that occurs when bridging two elements
 * 
 * @author owen
 *
 */
public class BridgeElementException extends AnchorFriendlyCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BridgeElementException(Throwable exc) {
		super(exc);
	}

	public BridgeElementException(String message) {
		super(message);
	}
}
