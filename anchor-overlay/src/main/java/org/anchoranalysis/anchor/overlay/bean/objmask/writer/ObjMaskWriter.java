package org.anchoranalysis.anchor.overlay.bean.objmask.writer;

import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;

/*
 * #%L
 * anchor-overlay
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



import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

/**
 * Writes an object-mask to the file-system
 * 
 * @author Owen Feehan
 *
 * @param <T> pre-calculate object type
 */
public abstract class ObjMaskWriter extends AnchorBean<ObjMaskWriter> {

	/**
	 * Writes a single-mask to a stack
	 * 
	 * @param mask 			the input-mask to write
	 * @param stack 		where to write it to
	 * @param idGetter		gets a unique ID associated with the objMask
	 * @param colorIDGetter	gets a color ID associated with the objMask
	 * @param iter			the current iteration
	 * @param colorIndex	gets a color from a colorID
	 * @param bboxContainer a restriction on which part of stack we write out to (considered in terms of the possibly-zoomed pixel cooridinates)
	 */
	public final void writeSingle(
		ObjectWithProperties mask,
		RGBStack stack,
		IDGetter<ObjectWithProperties> idGetter,
		IDGetter<ObjectWithProperties> colorIDGetter,
		int iter,
		ColorIndex colorIndex,
		BoundingBox bboxContainer
	) throws OperationFailedException {
		
		try {
			PrecalcOverlay precalculatedObj = precalculate( mask, stack.getDimensions() );
			precalculatedObj.writePrecalculatedMask(
				stack,
				idGetter,
				colorIDGetter,
				iter,
				colorIndex,
				bboxContainer
			);
			
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	
	// Does computational preprocessing (so it can be cached). Outputs a collection of ObjMasks that are later re used
	public abstract PrecalcOverlay precalculate( ObjectWithProperties mask, ImageDimensions dim ) throws CreateException;
		
	public void write(
		ObjectCollectionWithProperties masks,
		RGBStack background,
		ColorIndex colorIndex,
		IDGetter<ObjectWithProperties> idGetter,
		IDGetter<ObjectWithProperties> colorIDGetter
	) throws OperationFailedException {
		write(
			masks,
			background,
			colorIndex,
			idGetter,
			colorIDGetter,
			new BoundingBox(background.getDimensions().getExtent())
		);
	}



	/**
	 * 
	 * @param masks 			Masks to write
	 * @param stack 			Stack to write masks on top of
	 * @param colorIndex 		Maps integers to colors
	 * @param idGetter   		Gets a unique integer-ID from a mask
	 * @param colorIDGetter		Gets an integer representing a Color from a mask
	 * @param bboxContainer		A bounding box, which restricts where we write out to
	 * @throws OperationFailedException
	 */
	public void write(
		ObjectCollectionWithProperties masks,
		RGBStack stack,
		ColorIndex colorIndex,
		IDGetter<ObjectWithProperties> idGetter,
		IDGetter<ObjectWithProperties> colorIDGetter,
		BoundingBox bboxContainer
	) throws OperationFailedException {
		// We iterate through every mark
		int i = 0;
		for ( ObjectWithProperties mask : masks ) {
			writeSingle(mask, stack, idGetter, colorIDGetter, i++, colorIndex, bboxContainer);
		}
	}
}
