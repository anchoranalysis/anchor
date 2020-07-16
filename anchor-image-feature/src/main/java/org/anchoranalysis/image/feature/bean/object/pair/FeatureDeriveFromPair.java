/* (C)2020 */
package org.anchoranalysis.image.feature.bean.object.pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.cache.ChildCacheName;
import org.anchoranalysis.feature.cache.SessionInput;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.feature.object.calculation.CalculateInputFromPair;
import org.anchoranalysis.image.feature.object.calculation.CalculateInputFromPair.Extract;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

/**
 * Base class for evaluating {@link FeaturePairObjects} in terms of another feature that operations
 * on elements (first, second, merged etc.)
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class FeatureDeriveFromPair extends FeaturePairObjects {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Feature<FeatureInputSingleObject> item;
    // END BEAN PROPERTIES

    public static final ChildCacheName CACHE_NAME_FIRST =
            new ChildCacheName(FeatureDeriveFromPair.class, "first");
    public static final ChildCacheName CACHE_NAME_SECOND =
            new ChildCacheName(FeatureDeriveFromPair.class, "second");
    public static final ChildCacheName CACHE_NAME_MERGED =
            new ChildCacheName(FeatureDeriveFromPair.class, "merged");

    protected double valueFromFirst(SessionInput<FeatureInputPairObjects> input)
            throws FeatureCalcException {
        return featureValFrom(input, Extract.FIRST, CACHE_NAME_FIRST);
    }

    protected double valueFromSecond(SessionInput<FeatureInputPairObjects> input)
            throws FeatureCalcException {
        return featureValFrom(input, Extract.SECOND, CACHE_NAME_SECOND);
    }

    protected double valueFromMerged(SessionInput<FeatureInputPairObjects> input)
            throws FeatureCalcException {
        return featureValFrom(input, Extract.MERGED, CACHE_NAME_MERGED);
    }

    private double featureValFrom(
            SessionInput<FeatureInputPairObjects> input, Extract extract, ChildCacheName cacheName)
            throws FeatureCalcException {

        return input.forChild().calc(item, new CalculateInputFromPair(extract), cacheName);
    }
}
