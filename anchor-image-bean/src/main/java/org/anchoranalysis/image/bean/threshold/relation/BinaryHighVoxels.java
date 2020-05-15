package org.anchoranalysis.image.bean.threshold.relation;

/*-
 * #%L
 * anchor-image-bean
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

import org.anchoranalysis.core.relation.GreaterThan;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.binary.values.BinaryValues;

/**
 * Selects only the "high" pixels from a binary mask
 * 
 * <p>Uses the default "high" value of 255</p>
 * 
 * @author Owen Feehan
 *
 */
public class BinaryHighVoxels extends BinaryVoxelsBase {

	@Override
	public double threshold() {
		return (double) (BinaryValues.getDefault().getOnInt() - 1);
	}

	@Override
	public RelationToValue relation() {
		return new GreaterThan();
	}
}
