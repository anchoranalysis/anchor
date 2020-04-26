package org.anchoranalysis.core.error;

/**
 * This is an exceotion that should never be thrown, but can be used for type-safety where an exception is needed
 * 
 * <p>See e.g. {@link org.anchoranalysis.core.bridge.IObjectBridgeIndex} for the type of class that regularly uses exceptions
 * that may never be thrown</p>
 * 
 * @author Owen Feehan
 *
 */
public class AnchorNeverOccursException extends AnchorRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
