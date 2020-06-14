package org.anchoranalysis.image.bean.interpolator;

import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorNone;

public class InterpolatorBeanNone extends InterpolatorBean {

	@Override
	public Interpolator create() {
		return new InterpolatorNone();
	}

}
