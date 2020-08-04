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

package org.anchoranalysis.image.binary.logical;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BinaryChnlOr {

    /**
     * Performs a OR operation on each voxel in two masks, writing the result onto the second mask.
     *
     * @param first the first channel for operation
     * @param second the second channel for operation (and in which the result is written)
     */
    public static void binaryOr(Mask first, Mask second) {

        BinaryValuesByte bvbCrnt = first.binaryValues().createByte();
        BinaryValuesByte bvbReceiver = second.binaryValues().createByte();

        Extent e = first.dimensions().extent();

        byte crntOn = bvbCrnt.getOnByte();
        byte receiveOn = bvbReceiver.getOnByte();

        // All the on voxels in the receive, are put onto crnt
        for (int z = 0; z < e.z(); z++) {

            ByteBuffer bufSrc = first.voxels().slice(z).buffer();
            ByteBuffer bufReceive = second.voxels().slice(z).buffer();

            int offset = 0;
            for (int y = 0; y < e.y(); y++) {
                for (int x = 0; x < e.x(); x++) {

                    byte byteRec = bufReceive.get(offset);
                    if (byteRec == receiveOn) {
                        bufSrc.put(offset, crntOn);
                    }

                    offset++;
                }
            }
        }
    }
}
