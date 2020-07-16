/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.operator.FeatureSingleElem;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * Extracts one of the memos from the pair, and processes as a {@link FeatureSingleMemo}
 *
 * @author Owen Feehan
 */
public class AsSingle extends FeatureSingleElem<FeatureInputPairMemo, FeatureInputSingleMemo> {

    // START BEAN PROPERTIES
    /** Iff true, first object is used, otherwise the second */
    @BeanField @Getter @Setter private boolean first = true;
    // END BEAN PROPERTIES

    private static final ChildCacheName CACHE_NAME_FIRST =
            new ChildCacheName(AsSingle.class, "first");
    private static final ChildCacheName CACHE_NAME_SECOND =
            new ChildCacheName(AsSingle.class, "second");

    @Override
    public double calc(SessionInput<FeatureInputPairMemo> input) throws FeatureCalcException {
        return input.forChild()
                .calc(
                        getItem(),
                        new CalculateDeriveSingleMemoFromPair(first),
                        first ? CACHE_NAME_FIRST : CACHE_NAME_SECOND);
    }

    // We change the default behaviour, as we don't want to give the same paramsFactory
    //   as the item we pass to
    @Override
    public Class<? extends FeatureInput> inputType() {
        return FeatureInputPairMemo.class;
    }

    @Override
    public String getParamDscr() {
        return getItem().getParamDscr();
    }
}
