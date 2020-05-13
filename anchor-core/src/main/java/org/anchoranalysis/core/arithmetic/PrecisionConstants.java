package org.anchoranalysis.core.arithmetic;

/**
 * Constants used as expresses of precision in arithmetic operations
 * 
 * @author Owen Feehan
 *
 */
class PrecisionConstants {

	private PrecisionConstants() {}
	
	/** Minimum acceptable difference before two floating-point numbers are considered equal */
	public static final float EPSILON = 1e-10f;
}
