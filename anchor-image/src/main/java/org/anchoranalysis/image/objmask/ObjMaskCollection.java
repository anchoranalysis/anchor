package org.anchoranalysis.image.objmask;

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
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.index.rtree.ObjMaskCollectionRTree;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

public class ObjMaskCollection implements Iterable<ObjMask> {

	private List<ObjMask> delegate;

	public ObjMaskCollection() {
		 delegate = new ArrayList<>();
	}
	
	public ObjMaskCollection( List<ObjMask> list ) {
		delegate = list;
	}
	
	
	public ObjMaskCollection( ObjMask om ) {
		this();
		add(om);
	}
	
	public ObjMaskCollection flattenZ() {
		ObjMaskCollection out = new ObjMaskCollection();
		for( ObjMask om : delegate ) {
			out.add( om.flattenZ() );
		}
		return out;
	}
	
	public ObjMaskCollection extractSlice( int z, boolean keepZ ) throws OperationFailedException {
		ObjMaskCollection out = new ObjMaskCollection();
		for( ObjMask om : delegate ) {
			
			if (om.getBoundingBox().contains().z(z)) {
				out.add( om.extractSlice(z, keepZ ) );
			}
		}
		return out;
	}
	
	public boolean add(ObjMask e) {
		return delegate.add(e);
	}

	public boolean addAll(ObjMaskCollection objs) {
		return addAll(objs.delegate);
	}
	
