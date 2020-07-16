/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class ByteFrom8BitUnsignedNoInterleaving extends ConvertToByte {

    private int bytesPerPixel = 1;
    private int sizeXY;

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeXY = sd.getX() * sd.getY();
    }

    @Override
    protected VoxelBuffer<ByteBuffer> convertSingleChnl(byte[] src, int channelRelative) {
        ByteBuffer buffer = ByteBuffer.wrap(src);

        int sizeTotalBytes = sizeXY * bytesPerPixel;
        byte[] crntChnlBytes = new byte[sizeTotalBytes];

        buffer.position(sizeTotalBytes * channelRelative);
        buffer.get(crntChnlBytes, 0, sizeTotalBytes);
        return VoxelBufferByte.wrap(crntChnlBytes);
    }
}
