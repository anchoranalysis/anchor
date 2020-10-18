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

package org.anchoranalysis.image.core.mask;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.image.voxel.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Inverts masks and objects
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskInverter {

    /**
     * Creates a new object-mask where OFF voxels become ON voxels and vice versa
     *
     * @param object object to invert (unmodified)
     * @return a newly created inverted object
     */
    public static ObjectMask invertObjectDuplicate(ObjectMask object) {
        BinaryVoxels<UnsignedByteBuffer> voxels = object.binaryVoxels().duplicate();
        voxels.invert();
        return new ObjectMask(voxels);
    }

    /**
     * Modifies a mask so that OFF voxels become ON voxels and vice versa
     *
     * <p>The modification occur inplace, so no new masks are created.
     *
     * @param mask mask to invert (modified)
     */
    public static void invert(Mask mask) {

        BinaryValues bv = mask.binaryValues();
        BinaryValuesByte bvb = bv.createByte();
        invertVoxels(mask.voxels(), bvb);
    }

    private static void invertVoxels(Voxels<UnsignedByteBuffer> voxels, BinaryValuesByte bvb) {
        for (int z = 0; z < voxels.extent().z(); z++) {

            UnsignedByteBuffer buffer = voxels.sliceBuffer(z);

            int offset = 0;
            for (int y = 0; y < voxels.extent().y(); y++) {
                for (int x = 0; x < voxels.extent().x(); x++) {

                    byte val = buffer.getRaw(offset);

                    if (val == bvb.getOnByte()) {
                        buffer.putRaw(offset, bvb.getOffByte());
                    } else {
                        buffer.putRaw(offset, bvb.getOnByte());
                    }

                    offset++;
                }
            }
        }
    }
}
