package org.anchoranalysis.core.functional;

import java.util.Optional;

/**
 * Additional utility functions for {@link Optional} and exceptions.
 * 
 * @author Owen Feehan
 *
 */
public class OptionalExceptional {
	
	private OptionalExceptional() {}

	/**
	 * Function used to map from one optional to another
	 * 
	 * @author Owen Feehan
	 *
	 * @param <S> source-type
	 * @param <T> target-type
	 * @param <E> exception that can be thrown during mapping
	 */ 
	@FunctionalInterface
	public interface MapFunction<S, T, E extends Throwable> {
		T apply(S in) throws E;
	}

	/**
	 * Like {@link Optional::map} but tolerates an exception in the mapping function, which is immediately thrown.
	 * 
	 * @param <S> incoming optional-type for map
	 * @param <T> outgoing optional-type for map
	 * @param <E> exception that may be thrown during mapping
	 * @param opt incoming optional
	 * @param mapFunc the function that does the mapping from incoming to outgoing
	 * @return the outgoing "mapped" optional
	 * @throws E an exception if the mapping function throws it
	 */
	public static <S, T,E extends Throwable> Optional<T> map( Optional<S> opt, MapFunction<S,T,E> mapFunc ) throws E {
		if (opt.isPresent()) {
			T target = mapFunc.apply( opt.get() );
			return Optional.of(target);
		} else {
			return Optional.empty();
		}
	}
}
