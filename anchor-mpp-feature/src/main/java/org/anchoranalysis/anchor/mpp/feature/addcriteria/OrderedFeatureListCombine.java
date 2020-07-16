/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;

class OrderedFeatureListCombine {

    private OrderedFeatureListCombine() {}

    public static <T extends FeatureInput> Optional<FeatureList<T>> combine(
            List<? extends OrderedFeatureList<T>> list) throws CreateException {

        FeatureList<T> out =
                FeatureListFactory.flatMapFromOptional(
                        list, OrderedFeatureList::orderedListOfFeatures);

        if (!out.isEmpty()) {
            return Optional.of(out);
        } else {
            return Optional.empty();
        }
    }
}
