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
package org.anchoranalysis.image.voxel.kernel;

import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.kernel.apply.ApplyKernelForCount;
import org.anchoranalysis.image.voxel.kernel.outline.CalculateExpectedValue;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.box.Extent;
import org.junit.jupiter.api.Test;

/**
 * Base class for tests that apply a {@link Kernel} to a {@link BinaryVoxels} with an {@link
 * ObjectMask} on background, resulting in a count.
 *
 * @author Owen Feehan
 * @param <T> kernel-type
 */
public abstract class KernelTestBase<T extends Kernel> { // NOSONAR

    private final ObjectTester<T> tester;

    /**
     * Creates with a specific {@link ApplyKernelForCount}.
     *
     * @param kernelApplier how to apply the kernel to each voxel to create a corresponding count.
     */
    protected KernelTestBase(ApplyKernelForCount<T> kernelApplier) {
        tester =
                new ObjectTester<>((object, extent) -> createKernel(object, extent), kernelApplier);
    }

    /**
     * Creates the {@link Kernel} to be used during the tests.
     *
     * @param object the object-mask to place on a binary image to use in the test.
     * @param extentScene the size of the binary-image on which the object-mask is placed.
     * @return a {@link Kernel} to use in the test.
     */
    protected abstract T createKernel(ObjectMask object, Extent extentScene);

    /**
     * Applies a test where the <b>2D object</b> is located <b>inside the scene without touching a
     * boundary</b>.
     */
    @Test
    void testInside2D() {
        test(false, false, this::expectedInside2D);
    }

    /**
     * Applies a test where the <b>3D object</b> is located <b>inside the scene without touching a
     * boundary</b>.
     */
    @Test
    void testInside3D() {
        test(false, true, this::expectedInside3D);
    }

    /** Applies a test where the <b>2D object</b> is located <b>touching the scene border</b>. */
    @Test
    void testBoundary2D() {
        test(true, false, this::expectedBoundary2D);
    }

    /** Applies a test where the <b>3D object</b> is located <b>touching the scene border</b>. */
    @Test
    void testBoundary3D() {
        test(true, true, this::expectedBoundary3D);
    }

    /**
     * The expected value to be returned by a test when the {@link ObjectMask} is <b>inside</b>
     * (doesn't touch a boundary) and lies in a <b>2D scene</b>.
     *
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */
    protected abstract int expectedInside2D(
            ObjectMaskFixture fixture, KernelApplicationParameters params);

    /**
     * The expected value to be returned by a test when the {@link ObjectMask} is <b>inside</b>
     * (doesn't touch a boundary) and lies in a <b>3D scene</b>.
     *
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */
    protected abstract int expectedInside3D(
            ObjectMaskFixture fixture, KernelApplicationParameters params);

    /**
     * The expected value to be returned by a test when the {@link ObjectMask} is adjacent to
     * <b>boundaries</b> and lies in a <b>2D scene</b>.
     *
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */
    protected abstract int expectedBoundary2D(
            ObjectMaskFixture fixture, KernelApplicationParameters params);

    /**
     * The expected value to be returned by a test when the {@link ObjectMask} is adjacent to
     * <b>boundaries</b> and lies in a <b>3D scene</b>.
     *
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */
    protected abstract int expectedBoundary3D(
            ObjectMaskFixture fixture, KernelApplicationParameters params);

    /**
     * Runs the tests across all possible values of {@link KernelApplicationParameters}.
     *
     * @param atOrigin if true, the {@link ObjectMask} is located at the origin, otherwise without
     *     touching a boundary.
     * @param scene3D whether to create a 3D object and scene.
     * @param calculateValue the expected value for the test.
     */
    private void test(boolean atOrigin, boolean scene3D, CalculateExpectedValue calculateValue) {
        tester.applyTest(atOrigin, scene3D, calculateValue);
    }
}
