/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class ByteFrom32BitUnsignedInt extends ConvertToByte {

    private double convertRatio;
    private int bytesPerPixel = 4;
    private int sizeXY;
    private int sizeBytes;

    private int effectiveBitsPerPixel;
    private boolean littleEndian;

    public ByteFrom32BitUnsignedInt(int effectiveBitsPerPixel, boolean littleEndian) {
        super();
        this.effectiveBitsPerPixel = effectiveBitsPerPixel;
        this.littleEndian = littleEndian;
    }

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {

        convertRatio = calculateConvertRatio();

        sizeXY = sd.getX() * sd.getY();
        sizeBytes = sizeXY * bytesPerPixel;
    }

    @Override
    protected VoxelBuffer<ByteBuffer> convertSingleChnl(byte[] src, int channelRelative) {
        byte[] crntChnlBytes = new byte[sizeXY];

        int indOut = 0;
        for (int indIn = 0; indIn < sizeBytes; indIn += bytesPerPixel) {
            int i = DataTools.bytesToInt(src, indIn, littleEndian);
            crntChnlBytes[indOut++] = (byte) (i * convertRatio);
        }
        return VoxelBufferByte.wrap(crntChnlBytes);
    }

    private double calculateConvertRatio() {
        if (effectiveBitsPerPixel == 32) {
            return 1.0;
        } else {
            return ConvertHelper.twoToPower(-1 * (effectiveBitsPerPixel - 8));
        }
    }
}
