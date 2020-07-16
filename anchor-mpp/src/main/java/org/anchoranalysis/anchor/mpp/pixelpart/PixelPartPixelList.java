/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;
import org.anchoranalysis.image.voxel.VoxelIntensityList;

public class PixelPartPixelList implements PixelPart<VoxelIntensityList> {

    private VoxelIntensityList combined;
    private List<VoxelIntensityList> list;

    public PixelPartPixelList(int numSlices) {

        combined = new VoxelIntensityList();

        list = new ArrayList<>();
        for (int i = 0; i < numSlices; i++) {
            list.add(new VoxelIntensityList());
        }
    }

    // Should only be used RO, if we want to maintain integrity with the combined list
    @Override
    public VoxelIntensityList getSlice(int sliceID) {
        return list.get(sliceID);
    }

    @Override
    public void addForSlice(int sliceID, int val) {
        double valD = (double) val;
        combined.add(valD);
        list.get(sliceID).add(valD);
    }

    @Override
    public VoxelIntensityList getCombined() {
        return combined;
    }

    @Override
    public void cleanUp(PixelPartFactory<VoxelIntensityList> factory) {
        // NOTHING TO DO
    }

    @Override
    public int numSlices() {
        return list.size();
    }
}
