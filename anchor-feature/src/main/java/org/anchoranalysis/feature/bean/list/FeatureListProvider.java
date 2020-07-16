/* (C)2020 */
package org.anchoranalysis.feature.bean.list;

import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.provider.FeatureProviderBean;
import org.anchoranalysis.feature.input.FeatureInput;

/**
 * @author Owen Feehan
 * @param <T> input type for feature list
 */
@GroupingRoot
public abstract class FeatureListProvider<T extends FeatureInput>
        extends FeatureProviderBean<FeatureListProvider<T>, FeatureList<T>> {

    public abstract FeatureList<T> create() throws CreateException;
}
