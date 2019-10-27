package ch.ethz.biol.cell.imageprocessing.objmask.provider.assignobjstocfg;

/*
 * #%L
 * anchor-mpp
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
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.ops.ObjMaskMerger;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class RslvdEllipsoidList implements Iterable<RslvdEllipsoid> {
	private List<RslvdEllipsoid> delegate;
	
	public RslvdEllipsoidList() {
		delegate = new ArrayList<>();
	}

	public void add(RslvdEllipsoid element) {
		delegate.add(element);
	}
	
	private void assignTo( RslvdObjMask rom, int index ) {
		delegate.get(index).assignObj(rom);
	}
	
	
	//  Returns TRUE if it was contained in at least one object, FALSE otherwise
	private boolean containedWithinAndAssign( RslvdObjMask rom ) {
		
		int foundIndex = -1;
		boolean foundAlready = false;
		boolean foundMultiple = false;
		
		for( int i=0; i<delegate.size(); i++ ) {
			RslvdEllipsoid re = delegate.get(i); 
			
			if( re.contains(rom)) {
				
				assignTo( rom, i);
				
				if (foundAlready) {
					// Mark both as not-included 
					re.setAsExcluded();
					
					if (foundMultiple==false) {
						delegate.get(foundIndex).setAsExcluded();
						foundMultiple = true;
					}
				} else {
					foundIndex = i;
					foundAlready = true;
				}
			}
		}
		
		return foundAlready;
	}
	
	
	
//  Returns TRUE if it was contained in at least one object, FALSE otherwise
	private boolean containedWithinAndAssignMIP( RslvdObjMask rom ) {
		
		int foundIndex = -1;
		boolean foundAlready = false;
		boolean foundMultiple = false;
		
		for( int i=0; i<delegate.size(); i++ ) {
			RslvdEllipsoid re = delegate.get(i); 
			
			if( re.getObjMask().containsIgnoreZ(rom.getCenterInt())) {
				
				assignTo( rom, i);
				
				if (foundAlready) {
					// Mark both as not-included 
					re.setAsExcluded();
					
					if (foundMultiple==false) {
						delegate.get(foundIndex).setAsExcluded();
						foundMultiple = true;
					}
				} else {
					foundIndex = i;
					foundAlready = true;
				}
			}
		}
		
		return foundAlready;
	}

	@Override
	public Iterator<RslvdEllipsoid> iterator() {
		return delegate.iterator();
	}
	
	
	public int numIncluded() {
		int cnt = 0;
		for( RslvdEllipsoid re : this ) {
			if (re.isIncluded()) {
				cnt++;
			}
		}
		return cnt;
	}

	
	public int numExcluded() {
		int cnt = 0;
		for( RslvdEllipsoid re : this ) {
			if (!re.isIncluded()) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public void addIncluded( Cfg cfg ) {
		for( RslvdEllipsoid re : this ) {
			if (re.isIncluded()) {
				cfg.add(re.getMark());
			}
		}
	}
	
	public void addExcluded( Cfg cfg ) {
		for( RslvdEllipsoid re : this ) {
			if (!re.isIncluded()) {
				cfg.add(re.getMark());
			}
		}
	}
	
	
	// Create merged objects for those included
	public ObjMaskCollection createMergedObjsForIncluded() throws CreateException {
		ObjMaskCollection objsOut = new ObjMaskCollection();
		for( RslvdEllipsoid re : this ) {
			
			if (!re.isIncluded()) {
				continue;
			}
			
			try {
				ObjMaskCollection objsAssigned = re.getAssignedObjs().createObjs();
				
				if (objsAssigned.size()>0) {
					ObjMask omMerged = ObjMaskMerger.merge( objsAssigned);
					objsOut.add(omMerged);	
				} else {
					// Mark as excluded
					// We add an empty object (just to preserve a 1-1 mapping between the inclthe correct total number)
					objsOut.add(
						new ObjMask(
							VoxelBoxFactory.getByte().create(new Extent(1,1,1)))
						);
				}
				
			} catch (OperationFailedException e) {
				throw new CreateException(e);
			}
			
			
		}
		return objsOut;
	}
	
	
	public void excludeBorderXYObjects() {
		for( RslvdEllipsoid re : this ) {
			if( re.atBorderXY()) {
				re.setAsExcluded();
			}
		}
	}
	
	public void excludeEmptyEllipsoids() {
		for( RslvdEllipsoid re : this ) {
			if( re.getAssignedObjs().size()==0) {
				re.setAsExcluded();
			}
		}
	}
	
	
	// Assigns all objects that are contained inside the ellipsoids, so long as they are exclusively in a single ellipsoid
	//   If they are in more than one ellipsoid, we ignore the object, and exclude each ellipsoid
	//   Objects are removed from the list, if they are assigned, or belong to multiple objects
	public void assignContainedWithin( RslvdObjMaskList listObjs )	{
		Iterator<RslvdObjMask> itr = listObjs.iterator();
		while( itr.hasNext() ) {
			
			RslvdObjMask rom = itr.next();
			
			// Find matching index, and assign appropriately
			if (containedWithinAndAssign( rom )) {
				// We remove if it was found in something
				itr.remove();
			}
		}
	}

	
	// Assigns all objects that are contained inside the ellipsoids, so long as they are exclusively in a single ellipsoid
	//   If they are in more than one ellipsoid, we ignore the object, and exclude each ellipsoid
	//   Objects are removed from the list, if they are assigned, or belong to multiple objects
	public void assignContainedWithinMIP( RslvdObjMaskList listObjs )	{
		Iterator<RslvdObjMask> itr = listObjs.iterator();
		while( itr.hasNext() ) {
			
			RslvdObjMask rom = itr.next();
			
			// Find matching index, and assign appropriately
			if (containedWithinAndAssignMIP( rom )) {
				// We remove if it was found in something
				itr.remove();
			}
		}
	}

	public int size() {
		return delegate.size();
	}
	
	
	public static class DistWithIndex {
		private int index;
		private double dist;
		
		public DistWithIndex(int index, double dist) {
			super();
			this.index = index;
			this.dist = dist;
		}

		public int getIndex() {
			return index;
		}

		public double getDist() {
			return dist;
		}

	}
	
	// Returns nullif there are no objects
	private DistWithIndex indexWithMinDist( RslvdObjMask rom ) {
		
		if (delegate.size()==0) {
			return null;
		}
		
		int minIndex = -1;
		double minDist = Double.MAX_VALUE;
		
		// Quick distance (minimum bound) to each ellipsoid
		for( int i=0; i<delegate.size(); i++ ) {
			
			RslvdEllipsoid re = delegate.get(i);

			if (minIndex==-1) {
				minIndex = i;
				minDist = re.distTo( rom.getCenter() );
			} else {
				
				// We calculate our min bound first
				double distMinBound = re.minBoundDistTo( rom.getCenter() );
				
				// If our minDist is higher than our current minimum, then we don't do anything, but if it's lower
				//   then there's a possibility we might fit
				if (distMinBound<minDist) {
					double distReal = re.distTo( rom.getCenter() );
					
					if (distReal<minDist) {
						minIndex = i;
						minDist = distReal;
					}
				}
				
			}
		}
		
		return new DistWithIndex( minIndex, minDist );
	}
	
	
	
	// To be be assigned, after we are done measuring existing distances
	private static class RslvdObjMaskForAssignment {
		private RslvdObjMask rom;
		private int index;
		
		public RslvdObjMaskForAssignment(RslvdObjMask rom, int index) {
			super();
			this.rom = rom;
			this.index = index;
		}

		public RslvdObjMask getRom() {
			return rom;
		}

		public int getIndex() {
			return index;
		}
	}
		
	public boolean assignClosestEllipsoids( RslvdObjMaskList listObjs, double maxDist ) {
		
		boolean assignedAtLeastOne = false;
		
		List<RslvdObjMaskForAssignment> listForAssignment = new ArrayList<>();
		
		Iterator<RslvdObjMask> itr = listObjs.iterator();
		while( itr.hasNext() ) {
			RslvdObjMask rom = itr.next();
		
			DistWithIndex distWithIndex = indexWithMinDist( rom );
			assert( distWithIndex!=null );
			
			if (maxDist>0.0) {
				// Skip if we have more than our maxDist
				if (distWithIndex.getDist()>maxDist) {
					continue;
				}
			}
			
			// We don't assign straight-away, as it would distort the measurements, so we s
			
			// Simply assign to this object then
			//listEllipsoids.assignTo(rom, index);
			assignedAtLeastOne = true;
			listForAssignment.add( new RslvdObjMaskForAssignment(rom, distWithIndex.getIndex()) );
			
			itr.remove();
			

		}
		for( RslvdObjMaskForAssignment romForAssignment : listForAssignment ) {
			assignTo( romForAssignment.getRom(), romForAssignment.getIndex() );
		}
		
		return assignedAtLeastOne;
	}
}