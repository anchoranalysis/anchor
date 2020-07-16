/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class ByteFrom16BitUnsigned extends ConvertToByte {

    private int bytesPerPixel;
    private int sizeXY;
    private int sizeBytes;

    private boolean littleEndian;
    private int maxTotalBits;

    public ByteFrom16BitUnsigned(boolean littleEndian, int maxTotalBits) {
        super();
        this.littleEndian = littleEndian;
        this.maxTotalBits = maxTotalBits;
    }

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        bytesPerPixel = 2 * numChnlsPerByteArray;
        sizeXY = sd.getX() * sd.getY();
        sizeBytes = sizeXY * bytesPerPixel;
    }

    @Override
    protected VoxelBuffer<ByteBuffer> convertSingleChnl(byte[] src, int channelRelative) {
        // we assign a default that maps from 16-bit to 8-bit
        ApplyScaling applyScaling = new ApplyScaling(ConvertHelper.twoToPower(8 - maxTotalBits), 0);

        byte[] crntChnlBytes = new byte[sizeXY];

        int indOut = 0;
        for (int indIn = 0; indIn < sizeBytes; indIn += bytesPerPixel) {
            int s =
                    (int)
                            DataTools.bytesToShort(
                                    src, indIn + (channelRelative * 2), 2, littleEndian);

            // Make unsigned
            if (s < 0) {
                s += 65536;
            }

            s = applyScaling.apply(s);

            if (s > 255) {
                s = 255;
            }
            if (s < 0) {
                s = 0;
            }

            crntChnlBytes[indOut++] = (byte) (s);
        }
        return VoxelBufferByte.wrap(crntChnlBytes);
    }
}
