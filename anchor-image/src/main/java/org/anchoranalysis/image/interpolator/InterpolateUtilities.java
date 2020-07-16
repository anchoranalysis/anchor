/* (C)2020 */
package org.anchoranalysis.image.interpolator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.interpolator.transfer.Transfer;
import org.anchoranalysis.image.interpolator.transfer.TransferViaByte;
import org.anchoranalysis.image.interpolator.transfer.TransferViaShort;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InterpolateUtilities {

    private static Transfer createTransfer(VoxelBoxWrapper src, VoxelBoxWrapper dest) {

        if (!src.getVoxelDataType().equals(dest.getVoxelDataType())) {
            throw new IncorrectVoxelDataTypeException(
                    "Data types don't match between src and dest");
        }

        if (src.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            return new TransferViaByte(src, dest);
        } else if (src.getVoxelDataType().equals(VoxelDataTypeUnsignedShort.INSTANCE)) {
            return new TransferViaShort(src, dest);
        } else {
            throw new IncorrectVoxelDataTypeException("Only unsigned byte and short are supported");
        }
    }

    public static void transferSlicesResizeXY(
            VoxelBoxWrapper src, VoxelBoxWrapper trgt, Interpolator interpolator) {

        Extent eSrc = src.any().extent();
        Extent eTrgt = trgt.any().extent();

        Transfer biWrapper = createTransfer(src, trgt);

        for (int z = 0; z < eSrc.getZ(); z++) {

            biWrapper.assignSlice(z);
            if (eSrc.getX() == eTrgt.getX() && eSrc.getY() == eTrgt.getY()) {
                biWrapper.transferCopyTo(z);
            } else {
                if (eSrc.getX() != 1 && eSrc.getY() != 1) {
                    // We only bother to interpolate when we have more than a single pixel in both
                    // directions
                    // And in this case, some of the interpolation algorithms would crash.
                    biWrapper.transferTo(z, interpolator);
                } else {
                    biWrapper.transferTo(z, InterpolatorFactory.getInstance().noInterpolation());
                }
            }
        }
        assert (trgt.any().getPixelsForPlane(0).buffer().capacity() == eTrgt.getVolumeXY());
    }
}
