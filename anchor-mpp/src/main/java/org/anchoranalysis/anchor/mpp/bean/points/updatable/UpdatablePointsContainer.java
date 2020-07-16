/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.points.updatable;

import org.anchoranalysis.anchor.mpp.mark.set.UpdatableMarkSet;
import org.anchoranalysis.anchor.mpp.probmap.PointSampler;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.image.binary.mask.Mask;

public abstract class UpdatablePointsContainer extends AnchorBean<UpdatablePointsContainer>
        implements UpdatableMarkSet, PointSampler {

    public abstract void init(Mask binaryImage) throws InitException;

    public abstract int size();
}
