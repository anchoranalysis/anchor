package org.anchoranalysis.image.voxel.kernel.outline;

import java.util.function.IntSupplier;
import org.anchoranalysis.image.voxel.kernel.BinaryKernelTestBase;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;

abstract class OutlineTestBase extends BinaryKernelTestBase {
    
    @Override
    protected int expectedInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.expectedSurfaceNumberVoxels(useZFor2D(params));
    }
    
    @Override
    protected int expectedInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return fixture.expectedSurfaceNumberVoxels(params.isUseZ());
    }
    
    @Override
    protected int expectedBoundary2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOff(params, fixture, () -> 8 );
    }
    
    @Override
    protected int expectedBoundary3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        return whenOutsideOff(params, fixture, () -> params.isUseZ() ? 36 : 24 );
    }
    
    private static int whenOutsideOff(KernelApplicationParameters params, ObjectMaskFixture fixture, IntSupplier otherwise) {
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
