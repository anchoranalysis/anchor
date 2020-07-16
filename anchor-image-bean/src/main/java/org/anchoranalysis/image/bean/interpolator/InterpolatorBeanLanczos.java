/* (C)2020 */
package org.anchoranalysis.image.bean.interpolator;

import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorImgLib2Lanczos;

public class InterpolatorBeanLanczos extends InterpolatorBean {

    @Override
    public Interpolator create() {
        return new InterpolatorImgLib2Lanczos();
    }
}
