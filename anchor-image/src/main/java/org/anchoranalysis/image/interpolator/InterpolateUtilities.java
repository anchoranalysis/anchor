/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.interpolator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.interpolator.transfer.Transfer;
import org.anchoranalysis.image.interpolator.transfer.TransferViaByte;
import org.anchoranalysis.image.interpolator.transfer.TransferViaShort;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InterpolateUtilities {

    private static Transfer createTransfer(VoxelsWrapper src, VoxelsWrapper dest) {

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
            VoxelsWrapper src, VoxelsWrapper trgt, Interpolator interpolator) {

        Extent eSrc = src.any().extent();
        Extent eTrgt = trgt.any().extent();

        Transfer biWrapper = createTransfer(src, trgt);

        for (int z = 0; z < eSrc.z(); z++) {

            biWrapper.assignSlice(z);
            if (eSrc.x() == eTrgt.x() && eSrc.y() == eTrgt.y()) {
                biWrapper.transferCopyTo(z);
            } else {
                if (eSrc.x() != 1 && eSrc.y() != 1) {
                    // We only bother to interpolate when we have more than a single pixel in both
                    // directions
                    // And in this case, some of the interpolation algorithms would crash.
                    biWrapper.transferTo(z, interpolator);
                } else {
                    biWrapper.transferTo(z, InterpolatorFactory.getInstance().noInterpolation());
                }
            }
        }
        assert (trgt.any().sliceBuffer(0).capacity() == eTrgt.volumeXY());
    }
}
