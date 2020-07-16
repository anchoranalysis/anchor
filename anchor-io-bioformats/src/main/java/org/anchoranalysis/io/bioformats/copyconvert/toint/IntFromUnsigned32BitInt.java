/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.toint;

import java.nio.IntBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferInt;

public class IntFromUnsigned32BitInt extends ConvertToInt {

    private int bytesPerPixel = 4;
    private int sizeXY;
    private int sizeBytes;

    private boolean littleEndian;

    public IntFromUnsigned32BitInt(boolean littleEndian) {
        super();
        this.littleEndian = littleEndian;
    }

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeXY = sd.getX() * sd.getY();
        sizeBytes = sizeXY * bytesPerPixel;
    }

    @Override
    protected VoxelBuffer<IntBuffer> convertSingleChnl(byte[] src, int channelRelative) {

        int[] crntChnlBytes = new int[sizeXY];

        int indOut = 0;
        for (int indIn = 0; indIn < sizeBytes; indIn += bytesPerPixel) {
            int s = DataTools.bytesToInt(src, indIn, bytesPerPixel, littleEndian);
            crntChnlBytes[indOut++] = s;
        }

        return VoxelBufferInt.wrap(crntChnlBytes);
    }
}
