package org.anchoranalysis.image.voxel.kernel.outline;

import java.util.function.IntSupplier;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.Extent;
import org.junit.jupiter.api.Test;

abstract class OutlineTestBase {
 
    private final ObjectTester tester;
    
    public OutlineTestBase() {
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
    
    
    /** Applies a test where the 2D object is located inside the scene without touching a boundary. */
    @Test
    void testInside2D() {
        tester.applyTest(false, false, OutlineTestBase::expectedValueInside2D);
    }
    
    /** Applies a test where the 3D object is located inside the scene without touching a boundary. */
    @Test
    void testInside3D() {
        tester.applyTest(false, true, OutlineTestBase::expectedValueInside3D);
    }
    
    /** Applies a test where the 2D object is located touching the scene border. */
    @Test
    void testBoundary2D() {
        tester.applyTest(true, false, OutlineTestBase::expectedValueBoundary2D);
    }
    
    /** Applies a test where the 3D object is located touching the scene border. */
    @Test
    void testBoundary3D() {
        tester.applyTest(true, true, OutlineTestBase::expectedValueBoundary3D);
    }
    
    private static int expectedValueInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.expectedSurfaceNumberVoxels(useZFor2D(params));
    }
        
    private static int expectedValueInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.expectedSurfaceNumberVoxels(params.isUseZ());
    }
    
    private static int expectedValueBoundary2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return expectedSurfaceIfPolicyOff(params, fixture, () -> 8 );
    }
    
    private static int expectedValueBoundary3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return expectedSurfaceIfPolicyOff(params, fixture, () -> params.isUseZ() ? 36 : 24 );
    }
    
    private static int expectedSurfaceIfPolicyOff(KernelApplicationParameters params, ObjectMaskFixture fixture, IntSupplier otherwise) {
        if (params.getOutsideKernelPolicy()==OutsideKernelPolicy.AS_OFF) {
            return fixture.expectedSurfaceNumberVoxels(params.isUseZ());
        } else {
            return otherwise.getAsInt();
        }
    }
       
    private static boolean useZFor2D(KernelApplicationParameters params) {
        return params.isUseZ() && (!params.isIgnoreOutside() && !params.isOutsideHigh());
    }
}
