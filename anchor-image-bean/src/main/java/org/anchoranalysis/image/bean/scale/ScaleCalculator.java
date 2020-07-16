/* (C)2020 */
package org.anchoranalysis.image.bean.scale;

import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.scale.ScaleFactor;

public abstract class ScaleCalculator extends NullParamsBean<ScaleCalculator> {

    public abstract ScaleFactor calc(ImageDimensions srcDim) throws OperationFailedException;
}
