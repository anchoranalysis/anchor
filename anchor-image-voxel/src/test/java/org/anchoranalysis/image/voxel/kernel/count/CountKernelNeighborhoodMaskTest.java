package org.anchoranalysis.image.voxel.kernel.count;

import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.Extent;

class CountKernelNeighborhoodMaskTest extends CountKernelTestBase {

    @Override
    protected CountKernel createKernel(ObjectMask object, Extent extentScene) {
        return new CountKernelNeighborhoodMask(object.shiftToOrigin());
    }

    @Override
    protected int expectedInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 44;
        } else {
            return 4;
        }
    }

    @Override
    protected int expectedInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 7;
        } else {
            return 4;
        }
    }

    @Override
    protected int expectedBoundary2D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 49;
        } else {
            return 9;
        }
    }

    @Override
    protected int expectedBoundary3D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 47;
        } else {
            return 27;
        }
    }
}
