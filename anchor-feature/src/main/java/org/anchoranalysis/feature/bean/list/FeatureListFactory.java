package org.anchoranalysis.feature.bean.list;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.functional.FunctionalUtilities;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.objectmask.ObjectMask;

public class FeatureListFactory {

	private FeatureListFactory() {}
	
	public static <T extends FeatureInput> FeatureList<T> empty() {
		return new FeatureList<>();
	}
	
	/**
	 * Creates a list for one or more features
	 * 
	 * @param <T> input-type of feature(s)
	 * @param feature the feature(s)
	 * @return a newly-created list with each feature included in identical order to the argument
	 */
	@SafeVarargs
	public static <T extends FeatureInput> FeatureList<T> from(Feature<T> ...feature) {
		assert(feature!=null);
		return new FeatureList<>(
			Arrays.stream(feature)
		);
	}
	
	/**
	 * Creates a list for a single-feature created from a provider
	 * 
	 * @param <T> input-type of feature
	 * @param featureProvider provides the single feature
	 * @return a newly-created list
	 * @throws CreateException 
	 */
	public static <T extends FeatureInput> FeatureList<T> fromProvider( FeatureProvider<T> featureProvider) throws CreateException {
		return from(
			featureProvider.create()
		);
	}
	
	public static <T extends FeatureInput> FeatureList<T> fromIterable(Iterable<Feature<T>> iterFeatures) {
		return new FeatureList<>(
			StreamSupport.stream(iterFeatures.spliterator(), false)
		);
	}
	
	public static <T extends FeatureInput> FeatureList<T> fromProviders(List<FeatureProvider<T>> providers) throws CreateException {
		return new FeatureList<>(
			FunctionalUtilities.mapWithException(
				providers.stream(),
				CreateException.class,
				FeatureProvider::create
			)
		);
	}
	
	/** Wraps an existing list, reusing the list in the internals of the {@link FeatureList} */
	public static <T extends FeatureInput> FeatureList<T> wrapReuse(List<Feature<T>> list) {
		return new FeatureList<>(list);
	}

	/** Wraps an existing list, WITHOUT reusing the list in the internals of the {@link FeatureList} i.e. create a new list internally */
	public static <T extends FeatureInput> FeatureList<T> wrapDuplicate(List<Feature<T>> list) {
		return new FeatureList<>(list.stream());
	}
	
	
	/**
	 * Creates a new ObjectCollection by mapping an {@link Iterable} to {@link ObjectMask}
	 * 
	 * @param <S> type that will be mapped to {@link Feature<T>}
	 * @param <T> feature input-type for the result of the mapping
	 * @param <E> exception-type that can be thrown during mapping
	 * @param iterable source of entities to be mapped
	 * @param mapFunc function for mapping
	 * @return a newly created feature-list, with the result (in order) of the mapping of each iterm in {@link iterable}
	 * @throws E exception if it occurs during mapping
	 */
	public static <S, T extends FeatureInput,E extends Throwable> FeatureList<T> mapFrom( Iterable<S> iterable, FunctionWithException<S,Feature<T>,E> mapFunc ) throws E {
		FeatureList<T> out = new FeatureList<>();
		for( S item : iterable ) {
			out.add(
				mapFunc.apply(item)
			);
		}
		return out;
	}
}
