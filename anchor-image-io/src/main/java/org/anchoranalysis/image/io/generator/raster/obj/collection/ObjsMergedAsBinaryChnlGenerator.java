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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.ops.BinaryChnlFromObjs;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;


/**
 * Writes objects a binary-mask (with all objects merged together for the mask)
 * 
 * @author owen
 *
 */
public class ObjsMergedAsBinaryChnlGenerator extends ObjsGenerator {
	
	public ObjsMergedAsBinaryChnlGenerator(ImageDim dim) {
		super(dim);
	}

	public ObjsMergedAsBinaryChnlGenerator(ObjMaskCollection masks, ImageDim dim) {
		super(masks, dim);
	}

	@Override
	public Stack generate() throws OutputWriteFailedException {
		
		try {
			BinaryChnl chnl = BinaryChnlFromObjs.createFromObjs(
				getObjs(),
				getDimensions(),
				BinaryValues.getDefault()
			);
			return new ChnlGenerator(chnl.getChnl(), "maskCollection").generate();
		} catch (CreateException e) {
			throw new OutputWriteFailedException(e);
		}
		
		
	}

}