package org.anchoranalysis.image.outline.traverser.contiguouspath;

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
import java.util.Optional;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.outline.traverser.visitedpixels.LoopablePoints;

import lombok.Getter;

/** A list of visited pixels which forms one contiguous path (each pixel neighbours each other) */
public class ContiguousPixelPath {
	
	private List<Point3i> list;
	
	@Getter
	private Optional<Point3i> initialPnt;
	
	@Getter
	private Optional<Point3i> connPnt;
	
	/** With a single initial-point, and maybe a connection point */
	public ContiguousPixelPath( Point3i initialPnt, Point3i connPnt ) {
		this(Optional.of(connPnt));
		this.initialPnt = Optional.of(initialPnt);
		maybeAddPntToClosestEnd(initialPnt);
	}
	
	/** Without any connection-point */
	public ContiguousPixelPath() {
		this(Optional.empty());
		initialPnt = Optional.empty();
	}
	
	private ContiguousPixelPath( Optional<Point3i> connPnt ) {
		this.connPnt = connPnt;
		initialPnt = Optional.empty();
		list = new ArrayList<>();
	}
	
	public ContiguousPixelPath duplicate() {
		ContiguousPixelPath out = new ContiguousPixelPath(connPnt);
		out.list.addAll(list);
		return out;
	}
	
	/**
	 * Adds the point to the closest end of the path....  but only if it neighbours the head or the tail
	 * 
	 * @param pnt the point to add
	 * @return TRUE if point was successfully added, FALSE if the point could not be added
	 */
	public boolean maybeAddPntToClosestEnd(Point3i pnt) {
		
		if (list.isEmpty()) {
			list.add(pnt);
			return true;
		}
		
		// In an effort to keep the outline as connected as possible, we consider the head and tail
		//  of the list, and add the point to the end which minimal distance
		
		int distHead = pnt.distanceMax(head()); 
		int distTail = pnt.distanceMax(tail());
		
		if (distHead==1) {
			// If close to head than tail
			list.add(0, pnt);
			return true;
		} else if (distTail==1) {
			// If close to tail than head
			list.add(pnt);
			return true;
		} else {
			// This point can't be fit to either the head or the tail
			return false;
		}
	}
	
	/** Removes the first numToRemove pixels from the left
	 *  @return Returns the pixels removed (or empty() if numToRemove==0)
	 **/
	public Optional<LoopablePoints> removeLeft( int numToRemove ) {
		
		if (numToRemove==0) {
			return Optional.empty();
		}
		
		List<Point3i> toRemove = copySubList(list, 0, numToRemove); 
		
		list = list.subList( numToRemove, list.size() );
		
		return Optional.of(
			new LoopablePoints(toRemove, list.get(0) )
		);
	}
	
	/** Removes the last numToRemove pixels from the right
	 *  @return Returns the pixels removed (or empty() if numToRemove==0)
	 * */
	public Optional<LoopablePoints> removeRight( int numToRemove ) {
		
		if (numToRemove==0) {
			return Optional.empty();
		}
		
		int finalIndex = list.size()-numToRemove;
		List<Point3i> toRemove = copySubList(list, finalIndex, list.size());
				
		list = list.subList( 0, finalIndex );
		
		return Optional.of(
			new LoopablePoints(
				toRemove,	// Looped
				list.get( list.size() - 1 )
			)
		);
	}
	
	/** Inserts points before existing path. Does not check if they are neighbours. */
	public void insertBefore( List<Point3i> pts ) {
		list.addAll(0, pts);
	}
	
	/** Inserts points at end of existing path. Does not check if they are neighbours. */
	public void insertAfter( List<Point3i> pts ) {
		list.addAll(pts);
	}
	
	public Point3i get(int index) {
		return list.get(index);
	}
	
	public int size() {
		return list.size();
	}

	/** Adds a shift to each point (modifying the existing points in memory), and returns them as a list */
	public List<Point3i> addShift( ReadableTuple3i shift ) {
		for( Point3i relPnt : list) {
			relPnt.add( shift );
		}
		return list;
	}
	
	/** The points associated with the path */
	public List<Point3i> points() {
		return list;
	}
	
	@Override
	public String toString() {
		return String.format(
			"Path ( head=%s\ttail=%s\tsize=%d initialPnt=%s connPnt=%s )",
			head(),
			tail(),
			list.size(),
			pntOrNull(initialPnt),
			pntOrNull(connPnt)
		);
	}
	
	private static String pntOrNull( Optional<Point3i> pnt ) {
		return pnt.map(Point3i::toString).orElse("null");
	}
			
	public Point3i tail() {
		return list.get( list.size() - 1 );
	}
		
	public Point3i head() {
		return list.get(0);
	}
	
	private static List<Point3i> copySubList( List<Point3i> list, int from, int to) {
		return new ArrayList<>( list.subList(from, to) );
	}
	
	public Optional<Integer> indexInitialPnt() {
		return initialPnt.map( pnt->
			list.indexOf(pnt)
		);
	}
}
