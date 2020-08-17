package org.anchoranalysis.image.voxel.assigner;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.Voxels;

class FloatImplementation extends Base<FloatBuffer> {
    
    private final float valueCast;
    
    public FloatImplementation(Voxels<FloatBuffer> voxels,
            int valueToAssign) {
        super(voxels, valueToAssign);
        valueCast = (float) valueToAssign;
    }
    
    @Override
    protected void assignToEntireBuffer(FloatBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put(valueCast);
        }
    }

    @Override
    protected void assignAtBufferPosition(FloatBuffer buffer, int index) {
        buffer.put(index, valueCast);
    }
}
