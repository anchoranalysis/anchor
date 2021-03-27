package org.anchoranalysis.io.imagej.convert;

import ij.process.ColorProcessor;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.spatial.Extent;

@AllArgsConstructor
class RGBVoxels {
    private Voxels<UnsignedByteBuffer> red;
    private Voxels<UnsignedByteBuffer> green;
    private Voxels<UnsignedByteBuffer> blue;

    public ColorProcessor createColorProcessor(Extent extent, int z) {
        ColorProcessor processor = new ColorProcessor(extent.x(), extent.y());
        processor.setRGB(extractSlice(red, z), extractSlice(green, z), extractSlice(blue, z));
        return processor;
    }

    private static byte[] extractSlice(Voxels<UnsignedByteBuffer> voxels, int z) {
        return voxels.sliceBuffer(z).array();
    }
}
