/* (C)2020 */
package org.anchoranalysis.image.interpolator;

import ij.process.ImageProcessor;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.anchoranalysis.image.convert.IJWrap;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public class InterpolatorImageJ implements Interpolator {

    @Override
    public VoxelBuffer<ByteBuffer> interpolateByte(
            VoxelBuffer<ByteBuffer> src, VoxelBuffer<ByteBuffer> dest, Extent eSrc, Extent eDest) {

        ImageProcessor ipSrc = IJWrap.imageProcessorByte(src, eSrc);
        ImageProcessor ipOut = ipSrc.resize(eDest.getX(), eDest.getY(), true);
        return IJWrap.voxelBufferFromImageProcessorByte(ipOut);
    }

    @Override
    public VoxelBuffer<ShortBuffer> interpolateShort(
            VoxelBuffer<ShortBuffer> src,
            VoxelBuffer<ShortBuffer> dest,
            Extent eSrc,
            Extent eDest) {

        ImageProcessor ipSrc = IJWrap.imageProcessorShort(src, eSrc);
        ImageProcessor ipOut = ipSrc.resize(eDest.getX(), eDest.getY(), true);
        return IJWrap.voxelBufferFromImageProcessorShort(ipOut);
    }

    @Override
    public boolean isNewValuesPossible() {
        return true;
    }
}
