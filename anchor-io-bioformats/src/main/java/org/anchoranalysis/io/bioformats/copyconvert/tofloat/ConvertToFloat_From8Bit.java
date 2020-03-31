package org.anchoranalysis.io.bioformats.copyconvert.tofloat;

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



import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.ImageDim;

public class ConvertToFloat_From8Bit extends ConvertToFloat {

	@Override
	protected float[] convertIntegerBytesToFloatArray(ImageDim sd, byte[] src, int srcOffset) {
		
		float[] fArr = new float[sd.getX()*sd.getY()];
		  
		int cntLoc = 0;
		for (int y=0; y<sd.getY(); y++) {
			  for (int x=0; x<sd.getX(); x++) {
				  float f = ByteConverter.unsignedByteToInt( src[srcOffset++] );
				  fArr[cntLoc++] = f;
			  }
		}
		return fArr;
	}

	@Override
	protected int bytesPerPixel() {
		return 1;
	}
}