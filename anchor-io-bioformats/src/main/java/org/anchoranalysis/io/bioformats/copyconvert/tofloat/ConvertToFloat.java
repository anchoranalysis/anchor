/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tofloat;

import java.io.IOException;
import java.nio.FloatBuffer;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferFloat;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

public abstract class ConvertToFloat extends ConvertTo<FloatBuffer> {

    private int sizeBytesChnl;
    private ImageDimensions sd;

    public ConvertToFloat() {
        super(VoxelBoxWrapper::asFloat);
    }

    protected abstract int bytesPerPixel();

    @Override
    protected void setupBefore(ImageDimensions sd, int numChnlsPerByteArray) {
        sizeBytesChnl = sd.getX() * sd.getY() * bytesPerPixel();
        this.sd = sd;
    }

    @Override
    protected VoxelBuffer<FloatBuffer> convertSingleChnl(byte[] src, int channelRelative)
            throws IOException {
        int index = (sizeBytesChnl * channelRelative);
        float[] fArr = convertIntegerBytesToFloatArray(sd, src, index);
        return VoxelBufferFloat.wrap(fArr);
    }

    protected abstract float[] convertIntegerBytesToFloatArray(
            ImageDimensions sd, byte[] src, int srcOffset) throws IOException;
}
