package org.anchoranalysis.anchor.mpp.bean.points.updatable;

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


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembership;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.set.UpdateMarkSetException;
import org.anchoranalysis.anchor.mpp.overlap.OverlapUtilities;
import org.anchoranalysis.anchor.mpp.pxlmark.VoxelizedMark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.MemoForIndex;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public class SetUpdatable extends UpdatablePointsContainer {

	// START BEAN PROPERTIES
	@BeanField
	private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;
	// END BEAN PROPERTIES
	
	private RandomSet<Point3d> setPnts;
	
	private ImageDimensions dim;
	private BinaryChnl binaryImage;
	private Channel binaryImageChnl;
	
	@Override
	public void init(BinaryChnl binaryImage) throws InitException {
		this.binaryImage = binaryImage;
		this.binaryImageChnl = binaryImage.getChannel();
		
		dim = binaryImage.getDimensions();
		
		setPnts = new RandomSet<>();
		
		BinaryValuesByte bvb = binaryImage.getBinaryValues().createByte();
		addEntireScene(bvb);
	}
	
	// Randomise location
    @Override
	public Optional<Point3d> sample( RandomNumberGenerator re ) {
	
    	if (setPnts.isEmpty()) {
    		return Optional.empty();
    	}
    	
    	int randomIndex = (int) (re.nextDouble() * setPnts.size());
    	Point3d pnt = setPnts.get(randomIndex);
    	
		assert( pnt.getX() >= 0 );
		assert( pnt.getY() >= 0 );
		assert( pnt.getZ() >= 0 );
		
		assert( pnt.getX() < getDimensions().getX() );
		assert( pnt.getY() < getDimensions().getY() );
		assert( pnt.getZ() < getDimensions().getZ() );
		
		// To hide our internal data from manipulation, even though this (presumably) adds
		//  a bit of overhead
    	return Optional.of(
    		new Point3d(pnt)
    	);
    }

	@Override
	public void initUpdatableMarkSet(MemoForIndex marks, NRGStackWithParams nrgStack, Logger logger, SharedFeatureMulti sharedFeatures) throws InitException {
		// NOTHING TO DO
	}
		
	private void addEntireScene( BinaryValuesByte bvb ) {
		
		Extent e = binaryImageChnl.getDimensions().getExtent();
		
		VoxelBox<ByteBuffer> vbBinary = binaryImageChnl.getVoxelBox().asByte();
		
		// Where we actually do the work
		Point3i pos = new Point3i();
		
		ImageDimensions sd = getDimensions();
    	for( pos.setZ(0); pos.getZ()<sd.getZ(); pos.incrementZ()) {
    		
    		ByteBuffer bbBinaryImage = vbBinary.getPixelsForPlane(pos.getZ()).buffer();
    		
	    	for( pos.setY(0); pos.getY()<sd.getY(); pos.incrementY()) {
		    	for( pos.setX(0); pos.getX()<sd.getX(); pos.incrementX()) {
		    		
		    		if( bbBinaryImage.get(e.offsetSlice(pos))==bvb.getOnByte()) {
		    			
		    			assert( pos.getX() >= 0 );
		    			assert( pos.getY() >= 0 );
		    			assert( pos.getZ() >= 0 );
		    			
		    			assert( pos.getX() < getDimensions().getX() );
		    			assert( pos.getY() < getDimensions().getY() );
		    			assert( pos.getZ() < getDimensions().getZ() );
		    			setPnts.add(
		    				PointConverter.doubleFromInt(pos)
		    			);
		    		}
		    	}
	    	}
    	}
	}
	
	public Set<Point3d> getSetPnts() {
		return setPnts;
	}
	
	@Override
	public ImageDimensions getDimensions() {
		return dim;
	}
	
	
	@Override
	public void add(MemoForIndex marksExisting, VoxelizedMarkMemo newMark) throws UpdateMarkSetException {
		rmvPntsInMark( newMark );
	}
	
	private void rmvPnt( ReadableTuple3i crntExtentPnt,  ReadableTuple3i crnrPnt ) {
		int xGlobal = crnrPnt.getX() + crntExtentPnt.getX();
		int yGlobal = crnrPnt.getY() + crntExtentPnt.getY();
		int zGlobal = crnrPnt.getZ() + crntExtentPnt.getZ();
		
		Point3d pntGlobal = new Point3d( xGlobal, yGlobal, zGlobal );
		
		setPnts.remove( pntGlobal );
	}
	

	public void rmvPntsInMark(VoxelizedMarkMemo newMark) {
		
		// We add any points in our new mark to the set
		VoxelizedMark pxlMark = newMark.voxelized();
		
		ReadableTuple3i crnrPnt = pxlMark.getBoundingBox().cornerMin();
		
		RegionMembership rm = newMark.getRegionMap().membershipForIndex(regionID);
		byte flags = rm.flags();
		
		BoundedVoxelBox<ByteBuffer> voxelBox = pxlMark.getVoxelBox();
		Extent e = voxelBox.extent();
		
		Point3i crntExtentPnt = new Point3i();
		for (crntExtentPnt.setZ(0); crntExtentPnt.getZ()<e.getZ(); crntExtentPnt.incrementZ()) {
			
			ByteBuffer fb = voxelBox.getPixelsForPlane(crntExtentPnt.getZ());
			
			for (crntExtentPnt.setY(0); crntExtentPnt.getY()<e.getY(); crntExtentPnt.incrementY()) {
				for (crntExtentPnt.setX(0); crntExtentPnt.getX()<e.getX(); crntExtentPnt.incrementX()) {

					byte membership = fb.get( e.offset(crntExtentPnt.getX(), crntExtentPnt.getY()));
					
					if ( !rm.isMemberFlag(membership, flags) ) {
						rmvPnt( crntExtentPnt, crnrPnt );
					}
				}
			}
		}
	}
	
	
	@Override
	public void exchange(MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo oldMark,
			int indexOldMark, VoxelizedMarkMemo newMark) {
		
		addPntsInMark( pxlMarkMemoList, oldMark );
		rmvPntsInMark( newMark );
	}
	
	
	public void addPntsInMark(MemoForIndex marksExisting, VoxelizedMarkMemo markToAdd) {
		// We add any points in our new mark to the set, but only if there's not already a neighbour covering them
		
		// So our first step is to identify any overlapping marks
		List<VoxelizedMarkMemo> neighbours = findNeighbours(marksExisting, markToAdd);
		
		VoxelizedMark pxlMark = markToAdd.voxelized();
		
		ReadableTuple3i crnrPnt = pxlMark.getBoundingBox().cornerMin();
		
		RegionMembership rm = markToAdd.getRegionMap().membershipForIndex(regionID);
		
		BoundedVoxelBox<ByteBuffer> voxelBox = pxlMark.getVoxelBox();
		Extent e = voxelBox.extent();
		
		BinaryValuesByte bvb = binaryImage.getBinaryValues().createByte();
		
		VoxelBox<ByteBuffer> vbBinary = binaryImageChnl.getVoxelBox().asByte();
		
		Point3i crntExtentPnt = new Point3i();
		for (crntExtentPnt.setZ(0); crntExtentPnt.getZ()<e.getZ(); crntExtentPnt.incrementZ()) {
			
			int zGlobal = crnrPnt.getZ() + crntExtentPnt.getZ();
			
			addPointsForSlice(
				crntExtentPnt,
				crnrPnt,
				e,
				voxelBox.getPixelsForPlane(crntExtentPnt.getZ()),
				vbBinary.getPixelsForPlane(zGlobal).buffer(),
				bvb,
				zGlobal,
				rm,
				neighbours
			);
		}
	}
	
	private void addPointsForSlice(		// NOSONAR
		Point3i crntExtentPnt,
		ReadableTuple3i crnrPnt,
		Extent extent,
		ByteBuffer buffer,
		ByteBuffer bbBinaryImage,
		BinaryValuesByte bvb,
		int zGlobal,
		RegionMembership rm,
		List<VoxelizedMarkMemo> neighbours
	) {
		byte flags = rm.flags();
		
		for (crntExtentPnt.setY(0); crntExtentPnt.getY()<extent.getY(); crntExtentPnt.incrementY()) {
			int yGlobal = crnrPnt.getY() + crntExtentPnt.getY();
			
			for (crntExtentPnt.setX(0); crntExtentPnt.getX()<extent.getX(); crntExtentPnt.incrementX()) {
				
				int xGlobal = crnrPnt.getX() + crntExtentPnt.getX();
						
				int globOffset = extent.offset(xGlobal, yGlobal);
				byte posCheck = buffer.get( extent.offset(crntExtentPnt.getX(), crntExtentPnt.getY()));
				if ( rm.isMemberFlag(posCheck, flags) && bbBinaryImage.get(globOffset)==bvb.getOnByte()) {
					
					Point3d pntGlobal = new Point3d( xGlobal, yGlobal, zGlobal );
					
					// Now we check to make sure the point isn't contained in any of its neighbours
					if (!isPointInList(neighbours, pntGlobal)) {
						setPnts.add(pntGlobal);	
					}
				}
			}
		}
		
	}

	private List<VoxelizedMarkMemo> findNeighbours( MemoForIndex all, VoxelizedMarkMemo source) {
		
		ArrayList<VoxelizedMarkMemo> list = new ArrayList<>();
			
		for (int i=0; i<all.size(); i++) {
			
			VoxelizedMarkMemo pmm = all.getMemoForIndex(i);
			if (pmm!=source && OverlapUtilities.overlapWith(source,pmm,regionID)>0) {
				// We check if there's any overlap
				list.add(pmm);
			}
		}
		return list;
	}
	
	private static boolean isPointInList( List<VoxelizedMarkMemo> all, Point3d point) {
		
		for( VoxelizedMarkMemo memo : all ) {
			
			RegionMembership rm = memo.getRegionMap().membershipForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
			byte flags = rm.flags();
			
			byte membership = memo.getMark().evalPntInside(point);
			if (rm.isMemberFlag(membership, flags)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public void rmv(MemoForIndex marksExisting, VoxelizedMarkMemo mark) throws UpdateMarkSetException {
		addPntsInMark(marksExisting, mark);
	}

	@Override
	public int size() {
		return setPnts.size();
	}

	public int getRegionID() {
		return regionID;
	}

	public void setRegionID(int regionID) {
		this.regionID = regionID;
	}
}
