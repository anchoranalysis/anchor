/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;

public interface OrderedFeatureList<T extends FeatureInput> {

    /**
     * A list of features used to determine the add-criteria, or NULL if irrelevant. The order
     * features appear in this list, will be used to determine the FeatureSessionCreateParams passed
     * to generateEdge()
     *
     * @return
     */
    Optional<FeatureList<T>> orderedListOfFeatures() throws CreateException;
}
