/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.distance;

import java.io.Serializable;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.DirectionVector;

public abstract class UnitValueDistance extends AnchorBean<UnitValueDistance>
        implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    // Uses the direction between two points to resolve the distance.
    // NB the magnitude of the distance between these two points is not considered, only the
    // direction
    public double resolve(Optional<ImageResolution> res, Point3d point1, Point3d point2)
            throws OperationFailedException {
        return resolve(res, DirectionVector.createBetweenTwoPoints(point1, point2));
    }

    // Uses the direction between two points to resolve the distance.
    // NB the magnitude of the distance between these two points is not considered, only the
    // direction
    public double resolve(Optional<ImageResolution> res, Point3i point1, Point3i point2)
            throws OperationFailedException {
        return resolve(res, DirectionVector.createBetweenTwoPoints(point1, point2));
    }

    // Returns value in voxels
    public abstract double resolve(Optional<ImageResolution> res, DirectionVector dirVector)
            throws OperationFailedException;

    /**
     * Resolves the distance in a direction aligned to a particular axis
     *
     * @param res image-resolution
     * @param axis axis to indicate direction
     * @return the distance in the direction of the axis
     * @throws OperationFailedException
     */
    public double resolveForAxis(Optional<ImageResolution> res, AxisType axis)
            throws OperationFailedException {
        return resolve(res, new DirectionVector(axis));
    }
}
