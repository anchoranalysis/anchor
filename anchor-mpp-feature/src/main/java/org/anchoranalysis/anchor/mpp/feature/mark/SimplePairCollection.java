/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.mark;

import org.anchoranalysis.anchor.mpp.feature.addcriteria.PairCollectionAddCriteria;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;

/**
 * A pair collection where the underlying type is a simple {@link Pair}
 *
 * @author Owen Feehan
 */
public class SimplePairCollection extends PairCollectionAddCriteria<Pair<Mark>> {

    public SimplePairCollection() {
        super(Pair.class);
    }

    @Override
    public String getBeanDscr() {
        return getBeanName();
    }
}
