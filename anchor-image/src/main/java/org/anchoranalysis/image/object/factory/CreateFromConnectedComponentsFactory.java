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

package org.anchoranalysis.image.object.factory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.voxel.BinaryVoxels;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.factory.unionfind.ConnectedComponentUnionFind;

public class CreateFromConnectedComponentsFactory {

    private final ConnectedComponentUnionFind unionFind;

    public CreateFromConnectedComponentsFactory() {
        this(false);
    }

    public CreateFromConnectedComponentsFactory(boolean bigNeighborhood) {
        this(bigNeighborhood, 1);
    }

    public CreateFromConnectedComponentsFactory(int minNumberVoxels) {
        this(false, minNumberVoxels);
    }

    public CreateFromConnectedComponentsFactory(boolean bigNeighborhood, int minNumberVoxels) {
        unionFind = new ConnectedComponentUnionFind(minNumberVoxels, bigNeighborhood);
    }

    public ObjectCollection createConnectedComponents(Mask chnl) throws CreateException {
        return createConnectedComponents(chnl.binaryVoxels());
    }

    // This consumes the voxel buffer 'vb'
    public ObjectCollection createConnectedComponents(BinaryVoxels<ByteBuffer> vb)
            throws CreateException {
        try {
            return unionFind.deriveConnectedByte(vb);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    // This consumes the voxel buffer 'vb'
    public ObjectCollection create(BinaryVoxels<IntBuffer> vb) throws CreateException {
        try {
            return unionFind.deriveConnectedInt(vb);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
