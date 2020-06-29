package org.anchoranalysis.image.objectmask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.functional.FunctionalUtilities;
import org.anchoranalysis.core.functional.PredicateWithException;
import org.anchoranalysis.image.extent.BoundingBox;

/**
 * A custom "stream" like class for various functional-programming operations on {@link ObjectCollection}
 * @author Owen Feehan
 *
 */
public final class ObjectMaskStream {

	private ObjectCollection delegate;
	
	public ObjectMaskStream(ObjectCollection delegate) {
		super();
		this.delegate = delegate;
	}
	
	/**
	 * Creates a new {@link ObjectCollection} after mapping each item to another
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param <E> exception-type that can occur during mapping
	 * @param mapFunc performs mapping
	 * @return a newly created object-collection
	 * @throws E if an exception is thrown by the mapping function.
	 */
	public <E extends Throwable> ObjectCollection map(FunctionWithException<ObjectMask,ObjectMask,E> mapFunc) throws E {
		ObjectCollection out = new ObjectCollection();
		for( ObjectMask om : delegate) {
			out.add(
				mapFunc.apply(om)
			);
		}
		return out;
	}
		
	/**
	 * Creates a new {@link ObjectCollection} after mapping the bounding-box on each object
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param mapFunc maps the bounding-box to a new bounding-box
	 * @return a newly created object-collection
	 */
	public ObjectCollection mapBoundingBox(Function<BoundingBox,BoundingBox> mapFunc) {
		return map( om->
			om.mapBoundingBox(mapFunc)
		);
	}
	
	/**
	 * Creates a new {@link List} after mapping each item to another type
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 *
	 * @param <T> destination type for the mapping
	 * @param <E> exception that can be thrown during mapping
	 * @param mapFunc performs mapping
	 * @return a newly created list contained the mapped objects
	 * @throws E if an exception occurs during mapping
	 */
	public <T,E extends Throwable> List<T> mapToList(FunctionWithException<ObjectMask,T,E> mapFunc) throws E {
		List<T> out = new ArrayList<T>();
		for( ObjectMask obj : delegate) {
			out.add(
				mapFunc.apply(obj)
			);
		}
		return out;
	}
		
	
	/**
	 * Creates a new {@link ObjectCollection} after mapping each item to several others
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param mapFunc performs flat-mapping
	 * @return a newly created object-collection
	 */
	public ObjectCollection flatMap(Function<ObjectMask,ObjectCollection> mapFunc) {
		return new ObjectCollection(
			delegate.streamStandardJava().flatMap( element -> 
				mapFunc.apply(element).streamStandardJava()
			)
		);
	}
	
	/**
	 * Like {@link flatMap} but accepts a mapping function that throws a checked exception.
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param <E> exception-type that can be thrown by <code>mapFunc</code>
	 * @param mapFunc performs flat-mapping
	 * @return a newly created object-collection
	 * @throws E if its thrown by <code>mapFunc</code>
	 */
	public <E extends Throwable> ObjectCollection flatMapWithException(
		Class<?> throwableClass,
		FunctionWithException<ObjectMask,ObjectCollection,E> mapFunc
	) throws E {
		return new ObjectCollection(
			FunctionalUtilities.flatMapWithException(
				delegate.streamStandardJava(),
				throwableClass,
				element -> mapFunc.apply(element).asList()
			)
		);
	}
	
	/**
	 * Filters a {@link ObjectCollection} to include certain items based on a predicate
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param predicate iff true object is included, otherwise excluded
	 * @return a newly created object-collection, a filtered version of all objects
	 */
	public ObjectCollection filter(Predicate<ObjectMask> predicate) {
		return new ObjectCollection(
			delegate.streamStandardJava().filter(predicate)
		);
	}
	
	/**
	 * Filters a {@link ObjectCollection} to include certain items based on a predicate - and optionally store rejected objects.
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param <E> exception-type that can be thrown by the predicate
	 * @param predicate iff true object is included, otherwise excluded
	 * @param objsRejected iff true, any object rejected by the filter is added to this collection
	 * @return a newly created object-collection, a filtered version of all objects
	 * @throws E if thrown by the predicate
	 */
	public <E extends Throwable> ObjectCollection filter(PredicateWithException<ObjectMask,E> predicate, Optional<ObjectCollection> objsRejected) throws E {
		
		ObjectCollection out = new ObjectCollection();
		
		for(ObjectMask current : delegate) {
			
			if (predicate.test(current)) {
				out.add(current);
			} else {

				if (objsRejected.isPresent()) {
					objsRejected.get().add(current);
				}
			}
		}
		return out;
	}	
	
	
	/**
	 * Performs a {@link filter} and then a {@link map}
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param mapFunc performs mapping
	 * @param predicate iff true object is included, otherwise excluded
	 * @return a newly created object-collection, a filtered version of all objects
	 */
	public ObjectCollection filterAndMap(Predicate<ObjectMask> predicate, Function<ObjectMask,ObjectMask> mapFunc) {
		return new ObjectCollection(
			delegate.streamStandardJava().filter(predicate).map(mapFunc)
		);
	}
	
	
	/**
	 * Like {@link filter} but only operates on certain indices of the collection.
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param predicate iff true object is included, otherwise excluded
	 * @param indices which indices of the collection to consider
	 * @return a newly created object-collection, a filtered version of particular elements
	 */
	public ObjectCollection filterSubset(Predicate<ObjectMask> predicate, List<Integer> indices) {
		return new ObjectCollection(
			delegate.streamIndices(indices).filter(predicate)
		);
	}
	
	
	/**
	 * Does the predicate evaluate to true on any object in the collection?
	 * 
	 * @param predicate evaluates to true or false for a particular object
	 * @return true if the predicate returns true on ANY one of the contained objects
	 */
	public boolean anyMatch(Predicate<ObjectMask> predicate) {
		return delegate.streamStandardJava().anyMatch(predicate);
	}
	
	/** Converts to a {@link HashSet} (newly-created) */
	public Set<ObjectMask> toSet() {
		return delegate.streamStandardJava().collect(
			Collectors.toCollection(HashSet::new)
		);
	}
}