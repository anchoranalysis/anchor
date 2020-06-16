package org.anchoranalysis.anchor.overlay.objmask.scaled;

import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;

/*-
 * #%L
 * anchor-overlay
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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.properties.ObjectWithProperties;
import org.anchoranalysis.image.scale.ScaleFactor;

public class FromMask extends ScaledMaskCreator {

	private static Interpolator interpolator = InterpolatorFactory.getInstance().binaryResizing();
	
	@Override
	public ObjectWithProperties createScaledMask(
			OverlayWriter overlayWriter, ObjectWithProperties omUnscaled,
			double scaleFactor, Object originalObject, ImageDim sdScaled,
			BinaryValuesByte bvOut)
			throws CreateException {

		try {
			// Then we have to create the scaled-object fresh
			// We store it for next-time
			ObjectMask omScaled = omUnscaled.getMask().scaleNew(
				new ScaleFactor(scaleFactor),
				interpolator
			);
			
			//System.out.printf("create Scaling from %s to %s for zoom=%f\n", omUnscaled.toString(), omScaled.toString(), scaleFactor );
			
			assert( omScaled.hasPixelsGreaterThan(0) );
			
			// We keep the properties the same
			return new ObjectWithProperties(omScaled, omUnscaled.getProperties());
			
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}
	}

}
