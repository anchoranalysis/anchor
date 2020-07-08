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
import java.util.List;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramArray;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.statistics.VoxelStatistics;
import org.anchoranalysis.image.voxel.statistics.VoxelStatisticsFromHistogram;

public class PxlMarkHistogram extends PxlMarkWithPartition<Histogram> {

	private PixelPartFactory<Histogram> factoryHistogram;
	
	public PxlMarkHistogram( Mark mark, NRGStack stack, RegionMap regionMap, PixelPartFactory<Histogram> factoryHistogram ) {
		this.factoryHistogram = factoryHistogram;
		initForMark( mark, stack, regionMap );
	}
	
	private PxlMarkHistogram( PxlMarkHistogram src ) {
		super(src);
		this.factoryHistogram = src.factoryHistogram;
	}
	
	/** Does only a shallow copy of partition-list */
	@Override
	public PxlMark duplicate() {
		return new PxlMarkHistogram(this);
	}
	
	// Calculates the pixels for a mark
	@Override
	public void initForMark( Mark mark, NRGStack stack, RegionMap regionMap ) {
		
		ImageDimensions sd = stack.getDimensions();
		BoundingBox bbox = mark.bboxAllRegions( sd );
		
		ReadableTuple3i crnrMax = bbox.calcCornerMax();
		
		setObjMask( new ObjectMask(bbox) );
		
		
		BoundingBox bboxMIP = bbox.flattenZ();
		setObjMaskMIP( new ObjectMask(bboxMIP) );
		
		Extent localExtnt = bbox.extent();
		partitionList.init( factoryHistogram, stack.getNumChnl(), regionMap.numRegions(), localExtnt.getZ() );
		
		ByteBuffer bufferMIP = getObjMaskMIP().getVoxelBox().getPixelsForPlane(0).buffer();
		
		for (int z=bbox.cornerMin().getZ(); z<=crnrMax.getZ(); z++) {

			BufferArrList bufferArrList = new BufferArrList();
			bufferArrList.init(stack, z);
			initForSlice(
				z,
				mark,
				bbox,
				crnrMax,
				localExtnt,
				sd,
				bufferArrList,
				bufferMIP,
				regionMap
			);
		}
	}
	
	private void initForSlice(
		int z,
		Mark mark,
		BoundingBox bbox,
		ReadableTuple3i crnrMax,
		Extent localExtnt,
		ImageDimensions sd,
		BufferArrList bufferArrList,
		ByteBuffer bufferMIP,
		RegionMap regionMap
	) {
		
		Point3d ptRunning = new Point3d();
		ptRunning.setZ( z + 0.5 );
		
		int zLocal = z - bbox.cornerMin().getZ();
		
		List<RegionMembershipWithFlags> listRegionMembership = regionMap.createListMembershipWithFlags();
		
		ByteBuffer buffer = getObjMask().getVoxelBox().getPixelsForPlane(zLocal).buffer();
		
		for (int y=bbox.cornerMin().getY(); y<=crnrMax.getY(); y++) {
			ptRunning.setY( y + 0.5 );
			
			int yLocal = y - bbox.cornerMin().getY();
			
			for (int x=bbox.cornerMin().getX(); x<=crnrMax.getX(); x++) {
				
				ptRunning.setX( x + 0.5 );
								
				int xLocal = x - bbox.cornerMin().getX();
				
				int localOffset = localExtnt.offset(xLocal, yLocal);
				int globalOffset = sd.offset(x, y);
				
				byte membership = mark.evalPntInside( new Point3d(ptRunning) );
				
				buffer.put( localOffset, membership );
				bufferMIP.put(localOffset, membershipMIP(membership, bufferMIP, localOffset) );

				AddPxlsToHistogram.addPxls(
					membership,
					listRegionMembership,
					partitionList,
					bufferArrList,
					globalOffset,
					zLocal
				);
			}
		}
		
	}
	
	private static byte membershipMIP( byte membership, ByteBuffer bufferMIP, int localOffset ) {
		byte membershipMIP = bufferMIP.get(localOffset);
		membershipMIP = (byte) (membershipMIP | membership);
		return membershipMIP;
	}
		
	@Override
	public VoxelStatistics statisticsForAllSlices( int chnlID, int regionID ) {
		return new VoxelStatisticsFromHistogram( partitionList.get(chnlID).getForAllSlices(regionID) );
	}
	
	@Override
	public VoxelStatistics statisticsFor( int chnlID, int regionID, int sliceID ) {
		return new VoxelStatisticsFromHistogram( partitionList.get(chnlID).getForSlice( regionID, sliceID ) );
	}

	@Override
	public void cleanUp() {
		partitionList.cleanUp(factoryHistogram);
	}

	@Override
	public VoxelStatistics statisticsForAllSlicesMaskSlice(int chnlID,
			int regionID, int maskChnlID) {
		
		Histogram h = new HistogramArray(255);
		
		// We loop through each slice
		for (int z=0; z<partitionList.get(0).numSlices(); z++) {

			Histogram hChnl = partitionList.get(chnlID).getForSlice(regionID, z);
			Histogram hMaskChnl = partitionList.get(maskChnlID).getForSlice(regionID, z);
			
			if (hMaskChnl.hasAboveZero()) {
				try {
					h.addHistogram(hChnl);
				} catch (OperationFailedException e) {
					throw new AnchorImpossibleSituationException();
				}
			}
		}

		return new VoxelStatisticsFromHistogram(h);
	}
}
