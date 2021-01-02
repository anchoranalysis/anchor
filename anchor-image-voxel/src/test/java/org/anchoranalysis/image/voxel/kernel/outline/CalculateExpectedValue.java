package org.anchoranalysis.image.voxel.kernel.outline;

import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;

/**
 * Calculates the expected-value for a particular test.
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface CalculateExpectedValue {
    
    /**
     * Calculates the value expected for a a particular test.
     * 
     * @param fixture the fixture used to create the {@link ObjectMask} used in the test.
     * @param params the particular {@link KernelApplicationParameters} used in the test.
     * @return the value expected to be returned by the test
     */
    int calculate(ObjectMaskFixture fixture, KernelApplicationParameters params);
}