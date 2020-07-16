/* (C)2020 */
package org.anchoranalysis.image.interpolator;

import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class InterpolatorImgLib2Linear extends InterpolatorImgLib2 {

    public InterpolatorImgLib2Linear() {
        super(
                new NLinearInterpolatorFactory<UnsignedByteType>(),
                new NLinearInterpolatorFactory<UnsignedShortType>());
    }
}
