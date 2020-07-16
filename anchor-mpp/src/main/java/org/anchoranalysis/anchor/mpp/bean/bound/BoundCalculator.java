/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.bound;

import org.anchoranalysis.anchor.mpp.bean.MPPBean;
import org.anchoranalysis.anchor.mpp.bound.BidirectionalBound;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.math.rotation.RotationMatrix;

public abstract class BoundCalculator extends MPPBean<BoundCalculator> {

    // angle is in radians
    public abstract BidirectionalBound calcBound(Point3d point, RotationMatrix rotMatrix)
            throws OperationFailedException;
}
