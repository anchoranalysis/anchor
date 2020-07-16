/* (C)2020 */
package org.anchoranalysis.feature.resultsvectorcollection;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.name.FeatureNameMapToIndex;

@Value
@EqualsAndHashCode(callSuper = false)
public class FeatureInputResults implements FeatureInput {
    ResultsVectorCollection resultsVectorCollection;
    FeatureNameMapToIndex featureNameIndex;
}
