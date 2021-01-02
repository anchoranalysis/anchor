package org.anchoranalysis.image.voxel.kernel.outline;

import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

/**
 * Tests {@link OutlineKernel}.
 * 
 * @author Owen Feehan
 *
 */
class OutlineKernelTest extends OutlineTestBase {
    
    @Override
    protected BinaryKernel createKernel(ObjectMask object, Extent extentScene) {
        return new OutlineKernel();
    }
}
