/* (C)2020 */
package org.anchoranalysis.image.voxel.box.thresholder;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.iterator.ProcessVoxelSliceBuffer;

final class PointProcessor implements ProcessVoxelSliceBuffer<ByteBuffer> {

    private final int level;
    private final VoxelBox<ByteBuffer> boxOut;
    private final byte byteOn;
    private final byte byteOff;

    private ByteBuffer bbOut;

    public PointProcessor(int level, VoxelBox<ByteBuffer> boxOut, BinaryValuesByte bvOut) {
        super();
        this.level = level;
        this.boxOut = boxOut;
        this.byteOn = bvOut.getOnByte();
        this.byteOff = bvOut.getOffByte();
    }

    @Override
    public void notifyChangeZ(int z) {
        bbOut = boxOut.getPixelsForPlane(z).buffer();
    }

    @Override
    public void process(Point3i point, ByteBuffer buffer, int offset) {
        int val = ByteConverter.unsignedByteToInt(buffer.get(offset));

        bbOut.put(offset, val >= level ? byteOn : byteOff);
    }
}
