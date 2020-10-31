package org.anchoranalysis.image.voxel.buffer;

import org.anchoranalysis.image.voxel.Voxels;

/**
 * A buffer to which slices may be added to form a projection.
 * 
 * @author Owen Feehan
 *
 * @param <T> buffer-type
 */
public interface ProjectableBuffer<T> {

    /**
     * Adds a slice to the buffer.
     * 
     * @param voxels voxels for the slice.
     */
    void addSlice(VoxelBuffer<T> voxels);
    
    /**
     * Performs any final operation before turning the projected buffer.
     * 
     * <p>This should be called only <i>once</i> per buffer.
     * 
     * @return the projected buffer.
     */
    Voxels<T> completeProjection();
}
