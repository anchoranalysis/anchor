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


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

/**
 * A collection of {@link ObjectMask}
 * 
 * @author Owen Feehan
 *
 */
public class ObjectCollection implements Iterable<ObjectMask> {

	private final List<ObjectMask> delegate;

	public ObjectCollection() {
		 delegate = new ArrayList<>();
	}
	
	public ObjectCollection( List<ObjectMask> list ) {
		delegate = list;
	}
		
	public ObjectCollection( ObjectMask om ) {
		this();
		add(om);
	}
	
	public ObjectCollection flattenZ() {
		ObjectCollection out = new ObjectCollection();
		for( ObjectMask om : delegate ) {
			out.add( om.flattenZ() );
		}
		return out;
	}
	
	public ObjectCollection extractSlice( int z, boolean keepZ ) throws OperationFailedException {
		ObjectCollection out = new ObjectCollection();
		for( ObjectMask om : delegate ) {
			
			if (om.getBoundingBox().contains().z(z)) {
				out.add( om.extractSlice(z, keepZ ) );
			}
		}
		return out;
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

	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	public boolean containsAll(Collection<?> arg0) {
		return delegate.containsAll(arg0);
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

	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	public ListIterator<ObjectMask> listIterator() {
		return delegate.listIterator();
	}

	public ListIterator<ObjectMask> listIterator(int arg0) {
		return delegate.listIterator(arg0);
	}

	public ObjectMask remove(int index) {
		return delegate.remove(index);
	}

	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	public boolean removeAll(Collection<?> arg0) {
		return delegate.removeAll(arg0);
	}

	public ObjectMask set(int index, ObjectMask element) {
		return delegate.set(index, element);
	}

	public int size() {
		return delegate.size();
	}

	public List<ObjectMask> subList(int arg0, int arg1) {
		return delegate.subList(arg0, arg1);
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
	
	public void addToBBoxCrnrMin( ReadableTuple3i toAdd ) {
		
		for (ObjectMask mask : this) {
			mask.getBoundingBox().shiftBy(toAdd);
		}
	}
	
	public void scale( ScaleFactor sf, Interpolator interpolator ) throws OperationFailedException {
		for (ObjectMask mask : this) {
			mask.scale(sf, interpolator);
		}
	}
	
	public boolean contains( Point3i pnt ) {
		
		for (ObjectMask mask : this) {
			if (mask.contains(pnt)) {
				return true;
			}
		}
		
		return false;
	}
	
	public int containsIndex( Point3i pnt ) {
		
		for (int i=0; i<size(); i++) {
			
			ObjectMask mask = get(i);
			if (mask.contains(pnt)) {
				return i;
			}
		}
		
		return -1;
	}
	
	// objsRemoved is optional
	public void rmvBorderMasks2D( ImageDim sd, ObjectCollection objsRemoved ) {

		Iterator<ObjectMask> itr = iterator();
		while ( itr.hasNext() ) {
			
			ObjectMask mask = itr.next();
			
			if ( mask.getBoundingBox().atBorderXY(sd) ) {
				
				if (objsRemoved!=null) {
					objsRemoved.add( mask );
				}
				
				itr.remove();
			}
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
	
	
	private static boolean doesOverlap( ObjectMask mask, List<ObjectMask> listMasks, int index ) {
		
		for (int i=index+1; i<listMasks.size(); i++) {
			
			ObjectMask maskOther = listMasks.get(i);
			
			if (mask.countIntersectingPixels(maskOther)>0) {
				return true;
			}
		}
		return false;
	}
	
	public void rmvOverlappingMasks() {
		
		ArrayList<Integer> toDelete = new ArrayList<>();
		
		for (int i=0; i<delegate.size(); i++) {
			
			ObjectMask mask = delegate.get(i);
			
			if (doesOverlap(mask, delegate, i)) {
				// We add the index to the front of the list, so we delete in descending order
				toDelete.add(0,i);
			}
		}
		
		for (Integer index : toDelete) {
			// We need to get the intValue() otherwise the wrong version of remove is called (with object, rather than index)
			delegate.remove( index.intValue() );
		}
	}
	
	/**
	 * Merges all objects together, and then splits again into connected components
	 * 
	 * @param bbox a bounding box that contains all objects, allowing us to avoid writing to a full buffer
	 * @return
	 */
	public VoxelBox<ByteBuffer> merge( BoundingBox bbox ) {
		
		VoxelBox<ByteBuffer> out = VoxelBoxFactory.getByte().create( bbox.extent() );
		
		Point3i crnrSub = Point3i.immutableScale(bbox.getCrnrMin(), -1);
		
		// We write each object to the buffer
		for( ObjectMask om : this) {
			
			BoundingBox bbNew = om.getBoundingBox().shiftBy(crnrSub);
			
			ObjectMask objMaskRel = new ObjectMask( bbNew, om.getVoxelBox() );

			out.setPixelsCheckMask(objMaskRel, -1);
		}
		return out;
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
	
	public ObjectCollection growBuffer( Point3i neg, Point3i pos, Extent clipRegion ) throws OperationFailedException {
		ObjectCollection omc = new ObjectCollection();
		for (ObjectMask om : this) {
			omc.add(
				om.growBuffer(
					neg,
					pos,
					Optional.of(clipRegion)
				)
			);
		}
		return omc;
	}
	
	public void convertToMaxIntensityProjection() {
		for (ObjectMask om : this) {
			om.convertToMaxIntensityProjection();
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
		ObjectCollection out = new ObjectCollection();
		for (ObjectMask om : this) {
			out.add( om.duplicate() );
		}
		return out;
	}
	
	/** Shallow copy of ObjMasks */
	public ObjectCollection duplicateShallow() {
		ObjectCollection out = new ObjectCollection();
		for (ObjectMask om : this) {
			out.add(om);
		}
		return out;
	}
	
	public ObjectCollection createSubset( List<Integer> indices ) {
		ObjectCollection out = new ObjectCollection();
		for( int i : indices ) {
			out.add( get(i) );
		}
		return out;
	}

	public List<ObjectMask> asList() {
		return delegate;
	}
}
