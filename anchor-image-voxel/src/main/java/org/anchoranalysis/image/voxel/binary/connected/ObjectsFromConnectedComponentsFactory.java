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
import org.anchoranalysis.image.voxel.neighborhood.NeighborhoodFactory;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Creates a {@link ObjectCollection} from the connected-components of a mask or binary-voxels.
 *
 * @author Owen Feehan
 */
public class ObjectsFromConnectedComponentsFactory {

    private final ConnectedComponentUnionFind unionFind;

    /**
     * Creates to use a <i>small</i>-neighborhood.
     *
     * <p>See {@link org.anchoranalysis.image.voxel.neighborhood.NeighborhoodFactory}.
     */
    public ObjectsFromConnectedComponentsFactory() {
        this(false);
    }

    /**
     * Creates to use a specified type of neighborhood.
     *
     * @param bigNeighborhood if true, use 8-Connectivity instead of 4 in 2D, and 26-connectivity
     *     instead of 6 in 3D, as per {@link NeighborhoodFactory}.
     */
    public ObjectsFromConnectedComponentsFactory(boolean bigNeighborhood) {
        this(bigNeighborhood, 1);
    }

    /**
     * Creates to use a minimum number of voxels for each connected component.
     *
     * @param minNumberVoxels the minimum number of voxels that must exist for an independent
     *     connected-component, otherwise the connected-component is omitted.
     */
    public ObjectsFromConnectedComponentsFactory(int minNumberVoxels) {
        this(false, minNumberVoxels);
    }

    /**
     * Creates to use a specified type of neighborhood, and minimum number of voxels.
     *
     * @param bigNeighborhood if true, use 8-Connectivity instead of 4 in 2D, and 26-connectivity
     *     instead of 6 in 3D, as per {@link NeighborhoodFactory}.
     * @param minNumberVoxels the minimum number of voxels that must exist for an independent
     *     connected-component, otherwise the connected-component is omitted.
     */
    public ObjectsFromConnectedComponentsFactory(boolean bigNeighborhood, int minNumberVoxels) {
        unionFind = new ConnectedComponentUnionFind(minNumberVoxels, bigNeighborhood);
    }

    /**
     * Finds the connected-components in <i>unsigned byte</i> voxels.
     *
     * <p>Connected-components are considered to be contiguous neighboring voxels with <i>on</i>
     * states.
     *
     * @param voxels the voxels to find connected-components in. Note that these voxel values are
     *     changed, as the algorithm runs.
     * @return the connected-components, each encoded as an {@link ObjectMask}.
     */
    public ObjectCollection createUnsignedByte(BinaryVoxels<UnsignedByteBuffer> voxels) {
        return unionFind.deriveConnectedByte(voxels);
    }

    /**
     * Finds the connected-components in <i>unsigned int</i> voxels.
     *
     * <p>Connected-components are considered to be contiguous neighboring voxels with <i>on</i>
     * states.
     *
     * @param voxels the voxels to find connected-components in. Note that these voxel values are
     *     changed, as the algorithm runs.
     * @return the connected-components, each encoded as an {@link ObjectMask}.
     */
    public ObjectCollection createUnsignedInt(BinaryVoxels<UnsignedIntBuffer> voxels) {
        return unionFind.deriveConnectedInt(voxels);
    }
}
