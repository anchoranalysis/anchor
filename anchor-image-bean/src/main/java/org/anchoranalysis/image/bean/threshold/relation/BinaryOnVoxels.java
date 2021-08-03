/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.bean.threshold.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.math.relation.GreaterThan;
import org.anchoranalysis.math.relation.RelationToValue;

/**
 * Selects only the <i>on</i> pixels from a binary mask.
 *
 * <p>Uses the default on value of 255.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class BinaryOnVoxels extends BinaryVoxelsBase {

    @Override
    public double threshold() {
        return BinaryValues.getDefault().getOnInt() - 1.0;
    }

    @Override
    public RelationToValue relation() {
        return new GreaterThan();
    }
}
