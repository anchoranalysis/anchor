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
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.image.interpolator.InterpolatorFactory;
import org.anchoranalysis.image.objmask.ObjMask;

public class SeedObjMask extends Seed {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ObjMask om;
	
	SeedObjMask(ObjMask om) {
		super();
		this.om = om;
	}

	@Override
	public void scaleXY(double scale) throws OptionalOperationUnsupportedException {
		try {
			om.scale(scale, scale, InterpolatorFactory.getInstance().noInterpolation() );
		} catch (OperationFailedException e) {
			throw new OptionalOperationUnsupportedException();
		}
	}

	@Override
	public void flattenZ() throws OptionalOperationUnsupportedException {
		om = om.flattenZ();
	}

	@Override
	public Seed duplicate() {
		return new SeedObjMask(om.duplicate());
	}

	@Override
	public void growToZ(int sz) throws OptionalOperationUnsupportedException {
		om = om.growToZ(sz);
	}

	@Override
	public ObjMask createMask() {
		return om;
	}
	
	@Override
	public boolean equalsDeep(Seed other) {
		if (other instanceof SeedObjMask) {
			SeedObjMask otherCast = (SeedObjMask) other;
			if (!om.equalsDeep(otherCast.om)) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

}
