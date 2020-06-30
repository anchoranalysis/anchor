package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

/*-
 * #%L
 * anchor-plugin-io
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

import java.nio.ByteBuffer;

import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

import loci.common.DataTools;

public class ConvertToByte_From32BitUnsignedInt extends ConvertToByte {

	private double convertRatio;
	private int bytesPerPixel = 4;
	private int sizeXY;
	private int sizeBytes;
	
	private int effectiveBitsPerPixel;
	private boolean littleEndian;

	public ConvertToByte_From32BitUnsignedInt(int effectiveBitsPerPixel, boolean littleEndian) {
		super();
		this.effectiveBitsPerPixel = effectiveBitsPerPixel;
		this.littleEndian = littleEndian;
	}
	
	@Override
	protected void setupBefore( ImageDimensions sd, int numChnlsPerByteArray ) {

		convertRatio = calculateConvertRatio();
		
  		sizeXY = sd.getX() * sd.getY();
  		sizeBytes = sizeXY * bytesPerPixel;
	}
	

	@Override
	protected VoxelBuffer<ByteBuffer> convertSingleChnl( byte[] src, int c_rel ) {
		byte[] crntChnlBytes = new byte[sizeXY];
		
		int indOut = 0;
		for(int indIn =0; indIn<sizeBytes; indIn+=bytesPerPixel) {
			int i = DataTools.bytesToInt( src, indIn, littleEndian);
			crntChnlBytes[indOut++] = (byte) (i*convertRatio);
		}
		return VoxelBufferByte.wrap( crntChnlBytes );
	}
	
	private double calculateConvertRatio() {
		if (effectiveBitsPerPixel==32) {
			return 1.0;
		} else {
			return ConvertHelper.twoToPower(
				-1*(effectiveBitsPerPixel-8)
			);
		}		
	}
}
