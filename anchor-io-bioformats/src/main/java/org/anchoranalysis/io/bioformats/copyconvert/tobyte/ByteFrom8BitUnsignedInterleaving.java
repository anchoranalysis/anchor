/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class ByteFrom8BitUnsignedInterleaving extends ConvertToByte {

    private int bytesPerPixelOut = 1;
    private int sizeXY;
    private int numChnlsPerByteArray;

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeXY = sd.getX() * sd.getY();
        this.numChnlsPerByteArray = numChnlsPerByteArray;
    }

    @Override
    protected VoxelBuffer<ByteBuffer> convertSingleChnl(byte[] src, int channelRelative) {
        ByteBuffer buffer = ByteBuffer.wrap(src);

        int sizeTotalBytes = sizeXY * bytesPerPixelOut;
        byte[] crntChnlBytes = new byte[sizeTotalBytes];

        // Loop through the relevant positions
        int totalBytesBuffer = sizeXY * numChnlsPerByteArray;

        int indOut = 0;
        for (int indIn = channelRelative; indIn < totalBytesBuffer; indIn += numChnlsPerByteArray) {
            crntChnlBytes[indOut++] = buffer.get(indIn);
        }
        return VoxelBufferByte.wrap(crntChnlBytes);
    }
}
