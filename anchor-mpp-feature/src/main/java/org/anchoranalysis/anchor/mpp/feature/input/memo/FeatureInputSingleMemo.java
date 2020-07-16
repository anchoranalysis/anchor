/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@EqualsAndHashCode(callSuper = true)
public class FeatureInputSingleMemo extends FeatureInputNRG {

    private VoxelizedMarkMemo pxlPartMemo;

    public FeatureInputSingleMemo(VoxelizedMarkMemo pxlPartMemo, NRGStackWithParams nrgStack) {
        this(pxlPartMemo, Optional.of(nrgStack));
    }

    public FeatureInputSingleMemo(
            VoxelizedMarkMemo pxlPartMemo, Optional<NRGStackWithParams> nrgStack) {
        super(nrgStack);
        this.pxlPartMemo = pxlPartMemo;
    }

    public VoxelizedMarkMemo getPxlPartMemo() {
        return pxlPartMemo;
    }

    public void setPxlPartMemo(VoxelizedMarkMemo pxlPartMemo) {
        this.pxlPartMemo = pxlPartMemo;
    }
}
