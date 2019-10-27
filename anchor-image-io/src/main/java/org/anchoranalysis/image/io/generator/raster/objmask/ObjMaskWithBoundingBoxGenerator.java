package org.anchoranalysis.image.io.generator.raster.objmask;

/*-
 * #%L
 * anchor-image-io
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

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.io.generator.IterableGeneratorBridge;
import org.anchoranalysis.io.generator.combined.IterableCombinedListGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;

// Outputs a mask and a serialized Bounding Box for each ObjMask
public class ObjMaskWithBoundingBoxGenerator extends IterableCombinedListGenerator<ObjMask> {
	
	public ObjMaskWithBoundingBoxGenerator( ImageRes res ) {
		
		byte valOut = BinaryValuesByte.getDefault().getOnByte();
		
		add( null, new ObjMaskGenerator(valOut, res) );
		
		// We create an iterable bridge from ObjMask to BoundingBox
		IterableGeneratorBridge<ObjMask, BoundingBox> generatorBBox = new IterableGeneratorBridge<>(
				new ObjectOutputStreamGenerator<BoundingBox>("BoundingBox"),
				new IObjectBridge<ObjMask, BoundingBox>() {

					@Override
					public BoundingBox bridgeElement(ObjMask sourceObject)
							throws GetOperationFailedException {
						return sourceObject.getBoundingBox();
					}
				}				
		);
		
		add( null, generatorBBox );
	}
	
	public ObjMaskWithBoundingBoxGenerator( ImageRes res, ObjMask om ) throws SetOperationFailedException {
		this(res);
		setIterableElement(om);
	}
	

}
