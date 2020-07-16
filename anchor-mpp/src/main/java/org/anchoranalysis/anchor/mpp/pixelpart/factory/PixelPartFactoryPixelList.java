/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart.factory;

import org.anchoranalysis.anchor.mpp.pixelpart.PixelPartPixelList;
import org.anchoranalysis.image.voxel.VoxelIntensityList;

public class PixelPartFactoryPixelList implements PixelPartFactory<VoxelIntensityList> {

    @Override
    public PixelPartPixelList create(int numSlices) {
        return new PixelPartPixelList(numSlices);
    }

    @Override
    public void addUnused(VoxelIntensityList part) {
        // NOTHING TO DO
    }
}
