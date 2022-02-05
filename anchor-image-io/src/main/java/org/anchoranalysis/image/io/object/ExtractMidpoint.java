package org.anchoranalysis.image.io.object;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.voxel.object.ObjectMask; // NOSONAR
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;

/**
 * Extracts a {@link Point3i} representing the mid-point of an {@link ObjectWithProperties}.
 *
 * <p>The midpoint is taken, in order of preference:
 *
 * <ul>
 *   <li>From a property with key {@value ExtractMidpoint#PROPERTY_MIDPOINT}, if it exists.
 *   <li>From the center-of-gravity of the {@link ObjectMask}.
 * </ul>
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtractMidpoint {

    private static final String PROPERTY_MIDPOINT = "midpointInt";

    /**
     * Calculates the midpoint of {@code object}.
     *
     * @param object the object to calculate a midpoint for, possibly containing a property with key
     *     {@value ExtractMidpoint#PROPERTY_MIDPOINT}.
     * @param suppressZ when true, the z-dimension is ignored, and the midpoint will always have
     *     {@code z==0}. when false, it is included in the calculation.
     * @return the midpoint.
     */
    public static Point3i midpoint(ObjectWithProperties object, boolean suppressZ) {
        return maybeSuppressZ(calculateMidpoint3D(object), suppressZ);
    }

    /** Sets the z-dimension of {@code point} to 0, when {@code suppressZ==true}. */
    private static Point3i maybeSuppressZ(Point3i point, boolean suppressZ) {
        if (suppressZ) {
            point.setZ(0);
        }
        return point;
    }

    /** Calculates the midpoint in 3D dimensions. */
    private static Point3i calculateMidpoint3D(ObjectWithProperties object) {
        if (object.hasProperty(PROPERTY_MIDPOINT)) {
            return Point3i.immutableAdd(
                    object.getProperty(PROPERTY_MIDPOINT), object.boundingBox().cornerMin());
        } else {
            return PointConverter.intFromDoubleFloor(object.asObjectMask().centerOfGravity());
        }
    }
}
