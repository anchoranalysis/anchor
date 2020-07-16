/* (C)2020 */
package org.anchoranalysis.image.bean.interpolator;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.interpolator.Interpolator;

public abstract class InterpolatorBean extends AnchorBean<InterpolatorBean> {

    public abstract Interpolator create();
}
