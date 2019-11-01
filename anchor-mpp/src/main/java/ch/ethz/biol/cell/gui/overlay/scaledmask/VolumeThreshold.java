package ch.ethz.biol.cell.gui.overlay.scaledmask;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.objmask.scaled.ScaledMaskCreator;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;

/*-
 * #%L
 * anchor-mpp
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
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithProperties;

public class VolumeThreshold extends ScaledMaskCreator {

	private ScaledMaskCreator greaterThanEqualThreshold;
	private ScaledMaskCreator lessThreshold;
	private int threshold;
	
	public VolumeThreshold(ScaledMaskCreator greaterThanEqualThreshold,
			ScaledMaskCreator lessThreshold, int threshold) {
		super();
		this.greaterThanEqualThreshold = greaterThanEqualThreshold;
		this.lessThreshold = lessThreshold;
		this.threshold = threshold;
	}

	@Override
	public ObjMaskWithProperties createScaledMask(
			OverlayWriter overlayWriter, ObjMaskWithProperties omUnscaled,
			double scaleFactor, Object originalObject, ImageDim sdScaled,
			BinaryValuesByte bvOut)
			throws CreateException {

		Mark originalMark = (Mark) originalObject;
		
		 //TODO using region 0, fix
		double zoomVolume = originalMark.volume(0) * Math.pow(scaleFactor,2);
		
		if (zoomVolume > threshold) {
			return greaterThanEqualThreshold.createScaledMask(overlayWriter,omUnscaled, scaleFactor, originalObject, sdScaled, bvOut);
		} else {
			return lessThreshold.createScaledMask(overlayWriter,omUnscaled, scaleFactor, originalObject, sdScaled, bvOut);
		}
	}

}
