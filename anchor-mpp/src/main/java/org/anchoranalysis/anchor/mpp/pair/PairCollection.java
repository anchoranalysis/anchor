/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pair;

import org.anchoranalysis.anchor.mpp.mark.set.UpdatableMarkSet;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.random.RandomNumberGenerator;

/**
 * A collection of pairs, from which we can randomly sample
 *
 * @author Owen Feehan
 * @param <T> pair-type
 */
@GroupingRoot
public abstract class PairCollection<T> extends AnchorBean<PairCollection<T>>
        implements UpdatableMarkSet {

    public abstract T sampleRandomPairNonUniform(RandomNumberGenerator randomNumberGenerator);
}
