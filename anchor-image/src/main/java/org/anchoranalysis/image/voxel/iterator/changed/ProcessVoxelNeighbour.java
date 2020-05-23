package org.anchoranalysis.image.voxel.iterator.changed;

import org.anchoranalysis.core.geometry.Point3i;

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


/**
 * Processes a point that is an neighbour of another
 * 
 * @author Owen Feehan
 *
 */
public interface ProcessVoxelNeighbour {

	/**
	 * Specify the source-point (of which all the processed points are neighbours)
	 * 
	 * <p>This must be called before any calls to {@link processPoint}</p>.
	 * @param pntSource
	 */
	void initSource(Point3i pntSource);
	
	/** 
	 * Notifies the processor that there has been a change in z-coordinate
	 * 
	 *  @param zChange the change in the Z-dimension to reach this neighbour relative to the source coordinate
	 *  @return true if processing should continue on this slice, or false if processing should stop for this slice
	 **/
	default boolean notifyChangeZ( int zChange ) { return true; }
	
	/** 
	 * Processes a particular point
	 * 
	 * @param zChange the change in X-dimension to reach this neighbour relative to the source coordinate
	 * @param yChange the change in Y-dimension to reach this neighbour relative to the source coordinate
	 **/
	boolean processPoint(int xChange, int yChange);
	
}
