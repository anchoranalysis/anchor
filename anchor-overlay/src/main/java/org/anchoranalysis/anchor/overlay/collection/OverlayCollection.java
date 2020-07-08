package org.anchoranalysis.anchor.overlay.collection;

/*
 * #%L
 * anchor-overlay
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

public class OverlayCollection implements Iterable<Overlay> {

	private List<Overlay> delegate = new ArrayList<>();

	@Override
	public Iterator<Overlay> iterator() {
		return delegate.iterator();
	}

	public Overlay get(int index) {
		return delegate.get(index);
	}

	public Overlay remove(int index) {
		return delegate.remove(index);
	}

	public int size() {
		return delegate.size();
	}
	
	public boolean add(Overlay e) {
		return delegate.add(e);
	}

	public boolean addAll(OverlayCollection c) {
		return delegate.addAll(c.delegate);
	}
	
	public Set<Integer> integerSet() {
		HashSet<Integer> set = new HashSet<Integer>();
		for( Overlay ol : delegate ) {
			set.add( ol.getId() );
		}
		return set;
	}
	
	public List<BoundingBox> bboxList( OverlayWriter maskWriter, ImageDimensions dim ) {
		
		List<BoundingBox> out = new ArrayList<>();
		
		for( Overlay ol : this ) {
			BoundingBox bbox = ol.bbox(maskWriter, dim);
			out.add(bbox);
		}
		return out;
	}
	
	public OverlayCollection shallowCopy() {
		
		OverlayCollection out = new OverlayCollection();
		
		// We copy all the marks
		out.delegate = new ArrayList<>( this.delegate.size() );
		for( Overlay ol : this.delegate ) {
			out.delegate.add( ol );
		}
		
		return out;
	}

	// A hashmap of all the marks, using the Id as an index
	public Set<Overlay> createSet() {
		
		HashSet<Overlay> out = new HashSet<>();
	
		for (Overlay overlay : this) {
			out.add( overlay );
		}
		
		return out;
	}
	
	
	public OverlayCollection createMerged( OverlayCollection toMerge ) {
		
		OverlayCollection mergedNew = shallowCopy();
		
		Set<Overlay> set = mergedNew.createSet();
		
		for (Overlay m : toMerge) {
			if (!set.contains(m)) {
				mergedNew.add( m );
			}
		}
		
		return mergedNew;
	}
	
	
	public OverlayCollection createSubset( IndicesSelection indices ) {

		OverlayCollection out = new OverlayCollection();
		
		// This our current
		for (Overlay ol : this) {
			if (indices.contains(ol.getId())) {
				out.add( ol );
			}
		}
		
		return out;
	}
	
	public List<Overlay> asList() {
		return delegate;
	}
}
