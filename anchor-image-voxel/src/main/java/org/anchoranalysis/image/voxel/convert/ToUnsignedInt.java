package org.anchoranalysis.image.voxel.convert;

import java.nio.FloatBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;

/**
 * Base class for conversion to <b>unsigned 32-bit</b>.
 *
 * @author Owen Feehan
 */
public class ToUnsignedInt extends VoxelsConverter<UnsignedIntBuffer> {

    @Override
    protected void convertUnsignedByte(UnsignedByteBuffer in, UnsignedIntBuffer out) {
        out.putUnsigned(in.getUnsigned());
    }

    @Override
    protected void convertUnsignedShort(UnsignedShortBuffer in, UnsignedIntBuffer out) {
        out.putUnsigned(in.getUnsigned());
    }

    @Override
    protected void convertUnsignedInt(UnsignedIntBuffer in, UnsignedIntBuffer out) {
        out.putRaw(in.getRaw());
    }

    @Override
    protected void convertFloat(FloatBuffer in, UnsignedIntBuffer out) {
        out.putFloat(in.get());
    }
}
