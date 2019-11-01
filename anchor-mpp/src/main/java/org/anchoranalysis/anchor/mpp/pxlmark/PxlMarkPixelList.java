package org.anchoranalysis.anchor.mpp.pxlmark;

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

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembership;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.VoxelIntensityList;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactoryTypeBound;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;
import org.anchoranalysis.image.voxel.statistics.VoxelStatisticsFromList;

import ch.ethz.biol.cell.imageprocessing.pixellist.IndexByChnl;
import ch.ethz.biol.cell.imageprocessing.pixellist.factory.PixelPartFactoryPixelList;

public class PxlMarkPixelList extends PxlMark {

	// Quick access to what is inside and what is outside
	private IndexByChnl<VoxelIntensityList> partitionList = null;
	
	public PxlMarkPixelList() {
		partitionList = new IndexByChnl<>();
	}
	
	public PxlMarkPixelList( Mark mark, NRGStack stack, RegionMap regionMap, VoxelBoxFactoryTypeBound<ByteBuffer> factory ) {
		partitionList = new IndexByChnl<>();
		initForMark( mark, stack, regionMap );
	}
	
	@Override
	public boolean equals( Object othr ) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	
	// Calculates the pixels for a mark
	@Override
	public void initForMark( Mark mark, NRGStack stack, RegionMap regionMap ) {
		
		ImageDim sd = stack.getDimensions();
		BoundingBox bbox = mark.bboxAllRegions( sd );
		
		Point3i crnrMax = bbox.calcCrnrMax();
		
		setObjMask( new ObjMask(bbox) );
		
		BoundingBox bboxMIP = new BoundingBox(bbox);
		bboxMIP.convertToMaxIntensityProj();
		setObjMaskMIP( new ObjMask(bboxMIP) );
		
		
		Extent localExtnt = bbox.extnt();
		partitionList.init( new PixelPartFactoryPixelList(), stack.getNumChnl(), mark.numRegions(), localExtnt.getZ() );
		
		ByteBuffer bufferMIP = getObjMaskMIP().getVoxelBox().getPixelsForPlane(0).buffer();
		
		for (int z=bbox.getCrnrMin().getZ(); z<=crnrMax.getZ(); z++) {

			BufferArrList bufferArrList = new BufferArrList();
			bufferArrList.init(stack, z);
			initForSlice(z, mark, bbox, crnrMax, localExtnt, sd, stack, bufferArrList, bufferMIP, regionMap);
		}
	}
	
	private void initForSlice( int z, Mark mark, BoundingBox bbox, Point3i crnrMax, Extent localExtnt, ImageDim sd, NRGStack stack, BufferArrList bufferArrList, ByteBuffer bufferMIP, RegionMap regionMap ) {
		
		Point3d ptRunning = new Point3d();
		ptRunning.setZ( z + 0.5 );
		
		int z_local = z - bbox.getCrnrMin().getZ();
		
		ByteBuffer buffer = getObjMask().getVoxelBox().getPixelsForPlane(z_local).buffer();
		
		for (int y=bbox.getCrnrMin().getY(); y<=crnrMax.getY(); y++) {
			ptRunning.setY( y + 0.5 );
			
			int y_local = y - bbox.getCrnrMin().getY();
			
			for (int x=bbox.getCrnrMin().getX(); x<=crnrMax.getX(); x++) {
				
				ptRunning.setX( x + 0.5 );
								
				int x_local = x - bbox.getCrnrMin().getX();
					
				int localOffset = localExtnt.offset(x_local, y_local);
				int globalOffset = sd.offset(x, y);
				

				byte membership = mark.evalPntInside( new Point3d(ptRunning) );
				buffer.put( localOffset, membership );
				
				byte membershipMIP = bufferMIP.get(localOffset);
				membershipMIP = (byte) (membershipMIP | membership);
				bufferMIP.put(localOffset, membershipMIP);
				
				for( int r=0; r<regionMap.numRegions(); r++) {
					
					RegionMembership rm = regionMap.membershipForIndex(r);
					byte regionFlag = rm.flags();
					
					if (rm.isMemberFlag(membership, regionFlag)) {
						for (int i=0; i<stack.getNumChnl(); i++) {
							byte val = bufferArrList.get(i).get(globalOffset);
							
							int valInt = ByteConverter.unsignedByteToInt(val);
							
							partitionList.get(i).addToPxlList(r, z_local, valInt );
						}
						
					}
				}

			}
		}
		
	}

	@Override
	public VoxelStatistics statisticsForAllSlices( int chnlID, int regionID ) {
		return new VoxelStatisticsFromList( partitionList.get(chnlID).getForAllSlices(regionID) );
	}
	
	@Override
	public VoxelStatistics statisticsFor( int chnlID, int regionID, int sliceID ) {
		return new VoxelStatisticsFromList( partitionList.get(chnlID).getForSlice( regionID, sliceID ) );
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public VoxelStatistics statisticsForAllSlicesMaskSlice(int chnlID,
			int regionID, int maskChnlID) {
		assert false;
		return null;
	}

	/** Does only a shallow copy */
	@Override
	public PxlMark duplicate() {
		PxlMarkPixelList out = new PxlMarkPixelList();
		out.partitionList = partitionList;	// NO DUPLICATION This might need to be changed
		return out;
	}

}
