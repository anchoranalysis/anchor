/* (C)2020 */
package org.anchoranalysis.image.voxel.box;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public class VoxelBoxList implements Iterable<VoxelBoxWrapper> {

    private ArrayList<VoxelBoxWrapper> list = new ArrayList<>();

    public boolean add(VoxelBoxWrapper vb) {
        return list.add(vb);
    }

    @Override
    public Iterator<VoxelBoxWrapper> iterator() {
        return list.iterator();
    }

    public Extent getFirstExtent() {
        return list.get(0).any().extent();
    }

    public List<VoxelBuffer<?>> bufferListForSlice(int sliceNum) {
        List<VoxelBuffer<?>> listOut = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listOut.add(list.get(i).any().getPixelsForPlane(sliceNum));
        }
        return listOut;
    }

    public VoxelBoxWrapper get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }
}
