package org.anchoranalysis.image.objectmask;

/*
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.functional.FunctionalUtilities;
import org.anchoranalysis.core.functional.PredicateWithException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.scale.ScaleFactor;

/**
 * A collection of {@link ObjectMask}
 * 
 * @author Owen Feehan
 *
 */
public class ObjectCollection implements Iterable<ObjectMask> {

	private final List<ObjectMask> delegate;

	/**
	 * Constructor - create with no objects
	 */
	public ObjectCollection() {
		 delegate = new ArrayList<>();
	}
	
	/**
	 * Constructor - create with a single object
	 * 
	 * @param obj object-mask to add to collection
	 */
	public ObjectCollection( ObjectMask ...obj ) {
		this();
		for( ObjectMask om : obj ) {
			add(om);
		}
	}
	
	/**
	 * Constructor - creates a new collection with elements from a stream
	 * 
	 * @param stream objects
	 */
	public ObjectCollection( Stream<ObjectMask> stream ) {
		delegate = stream.collect( Collectors.toList() );
	}
	
	/**
	 * Constructor - creates a new collection with elements copied from existing collections
	 * 
	 * @param objs existing collections to copy from
	 */
	@SafeVarargs
	public ObjectCollection( ObjectCollection ...objs) {
		this();
		for( ObjectCollection collection : objs) {
			addAll(collection);	
		}
	}
	
