package org.anchoranalysis.feature.cache;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Resolves and calculates a feature by a symbol (an ID/variable-name referring to another feature)
 * 
 * @author Owen Feehan
 *
 * @param <T> feature-input type
 */
public interface FeatureSymbolCalculator<T extends FeatureInput> {

	/**
	 * Due to scoping (different prefixes that can exist), an ID needs to be resolved
	 *  to a unique-string before it can be passed to calcFeatureByID
	 * 
	 * @param id
	 * @return
	 */
	public abstract String resolveFeatureID( String id );
	
	/**
	 * Searches for a feature that matches a particular ID
	 * 
	 * @param resolvedID
	 * @param input TODO
	 * @throws GetOperationFailedException 
	 */
	public abstract double calcFeatureByID( String resolvedID, SessionInput<T> input ) throws FeatureCalcException;
}
