package org.anchoranalysis.io.bioformats;

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

import org.anchoranalysis.image.chnl.factory.ChnlFactorySingleType;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryByte;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryFloat;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryInt;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryShort;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeSignedShort;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

import loci.formats.FormatTools;

class MultiplexDataTypes {

	public static VoxelDataType multiplexFormat( int pixelType ) throws RasterIOException {
		switch(pixelType) {
		case FormatTools.UINT8:
			return VoxelDataTypeUnsignedByte.instance;
		case FormatTools.UINT16:
			return VoxelDataTypeUnsignedShort.instance;
		case FormatTools.INT16:
			return VoxelDataTypeSignedShort.instance;			
		case FormatTools.FLOAT:
			return VoxelDataTypeFloat.instance;
		default:
			throw new RasterIOException( String.format("File has unknown type %s",  FormatTools.getPixelTypeString(pixelType) ) );
		}	
	}
	
	public static ChnlFactorySingleType multiplexVoxelDataType( VoxelDataType voxelDataType ) {
		if (voxelDataType.equals(VoxelDataTypeUnsignedByte.instance)) {
			return new ChnlFactoryByte();
		} else if (voxelDataType.equals(VoxelDataTypeUnsignedShort.instance)) {
			return new ChnlFactoryShort();
		} else if (voxelDataType.equals(VoxelDataTypeSignedShort.instance)) {
			return new ChnlFactoryShort();			
		} else if (voxelDataType.equals(VoxelDataTypeFloat.instance)) {
			return new ChnlFactoryFloat();
		} else if (voxelDataType.equals(VoxelDataTypeUnsignedInt.instance)) {
			return new ChnlFactoryInt();
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
