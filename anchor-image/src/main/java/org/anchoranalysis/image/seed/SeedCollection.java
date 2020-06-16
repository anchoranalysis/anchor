package org.anchoranalysis.image.seed;

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
import java.util.Iterator;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectMaskCollection;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;

public class SeedCollection implements Iterable<Seed> {

	private ArrayList<Seed> delegate = new ArrayList<>();
	
	public SeedCollection duplicate() {
		
		SeedCollection out = new SeedCollection();
		for ( Seed seed : this ) {
			out.delegate.add(seed.duplicate());
		}
		
		return out;
	}

	public void scaleXY( double scale) throws OperationFailedException {
		
		for( Seed seed : this ) {
			seed.scaleXY(scale);
		}
		
	}
	
	public ObjectMaskCollection createMasks() {
		
		ObjectMaskCollection objMasks = new ObjectMaskCollection();
		
		for( Seed seed : this ) {
			objMasks.add( seed.createMask() );
		}
		
		return objMasks;
	}
	
	public void flattenZ() {
		
		for( Seed seed : delegate ) {
			seed.flattenZ();
		}
	}
	
	public void growToZ(int sz) {
		
		for( Seed seed : delegate ) {
			seed.growToZ(sz);
		}
	}

	public void add(Seed element) {
		delegate.add(element);
	}
	
	public Seed get(int index) {
		return delegate.get(index);
	}


	@Override
	public Iterator<Seed> iterator() {
		return delegate.iterator();
	}

	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	public Seed set(int index, Seed element) {
		return delegate.set(index, element);
	}

	public int size() {
		return delegate.size();
	}
	
	public boolean doSeedsIntersectWithContainingMask( ObjectMask omContaining, VoxelBoxFactoryTypeBound<ByteBuffer> factory ) {
		
		for( int i=0; i<delegate.size(); i++) {
			
			Seed s = delegate.get(i);
			ObjectMask omS = s.createMask();
				
			if (!omS.hasIntersectingPixels(omContaining)) {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean doSeedsIntersect( VoxelBoxFactoryTypeBound<ByteBuffer> factory ) {
		
		for( int i=0; i<delegate.size(); i++) {
			
			Seed s = delegate.get(i);
			
			ObjectMask omS = s.createMask();
			
			for( int j=0; j<i; j++) {
				
				Seed t = delegate.get(j);
				
				ObjectMask omT = t.createMask();
				
				if (omS.hasIntersectingPixels(omT)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean verifySeedsAreInside( Extent e ) {
		for (Seed seed : this) {
			
			ObjectMask om = seed.createMask();
			
			if (!e.contains(om.getBoundingBox())) {
				return false;
			}
		}
		return true;
	}

}
