package org.anchoranalysis.image.voxel.box.factory;

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


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.box.pixelsforplane.IPixelsForPlane;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class VoxelBoxFactory {

	private VoxelBoxFactory() {
		// FORCE STATIC USAGE
	}
		
	private static VoxelBoxFactoryTypeBound<ByteBuffer> factoryByte = new VoxelBoxFactoryByte();
	private static VoxelBoxFactoryTypeBound<ShortBuffer> factoryShort = new VoxelBoxFactoryShort();
	private static VoxelBoxFactoryTypeBound<IntBuffer> factoryInt = new VoxelBoxFactoryInt();
	private static VoxelBoxFactoryTypeBound<FloatBuffer> factoryFloat = new VoxelBoxFactoryFloat();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static VoxelBoxWrapper create( IPixelsForPlane<?> pixelsForPlane, VoxelDataType dataType ) {
		VoxelBoxFactoryTypeBound factory = multiplex(dataType);
		return new VoxelBoxWrapper(factory.create(pixelsForPlane));
	}

	public static VoxelBoxWrapper create( Extent e, VoxelDataType dataType ) {
		VoxelBoxFactoryTypeBound<?> factory = multiplex(dataType);
		return new VoxelBoxWrapper(factory.create(e));
	}


	public static VoxelBoxFactoryTypeBound<ByteBuffer> getByte() {
		return factoryByte;
	}

	public static VoxelBoxFactoryTypeBound<ShortBuffer> getShort() {
		return factoryShort;
	}

	public static VoxelBoxFactoryTypeBound<IntBuffer> getInt() {
		return factoryInt;
	}

	public static VoxelBoxFactoryTypeBound<FloatBuffer> getFloat() {
		return factoryFloat;
	}
	
	private static VoxelBoxFactoryTypeBound<?> multiplex( VoxelDataType dataType ) {
		
		if (dataType.equals(VoxelDataTypeUnsignedByte.instance)) {
			return factoryByte;
		} else if (dataType.equals(VoxelDataTypeUnsignedShort.instance)) {
			return factoryShort;
		} else if (dataType.equals(VoxelDataTypeUnsignedInt.instance)) {
			return factoryInt;
		} else if (dataType.equals(VoxelDataTypeFloat.instance)) {
			return factoryFloat;
		} else {
			throw new IncorrectVoxelDataTypeException("Non-existent type");
		}
	}
}
