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

package org.anchoranalysis.image.binary.mask;

import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.box.VoxelBox;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskInverter {

    public static ObjectMask invertObjectDuplicate(ObjectMask object) {
        BinaryVoxelBox<ByteBuffer> bvb = object.binaryVoxelBox().duplicate();
        bvb.invert();
        return new ObjectMask(bvb);
    }

    public static void invertChnl(Mask chnl) {

        BinaryValues bv = chnl.getBinaryValues();
        BinaryValuesByte bvb = bv.createByte();
        invertVoxelBox(chnl.getVoxelBox(), bvb);
    }

    public static void invertVoxelBox(VoxelBox<ByteBuffer> vb, BinaryValuesByte bvb) {
        for (int z = 0; z < vb.extent().getZ(); z++) {

            ByteBuffer bb = vb.getPixelsForPlane(z).buffer();

            int offset = 0;
            for (int y = 0; y < vb.extent().getY(); y++) {
                for (int x = 0; x < vb.extent().getX(); x++) {

                    byte val = bb.get(offset);

                    if (val == bvb.getOnByte()) {
                        bb.put(offset, bvb.getOffByte());
                    } else {
                        bb.put(offset, bvb.getOnByte());
                    }

                    offset++;
                }
            }
        }
    }
}
