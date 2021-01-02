package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.kernel.BinaryKernelTestBase;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.OutsideKernelPolicy;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract class MorphologicalKernelTestBase extends BinaryKernelTestBase {
    
    /** The expected value when there is a maximal dilation in 2D plane (in all directions in 2D). */
    protected static final int EXPECTED_MAXIMAL_2D = 90;
    
    /** Expected values where the <b>2D object</b> is located <b>inside the scene without touching a boundary</b>. */
    protected abstract ExpectedValues inside2D();

    /** Expected values where the <b>3D object</b> is located <b>inside the scene without touching a boundary</b>. */
    protected abstract ExpectedValues inside3D();
    
    /** Applies a test where the <b>2D object</b> is located <b>touching the scene border</b>. */
    protected abstract ExpectedValues boundary2D();

    /** Applies a test where the <b>3D object</b> is located <b>touching the scene border</b>. */
    protected abstract ExpectedValues boundary3D();
        
    @Override
    protected int expectedInside2D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        return whenOutsideOn(params, inside2D());
    }

    @Override
    protected int expectedInside3D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        return whenOutsideOn(params, inside3D());
    }

    @Override
    protected int expectedBoundary2D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        return whenOutsideOn(params, boundary2D());
    }

    @Override
    protected int expectedBoundary3D(ObjectMaskFixture fixture,
            KernelApplicationParameters params) {
        return whenOutsideOn(params, boundary3D());
    }
        
   /** 
    * Specifying values for combinations of useZ (termed 3D or 2D) for when policy is {@link OutsideKernelPolicy#AS_ON}).
    */
   private static int whenOutsideOn(KernelApplicationParameters params, ExpectedValues values) {
       if (params.isUseZ()) {
           return whenOutsideOn(params, values.on3D(), values.otherwise3D());
       } else {
           return whenOutsideOn(params, values.on2D(), values.otherwise2D());
       }        
   }
   
   private static int whenOutsideOn(KernelApplicationParameters params, int valueIfOn, int valueOtherwise) {
       if (params.getOutsideKernelPolicy()==OutsideKernelPolicy.AS_ON) {
           return valueIfOn;
       } else {
           return valueOtherwise;
       }
   }
}
