package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

/**
 * Tests {@link ErosionKernel}.
 * 
 * @author Owen Feehan
 *
 */
class ErosionKernelTest extends MorphologicalKernelTestBase {
    
    private static final int USEZ_2D = 0;
    private static final int NOT_USEZ_2D = 6;
    
    private static final int USEZ_3D = 6;
    private static final int NOT_USEZ_3D = 18;
    
    @Override
    protected BinaryKernel createKernel(ObjectMask object, Extent extentScene) {
        return new ErosionKernel();
    }
    
    @Override
    protected ExpectedValues inside2D() {
        return new ExpectedValues(USEZ_2D, NOT_USEZ_2D, 6);
    }

    @Override
    protected ExpectedValues inside3D() {
        return new ExpectedValues(USEZ_3D, NOT_USEZ_3D, 6, 18);
    }
    
    @Override
    protected ExpectedValues boundary2D() {
        return new ExpectedValues(USEZ_2D, NOT_USEZ_2D, 12);
    }
    
    @Override
    protected ExpectedValues boundary3D() {
        return new ExpectedValues(USEZ_3D, NOT_USEZ_3D, 24, 36);
    }
}
