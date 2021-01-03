package org.anchoranalysis.image.voxel.kernel.count;

import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

class CountKernelNeighborhoodIgnoreOutsideSceneTest extends CountKernelTestBase {

    @Override
    protected CountKernel createKernel(ObjectMask object, Extent extentScene) {
        return new CountKernelNeighborhoodIgnoreOutsideScene(extentScene, new Point3i());
    }

    @Override
    protected int expectedInside2D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 58;
        } else {
            return 18;
        }
    }

    @Override
    protected int expectedInside3D(ObjectMaskFixture fixture, KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 94;
        } else {
            return 54;
        }
    }

    @Override
    protected int expectedBoundary2D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 58;
        } else {
            return 18;
        }
    }

    @Override
    protected int expectedBoundary3D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        if (params.isUseZ()) {
            return 94;
        } else {
            return 54;
        }
    }

}
