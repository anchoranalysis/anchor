/* (C)2020 */
package org.anchoranalysis.image.object.factory;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.points.BoundingBoxFromPoints;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateFromPointsFactory {

    public static ObjectMask create(List<Point3i> points) throws CreateException {

        BoundingBox bbox;
        try {
            bbox = BoundingBoxFromPoints.forList(points);
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }

        ObjectMask mask = new ObjectMask(bbox);
        points.forEach(mask::setOn);
        return mask;
    }
}
