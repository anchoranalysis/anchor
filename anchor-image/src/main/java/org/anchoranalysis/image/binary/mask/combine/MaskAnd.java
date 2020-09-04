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

package org.anchoranalysis.image.binary.mask.combine;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.IterateVoxels;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskAnd {

    /**
     * Performs a AND operation on each voxel in two masks, writing the result onto the second mask.
     *
     * @param first the first channel for operation (and in which the result is written)
     * @param second the second channel for operation
     */
    public static void apply(Mask first, Mask second) {
        apply(
                first.voxels(),
                second.voxels(),
                first.binaryValues().createByte(),
                second.binaryValues().createByte());
    }

    /**
     * Performs a AND operation on each voxel in two {@link Voxels} (considered to be masks),
     * writing the result onto the second mask.
     *
     * @param voxelsFirst the first voxels for the operation (and in which the result is written)
     * @param voxelsSecond the second voxels for the operation
     * @param bvbFirst binary-values to mask first voxels
     * @param bvbSecond binary-values to mask second voxels
     */
    public static void apply(
            Voxels<UnsignedByteBuffer> voxelsFirst,
            Voxels<UnsignedByteBuffer> voxelsSecond,
            BinaryValuesByte bvbFirst,
            BinaryValuesByte bvbSecond) {

        byte sourceOn = bvbFirst.getOnByte();
        byte sourceOff = bvbFirst.getOffByte();
        byte receiveOff = bvbSecond.getOffByte();

        IterateVoxels.callEachPointTwo(
                voxelsFirst,
                voxelsSecond,
                (point, bufferSource, bufferReceive, offset) -> {
                    if (bufferSource.get(offset) == sourceOn
                            && bufferReceive.get(offset) == receiveOff) {
                        // source is ON but receive is OFF, so we change the buffer
                        bufferSource.put(offset, sourceOff);
                    }
                });
    }
}
