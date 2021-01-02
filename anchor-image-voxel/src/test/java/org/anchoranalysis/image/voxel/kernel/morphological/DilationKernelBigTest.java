package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

/**
 * Tests {@link DilationKernel} with a small-neighbourhood.
 * 
 * @author Owen Feehan
 *
 */
class DilationKernelBigTest extends MorphologicalKernelTestBase {
        
    @Override
    protected BinaryKernel createKernel(ObjectMask object, Extent extentScene) {
        return new DilationKernel(true);
    }
    
    @Override
    protected ExpectedValues inside2D() {
        return new ExpectedValues(EXPECTED_MAXIMAL_2D, 76, 42);
    }

    @Override
    protected ExpectedValues inside3D() {
        return new ExpectedValues(652, 500, 166, 126);
    }
    
    @Override
    protected ExpectedValues boundary2D() {
        return new ExpectedValues(EXPECTED_MAXIMAL_2D, 54, 30);
    }
    
    @Override
    protected ExpectedValues boundary3D() {
        return new ExpectedValues(538, 434, 110, EXPECTED_MAXIMAL_2D);
    }
}
