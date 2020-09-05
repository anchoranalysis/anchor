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

package org.anchoranalysis.io.imagej.convert;

import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedShortBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.pixelsforslice.PixelsForSlice;

/**
 * Converts other voxel data-structures (as used by Anchor) to an ImageJ {@link ImageProcessor}.
 *  
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertToImageProcessor {

    private static final VoxelDataType DATA_TYPE_BYTE = UnsignedByteVoxelType.INSTANCE;
    private static final VoxelDataType DATA_TYPE_SHORT = UnsignedShortVoxelType.INSTANCE;

    /**
     * Creates a {@link ImageProcessor} by extracting a slice from a {@link VoxelsWrapper}.
     * 
     * @param voxels the voxels to extract a slice from.
     * @param z slice-index
     * @return a newly created image-procesor (reusing the existing buffer).
     */
    public static ImageProcessor from(VoxelsWrapper voxels, int z) {

        if (voxels.any().extent().volumeXY() != voxels.slice(z).size()) {
            throw new AnchorFriendlyRuntimeException(
                    String.format(
                            "Extent volume (%d) and buffer-capacity (%d) are not equal",
                            voxels.any().extent().volumeXY(),
                            voxels.slice(z).size()));
        }

        if (voxels.getVoxelDataType().equals(DATA_TYPE_BYTE)) {
            return fromByte(voxels.asByte().slices(), z);
        } else if (voxels.getVoxelDataType().equals(DATA_TYPE_SHORT)) {
            return fromShort(voxels.asShort().slices(), z);
        } else {
            throw new IncorrectVoxelTypeException("Only byte or short data types are supported");
        }
    }
    
    /**
     * Creates a {@link ImageProcessor} by extracting a slice from a {@link PixelsForSlice} of type {@link UnsignedByteBuffer}.
     * 
     * @param pixelsForSlice the pixels to extract a slice from.
     * @param z slice-index
     * @return a newly created image-processor (reusing the existing buffer).
     */
    public static ImageProcessor fromByte(PixelsForSlice<UnsignedByteBuffer> pixelsForSlice, int z) {
        return fromByte(pixelsForSlice.slice(z), pixelsForSlice.extent());
    }

    /**
     * Creates a {@link ImageProcessor} by extracting a slice from a {@link PixelsForSlice} of type {@link UnsignedShortBuffer}.
     * 
     * @param pixelsForSlice the pixels to extract a slice from.
     * @param z slice-index
     * @return a newly created image-processor (reusing the existing buffer).
     */

    public static ImageProcessor fromShort(
            PixelsForSlice<UnsignedShortBuffer> pixelsForSlice, int z) {
        return fromShort(pixelsForSlice.slice(z), pixelsForSlice.extent());
    }
    
    /**
     * Creates a {@link ImageProcessor} from voxel-buffer (of type {@code ByteBuffer}) that is already a slice.
     * 
     * @param slice the voxels representing a slice
     * @param extent the size of image to create
     * @return a newly created image-processor (reusing the existing buffer).
     */
    public static ImageProcessor fromByte(VoxelBuffer<UnsignedByteBuffer> slice, Extent extent) {
        return new ByteProcessor(extent.x(), extent.y(), slice.buffer().array(), null);
    }

    /**
     * Creates a {@link ImageProcessor} from voxel-buffer (of type {@code ShortBuffer}) that is already a slice.
     * 
     * @param slice the voxels representing a slice
     * @param extent the size of image to create
     * @return a newly created image-processor (reusing the existing buffer).
     */
    public static ImageProcessor fromShort(
            VoxelBuffer<UnsignedShortBuffer> slice, Extent extent) {
        return new ShortProcessor(extent.x(), extent.y(), slice.buffer().array(), null);
    }
}
