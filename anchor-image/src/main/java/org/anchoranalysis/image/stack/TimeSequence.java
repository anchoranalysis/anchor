/* (C)2020 */
package org.anchoranalysis.image.stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class TimeSequence implements Iterable<Stack> {

    private final List<Stack> list = new ArrayList<>();

    public TimeSequence() {}

    public TimeSequence(Stack s) {
        add(s);
    }

    public boolean add(Stack arg0) {
        return list.add(arg0);
    }

    public Stack get(int arg0) {
        return list.get(arg0);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<Stack> iterator() {
        return list.iterator();
    }

    public int size() {
        return list.size();
    }

    // Returns true if the data type of all channels is equal to
    public boolean allChnlsHaveType(VoxelDataType chnlDataType) {

        for (Stack stack : this) {
            if (!stack.allChnlsHaveType(chnlDataType)) {
                return false;
            }
        }
        return true;
    }

    // Assumes all dimensions are the same, but doesn't check
    public ImageDimensions getDimensions() {
        return list.get(0).getDimensions();
    }
}
