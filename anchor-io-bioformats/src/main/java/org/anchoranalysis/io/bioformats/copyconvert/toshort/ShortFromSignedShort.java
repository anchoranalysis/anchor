/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.toshort;

import java.nio.ShortBuffer;
import loci.common.DataTools;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferShort;

public class ShortFromSignedShort extends ConvertToShort {

    private int bytesPerPixel = 2;
    private int sizeXY;
    private int sizeBytes;

    private boolean littleEndian;

    public ShortFromSignedShort(boolean littleEndian) {
        super();
        this.littleEndian = littleEndian;
    }

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeXY = sd.getX() * sd.getY();
        sizeBytes = sizeXY * bytesPerPixel;
    }

    @Override
    protected VoxelBuffer<ShortBuffer> convertSingleChnl(byte[] src, int channelRelative) {

        short[] crntChnlShorts = new short[sizeXY];

        int indOut = 0;
        for (int indIn = 0; indIn < sizeBytes; indIn += bytesPerPixel) {
            short s = DataTools.bytesToShort(src, indIn, bytesPerPixel, littleEndian);
            crntChnlShorts[indOut++] = s;
        }

        return VoxelBufferShort.wrap(crntChnlShorts);
    }
}