	public boolean addAll(Collection<? extends ObjMask> c) {
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
	 * <p>Specifically, objects are tested to be equal using a deep byte-by-byte comparison using {@link ObjMask.equalsDeep}. Their objects do not need to be equal.
	 * <p>This is more expensive equality check than with {@link equalsDeep}, but is useful for comparing objects that were instantiated in different places.</p>
	 * <p>Both collections must have identical ordering.</p>
	 */	
	public boolean equalsDeep(ObjMaskCollection othr) {
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
	
	/**
	 * Similar to {@link equalsDeep} but ignores any differences in the ordering of the collectiosn.
	 * 
	 * @param othr to compare to
	 * @return true if both obj mask collections are identical (except for order), false otherwise
	 */
	public boolean equalsIgnoreOrder(ObjMaskCollection othr) {
		if (size()!=othr.size()) {
			return false;
		}
		
		ObjMaskCollectionRTree index = new ObjMaskCollectionRTree(othr);
		
		for( int i=0; i<size(); i++) {
			
			ObjMask curObj = get(i);
			ObjMaskCollection othersWithPoints = index.contains( curObj.findAnyPntOnMask() );
			
			if (othersWithPoints.size()!=1) {
				return false;
			}
			
			ObjMask curOther = othersWithPoints.get(0);
			if (!curObj.equalsDeep(curOther)) {
				return false;
			}
		}
		return true;
	}

	public ObjMask get(int index) {
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
	public Iterator<ObjMask> iterator() {
		return delegate.iterator();
	}

	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	public ListIterator<ObjMask> listIterator() {
		return delegate.listIterator();
	}

	public ListIterator<ObjMask> listIterator(int arg0) {
		return delegate.listIterator(arg0);
	}

	public ObjMask remove(int index) {
		return delegate.remove(index);
	}

	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	public boolean removeAll(Collection<?> arg0) {
		return delegate.removeAll(arg0);
	}

	public ObjMask set(int index, ObjMask element) {
		return delegate.set(index, element);
	}

	public int size() {
		return delegate.size();
	}

	public List<ObjMask> subList(int arg0, int arg1) {
		return delegate.subList(arg0, arg1);
	}

	public String toString( boolean newlines, boolean indexes ) {
		
		String sep = newlines ? "\n" : " ";
		
		StringBuilder sb = new StringBuilder();
		sb.append("(  ");
		for( int i=0; i<delegate.size(); i++) {
			
			 ObjMask om = delegate.get(i);
			 
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
	
	public void addToBBoxCrnrMin( Point3i pnt ) {
		
		for (ObjMask mask : this) {
			mask.getBoundingBox().getCrnrMin().add(pnt);
		}
	}
	
	public void scale( ScaleFactor sf, Interpolator interpolator ) throws OperationFailedException {
		for (ObjMask mask : this) {
			mask.scale(sf, interpolator);
		}
	}
	
	public boolean contains( Point3i pnt ) {
		
		for (ObjMask mask : this) {
			if (mask.contains(pnt)) {
				return true;
			}
		}
		
		return false;
	}
	
	public int containsIndex( Point3i pnt ) {
		
		for (int i=0; i<size(); i++) {
			
			ObjMask mask = get(i);
			if (mask.contains(pnt)) {
				return i;
			}
		}
		
		return -1;
	}
	
	// objsRemoved is optional
	public void rmvBorderMasks2D( ImageDim sd, ObjMaskCollection objsRemoved ) {

		Iterator<ObjMask> itr = iterator();
		while ( itr.hasNext() ) {
			
			ObjMask mask = itr.next();
			
			if ( mask.getBoundingBox().atBorderXY(sd) ) {
				
				if (objsRemoved!=null) {
					objsRemoved.add( mask );
				}
				
				itr.remove();
			}
		}
	}
	
	public void rmvNumPixelsLessThan( int numPixels, Optional<ObjMaskCollection> objsRemoved ) {

		Iterator<ObjMask> itr = iterator();
		while ( itr.hasNext() ) {
			
			ObjMask mask = itr.next();
			
			if ( mask.getVoxelBox().countEqual( ByteConverter.unsignedByteToInt( mask.getBinaryValuesByte().getOnByte()) ) < numPixels ) {
				
				if (objsRemoved.isPresent()) {
					objsRemoved.get().add( mask );
				}
				
				itr.remove();
			}
		}
	}
	
	
	private static boolean doesOverlap( ObjMask mask, List<ObjMask> listMasks, int index ) {
		
		for (int i=index+1; i<listMasks.size(); i++) {
			
			ObjMask maskOther = listMasks.get(i);
			
			if (mask.countIntersectingPixels(maskOther)>0) {
				return true;
			}
		}
		return false;
	}
	
	public void rmvOverlappingMasks() {
		
		ArrayList<Integer> toDelete = new ArrayList<>();
		
		for (int i=0; i<delegate.size(); i++) {
			
			ObjMask mask = delegate.get(i);
			
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
	
	// Merges all objects together, and then splits again into connected components
	// BoundingBox is a bounding box that contains all objects, allowing us to avoid writing to a full buffer
	public VoxelBox<ByteBuffer> merge( BoundingBox bbox ) {
		
		VoxelBox<ByteBuffer> out = VoxelBoxFactory.instance().getByte().create( bbox.extent() );
		
		Point3i crnrSub = new Point3i( bbox.getCrnrMin() );
		crnrSub.scale(-1);
		
		// We write each object to the buffer
		for( ObjMask om : this) {
			
			BoundingBox bbNew = om.getBoundingBox().shift(crnrSub);
			
			ObjMask objMaskRel = new ObjMask( bbNew, om.getVoxelBox() );

			out.setPixelsCheckMask(objMaskRel, -1);
		}
		return out;
		
	}
	
	
	public int countIntersectingPixels( ObjMask om ) {
		
		int cnt = 0;
		for( ObjMask s : this ) {
			
			cnt += s.countIntersectingPixels(om);
		}
		
		return cnt;
	}
	
	public ObjMaskCollection findObjsWithIntersectingBBox( ObjMask om ) {
		ObjMaskCollection omc = new ObjMaskCollection();
		for (ObjMask omItr : this) {
			if (omItr.getBoundingBox().intersection().existsWith(om.getBoundingBox())) {
				omc.add(omItr);
			}
		}
		return omc;
	}
	
	public void assertObjMasksAreInside( Extent e ) {
		for( ObjMask om : this ) {
			assert( e.contains( om.getBoundingBox().getCrnrMin()) );
			assert( e.contains( om.getBoundingBox().calcCrnrMax()) );
		}
	}
	
	public ObjMaskCollection growBuffer( Point3i neg, Point3i pos, Extent clipRegion ) throws OperationFailedException {
		ObjMaskCollection omc = new ObjMaskCollection();
		for (ObjMask om : this) {
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
		for (ObjMask om : this) {
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
	public ObjMaskCollection duplicate() {
		ObjMaskCollection out = new ObjMaskCollection();
		for (ObjMask om : this) {
			out.add( om.duplicate() );
		}
		return out;
	}
	
	/** Shallow copy of ObjMasks */
	public ObjMaskCollection duplicateShallow() {
		ObjMaskCollection out = new ObjMaskCollection();
		for (ObjMask om : this) {
			out.add( om );
		}
		return out;
	}
	
	public ObjMaskCollection createSubset( List<Integer> indices ) {
		ObjMaskCollection out = new ObjMaskCollection();
		for( int i : indices ) {
			out.add( get(i) );
		}
		return out;
	}

	public List<ObjMask> asList() {
		return delegate;
	}

}
