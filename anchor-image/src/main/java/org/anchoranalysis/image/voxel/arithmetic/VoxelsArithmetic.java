package org.anchoranalysis.image.voxel.arithmetic;

import org.anchoranalysis.image.object.ObjectMask;

public interface VoxelsArithmetic {

    /**
     * Adds a constant-value to each voxel but <i>only</i> for voxels inside an object-mask
     * 
     * @param object object-mask to restrict operation to certain voxels
     * @param valueToBeAdded constant-value to be added
     */
    void addTo(ObjectMask object, int valueToBeAdded);
    
    /**
     * Multiplies the value of all voxels by a factor
     * 
     * @param factor what to multiply-by
     */
    void multiplyBy(double factor);
    
    /**
     * Multiplies each voxel by constant factor but <i>only</i> for voxels inside an object-mask
     * 
     * @param object object-mask to restrict operation to certain voxels
     * @param factor constant-value to multiply by
     */
    void multiplyBy(ObjectMask object, double factor);
    
    /**
     * Subtracts all current voxel-values from a constant-value
     * 
     * <p>i.e. each voxel value {@code v} is updated to become {@code valueToSubtractFrom -v }
     * 
     * @param valueToSubtractFrom the value to subtract from
     */
    void subtractFrom(int valueToSubtractFrom);
}
