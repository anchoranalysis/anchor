package org.anchoranalysis.image.io.generator.raster.obj.collection;

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

import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * Writes objects as a Raster with unique id values for each object.
 * 
 * <p>Note that a maximum of 254 objects are allowed to be written on a channel in this way (for a 8-bit image)</p>
 * @author owen
 *
 */
public class ObjsAsUniqueValueGenerator extends ObjsGenerator {
	
	private static ChnlFactoryByte factory = new ChnlFactoryByte();
	
	public ObjsAsUniqueValueGenerator(ImageDim dim) {
		super(dim);
	}

	public ObjsAsUniqueValueGenerator(ObjMaskCollection masks, ImageDim dim) {
		super(masks, dim);
	}
	
	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		Chnl outChnl = factory.createEmptyInitialised(
			getDimensions()
		);

		VoxelBox<?> vbOutput = outChnl.getVoxelBox().any();
		
		if (getObjs().size()>254) {
			throw new OutputWriteFailedException( String.format("Collection has %d objs. A max of 254 is allowed", getObjs().size()));
		}
		
		int val = 1;
		for( ObjMask om : getObjs() ) {
			vbOutput.setPixelsCheckMask(om, val++);
		}
		
		return new ChnlGenerator(outChnl, "maskCollection").generate();
	}
}
