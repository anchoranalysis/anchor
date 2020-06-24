package org.anchoranalysis.image.bean.interpolator;

import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorImgLib2Linear;

public class InterpolatorBeanLinear extends InterpolatorBean {

	@Override
	public Interpolator create() {
		return new InterpolatorImgLib2Linear();
	}

}
