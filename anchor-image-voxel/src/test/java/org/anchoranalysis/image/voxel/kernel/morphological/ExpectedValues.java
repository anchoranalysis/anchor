package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

/** 
 * Values expected for test result given a particular states of {@link KernelApplicationParameters}.
 *
 * @author Owen Feehan
 */
@Value @AllArgsConstructor @Accessors(fluent=true) class ExpectedValues {

    /** With {@link OutsideKernelPolicy#AS_ON} and {@code useZ==true}. */ 
    private final int on3D;

    /** With {@link OutsideKernelPolicy#AS_ON} and {@code useZ==false}. */
    private final int on2D;

    /** Otherwise when {@code useZ==true}. */
    private final int otherwise3D;
    
    /** Otherwise when {@code useZ==false}. */
    private final int otherwise2D;
    
    public ExpectedValues(int on, int otherwise) {
        this(on, on, otherwise);
    }
    
    public ExpectedValues(int on3D, int on2D, int otherwise) {
        this.on3D = on3D;
        this.on2D = on2D;
        this.otherwise2D = otherwise;
        this.otherwise3D = otherwise;
    }
}