package org.anchoranalysis.image.binary.voxel;

/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;

public class BinaryVoxelBoxFactory {
	
	private static BinaryVoxelBoxFactory instance;
	
	/** Singleton */
	public static BinaryVoxelBoxFactory instance() {
		if (instance==null) {
			instance = new BinaryVoxelBoxFactory();
		}
		return instance;
	}
	
	/**
	 * Creates a binary-voxel box of a given size using default factory and unsigned byte type
	 * 
	 * @param extent the size of the voxel-box
	 * @return a new created empty binary voxel box of specified size (all voxels initialized to 0)
	 * @throws CreateException
	 */
	@SuppressWarnings("unchecked")
	public BinaryVoxelBox<ByteBuffer> create( Extent extent ) throws CreateException  {
		return (BinaryVoxelBox<ByteBuffer>) create(extent, VoxelDataTypeUnsignedByte.INSTANCE, BinaryValues.getDefault() );
	}
	
	
	/**
	 * Creates an empty binary-value-box (all voxels initialized to 0)
	 * 
	 * @param extent the size of the voxel-box
	 * @param dataType the data-type of the underlying voxel-buffer, either unsigned-byte or unsigned-int
	 * @param binaryValues what voxel-values constitutes OFF and ON
	 * @return a new created empty binary voxel box of specified size (all voxels initialized to 0)
	 * @throws CreateException
	 */
	public BinaryVoxelBox<?> create( Extent extent, VoxelDataType dataType, BinaryValues binaryValues ) throws CreateException  {
		if (dataType.equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
			return new BinaryVoxelBoxByte( VoxelBoxFactory.getByte().create(extent), binaryValues );
		} else if (dataType.equals(VoxelDataTypeUnsignedInt.INSTANCE)) {
			return new BinaryVoxelBoxInt( VoxelBoxFactory.getInt().create(extent), binaryValues );
		} else {
			throw new CreateException("Unsupported voxel-data-type, only unsigned byte and int are supported");
		}
	}
}
