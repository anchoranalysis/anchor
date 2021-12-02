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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.kernel.apply.ApplyKernelForCount;
import org.anchoranalysis.image.voxel.kernel.outline.CalculateExpectedValue;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Applies the kernel to a created binary-mask showing a rectangular/cuboid object on a background,
 * and asserts an expected count on the number of outputted <i>on</i> voxels.
 *
 * @author Owen Feehan
 * @param <T> kernel-type
 */
@AllArgsConstructor
class ObjectTester<T extends Kernel> {

    /**
     * An extent used for creating masks or other derived entities from the object-masks.
     *
     * <p>Note that if 2D is requested instead of 3D, the z-dimension becomes instead 1.
     */
    private static final Extent EXTENT_OBJECT = new Extent(5, 4, 3);

    /**
     * An extent used for the entire scene, on which an object-mask is defined.
     *
     * <p>Note that if 2D is requested instead of 3D, the z-dimension becomes instead 1.
     */
    private static final Extent EXTENT_SCENE = new Extent(10, 9, 11);

    /** Creates a kernel to be tested for a particular {@link ObjectMask} and scene-size. */
    private final BiFunction<ObjectMask, Extent, T> createKernel;

    /** Applies the kernel to determine a count. */
    private final ApplyKernelForCount<T> kernelApplier;

    /**
     * Runs the tests across all possible values of {@link KernelApplicationParameters}.
     *
     * @param atOrigin if true, the {@link ObjectMask} is located at the origin, otherwise without
     *     touching a boundary.
     * @param scene3D whether to create a 3D object and scene.
     * @param calculateValue calculates the expected-value for the test.
     */
    public void applyTest(
            boolean atOrigin, boolean scene3D, CalculateExpectedValue calculateValue) {
        testAcrossOutsidePolicies(scene3D, false, atOrigin, calculateValue);
        testAcrossOutsidePolicies(scene3D, true, atOrigin, calculateValue);
    }

    /**
     * Runs the tests across all possible values of {@link OutsideKernelPolicy}.
     *
     * @param scene3D whether to create a 3D object and scene
     * @param useZ whether to apply the kernel in the z-direction.
     * @param atOrigin if true the object is created with its corner at point (0,0,0) in the scene.
     *     Otherwise at a point that isn't adjacent to an XY border, nor a Z border (if 3D).
     */
    private void testAcrossOutsidePolicies(
            boolean scene3D,
            boolean useZ,
            boolean atOrigin,
            CalculateExpectedValue calculateValue) {
        for (OutsideKernelPolicy policy : OutsideKernelPolicy.values()) {
            KernelApplicationParameters parameters = new KernelApplicationParameters(policy, useZ);
            String message =
                    policy.name()
                            + stringOrEmpty("_atOrigin", atOrigin)
                            + stringOrEmpty("_useZ", useZ)
                            + stringOrEmpty("_scene3D", scene3D);
            testAndCountOn(scene3D, atOrigin, parameters, calculateValue, message);
        }
    }

    /**
     * Runs the test for a particular {@link KernelApplicationParameters}.
     *
     * @param scene3D whether to create a 3D object and scene
     * @param atOrigin if true the object is created with its corner at point (0,0,0) in the scene.
     *     Otherwise at a point that isn't adjacent to an XY border, nor a Z border (if 3D).
     * @param parameters the parameters to use when applying the kernel.
     * @param assertMessage a message to uniquely identify the call to the method for debugging
     *     purposes.
     */
    private void testAndCountOn(
            boolean scene3D,
            boolean atOrigin,
            KernelApplicationParameters parameters,
            CalculateExpectedValue calculateValue,
            String assertMessage) {

        Extent extentObject = FlattenHelper.maybeFlattenExtent(EXTENT_OBJECT, scene3D);
        Extent extentScene = FlattenHelper.maybeFlattenExtent(EXTENT_SCENE, scene3D);

        ObjectMaskFixture fixture = new ObjectMaskFixture(false, extentObject);
        ObjectMask object =
                CorneredObjectHelper.createObjectFromFixture(fixture, scene3D, atOrigin);

        assertEquals(
                calculateValue.calculate(fixture, parameters),
                kernelApplier.apply(object, extentScene, parameters, createKernel),
                assertMessage);
    }

    /** Returns {@code string} if {@code condition} is true, otherwise an empty-string. */
    private static String stringOrEmpty(String string, boolean condition) {
        return (condition ? string : "");
    }
}
