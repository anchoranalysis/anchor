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
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;

import lombok.Getter;
import lombok.Setter;

public class SetUpdatable extends UpdatablePointsContainer {

	// START BEAN PROPERTIES
	@BeanField @Getter @Setter
	private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;
	// END BEAN PROPERTIES
	
	private RandomSet<Point3d> setPoints;
	
	private ImageDimensions dim;
	private Mask binaryImage;
	private Channel binaryImageChnl;
	
	@Override
	public void init(Mask binaryImage) throws InitException {
		this.binaryImage = binaryImage;
		this.binaryImageChnl = binaryImage.getChannel();
		
		dim = binaryImage.getDimensions();
		
		setPoints = new RandomSet<>();
		
		BinaryValuesByte bvb = binaryImage.getBinaryValues().createByte();
		addEntireScene(bvb);
	}
	
	// Randomise location
    @Override
	public Optional<Point3d> sample( RandomNumberGenerator randomNumberGenerator ) {
	
    	if (setPoints.isEmpty()) {
    		return Optional.empty();
    	}
    	
    	Point3d point = sampleFromSet(setPoints, randomNumberGenerator);
    	
		assert( point.getX() >= 0 );
		assert( point.getY() >= 0 );
		assert( point.getZ() >= 0 );
		
		assert( point.getX() < getDimensions().getX() );
		assert( point.getY() < getDimensions().getY() );
		assert( point.getZ() < getDimensions().getZ() );
		
		// To hide our internal data from manipulation, even though this (presumably) adds
		//  a bit of overhead
    	return Optional.of(
    		new Point3d(point)
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
		    			setPoints.add(
		    				PointConverter.doubleFromInt(pos)
		    			);
		    		}
		    	}
	    	}
    	}
	}
	
	public Set<Point3d> getSetPoints() {
		return setPoints;
	}
	
	@Override
	public ImageDimensions getDimensions() {
		return dim;
	}
	
	
	@Override
	public void add(MemoForIndex marksExisting, VoxelizedMarkMemo newMark) throws UpdateMarkSetException {
		rmvPointsInMark( newMark );
	}
	
	private void rmvPoint( ReadableTuple3i crntExtentPoint,  ReadableTuple3i crnrPoint ) {
		int xGlobal = crnrPoint.getX() + crntExtentPoint.getX();
		int yGlobal = crnrPoint.getY() + crntExtentPoint.getY();
		int zGlobal = crnrPoint.getZ() + crntExtentPoint.getZ();
		
		Point3d pointGlobal = new Point3d( xGlobal, yGlobal, zGlobal );
		
		setPoints.remove( pointGlobal );
	}
	

	public void rmvPointsInMark(VoxelizedMarkMemo newMark) {
		
		// We add any points in our new mark to the set
		VoxelizedMark pxlMark = newMark.voxelized();
		
		ReadableTuple3i crnrPoint = pxlMark.getBoundingBox().cornerMin();
		
		RegionMembership rm = newMark.getRegionMap().membershipForIndex(regionID);
		byte flags = rm.flags();
		
		BoundedVoxelBox<ByteBuffer> voxelBox = pxlMark.getVoxelBox();
		Extent e = voxelBox.extent();
		
		Point3i crntExtentPoint = new Point3i();
		for (crntExtentPoint.setZ(0); crntExtentPoint.getZ()<e.getZ(); crntExtentPoint.incrementZ()) {
			
			ByteBuffer fb = voxelBox.getPixelsForPlane(crntExtentPoint.getZ());
			
			for (crntExtentPoint.setY(0); crntExtentPoint.getY()<e.getY(); crntExtentPoint.incrementY()) {
				for (crntExtentPoint.setX(0); crntExtentPoint.getX()<e.getX(); crntExtentPoint.incrementX()) {

					byte membership = fb.get( e.offset(crntExtentPoint.getX(), crntExtentPoint.getY()));
					
					if ( !rm.isMemberFlag(membership, flags) ) {
						rmvPoint( crntExtentPoint, crnrPoint );
					}
				}
			}
		}
	}
	
	
	@Override
	public void exchange(MemoForIndex pxlMarkMemoList, VoxelizedMarkMemo oldMark,
			int indexOldMark, VoxelizedMarkMemo newMark) {
		
		addPointsInMark( pxlMarkMemoList, oldMark );
		rmvPointsInMark( newMark );
	}
	
	
	public void addPointsInMark(MemoForIndex marksExisting, VoxelizedMarkMemo markToAdd) {
		// We add any points in our new mark to the set, but only if there's not already a neighbor covering them
		
		// So our first step is to identify any overlapping marks
		List<VoxelizedMarkMemo> neighbors = findNeighbors(marksExisting, markToAdd);
		
		VoxelizedMark pxlMark = markToAdd.voxelized();
		
		ReadableTuple3i crnrPoint = pxlMark.getBoundingBox().cornerMin();
		
		RegionMembership rm = markToAdd.getRegionMap().membershipForIndex(regionID);
		
		BoundedVoxelBox<ByteBuffer> voxelBox = pxlMark.getVoxelBox();
		Extent e = voxelBox.extent();
		
		BinaryValuesByte bvb = binaryImage.getBinaryValues().createByte();
		
		VoxelBox<ByteBuffer> vbBinary = binaryImageChnl.getVoxelBox().asByte();
		
		Point3i crntExtentPoint = new Point3i();
		for (crntExtentPoint.setZ(0); crntExtentPoint.getZ()<e.getZ(); crntExtentPoint.incrementZ()) {
			
			int zGlobal = crnrPoint.getZ() + crntExtentPoint.getZ();
			
			addPointsForSlice(
				crntExtentPoint,
				crnrPoint,
				e,
				voxelBox.getPixelsForPlane(crntExtentPoint.getZ()),
				vbBinary.getPixelsForPlane(zGlobal).buffer(),
				bvb,
				zGlobal,
				rm,
				neighbors
			);
		}
	}
	
	private void addPointsForSlice(		// NOSONAR
		Point3i crntExtentPoint,
		ReadableTuple3i crnrPoint,
		Extent extent,
		ByteBuffer buffer,
		ByteBuffer bbBinaryImage,
		BinaryValuesByte bvb,
		int zGlobal,
		RegionMembership rm,
		List<VoxelizedMarkMemo> neighbors
	) {
		byte flags = rm.flags();
		
		for (crntExtentPoint.setY(0); crntExtentPoint.getY()<extent.getY(); crntExtentPoint.incrementY()) {
			int yGlobal = crnrPoint.getY() + crntExtentPoint.getY();
			
			for (crntExtentPoint.setX(0); crntExtentPoint.getX()<extent.getX(); crntExtentPoint.incrementX()) {
				
				int xGlobal = crnrPoint.getX() + crntExtentPoint.getX();
						
				int globOffset = extent.offset(xGlobal, yGlobal);
				byte posCheck = buffer.get( extent.offset(crntExtentPoint.getX(), crntExtentPoint.getY()));
				if ( rm.isMemberFlag(posCheck, flags) && bbBinaryImage.get(globOffset)==bvb.getOnByte()) {
					
					Point3d pointGlobal = new Point3d( xGlobal, yGlobal, zGlobal );
					
					// Now we check to make sure the point isn't contained in any of its neighbors
					if (!isPointInList(neighbors, pointGlobal)) {
						setPoints.add(pointGlobal);	
					}
				}
			}
		}
		
	}

	private List<VoxelizedMarkMemo> findNeighbors( MemoForIndex all, VoxelizedMarkMemo source) {
		
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
			
			byte membership = memo.getMark().evalPointInside(point);
			if (rm.isMemberFlag(membership, flags)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public void rmv(MemoForIndex marksExisting, VoxelizedMarkMemo mark) throws UpdateMarkSetException {
		addPointsInMark(marksExisting, mark);
	}

	@Override
	public int size() {
		return setPoints.size();
	}
		
	private static <T> T sampleFromSet( RandomSet<T> set, RandomNumberGenerator randomNumberGenerator ) {
		return set.get(
			randomNumberGenerator.sampleIntFromRange(set.size())	
		);
	}
}
