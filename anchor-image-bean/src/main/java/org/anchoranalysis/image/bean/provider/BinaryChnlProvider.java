package org.anchoranalysis.image.bean.provider;

/*-
 * #%L
 * anchor-image-bean
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

import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.ops.BinaryChnlFromObjs;
import org.anchoranalysis.image.stack.Stack;

@GroupingRoot
public abstract class BinaryChnlProvider extends BeanImgStackProvider<BinaryChnlProvider,BinaryChnl> {

	@Override
	public abstract BinaryChnl create() throws CreateException;
	
	public Stack createStack() throws CreateException {
		Chnl chnl = createChnlFromBinary( create(), BinaryValues.getDefault() );
		return new Stack( chnl ); 
	}

	private static Chnl createChnlFromBinary( BinaryChnl binaryImgChnl, BinaryValues bvOut ) throws CreateException {
		ObjMaskCollection omc = expressAsObj(binaryImgChnl);
		return BinaryChnlFromObjs.createFromObjs( omc, binaryImgChnl.getDimensions(), bvOut ).getChnl();
	}
		
	private static ObjMaskCollection expressAsObj( BinaryChnl binaryImgChnl ) throws CreateException {
		ObjMask om = new ObjMask( binaryImgChnl.binaryVoxelBox() ); 
		return new ObjMaskCollection(om);
	}
}