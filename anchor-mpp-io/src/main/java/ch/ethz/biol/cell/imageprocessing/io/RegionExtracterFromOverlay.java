package ch.ethz.biol.cell.imageprocessing.io;

import org.anchoranalysis.core.error.CreateException;

/*
 * #%L
 * anchor-mpp-io
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
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.io.stack.ConvertDisplayStackToRGB;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.stack.rgb.RGBStack;

import ch.ethz.biol.cell.gui.overlay.BoundColoredOverlayCollection;

public class RegionExtracterFromOverlay extends RegionExtracter {

	private RegionExtracter regionExtracter;
	private BoundColoredOverlayCollection overlay;
		
	public RegionExtracterFromOverlay( RegionExtracter regionExtracter, BoundColoredOverlayCollection overlay ) {
		super();
		this.regionExtracter = regionExtracter;
		this.overlay = overlay;
	}

	@Override
	public DisplayStack extractRegionFrom(BoundingBox BoundingBox, double zoomFactor) throws OperationFailedException {
		DisplayStack ds = regionExtracter.extractRegionFrom(BoundingBox, zoomFactor);
		
		try {
			RGBStack rgbStack = ConvertDisplayStackToRGB.convert(ds);
			overlay.drawRGB( rgbStack, BoundingBox, zoomFactor );
			return DisplayStack.create(rgbStack);
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}

}
