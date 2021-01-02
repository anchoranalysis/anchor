/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.kernel.morphological;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.kernel.BinaryKernelTestBase;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;

@AllArgsConstructor
abstract class MorphologicalKernelTestBase extends BinaryKernelTestBase {

    /**
     * The expected value when there is a maximal dilation in 2D plane (in all directions in 2D).
     */
    protected static final int EXPECTED_MAXIMAL_2D = 90;

    /**
     * Expected values where the <b>2D object</b> is located <b>inside the scene without touching a
     * boundary</b>.
     */
    protected abstract ExpectedValues inside2D();

    /**
     * Expected values where the <b>3D object</b> is located <b>inside the scene without touching a
     * boundary</b>.
     */
    protected abstract ExpectedValues inside3D();

    /** Applies a test where the <b>2D object</b> is located <b>touching the scene border</b>. */
    protected abstract ExpectedValues boundary2D();

    /** Applies a test where the <b>3D object</b> is located <b>touching the scene border</b>. */
    protected abstract ExpectedValues boundary3D();

    @Override
    protected int expectedInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOn(params, inside2D());
    }

    @Override
    protected int expectedInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOn(params, inside3D());
    }

    @Override
    protected int expectedBoundary2D(
            ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOn(params, boundary2D());
    }

    @Override
    protected int expectedBoundary3D(
            ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOn(params, boundary3D());
    }

    /**
     * Specifying values for combinations of useZ (termed 3D or 2D) for when policy is {@link
     * OutsideKernelPolicy#AS_ON}).
     */
    private static int whenOutsideOn(KernelApplicationParameters params, ExpectedValues values) {
        if (params.isUseZ()) {
            return whenOutsideOn(params, values.on3D(), values.otherwise3D());
        } else {
            return whenOutsideOn(params, values.on2D(), values.otherwise2D());
        }
    }

    private static int whenOutsideOn(
            KernelApplicationParameters params, int valueIfOn, int valueOtherwise) {
        if (params.getOutsideKernelPolicy() == OutsideKernelPolicy.AS_ON) {
            return valueIfOn;
        } else {
            return valueOtherwise;
        }
    }
}
