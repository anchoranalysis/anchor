package org.anchoranalysis.image.stack.region.chnlconverter;

/*-
 * #%L
 * anchor-image
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

import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeFloat;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedInt;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class ChnlConverterMulti {

	private ConversionPolicy conversionPolicy = ConversionPolicy.DO_NOT_CHANGE_EXISTING;
	
	public ChnlConverterMulti() {
		super();
	}
	
	public Chnl convert( Chnl chnlIn, VoxelDataType outputType ) {
		
		if (chnlIn.getVoxelDataType().equals(outputType)) {
			return chnlIn;
		} else if (outputType.equals(VoxelDataTypeUnsignedByte.instance)) {
			return new ChnlConverterToUnsignedByte().convert(chnlIn, conversionPolicy);
		} else if (outputType.equals(VoxelDataTypeUnsignedShort.instance)) {
			return new ChnlConverterToUnsignedShort().convert(chnlIn, conversionPolicy);
		} else if (outputType.equals(VoxelDataTypeFloat.instance)) {
			return new ChnlConverterToFloat().convert(chnlIn, conversionPolicy);
		} else if (outputType.equals(VoxelDataTypeUnsignedInt.instance)) {
			throw new UnsupportedOperationException("UnsignedInt is not yet supported for this operation");
		} else { 
			throw new UnsupportedOperationException();
		}
	}
}
