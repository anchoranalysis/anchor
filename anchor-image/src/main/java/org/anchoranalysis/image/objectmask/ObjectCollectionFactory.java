package org.anchoranalysis.image.objectmask;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.anchoranalysis.core.functional.BiFunctionWithException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.functional.FunctionalUtilities;
import org.anchoranalysis.image.binary.BinaryChnl;

/**
 * Creates {@link ObjectCollection} using various utility and helper methods
 * 
 * @author Owen Feehan
 *
 */
public class ObjectCollectionFactory {

	private ObjectCollectionFactory() {}
	
	
	/**
	 *  Creates a newly created object-collection that is empty
	 *  
	 *  @return a newly-created empty object collection
	 */
	public static ObjectCollection empty() {
		return new ObjectCollection();
	}
	
	
	/**
	 * Creates a new collection with elements from the parameter-list
	 * 
	 * @param obj object-mask to add to collection
	 */
	@SafeVarargs
	public static ObjectCollection from( ObjectMask ...obj ) {
		ObjectCollection out = new ObjectCollection();
		for( ObjectMask om : obj ) {
			out.add(om);
		}
		return out;
	}
	
	/**
	 * Creates a new collection with elements copied from existing collections
	 * 
	 * @param objs existing collections to copy from
	 */
	@SafeVarargs
	public static ObjectCollection from( ObjectCollection ...objs) {
		ObjectCollection out = new ObjectCollection();
		for( ObjectCollection collection : objs) {
			out.addAll(collection);	
		}
		return out;
	}
	
	/**
	 * Creates a new collection with elements copied from existing collections (if they exist)
	 * 
	 * @param objs existing collections to copy from
	 */
	@SafeVarargs
	public static ObjectCollection from( Optional<ObjectCollection> ...objs) {
		ObjectCollection out = new ObjectCollection();
		for( Optional<ObjectCollection> collection : objs) {
			collection.ifPresent(out::addAll);
		}
		return out;
	}
	
	/**
	 * Creates a new collection with elements copied from existing collections
	 * 
	 * @param collection one or more collections to add items from
	 */
	@SafeVarargs
	public static ObjectCollection from( Collection<ObjectMask> ...collection) {
		ObjectCollection out = new ObjectCollection();
		for( Collection<ObjectMask> item : collection) {
			out.addAll(item);	
		}
		return out;
	}
	
	
	/**
	 * Creates a new ObjectCollection by mapping an {@link Iterable} to {@link ObjectMask}
	 * 
	 * @param <T> type that will be mapped to {@link ObjectCollection}
	 * @param <E> exception-type that can be thrown during mapping
	 * @param iterable source of entities to be mapped
	 * @param mapFunc function for mapping
	 * @return a newly created ObjectCollection
	 * @throws E exception if it occurs during mapping
	 */
	public static <T,E extends Throwable> ObjectCollection mapFrom( Iterable<T> iterable, FunctionWithException<T,ObjectMask,E> mapFunc ) throws E {
		ObjectCollection out = new ObjectCollection();
		for( T item : iterable ) {
			out.add(
				mapFunc.apply(item)
			);
		}
		return out;
	}
	
	/**
	 * Creates a new ObjectCollection by mapping an {@link Iterable} to {@link Optional<ObjectMask>}
	 * 
	 * <p>The object is only included in the outgoing collection if Optional.isPresent()<p/>
	 * 
	 * @param <T> type that will be mapped to {@link ObjectCollection}
	 * @param <E> exception-type that can be thrown during mapping
	 * @param collection incoming collection to be mapped
	 * @param mapFunc function for mapping
	 * @return a newly created ObjectCollection
	 * @throws E exception if it occurs during mapping
	 */
	public static <T,E extends Throwable> ObjectCollection mapFromOptional( Iterable<T> iterable, FunctionWithException<T,Optional<ObjectMask>,E> mapFunc ) throws E {
		ObjectCollection out = new ObjectCollection();
		for( T item : iterable ) {
			mapFunc.apply(item).ifPresent(out::add);
		}
		return out;
	}
	
	
	/**
	 * Creates a new collection with elements from the parameter-list of {@link BinaryChnl} converting each channel to an object-mask
	 * 
	 * @param obj object-mask to add to collection
	 */
	@SafeVarargs
	public static ObjectCollection from( BinaryChnl ...chnl ) {
		ObjectCollection out = new ObjectCollection();
		for( BinaryChnl bc : chnl ) {
			out.add(
				new ObjectMask(bc.binaryVoxelBox())
			);
		}
		return out;
	}
	
	
	/**
	 * Creates a new {@link ObjectCollection} from a set of {@link ObjectMask}
	 * 
	 * @param set set
	 * @return the newly created collection
	 */
	public static ObjectCollection fromSet(Set<ObjectMask> set) {
		return new ObjectCollection( set.stream() );
	}
	
	/**
	 * Creates a new {@link ObjectCollection} by repeatedly calling a function to create a single {@link ObjectMask}
	 * 
	 * @param repeats the number of objects created
	 * @param createObjectMask creates a new object-mask
	 * @return a newly created ObjectCollection
	 */
	public static ObjectCollection fromRepeated(int repeats, Supplier<ObjectMask> createObjectMask ) {
		return mapFromRange(
			0,
			repeats,
			index->createObjectMask.get()
		);
	}
	
