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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.convert.ByteConverter;
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
	public ObjectCollection( ObjectMask obj ) {
		this();
		add(obj);
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
	 * @param mapFunc performs mapping
	 * @return a newly created object-collection
	 */
	public ObjectCollection map(Function<ObjectMask,ObjectMask> mapFunc) {
		return new ObjectCollection(
			delegate.stream().map(mapFunc)
		);
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
	 * Filters a {@link ObjectCollection} to include certain items based on a predicate
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param mapFunc performs mapping
	 * @return a newly created object-collection, a filtered version of all objects
	 */
	public ObjectCollection filter(Predicate<ObjectMask> predicate) {
		return new ObjectCollection(
			stream().filter(predicate)
		);
	}
	
	/**
	 * Like {@link filter} but only operates on certain indices of the collection.
	 * 
	 * <p>This is an IMMUTABLE operation.</p>
	 * 
	 * @param mapFunc performs mapping
	 * @param indices which indices of the collection to consider
	 * @return a newly created object-collection, a filtered version of particular elements
	 */
	public ObjectCollection filterSubset(Predicate<ObjectMask> predicate, List<Integer> indices) {
		return new ObjectCollection(
			streamIndices(indices).filter(predicate)
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

	public int indexOf(Object o) {
		return delegate.indexOf(o);
	}

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Iterator<ObjectMask> iterator() {
		return delegate.iterator();
	}

	public ObjectMask remove(int index) {
		return delegate.remove(index);
	}

	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	public int size() {
		return delegate.size();
	}

	public String toString( boolean newlines, boolean indexes ) {
		
		String sep = newlines ? "\n" : " ";
		
		StringBuilder sb = new StringBuilder();
		sb.append("(  ");
		for( int i=0; i<delegate.size(); i++) {
			
			 ObjectMask om = delegate.get(i);
			 
			if (indexes) {
				sb.append(i);
				sb.append(" ");
			}
			
			sb.append( om.centerOfGravity() );
			sb.append( sep );
		}
		sb.append("  )");
		return sb.toString();		
	}

	@Override
	public String toString() {
		return toString(false,false);
	}
	
	public void scale( ScaleFactor sf, Interpolator interpolator ) throws OperationFailedException {
		for (ObjectMask mask : this) {
			mask.scale(sf, interpolator);
		}
	}
	
	public void rmvNumPixelsLessThan( int numPixels, Optional<ObjectCollection> objsRemoved ) {

		Iterator<ObjectMask> itr = iterator();
		while ( itr.hasNext() ) {
			
			ObjectMask mask = itr.next();
			
			if ( mask.getVoxelBox().countEqual( ByteConverter.unsignedByteToInt( mask.getBinaryValuesByte().getOnByte()) ) < numPixels ) {
				
				if (objsRemoved.isPresent()) {
					objsRemoved.get().add( mask );
				}
				
				itr.remove();
			}
		}
	}
	
	public int countIntersectingPixels( ObjectMask om ) {
		
		int cnt = 0;
		for( ObjectMask s : this ) {
			cnt += s.countIntersectingPixels(om);
		}
		return cnt;
	}
	
	public ObjectCollection findObjsWithIntersectingBBox( ObjectMask om ) {
		ObjectCollection omc = new ObjectCollection();
		for (ObjectMask omItr : this) {
			if (omItr.getBoundingBox().intersection().existsWith(om.getBoundingBox())) {
				omc.add(omItr);
			}
		}
		return omc;
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
	
	public Stream<ObjectMask> stream() {
		return delegate.stream();
	}
	
	/** Streams only objects at specific indices */
	private Stream<ObjectMask> streamIndices(List<Integer> indices) {
		return indices.stream().map(this::get);
	}
}
