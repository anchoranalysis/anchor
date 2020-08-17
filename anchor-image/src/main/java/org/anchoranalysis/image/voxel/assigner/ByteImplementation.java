package org.anchoranalysis.image.voxel.assigner;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.voxel.Voxels;

class ByteImplementation extends Base<ByteBuffer> {
        
    private final byte valueCast;
    
    public ByteImplementation(Voxels<ByteBuffer> voxels, int valueToAssign) {
        super(voxels, valueToAssign);
        
        valueCast = (byte) valueToAssign;
    }

    @Override
    protected void assignToEntireBuffer(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            buffer.put(valueCast);
        }
    }
    
    @Override
    protected void assignAtBufferPosition(ByteBuffer buffer, int index) {
        buffer.put(index, valueCast);
        
    }
}
