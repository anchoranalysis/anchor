/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.dilateerode;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.box.VoxelBox;

public abstract class BinaryKernelMorph3Extent extends BinaryKernelMorph3 {

    protected final boolean useZ;
    protected Extent extent;

    public BinaryKernelMorph3Extent(BinaryValuesByte bv, boolean outsideAtThreshold, boolean useZ) {
        super(bv, outsideAtThreshold);
        this.useZ = useZ;
    }

    @Override
    public void init(VoxelBox<ByteBuffer> in) {
        this.extent = in.extent();
    }
}
