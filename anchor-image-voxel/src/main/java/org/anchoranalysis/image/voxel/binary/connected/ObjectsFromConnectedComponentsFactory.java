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

package org.anchoranalysis.image.voxel.binary.connected;

import org.anchoranalysis.image.voxel.binary.BinaryVoxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedIntBuffer;
import org.anchoranalysis.image.voxel.object.ObjectCollection;

/**
 * Creates a {@link ObjectCollection} from the connected-components of a mask or binary-voxels.
 *
 * @author Owen Feehan
 */
public class ObjectsFromConnectedComponentsFactory {

    private final ConnectedComponentUnionFind unionFind;

    public ObjectsFromConnectedComponentsFactory() {
        this(false);
    }

    public ObjectsFromConnectedComponentsFactory(boolean bigNeighborhood) {
        this(bigNeighborhood, 1);
    }

    public ObjectsFromConnectedComponentsFactory(int minNumberVoxels) {
        this(false, minNumberVoxels);
    }

    public ObjectsFromConnectedComponentsFactory(boolean bigNeighborhood, int minNumberVoxels) {
        unionFind = new ConnectedComponentUnionFind(minNumberVoxels, bigNeighborhood);
    }

    // This consumes the voxel buffer 'vb'
    public ObjectCollection createUnsignedByte(BinaryVoxels<UnsignedByteBuffer> voxels) {
        return unionFind.deriveConnectedByte(voxels);
    }

    // This consumes the voxel buffer 'vb'
    public ObjectCollection createUnsignedInt(BinaryVoxels<UnsignedIntBuffer> voxels) {
        return unionFind.deriveConnectedInt(voxels);
    }
}
