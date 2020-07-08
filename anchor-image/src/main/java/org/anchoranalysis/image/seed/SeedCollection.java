package org.anchoranalysis.image.seed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;

public class SeedCollection implements Iterable<Seed> {

	private List<Seed> delegate = new ArrayList<>();
	
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
	
	public ObjectCollection createMasks() {
		return ObjectCollectionFactory.mapFrom(delegate, Seed::createMask);
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
	
	public boolean doSeedsIntersectWithContainingMask( ObjectMask omContaining ) {
		
		for( int i=0; i<delegate.size(); i++) {
			
			Seed s = delegate.get(i);
			ObjectMask omS = s.createMask();
				
			if (!omS.hasIntersectingPixels(omContaining)) {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean doSeedsIntersect() {
		
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

	public boolean isEmpty() {
		return delegate.isEmpty();
	}

}
