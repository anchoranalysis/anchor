/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@EqualsAndHashCode(callSuper = true)
public class FeatureInputAllMemo extends FeatureInputNRG {

    private MemoCollection pxlMarkMemoList;

    public FeatureInputAllMemo(MemoCollection pxlMarkMemoList, NRGStackWithParams raster) {
        super(Optional.of(raster));
        this.pxlMarkMemoList = pxlMarkMemoList;
    }

    public MemoCollection getPxlPartMemo() {
        return pxlMarkMemoList;
    }

    public void setPxlPartMemo(MemoCollection pxlPartMemoList) {
        this.pxlMarkMemoList = pxlPartMemoList;
    }
}
