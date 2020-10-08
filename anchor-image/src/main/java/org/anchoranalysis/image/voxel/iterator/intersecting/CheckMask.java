package org.anchoranalysis.image.voxel.iterator.intersecting;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferTernary;

/**
 * Processes voxels checking that they lie on a mask, converting a {@link ProcessBufferTernary} to a
 * {@code ProcessBufferBinary<UnsignedByteBuffer>}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class CheckMask implements ProcessBufferTernary<UnsignedByteBuffer> {

    private final ProcessBufferBinary<UnsignedByteBuffer> process;
    private final byte onMaskGlobal;

    @Override
    public void process(
            Point3i point,
            UnsignedByteBuffer buffer1,
            UnsignedByteBuffer buffer2,
            UnsignedByteBuffer bufferObject,
            int offset1,
            int offset2,
            int offsetObject) {
        byte globalMask = bufferObject.getRaw(offsetObject);
        if (globalMask == onMaskGlobal) {
            process.process(point, buffer1, buffer2, offset1, offset2);
        }
    }

    @Override
    public void notifyChangeSlice(int z) {
        process.notifyChangeSlice(z);
    }
}
