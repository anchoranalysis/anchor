/* (C)2020 */
package org.anchoranalysis.image.object.factory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
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
        return createConnectedComponents(chnl.binaryVoxelBox());
    }

    // This consumes the voxel buffer 'vb'
    public ObjectCollection createConnectedComponents(BinaryVoxelBox<ByteBuffer> vb)
            throws CreateException {
        try {
            return unionFind.deriveConnectedByte(vb);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    // This consumes the voxel buffer 'vb'
    public ObjectCollection create(BinaryVoxelBox<IntBuffer> vb) throws CreateException {
        try {
            return unionFind.deriveConnectedInt(vb);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}
