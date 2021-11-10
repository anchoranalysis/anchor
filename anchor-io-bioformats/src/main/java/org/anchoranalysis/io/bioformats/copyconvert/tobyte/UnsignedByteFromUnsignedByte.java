package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Like {@link ToUnsignedByte} and provides common functionality when converting to an <i>unsigned
 * byte</i>.
 *
 * <p>As this is the same as the source type, optimizations can occur to copy the existing memory
 * without further manipulation.
 *
 * @author Owen Feehan
 */
public abstract class UnsignedByteFromUnsignedByte extends ToUnsignedByte {

    @Override
    protected boolean supportsMultipleChannelsPerSourceBuffer() {
        return true;
    }

    @Override
    protected int bytesPerVoxel() {
        return 1;
    }

    @Override
    protected UnsignedByteBuffer convert(
            ByteBuffer source,
            int channelIndexRelative,
            OrientationChange orientationCorrection,
            boolean littleEndian)
            throws IOException {
        if (isSourceIdenticalToDestination(source, channelIndexRelative, orientationCorrection)) {
            // Reuse the existing buffer, if it's single channeled
            return UnsignedByteBuffer.wrapRaw(source);
        } else {
            return super.convert(source, channelIndexRelative, orientationCorrection, littleEndian);
        }
    }

    private boolean isSourceIdenticalToDestination(
            ByteBuffer source, int channelIndexRelative, OrientationChange orientationCorrection) {
        return source.capacity() == destinationSize
                && sourceIncrement == destinationSize
                && channelIndexRelative == 0
                && orientationCorrection == OrientationChange.KEEP_UNCHANGED;
    }
}
