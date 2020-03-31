package org.anchoranalysis.anchor.mpp.pxlmark;

/*-
 * #%L
 * anchor-mpp
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

import java.util.List;
import java.util.function.Consumer;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.pixelpart.IndexByChnl;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.histogram.Histogram;

class AddPxlsToHistogram {

	public static void addPxls(
		byte membership,			
		List<RegionMembershipWithFlags> listRegionMembership,
		IndexByChnl<Histogram> partitionList,
		BufferArrList bufferArrList,
		int globalOffset,
		int z_local
	) {
		iterateRegions( membership, listRegionMembership, r->
			addRegionToPartition(partitionList, bufferArrList, globalOffset, r, z_local)
		);
	}
	
	private static void addRegionToPartition(
		IndexByChnl<Histogram> partitionList,
		BufferArrList bufferArrList,
		int globalOffset,
		int r,
		int z_local
	) {
		for (int i=0; i<partitionList.size(); i++) {
			byte val = bufferArrList.get(i).get(globalOffset);
			partitionList.get(i).addToPxlList(
				r,
				z_local,
				ByteConverter.unsignedByteToInt(val)
			);
		}		
	}
	
	private static void iterateRegions( byte membership, List<RegionMembershipWithFlags> listRegionMembership, Consumer<Integer> func ) {
		// We optimise this
		for( int r=0; r<listRegionMembership.size(); r++) {
			RegionMembershipWithFlags rm = listRegionMembership.get(r);
			if (rm.isMemberFlag(membership)) {
				func.accept(r);
				
			}
		}
	}
	
}
