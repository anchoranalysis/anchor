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
class DilationKernelSmallTest extends MorphologicalKernelTestBase {
        
    @Override
    protected BinaryKernel createKernel(ObjectMask object, Extent extentScene) {
        return new DilationKernel(false);
    }
    
    @Override
    protected ExpectedValues inside2D() {
        return new ExpectedValues(EXPECTED_MAXIMAL_2D, 72, 38);
    }

    @Override
    protected ExpectedValues inside3D() {
        return new ExpectedValues(640, 488, 154, 114);
    }
    
    @Override
    protected ExpectedValues boundary2D() {
        return new ExpectedValues(EXPECTED_MAXIMAL_2D, 53, 29);
    }
    
    @Override
    protected ExpectedValues boundary3D() {
        return new ExpectedValues(536, 431, 107, 87);
    }
}
