package org.anchoranalysis.feature.session.strategy.replace;

import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CacheCreator;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.SessionInputSequential;
import org.anchoranalysis.feature.session.strategy.child.DefaultFindChildStrategy;
import org.anchoranalysis.feature.session.strategy.child.FindChildStrategy;

/**
 * Always create a new session-input with no reuse or caching
 * 
 * @author Owen Feehan
 *
 * @param <T> feature-input type
 */
public class AlwaysNew<T extends FeatureInput> extends ReplaceStrategy<T> {

	private CacheCreator cacheCreator;
	private FindChildStrategy findChildStrategy;
	
	/**
	 * Constructor with default means of creating a session-input
	 * 
	 * @param createSessionInput
	 */
	public AlwaysNew(CacheCreator cacheCreator) {
		this(
			cacheCreator,
			DefaultFindChildStrategy.instance()		
		);
	}
	
	/**
	 * Constructor with custom means of creating a session-input
	 * 
	 * @param createSessionInput
	 */
	public AlwaysNew(CacheCreator cacheCreator, FindChildStrategy findChildStrategy) {
		this.cacheCreator = cacheCreator;
		this.findChildStrategy = findChildStrategy;
	}
	
	@Override
	public SessionInput<T> createOrReuse(T input) throws FeatureCalcException {
		return new SessionInputSequential<T>(
			input,
			cacheCreator,
			findChildStrategy
		);
	}

}
