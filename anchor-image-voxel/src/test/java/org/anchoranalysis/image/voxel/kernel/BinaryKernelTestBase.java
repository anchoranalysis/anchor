package org.anchoranalysis.image.voxel.kernel;

import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.kernel.outline.CalculateExpectedValue;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.Extent;
import org.junit.jupiter.api.Test;

/**
 * Applies tests that apply a {@link BinaryKernel} to a {@link BinaryVoxels} showing an {@link ObjectMask} on background.
 * 
 * @author Owen Feehan
 *
 */
public abstract class BinaryKernelTestBase {    // NOSONAR

    private final ObjectTester tester;
    
    public BinaryKernelTestBase() {
        tester = new ObjectTester( (object,extent) -> createKernel(object,extent) );
    }
    
    /**
     * Creates the {@link BinaryKernel} to be used during the tests.
     * 
     * @param object the object-mask to place on a binary image to use in the test.
     * @param extentScene the size of the binary-image on which the object-mask is placed.
     * 
     * @return a {@link BinaryKernel} to use in the test.
     */
    protected abstract BinaryKernel createKernel(ObjectMask object, Extent extentScene);
    
    
    /** Applies a test where the <b>2D object</b> is located <b>inside the scene without touching a boundary</b>. */
    @Test
    void testInside() {
        test(false, false, this::expectedInside2D);
    }
    
    /** Applies a test where the <b>3D object</b> is located <b>inside the scene without touching a boundary</b>. */
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
     * The expected value to be returned by a test when the {@link ObjectMask} is <b>inside</b> (doesn't touch a boundary) and lies in a <b>2D scene</b>.
     * 
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */
    protected abstract int expectedInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params);

    /**
     * The expected value to be returned by a test when the {@link ObjectMask} is <b>inside</b> (doesn't touch a boundary) and lies in a <b>3D scene</b>.
     * 
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */
    protected abstract int expectedInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params);

    /**
     * The expected value to be returned by a test when the {@link ObjectMask} is adjacent to <b>boundaries</b> and lies in a <b>2D scene</b>.
     * 
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */
    protected abstract int expectedBoundary2D(ObjectMaskFixture fixture, KernelApplicationParameters params);

    /**
     * The expected value to be returned by a test when the {@link ObjectMask} is adjacent to <b>boundaries</b> and lies in a <b>3D scene</b>.
     * 
     * @param fixture the fixture used to the create the {@link ObjectMask}.
     * @param params the parameters applied on the kernel.
     * @return the expected value.
     */

    protected abstract int expectedBoundary3D(ObjectMaskFixture fixture, KernelApplicationParameters params);
        
    /** 
     * Runs the tests across all possible values of {@link KernelApplicationParameters}.
     *
     * @param atOrigin if true, the {@link ObjectMask} is located at the origin, otherwise without touching a boundary.
     * @param scene3D whether to create a 3D object and scene
     * @param calculateValue the expected value for the test
     */
    private void test(boolean atOrigin, boolean scene3D, CalculateExpectedValue calculateValue) {
        tester.applyTest(atOrigin, scene3D, calculateValue);
    }
}
