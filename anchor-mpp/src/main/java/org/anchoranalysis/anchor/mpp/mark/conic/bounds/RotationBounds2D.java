package org.anchoranalysis.anchor.mpp.mark.conic.bounds;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.BoundUnitless;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation2D;

import lombok.Getter;
import lombok.Setter;

/**
 * Creates a randomly-generated orientation in 2D by uniformally sampling a scalar rotation angle
 * 
 * @author Owen Feehan
  */
public class RotationBounds2D extends RotationBounds {

	// START BEAN PROPERTIES
	@BeanField @Getter @Setter
	private Bound rotationAngle = new BoundUnitless(0, 2 * Math.PI);
	// END BEAN PROPERTIES

	@Override
	public Orientation randomOrientation(RandomNumberGenerator randomNumberGenerator, ImageResolution res) {
		return new Orientation2D( getRotationAngle().resolve(res, false).randOpen(randomNumberGenerator) );
	}	
	@Override
	public String getBeanDscr() {
		return String.format("%s, rotation=(%s)", getBeanName(), rotationAngle.toString() );
	}
}
