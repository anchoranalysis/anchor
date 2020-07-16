/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.count;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.kernel.Kernel;

public abstract class CountKernel extends Kernel {

    public CountKernel(int size) {
        super(size);
    }

    public abstract int countAtPos(int ind, Point3i point);
}
