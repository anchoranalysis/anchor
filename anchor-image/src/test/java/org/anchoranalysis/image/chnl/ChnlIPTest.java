package org.anchoranalysis.image.chnl;

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


import static org.junit.Assert.*;

import java.nio.FloatBuffer;

import org.anchoranalysis.image.chnl.factory.ChnlFactorySingleType;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryFloat;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;
import org.junit.Test;

public class ChnlIPTest {

	@Test
	public void testSetPixelsForPlane() {
		
		ChnlFactorySingleType imgChnlFloatFactory = new ChnlFactoryFloat();
		
		ImageDim sd = new ImageDim(
			new Extent(2,2,1)
		);
		
		Chnl ic = imgChnlFloatFactory.createEmptyInitialised( sd );
		
		float[] arr = new float[]{ 1, 2, 3, 4 };
		
		VoxelBox<FloatBuffer> vb = ic.getVoxelBox().asFloat(); 
		vb.getPlaneAccess().setPixelsForPlane(0, VoxelBufferFloat.wrap(arr) );
		
		double delta = 1e-3;
		assertEquals( 1.0f, vb.getVoxel(0, 0, 0), delta );
		assertEquals( 2.0f, vb.getVoxel(1, 0, 0), delta );
		assertEquals( 3.0f, vb.getVoxel(0, 1, 0), delta );
		assertEquals( 4.0f, vb.getVoxel(1, 1, 0), delta );
	}

}
