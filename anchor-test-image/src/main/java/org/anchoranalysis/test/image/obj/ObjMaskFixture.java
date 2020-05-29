package org.anchoranalysis.test.image.obj;

/*-
 * #%L
 * anchor-test-feature-plugins
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

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBoxByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public class ObjMaskFixture {

	private ImageDim dim;
	
	public ObjMaskFixture(ImageDim dim ) {
		this.dim = dim;
	}
	
	public ObjMask create1() {
		Extent extent = new Extent(20,34,11);
		CutOffCorners pattern = new CutOffCorners(3, 2, extent);
		return createAt(new Point3i(10,15,3), extent, pattern);
	}
	
	public ObjMask create2() {
		Extent extent = new Extent(19,14,5);
		CutOffCorners pattern = new CutOffCorners(5, 1, extent);
		return createAt(new Point3i(3,1,7), extent, pattern);
	}
	
	public ObjMask create3() {
		Extent extent = new Extent(19,14,13);
		CutOffCorners pattern = new CutOffCorners(1, 5, extent);
		return createAt(new Point3i(17,15,2), extent, pattern);
	}
	
	private ObjMask createAt( Point3i crnrMin, Extent extent, VoxelPattern pattern ) {
		BoundingBox bbox = new BoundingBox(crnrMin, extent);
		
		assertTrue( dim.contains(bbox) );
		
		VoxelBox<ByteBuffer> vb = VoxelBoxFactory.instance().getByte().create(extent);
		BinaryValues bv =  BinaryValues.getDefault();
		BinaryValuesByte bvb = bv.createByte();

		boolean atLeastOneHigh = false;
		
		for( int z=0; z<extent.getZ(); z++) {
			VoxelBuffer<ByteBuffer> slice = vb.getPixelsForPlane(z);
		
			for( int y=0; y<extent.getY(); y++) {
				for( int x=0; x<extent.getX(); x++) {
					byte toPut;
					if (pattern.isPixelOn(x, y, z)) {
						toPut = bvb.getOnByte();
						atLeastOneHigh = true;
					} else {
						toPut = bvb.getOffByte();
					}
					slice.putByte( extent.offset(x, y), toPut);
				}
			}
		}
		
		assertTrue(atLeastOneHigh);
		
		return new ObjMask(bbox, new BinaryVoxelBoxByte(vb, bv) );
	}
}
