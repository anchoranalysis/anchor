/* (C)2020 */
package org.anchoranalysis.image.voxel.kernel;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.voxel.box.VoxelBox;

// Caches a small number of slices around which we wish to work, so the memory
//  we are interested in is nearby
public class LocalSlices {

    private final byte[][] arr;
    private final int shift;

    // Loads the local slices
    public LocalSlices(int z, int kernelSize, VoxelBox<ByteBuffer> bufferAccess) {
        super();

        arr = new byte[kernelSize][];

        shift = ((kernelSize - 1) / 2);

        for (int i = 0; i < kernelSize; i++) {

            int rel = z + i - shift;

            if (rel >= 0 && rel < bufferAccess.extent().getZ()) {
                arr[i] = bufferAccess.getPixelsForPlane(rel).buffer().array();
            }
        }
    }

    // All local access is done relative (e.g. -1, -2, +1, +2 etc.)
    // If an invalid index is requested null is returned
    public ByteBuffer getLocal(int rel) {
        byte[] slice = arr[rel + shift];
        return slice != null ? ByteBuffer.wrap(slice) : null;
    }
}
