/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.points.fitter;

import java.util.List;
import org.anchoranalysis.anchor.mpp.bean.points.PointsBean;
import org.anchoranalysis.anchor.mpp.mark.CompatibleWithMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.image.extent.ImageDimensions;

// Fits an ellipsoid to a set of points
@GroupingRoot
public abstract class PointsFitter extends PointsBean<PointsFitter> implements CompatibleWithMark {

    public abstract void fit(List<Point3f> points, Mark mark, ImageDimensions dimensions)
            throws PointsFitterException, InsufficientPointsException;
}
