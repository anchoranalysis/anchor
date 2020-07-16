package org.anchoranalysis.mpp.io.cfg.generator;

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


import java.util.List;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.rgb.RGBStack;

import lombok.AllArgsConstructor;

/**
 * Converts a configuration to a set of object-masks, using a simple {@link DrawObject} for all objects.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public class SimpleOverlayWriter extends DrawOverlay {

	private final DrawObject drawObject;

	@Override
	public void writePrecalculatedOverlays(
		List<PrecalcOverlay> precalculatedMasks,
		ImageDimensions dimensions,
		RGBStack background,
		ObjectDrawAttributes attributes,
		BoundingBox restrictTo
	) throws OperationFailedException {
		
		for( int i=0; i<precalculatedMasks.size(); i++ ) {
			precalculatedMasks.get(i).writePrecalculatedMask(
				background,
				attributes,
				i,
				restrictTo
			);
		}
	}

	@Override
	public void writeOverlaysIfIntersects(
		ColoredOverlayCollection overlays,
		RGBStack stack,
		IDGetter<Overlay> idGetter,
		List<BoundingBox> intersectList
	) throws OperationFailedException {
		
		writeOverlays(
			overlays.subsetWhereBBoxIntersects(
				stack.getDimensions(),
				this,
				intersectList
			),
			stack,
			idGetter
		);
	}

	@Override
	public DrawObject getDrawObject() {
		return drawObject;
	}
}