	/**
	 * Creates a new ObjectCollection by mapping integers (from a range) each to a {@link ObjectMask}
	 * 
	 * @param startInclusive start index for the integer range (inclusive)
	 * @param endExclusive end index for the integer range (exclusive)
	 * @param mapFunc function for mapping
	 * @return a newly created ObjectCollection
	 */
	public static ObjectCollection mapFromRange(int startInclusive, int endExclusive, IntFunction<ObjectMask> mapFunc ) {
		return new ObjectCollection(
			IntStream.range(startInclusive, endExclusive).mapToObj(mapFunc)
		);
	}
	
	/**
	 * Creates a new ObjectCollection by flat-mapping integers (from a range) each to a {@link ObjectMaskCollection}
	 * 
	 * @param startInclusive start index for the integer range (inclusive)
	 * @param endExclusive end index for the integer range (exclusive)
	 * @param mapFunc function for flat-mmapping
	 * @return a newly created ObjectCollection
	 */
	public static ObjectCollection flatMapFromRange(int startInclusive, int endExclusive, IntFunction<ObjectCollection> mapFunc ) {
		return new ObjectCollection(
			IntStream
				.range(startInclusive, endExclusive)
				.mapToObj(mapFunc)
				.flatMap(ObjectCollection::streamStandardJava)
		);
	}
	
	
	/**
	 * Creates a new ObjectCollection by filtering an iterable and then mapping it to {@link ObjectMask}
	 * 
	 * @param <T> type that will be mapped to {@link ObjectCollection}
	 * @param <E> exception-type that may be thrown during mapping
	 * @param iterable incoming collection to be mapped
	 * @param mapFunc function for mapping
	 * @return a newly created ObjectCollection
	 * @throws E if thrown by <code>mapFunc</code>
	 */
	public static <T,E extends Throwable> ObjectCollection filterAndMapFrom( Iterable<T> iterable, Predicate<T> predicate, FunctionWithException<T,ObjectMask,E> mapFunc ) throws E {
		ObjectCollection out = new ObjectCollection();
		for( T item : iterable) {
			
			if (!predicate.test(item)) {
				continue;
			}
			
			out.add(
				mapFunc.apply(item)	
			);
		}
		return out;
	}
	
	/**
	 * Creates a new ObjectCollection by filtering and a list and then mapping it to {@link ObjectMask}
	 * 
	 * @param <T> type that will be mapped to {@link ObjectCollection}
	 * @param <E> exception that be thrown during mapping
	 * @param list incoming list to be mapped
	 * @param mapFuncWithIndex function for mapping, also including an index (the original position in the bounding-box)
	 * @return a newly created ObjectCollection
	 * @throws E 
	 * @throw E if an exception is thrown during mapping
	 */
	public static <T,E extends Throwable> ObjectCollection filterAndMapWithIndexFrom( List<T> list, Predicate<T> predicate, BiFunctionWithException<T,Integer,ObjectMask,E> mapFuncWithIndex ) throws E {
		ObjectCollection out = new ObjectCollection();
		for( int i=0; i<list.size(); i++) {
			
			T item = list.get(i);
			
			if (!predicate.test(item)) {
				continue;
			}
			
			out.add(
				mapFuncWithIndex.apply(item, i)	
			);
		}
		return out;
	}
		
	/**
	 * Creates a new ObjectCollection by flatMapping an incoming stream to {@link ObjectCollection}
	 * 
	 * @param <T> type that will be flatMapped to {@link ObjectCollection}
	 * @param collection incoming collection to be flat-mapped
	 * @param mapFunc function for mapping
	 * @return a newly created ObjectCollection
	 */
	public static <T> ObjectCollection flatMapFrom( Collection<T> collection, Function<T,ObjectCollection> mapFunc ) {
		return new ObjectCollection(
			collection.stream().flatMap(t->mapFunc.apply(t).streamStandardJava() )
		);
	}
	
	/**
	 * Creates a new ObjectCollection by flatMapping an incoming stream to {@link ObjectCollection} AND rethrowing any exception during mapping
	 * 
	 * @param <T> type that will be flatMapped to {@link ObjectCollection}
	 * @param stream incoming stream to be flat-mapped
	 * @param throwableClass the class of the exception that might be thrown during mapping
	 * @param mapFunc function for flat-mapping
	 * @return a newly created ObjectCollection
	 * @throws E exception of it occurs during mapping
	 */
	public static <T,E extends Throwable> ObjectCollection flatMapFrom( Stream<T> stream, Class<?> throwableClass, FunctionWithException<T,ObjectCollection,E> mapFunc) throws E {
		return flatMapFromCollection(
			stream,
			throwableClass,
			source -> mapFunc.apply(source).asList()
		);
	}
	
	/**
	 * Creates a new ObjectCollection by flatMapping an incoming stream to {@link Collection<ObjectMask>} AND rethrowing any exception during mapping
	 * 
	 * @param <T> type that will be flatMapped to {@link ObjectCollection}
	 * @param stream incoming stream to be flat-mapped
	 * @param throwableClass the class of the exception that might be thrown during mapping
	 * @param mapFunc function for flat-mapping
	 * @return a newly created ObjectCollection
	 * @throws E exception of it occurs during mapping
	 */
	public static <T,E extends Throwable> ObjectCollection flatMapFromCollection( Stream<T> stream, Class<?> throwableClass, FunctionWithException<T,Collection<? extends ObjectMask>,E> mapFunc) throws E {
		return new ObjectCollection(
			FunctionalUtilities.flatMapWithException(
				stream,
				throwableClass,
				mapFunc
			)
		);
	}
}
