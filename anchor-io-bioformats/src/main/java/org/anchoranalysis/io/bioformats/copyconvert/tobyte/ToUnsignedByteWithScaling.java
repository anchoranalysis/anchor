package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Like {@link ToUnsignedByte} but applies scaling, if necessary, to map the original value to
 * 8-bits.
 *
 * @author Owen Feehan
 */
public abstract class ToUnsignedByteWithScaling extends ToUnsignedByte {

    protected final ApplyScaling scaling;

    /**
     * Create with the number of bits that are used in the input-type.
     *
     * <p>e.g. 8 or 12 or 16.
     *
     * <p>This should always be a positive number.
     *
     * <p>If {@code > 8}, then scaling is applied to values, to map them to the 8-bits available in
     * an <i>unsigned-byte</i>.
     *
     * <p>If {@code <= 8}, then no scaling is applied.
     *
     * @param effectiveBits the number of bits that are used in the input-byte, from which a scaling
     *     factor is derived.
     */
    protected ToUnsignedByteWithScaling(int effectiveBits) {
        scaling = new ApplyScaling(effectiveBits, UnsignedByteVoxelType.INSTANCE);
    }

    @Override
    protected boolean supportsMultipleChannelsPerSourceBuffer() {
        return false;
    }

    @Override
    protected void copyKeepOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedByteBuffer destination) {
        byte[] sourceArray = source.array();
        for (int index = 0; index < sourceSize; index += sourceIncrement) {
            int value = extractScaledValue(sourceArray, index, littleEndian);
            destination.putUnsigned(value);
        }
    }

    @Override
    protected void copyChangeOrientation(
            ByteBuffer source,
            boolean littleEndian,
            int channelIndexRelative,
            UnsignedByteBuffer destination,
            OrientationChange orientationCorrection) {
        byte[] sourceArray = source.array();
        int x = 0;
        int y = 0;

        for (int index = 0; index < sourceSize; index += sourceIncrement) {
            int value = extractScaledValue(sourceArray, index, littleEndian);

            int indexOut = orientationCorrection.index(x, y, extent);
            destination.putUnsigned(indexOut, value);

            x++;
            if (x == extent.x()) {
                y++;
                x = 0;
            }
        }
    }

    /** Extracts a value from the source-array, and apply any scaling and clamping. */
    protected abstract int extractScaledValue(byte[] sourceArray, int index, boolean littleEndian);
}
