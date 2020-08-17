package org.anchoranalysis.image.voxel.assigner;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.voxel.Voxels;

class ShortImplementation extends Base<ShortBuffer> {

    private final short valueCast;

    public ShortImplementation(Voxels<ShortBuffer> voxels, int valueToAssign) {
        super(voxels, valueToAssign);

        valueCast = (short) valueToAssign;
    }

    protected void assignToEntireBuffer(ShortBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put(valueCast);
        }
    }

    @Override
    protected void assignAtBufferPosition(ShortBuffer buffer, int index) {
        buffer.put(index, (short) valueToAssign);
    }
}
