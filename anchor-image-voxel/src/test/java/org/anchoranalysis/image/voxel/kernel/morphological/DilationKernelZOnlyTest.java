package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

/**
 * Tests {@link DilationKernelZOnly}.
 * 
 * @author Owen Feehan
 *
 */
class DilationKernelZOnlyTest extends MorphologicalKernelTestBase {
    
    private static final ExpectedValues VALUES_2D = new ExpectedValues(EXPECTED_MAXIMAL_2D, 20, 20, 20);
    
    @Override
    protected BinaryKernel createKernel(ObjectMask object, Extent extentScene) {
        return new DilationKernelZOnly();
    }
    
    @Override
    protected ExpectedValues inside2D() {
        return VALUES_2D;
    }

    @Override
    protected ExpectedValues inside3D() {
        return new ExpectedValues(280, 60, 100, 60);
    }
    
    @Override
    protected ExpectedValues boundary2D() {
        return VALUES_2D;
    }
    
    @Override
    protected ExpectedValues boundary3D() {
        return new ExpectedValues(240, 60, 80, 60);
    }
}
