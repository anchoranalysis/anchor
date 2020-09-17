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

package org.anchoranalysis.image.binary.voxel;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.convert.UnsignedIntBuffer;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.factory.VoxelsFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryVoxelsFactory {

    /**
     * Creates binary-voxels using unsigned 8-bit byte type and with all voxels set to ON (255)
     *
     * @param extent the size of the voxels
     * @return newly created binary-voxels of specified size with all voxels initialized to ON.
     */
    public static BinaryVoxels<UnsignedByteBuffer> createEmptyOn(Extent extent) {
        BinaryVoxels<UnsignedByteBuffer> voxels = createEmptyOff(extent);
        voxels.assignOn().toAll();
        return voxels;
    }

    /**
     * Creates binary-voxels using unsigned 8-bit byte type and with all voxels set to <i>off</i>
     * (0).
     *
     * @param extent the size of the voxels
     * @return newly created binary-voxels of specified size with all voxels initialized to
     *     <i>off</i>.
     */
    @SuppressWarnings("unchecked")
    public static BinaryVoxels<UnsignedByteBuffer> createEmptyOff(Extent extent) {
        try {
            return (BinaryVoxels<UnsignedByteBuffer>)
                    createEmptyOff(extent, UnsignedByteVoxelType.INSTANCE);
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Creates an empty binary-voxels of a particular data-type with all voxels initialized to OFF
     * (0)
     *
     * @param extent the size of the voxels
     * @param dataType the data-type of the underlying voxel-buffer, either unsigned-byte or
     *     unsigned-int
     * @return newly created empty binary-voxels of specified size (all voxels initialized to 0)
     * @throws CreateException if an unsupported data-type is requested
     */
    public static BinaryVoxels<?> createEmptyOff( // NOSONAR
            Extent extent, VoxelDataType dataType) throws CreateException {
        if (dataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return new BinaryVoxelsByte(
                    VoxelsFactory.getUnsignedByte().createInitialized(extent), BinaryValues.getDefault());
        } else if (dataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return new BinaryVoxelsInt(
                    VoxelsFactory.getUnsignedInt().createInitialized(extent), BinaryValues.getDefault());
        } else {
            throw new CreateException(
                    "Unsupported voxel-data-type, only unsigned byte and int are supported");
        }
    }

    /**
     * Like {@link #reuseByte} but uses default binary-values for OFF (0) and ON (255).
     *
     * @param voxels voxel-buffer to treat as binary (and internally reused)
     * @return newly created binary-voxels reusing existing voxels internally
     */
    public static BinaryVoxels<UnsignedByteBuffer> reuseByte(Voxels<UnsignedByteBuffer> voxels) {
        return reuseByte(voxels, BinaryValues.getDefault());
    }

    /**
     * Reuses an existing voxel-buffer (of type unsigned byte) as a binary-version which should have
     * only two intensity-values representing OFF and ON
     *
     * <p>No check occurs that only these intensity values exist.
     *
     * @param voxels voxel-buffer to treat as binary (and internally reused)
     * @param binaryValues how to interpret OFF and ON states
     * @return newly created binary-voxels reusing existing voxels internally
     */
    public static BinaryVoxels<UnsignedByteBuffer> reuseByte(
            Voxels<UnsignedByteBuffer> voxels, BinaryValues binaryValues) {
        return new BinaryVoxelsByte(voxels, binaryValues);
    }

    /**
     * Reuses an existing voxel-buffer (of type unsigned int) as a binary-version which should have
     * only two intensity-values representing OFF and ON
     *
     * <p>No check occurs that only these intensity values exist.
     *
     * @param voxels voxel-buffer to treat as binary (and internally reused)
     * @param binaryValues how to interpret OFF and ON states
     * @return newly created binary-voxels reusing existing voxels internally
     */
    public static BinaryVoxels<UnsignedIntBuffer> reuseInt(
            Voxels<UnsignedIntBuffer> voxels, BinaryValues binaryValues) {
        return new BinaryVoxelsInt(voxels, binaryValues);
    }
}
