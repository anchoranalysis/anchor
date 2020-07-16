/* (C)2020 */
package org.anchoranalysis.image.interpolator;

import net.imglib2.interpolation.randomaccess.LanczosInterpolatorFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class InterpolatorImgLib2Lanczos extends InterpolatorImgLib2 {

    public InterpolatorImgLib2Lanczos() {
        super(
                new LanczosInterpolatorFactory<UnsignedByteType>(),
                new LanczosInterpolatorFactory<UnsignedShortType>());
    }
}
