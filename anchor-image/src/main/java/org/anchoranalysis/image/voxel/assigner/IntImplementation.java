package org.anchoranalysis.image.voxel.assigner;

import java.nio.IntBuffer;
import org.anchoranalysis.image.voxel.Voxels;

class IntImplementation extends Base<IntBuffer> {
    
    public IntImplementation(Voxels<IntBuffer> voxels,
            int valueToAssign) {
        super(voxels, valueToAssign);
    }
    
    @Override
    protected void assignToEntireBuffer(IntBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put(valueToAssign);
        }
    }
    
    @Override
    protected void assignAtBufferPosition(IntBuffer buffer, int index) {
        buffer.put(index, valueToAssign);
    }
}
