/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculateDeriveSingleMemoFromPair
        extends FeatureCalculation<FeatureInputSingleMemo, FeatureInputPairMemo> {

    /** Iff true, first object is used, otherwise the second */
    private final boolean first;

    @Override
    protected FeatureInputSingleMemo execute(FeatureInputPairMemo input) {
        return new FeatureInputSingleMemo(
                first ? input.getObj1() : input.getObj2(), input.getNrgStackOptional());
    }
}
