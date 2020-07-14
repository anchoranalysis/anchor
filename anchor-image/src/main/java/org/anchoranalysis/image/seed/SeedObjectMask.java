package org.anchoranalysis.image.seed;

/*-
 * #%L
 * anchor-image
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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SeedObjectMask implements Seed {
	
	private ObjectMask object;

	@Override
	public void scaleXY(double scale) throws OperationFailedException {
		object = object.scale(
			new ScaleFactor(scale),
			InterpolatorFactory.getInstance().noInterpolation()
		);
	}

	@Override
	public void flattenZ() {
		object = object.flattenZ();
	}

	@Override
	public Seed duplicate() {
		return new SeedObjectMask(object.duplicate());
	}

	@Override
	public void growToZ(int sz) {
		object = object.growToZ(sz);
	}

	@Override
	public ObjectMask createMask() {
		return object;
	}
	
	@Override
	public boolean equalsDeep(Seed other) {
		if (other instanceof SeedObjectMask) {
			SeedObjectMask otherCast = (SeedObjectMask) other;
			return object.equalsDeep(otherCast.object);
		} else {
			return false;
		}
	}

}
