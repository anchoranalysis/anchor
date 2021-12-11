/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.spatial.orientation;

import java.util.function.BiConsumer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Orients an object by rotating anti-clockwise with an explicit {@link RotationMatrix}.
 *
 * <p>Before rotation, the entity is presumed to be aligned with the x-axis.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrientationRotationMatrix extends Orientation {

    /** */
    private static final long serialVersionUID = -496736778234811706L;

    /**
     * The rotation-matrix. Once used here, it must be treated as immutable, and its state may not
     * be changed elsewhere.
     */
    @Getter private RotationMatrix rotationMatrix;

    @Override
    protected RotationMatrix deriveRotationMatrix() {
        return rotationMatrix;
    }

    @Override
    public Orientation negative() {
        // The inverse of a rotation matrix is equal to it's transpose because it's an orthogonal
        // matrix
        RotationMatrix matrix = rotationMatrix.duplicate();
        matrix.multiplyByConstant(-1);
        return new OrientationRotationMatrix(matrix);
    }

    @Override
    public int numberDimensions() {
        return rotationMatrix.getNumberDimensions();
    }

    @Override
    public void describeOrientation(BiConsumer<String, String> consumer) {
        // NOTHING TO DO
    }
}
