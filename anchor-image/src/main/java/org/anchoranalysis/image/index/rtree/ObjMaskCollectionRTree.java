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
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

/**
 * An R-Tree of bounding boxes. The index of the item in a list, determines an integer ID, associated with the item in the R-Tree.
 * 
 * @author Owen Feehan
 *
 */
public class ObjMaskCollectionRTree {

	private BBoxRTree delegate;
	private ObjMaskCollection objs;
	
	public ObjMaskCollectionRTree( ObjMaskCollection objs ) {
		this.objs = objs;
		delegate = new BBoxRTree( objs.size() );
		
		for( int i=0; i<objs.size(); i++ ) {
			
			ObjMask om = objs.get(i);
			
			delegate.add(i, om.getBoundingBox());
		}
	}
	
	public ObjMaskCollection contains( Point3i pnt ) {
		List<Integer> indices = delegate.contains( pnt );
		
		// We do an additional check to make sure the point is inside the object,
		//  as points can be inside the Bounding Box but not inside the object
		ObjMaskCollection out = new ObjMaskCollection();
		
		for( int i : indices) {
			ObjMask om = objs.get(i);
			if (om.contains(pnt)) {
				out.add(om);
			}
		}
		return out;
	}
	
	public ObjMaskCollection intersectsWith( ObjMask om ) {
		List<Integer> indices = delegate.intersectsWith( om.getBoundingBox() );
		
		// We do an additional check to make sure the point is inside the object,
		//  as points can be inside the Bounding Box but not inside the object
		ObjMaskCollection out = new ObjMaskCollection();
		
		for( int i : indices) {
			ObjMask omInd = objs.get(i);
			if (omInd.hasIntersectingPixels(om)) {
				out.add(omInd);
			}
		}
		return out;
	}
	
	public ObjMaskCollection intersectsWith( BoundingBox bbox ) {
		List<Integer> indices = delegate.intersectsWith(bbox);
		return objs.createSubset(indices);
	}
	
	public List<Integer> intersectsWithAsIndices( BoundingBox bbox ) {
		return delegate.intersectsWith(bbox);
	}
}
