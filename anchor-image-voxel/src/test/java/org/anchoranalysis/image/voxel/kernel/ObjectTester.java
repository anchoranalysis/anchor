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
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.outline.CalculateExpectedValue;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import lombok.AllArgsConstructor;

/**
 * Applies the kernel to a created binary-mask showing a rectangular/cuboid object on a background, and asserts an expected count on the number of outputted <i>on</i> voxels.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor class ObjectTester {
        
    /** 
     * An extent used for creating masks or other derived entities from the object-masks.
     * 
     * <p>Note that if 2D is requested instead of 3D, the z-dimension becomes instead 1.
     */
    private static final Extent EXTENT_OBJECT = new Extent(5,4,3);
    
    /** 
     * An extent used for the entire scene, on which an object-mask is defined. 
     * 
     * <p>Note that if 2D is requested instead of 3D, the z-dimension becomes instead 1.
     */
    private static final Extent EXTENT_SCENE = new Extent(10,9,11);
    
    /** A corner for the object that doesn't touch a boundary in {@link #EXTENT_SCENE}. */
    private static final Point3i CORNER_NOT_AT_BORDER = new Point3i(2, 3, 2);

    /** A corner for the object that sits at the origin in {@link #EXTENT_SCENE}. */
    private static final Point3i CORNER_ORIGIN = new Point3i(0,0,0);
    
    /** Creates a kernel to be tested for a particular {@link ObjectMask} and scene-size. */
    private final BiFunction<ObjectMask,Extent,BinaryKernel> createKernel;
    
    /** 
     * Runs the tests across all possible values of {@link KernelApplicationParameters}.
     *
     * @param atOrigin if true, the {@link ObjectMask} is located at the origin, otherwise without touching a boundary.
     * @param scene3D whether to create a 3D object and scene
     */
    public void applyTest(boolean atOrigin, boolean scene3D, CalculateExpectedValue calculateValue) {
        testAcrossOutsidePolicies(scene3D, false, atOrigin, calculateValue);
        testAcrossOutsidePolicies(scene3D, true, atOrigin, calculateValue);
    }
    
    /**
     * Runs the tests across all possible values of {@link OutsideKernelPolicy}.
     * 
     * @param scene3D whether to create a 3D object and scene
     * @param useZ whether to apply the kernel in the z-direction.
     * @param atOrigin if true the object is created with its corner at point (0,0,0) in the scene. Otherwise at a point that isn't adjacent to an XY border, nor a Z border (if 3D).
     */
    private void testAcrossOutsidePolicies(boolean scene3D, boolean useZ, boolean atOrigin, CalculateExpectedValue calculateValue) {
        for( OutsideKernelPolicy policy : OutsideKernelPolicy.values() ) {
            KernelApplicationParameters params = new KernelApplicationParameters(policy, useZ);
            String message = policy.name() + stringOrEmpty("_atOrigin", atOrigin) + stringOrEmpty("_useZ", useZ) + stringOrEmpty("_scene3D",scene3D);
            testAndCountOn(scene3D, atOrigin, params, calculateValue, message);
        }
    }
    
    /**
     * Runs the test for a particular {@link KernelApplicationParameters}.
     * 
     * @param scene3D whether to create a 3D object and scene
     * @param atOrigin if true the object is created with its corner at point (0,0,0) in the scene. Otherwise at a point that isn't adjacent to an XY border, nor a Z border (if 3D).
     * @param params the parameters to use when applying the kernel.
     * @param assertMessage a message to uniquely identify the call to the method for debugging purposes.
     */
    private void testAndCountOn(boolean scene3D, boolean atOrigin, KernelApplicationParameters params, CalculateExpectedValue calculateValue, String assertMessage) {
        
        ObjectMaskFixture fixture = new ObjectMaskFixture(false, FlattenHelper.maybeFlattenExtent(EXTENT_OBJECT, scene3D));

        ObjectMask object = fixture.filledMask(corner(scene3D,atOrigin));
        
        Extent extentScene = FlattenHelper.maybeFlattenExtent(EXTENT_SCENE, scene3D);
        
        BinaryVoxels<UnsignedByteBuffer> voxelsObject = ObjectOnBinaryHelper.createVoxelsWithObject(object, extentScene, true);
        
        BinaryKernel kernel = createKernel.apply(object, EXTENT_SCENE);

        BinaryVoxels<UnsignedByteBuffer> out = ApplyKernel.apply(kernel, voxelsObject, BinaryValuesByte.getDefault(), params);
        int actualCount = out.extract().voxelsEqualTo(BinaryValues.getDefault().getOnInt()).count();
        int expectedCount = calculateValue.calculate(fixture, params);
        assertEquals( expectedCount, actualCount, assertMessage);
    }
    
    private Point3i corner(boolean scene3D, boolean atOrigin) {
        if (atOrigin) {
            return CORNER_ORIGIN;
        } else {
            return FlattenHelper.maybeFlattenPoint(CORNER_NOT_AT_BORDER, scene3D);
        }
    }
    
    /** Returns {@code string} if {@code condition} is true, otherwise an empty-string. */
    private static String stringOrEmpty(String string, boolean condition) {
        return (condition ? string : "");
    }
}
