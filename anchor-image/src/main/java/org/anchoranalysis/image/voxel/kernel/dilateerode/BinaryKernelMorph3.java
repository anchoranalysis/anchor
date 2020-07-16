/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel.dilateerode;

import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

public abstract class BinaryKernelMorph3 extends BinaryKernel {

    protected final BinaryValuesByte bv;
    protected final boolean outsideAtThreshold;

    protected LocalSlices inSlices;

    public BinaryKernelMorph3(BinaryValuesByte bv, boolean outsideAtThreshold) {
        super(3);
        this.bv = bv;
        this.outsideAtThreshold = outsideAtThreshold;
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        this.inSlices = inSlices;
    }
}
