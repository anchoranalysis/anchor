package org.anchoranalysis.image.voxel.convert;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;

/**
 * Base class for conversion to unsigned 8-bit.
 *
 * @author Owen Feehan
 */
public abstract class ToByte extends VoxelsConverter<UnsignedByteBuffer> {

    @Override
    protected void convertUnsignedByte(UnsignedByteBuffer in, UnsignedByteBuffer out) {
        out.putRaw(in.getRaw());
    }
}
