/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.kernel;

import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;

@NoArgsConstructor
public class ObjectOnVoxelsHelper {

    /**
     * Creates {@link Voxels} showing an object on top of an otherwise empty background.
     *
     * @param object the object to show
     * @param extent the size of the {@link Voxels} to create.
     * @param objectOn if true, the objects use <i>on</i> and the background <i>off</i>. if false,
     *     the opposite combination.
     * @return a newly created {@link BinaryVoxels} using default values for <i>off</i> and
     *     </i>on</i>.
     */
    public static BinaryVoxels<UnsignedByteBuffer> createVoxelsWithObject(
            ObjectMask object, Extent extent, boolean objectOn) {
        if (objectOn) {
            BinaryVoxels<UnsignedByteBuffer> voxels = BinaryVoxelsFactory.createEmptyOff(extent);
            voxels.assignOn().toObject(object);
            return voxels;
        } else {
            BinaryVoxels<UnsignedByteBuffer> voxels = BinaryVoxelsFactory.createEmptyOn(extent);
            voxels.assignOff().toObject(object);
            return voxels;
        }
    }
}
