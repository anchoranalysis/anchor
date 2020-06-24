package org.anchoranalysis.image.index.rtree;

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


import java.util.List;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ObjectCollection;

/**
 * An R-Tree of bounding boxes. The index of the item in a list, determines an integer ID, associated with the item in the R-Tree.
 * 
 * @author Owen Feehan
 *
 */
public class ObjMaskCollectionRTree {

	private BBoxRTree delegate;
	private ObjectCollection objs;
	
	public ObjMaskCollectionRTree( ObjectCollection objs ) {
		this.objs = objs;
		delegate = new BBoxRTree( objs.size() );
		
		for( int i=0; i<objs.size(); i++ ) {
			
			ObjectMask om = objs.get(i);
			
			delegate.add(i, om.getBoundingBox());
		}
	}
	
	public ObjectCollection contains( Point3i pnt ) {
		// We do an additional check to make sure the point is inside the object,
		//  as points can be inside the Bounding Box but not inside the object
		return objs.stream().filterSubset(
			om->om.contains(pnt),
			delegate.contains(pnt)
		);
	}
		
	public ObjectCollection intersectsWith( ObjectMask om ) {
		// We do an additional check to make sure the point is inside the object,
		//  as points can be inside the Bounding Box but not inside the object
		return objs.stream().filterSubset(
			omInd->omInd.hasIntersectingPixels(om),
			delegate.intersectsWith( om.getBoundingBox() )
		);
	}
	
	public ObjectCollection intersectsWith( BoundingBox bbox ) {
		return objs.createSubset(
			delegate.intersectsWith(bbox)
		);
	}
	
	public List<Integer> intersectsWithAsIndices( BoundingBox bbox ) {
		return delegate.intersectsWith(bbox);
	}
}
