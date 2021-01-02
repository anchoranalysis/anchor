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

package org.anchoranalysis.image.core.mask.combine;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskXor {

    /**
     * Performs a XOR (exclusive OR) operation on each voxel in two masks, writing the result onto
     * the first mask.
     *
     * @param first the first channel for operation (and in which the result is written)
     * @param second the second channel for operation
     */
    public static void apply(Mask first, Mask second) {
        apply(
                first.binaryVoxels(),
                second.binaryVoxels());
    }

    /**
     * Performs a XOR (exclusive OR) operation on each voxel in two {@link Voxels} (considered to be
     * masks), writing the result onto the second mask.
     *
     * @param voxelsFirst the first voxels for operation
     * @param voxelsSecond the second voxels for operation (and in which the result is written)
     */
    public static void apply(
            BinaryVoxels<UnsignedByteBuffer> voxelsFirst,
            BinaryVoxels<UnsignedByteBuffer> voxelsSecond
            ) {

        BinaryValuesByte binaryValuesFirst = voxelsFirst.binaryValues().createByte();
        BinaryValuesByte binaryValuesSecond = voxelsSecond.binaryValues().createByte();
        byte sourceOn = binaryValuesFirst.getOnByte();
        byte sourceOff = binaryValuesFirst.getOffByte();

        byte receiveOn = binaryValuesSecond.getOnByte();

        IterateVoxelsAll.withTwoBuffersAndPoint(
                voxelsFirst.voxels(),
                voxelsSecond.voxels(),
                (point, bufferSource, bufferReceive, offsetSource, offsetReceive) -> {
                    boolean identicalStates =
                            (bufferSource.getRaw(offsetSource) == sourceOn)
                                    == (bufferReceive.getRaw(offsetReceive) == receiveOn);

                    if (identicalStates) {
                        bufferSource.putRaw(offsetSource, sourceOff);
                    } else {
                        bufferSource.putRaw(offsetSource, sourceOn);
                    }
                });
    }
}
