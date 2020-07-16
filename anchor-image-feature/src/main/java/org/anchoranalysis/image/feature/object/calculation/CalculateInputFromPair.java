/* (C)2020 */
package org.anchoranalysis.image.feature.object.calculation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Calculates a single-input from a pair
 *
 * <p><div> Three states are possible:
 *
 * <ul>
 *   <li>First
 *   <li>Second
 *   <li>Merged </div>
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CalculateInputFromPair
        extends FeatureCalculation<FeatureInputSingleObject, FeatureInputPairObjects> {

    public enum Extract {
        FIRST,
        SECOND,
        MERGED
    }

    private final Extract extract;

    @Override
    protected FeatureInputSingleObject execute(FeatureInputPairObjects input) {
        FeatureInputSingleObject paramsNew = new FeatureInputSingleObject(extractObj(input));
        paramsNew.setNrgStack(input.getNrgStackOptional());
        return paramsNew;
    }

    private ObjectMask extractObj(FeatureInputPairObjects input) {

        if (extract == Extract.MERGED) {
            return input.getMerged();
        }

        return extract == Extract.FIRST ? input.getFirst() : input.getSecond();
    }
}
