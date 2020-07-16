/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class ByteFrom32BitFloat extends ConvertToByte {

    private static final int BYTES_PER_PIXEL = 4;

    private int sizeXY;
    private int sizeBytes;

    private boolean littleEndian;

    public ByteFrom32BitFloat(boolean littleEndian) {
        super();
        this.littleEndian = littleEndian;
    }

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeXY = sd.getX() * sd.getY();
        sizeBytes = sizeXY * BYTES_PER_PIXEL;
    }

    @Override
    protected VoxelBuffer<ByteBuffer> convertSingleChnl(byte[] src, int channelRelative) {
        byte[] crntChnlBytes = new byte[sizeXY];

        int indOut = 0;
        for (int indIn = 0; indIn < sizeBytes; indIn += BYTES_PER_PIXEL) {
            float f = DataTools.bytesToFloat(src, indIn, littleEndian);

            if (f > 255) {
                f = 255;
            }
            if (f < 0) {
                f = 0;
            }
            crntChnlBytes[indOut++] = (byte) (f);
        }
        return VoxelBufferByte.wrap(crntChnlBytes);
    }
}
