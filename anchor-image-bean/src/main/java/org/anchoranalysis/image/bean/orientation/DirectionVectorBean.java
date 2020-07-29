package org.anchoranalysis.image.bean.orientation;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.orientation.DirectionVector;

/**
 * A bean that creates a {@link DirectionVector}
 * 
 * @author Owen Feehan
 *
 */
public abstract class DirectionVectorBean extends AnchorBean<DirectionVectorBean> {

    /**
     * Creates a vector in a particular direction
     * 
     * @return the created vector
     */
    public abstract DirectionVector createVector() throws CreateException;
}
