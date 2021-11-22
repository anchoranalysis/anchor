/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.merge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.BoundingBoxFactory;

/**
 * Creates {@link ObjectMask} whose <i>on</i> voxels look like two- or three-dimensional boxes.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BoxObjectFixture {

    /**
     * Creates an {@link ObjectMask} that is a box in 2D.
     *
     * @param coordinate the minimum corner in all three dimensions.
     * @param extent the size in all three dimensions.
     * @return a newly created {@link ObjectMask}.
     */
    public static ObjectMask create2D(int coordinate, int extent) {
        BoundingBox box = BoundingBoxFactory.at(coordinate, coordinate, extent, extent);
        return createFromBox(box);
    }

    /**
     * Creates an {@link ObjectMask} that is a box in 3D.
     *
     * @param coordinate the minimum corner in all three dimensions.
     * @param extent the size in all three dimensions.
     * @return a newly created {@link ObjectMask}.
     */
    public static ObjectMask create3D(int coordinate, int extent) {
        return createFromBox(BoundingBoxFactory.uniform3D(coordinate, extent));
    }

    private static ObjectMask createFromBox(BoundingBox box) {
        ObjectMask object = new ObjectMask(box);
        object = object.invert();
        return object;
    }
}
