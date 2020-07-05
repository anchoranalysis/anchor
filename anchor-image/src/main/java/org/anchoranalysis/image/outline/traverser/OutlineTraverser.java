package org.anchoranalysis.image.outline.traverser;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import java.util.List;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.visitedpixels.VisitedPixels;

public class OutlineTraverser {

	private ObjectMask omOutline;
	private IConsiderVisit visitCondition;
	private boolean useZ;
	private boolean nghb8;
			
	/**
	 * 
	 * NOTE omOutline is MODIFIED. It has each voxel removed after being visited.
	 * 
	 * @param omOutline the object-mask whose outline is traversed (this object-mask is modified to remove visited pixels)
	 * @param visitCondition predicate determining whether to visit a particular pixel or not
	 * @param useZ whether to traverse in Z?
	 * @param nghb8 whether to use bigger neighbourhoods (8 instead of 4, 12 instead of 6 etc.)
	 */
	public OutlineTraverser(ObjectMask omOutline, IConsiderVisit visitCondition, boolean useZ, boolean nghb8) {
		super();
		this.omOutline = omOutline;
		this.visitCondition = visitCondition;
		this.useZ = useZ;
		this.nghb8 = nghb8;
	}
	
	
	/**
	 * Root point is arbitrarily chosen from object
	 * 
	 * @param listOut
	 * @throws TraverseOutlineException
	 */
	public void applyGlobal( List<Point3i> listOut ) throws OperationFailedException {
		OptionalUtilities.ifPresent(
			omOutline.findArbitraryOnVoxel(),
			rootPoint->	applyGlobal( rootPoint, listOut )
		);
	}
	
	/**
	 * 
	 * @param root this root point should exist on the omOutline (in absolute scene co-ordinates)
	 * @param listOut
	 * @throws TraverseOutlineException
	 */
	public void applyGlobal( Point3i root, List<Point3i> listOut ) throws OperationFailedException {

		ReadableTuple3i crnrMin = omOutline.getBoundingBox().cornerMin();
		Point3i rootRel = BoundingBox.relPosTo(root, crnrMin);

		ContiguousPixelPath listOutRel = applyLocal(rootRel);
		listOut.addAll( listOutRel.addShift(crnrMin) );
	}
	
	private ContiguousPixelPath applyLocal( Point3i rootRel ) throws OperationFailedException {

		VisitedPixels visitedPixels = new VisitedPixels();
		PriorityQueueVisit queue = new PriorityQueueVisit();
		
		if(ConsiderNghb.considerVisitMarkRaster(visitCondition, rootRel, 0, omOutline)) {
			queue.add( new Point3iWithDist(rootRel, 0) );
		}
	
		processQueue( queue, visitedPixels );
				
		return visitedPixels.combineToOnePath();
	
	}
	
	// process FIFO
	private void processQueue( PriorityQueueVisit queue, VisitedPixels visitedPixels ) {
		
		while(true) {
			
			Point3iWithDist pnt = queue.pop(visitedPixels);
			
			visit( pnt, queue, visitedPixels );
			
			// We go again, assuming next is not empty
			if (queue.isEmpty()) {
				break;
			}
		}
	}
		
	private void visit( Point3iWithDist pntWithDist, PriorityQueueVisit queue, VisitedPixels visitedPixels ) {
		
		Point3i pnt = pntWithDist.getPoint();

		List<Point3iWithDist> localQueue = considerNghbs( pnt,	pntWithDist.getDist() );
				
		if (pntWithDist.isForceNewPath()) {
			visitedPixels.addNewPath(pnt, pntWithDist.getConnPnt() );
		} else {
			visitedPixels.addVisitedPnt(pnt);
		}
		
		// If there is more than one neighbour, we make sure that a new path is created when it is visited
		if (localQueue.size()>1) {
			queue.addAllWithConn( localQueue, pnt );
		} else {
			queue.addAll( localQueue );
		}
	}
		
	private List<Point3iWithDist> considerNghbs( Point3i pnt, int dist ) {

		List<Point3iWithDist> out = new ArrayList<>(); 
		
		ConsiderNghb consider = new ConsiderNghb(
			omOutline,
			dist,
			out,
			visitCondition,
			pnt
		);
		consider.considerNghbs(useZ, nghb8);
		return out;
	}
}
