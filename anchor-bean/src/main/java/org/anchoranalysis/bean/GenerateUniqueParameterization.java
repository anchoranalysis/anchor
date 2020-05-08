package org.anchoranalysis.bean;

/**
 * Objects that must implement well-defined equals() and hashCode() and also generate a name uniquely identifying the class and all parameterization
 * 
 * @author Owen Feehan
 *
 */
public interface GenerateUniqueParameterization {

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
	
	/** A string describing this class and all its parameterization that is guaranteed to be unique for a given set of parameters */
	public abstract String uniqueName();
}
