package org.anchoranalysis.feature.session;

import java.util.List;

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.FeatureSymbolCalculator;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.cache.calculation.CalculationResolver;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.feature.cache.calculation.ResolvedCalculation;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.cache.FeatureSessionCache;
import org.anchoranalysis.feature.session.cache.FeatureSessionCacheCalculator;
import org.anchoranalysis.feature.session.cache.creator.CacheCreator;

/**
 * A feature-input that will be used in a {@link SequentialSession}
 * 
 * @author Owen Feehan
 *
 * @param <T> feature-type
 */
public class SessionInputSequential<T extends FeatureInput> implements SessionInput<T> {

	private FeatureSessionCache<T> cache;
	
	private T input;
	private CacheCreator factory;
	
	/**
	 * Constructor
	 * 
	 * @param input
	 * @param factory
	 */
	public SessionInputSequential(T input, CacheCreator factory) {
		this.input = input;
		this.factory = factory;
		
		// Deliberately two lines, as it needs an explicitly declared type for the template type inference to work
		this.cache = factory.create( input.getClass() ); 
	}
	
	private SessionInputSequential(T params, FeatureSessionCache<T> cache, CacheCreator factory) {
		this.input = params;
		this.factory = factory;
		this.cache = cache;
	}
		
	/** 
	 * Replaces existing input with new input
	 * 
	 * @param params new parameters which will replace existing ones
	 **/
	public void replaceInput(T params) {
		cache.invalidate();
		this.input = params;
	}
	
	@Override
	public <V extends FeatureInput> FeatureSessionCacheCalculator<V> resolverForChild(String childName, Class<?> paramsType) {
		FeatureSessionCache<V> cache = cacheForInternal(childName, paramsType);
		return cache.calculator();
	}
	
	private <V extends FeatureInput> FeatureSessionCache<V> cacheForInternal(String childName, Class<?> paramsType) {
		return cache.childCacheFor(childName, paramsType, factory);
	}

	@Override
	public T get() {
		return input;
	}
	
	@Override
	public double calc(Feature<T> feature) throws FeatureCalcException {
		//System.out.printf("CALCulating feature=%s%n", feature.getDscrWithCustomName());
		return cache.calculator().calc(feature, this);
	}

	@Override
	public ResultsVector calc(List<Feature<T>> features) throws FeatureCalcException {
		return cache.calculator().calc(features, this );
	}
	
	@Override
	public <S> S calc(FeatureCalculation<S,T> cc) throws FeatureCalcException {
		return resolver().search(cc).getOrCalculate(input);
	}
	
	@Override
	public <S> S calc(ResolvedCalculation<S,T> cc) throws FeatureCalcException {
		return cc.getOrCalculate(input);
	}
	
	@Override
	public <S extends FeatureInput> double calcChild(Feature<S> feature, S input, String childCacheName) throws FeatureCalcException {
		
		FeatureSessionCache<S> child = cacheForInternal(childCacheName, input.getClass()); 
		return child.calculator().calc(
			feature,
			new SessionInputSequential<S>(
				input,
				child,
				factory
			)
		);
	}
		
	@Override
	public <S extends FeatureInput> double calcChild(Feature<S> feature, FeatureCalculation<S,T> cc, String childCacheName) throws FeatureCalcException {
		return calcChild(
			feature,
			calc(cc),
			childCacheName
		);
	}

	@Override
	public CalculationResolver<T> resolver() {
		return cache.calculator();
	}

	@Override
	public FeatureSymbolCalculator<T> bySymbol() {
		return cache.calculator();
	}
}