	/**
	 * Constructor - creates a new collection with elements copied from existing collections
	 * 
	 * @param list1 first existing list to copy from
	 * @param list2 second existing list to copy from
	 */
	public ObjectCollection( List<ObjectMask> list1, List<ObjectMask> list2) {
		this();
		delegate.addAll(list1);	
		delegate.addAll(list2);
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
		for( ObjectMask om : this) {
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
	 * @param mapFunc performs mapping
	 * @return a newly created list contained the mapped objects
	 */
	public <T> List<T> mapAsList(Function<ObjectMask,T> mapFunc) {
		return delegate.stream().map(mapFunc).collect( Collectors.toList() );
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
			stream().flatMap( element -> 
				mapFunc.apply(element).stream()
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
				stream(),
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
			stream().filter(predicate)
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
		
		for(ObjectMask current : this) {
			
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
			stream().filter(predicate).map(mapFunc)
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
			streamIndices(indices).filter(predicate)
		);
	}
	
	
	/** 
	 * Shifts the bounding-box of each object by adding to it i.e. adds a vector to the corner position
	 * 
	 * <p>This is an IMMUTABLE operation.<p>
	 * 
	 * @param shiftBy what to add to the corner position
	 * @return newly created object-collection with shifted corner position and identical extent 
	 **/
	public ObjectCollection shiftBy(ReadableTuple3i shiftBy) {
		return mapBoundingBox( bbox->
			bbox.shiftBy(shiftBy)
		);
	}
	
	public boolean add(ObjectMask e) {
		return delegate.add(e);
	}

	public boolean addAll(ObjectCollection objs) {
		return addAll(objs.delegate);
	}
	
	public boolean addAll(Collection<? extends ObjectMask> c) {
		return delegate.addAll(c);
	}

	public void clear() {
		delegate.clear();
	}

	/** 
	 * Checks if two collections are equal in a shallow way
	 * 
	 * <p>Specifically, objects are tested to be equal using their object references (i.e. they are equal iff they have the same reference)</p>
	 * <p>This is a cheaper equality check than with {@link equalsDeep}</p>
	 * <p>Both collections must have identical ordering.</p> 
	 */
	@Override
	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}
		
	/** 
	 * Checks if two collections are equal in a deeper way
	 * 
	 * <p>Specifically, objects are tested to be equal using a deep byte-by-byte comparison using {@link ObjectMask.equalsDeep}. Their objects do not need to be equal.
	 * <p>This is more expensive equality check than with {@link equalsDeep}, but is useful for comparing objects that were instantiated in different places.</p>
	 * <p>Both collections must have identical ordering.</p>
	 */	
	public boolean equalsDeep(ObjectCollection othr) {
		if (size()!=othr.size()) {
			return false;
		}
		
		for( int i=0; i<size(); i++) {
			if (!get(i).equalsDeep(othr.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public ObjectMask get(int index) {
		return delegate.get(index);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Iterator<ObjectMask> iterator() {
		return delegate.iterator();
	}
	
	public void remove(int index) {
		delegate.remove(index);
	}

	public int size() {
		return delegate.size();
	}

	/**
	 * A string representation of all objects in the collection using their center of gravities (and optionally indices)
	 * 
	 * @param newlines if TRUE a newline separates each item, otherwise a whitespace
	 * @param includeIndices whether to additionally show the index of each item beside its center of gravity
	 * @return a descriptive string of the collection (begining and ending with parantheses)
	 */
	public String toString( boolean newlines, boolean includeIndices ) {
		
		String sep = newlines ? "\n" : " ";
		
		StringBuilder sb = new StringBuilder();
		sb.append("( ");
		for( int index=0; index<delegate.size(); index++) {

			sb.append(
				objectToString(
					delegate.get(index),
					index,
					includeIndices
				)
			);
			sb.append( sep );
		}
		sb.append(")");
		return sb.toString();		
	}

	/** Default string representation of the collection, one line with each object described by its center-of-gravity */
	@Override
	public String toString() {
		return toString(false,false);
	}
	
	/**
	 * Scales every object-mask
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param factor scaling-factor
	 * @param interpolator interpolator
	 * @return a new collection with scaled-masks
	 */
	public ObjectCollection scale(ScaleFactor factor, Interpolator interpolator) {
		return map( om->
			om.scale(factor, interpolator)
		);
	}
	
	public int countIntersectingPixels( ObjectMask om ) {
		
		int cnt = 0;
		for( ObjectMask s : this ) {
			cnt += s.countIntersectingPixels(om);
		}
		return cnt;
	}
	
	public ObjectCollection findObjsWithIntersectingBBox( ObjectMask om ) {
		return filter( omItr->
			omItr.getBoundingBox().intersection().existsWith(om.getBoundingBox())
		);
	}
	
	public void assertObjMasksAreInside( Extent e ) {
		for( ObjectMask om : this ) {
			assert( e.contains( om.getBoundingBox().getCrnrMin()) );
			assert( e.contains( om.getBoundingBox().calcCrnrMax()) );
		}
	}
	
	public BinaryValuesByte getFirstBinaryValuesByte() {
		return get(0).getBinaryValuesByte();
	}

	public BinaryValues getFirstBinaryValues() {
		return get(0).getBinaryValues();
	}
	
	/** Deep copy, including duplicating ObjMasks */
	public ObjectCollection duplicate() {
		return map(ObjectMask::duplicate);
	}
	
	/** Shallow copy of ObjMasks */
	public ObjectCollection duplicateShallow() {
		return new ObjectCollection(stream());
	}
	
	/**
	 * A subset of the collection identified by particular indices.
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param indices index of each element to keep in new collection.
	 * @return newly-created collection with only the indexed elements.
	 */
	public ObjectCollection createSubset( List<Integer> indices ) {
		return new ObjectCollection(
			streamIndices(indices)
		);
	}

	/** 
	 * Exposes the underlying objects as a list
	 * 
	 * <p>Be CAREFUL when manipulating this list, as it is the same list used internally in the object.</p>
	 * 
	 * @return a list with the object-masks in this collection
	 */
	public List<ObjectMask> asList() {
		return delegate;
	}
	
	/**
	 * A stream of object-masks as per Java's standard collections interface
	 * 
	 * @return the stream
	 */
	public Stream<ObjectMask> stream() {
		return delegate.stream();
	}
	
	/** Streams only objects at specific indices */
	private Stream<ObjectMask> streamIndices(List<Integer> indices) {
		return indices.stream().map(this::get);
	}
	
	/** Descriptive string representation of an object-mask */
	private static String objectToString( ObjectMask obj, int index, boolean includeIndex ) {
		String cog = obj.centerOfGravity().toString();
		if (includeIndex) {
			return index + " " + cog;
		} else {
			return cog;
		}
	}
}
