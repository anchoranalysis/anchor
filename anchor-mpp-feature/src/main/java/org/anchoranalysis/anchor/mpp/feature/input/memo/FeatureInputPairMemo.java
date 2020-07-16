/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@EqualsAndHashCode(callSuper = true)
public class FeatureInputPairMemo extends FeatureInputNRG {

    private VoxelizedMarkMemo obj1;
    private VoxelizedMarkMemo obj2;

    public FeatureInputPairMemo(
            VoxelizedMarkMemo obj1, VoxelizedMarkMemo obj2, NRGStackWithParams nrgStack) {
        super(Optional.of(nrgStack));
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public VoxelizedMarkMemo getObj1() {
        return obj1;
    }

    public void setObj1(VoxelizedMarkMemo obj1) {
        this.obj1 = obj1;
    }

    public VoxelizedMarkMemo getObj2() {
        return obj2;
    }

    public void setObj2(VoxelizedMarkMemo obj2) {
        this.obj2 = obj2;
    }
}
