/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel;

import org.anchoranalysis.core.geometry.Point3i;

public abstract class BinaryKernel extends Kernel {

    public BinaryKernel(int size) {
        super(size);
    }

    public abstract boolean accptPos(int ind, Point3i point);
}
